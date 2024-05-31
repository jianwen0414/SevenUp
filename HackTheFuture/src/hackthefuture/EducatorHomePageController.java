package hackthefuture;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.io.IOException;
import javafx.scene.input.MouseEvent;

public class EducatorHomePageController {

    private Stage primaryStage; // Reference to the primary stage

//    private int userId;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label eventCountLabel;
    
    @FXML
    private Label quizCountLabel;

    
    @FXML
    private Button viewprofile;

    @FXML
    private Button createQuizButton;
    
    private Educator currentUser;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

//    public void setUserId(int userId) {
//        this.userId = userId;
//    }
    
    public void setup(Educator currentUser){
        this.currentUser = currentUser;
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        locationLabel.setText(String.format("%.2f, %.2f", currentUser.getLocationCoordinateX(), currentUser.getLocationCoordinateY()));
//        viewEventsButton.setOnAction(eh->handleViewEventsButtonAction(eh));
        eventCountLabel.setText(String.valueOf(currentUser.getCreatedEvents().size()));
        quizCountLabel.setText(String.valueOf(currentUser.getCreatedQuizzes().size()));
        

    }

    
    public void setUserInformation(String username, String email, String location) {
        // Display user information on the GUI
        usernameLabel.setText(username);
        emailLabel.setText(email);
        locationLabel.setText(location);
    }

     @FXML
    void handleViewprofileButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewProfile.fxml"));
            Parent root = loader.load();
            ViewProfileController controller = loader.getController();
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
    private void handleViewEventsButtonAction(ActionEvent event) {
        try {
            // Load the Create Quiz page FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewEvent_1.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the Create Quiz page
            ViewEventController controller = loader.getController();
            controller.setup(currentUser);// Set the current user's ID
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Create Quiz");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCreateQuizButtonAction(ActionEvent event) {
        try {
            // Load the Create Quiz page FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateQuiz.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the Create Quiz page
            CreateQuizController controller = loader.getController();
            controller.setup(currentUser);// Set the current user's ID
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Create Quiz");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleCreateEventButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateEvent_1.fxml"));
            Parent root = loader.load();
            CreateEvent_1Controller controller = loader.getController();
            controller.setup(currentUser);// Set the current user's ID
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Create Event");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Add any additional methods or event handlers as needed
    @FXML
    void handleLeaderboardAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
            Parent root = loader.load();
            LeaderboardController controller = loader.getController();
//            controller.setup(currentUser);// Set the current user's ID
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Leaderboard");
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
            loginController controller=loader.getController();
            Scene scene=new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    void handleDiscussionAction(ActionEvent event) {
        try {
            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
            Parent root = loader.load();
//            ForumController controller=loader.getController();
            Scene scene=new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Discussion");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
