package hackthefuture;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */





import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommentController {

    @FXML
    private Label topicTitleLabel;

    @FXML
    private VBox commentsVBox;

    @FXML
    private HBox commentBox;

    @FXML
    private TextField commentField;

    @FXML
    private Button addCommentButton;

    @FXML
    private Button submitCommentButton;

    private String topicTitle;
    private String username = "User"; // Replace with actual username logic
    private User currentUser;

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
        topicTitleLabel.setText(topicTitle);
        loadComments();
    }

    private void loadComments() {
        // Load comments from your data source and display them in the commentsVBox
        // This is just a placeholder example
        commentsVBox.getChildren().add(new Label("user comment: " + topicTitle));
    }

    @FXML
    private void initialize() {
        addCommentButton.setOnAction(event -> showCommentField());
        submitCommentButton.setOnAction(event -> handleAddComment());
        commentField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleAddComment();
            }
        });
    }

    private void showCommentField() {
        commentBox.setVisible(true);
        commentField.requestFocus();
    }

    @FXML
    private void handleAddComment() {
        String newComment = commentField.getText();
        if (newComment.isEmpty()) {
            return;
        }

        // Get current time
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Create VBox for the comment
        VBox commentContainer = new VBox(5); // Vertical box to stack user/time, separator, and comment
        HBox userTimeBox = new HBox(10);
        Label userTimeLabel = new Label(currentUser.getUsername() + " || " + currentTime);
        userTimeLabel.getStyleClass().add("comment-user-time-label");
        Label commentTextLabel = new Label(newComment);
        commentTextLabel.getStyleClass().add("comment-text-label");
        
        Separator separator = new Separator();
        separator.getStyleClass().add("comment-separator");

        userTimeBox.getChildren().add(userTimeLabel);
        commentContainer.getChildren().addAll(userTimeBox, commentTextLabel, separator);

        // Ensure the separator expands to fit the width
        VBox.setVgrow(separator, Priority.ALWAYS);

        // Add the new comment to the VBox
        commentsVBox.getChildren().add(commentContainer);

        // Clear the text field
        commentField.clear();
        commentBox.setVisible(false); // Hide the comment box again
    }

    void setup(Educator currentUser) {
        this.currentUser=currentUser;
    }
}
