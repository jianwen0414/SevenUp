package hackthefuture;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author minzi
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

public class viewOtherParentProfileController {

    @FXML
    private ListView<String> childList;

    @FXML
    private Label emailLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private ComboBox<String> monthComboBox;

//    @FXML
//    private Label username;
    @FXML
    private Label usernameLabel;

    @FXML
    private Button viewPastBookingsButton;

    @FXML
    private Button viewRelationshipButton;

    public void setup(String selectedUsername) {
        // Fetch user details
        try (Connection connection = DatabaseConnector.getConnection()) {
            // Fetch user details
            String userQuery = "SELECT email, username, location_coordinate_x, location_coordinate_y FROM User WHERE username = ?";
            try (PreparedStatement userStmt = connection.prepareStatement(userQuery)) {
                userStmt.setString(1, selectedUsername);
                try (ResultSet userRs = userStmt.executeQuery()) {
                    if (userRs.next()) {
                        emailLabel.setText(userRs.getString("email"));
                        usernameLabel.setText(userRs.getString("username"));
                        double locationX=userRs.getDouble("location_coordinate_x");
                        double locationY=userRs.getDouble("location_coordinate_y");
                        locationLabel.setText(locationX+", "+locationY);
                        //username.setText(userRs.getString("username"));
//                        locationLabel.setText(userRs.getBigDecimal("location_coordinate_x") + ", " + userRs.getBigDecimal("location_coordinate_y"));
                    }
                }
            }

            //fetch children list
            ObservableList<String> childrenList = FXCollections.observableArrayList();
            String childQuery = "SELECT u.username "
                    + "FROM ParentChildRelationship p "
                    + "JOIN User u ON p.child_id = u.user_id "
                    + "JOIN User parent ON p.parent_id = parent.user_id "
                    + "WHERE parent.username = ?";
            try (PreparedStatement userStmt = connection.prepareStatement(childQuery)) {
                userStmt.setString(1, selectedUsername);
                try (ResultSet rs = userStmt.executeQuery()) {
                    while (rs.next()) {
                        String childName = rs.getString("username");
                        childrenList.add(childName);
                    }
                }
            }
            childList.setItems(childrenList);

            //fetch past booking
            populateMonthComboBox();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewPastBookingsAction(ActionEvent event) {
        String selectedMonth = monthComboBox.getValue();
        if (selectedMonth != null) {
            int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            List<String> bookings = getPastBookingsForMonth(month);
            displayBookings(bookings);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please select a month.");
            alert.showAndWait();
        }
    }

    private List<String> getPastBookingsForMonth(int month) {
        List<String> bookings = new ArrayList<>();
        String username = usernameLabel.getText();

        try (Connection connection = DatabaseConnector.getConnection()) {
            // Prepare SQL statement to retrieve past bookings for the selected month
            String sql = "SELECT U.username AS child_username, B.destination_name, B.booking_date "
                    + "FROM UserBookingDestination B "
                    + "JOIN User U ON B.student_id = U.user_id "
                    + "JOIN User P ON B.booking_parent_id = P.user_id "
                    + "WHERE P.username = ? AND MONTH(B.booking_date) = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setInt(2, month);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Process the result set
            while (resultSet.next()) {
                String childUsername = resultSet.getString("child_username");
                String destinationName = resultSet.getString("destination_name");
                LocalDate bookingDate = resultSet.getDate("booking_date").toLocalDate();
                bookings.add(childUsername + " - " + destinationName + " - " + bookingDate);
            }

            // Close the result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    private void displayBookings(List<String> bookings) {
        if (bookings.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Past Bookings");
            alert.setHeaderText(null);
            alert.setContentText("No bookings found for the selected month.");
            alert.showAndWait();
        } else {
            StringBuilder bookingDetails = new StringBuilder();
            for (String booking : bookings) {
                bookingDetails.append(booking).append("\n");
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Past Bookings");
            alert.setHeaderText("Bookings for " + monthComboBox.getValue());
            alert.setContentText(bookingDetails.toString());
            alert.showAndWait();
        }
    }

    private void populateMonthComboBox() {
        monthComboBox.setItems(FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        ));

        // Add listener to show button when a month is selected
        monthComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                viewPastBookingsButton.setVisible(true);
            }
        });
    }

    @FXML
    private void handleViewRelationshipGraphAction(ActionEvent event) {
        String selectedUsername = usernameLabel.getText();
        int userId = getUserIdByUsername(selectedUsername);
        if (userId != -1) {
            List<ParentChildRelationship> relationships = RelationshipService.getRelationshipsForUser(userId);
            Graph graph = RelationshipGraph.createGraph(relationships);
            RelationshipGraph.displayGraph(graph);
        }
    }

    private int getUserIdByUsername(String username) {
        int userId = -1;
        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT user_id FROM User WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
}
