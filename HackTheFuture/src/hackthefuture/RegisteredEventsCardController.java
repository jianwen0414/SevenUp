/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.sql.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Tan Shi Han
 * 
 */
public class RegisteredEventsCardController implements Initializable {
    User currentUser;
    
    Event event;
    @FXML
    private Label eventDate;

    @FXML
    private Label eventDes;

    @FXML
    private Label eventName;

    @FXML
    private Label eventTime;

    @FXML
    private Label eventVenue;
    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    
    
    public void setup(Event event, User currentUser) {
        this.event = event;
        this.currentUser = currentUser;
        refresh();
    }

    private void refresh() {
        eventName.setText(event.getTitle());
        eventDes.setText(event.getDescription());
        eventDate.setText(event.getDate().toString());
        eventTime.setText(event.getTime().toString());
        System.out.println(eventDate);
        eventVenue.setText(event.getVenue());
    }
}

    