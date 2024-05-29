package hackthefuture;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

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
    private Label child4Label;

    @FXML
    private Label child5Label;

    @FXML
    private Label child6Label;

    @FXML
    private Button viewEventsButton;

    @FXML
    private Button bookingButton;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private Button viewPastBookingsButton;

    @FXML
    private Button viewRelationship;

    private Parents currentUser;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setup(Parents currentUser) {
        this.currentUser = currentUser;
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        locationLabel.setText(String.format("%.2f, %.2f", currentUser.getLocationCoordinateX(), currentUser.getLocationCoordinateY()));
        displayChildren(currentUser.getUsername());
        populateMonthComboBox();
    }

    /*
    public void setUserInformation(String username, String email, String location) {
        // Display user information on the GUI
        usernameLabel.setText(username);
        emailLabel.setText(email);
        locationLabel.setText(location);       
    }
     */
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeBookings.fxml"));
            Parent root = loader.load();
            MakeBookingsController controller = loader.getController();
            controller.setup(currentUser);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Make Booking");

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

    private void displayChildren(String username) {
        // Connect to the database
        try (Connection connection = DatabaseConnector.getConnection()) {
            // Prepare SQL statement to retrieve children's usernames
            String sql = "SELECT U.username FROM User U JOIN ParentChildRelationship PCR ON U.user_id = PCR.child_id "
                    + "JOIN User P ON P.user_id = PCR.parent_id WHERE P.username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display children's usernames on labels
            int childCount = 0;
            while (resultSet.next() && childCount < 6) {
                String childUsername = resultSet.getString("username");
                switch (childCount) {
                    case 0:
                        child1Label.setText(childUsername);
                        break;
                    case 1:
                        child2Label.setText(childUsername);
                        break;
                    case 2:
                        child3Label.setText(childUsername);
                        break;
                    case 3:
                        child4Label.setText(childUsername);
                        break;
                    case 4:
                        child5Label.setText(childUsername);
                        break;
                    case 5:
                        child6Label.setText(childUsername);
                        break;

                }
                childCount++;
            }

            // Close the result set and statement
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

    @FXML
    void handleLogoutAction(MouseEvent event) {
        clearSessionData();
        redirectToLogin();
    }

    private void clearSessionData() {
        currentUser = null;
    }

    private void redirectToLogin() {
        try {
            // Load the login FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            loginController controller = loader.getController();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewRelationshipAction(ActionEvent event) {
        List<ParentChildRelationship> relationships = RelationshipService.getRelationshipsForUser(currentUser.getUserId());
        Graph graph = RelationshipGraph.createGraph(relationships);
        RelationshipGraph.displayGraph(graph);
    }

}
