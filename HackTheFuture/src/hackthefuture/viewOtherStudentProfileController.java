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
import java.util.List;
import javafx.event.ActionEvent;
import org.graphstream.graph.Graph;

public class viewOtherStudentProfileController implements Initializable {

    private int currentUserId;
    private int selectedUserId;
    @FXML
    private Label username;

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

    @FXML
    private Button viewRelationshipButton;

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
                        double locationX = resultSetUser.getDouble("location_coordinate_x");
                    double locationY = resultSetUser.getDouble("location_coordinate_y");
                    getLocationX.setText(locationX + ", " + locationY);
//                        getLocationX.setText(Double.toString(resultSetUser.getDouble("location_coordinate_x")));
//                        getLocationY.setText(Double.toString(resultSetUser.getDouble("location_coordinate_y")));
                        getPoint.setText(Integer.toString(resultSetUser.getInt("current_points")));

                        selectedUserId = resultSetUser.getInt("user_id");

                        String parentQuery = "SELECT p.username AS parent_name "
                                + "FROM ParentChildRelationship pc "
                                + "JOIN User p ON pc.parent_id = p.user_id "
                                + "WHERE pc.child_id = ?";
                        try (PreparedStatement pstmtParent = connection.prepareStatement(parentQuery)) {
                            pstmtParent.setInt(1, selectedUserId);
                            try (ResultSet resultSetParent = pstmtParent.executeQuery()) {
                                if (resultSetParent.next()) {
                                    getParent1.setText(resultSetParent.getString("parent_name"));
                                    if (resultSetParent.next()) {
                                        getParent2.setText(resultSetParent.getString("parent_name"));
                                    } else {
                                        getParent2.setText(" ");
                                    }
                                } else {
                                    getParent1.setText("null");
                                    getParent2.setText("null");
                                }
                            }
                        }

                        getFriends();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getFriends() {
        System.out.println("Fetching friends for user ID: " + selectedUserId); // Debug statement
        String query = "SELECT DISTINCT u.username FROM UserFriend uf "
                + "JOIN User u ON (u.user_id = uf.user1_id OR u.user_id = uf.user2_id) "
                + "WHERE (uf.user1_id = ? OR uf.user2_id = ?) "
                + "AND u.user_id <> ?";

        try (Connection connection = DatabaseConnector.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {

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

    @FXML
    private void handleViewRelationshipGraphAction(ActionEvent event) {
        String selectedUsername = username.getText();
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
