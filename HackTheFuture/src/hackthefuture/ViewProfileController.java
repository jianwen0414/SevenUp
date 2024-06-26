package hackthefuture;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ViewProfileController implements Initializable {

    User currentUser;

    @FXML
    private ListView<String> StudentProfile;

    @FXML
    private ListView<String> educatorProfile;

    @FXML
    private ListView<String> parentsProfile;

    @FXML
    private Button backButton;

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        StudentProfile.setOnMouseClicked(this::handleStudentProfileClick);
        educatorProfile.setOnMouseClicked(this::handleEducatorProfileClick);
        parentsProfile.setOnMouseClicked(this::handleParentProfileClick);

    }

    private void handleStudentProfileClick(MouseEvent event) {
        String selectedUsername = StudentProfile.getSelectionModel().getSelectedItem();
        if (selectedUsername != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("viewOtherStudentProfile.fxml"));
                javafx.scene.Parent root = loader.load();

                viewOtherStudentProfileController controller = loader.getController();

                controller.initData(selectedUsername, currentUser.userId);

                Stage profileStage = new Stage();
                profileStage.setScene(new Scene(root));
                profileStage.setTitle(selectedUsername);
                profileStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void handleEducatorProfileClick(MouseEvent event) {
        String selectedUsername = educatorProfile.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("viewOtherEducatorProfile.fxml"));
                javafx.scene.Parent root = loader.load();

                viewOtherEducatorProfileController controller = loader.getController();

                controller.setup(selectedUsername);

                Stage profileStage = new Stage();
                profileStage.setScene(new Scene(root));
                profileStage.setTitle(selectedUsername);
                profileStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void handleParentProfileClick(MouseEvent event) {
        String selectedUsername = parentsProfile.getSelectionModel().getSelectedItem();

        if (selectedUsername != null) {
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("viewOtherParentProfile.fxml"));
                javafx.scene.Parent root = loader.load();

                viewOtherParentProfileController controller = loader.getController();

                controller.setup(selectedUsername);

                Stage profileStage = new Stage();
                profileStage.setScene(new Scene(root));
                profileStage.setTitle(selectedUsername);
                profileStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void setup(User currentUser) {
        this.currentUser = currentUser;
        StudentProfile.getItems().addAll(currentUser.viewProfileList(3));
        educatorProfile.getItems().addAll(currentUser.viewProfileList(1));
        parentsProfile.getItems().addAll(currentUser.viewProfileList(2));
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
