/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package hackthefuture;

import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tan Shi Han
 */
public class RegisterController implements Initializable {

    @FXML
    private Button backLogin1;

    @FXML
    private AnchorPane pane1;

    @FXML
    private AnchorPane pane2;

    @FXML
    private PasswordField regConPass;

    @FXML
    private PasswordField regConPass1;

    @FXML
    private TextField regEmail;

    @FXML
    private TextField regEmail1;

    @FXML
    private TextField regName;

    @FXML
    private TextField regName1;

    @FXML
    private PasswordField regPass;

    @FXML
    private PasswordField regPass1;

    @FXML
    private CheckBox regSelectShow;

    @FXML
    private CheckBox regSelectShow1;

    @FXML
    private TextField regShowConPw;

    @FXML
    private TextField regShowConPw1;

    @FXML
    private TextField regShowPw;

    @FXML
    private TextField regShowPw1;

    @FXML
    private Button registerOther;

    @FXML
    private Button registerOther1;

    @FXML
    private TextField relation;

    @FXML
    private TextField relation1;

    @FXML
    private ComboBox<String> selectRole;

    @FXML
    private ComboBox<String> selectRole1;

    @FXML
    private Button signUp;

    @FXML
    private Button signUp1;

    @FXML
    private Label forWho1;

    @FXML
    private Label forWho;
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @FXML
    void BackToLoginPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            loginController controller = loader.getController();
            Scene scene = new Scene(root);
            primaryStage = (Stage) signUp.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void register(ActionEvent event) {
        String email = regEmail.getText();
        String username = regName.getText();
        String pw = regPass.getText();
        String conPw = regConPass.getText();
        String selectedRole = selectRole.getValue();
        String relationUsername = relation.getText();

        if (UserUtils.validateRegistrationInputs(email, username, pw, conPw, selectedRole)) {
            int roleId = UserUtils.getRoleIdByRoleName(selectedRole);

            boolean registered = false;
            
            if (roleId == 1) { //if new user is educator
                registered = UserUtils.registerUser(email, username, pw, selectedRole);
            } else if (roleId == 2 || roleId == 3) {  // if new user is parent or a student
                if (relationUsername.isEmpty()) {
                    AlertUtils.showRegistrationWarningAlert();
                    return;
                } else if (!UserUtils.doesUsernameExist(relationUsername)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning Alert");
                    alert.setContentText("Your parents/child does not have an account yet.");
                    alert.showAndWait();
                    return;

                } else {
                    registered = UserUtils.registerUserMemberExists(email, username, pw, selectedRole, relationUsername);
                }
            }

            if (registered) {
                AlertUtils.showRegistrationSuccessAlert();

                try (Connection connection = DatabaseConnector.getConnection()) {
                    // Fetch user details for redirection
                    String sql = "SELECT * FROM User WHERE username = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                        preparedStatement.setString(1, username);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            if (resultSet.next()) {
                                int userId = resultSet.getInt("user_id");
                                String userEmail = resultSet.getString("email");
                                String userUsername = resultSet.getString("username");
                                String userPassword = resultSet.getString("password");
                                double locationX = resultSet.getDouble("location_coordinate_x");
                                double locationY = resultSet.getDouble("location_coordinate_y");
                                int userPoints = resultSet.getInt("current_points");

                                UserRedirectionUtils.redirectUserToHomepage(userId, roleId, userEmail, userUsername, userPassword,
                                        locationX, locationY, userPoints, connection,
                                        (Stage) ((Node) event.getSource()).getScene().getWindow());
                            } else {
                                System.out.println("User not found in the database after registration.");
                            }
                        }
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                AlertUtils.showRegistrationFailedAlert();
            }
        }
    }


    @FXML
    void registerAll(ActionEvent event) {
        String email = regEmail.getText();
        String username = regName.getText();
        String pw = regPass.getText();
        String conPw = regConPass.getText();
        String selectedRole = selectRole.getValue();
        String email1 = regEmail1.getText();
        String username1 = regName1.getText();
        String pw1 = regPass1.getText();
        String conPw1 = regConPass1.getText();

        if (UserUtils.validateRegistrationInputs(email, username, pw, conPw, selectedRole)
                && UserUtils.validateRegistrationInputs(email1, username1, pw1, conPw1, selectRole1.getValue())) {
            if (email.equals(email1)) {
                AlertUtils.showEmailSame();
                return;
            } else if (username.equals(username1)) {
                AlertUtils.showUsernameSame();
                return;
            }
            boolean registered = UserUtils.registerUserWithFam(email, username, pw, selectedRole, email1, username1, pw1, selectRole1.getValue());

            if (registered) {
                AlertUtils.showRegistrationSuccessAlert();

                // Fetch user information for redirection
                try (Connection connection = DatabaseConnector.getConnection()) {
                    String sql = "SELECT * FROM User WHERE username = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, username);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        int userId = resultSet.getInt("user_id");
                        int roleId = resultSet.getInt("role_id");
                        String userEmail = resultSet.getString("email");
                        String userUsername = resultSet.getString("username");
                        String userPassword = resultSet.getString("password");
                        Double locationX = resultSet.getDouble("location_coordinate_x");
                        Double locationY = resultSet.getDouble("location_coordinate_y");
                        int userPoints = resultSet.getInt("current_points");

                        // Redirect user based on role
                        UserRedirectionUtils.redirectUserToHomepage(userId, roleId, userEmail, userUsername, userPassword, locationX, locationY, userPoints, connection, (Stage) ((Node) event.getSource()).getScene().getWindow());
                    }

                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                AlertUtils.showRegistrationFailedAlert();
            }
        }
    }
    
    public void showPw() {
        if (regSelectShow.isSelected()) {
            regShowPw.setVisible(true);
            regShowConPw.setVisible(true);
            regPass.setVisible(false);
            regConPass.setVisible(false);
        } else {
            regShowPw.setVisible(false);
            regShowConPw.setVisible(false);
            regPass.setVisible(true);
            regConPass.setVisible(true);
        }

        if (regSelectShow1.isSelected()) {
            regShowPw1.setText(regPass1.getText());
            regShowConPw1.setText(regConPass1.getText());
            regShowPw1.setVisible(true);
            regShowConPw1.setVisible(true);
            regPass1.setVisible(false);
            regConPass1.setVisible(false);
        } else {
            regPass1.setText(regShowPw1.getText());
            regConPass1.setText(regShowConPw1.getText());
            regShowPw1.setVisible(false);
            regShowConPw1.setVisible(false);
            regPass1.setVisible(true);
            regConPass1.setVisible(true);
        }
    }

    @FXML
    private void handleRole(ActionEvent event) {
            // Set the text of the button to let user click on it
        String selectedRole = selectRole.getValue();
        if (selectedRole != null && (selectedRole.equals("Parent"))) {
            relation.setPromptText("Existing child's username");
            relation.setVisible(true);
            registerOther.setVisible(true);
            registerOther.setText("No account for your child yet? Click here to sign up now!");
        } else if (selectedRole != null && (selectedRole.equals("Student"))) {
            relation.setPromptText("Existing parent's username");
            relation.setVisible(true);
            registerOther.setVisible(true);
            registerOther.setText("No account for parents yet? Click here to register now!");
        }else if(selectedRole !=null && (selectedRole.equals("Educator"))){
            relation.setVisible(false);
            registerOther.setVisible(false);
        }
    }

    @FXML
    void newRegister(ActionEvent event) {
        // changes of visibilty of anchor pane to let user register for his parent/student 
        String email = regEmail.getText();
        String username = regName.getText();
        String pw = regPass.getText();
        String conPw = regConPass.getText();
        String selectedRole = selectRole.getValue();
        if (UserUtils.validateRegistrationInputs(email, username, pw, conPw, selectedRole)) {
            pane1.setVisible(false);
            pane2.setVisible(true);
            if (selectRole.getValue().equals("Student")) {
                selectRole1.setValue("Parent");
                forWho1.setText("for Parent");
                relation1.setText(regName.getText() + " (student)");
            } else {
                selectRole1.setValue("Student");
                forWho1.setText("for Child");
                relation1.setText(regName.getText() + " (parent)");
            }
            selectRole1.setDisable(true);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Create an observable list with role options and set it to the selectRole ComboBox.
        ObservableList<String> list = FXCollections.observableArrayList("Parent", "Student", "Educator");
        selectRole.setItems(list);
        relation1.setDisable(true);
        // Bind the text properties of password fields and their corresponding show password fields.
    // This ensures that both fields will always display the same text, providing functionality to toggle password visibility.
        regPass.textProperty().bindBidirectional(regShowPw.textProperty());
        regConPass.textProperty().bindBidirectional(regShowConPw.textProperty());
        regPass1.textProperty().bindBidirectional(regShowPw1.textProperty());
        regConPass1.textProperty().bindBidirectional(regShowConPw1.textProperty());
    }

}
