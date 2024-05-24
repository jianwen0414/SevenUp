/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
//import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author minzi
 */
public class StudentProfileController implements Initializable {

    @FXML
    private Label getUsername;
    @FXML
    private Button quizPage;
    @FXML
    private Button eventPage;
    @FXML
    private Button addFriend;
    @FXML
    private ComboBox<?> getFriend;
    @FXML
    private Label getUserName;
    @FXML
    private Label getEmail;
    @FXML
    private Label getLocationX;
    @FXML
    private Label getPoint;
    @FXML
    private Label getParent1;
    @FXML
    private Label getParent2;
    @FXML
    private Label getLocationY;
    @FXML
    private Button noti;

    private Student currentUser; 
    
    private int userID=1;   //assume id is 1 (belum pass from login page)
    
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
//    public void setUserInformation(int userId) {    //pass from login page
//        this.userID = userId;
//    }
    
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection connection = DatabaseConnection.getConnection();

            String query = "SELECT u.user_id, u.email, u.username, u.location_coordinate_x, u.location_coordinate_y, u.current_points, " +
                "(SELECT username FROM User WHERE user_id = (SELECT parent_id FROM ParentChildRelationship WHERE child_id = u.user_id LIMIT 1)) AS parent1, " +
                "(SELECT username FROM User WHERE user_id = (SELECT parent_id FROM ParentChildRelationship WHERE child_id = u.user_id LIMIT 1 OFFSET 1)) AS parent2 " +
                "FROM User u " +
                "WHERE u.user_id = 1"; // Assuming user_id 1 is the child's ID
                //"WHERE u.user_id=?";  //after passing user id from login page
            PreparedStatement statement = connection.prepareStatement(query);
            //statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            

            if (resultSet.next()) {
                Parent[] parents = new Parent[2];
                int parentCount=0;
                if (resultSet.getString("parent1") != null) {
                    parents[0] = new Parent(0,"", resultSet.getString("parent1"),"",0,0,0);
                    parentCount++;
                }
                if (resultSet.getString("parent2") != null) {
                    parents[1] = new Parent(0,"",resultSet.getString("parent2"),"",0,0,0);
                    parentCount++;
                }

                currentUser = new Student(
                        1,    //assume is userid is 1
                        resultSet.getString("email"),
                        resultSet.getString("username"),"",0,    //password and roleId no need
                        resultSet.getDouble("location_coordinate_x"),
                        resultSet.getDouble("location_coordinate_y"),
                        resultSet.getInt("current_points"),
                        Arrays.copyOf(parents, parentCount)
                );

                getUsername.setText(currentUser.getUsername());
                getUserName.setText(currentUser.getUsername());
                getEmail.setText(currentUser.getEmail());
                getLocationX.setText(String.valueOf(currentUser.getLocationCoordinateX()));
                getLocationY.setText(String.valueOf(currentUser.getLocationCoordinateY()));
                getPoint.setText(String.valueOf(currentUser.getCurrentPoints()));

                if (currentUser.getParents().length > 0 && currentUser.getParents()[0] != null) {
                    getParent1.setText(currentUser.getParents()[0].getUsername());
                }

                if (currentUser.getParents().length > 1 && currentUser.getParents()[1] != null) {
                    getParent2.setText(currentUser.getParents()[1].getUsername());
                }
            }
            statement.close();
            connection.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleQuizPageButtonClick(ActionEvent event) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AttemptQuiz.fxml"));
        Scene scene = new Scene(loader.load()); // Directly creating a Scene from the loaded FXML
        // Get the stage from the event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Get the controller of the quiz page
        AttemptQuizController attemptQuizController = loader.getController();
        attemptQuizController.setCurrentUserID(userID); // Set the currentUser before the controller is initialized
        

        // Set the scene to the stage
        stage.setScene(scene);
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }

    //still assume userid=1
    //add friend there jump to add fren page (not yet set)
    //show friend list (not yet add database)
    //notification button jump to show friend request page (not yet set) 
    //now only access first student id(not yet edit for multiple student user)
}
