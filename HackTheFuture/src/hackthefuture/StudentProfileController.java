/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package hackthefuture;

import hackthefuture.DatabaseConnector;
import hackthefuture.Parents;
import hackthefuture.Student;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
//import javafx.scene.Parents;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import java.util.List;

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
    private Button viewRelationship;
    @FXML
    private ListView<String> getFriendList;
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
    private Button viewprofile;

    private Student currentUser;

    //private int userID = 1;   //assume id is 1 (belum pass from login page)
    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setup(Student currentUser) {
        this.currentUser = currentUser;
        getUserName.setText(currentUser.getUsername());
        getEmail.setText(currentUser.getEmail());
        getLocationX.setText(String.format("%.2f, %.2f", currentUser.getLocationCoordinateX(), currentUser.getLocationCoordinateY()));
        int numParent = currentUser.getParents().size();
        getParent1.setText(currentUser.getParents().get(0).getUsername());
        if (numParent == 2) {
            getParent2.setText(currentUser.getParents().get(1).getUsername());
        }
        getUserName.setText(currentUser.getUsername());
        getPoint.setText(String.valueOf(currentUser.getCurrentPoints()));
        fetchFriends();
    }
    
    public void incrementPoints(){
        int currentPoint=Integer.parseInt(getPoint.getText());
        getPoint.setText(String.valueOf(currentPoint+5));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //fetchFriends();
//        try {
//            Connection connection = DatabaseConnector.getConnection();
//
//            String query = "SELECT u.user_id, u.email, u.username, u.location_coordinate_x, u.location_coordinate_y, u.current_points, " +
//                "(SELECT username FROM User WHERE user_id = (SELECT parent_id FROM ParentsChildRelationship WHERE child_id = u.user_id LIMIT 1)) AS parent1, " +
//                "(SELECT username FROM User WHERE user_id = (SELECT parent_id FROM ParentsChildRelationship WHERE child_id = u.user_id LIMIT 1 OFFSET 1)) AS parent2 " +
//                "FROM User u " +
//                "WHERE u.user_id = 1"; // Assuming user_id 1 is the child's ID
//                //"WHERE u.user_id=?";  //after passing user id from login page
//            PreparedStatement statement = connection.prepareStatement(query);
//            //statement.setInt(1, userId);
//            ResultSet resultSet = statement.executeQuery();
//            
//
//            if (resultSet.next()) {
//                Parents[] parents = new Parents[2];
//                int parentCount=0;
//                if (resultSet.getString("parent1") != null) {
//                    parents[0] = new Parents(0,"", resultSet.getString("parent1"),"",0,0,0);
//                    parentCount++;
//                }
//                if (resultSet.getString("parent2") != null) {
//                    parents[1] = new Parents(0,"",resultSet.getString("parent2"),"",0,0,0);
//                    parentCount++;
//                }
//
////                currentUser = new Student(
////                        1,    //assume is userid is 1
////                        resultSet.getString("email"),
////                        resultSet.getString("username"),"",0,    //password and roleId no need
////                        resultSet.getDouble("location_coordinate_x"),
////                        resultSet.getDouble("location_coordinate_y"),
////                        resultSet.getInt("current_points"),
////                        Arrays.copyOf(parents, parentCount)
////                );
////
////                getUsername.setText(currentUser.getUsername());
////                getUserName.setText(currentUser.getUsername());
////                getEmail.setText(currentUser.getEmail());
////                getLocationX.setText(String.valueOf(currentUser.getLocationCoordinateX()));
////                getLocationY.setText(String.valueOf(currentUser.getLocationCoordinateY()));
////                getPoint.setText(String.valueOf(currentUser.getCurrentPoints()));
////
////                if (currentUser.getParents().length > 0 && currentUser.getParentss()[0] != null) {
////                    getParents1.setText(currentUser.getParentss()[0].getUsername());
////                }
////
////                if (currentUser.getParents().length > 1 && currentUser.getParentss()[1] != null) {
////                    getParents2.setText(currentUser.getParentss()[1].getUsername());
////                }
//            }
//            statement.close();
//            connection.close();
//            
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    private void handleQuizPageButtonClick(ActionEvent event) {
//    try {
//        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("AttemptQuiz.fxml"));
//        Scene scene = new Scene(loader.load()); // Directly creating a Scene from the loaded FXML
//        // Get the stage from the event source
//        
//        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//        // Get the controller of the quiz page
//        AttemptQuizController attemptQuizController = loader.getController();
//        attemptQuizController.setCurrentUserID(userID); // Set the currentUser before the controller is initialized
//        
//
//        // Set the scene to the stage
//        stage.setScene(scene);
//        stage.show();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AttemptQuiz.fxml"));
            Parent root = loader.load();
            AttemptQuizController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setup(currentUser);
            controller.setHomePageController(this);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Attempt Quiz");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewEventsAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewEvent_1.fxml"));
            Parent root = loader.load();
            ViewEventController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setup(currentUser);
            controller.setupControllers(this);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("View Event");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLeaderboardAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
            Parent root = loader.load();
            LeaderboardController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
//            controller.setup(currentUser);// Set the current user's ID
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Leaderboard");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ////shinyen
    @FXML
    void handleAddFriendButton(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddFriend.fxml"));
            Parent root = loader.load();
            AddFriendController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setup(currentUser);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Add Friend");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewprofileButton(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewProfile.fxml"));
            Parent root = loader.load();
            ViewProfileController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setup(currentUser);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("View Profile");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDiscussionAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());
            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
            Parent root = loader.load();
            ForumController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);  // Set the primary stage
            controller.setup(currentUser);// Set the current user's ID
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Discussion");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleLogoutAction(MouseEvent event) {
        clearSessionData();
        redirectToLogin();
    }

    private void clearSessionData() {
        currentUser = null;
    }

    private void redirectToLogin() {
        try {
            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            loginController controller = loader.getController();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fetchFriends() {
        ObservableList<String> friendsList = FXCollections.observableArrayList();

        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT user2_id FROM UserFriend WHERE user1_id = ? UNION SELECT user1_id FROM UserFriend WHERE user2_id = ?"
        )) {

            stmt.setInt(1, currentUser.getUserId());
            stmt.setInt(2, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int friendId = rs.getInt(1);
                System.out.println("friend id : " + friendId);
                String friendName = getUserNameById(friendId); // Fetch friend's name by ID
                friendsList.add(friendName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        getFriendList.setItems(friendsList);
    }

    private String getUserNameById(int userId) {
        String userName = "";
        //String query = "SELECT username FROM User WHERE user_id = ?";

        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement("SELECT username FROM User WHERE user_id = ?")) {

            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                userName = resultSet.getString("username");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userName;
    }

    @FXML
    private void handleViewRelationshipAction(ActionEvent event) {
        List<ParentChildRelationship> relationships = RelationshipService.getRelationshipsForUser(currentUser.getUserId());
        Graph graph = RelationshipGraph.createGraph(relationships);
        RelationshipGraph.displayGraph(graph);
    }

    public void incrementPoints(int points) {
        int currentPoints = Integer.parseInt(getPoint.getText());
        getPoint.setText(String.valueOf(currentPoints + points));
    }

}
