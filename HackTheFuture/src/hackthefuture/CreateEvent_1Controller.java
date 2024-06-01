/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package hackthefuture;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.time.LocalTime;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Tan Shi Han
 */
public class CreateEvent_1Controller implements Initializable {

    @FXML
    private DatePicker eventDate;

    @FXML
    private TextField eventDescribe;

    @FXML
    private TextField eventTitle;

    @FXML
    private TextField eventVenue;

    @FXML
    private Button createEvent;

    @FXML
    private ComboBox<String> timeHour;

    @FXML
    private ComboBox<String> timeMinute;

    @FXML
    private Button backButton;

    private Stage primaryStage;

    private Educator currentUser;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        eventDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        eventDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            populateTimeComboBoxes(newValue);
        });
    }

    private void populateTimeComboBoxes(LocalDate selectedDate) {
        // Clear the existing items in the ComboBox
        timeHour.getItems().clear();

        if (selectedDate != null) {
            // Determine if the selected date is today
            boolean isToday = selectedDate.isEqual(LocalDate.now());

            // Populate ComboBox for hour based on the selected date
            int startHour = isToday ? LocalTime.now().getHour() : 0;
            int endHour = isToday ? 23 : 23;
            for (int i = startHour; i <= endHour; i++) {
                timeHour.getItems().add(String.format("%02d", i));
            }

            timeHour.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                timeMinute.getItems().clear(); // Clear existing items

                if (newValue != null) {
                    int startMinute = 0;
                    if (isToday && Integer.parseInt(newValue) == LocalTime.now().getHour()) {
                        // If it's today and the selected hour is the current hour, start from the current minute
                        startMinute = LocalTime.now().getMinute();
                    }
                    // Populate ComboBox for minute with the appropriate range
                    for (int i = startMinute; i < 60; i++) {
                        timeMinute.getItems().add(String.format("%02d", i));
                    }
                }
            });

            // Populate ComboBox for minute with all minutes (00 to 59) initially
            for (int i = 0; i < 60; i++) {
                timeMinute.getItems().add(String.format("%02d", i));
            }
        }
    }

    public void setup(Educator currentUser) {
        this.currentUser = currentUser;
    }

    @FXML
    private void handleCreateEventButtonClick(ActionEvent eh) {
        String title = eventTitle.getText();
        String venue = eventVenue.getText();
        String description = eventDescribe.getText();
        String date = eventDate.getValue().toString();
        String hour = timeHour.getValue();
        String minute = timeMinute.getValue();
        Event event = new Event(title, description, venue, LocalDate.parse(date), LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute)));
//        EventDao.saveEvent(title, venue, description, date, hour, minute);
        EventDao.saveEvent(event, currentUser);
        currentUser.createEvent(event);
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
