package hackthefuture;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

/**
 * FXML Controller class
 *
 * @author User
 */
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ForumController {

    @FXML
    private TextField topicField;

    @FXML
    private Button createTopicButton;

    @FXML
    private VBox topicsVBox;

    @FXML
    private ImageView forumLogoImageView;

    private int topicCount = 0;
    private Stage primaryStage;
    private Educator currentUser;

    @FXML
    public void initialize() {
        createTopicButton.setOnAction(event -> addTopic());
        setRoundedCornersForLogo();
        loadTopicsFromDatabase();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setWidth(1200);
        primaryStage.setHeight(800);
        primaryStage.setFullScreenExitHint(""); // Optional: Remove the default full-screen exit hint
        primaryStage.setFullScreenExitKeyCombination(javafx.scene.input.KeyCombination.NO_MATCH); // Optional: Disable exiting full-screen with ESC
        primaryStage.fullScreenProperty().addListener((obs, wasFullScreen, isNowFullScreen) -> {
            if (!isNowFullScreen) {
                primaryStage.setWidth(1200);
                primaryStage.setHeight(800);
            }
        });
    }

    public void setup(Educator currentUser) {
        this.currentUser = currentUser;
    }

    private void addTopic() {
        String topicText = topicField.getText();
        if (topicText.isEmpty()) {
            return; // Do nothing if the topic field is empty
        }

        topicCount++;

        // Create a new HBox to represent a topic
        HBox topicHBox = createTopicHBox(topicCount, topicText, currentUser.getUsername());

        // Add the HBox to the VBox
        topicsVBox.getChildren().add(topicHBox);

        // Save the topic to the database
        saveTopicToDatabase(topicText, currentUser.getUserId());

        // Clear the text field
        topicField.clear();
    }

    private void saveTopicToDatabase(String title, int userId) {
        String sql = "INSERT INTO Topics (title, user_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, title);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            System.out.println("Topic saved successfully");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadTopicsFromDatabase() {
        List<String> titles = new ArrayList<>();
        List<String> usernames = new ArrayList<>();
        
        String sql = "SELECT t.title, u.username FROM Topics t JOIN User u ON t.user_id = u.user_id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                titles.add(rs.getString("title"));
                usernames.add(rs.getString("username"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        topicsVBox.getChildren().clear(); // Clear existing topics if any
        addHeader(); // Add the header row
        topicCount = 0; // Reset topic count
        for (int i = 0; i < titles.size(); i++) {
            topicCount++;
            addTopicToVBox(titles.get(i), usernames.get(i), topicCount);
        }
    }

    private void addTopicToVBox(String title, String username, int topicNumber) {
        // Create a new HBox to represent a topic
        HBox topicHBox = createTopicHBox(topicNumber, title, username);

        // Add the HBox to the VBox
        topicsVBox.getChildren().add(topicHBox);
    }

    private HBox createTopicHBox(int number, String title, String username) {
        HBox topicHBox = new HBox(10); // 10 is the spacing between elements
        topicHBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // Create labels for numbering, topic title, and topic starter
        Label numberLabel = new Label(String.valueOf(number));
        Label titleLabel = new Label(title);
        Label starterLabel = new Label(username); // Topic starter name

        // Style the labels for better layout (optional)
        numberLabel.setPrefWidth(50); // Set a fixed width for numbering
        titleLabel.setPrefWidth(300); // Set a fixed width for topic title
        starterLabel.setPrefWidth(150); // Set a fixed width for topic starter

        // Add click event to the title label to navigate to the comment page
        titleLabel.setOnMouseClicked(event -> showCommentPage(title));

        // Add the labels to the HBox
        topicHBox.getChildren().addAll(numberLabel, titleLabel, starterLabel);

        return topicHBox;
    }

    private void addHeader() {
        // Create a new HBox for the header row
        HBox headerHBox = new HBox(10); // 10 is the spacing between elements
        headerHBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // Create labels for the header
        Label numberHeader = new Label("Number");
        Label titleHeader = new Label("Topic Title");
        Label starterHeader = new Label("Topic Starter");

        // Style the labels for better layout (optional)
        numberHeader.setPrefWidth(50); // Set a fixed width for numbering
        titleHeader.setPrefWidth(300); // Set a fixed width for topic title
        starterHeader.setPrefWidth(150); // Set a fixed width for topic starter

        // Add the labels to the HBox
        headerHBox.getChildren().addAll(numberHeader, titleHeader, starterHeader);

        // Add the header HBox to the VBox
        topicsVBox.getChildren().add(headerHBox);
    }

    private void showCommentPage(String topicTitle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CommentView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Comments for " + topicTitle);
            stage.setScene(new Scene(root, 1200, 800));

            // Pass the topic title to the CommentController
            CommentController controller = loader.getController();
            controller.setup(currentUser);
            controller.setTopicTitle(topicTitle);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRoundedCornersForLogo() {
        // Ensure the dimensions of the clip match the dimensions of the ImageView
        forumLogoImageView.setFitHeight(103);  // Match the FXML fitHeight
        forumLogoImageView.setFitWidth(85);    // Match the FXML fitWidth
        
        // Create a rectangle with rounded corners
        Rectangle clip = new Rectangle(forumLogoImageView.getFitWidth(), forumLogoImageView.getFitHeight());
        clip.setArcWidth(20);  // Adjust as needed for the desired roundness
        clip.setArcHeight(20); // Adjust as needed for the desired roundness
        forumLogoImageView.setClip(clip);
    }
}
