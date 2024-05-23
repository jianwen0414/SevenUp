package hackthefuture;

import hackthefuture.CreateQuizController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EducatorHomePageController {

    private Stage primaryStage; // Reference to the primary stage

    private int userId;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label quizCountLabel; // Label to display the number of quizzes created

    @FXML
    private Label eventCountLabel; // Label to display the number of events created

    @FXML
    private Button viewEventsButton;

    @FXML
    private Button createEventButton;

    @FXML
    private Button createQuizButton;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        loadQuizCount(); // Load the quiz count when the user ID is set
        loadEventCount(); // Load the event count when the user ID is set
    }

    public void setUserInformation(String username, String email, String location) {
        // Display user information on the GUI
        usernameLabel.setText(username);
        emailLabel.setText(email);
        locationLabel.setText(location);
    }

    @FXML
    private void handleViewEventsButtonAction(ActionEvent event) {
        try {
            // Load the Create Quiz page FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateQuiz.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the Create Quiz page
            CreateQuizController controller = loader.getController();
            controller.setCurrentUserId(userId); // Set the current user's ID

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
    private void handleCreateQuizButtonAction(ActionEvent event) {
        try {
            // Load the Create Quiz page FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateQuiz.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the Create Quiz page
            CreateQuizController controller = loader.getController();
            controller.setCurrentUserId(userId); // Set the current user's ID

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

    private void loadQuizCount() {
        String query = "SELECT COUNT(*) AS quiz_count FROM QuizCreationRecord WHERE creator_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hackingthefuture", "root", "jianwen0414");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int quizCount = resultSet.getInt("quiz_count");
                    quizCountLabel.setText(String.valueOf(quizCount));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadEventCount() {
        String query = "SELECT COUNT(*) AS event_count FROM EventCreationRecord WHERE creator_id = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hackingthefuture", "root", "jianwen0414");
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int eventCount = resultSet.getInt("event_count");
                    eventCountLabel.setText(String.valueOf(eventCount));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
