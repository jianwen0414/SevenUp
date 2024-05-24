package hackthefuture;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

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
    private Button viewEventsButton;

    @FXML
    private Button bookingButton;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setUserInformation(String username, String email, String location) {
        // Display user information on the GUI
        usernameLabel.setText(username);
        emailLabel.setText(email);
        locationLabel.setText(location);

        // Retrieve and display parent's children
        displayChildren(username);
    }

    private void displayChildren(String username) {
        // Connect to the database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/hackingthefuture", "root", "jianwen0414") ){
            // Prepare SQL statement to retrieve children's usernames
            String sql = "SELECT U.username FROM User U JOIN ParentChildRelationship PCR ON U.user_id = PCR.child_id " +
                         "JOIN User P ON P.user_id = PCR.parent_id WHERE P.username = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);

            // Execute the query
            ResultSet resultSet = statement.executeQuery();

            // Display children's usernames on labels
            int childCount = 0;
            while (resultSet.next() && childCount < 3) {
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

    @FXML
    private void handleViewEventsAction(ActionEvent event) {
        // Your implementation to handle view events action
    }

    @FXML
    private void handleBookingAction(ActionEvent event) {
        // Your implementation to handle booking action
    }

    // Add any additional methods or event handlers as needed
}
