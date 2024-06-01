package hackthefuture;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javafx.scene.control.ComboBox;
import java.net.URISyntaxException;
import java.util.HashSet;
import javafx.application.Platform;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author minzi
 */
public class AttemptQuizController implements Initializable {

    @FXML
    private Button complete;

    @FXML
    private ComboBox<String> quizChoose;

    @FXML
    private Button themeEng;

    @FXML
    private Button themeMaths;

    @FXML
    private Button themeSc;

    @FXML
    private Button themeTech;

    @FXML
    private Button backButton;

    private Stage primaryStage;

    private User currentUser;

    private Map<String, String> quizNamesToLinks = new HashMap<>();
    private Map<String, Set<String>> quizLinks = new HashMap<>();
    private Map<String, Boolean> quizCompletionStatus = new HashMap<>();

    private int userID;
    private String lastSelectedQuizName = null;
    private String lastSelectedQuizLink = null;
    private boolean quizCompleted = false;

//    public void setCurrentUserID(int userid){
//        this.userID = userid;
//        initializeQuizLinksFromDatabase();
//    }
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setup(User currentUser) {
        this.currentUser = currentUser;
        System.out.println("current user id:" + currentUser.getUserId());
        initializeQuizLinksFromDatabase();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        themeSc.setOnMouseClicked(event -> {
            toggleThemeSelection(themeSc);
        });

        themeTech.setOnMouseClicked(event -> {
            toggleThemeSelection(themeTech);
        });

        themeEng.setOnMouseClicked(event -> {
            toggleThemeSelection(themeEng);
        });

        themeMaths.setOnMouseClicked(event -> {
            toggleThemeSelection(themeMaths);
        });

        //default setting: user enter the quiz page and all theme is selected
        themeSc.setStyle("-fx-background-color: #708090");
        themeTech.setStyle("-fx-background-color: #708090");
        themeEng.setStyle("-fx-background-color: #708090");
        themeMaths.setStyle("-fx-background-color: #708090");

        updateQuizSelection(null);
        // Hide the complete button initially
        complete.setVisible(false);

        quizChoose.setOnAction(e -> {
            lastSelectedQuizName = quizChoose.getSelectionModel().getSelectedItem();
            if (lastSelectedQuizName != null && !lastSelectedQuizName.isEmpty()) {
                lastSelectedQuizLink = quizNamesToLinks.get(lastSelectedQuizName);
                if (lastSelectedQuizLink != null && !lastSelectedQuizLink.isEmpty() && !quizCompletionStatus.containsKey(lastSelectedQuizName)) {
                    try {
                        Desktop.getDesktop().browse(new URI(lastSelectedQuizLink));
                        complete.setVisible(true); // Show the complete button
                    } catch (IOException | URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    complete.setVisible(false); // Hide the complete button if the quiz is completed or no quiz link is available
                }
            }
            quizCompleted = false;
        });

        complete.setOnAction(e -> {
            complete.setVisible(false); // Hide the complete button
            updateStudentPoints(currentUser.getUserId(), 2);
            String selectedQuizName = lastSelectedQuizName;
            String selectedQuizLink = lastSelectedQuizLink;
            if (selectedQuizName != null && !selectedQuizName.isEmpty()) {
                if (!quizCompletionStatus.containsKey(selectedQuizName)) {
                    storeQuizCompletionInDatabase(selectedQuizName); // Store the completion status in the database
                    quizCompletionStatus.put(selectedQuizName, true);
                } else {
                    System.out.println("Quiz already completed: " + selectedQuizName);
                }
                updateQuizSelection(selectedQuizName);
                lastSelectedQuizName = null;
                lastSelectedQuizLink = null;
                quizCompleted = true;
            } else {
                System.out.println("No quiz selected");
            }
        });
    }

    private void initializeQuizLinksFromDatabase() {
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT q.quiz_id, q.quiz_title, q.quiz_content, q.theme_id "
                + "FROM Quiz q "
                + "WHERE q.quiz_id NOT IN (SELECT uqc.quiz_id "
                + "                         FROM UserQuizCompletion uqc "
                + "                         WHERE uqc.user_id = ?)"
        )) {

            stmt.setInt(1, currentUser.getUserId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int quizId = rs.getInt("quiz_id");
                String quizTitle = rs.getString("quiz_title");
                String quizContent = rs.getString("quiz_content");
                int themeId = rs.getInt("theme_id");
                quizNamesToLinks.put(quizTitle, quizContent);
                String theme = getThemeNameById(themeId);
                quizLinks.computeIfAbsent(theme, k -> new HashSet<>()).add(quizTitle);
            }
            for (String theme : new String[]{"Science", "Mathematics", "Engineering", "Technology"}) {
                if (!quizLinks.containsKey(theme) || quizLinks.get(theme).isEmpty()) {
                    Button themeButton = getThemeButton(theme);
                    if (themeButton != null) {
                        themeButton.setDisable(true);
                    }
                }
            }
            for (Map.Entry<String, Set<String>> entry : quizLinks.entrySet()) {
                String theme = entry.getKey();
                Set<String> links = entry.getValue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        updateQuizSelection(null);
    }

    private String getThemeNameById(int themeId) {
        switch (themeId) {
            case 1:
                return "Science";
            case 2:
                return "Mathematics";
            case 3:
                return "Engineering";
            case 4:
                return "Technology";
            default:
                return null;
        }
    }

    private void toggleThemeSelection(Button button) {
        if (button.getStyle().contains("-fx-background-color: #708090")) {
            button.setStyle("-fx-background-color: #d4d5cf");
        } else {
            button.setStyle("-fx-background-color: #708090");
        }
        updateQuizSelection(null);
    }

    private void updateQuizSelection(String selectedLink) {
        ObservableList<String> items = FXCollections.observableArrayList();
        boolean themeSelected = false;

        for (Map.Entry<String, Set<String>> entry : quizLinks.entrySet()) {
            String theme = entry.getKey();
            Set<String> links = entry.getValue();
            Button themeButton = getThemeButton(theme);

            if (themeButton.getStyle().contains("-fx-background-color: #708090")) {
                items.addAll(links);
                themeSelected = true;

                boolean allLinksClicked = links.stream().allMatch(quizCompletionStatus::containsKey);
                if (allLinksClicked) {
                    themeButton.setDisable(true);
                    items.removeAll(links);
                }
            }
        }
        items.removeIf(quizCompletionStatus::containsKey);
        quizChoose.setItems(items);
        complete.setVisible(false);

        if (!items.isEmpty() && selectedLink != null && items.contains(selectedLink)) {
            quizChoose.getSelectionModel().select(selectedLink);
        } else {
            Platform.runLater(() -> quizChoose.getSelectionModel().clearSelection());
        }

        // Remove completed quiz from combo box
        if (selectedLink != null && items.contains(selectedLink)) {
            items.remove(selectedLink);
            quizChoose.setItems(items);
            quizCompletionStatus.put(selectedLink, true);

        }
        System.out.println("Updated quiz selection.");
    }

    private Button getThemeButton(String theme) {
        switch (theme) {
            case "Engineering":
                return themeEng;
            case "Mathematics":
                return themeMaths;
            case "Science":
                return themeSc;
            case "Technology":
                return themeTech;
            default:
                return null;
        }
    }

    private void updateStudentPoints(int userID, int points) {
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement("UPDATE User SET current_points = current_points + ? WHERE user_id = ?")) {

            stmt.setInt(1, points);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void storeQuizCompletionInDatabase(String quizTitle) {
        try (Connection conn = DatabaseConnector.getConnection(); PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO UserQuizCompletion (user_id, quiz_id, completion_date) "
                + "SELECT ?, quiz_id, ? FROM Quiz WHERE quiz_title = ?"
        )) {

            stmt.setInt(1, currentUser.getUserId());
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setString(3, quizTitle);
            stmt.executeUpdate();
        } catch (SQLException e) {
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
