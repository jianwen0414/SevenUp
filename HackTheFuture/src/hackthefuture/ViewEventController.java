/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author Tan Shi Han
 */
public class ViewEventController implements Initializable {

    @FXML
    private Button registerEvent;

    @FXML
    private Button registerEvent1;

    @FXML
    private Button registerEvent2;

    @FXML
    private Button registerEvent3;

    @FXML
    private Button registerEvent4;

    @FXML
    private Button registerEvent5;

    @FXML
    private Label viewEventDate;

    @FXML
    private Label viewEventDate1;

    @FXML
    private Label viewEventDate2;

    @FXML
    private Label viewEventDate3;

    @FXML
    private Label viewEventDate4;

    @FXML
    private Label viewEventDate5;

    @FXML
    private Button viewEventDetails;

    @FXML
    private Button viewEventDetails1;

    @FXML
    private Button viewEventDetails2;

    @FXML
    private Button viewEventDetails3;

    @FXML
    private Button viewEventDetails4;

    @FXML
    private Button viewEventDetails5;

    @FXML
    private Label viewEventName;

    @FXML
    private Label viewEventName1;

    @FXML
    private Label viewEventName2;

    @FXML
    private Label viewEventName3;

    @FXML
    private Label viewEventName4;

    @FXML
    private Label viewEventName5;

    @FXML
    private Label viewEventTime;

    @FXML
    private Label viewEventTime1;

    @FXML
    private Label viewEventTime2;

    @FXML
    private Label viewEventTime3;

    @FXML
    private Label viewEventTime4;

    @FXML
    private Label viewEventTime5;

    @FXML
    private HBox todayEventsBox;

    @FXML
    private HBox upcomingEventsBox;

    @FXML
    private Button backButton;

    private Stage primaryStage;

    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setup(User currentUser) {
        this.currentUser = currentUser;
        refresh();
    }

    private void refresh() {
        todayEventsBox.getChildren().clear();
        for (Event event : EventDao.getTodayEvent()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EventCard.fxml"));
                Node root = loader.load();
                EventCardController eventCardController = loader.getController();
                eventCardController.setup(event, currentUser);
                todayEventsBox.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }

        upcomingEventsBox.getChildren().clear();
        for (Event event : EventDao.getUpcomingEvent()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EventCard.fxml"));
                Node root = loader.load();
                EventCardController eventCardController = loader.getController();
                eventCardController.setup(event, currentUser);
                upcomingEventsBox.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
    }

    private int getEventId(String eventName, String eventDate, String eventTime, String eventDescription, String eventVenue) {
        // Implement this method to retrieve the event ID from the Event table based on the provided parameters
        int eventId = -1; // Initialize eventId
        try (Connection connection = DatabaseConnector.getConnection()) {
            String sql = "SELECT event_id FROM Event WHERE event_title = ? AND event_date = ? AND event_time = ? AND event_description = ? AND event_venue = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, eventName);
                statement.setString(2, eventDate);
                statement.setString(3, eventTime);
                statement.setString(4, eventDescription);
                statement.setString(5, eventVenue);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    eventId = resultSet.getInt("event_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exceptions here
        }
        return eventId;
    }

    private boolean registerForEvent(int userId, int eventId, String bookingDate) {
        if (userId == -1) {
            // No user logged in, handle accordingly
            return false;
        }

        try (Connection connection = DatabaseConnector.getConnection()) {
            String insertSql = "INSERT INTO UserBookingEvent (user_id, event_id, booking_date) VALUES (?, ?, ?)";
            String updateSql = "UPDATE User SET current_points = current_points + ? WHERE user_id = ?";

            // Begin transaction
            connection.setAutoCommit(false);

            try (PreparedStatement insertStatement = connection.prepareStatement(insertSql); PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                // Insert registration
                insertStatement.setInt(1, userId);
                insertStatement.setInt(2, eventId);
                insertStatement.setDate(3, java.sql.Date.valueOf(bookingDate));
                int rowsInserted = insertStatement.executeUpdate();

                // Update points
                if (rowsInserted > 0) {
                    // Registration successful
                    updateStatement.setInt(1, 5); // Add 5 points
                    updateStatement.setInt(2, userId);
                    int rowsUpdated = updateStatement.executeUpdate();

                    if (rowsUpdated > 0) {
                        // Commit transaction
                        connection.commit();

                        // Show success message
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Registration Successful");
                        alert.setHeaderText(null);
                        alert.setContentText("You have successfully registered for the event! You have earned 5 points.");
                        alert.showAndWait();
                        return true;
                    } else {
                        // Rollback transaction
                        connection.rollback();

                        // Show error message
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Registration Failed");
                        alert.setHeaderText(null);
                        alert.setContentText("Failed to update points. Registration aborted. Please try again.");
                        alert.showAndWait();
                    }
                } else {
                    // Registration failed
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Registration Failed");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to register for the event. Please try again.");
                    alert.showAndWait();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle any SQL exceptions here
        }
        return false;
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
