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
import javafx.fxml.Initializable;
import javafx.scene.Node;
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
    private ImageView mars;

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
    private ImageView rocket;

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

    /* private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    private boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    } */
    
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
        
        if (roleId == 1) {
            registered = UserUtils.registerUser(email, username, pw, selectedRole);
        } else if (roleId == 2 || roleId == 3) {
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
//            try {
//                AlertUtils.showRegistrationSuccessAlert();
//                
//                String sql = "SELECT * FROM User u WHERE u.username = ? AND u.password = ?";
//                Connection connection = DatabaseConnector.getConnection();
//                PreparedStatement preparedStatement = null;
//                ResultSet resultSet = null;
//                try {
//                    preparedStatement = connection.prepareStatement(sql);
//                    preparedStatement.setString(1, username);
//                    preparedStatement.setString(2, pw);
//                    resultSet = preparedStatement.executeQuery();
//                    
//                    if (resultSet.next()) {
//                        int userId = resultSet.getInt("user_id");
//                        String userEmail = resultSet.getString("email");
//                        String userUsername = resultSet.getString("username");
//                        String userPassword = resultSet.getString("password");
//                        double locationX = resultSet.getDouble("location_coordinate_x");
//                        double locationY = resultSet.getDouble("location_coordinate_y");
//                        int userPoints = resultSet.getInt("current_points");
//                        
//                        UserRedirectionUtils.redirectUserToHomepage(userId, roleId, userEmail, userUsername, userPassword,
//                                locationX, locationY, userPoints, connection,
//                                (Stage) regEmail.getScene().getWindow());
//                    }
//                } catch (SQLException | IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (resultSet != null) {
//                            resultSet.close();
//                        }
//                        if (preparedStatement != null) {
//                            preparedStatement.close();
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(RegisterController.class.getName()).log(Level.SEVERE, null, ex);
//            }
        } else {
            AlertUtils.showRegistrationFailedAlert();
        }
    }
}


// ori method
//    @FXML
//    void register(ActionEvent event) {
//        String email = regEmail.getText();
//        String username = regName.getText();
//        String pw = regPass.getText();
//        String conPw = regConPass.getText();
//        String selectedRole = selectRole.getValue();
//        String relationUsername = relation.getText();
//        if (UserUtils.validateRegistrationInputs(email, username, pw, conPw, selectedRole)) {
//            if (UserUtils.getRoleIdByRoleName(selectedRole) == 1) {
//                boolean registered = UserUtils.registerUser(email, username, pw, selectedRole);
//                if (registered) {
//                    AlertUtils.showRegistrationSuccessAlert();
//                    // skip to wheree??????????????????????????????????????????????????
//                } else {
//                    AlertUtils.showRegistrationFailedAlert();
//                    //--------------------------------------------------------
//                }
//            } else if ((UserUtils.getRoleIdByRoleName(selectedRole) == 2 || UserUtils.getRoleIdByRoleName(selectedRole) == 3) ) {
//                if(relationUsername.isEmpty()){
//                    AlertUtils.showRegistrationWarningAlert();
//                } else if (!UserUtils.doesUsernameExist(relationUsername)) {
//                    Alert alert = new Alert(Alert.AlertType.WARNING);
//                    alert.setTitle("Warning Alert");
//                    alert.setContentText("Your parents/child does not have an account yet.");
//                    alert.showAndWait();
//                } else {
//                    boolean registered = UserUtils.registerUserMemberExists(email, username, pw, selectedRole, relationUsername);
//                    if (registered) {
//                        AlertUtils.showRegistrationSuccessAlert();
//                    } else {
//                        AlertUtils.showRegistrationFailedAlert();
//                    }
//                }
//            }
//        }
//    }

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

    if (UserUtils.validateRegistrationInputs(email, username, pw, conPw, selectedRole) && 
        UserUtils.validateRegistrationInputs(email1, username1, pw1, conPw1, selectRole1.getValue())) {

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
 // ori method
//    @FXML
//    void registerAll(ActionEvent event) {
//        String email = regEmail.getText();
//        String username = regName.getText();
//        String pw = regPass.getText();
//        String conPw = regConPass.getText();
//        String selectedRole = selectRole.getValue();
//        String email1 = regEmail1.getText();
//        String username1 = regName1.getText();
//        String pw1 = regPass1.getText();
//        String conPw1 = regConPass1.getText();
//        if (UserUtils.validateRegistrationInputs(email, username, pw, conPw, selectedRole) && UserUtils.validateRegistrationInputs(email1, username1, pw1, conPw1, selectRole1.getValue())) {
//            boolean registered = UserUtils.registerUserWithFam(email, username, pw, selectedRole, email1, username1, pw1, selectRole1.getValue());
//            if (registered) {
//               AlertUtils.showRegistrationSuccessAlert();
//                System.out.println("Registration successful!");
//            } else {
//                AlertUtils.showRegistrationFailedAlert();
//                System.out.println("Registration failed. Please try again.");
//
//            }
//
//        }
//
//    }

    //@FXML
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
        String selectedRole = selectRole.getValue();
        if (selectedRole != null && (selectedRole.equals("Parent"))) {
            relation.setPromptText("Existing child's username/email");
            relation.setVisible(true);
            registerOther.setText("No account for your child yet? Sign up now!");
        } else if (selectedRole != null && (selectedRole.equals("Student"))) {
            relation.setPromptText("Existing parent's username/email");
            relation.setVisible(true);
            registerOther.setText("No account for parents yet? Register now!");
        }
    }

    @FXML
    void newRegister(ActionEvent event) {
        String email = regEmail.getText();
        String username = regName.getText();
        String pw = regPass.getText();
        String conPw = regConPass.getText();
        String selectedRole = selectRole.getValue();
        //String relationUsername = relation.getText();
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
        ObservableList<String> list = FXCollections.observableArrayList("Parent", "Student", "Educator");
        selectRole.setItems(list);
        relation1.setDisable(true);
        regPass.textProperty().bindBidirectional(regShowPw.textProperty());
        regConPass.textProperty().bindBidirectional(regShowConPw.textProperty());
        regPass1.textProperty().bindBidirectional(regShowPw1.textProperty());
        regConPass1.textProperty().bindBidirectional(regShowConPw1.textProperty());
    }

}
