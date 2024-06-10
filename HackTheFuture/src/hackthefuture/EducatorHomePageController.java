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

    // FXML injected UI elements
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

    private Educator currentUser; // Reference to the current user (Educator)

    // Method to set the primary stage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Method to set up the controller with the current user
    public void setup(Educator currentUser) {
        this.currentUser = currentUser;
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        locationLabel.setText(String.format("%.2f, %.2f", currentUser.getLocationCoordinateX(), currentUser.getLocationCoordinateY()));
        eventCountLabel.setText(String.valueOf(currentUser.getCreatedEvents().size()));
        quizCountLabel.setText(String.valueOf(currentUser.getCreatedQuizzes().size()));
    }

    // Method to increment the event count
    public void incrementEventCount() {
        int currentCount = Integer.parseInt(eventCountLabel.getText());
        eventCountLabel.setText(String.valueOf(currentCount + 1));
    }

    // Method to set user information
    public void setUserInformation(String username, String email, String location) {
        usernameLabel.setText(username);
        emailLabel.setText(email);
        locationLabel.setText(location);
    }

    // Handle the action of the view profile button
    @FXML
    void handleViewprofileButton(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewProfile.fxml"));
            Parent root = loader.load();
            ViewProfileController controller = loader.getController();
            controller.setPrimaryStage(primaryStage); // Set the primary stage
            controller.setup(currentUser); // Set up the controller with the current user
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("View Profile");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle the action of viewing events
    @FXML
    private void handleViewEventsButtonAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewEvent_1.fxml"));
            Parent root = loader.load();

            ViewEventController controller = loader.getController();
            controller.setPrimaryStage(primaryStage); // Set the primary stage
            controller.setup(currentUser); // Set up the controller with the current user
            StudentProfileController homePageController = null;
            controller.setupControllers(homePageController); // Set up the home page controller
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Create Quiz");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle the action of creating a quiz
    @FXML
    private void handleCreateQuizButtonAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateQuiz.fxml"));
            Parent root = loader.load();

            CreateQuizController controller = loader.getController();
            controller.setPrimaryStage(primaryStage); // Set the primary stage
            controller.setup(currentUser); // Set up the controller with the current user
            controller.setHomePageController(this); // Set the home page controller
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Create Quiz");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle the action of creating an event
    @FXML
    void handleCreateEventButtonAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateEvent_1.fxml"));
            Parent root = loader.load();
            CreateEvent_1Controller controller = loader.getController();
            controller.setPrimaryStage(primaryStage); // Set the primary stage
            controller.setup(currentUser); // Set up the controller with the current user
            controller.setHomePageController(this); // Set the home page controller
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Create Event");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle the action of viewing the leaderboard
    @FXML
    void handleLeaderboardAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
            Parent root = loader.load();
            LeaderboardController controller = loader.getController();
            controller.setPrimaryStage(primaryStage); // Set the primary stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Leaderboard");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handle the action of logging out
    @FXML
    void handleLogoutAction(MouseEvent event) {
        clearSessionData();
        redirectToLogin();
    }

    // Clear session data
    private void clearSessionData() {
        currentUser = null;
    }

    // Redirect to the login page
    private void redirectToLogin() {
        try {
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

    // Handle the action of viewing the discussion forum
    @FXML
    void handleDiscussionAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
            Parent root = loader.load();
            ForumController controller = loader.getController();
            controller.setPrimaryStage(primaryStage); // Set the primary stage
            controller.setup(currentUser); // Set up the controller with the current user
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Discussion");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Increment the quiz count
    void incrementQuizCount() {
        int currentCount = Integer.parseInt(quizCountLabel.getText());
        quizCountLabel.setText(String.valueOf(currentCount + 1));
    }
}
