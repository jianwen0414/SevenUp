package hackthefuture;


import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ParentHomePageController {

    private Stage primaryStage; // Reference to the primary stage

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label child1Label;

    @FXML
    private Label child2Label;

    @FXML
    private Label child3Label;

    @FXML
    private Button viewEventsButton;

    @FXML
    private Button bookingButton;
    
    private Parents currentUser;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    public void setup(Parents currentUser){
        this.currentUser = currentUser;
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        locationLabel.setText(String.format("%.2f, %.2f", currentUser.getLocationCoordinateX(), currentUser.getLocationCoordinateY()));
    }

    public void setUserInformation(String username, String email, String location) {
        // Display user information on the GUI
        usernameLabel.setText(username);
        emailLabel.setText(email);
        locationLabel.setText(location);
    }

    @FXML
    private void handleViewEventsAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewEvent_1.fxml"));
            Parent root = loader.load();
            ViewEventController controller = loader.getController();
            controller.setup(currentUser);
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
    private void handleBookingAction(ActionEvent event) {
        try {
            // Load the Create Quiz page FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateQuiz.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the Create Quiz page
            CreateQuizController controller = loader.getController();

            // Pass any necessary data to the Create Quiz controller if needed
            // For example, you can pass the user's information
            // controller.setUserInfo(usernameLabel.getText(), emailLabel.getText(), locationLabel.getText());
            // Replace the current scene with the Create Quiz page
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
}
