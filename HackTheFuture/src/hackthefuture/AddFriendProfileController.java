package hackthefuture;

import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.util.ArrayList;

public class AddFriendProfileController implements Initializable{
    private int currentUserId; 
    private int selectedUserId;
    @FXML
    private Label username;
    
    @FXML
    private Button addFriend;

    @FXML
    private ListView<String> friendList;

    @FXML
    private Label getEmail;

    @FXML
    private Label getLocationX;

    @FXML
    private Label getLocationY;

    @FXML
    private Label getParent1;

    @FXML
    private Label getParent2;

    @FXML
    private Label getPoint;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

     public void initData(String selectedUsername, int currentUserId) {
        this.currentUserId = currentUserId;
        username.setText(selectedUsername);
        try (Connection connection = DatabaseConnector.getConnection()) {
            String userQuery = "SELECT * FROM User WHERE username = ?";
            try (PreparedStatement pstmtUser = connection.prepareStatement(userQuery)) {
                pstmtUser.setString(1, selectedUsername);
                try (ResultSet resultSetUser = pstmtUser.executeQuery()) {
                    if (resultSetUser.next()) {
                        getEmail.setText(resultSetUser.getString("email"));
                        getLocationX.setText(Double.toString(resultSetUser.getDouble("location_coordinate_x")));
                        getLocationY.setText(Double.toString(resultSetUser.getDouble("location_coordinate_y")));
                        getPoint.setText(Integer.toString(resultSetUser.getInt("current_points")));

                        selectedUserId = resultSetUser.getInt("user_id");

                        String parentQuery = "SELECT p.username AS parent_name " +
                                             "FROM ParentChildRelationship pc " +
                                             "JOIN User p ON pc.parent_id = p.user_id " +
                                             "WHERE pc.child_id = ?";
                        try (PreparedStatement pstmtParent = connection.prepareStatement(parentQuery)) {
                            pstmtParent.setInt(1, selectedUserId);
                            try (ResultSet resultSetParent = pstmtParent.executeQuery()) {
                                if (resultSetParent.next()) {
                                    getParent1.setText(resultSetParent.getString("parent_name"));
                                    if (resultSetParent.next()) {
                                        getParent2.setText(resultSetParent.getString("parent_name"));
                                    } else {
                                        getParent2.setText("null");
                                    }
                                } else {
                                    getParent1.setText("null");
                                    getParent2.setText("null");
                                }
                            }
                        }

                        updateFriendRequestButton();
                        getFriends();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendFriendRequest() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String insertQuery = "INSERT INTO FriendRequest (sender_id, receiver_id, request_status) VALUES (?, ?, 'Pending')";
            try (PreparedStatement pstmtInsert = connection.prepareStatement(insertQuery)) {
                pstmtInsert.setInt(1, currentUserId);
                pstmtInsert.setInt(2, selectedUserId);
                pstmtInsert.executeUpdate();
            }
            addFriend.setText("Request Sent");
            addFriend.setDisable(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateFriendRequestButton() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String checkQuery = "SELECT request_status FROM FriendRequest WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
            try (PreparedStatement pstmtCheck = connection.prepareStatement(checkQuery)) {
                pstmtCheck.setInt(1, currentUserId);
                pstmtCheck.setInt(2, selectedUserId);
                pstmtCheck.setInt(3, selectedUserId);
                pstmtCheck.setInt(4, currentUserId);
                try (ResultSet resultSet = pstmtCheck.executeQuery()) {
                    if (resultSet.next()) {
                        String status = resultSet.getString("request_status");
                        switch (status) {
                            case "Accepted":
                                addFriend.setText("Added");
                                addFriend.setDisable(true);
                                break;
                            case "Rejected":
                                addFriend.setText("Rejected");
                                addFriend.setDisable(true);
                                break;
                            case "Pending":
                                addFriend.setText("Requested");
                                addFriend.setDisable(true);
                                break;
                        }
                    } else {
                        addFriend.setText("Add Friend");
                        addFriend.setDisable(false);
                        addFriend.setOnAction(e -> sendFriendRequest());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void getFriends() {
        System.out.println("Fetching friends for user ID: " + selectedUserId); // Debug statement
        String query = "SELECT DISTINCT u.username FROM UserFriend uf " +
                       "JOIN User u ON (u.user_id = uf.user1_id OR u.user_id = uf.user2_id) " +
                       "WHERE (uf.user1_id = ? OR uf.user2_id = ?) " +
                       "AND u.user_id <> ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, selectedUserId);
            statement.setInt(2, selectedUserId);
            statement.setInt(3, selectedUserId);

            try (ResultSet resultSet = statement.executeQuery()) {
                friendList.getItems().clear(); // Clear the list before adding new items
                while (resultSet.next()) {
                    String friendUsername = resultSet.getString("username");
                    System.out.println("Found friend: " + friendUsername); // Debug statement
                    friendList.getItems().add(friendUsername);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
