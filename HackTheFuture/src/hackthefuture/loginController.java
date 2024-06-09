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
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
// Set initial state
 
        usernameRadio.setSelected(true);
    emailField.setVisible(false);
    emailField.setManaged(false);

    // Add listeners to radio buttons
    usernameRadio.setOnAction(event -> toggleFields());
    emailRadio.setOnAction(event -> toggleFields());

    // Ensure the usernameField is focusable when initialized
    usernameField.requestFocus();
        // Register event handler for login button
        loginButton.setOnAction(event -> handleLoginButtonAction());
        registerButton.setOnAction(event -> handleRegisterButtonAction());
        // Set initial focus on username field
//        usernameField.requestFocus();        // Optional: You can perform additional initialization here
        
        // Initialize database connection
        initializeDatabaseConnection();
    }
//    @FXML
//    private void initialize() {
//// Set initial state
//        usernameField.setText("");
//        passwordField.setText("");
//        emailField.setText("");
//        usernameRadio.setSelected(true);
//    emailField.setVisible(false);
//    emailField.setManaged(false);
//
//    // Add listeners to radio buttons
//    usernameRadio.setOnAction(event -> toggleFields());
//    emailRadio.setOnAction(event -> toggleFields());
//
//    // Ensure the usernameField is focusable when initialized
//    usernameField.requestFocus();
//        // Register event handler for login button
//        loginButton.setOnAction(event -> handleLoginButtonAction());
//        registerButton.setOnAction(event -> handleRegisterButtonAction());
//        // Set initial focus on username field
////        usernameField.requestFocus();        // Optional: You can perform additional initialization here
//        
//        // Initialize database connection
//        initializeDatabaseConnection();
//    }
    
 

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
        preparedStatement = connection.prepareStatement(sql);
        
        // Set the parameter based on the selected radio button
        if (usernameRadio.isSelected()) {
            preparedStatement.setString(1, username);
        } else {
            preparedStatement.setString(1, email);
        }


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

    private void toggleFields() {
if (usernameRadio.isSelected()) {
            stackPane.getChildren().setAll(usernameBox, emailBox);

        usernameField.clear();
        passwordField.clear();
        usernameBox.setVisible(true);
        usernameField.setVisible(true);
        usernameField.setManaged(true);
        emailBox.setVisible(false);
//        emailField.setVisible(false);
//        emailField.setManaged(false);
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
//        usernameField.setVisible(false);
//        usernameField.setManaged(false);
        emailField.requestFocus();
    }    }
}
