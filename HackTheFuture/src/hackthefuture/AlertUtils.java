/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import javafx.scene.control.Alert;

/**
 *
 * @author Tan Shi Han
 */
public class AlertUtils {
    public static void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showRegistrationWarningAlert() {
        showAlert(Alert.AlertType.WARNING, "Warning Alert", "Please fill in all required fields before continuing.");
    }

    public static void showInvalidEmailAlert() {
        showAlert(Alert.AlertType.WARNING, "Warning Alert", "Please enter a valid email address.");
    }

    public static void showPasswordMismatchAlert() {
        showAlert(Alert.AlertType.WARNING, "Warning Alert", "Your passwords don't match. Please try again.");
    }
    
    public static void showEmailNotUnique() {
        showAlert(Alert.AlertType.WARNING, "Warning Alert", "Email already in use. Please choose another one.");
    }
    public static void showUsernameNotUnique() {
        showAlert(Alert.AlertType.WARNING, "Warning Alert", "Username already in use. Please choose another one.");
    }
    public static void showRegistrationSuccessAlert() {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful! Welcome aboard.");
    }

    public static void showRegistrationFailedAlert() {
        showAlert(Alert.AlertType.ERROR, "Registration Failed", "Registration failed. Please try again later.");
    }
    
    public static void showSuccessCreateEvent() {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Event created successfully! ");
    }
    
    public static void showFailCreateEvent() {
        showAlert(Alert.AlertType.INFORMATION, "Failed", "Failed to create event.");
    }
    public static void showAccountNotFound() {
        showAlert(Alert.AlertType.ERROR, "Failed", "You dont have an account yet.");
    }
    public static void showLoginError() {
        showAlert(Alert.AlertType.ERROR, "Failed", "Invalid username or password. Please try again.");
    }
}
