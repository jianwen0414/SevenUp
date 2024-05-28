package hackthefuture;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MakeBookingsController implements Initializable {
    //hardcode
    //Parents p = new Parents(3, "parent@gmail.com", "parent", "123", 1, 9.0700000, 11.0400000);
    //Student s = new Student(4, "child@gmail.com", "child1", "123", 2, 9.0700000, 11.0400000, 0, new aParents[]{p});
    //Student s1 = new Student(7, "child@gmail.com", "child2", "123", 2, 9.0700000, 11.0400000, 0, new asg.Parent[]{p});
    
    private Parents currentUser;
    @FXML
    private Button Book;

    @FXML
    private ChoiceBox<String> Date;
    
    @FXML
    private ListView<String> Destination;
    
    @FXML
    private ChoiceBox<String> Child;
    private boolean childSelected = false;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        Destination.getItems().addAll(currentUser.bookDestination());
//      
//       
//      
//        Child.getItems().addAll(currentUser.getChildrenName());
//      
//        Date.setDisable(true);
//        
//        Child.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue != null) {
//                childSelected = true;
//                Date.setDisable(false);
//                Date.getItems().clear();
//                Student selectedChild = currentUser.getChildByName(newValue);
//                if (selectedChild != null) {
//                    Date.getItems().addAll(currentUser.bookDate(selectedChild));
//                }
//            }
//        });
//        
        
    }
    public void refresh(){
                Destination.getItems().addAll(currentUser.bookDestination());
      
       
      
        Child.getItems().addAll(currentUser.getChildrenName());
      
        Date.setDisable(true);
        
        Child.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                childSelected = true;
                Date.setDisable(false);
                Date.getItems().clear();
                Student selectedChild = currentUser.getChildByName(newValue);
                if (selectedChild != null) {
                    Date.getItems().addAll(currentUser.bookDate(selectedChild));
                }
            }
        });
        
    }
    
    @FXML
    private void handleBooking() {
    String selectedChildName = Child.getValue();
    String selectedDestination = Destination.getSelectionModel().getSelectedItem();
    LocalDate selectedDate = LocalDate.parse(Date.getValue());

    if (selectedChildName != null && selectedDestination != null && selectedDate != null) {
        try (Connection connection = DatabaseConnector.getConnection()) {
            String insertQuery = "INSERT INTO UserBookingDestination (booking_parent_id, student_id, destination_name, booking_date) " +
                                 "VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                pstmt.setInt(1, currentUser.getUserId()); // Set booking_parent_id
                pstmt.setInt(2, currentUser.getChildByName(selectedChildName).getId()); // Set student_id
                pstmt.setString(3, selectedDestination);
                pstmt.setDate(4, java.sql.Date.valueOf(selectedDate));
                pstmt.executeUpdate();
                showSuccessAlert("Booking Successful", "Your booking has been successfully made.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showFailAlert("Booking Failed", "Failed to make the booking. Please try again later.");
        }
    } else {
        showFailAlert("Booking Failed", "Please select a child, a destination, and a date to make a booking.");
    }
}

    
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showFailAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public void setup(Parents currentUser){
        this.currentUser=currentUser;
        refresh();
    }
}

    
   
