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
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author Tan Shi Han
 */
public class ViewEventController implements Initializable {


    @FXML
    private HBox todayEventsBox;

    @FXML
    private HBox upcomingEventsBox;

    @FXML
    private Button backButton;

    @FXML
    private Button Registered;

    private Stage primaryStage;

    private User currentUser;

    private EventCardController eventCardController;
    private StudentProfileController studentProfileController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setupControllers(StudentProfileController homePageController) {
        // Check whether is a student or other roles since will be used as students have the rights to register event
        if (currentUser instanceof Student) {
            studentProfileController = homePageController;
        }
        refresh();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setup(User currentUser) {
        this.currentUser = currentUser;
    }

    private void refresh() {
        // Set the View Registered Events button for students
        Registered.setVisible(currentUser.getRoleId() == 3);
        //Setup the Hbox to load all today(live) events into the Hbox
        todayEventsBox.getChildren().clear();
        for (Event event : EventDao.getTodayEvent()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EventCard.fxml"));
                Node root = loader.load();
                EventCardController eventCardController = loader.getController();
                eventCardController.setStudentProfileController(studentProfileController);
                eventCardController.setup(event, currentUser);
                todayEventsBox.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
        //Setup the Hbox to load 3 upcoming events into Hbox
        upcomingEventsBox.getChildren().clear();
        for (Event event : EventDao.getUpcomingEvent()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EventCard.fxml"));
                Node root = loader.load();
                EventCardController eventCardController = loader.getController();
                eventCardController.setStudentProfileController(studentProfileController);

                eventCardController.setup(event, currentUser);
                upcomingEventsBox.getChildren().add(root);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
    }


    @FXML
    void handleViewRegisteredEventsAction(ActionEvent event) { //If the student click onto the view registered events
        try {
            NavigationHistory.push(primaryStage.getScene());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisteredEvents.fxml"));
            Parent root = loader.load();
            RegisteredEventsController controller = loader.getController();
            controller.setup(currentUser);// Set the current user's ID
            controller.setPrimaryStage(primaryStage);  // Set the primary stage
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Registered Events");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
