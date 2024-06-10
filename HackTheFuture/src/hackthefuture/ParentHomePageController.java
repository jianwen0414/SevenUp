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

    @FXML
    private Button viewprofile;

    private Parents currentUser; // Reference to the current user (parent)

    // Method to set the primary stage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Method to set up the parent home page with user data
    public void setup(Parents currentUser) {
        this.currentUser = currentUser;
        usernameLabel.setText(currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        locationLabel.setText(String.format("%.2f, %.2f", currentUser.getLocationCoordinateX(), currentUser.getLocationCoordinateY()));
        displayChildren(currentUser.getUsername());
        populateMonthComboBox();
    }

    // Handler for the "View Events" button
    @FXML
    private void handleViewEventsAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene()); // Save the current scene to navigation history

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewEvent_1.fxml"));
            Parent root = loader.load();
            ViewEventController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);  // Set the primary stage in the new controller
            controller.setup(currentUser);
            StudentProfileController homePageController = null;
            controller.setupControllers(homePageController);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("View Event");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handler for the "Make Booking" button
    @FXML
    private void handleBookingAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene()); // Save the current scene to navigation history

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MakeBookings.fxml"));
            Parent root = loader.load();
            MakeBookingsController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);  // Set the primary stage in the new controller
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

    // Handler for the "Leaderboard" button
    @FXML
    void handleLeaderboardAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene()); // Save the current scene to navigation history

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Leaderboard.fxml"));
            Parent root = loader.load();
            LeaderboardController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);  // Set the primary stage in the new controller
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Leaderboard");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handler for the "View Past Bookings" button
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

    // Handler for the "View Profile" button
    @FXML
    void handleViewprofileButton(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene()); // Save the current scene to navigation history

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewProfile.fxml"));
            Parent root = loader.load();
            ViewProfileController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);  // Set the primary stage in the new controller
            controller.setup(currentUser);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("View Profile");

            // Show the new scene
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handler for the "Discussion" button
    @FXML
    void handleDiscussionAction(ActionEvent event) {
        try {
            NavigationHistory.push(primaryStage.getScene()); // Save the current scene to navigation history

            // Load the discussion view FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
            Parent root = loader.load();
            ForumController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);  // Set the primary stage in the new controller
            controller.setup(currentUser); // Set up the current user
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Discussion");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Display children associated with the parent
    private void displayChildren(String username) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String sql = "SELECT U.username FROM User U JOIN ParentChildRelationship PCR ON U.user_id = PCR.child_id "
                    + "JOIN User P ON P.user_id = PCR.parent_id WHERE P.username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

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

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Populate the month combo box with month names
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

    // Retrieve past bookings for the selected month
    private List<String> getPastBookingsForMonth(int month) {
        List<String> bookings = new ArrayList<>();
        String username = usernameLabel.getText();

        try (Connection connection = DatabaseConnector.getConnection()) {
            String sql = "SELECT U.username AS child_username, B.destination_name, B.booking_date "
                    + "FROM UserBookingDestination B "
                    + "JOIN User U ON B.student_id = U.user_id "
                    + "JOIN User P ON B.booking_parent_id = P.user_id "
                    + "WHERE P.username = ? AND MONTH(B.booking_date) = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setInt(2, month);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String childUsername = resultSet.getString("child_username");
                String destinationName = resultSet.getString("destination_name");
                LocalDate bookingDate = resultSet.getDate("booking_date").toLocalDate();
                bookings.add(childUsername + " - " + destinationName + " - " + bookingDate);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }

    // Display bookings in an alert dialog
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

    // Handler for the "Logout" button
    @FXML
    void handleLogoutAction(MouseEvent event) {
        clearSessionData(); // Clear the current user session data
        redirectToLogin(); // Redirect to the login page
    }

    // Clear the current user session data
    private void clearSessionData() {
        currentUser = null;
    }

    // Redirect to the login page
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

    // Handler for the "View Relationship" button
    @FXML
    private void handleViewRelationshipAction(ActionEvent event) {
        List<ParentChildRelationship> relationships = RelationshipService.getRelationshipsForUser(currentUser.getUserId());
        Graph graph = RelationshipGraph.createGraph(relationships);
        RelationshipGraph.displayGraph(graph);
    }
}
