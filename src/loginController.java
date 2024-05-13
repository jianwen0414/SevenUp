
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

public class loginController {

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hackingthefuture";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "jianwen0414";

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
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error
        }
    }

    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // SQL query to check if username and password match and retrieve user role ID
        String sql = "SELECT u.username, u.email, u.location_coordinate_x, u.location_coordinate_y, u.role_id "
                + "FROM User u "
                + "WHERE u.username = ? AND u.password = ?";

        try {
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            // If a row is returned, authentication is successful
            if (resultSet.next()) {
                int roleId = resultSet.getInt("role_id");
                String userEmail = resultSet.getString("email");
                String locationX = resultSet.getString("location_coordinate_x");
                String locationY = resultSet.getString("location_coordinate_y");

                // Pass user information to the corresponding homepage controller based on role ID
                switch (roleId) {
                    case 1: // Educator
                        FXMLLoader educatorLoader = new FXMLLoader(getClass().getResource("EducatorHomePage.fxml"));
                        Parent educatorRoot = educatorLoader.load();
                        EducatorHomePageController educatorController = educatorLoader.getController();
                        educatorController.setUserInformation(username, userEmail, locationX + ", " + locationY);
                        educatorController.setPrimaryStage((Stage) usernameField.getScene().getWindow());

                        // Show the educator homepage
                        Stage educatorStage = (Stage) usernameField.getScene().getWindow();
                        educatorStage.setScene(new Scene(educatorRoot));
                        educatorStage.show();
                        break;

                    case 2: // Educator
                        FXMLLoader parentLoader = new FXMLLoader(getClass().getResource("ParentHomePage.fxml"));
                        Parent parentRoot = parentLoader.load();
                        ParentHomePageController parentController = parentLoader.getController();
                        parentController.setUserInformation(username, userEmail, locationX + ", " + locationY);
                        parentController.setPrimaryStage((Stage) usernameField.getScene().getWindow());

                        // Show the educator homepage
                        Stage parentStage = (Stage) usernameField.getScene().getWindow();
                        parentStage.setScene(new Scene(parentRoot));
                        parentStage.show();
                        break;
                    // Add cases for other roles (e.g., Student, Parent) similarly
                }
            } else {
                // Display error message for invalid username or password
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password. Please try again.");
                alert.showAndWait();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            // Handle SQL error or IO error
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
    }
}
