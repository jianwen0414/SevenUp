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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    private int topicId; // Add a field for topic ID
    private Educator currentUser;

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

    public void setup(Educator currentUser) {
        this.currentUser = currentUser;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
        topicTitleLabel.setText(topicTitle); // Ensure the topic title is set in the UI
        loadTopicId();
        loadComments();
    }

    private void loadTopicId() {
        String sql = "SELECT topic_id FROM Topics WHERE title = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, topicTitle);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                this.topicId = rs.getInt("topic_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadComments() {
        commentsVBox.getChildren().clear();
        
        // Add header showing the topic title
        addHeader();
        
        String sql = "SELECT c.content, c.created_at, u.username FROM Comments c JOIN User u ON c.user_id = u.user_id WHERE c.topic_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, topicId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String content = rs.getString("content");
                String createdAt = rs.getString("created_at");
                String username = rs.getString("username");
                addCommentToVBox(content, username, createdAt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addHeader() {
        // Create a new HBox for the header row
        HBox headerHBox = new HBox(10); // 10 is the spacing between elements
        headerHBox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        // Create a label for the header
        Label titleHeader = new Label( topicTitle);

        // Style the label to make it bigger and bold
        titleHeader.setStyle("-fx-font-size: 25px; -fx-font-weight: bold;");

        // Add the label to the HBox
        headerHBox.getChildren().add(titleHeader);

        // Add the header HBox to the VBox
        commentsVBox.getChildren().add(headerHBox);
    }

    private void addCommentToVBox(String content, String username, String createdAt) {
        VBox commentContainer = new VBox(5);
        HBox userTimeBox = new HBox(10);
        Label userTimeLabel = new Label(username + " || " + createdAt);
        userTimeLabel.getStyleClass().add("comment-user-time-label");
        Label commentTextLabel = new Label(content);
        commentTextLabel.getStyleClass().add("comment-text-label");
        
        Separator separator = new Separator();
        separator.getStyleClass().add("comment-separator");

        userTimeBox.getChildren().add(userTimeLabel);
        commentContainer.getChildren().addAll(userTimeBox, commentTextLabel, separator);
        VBox.setVgrow(separator, Priority.ALWAYS);
        commentsVBox.getChildren().add(commentContainer);
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

        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        saveCommentToDatabase(newComment, currentUser.getUserId(), topicId);

        addCommentToVBox(newComment, currentUser.getUsername(), currentTime);

        commentField.clear();
        commentBox.setVisible(false);
    }

    private void saveCommentToDatabase(String content, int userId, int topicId) {
        String sql = "INSERT INTO Comments (topic_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, topicId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, content);
            pstmt.executeUpdate();
            System.out.println("Comment saved successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
