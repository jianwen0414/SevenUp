/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tan Shi Han
 */
public class UserUtils {
    public static void writeUserDataToFile() { // Store user information into Text File
        String fileName = "User.txt";

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT user_id, email, username, password, salt, role_id, location_coordinate_x, location_coordinate_y, current_points FROM User";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery();
                 PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

                // Write column names
                writer.println("user_id, email, username, password, salt, role_id, location_coordinate_x, location_coordinate_y, current_points");

                // Write data rows
                while (rs.next()) {
                    writer.println(rs.getInt("user_id") + ", " +
                                   rs.getString("email") + ", " +
                                   rs.getString("username") + ", " +
                                   rs.getString("password") + ", " +
                                   rs.getString("salt") + ", " +
                                   rs.getInt("role_id") + ", " +
                                   rs.getDouble("location_coordinate_x") + ", " +
                                   rs.getDouble("location_coordinate_y") + ", " +
                                   rs.getInt("current_points"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isEmailUnique(String email) {
        try (Connection conn = (Connection) DatabaseConnector.getConnection()) {
            try (PreparedStatement emailCheck = conn.prepareStatement("SELECT email FROM user WHERE email=?")) {
                emailCheck.setObject(1, email);
                try (ResultSet emailResult = emailCheck.executeQuery()) {
                    return !emailResult.next(); // Return true if email doesn't exist
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isUsernameUnique(String username) {
        try (Connection conn = (Connection) DatabaseConnector.getConnection()) {
            try (PreparedStatement usernameCheck = conn.prepareStatement("SELECT username FROM user WHERE username=?")) {
                usernameCheck.setObject(1, username);
                try (ResultSet usernameResult = usernameCheck.executeQuery()) {
                    return !usernameResult.next(); // Return true if username doesn't exist
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean registerUser(String email, String username, String password, String role) {

        int roleId = getRoleIdByRoleName(role);
        if (roleId == -1) {
            return false;
        }
        //Generate coordinate with two decimal places only
        double x, y;
        do {
            double minX = -500.0;
            double maxX = 500.0;
            double minY = -500.0;
            double maxY = 500.0;
            Random random = new Random();
            x = minX + (maxX - minX) * random.nextDouble();
            y = minY + (maxY - minY) * random.nextDouble();
            x = Math.round(x * 100.0) / 100.0;
            y = Math.round(y * 100.0) / 100.0;
        } while (coordinatesExist(x, y));
        //Generate salt and hash the password
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        //Start the insertion
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO User (email, username, password, salt,role_id, location_coordinate_x, location_coordinate_y) VALUES (?,?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, username);
                ps.setString(3, hashedPassword);
                ps.setString(4, salt); 
                ps.setInt(5, roleId);
                ps.setDouble(6, x);
                ps.setDouble(7, y);
                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    writeUserDataToFile(); // Call the method to write data to the file
                    return true;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean doesUsernameExist(String username) {
        try (Connection conn = (Connection) DatabaseConnector.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM user WHERE username=?")) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next(); // Returns true if username exists
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getRoleIdByRoleName(String roleName) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT role_id FROM Role WHERE role_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, roleName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("role_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if role not found
    }

    private static boolean coordinatesExist(double x, double y) {
        //Check whether the coordinate exists or not
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM User WHERE location_coordinate_x = ? AND location_coordinate_y = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, x);
                ps.setDouble(2, y);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        return count > 0;  //Return true if coordinate exists
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean validateRegistrationInputs(String email, String username, String pw, String conPw, String selectedRole) {
        if (email.isEmpty() || username.isEmpty() || pw.isEmpty() || conPw.isEmpty() || selectedRole == null) {
            AlertUtils.showRegistrationWarningAlert();
            return false;
        } else if (!isValidEmail(email)) {
            AlertUtils.showInvalidEmailAlert();
            return false;
        } else if (!pw.equals(conPw)) {
            AlertUtils.showPasswordMismatchAlert();
            return false;
        } else if (!(isUsernameUnique(username))) {
            AlertUtils.showUsernameNotUnique();
            return false;
        } else if (!(isEmailUnique(email))) {
            AlertUtils.showEmailNotUnique();
            return false;
        }
        return true;
    }

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    public static boolean registerUserMemberExists(String email, String username, String pw, String selectedRole, String relationUsername) {
    double locationCoordinateX = 0.0;
    double locationCoordinateY = 0.0;

    try (Connection conn = DatabaseConnector.getConnection()) {
        String sql = "SELECT location_coordinate_x, location_coordinate_y, role_id FROM User WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, relationUsername);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    locationCoordinateX = rs.getDouble("location_coordinate_x");
                    locationCoordinateY = rs.getDouble("location_coordinate_y");
                    int relationRoleId = rs.getInt("role_id");
                    // Ensure that the existing user is a suitable relation based on their role
                    if ((selectedRole.equals("Parent") && relationRoleId != 3)
                            || (selectedRole.equals("Student") && relationRoleId != 2)) {
                        AlertUtils.showRoleSame();
                        return false;
                    }
                    if (selectedRole.equals("Parent")) {
                        int childId = getUserIdByUsername(conn, relationUsername);
                        int parentCount = getParentCountForChild(conn, childId);
                        if (parentCount >= 2) {
                            AlertUtils.showMaxParentsReached();
                            return false;
                        }
                    }
                } else {
                    return false; // No user found with the given username
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }

    // Register the new user with the retrieved location coordinates
    int roleId = getRoleIdByRoleName(selectedRole);
    if (roleId == -1) {
        return false;
    }
    //Generate salt and hash the password using the salt
    String salt = generateSalt();
    String hashedPassword = hashPassword(pw, salt);
    try (Connection conn = DatabaseConnector.getConnection()) {
        String sql = "INSERT INTO User (email, username, password, salt, role_id, location_coordinate_x, location_coordinate_y) VALUES (?,?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, email);
            ps.setString(2, username);
            ps.setString(3, hashedPassword);
            ps.setString(4, salt);
            ps.setInt(5, roleId);
            ps.setDouble(6, locationCoordinateX);
            ps.setDouble(7, locationCoordinateY);
            int rowsInserted = ps.executeUpdate();
            if (rowsInserted > 0) {
                writeUserDataToFile(); // Call the method to write data to the file
            }
            if (rowsInserted <= 0) {
                return false; // Failed to insert new user
            }

            ResultSet generatedKeys = ps.getGeneratedKeys();
            int newUserId = -1;
            if (generatedKeys.next()) {
                newUserId = generatedKeys.getInt(1);
            } else {
                return false; // Failed to get new user_id
            }

            // Insert into ParentChildRelationship table
            String relationshipSql = "INSERT INTO ParentChildRelationship (parent_id, child_id) VALUES (?, ?)";
            try (PreparedStatement relationshipPs = conn.prepareStatement(relationshipSql)) {
                if (selectedRole.equals("Parent")) {
                    // Existing user (relationUsername) is the child
                    relationshipPs.setInt(1, newUserId); // parent_id
                    int childId = getUserIdByUsername(conn, relationUsername);
                    relationshipPs.setInt(2, childId); // child_id
                } else {
                    // Existing user (relationUsername) is the parent
                    int parentId = getUserIdByUsername(conn, relationUsername);
                    relationshipPs.setInt(1, parentId); // parent_id
                    relationshipPs.setInt(2, newUserId); // child_id
                }
                rowsInserted = relationshipPs.executeUpdate();
                if (rowsInserted <= 0) {
                    return false;
                }

                // This is to prevent some logic bug
                // If a new parent and new child register (Father1, Child1)
                // Then a new child register using existing parent (Child2, Father1)
                //Now if a mother register, she only can input a child name only(Mother1,Child2)
                // This will have some logic bug as she cant get all the child of her family (Child1)
                //Below logics will solve this bug perfectly
                String findRelatedUsersSql;
                if (selectedRole.equals("Parent")) {
                    //Find from user table, role_id=3(Student) and location x, location y is same
                    findRelatedUsersSql = "SELECT user_id FROM User WHERE role_id = 3 AND location_coordinate_x = ? AND location_coordinate_y = ? AND username != ?";
                } else {
                    //Find from user table, role_id=2(Parent) and location x, location y is same
                    findRelatedUsersSql = "SELECT user_id FROM User WHERE role_id = 2 AND location_coordinate_x = ? AND location_coordinate_y = ? AND username != ?";
                }

                try (PreparedStatement findPs = conn.prepareStatement(findRelatedUsersSql)) {
                    findPs.setDouble(1, locationCoordinateX);
                    findPs.setDouble(2, locationCoordinateY);
                    findPs.setString(3, relationUsername);

                    try (ResultSet relatedUsersRs = findPs.executeQuery()) {
                        while (relatedUsersRs.next()) {
                            int relatedUserId = relatedUsersRs.getInt("user_id");

                            try (PreparedStatement insertRelatedPs = conn.prepareStatement(relationshipSql)) {
                                if (selectedRole.equals("Parent")) {
                                    insertRelatedPs.setInt(1, newUserId);
                                    insertRelatedPs.setInt(2, relatedUserId);
                                } else {
                                    insertRelatedPs.setInt(1, relatedUserId);
                                    insertRelatedPs.setInt(2, newUserId);
                                }
                                insertRelatedPs.executeUpdate();
                            }
                        }
                    }
                }
                return true;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    private static int getUserIdByUsername(Connection conn, String username) throws SQLException {
        String sql = "SELECT user_id FROM User WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                } else {
                    throw new SQLException("User with username '" + username + "' not found.");
                }
            }
        }
    }


   
    public static boolean registerUserWithFam(String email, String username, String pw, String selectedRole,
            String email1, String username1, String pw1, String selectedRole1) {
        int roleId = getRoleIdByRoleName(selectedRole);
        int roleId1 = getRoleIdByRoleName(selectedRole1);
        if (roleId == -1 || roleId1 == -1) {
            return false;
        }
        double locationCoordinateX;
        double locationCoordinateY;
        do {
            double minX = -500.0;
            double maxX = 500.0;
            double minY = -500.0;
            double maxY = 500.0;
            Random random = new Random();
            locationCoordinateX = minX + (maxX - minX) * random.nextDouble();
            locationCoordinateY = minY + (maxY - minY) * random.nextDouble();
            locationCoordinateX = Math.round(locationCoordinateX * 100.0) / 100.0;
            locationCoordinateY = Math.round(locationCoordinateY * 100.0) / 100.0;
        } while (coordinatesExist(locationCoordinateX, locationCoordinateY)); //Ensure the coordinate didnt repeat for different family
        //Generate salt and hash the password using salt
        String salt = generateSalt();
        String hashedPassword = hashPassword(pw, salt);
        String salt2 = generateSalt();
        String hashedPassword2 = hashPassword(pw1, salt2);
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO User (email, username, password, salt, role_id, location_coordinate_x, location_coordinate_y) VALUES (?,?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                // Insert first user
                ps.setString(1, email);
                ps.setString(2, username);
                ps.setString(3, hashedPassword);
                ps.setString(4, salt);
                ps.setInt(5, roleId);
                ps.setDouble(6, locationCoordinateX);
                ps.setDouble(7, locationCoordinateY);
                int rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    writeUserDataToFile(); // Call the method to write data to the file
                    
                }
                if (rowsInserted <= 0) {
                    return false; // Failed to insert first user
                }
                ResultSet generatedKeys = ps.getGeneratedKeys();
                int parentId = -1;
                if (generatedKeys.next()) {
                    parentId = generatedKeys.getInt(1);
                } else {
                    return false; // Failed to get parent_id
                }

                // Insert second user
                ps.clearParameters(); // Clear parameters before reusing
                ps.setString(1, email1);
                ps.setString(2, username1);
                ps.setString(3, hashedPassword2);
                ps.setString(4, salt2);
                ps.setInt(5, roleId1);
                ps.setDouble(6, locationCoordinateX);
                ps.setDouble(7, locationCoordinateY);
                rowsInserted = ps.executeUpdate();
                if (rowsInserted > 0) {
                    writeUserDataToFile(); // Call the method to write data to the file
                }
                if (rowsInserted <= 0) {
                    return false; // Failed to insert second user
                }
                generatedKeys = ps.getGeneratedKeys();
                int childId = -1;
                if (generatedKeys.next()) {
                    childId = generatedKeys.getInt(1);
                } else {
                    return false; // Failed to get child_id
                }

                // Insert into ParentChildRelationship table
                String relationshipSql = "INSERT INTO ParentChildRelationship (parent_id, child_id) VALUES (?, ?)";
                try (PreparedStatement relationshipPs = conn.prepareStatement(relationshipSql)) {
                    if (roleId == 2) { // If the first user is a parent
                        relationshipPs.setInt(1, parentId);
                        relationshipPs.setInt(2, childId);
                    } else { // If the second user is a parent
                        relationshipPs.setInt(1, childId);
                        relationshipPs.setInt(2, parentId);
                    }
                    rowsInserted = relationshipPs.executeUpdate();
                    return rowsInserted > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Generates a random salt using SecureRandom and returns it encoded in Base64
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    // Hashes a password with a given salt using SHA-256 and returns the hash in hexadecimal format
    public static String hashPassword(String password, String salt) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Concatenate the salt and password
            String saltedPassword = salt + password;
            // Hash the salted password
            byte[] encodedhash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            // Convert the hash bytes to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                // Convert each byte to a hexadecimal string
                String hex = Integer.toHexString(0xff & b);
                // If the hex string is only one character, pad it with a leading zero
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static int getParentCountForChild(Connection conn, int childId) throws SQLException {
        //Get parent count of a child, so that will be used to check whether a child already has 2 parents or not
    String sql = "SELECT COUNT(*) AS parent_count FROM ParentChildRelationship WHERE child_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, childId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("parent_count");
            } else {
                return 0;
            }
        }
    }
}
}

