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
    private User currentUser;

    @FXML
    public void initialize() {
        createTopicButton.setOnAction(event -> addTopic());
        setRoundedCornersForLogo();
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

    public void setup(User currentUser) {
        this.currentUser = currentUser;
    }
    private void addTopic() {
        String topicText = topicField.getText();
        if (topicText.isEmpty()) {
            return;
        }

        topicCount++;

        // Create a new HBox to represent a topic
        HBox topicHBox = new HBox(10); // 10 is the spacing between elements
        topicHBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // Create labels for numbering, topic title, and topic starter
        Label numberLabel = new Label(String.valueOf(topicCount));
        Label titleLabel = new Label(topicText);
        Label starterLabel = new Label(currentUser.getUsername()); // Replace with the actual starter name

        // Style the labels for better layout (optional)
        numberLabel.setPrefWidth(50); // Set a fixed width for numbering
        titleLabel.setPrefWidth(300); // Set a fixed width for topic title
        starterLabel.setPrefWidth(150); // Set a fixed width for topic starter

        // Add click event to the title label to navigate to the comment page
        titleLabel.setOnMouseClicked(event -> showCommentPage(topicText));

        // Add the labels to the HBox
        topicHBox.getChildren().addAll(numberLabel, titleLabel, starterLabel);

        // Add the HBox to the VBox
        topicsVBox.getChildren().add(topicHBox);

        // Clear the text field
        topicField.clear();
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
