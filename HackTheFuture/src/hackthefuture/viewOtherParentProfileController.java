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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class viewOtherParentProfileController {
    @FXML
    private ListView<String> childList;

    @FXML
    private Label emailLabel;

    @FXML
    private Label locationLabel;

    @FXML
    private ComboBox<String> monthComboBox;

    @FXML
    private Label username;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button viewPastBookingsButton;
    
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
                        locationLabel.setText(userRs.getBigDecimal("location_coordinate_x") + ", " + userRs.getBigDecimal("location_coordinate_y"));
                    }
                }
            }
            
            //fetch children list
            ObservableList<String> childrenList = FXCollections.observableArrayList();
            String childQuery = "SELECT u.username " +
                         "FROM ParentChildRelationship p " +
                         "JOIN User u ON p.child_id = u.user_id " +
                         "JOIN User parent ON p.parent_id = parent.user_id " +
                         "WHERE parent.username = ?";
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getUserNameById(int userId) {
        String userName = "";
        //String query = "SELECT username FROM User WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM User WHERE user_id = ?")) {
            
            stmt.setInt(1, userId);
            ResultSet resultSet = stmt.executeQuery();
            
            if (resultSet.next()) {
                userName = resultSet.getString("username");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return userName;
    }
}

