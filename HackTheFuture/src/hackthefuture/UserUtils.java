/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

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

        double x, y;
        do {
            double minX = -500.0;
            double maxX = 500.0;
            double minY = -500.0;
            double maxY = 500.0;
            Random random = new Random();
            x = minX + (maxX - minX) * random.nextDouble();
            y = minY + (maxY - minY) * random.nextDouble();
        } while (coordinatesExist(x, y));

        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);

        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO User (email, username, password, salt,role_id, location_coordinate_x, location_coordinate_y) VALUES (?,?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setString(2, username);
                ps.setString(3, hashedPassword);
                ps.setString(4, salt); // Store the salt in the database
                ps.setInt(5, roleId);
                ps.setDouble(6, x);
                ps.setDouble(7, y);
                int rowsInserted = ps.executeUpdate();
                return rowsInserted > 0;
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
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM User WHERE location_coordinate_x = ? AND location_coordinate_y = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, x);
                ps.setDouble(2, y);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        return count > 0;
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
            System.out.println("first");
            AlertUtils.showRegistrationWarningAlert();
            return false;
        } else if (!isValidEmail(email)) {
            System.out.println("second");
            AlertUtils.showInvalidEmailAlert();
            return false;
        } else if (!pw.equals(conPw)) {
            System.out.println("third");
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
                                || // Parent should have a child relation
                                (selectedRole.equals("Student") && relationRoleId != 2)) { // Child should have a parent relation
                            AlertUtils.showRoleSame();
                            return false;
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

        // Step 2: Register the new user with the retrieved location coordinates
        int roleId = getRoleIdByRoleName(selectedRole);
        if (roleId == -1) {
            return false;
        }
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
                        // Retrieve the child's user_id based on their username
                        int childId = getUserIdByUsername(conn, relationUsername);
                        relationshipPs.setInt(2, childId); // child_id
                    } else {
                        // Existing user (relationUsername) is the parent
                        // Retrieve the parent's user_id based on their username
                        int parentId = getUserIdByUsername(conn, relationUsername);
                        relationshipPs.setInt(1, parentId); // parent_id
                        relationshipPs.setInt(2, newUserId); // child_id
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


    /* public static boolean registerUserWithFam(String email, String username, String pw, String selectedRole, String email1, String username1, String pw1, String selectedRole1) {
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
        } while (coordinatesExist(locationCoordinateX, locationCoordinateY));
        try (Connection conn = DatabaseConnector.getConnection()) {
            String sql = "INSERT INTO User (email, username, password, role_id, location_coordinate_x, location_coordinate_y) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // Insert first user
                ps.setString(1, email);
                ps.setString(2, username);
                ps.setString(3, pw);
                ps.setInt(4, roleId);
                ps.setDouble(5, locationCoordinateX);
                ps.setDouble(6, locationCoordinateY);
                int rowsInserted = ps.executeUpdate();
                if (rowsInserted <= 0) {
                    return false; // Failed to insert first user
                }

                // Insert second user
                ps.setString(1, email1);
                ps.setString(2, username1);
                ps.setString(3, pw1);
                ps.setInt(4, roleId1);
                ps.setDouble(5, locationCoordinateX);
                ps.setDouble(6, locationCoordinateY);
                rowsInserted = ps.executeUpdate();
                return rowsInserted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    } */
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
        } while (coordinatesExist(locationCoordinateX, locationCoordinateY));
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

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPassword = salt + password;
            byte[] encodedhash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
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
}
