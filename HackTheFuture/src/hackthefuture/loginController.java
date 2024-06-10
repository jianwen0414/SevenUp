package hackthefuture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class loginController implements Initializable {

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hackingthefuture";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "No16juru*";

    // JDBC variables for opening, closing, and managing connection
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    @FXML
    private RadioButton emailRadio;

    @FXML
    private HBox emailBox;

    @FXML
    private HBox usernameBox;

    @FXML
    private RadioButton usernameRadio;

    @FXML
    private ToggleGroup UsernameOrEmail;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField emailField;

    @FXML
    private StackPane stackPane;

    // This method is called when the controller is initialized
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set initial state: username login is selected by default
        usernameRadio.setSelected(true);
        emailField.setVisible(false);
        emailField.setManaged(false);

        // Add listeners to radio buttons to toggle input fields
        usernameRadio.setOnAction(event -> toggleFields());
        emailRadio.setOnAction(event -> toggleFields());

        // Ensure the usernameField is focusable when initialized
        usernameField.requestFocus();

        // Register event handlers for login and register buttons
        loginButton.setOnAction(event -> handleLoginButtonAction());
        registerButton.setOnAction(event -> handleRegisterButtonAction());

        // Initialize database connection
        initializeDatabaseConnection();
    }

    // Initialize database connection
    private void initializeDatabaseConnection() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error
        }
    }

    // Handle login button action
    @FXML
    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String sql;

        // Check which radio button is selected and set the SQL query accordingly
        if (usernameRadio.isSelected()) {
            sql = "SELECT * FROM User WHERE username = ?";
        } else if (emailRadio.isSelected()) {
            sql = "SELECT * FROM User WHERE email = ?";
        } else {
            return;
        }

        try {
            // Prepare the SQL statement
            preparedStatement = connection.prepareStatement(sql);

            // Set the parameter based on the selected radio button
            if (usernameRadio.isSelected()) {
                preparedStatement.setString(1, username);
            } else {
                preparedStatement.setString(1, email);
            }

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Retrieve the stored password and salt
                String storedHashedPassword = resultSet.getString("password");
                String storedSalt = resultSet.getString("salt");

                // Hash the input password using the retrieved salt
                String hashedInputPassword = UserUtils.hashPassword(password, storedSalt);

                // Compare the hashed input password with the stored hashed password
                if (storedHashedPassword.equals(hashedInputPassword)) {
                    // Retrieve user details
                    int userId = resultSet.getInt("user_id");
                    int roleId = resultSet.getInt("role_id");
                    String userEmail = resultSet.getString("email");
                    String userUsername = resultSet.getString("username");
                    double locationX = resultSet.getDouble("location_coordinate_x");
                    double locationY = resultSet.getDouble("location_coordinate_y");
                    int userPoints = resultSet.getInt("current_points");

                    // Redirect user to the appropriate homepage
                    UserRedirectionUtils.redirectUserToHomepage(userId, roleId, userEmail, userUsername, storedHashedPassword,
                            locationX, locationY, userPoints, connection,
                            (Stage) usernameField.getScene().getWindow());
                } else {
                    // Show login error alert
                    AlertUtils.showLoginError();
                }
            } else {
                // Show account not found alert
                AlertUtils.showAccountNotFound();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            // Close resultSet and preparedStatement
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

    // Handle register button action
    @FXML
    private void handleRegisterButtonAction() {
        // Redirect to registration page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
            ((Stage) registerButton.getScene().getWindow()).setScene(new Scene(loader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Toggle visibility of input fields based on the selected radio button
    private void toggleFields() {
        if (usernameRadio.isSelected()) {
            stackPane.getChildren().setAll(usernameBox, emailBox);

            usernameField.clear();
            passwordField.clear();
            usernameBox.setVisible(true);
            usernameField.setVisible(true);
            usernameField.setManaged(true);
            emailBox.setVisible(false);
            usernameField.requestFocus();
        } else if (emailRadio.isSelected()) {
            stackPane.getChildren().setAll(emailBox, usernameBox);

            emailField.clear();
            passwordField.clear();
            emailBox.setVisible(true);
            emailField.setVisible(true);
            emailField.setManaged(true);
            usernameBox.setVisible(false);
            usernameBox.setManaged(false);
            emailField.requestFocus();
        }
    }
}
