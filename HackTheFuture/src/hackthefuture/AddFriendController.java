package hackthefuture;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.application.Application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AddFriendController implements Initializable {
//    //hardcode
//    asg.Parent p = new asg.Parent(3, "parent@gmail.com", "parent", "123", 1, 9.0700000, 11.0400000);
//    Student s = new Student(4, "child@gmail.com", "child1", "123", 2, 9.0700000, 11.0400000, 0, new asg.Parent[]{p});
//    Student s1 = new Student(7, "child@gmail.com", "child2", "123", 2, 9.0700000, 11.0400000, 0, new asg.Parent[]{p});
//    Student s2 = new Student(8, "child@gmail.com", "child3", "123", 2, 9.0700000, 11.0400000, 0, new asg.Parent[]{p});
//    Student s3 = new Student(9, "child@gmail.com", "child4", "123", 2, 9.0700000, 11.0400000, 0, new asg.Parent[]{p});
//    Student jett = new Student(10, "jett@gmail.com", "jettyeoh", "123", 2, 9.0700000, 11.0400000, 0, new asg.Parent[]{p});

    Student currentStudent;
    
    @FXML
    private ListView<String> FriendProfile;

    @FXML
    private ListView<String> FriendRequest;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        FriendProfile.setOnMouseClicked(this::handleFriendProfileClick);
        
        
        FriendRequest.setOnMouseClicked(this::handleFriendRequestClick);
//        
    }    
    
//    public void refresh(){
//        FriendProfile.getItems().addAll(currentStudent.viewStudentProfileList());
//        FriendProfile.setOnMouseClicked(this::handleFriendProfileClick);
//        
//        fetchFriendRequests();
//        FriendRequest.setOnMouseClicked(this::handleFriendRequestClick);
//        
//    }
    
    private void handleFriendProfileClick(MouseEvent event) {
        String selectedUsername = FriendProfile.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("AddFriendProfile.fxml"));
                Parent root = loader.load();

                AddFriendProfileController controller = loader.getController();

                controller.initData(selectedUsername,currentStudent.userId);

                Stage profileStage = new Stage();
                profileStage.setScene(new Scene(root));
                profileStage.setTitle(selectedUsername);
                profileStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void fetchFriendRequests() {
        try {
            Connection connection = DatabaseConnector.getConnection();

            String query = "SELECT sender_id FROM FriendRequest WHERE receiver_id = ? AND request_status = 'Pending'";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, currentStudent.getUserId());

            ResultSet resultSet = statement.executeQuery();

            FriendRequest.getItems().clear();

            while (resultSet.next()) {
                int senderId = resultSet.getInt("sender_id");
                String senderUsername = getUsernameById(senderId); 
                FriendRequest.getItems().add(senderUsername);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private String getUsernameById(int userId) {
        String username = null;
        try {
            Connection connection = DatabaseConnector.getConnection(); 

            String query = "SELECT username FROM User WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                username = resultSet.getString("username");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
    
    private void handleFriendRequestClick(MouseEvent event) {
        String selectedUsername = FriendRequest.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Friend Request");
            alert.setHeaderText("Accept or decline friend request from " + selectedUsername + "?");
            alert.setContentText("Choose your option.");

            ButtonType acceptButton = new ButtonType("Accept");
            ButtonType declineButton = new ButtonType("Decline");
            alert.getButtonTypes().setAll(acceptButton, declineButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == acceptButton) {
                    updateFriendRequestStatus(selectedUsername, "Accepted");

                    insertUserFriendRecord(selectedUsername);
                } else if (response == declineButton) {
                    updateFriendRequestStatus(selectedUsername, "Rejected");
                }
            });
        }
       
    }

    private void updateFriendRequestStatus(String senderUsername, String status) {
        try {
            Connection connection = DatabaseConnector.getConnection(); 

            String query = "UPDATE FriendRequest SET request_status = ? WHERE receiver_id = ? AND sender_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, status);
            statement.setInt(2, currentStudent.getUserId());
            statement.setInt(3, getUserIdByUsername(senderUsername)); // You need to implement this method
            statement.executeUpdate();

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        fetchFriendRequests();
    }

    private void insertUserFriendRecord(String senderUsername) {
        try {
            Connection connection = DatabaseConnector.getConnection(); 

            String query = "INSERT INTO UserFriend (user1_id, user2_id) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, currentStudent.getUserId());
            statement.setInt(2, getUserIdByUsername(senderUsername)); 

            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private int getUserIdByUsername(String username) {
        int userId = -1; 
        try {
            Connection connection = DatabaseConnector.getConnection(); 
            String query = "SELECT user_id FROM User WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                userId = resultSet.getInt("user_id");
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }
    
    public void setup(Student currentStudent){
        this.currentStudent = currentStudent;
        FriendProfile.getItems().addAll(currentStudent.viewStudentProfileList());
        fetchFriendRequests();
    }



}
