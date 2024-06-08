/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Tan Shi Han
 */
public class RegisteredEventsController implements Initializable {

    private Stage primaryStage;
    private User currentUser;
    @FXML
    private VBox UpcomingEvents;
    
    @FXML
    private Button backButton;
    
    @FXML
    private VBox UpcomingEventsOnly;
    
    @FXML
    private CheckBox showUpcoming;
    
    @FXML
    private ScrollPane scroll1;

    @FXML
    private ScrollPane scroll2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setup(User currentUser) {
        this.currentUser = currentUser;
        refresh();
    }

    private void refresh() {
        UpcomingEvents.getChildren().clear();
        for (Event event : EventDao.getRegisteredEvent(currentUser)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisteredEventsCard.fxml"));
                
                Node root = loader.load();
                RegisteredEventsCardController controller = loader.getController();
//                eventCardController.setStudentProfileController(studentProfileController);
                controller.setup(event, currentUser);
                UpcomingEvents.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
        
        UpcomingEventsOnly.getChildren().clear();
        for (Event event : EventDao.getUpcomingEvents(currentUser)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisteredEventsCard.fxml"));
                
                Node root = loader.load();
                RegisteredEventsCardController controller = loader.getController();
//                eventCardController.setStudentProfileController(studentProfileController);
                controller.setup(event, currentUser);
                UpcomingEventsOnly.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
    }
    
    
    @FXML
    void showUpcomingOnly(ActionEvent event) {
      if(showUpcoming.isSelected()){
          scroll2.setVisible(true);
                    scroll1.setVisible(false);

      }else{
                    scroll2.setVisible(false);
                                        scroll1.setVisible(true);


      }
    }
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (!NavigationHistory.isEmpty()) {
            Scene previousScene = NavigationHistory.pop();
            primaryStage.setScene(previousScene);
            primaryStage.show();
        }
    }
}
