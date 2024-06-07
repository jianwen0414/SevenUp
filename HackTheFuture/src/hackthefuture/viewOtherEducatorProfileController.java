package hackthefuture;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class viewOtherEducatorProfileController {
    @FXML
    private Label emailLabel;

    @FXML
    private Label eventCountLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private Label quizCountLabel;

//    @FXML
//    private Label username;

    @FXML
    private Label usernameLabel;
    
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
                        //username.setText(userRs.getString("username"));
                        double locationX=userRs.getDouble("location_coordinate_x");
                        double locationY=userRs.getDouble("location_coordinate_y");
                        locationLabel.setText(locationX+", "+locationY);
//                        locationLabel.setText(userRs.getBigDecimal("location_coordinate_x") + ", " + userRs.getBigDecimal("location_coordinate_y"));
                    }
                }
            }

            // Fetch quiz count
            String quizCountQuery = "SELECT COUNT(*) AS quiz_count FROM QuizCreationRecord WHERE creator_id = (SELECT user_id FROM User WHERE username = ?)";
            try (PreparedStatement quizStmt = connection.prepareStatement(quizCountQuery)) {
                quizStmt.setString(1, selectedUsername);
                try (ResultSet quizRs = quizStmt.executeQuery()) {
                    if (quizRs.next()) {
                        quizCountLabel.setText(String.valueOf(quizRs.getInt("quiz_count")));
                    }
                }
            }

            // Fetch event count
            String eventCountQuery = "SELECT COUNT(*) AS event_count FROM EventCreationRecord WHERE creator_id = (SELECT user_id FROM User WHERE username = ?)";
            try (PreparedStatement eventStmt = connection.prepareStatement(eventCountQuery)) {
                eventStmt.setString(1, selectedUsername);
                try (ResultSet eventRs = eventStmt.executeQuery()) {
                    if (eventRs.next()) {
                        eventCountLabel.setText(String.valueOf(eventRs.getInt("event_count")));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    

}
