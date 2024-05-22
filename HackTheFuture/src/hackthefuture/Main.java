package hackthefuture;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author Jian Wen Lee
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Login Page");
        primaryStage.setScene(new Scene(root, 1200, 800));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /*
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file for the Create Quiz page
        Parent root = FXMLLoader.load(getClass().getResource("CreateQuiz.fxml"));
        
        // Set the scene with the loaded FXML
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Create Quiz");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
*/
}

