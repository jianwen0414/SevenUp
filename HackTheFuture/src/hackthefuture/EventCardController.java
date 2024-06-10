/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Tan Shi Han
 */
public class EventCardController implements Initializable {

    @FXML
    private Button registerEvent;

    @FXML
    private Label viewEventDate;

    @FXML
    private Button viewEventDetails;

    @FXML
    private Label viewEventName;

    @FXML
    private Label viewEventTime;

    private Event event;
    private User currentUser;
    private StudentProfileController studentProfileController;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setup(Event event, User currentUser) {
        this.event = event;
        this.currentUser = currentUser;
        refresh();
    }
    
    private void refresh() {
        viewEventName.setText("Event Name: " + event.getTitle());
        viewEventDate.setText("Date: " + event.getDate().toString());
        viewEventTime.setText("Time: " + event.getTime().toString());
        registerEvent.setVisible(currentUser.getRoleId() == 3);
        try (Connection connection = DatabaseConnector.getConnection()) {
            //Check the Event, if the event has already registered, disable the register button
        String checkEventExistsSql = "SELECT COUNT(*) FROM UserBookingEvent WHERE user_id = ? AND event_id = ?";
        
        try (PreparedStatement checkEventExistsStatement = connection.prepareStatement(checkEventExistsSql)) {
            checkEventExistsStatement.setInt(1, currentUser.getUserId());
            checkEventExistsStatement.setInt(2, event.getEventId());
            ResultSet eventExistsResultSet = checkEventExistsStatement.executeQuery();
            eventExistsResultSet.next();
            int eventExistsCount = eventExistsResultSet.getInt(1);

            if (eventExistsCount > 0) {
                registerEvent.setText("Registered");
                registerEvent.setDisable(true);
            } 
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle any SQL exceptions here
    }
        viewEventDetails.setOnAction(eh -> handleViewDetails());
        registerEvent.setOnAction(eh -> handleRegisterButtonClick());
    }

    private void handleViewDetails() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Event Details");
        alert.setHeaderText(null);
        alert.setContentText("Event Name: " + event.getTitle() + "\n"
                + "Event Description: " + event.getDescription() + "\n"
                + "Event Venue: " + event.getVenue() + "\n"
                + "Event Date: " + event.getDate().toString() + "\n"
                + "Event Time: " + event.getTime().toString());
        alert.showAndWait();
    }

    private void handleRegisterButtonClick() {
        // Update button text and disable it if registration was successful
        if (registerForEvent()) {
            registerEvent.setText("Registered");
            registerEvent.setDisable(true);
        }
    }

 private boolean registerForEvent() {
    if (currentUser.getUserId() == -1) {
        // No user logged in, handle accordingly
        return false;
    }

    try (Connection connection = DatabaseConnector.getConnection()) {
        String checkEventsSql = "SELECT COUNT(*) FROM UserBookingEvent WHERE user_id = ? AND booking_date = ?";
        String checkDestinationsSql = "SELECT COUNT(*) FROM UserBookingDestination WHERE student_id = ? AND booking_date = ?";
        String insertSql = "INSERT INTO UserBookingEvent (user_id, event_id, booking_date) VALUES (?, ?, ?)";
        String updateSql = "UPDATE User SET current_points = current_points + ? WHERE user_id = ?";

        // Begin transaction
        connection.setAutoCommit(false);

        try (PreparedStatement checkEventsStatement = connection.prepareStatement(checkEventsSql);
             PreparedStatement checkDestinationsStatement = connection.prepareStatement(checkDestinationsSql);
             PreparedStatement insertStatement = connection.prepareStatement(insertSql);
             PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

            // Check for existing events on the selected date
            checkEventsStatement.setInt(1, currentUser.getUserId());
            checkEventsStatement.setDate(2, Date.valueOf(event.getDate()));
            ResultSet eventsResultSet = checkEventsStatement.executeQuery();
            eventsResultSet.next();
            int eventsCount = eventsResultSet.getInt(1);

            // Check for existing destinations on the selected date
            checkDestinationsStatement.setInt(1, currentUser.getUserId());
            checkDestinationsStatement.setDate(2, Date.valueOf(event.getDate()));
            ResultSet destinationsResultSet = checkDestinationsStatement.executeQuery();
            destinationsResultSet.next();
            int destinationsCount = destinationsResultSet.getInt(1);

            if (eventsCount > 0 || destinationsCount > 0) {
                // User already has an event or destination on the selected date
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Registration Failed");
                alert.setHeaderText(null);
                alert.setContentText("You already have an event or destination booked on this date.");
                alert.showAndWait();
                return false;
            }

            // Insert registration
            insertStatement.setInt(1, currentUser.getUserId());
            insertStatement.setInt(2, event.getEventId());
            insertStatement.setDate(3, Date.valueOf(event.getDate()));
            int rowsInserted = insertStatement.executeUpdate();

            // Update points
            if (rowsInserted > 0) {
                // Registration successful
                updateStatement.setInt(1, 5); // Add 5 points
                updateStatement.setInt(2, currentUser.getUserId());
                int rowsUpdated = updateStatement.executeUpdate();

                if (rowsUpdated > 0) {
                    // Commit transaction
                    connection.commit();

                    // Show success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Registration Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("You have successfully registered for the event! You have earned 5 points.");
                    alert.showAndWait();
                    if (studentProfileController != null) {
                        System.out.println("Inside if: studentProfileController is not null");
                        studentProfileController.incrementPoints();
                    } else {
                        System.out.println("Inside if: studentProfileController is null");
                    }
                    return true;
                }
            }

            // If registration or points update failed, show error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Registration Failed");
            alert.setHeaderText(null);
            alert.setContentText("Failed to register for the event. Please try again.");
            alert.showAndWait();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    public void setStudentProfileController(StudentProfileController controller) {
        this.studentProfileController = controller;
        System.out.println("event card set successful"+studentProfileController);
    }

}
