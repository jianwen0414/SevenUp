/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.net.URL;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ResourceBundle;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.sql.*;
import java.time.LocalDateTime;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author Tan Shi Han
 */
public class LeaderboardController implements Initializable {

    @FXML
    private Label name1;

    @FXML
    private Label name10;

    @FXML
    private Label name2;

    @FXML
    private Label name3;

    @FXML
    private Label name4;

    @FXML
    private Label name5;

    @FXML
    private Label name6;

    @FXML
    private Label name7;

    @FXML
    private Label name8;

    @FXML
    private Label name9;

    @FXML
    private Label points1;

    @FXML
    private Label points10;

    @FXML
    private Label points2;

    @FXML
    private Label points3;

    @FXML
    private Label points4;

    @FXML
    private Label points5;

    @FXML
    private Label points6;

    @FXML
    private Label points7;

    @FXML
    private Label points8;

    @FXML
    private Label points9;

    private User currentUser;

    @FXML
    private Button backButton;

    private Stage primaryStage;


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize a PriorityQueue to store students for the leaderboard.
    // The students are ordered primarily by current points in descending order.
        PriorityQueue<Student> leaderboard = new PriorityQueue<>(Comparator.comparingInt(Student::getCurrentPoints).reversed().thenComparing(Student::getLastPointsUpdate));

        try (Connection connection = DatabaseConnector.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM User WHERE role_id = 3 ORDER BY current_points DESC, last_points_update ASC"); //             PreparedStatement statement = connection.prepareStatement("SELECT user_id, email, username, password, role_id, location_coordinate_x, location_coordinate_y, current_points FROM User WHERE role_id = 3 ORDER BY current_points DESC");
                 ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int userId = resultSet.getInt("user_id");
                String email = resultSet.getString("email");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                int roleId = resultSet.getInt("role_id");
                double x = resultSet.getDouble("location_coordinate_x");
                double y = resultSet.getDouble("location_coordinate_y");
                int currentPoints = resultSet.getInt("current_points");
                LocalDateTime lastPointsUpdate = resultSet.getTimestamp("last_points_update").toLocalDateTime();
                // Create a new Student object. 
                Student student = new Student(userId, email, username, password, roleId, x, y, currentPoints, lastPointsUpdate);
                leaderboard.add(student);
            }
            // Update the leaderboard labels with the students in the priority queue.
            updateLeaderboardLabels(leaderboard);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateLeaderboardLabels(PriorityQueue<Student> leaderboard) {
        // Array of name labels for the leaderboard.
        Label[] nameLabels = {name1, name2, name3, name4, name5, name6, name7, name8, name9, name10};
        // Array of points labels for the leaderboard.
        Label[] pointsLabels = {points1, points2, points3, points4, points5, points6, points7, points8, points9, points10};

        int index = 0;
        // Poll the priority queue to update the labels until either the queue is empty or all labels are updated.
        while (!leaderboard.isEmpty() && index < nameLabels.length) {
            Student student = leaderboard.poll();
            nameLabels[index].setText(student.getUsername());
            pointsLabels[index].setText(String.valueOf(student.getCurrentPoints()));
            index++;
        }
    }

    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (!NavigationHistory.isEmpty()) {
            Scene previousScene = NavigationHistory.pop();
            primaryStage.setScene(previousScene);
            primaryStage.show();
        }
    }

}
