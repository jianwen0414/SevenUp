package hackthefuture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class loginController {

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hackingthefuture";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "No16juru*";

    // JDBC variables for opening, closing, and managing connection
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    //private DatabaseConnector databaseConnector;
    // Initialize the database connection
    /*static {
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error
        }
    }*/
    @FXML
    private void initialize() {
// Set initial state
        usernameField.setText("");
        passwordField.setText("");

        // Register event handler for login button
        loginButton.setOnAction(event -> handleLoginButtonAction());
        registerButton.setOnAction(event -> handleRegisterButtonAction());
        // Set initial focus on username field
        usernameField.requestFocus();        // Optional: You can perform additional initialization here

        // Initialize database connection
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error
        }
    }

    @FXML
    private void handleLoginButtonAction() {
        String usernameOrEmail = usernameField.getText();
        String password = passwordField.getText();

        String sql = "SELECT * FROM User WHERE (username = ? OR email = ?)";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usernameOrEmail);
            preparedStatement.setString(2, usernameOrEmail);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve the stored password and salt
                String storedHashedPassword = resultSet.getString("password");
                String storedSalt = resultSet.getString("salt");

                // Hash the input password using the retrieved salt
                String hashedInputPassword = UserUtils.hashPassword(password, storedSalt);

                // Compare the hashed input password with the stored hashed password
                if (storedHashedPassword.equals(hashedInputPassword)) {
                    int userId = resultSet.getInt("user_id");
                    int roleId = resultSet.getInt("role_id");
                    String userEmail = resultSet.getString("email");
                    String userUsername = resultSet.getString("username");
                    double locationX = resultSet.getDouble("location_coordinate_x");
                    double locationY = resultSet.getDouble("location_coordinate_y");
                    int userPoints = resultSet.getInt("current_points");

                    UserRedirectionUtils.redirectUserToHomepage(userId, roleId, userEmail, userUsername, storedHashedPassword,
                            locationX, locationY, userPoints, connection,
                            (Stage) usernameField.getScene().getWindow());
                } else {
                    AlertUtils.showLoginError();
                }
            } else {
                AlertUtils.showAccountNotFound();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
 //before doing password hashing
//    @FXML
//private void handleLoginButtonAction() {
//    String username = usernameField.getText();
//    String password = passwordField.getText();
//
//    String sql = "SELECT * FROM User u WHERE u.username = ? AND u.password = ?";
//
//    try {
//        preparedStatement = connection.prepareStatement(sql);
//        preparedStatement.setString(1, username);
//        preparedStatement.setString(2, password);
//        resultSet = preparedStatement.executeQuery();
//
//        if (resultSet.next()) {
//            int userId = resultSet.getInt("user_id");
//            int roleId = resultSet.getInt("role_id");
//            String userEmail = resultSet.getString("email");
//            String userUsername = resultSet.getString("username");
//            String userPassword = resultSet.getString("password");
//            double locationX = resultSet.getDouble("location_coordinate_x");
//            double locationY = resultSet.getDouble("location_coordinate_y");
//            int userPoints = resultSet.getInt("current_points");
//
//            UserRedirectionUtils.redirectUserToHomepage(userId, roleId, userEmail, userUsername, userPassword, 
//                                                        locationX, locationY, userPoints, connection, 
//                                                        (Stage) usernameField.getScene().getWindow());
//        } else {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Login Failed");
//            alert.setHeaderText(null);
//            alert.setContentText("Invalid username or password. Please try again.");
//            alert.showAndWait();
//        }
//    } catch (SQLException | IOException e) {
//        e.printStackTrace();
//    } finally {
//        try {
//            if (resultSet != null) {
//                resultSet.close();
//            }
//            if (preparedStatement != null) {
//                preparedStatement.close();
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}

// ori method
//    @FXML
//    private void handleLoginButtonAction() {
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//
//        // SQL query to check if username and password match and retrieve user role ID
//        String sql = "SELECT * "
//                + "FROM User u "
//                + "WHERE u.username = ? AND u.password = ?";
//
//        try {
//            preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setString(1, username);
//            preparedStatement.setString(2, password);
//            resultSet = preparedStatement.executeQuery();
//
//            // If a row is returned, authentication is successful
//            if (resultSet.next()) {
//                int userId = resultSet.getInt("user_id");
//                int roleId = resultSet.getInt("role_id");
//                String userEmail = resultSet.getString("email");
//                String userUsername = resultSet.getString("username");
//                String userPassword = resultSet.getString("password");
//                Double locationX = resultSet.getDouble("location_coordinate_x");
//                Double locationY = resultSet.getDouble("location_coordinate_y");
//                int userPoints = resultSet.getInt("current_points");
//
//                System.out.println(roleId);
//                // Pass user information to the corresponding homepage controller based on role ID
//                switch (roleId) {
//                    case 1: // Educator
//                        Educator currentEducator = new Educator(userId, userEmail, userUsername, userPassword, roleId, locationX, locationY);
//                        preparedStatement = connection.prepareStatement("SELECT * FROM EventCreationRecord WHERE creator_id = ?");
//                        preparedStatement.setInt(1, currentEducator.getUserId());
//                        resultSet = preparedStatement.executeQuery();
//                        List<Integer> eventList = new ArrayList<>();
//                        while (resultSet.next()) {
//                            eventList.add(resultSet.getInt("event_id"));
//                        }
//                        for (int id : eventList) {
//                            preparedStatement = connection.prepareStatement("SELECT * FROM Event WHERE event_id = ?");
//                            preparedStatement.setInt(1, id);
//                            resultSet = preparedStatement.executeQuery();
//                            while (resultSet.next()) {
//                                int eventId = resultSet.getInt("event_id");
//                                String eventTitle= resultSet.getString("event_title");
//                                String eventDescription = resultSet.getString("event_description");
//                                String eventVenue = resultSet.getString("event_venue");
//                                LocalDate eventDate = resultSet.getDate("event_date").toLocalDate();
//                                LocalTime eventTime = resultSet.getTime("event_time").toLocalTime();
//                                Event event=new Event(eventId,eventTitle,eventDescription,eventVenue,eventDate,eventTime);
//                                currentEducator.createEvent(event);
//                            }
//                        }
//                        preparedStatement = connection.prepareStatement("SELECT * FROM QuizCreationRecord WHERE creator_id = ?");
//                        preparedStatement.setInt(1, currentEducator.getUserId());
//                        resultSet = preparedStatement.executeQuery();
//                        List<Integer> quizList = new ArrayList<>();
//                        while (resultSet.next()) {
//                            quizList.add(resultSet.getInt("quiz_id"));
//                        }
//                        for (int id : quizList) {
//                            preparedStatement = connection.prepareStatement("SELECT * FROM Quiz WHERE quiz_id = ?");
//                            preparedStatement.setInt(1, id);
//                            resultSet = preparedStatement.executeQuery();
//                            while (resultSet.next()) {
//                                int quizId = resultSet.getInt("quiz_id");
//                                String quizTitle= resultSet.getString("quiz_title");
//                                String quizDescription = resultSet.getString("quiz_description");
//                                String quizContent = resultSet.getString("quiz_content");
//                                int quizTheme=resultSet.getInt("theme_id");
//                                Quiz quiz=new Quiz(quizId,quizTitle,quizDescription,quizTheme,quizContent);
//                                currentEducator.createQuiz(quiz);
//                            }
//                        }
//                        FXMLLoader educatorLoader = new FXMLLoader(getClass().getResource("EducatorHomePage.fxml"));
//                        Parent educatorRoot = educatorLoader.load();
//                        EducatorHomePageController educatorController = educatorLoader.getController();
//                        educatorController.setup(currentEducator);
//                        educatorController.setPrimaryStage((Stage) usernameField.getScene().getWindow());
//                        Stage educatorStage = (Stage) usernameField.getScene().getWindow();
//                        educatorStage.setScene(new Scene(educatorRoot));
//                        educatorStage.show();
//                        break;
//
//                    case 2: // Parents
//                        Parents currentParents = new Parents(userId, userEmail, userUsername, userPassword, roleId, locationX, locationY);
//                        preparedStatement = connection.prepareStatement("SELECT * FROM ParentChildRelationship WHERE parent_id = ?");
//                        preparedStatement.setInt(1, currentParents.getUserId());
//                        resultSet = preparedStatement.executeQuery();
//                        List<Integer> childList = new ArrayList<>();
//                        while (resultSet.next()) {
//                            childList.add(resultSet.getInt("child_id"));
//                        }
//                        for (int id : childList) {
//                            preparedStatement = connection.prepareStatement("SELECT * FROM User WHERE user_id = ?");
//                            preparedStatement.setInt(1, id);
//                            resultSet = preparedStatement.executeQuery();
//                            while (resultSet.next()) {
//                                int childId = resultSet.getInt("user_id");
//                                String childEmail = resultSet.getString("email");
//                                String childUsername = resultSet.getString("username");
//                                String childPassword = resultSet.getString("password");
//                                double childX = resultSet.getDouble("location_coordinate_x");
//                                double childY = resultSet.getDouble("location_coordinate_y");
//                                int childPoints = resultSet.getInt("current_points");
//                                Student student = new Student(childId, childEmail, childUsername, childPassword, 3, childX, childY, childPoints);
//                                currentParents.addChild(student);
//                            }
//                        }
//                        FXMLLoader parentLoader = new FXMLLoader(getClass().getResource("ParentHomePage.fxml"));
//                        Parent parentRoot = parentLoader.load();
//                        ParentHomePageController parentController = parentLoader.getController();
//                        parentController.setup(currentParents);
////                        parentController.setUserInformation(username, userEmail, locationX + ", " + locationY);
//                        parentController.setPrimaryStage((Stage) usernameField.getScene().getWindow());
//
//                        // Show the parents homepage
//                        Stage parentStage = (Stage) usernameField.getScene().getWindow();
//                        parentStage.setScene(new Scene(parentRoot));
//                        parentStage.show();
//                        break;
//
//                    case 3: //Student
//                        Student currentStudent = new Student(userId, userEmail, userUsername, userPassword, roleId, locationX, locationY, userPoints);
//                        preparedStatement = connection.prepareStatement("SELECT * FROM ParentChildRelationship WHERE child_id = ?");
//                        preparedStatement.setInt(1, currentStudent.getUserId());
//                        resultSet = preparedStatement.executeQuery();
//                        List<Integer> parentList = new ArrayList<>();
//                        while (resultSet.next()) {
//                            parentList.add(resultSet.getInt("parent_id"));
//                        }
//                        for (int id : parentList) {
////                            System.out.println(id);
//                            preparedStatement = connection.prepareStatement("SELECT * FROM User WHERE user_id = ?");
//                            preparedStatement.setInt(1, id);
//                            resultSet = preparedStatement.executeQuery();
//                            while (resultSet.next()) {
//                                int parentId = resultSet.getInt("user_id");
//                                String parentEmail = resultSet.getString("email");
//                                String parentUsername = resultSet.getString("username");
//                                String parentPassword = resultSet.getString("password");
//                                double parentX = resultSet.getDouble("location_coordinate_x");
//                                double parentY = resultSet.getDouble("location_coordinate_y");
//                                Parents parent = new Parents(parentId, parentEmail, parentUsername, parentPassword, 2, parentX, parentY);
//                                currentStudent.addParent(parent);
//                            }
//                        }
//                        FXMLLoader studentLoader = new FXMLLoader(getClass().getResource("studentProfile.fxml"));
//                        Parent studentRoot = studentLoader.load();
//                        StudentProfileController studentController = studentLoader.getController();
//                        studentController.setup(currentStudent);
//                        studentController.setPrimaryStage((Stage) usernameField.getScene().getWindow());
//                        Stage studentStage = (Stage) usernameField.getScene().getWindow();
//                        studentStage.setScene(new Scene(studentRoot));
//                        studentStage.show();
//                        break;
//                }
//            } else {
//                // Display error message for invalid username or password
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Login Failed");
//                alert.setHeaderText(null);
//                alert.setContentText("Invalid username or password. Please try again.");
//                alert.showAndWait();
//            }
//        } catch (SQLException | IOException e) {
//            e.printStackTrace();
//            // Handle SQL error or IO error
//        } finally {
//            // Close JDBC objects in finally block
//            try {
//                if (resultSet != null) {
//                    resultSet.close();
//                }
//                if (preparedStatement != null) {
//                    preparedStatement.close();
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /*
    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // SQL query to check if username and password match
        String sql = "SELECT * FROM User WHERE username = ? AND password = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            // If a row is returned, authentication is successful
            if (resultSet.next()) {
                // Redirect to homepage or perform other actions
                System.out.println("Authentication successful. Redirecting to homepage...");
            } else {
                // Display error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL error
        } finally {
            // Close JDBC objects in finally block
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
     */
    @FXML
    private void handleRegisterButtonAction() {
        // Redirect to registration page (to be implemented by your teammate)
        System.out.println("Redirecting to registration page...");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            ((Stage) registerButton.getScene().getWindow()).setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();;
        }
    }
}
