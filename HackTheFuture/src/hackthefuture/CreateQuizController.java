package hackthefuture;

import java.net.URL;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CreateQuizController implements Initializable {

    // JDBC variables for opening, closing, and managing connection
    private Connection connection;
    private PreparedStatement preparedStatement;
    private static ResultSet resultSet;

    // Store the current user's ID
    private int currentUserId;

    // FXML injected UI elements
    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField quizLinkField;

    @FXML
    private ChoiceBox<String> themeChoiceBox;

    @FXML
    private Button createQuizButton;

    @FXML
    private Button backButton;

    // Reference to the current user (Educator)
    private Educator currentUser;

    // Reference to the primary stage
    private Stage primaryStage;

    // Reference to the home page controller
    private EducatorHomePageController homePageController;

    // Method to set the primary stage
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Method to set up the controller with the current user
    public void setup(Educator currentUser) {
        this.currentUser = currentUser;
    }

    // Method to connect to the database
    private void connectToDatabase() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error
        }
    }

    // Handle the action of creating a quiz
    @FXML
    private void handleCreateQuizButtonAction() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String quizLink = quizLinkField.getText();
        String themeName = themeChoiceBox.getValue(); // Get selected theme name

        // Validate the quiz link URL
        if (!isValidURL(quizLink)) {
            AlertUtils.showNotValidLink();
            return;
        }

        try {
            // Retrieve theme ID based on the selected theme name
            int themeId = getThemeId(themeName);

            // Create an instance of the Quiz class
            Quiz quiz = new Quiz(title, description, themeId, quizLink);
            currentUser.createQuiz(quiz);

            // Increment quiz count on the home page if the controller is set
            if (homePageController != null) {
                homePageController.incrementQuizCount();
            }

            // SQL query to insert quiz details into the Quiz table
            String sql = "INSERT INTO Quiz (quiz_title, quiz_description, theme_id, quiz_content) VALUES (?, ?, ?, ?)";

            // Prepare the statement with RETURN_GENERATED_KEYS
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, quiz.getTitle());
            preparedStatement.setString(2, quiz.getDescription());
            preparedStatement.setInt(3, quiz.getThemeId());
            preparedStatement.setString(4, quiz.getContent());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated quiz_id
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int quizId = generatedKeys.getInt(1);
                    quiz.setQuizId(quizId);
                    // Insert a record into the QuizCreationRecord table
                    insertQuizCreationRecord(currentUser.getUserId(), quizId);
                }
                // Quiz creation successful
                showAlert("Quiz created successfully!");
            } else {
                // Quiz creation failed
                showAlert("Failed to create quiz!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL error
        } finally {
            // Close JDBC objects in finally block
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Check if a user exists in the database
    private boolean doesUserExist(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM User WHERE user_id = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, userId);
        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        } else {
            return false;
        }
    }

    // Retrieve theme ID based on the theme name
    private int getThemeId(String themeName) throws SQLException {
        String sql = "SELECT theme_id FROM QuizTheme WHERE theme_name = ?";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, themeName);
        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("theme_id");
        } else {
            // Theme not found, handle accordingly (e.g., throw exception)
            throw new SQLException("Theme not found for theme name: " + themeName);
        }
    }

    // Insert a record into the QuizCreationRecord table
    private void insertQuizCreationRecord(int creatorId, int quizId) throws SQLException {
        String sql = "INSERT INTO QuizCreationRecord (creator_id, quiz_id) VALUES (?, ?)";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, creatorId);
        preparedStatement.setInt(2, quizId);
        preparedStatement.executeUpdate();
    }

    // Show an alert with the specified message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Quiz");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Initialize method called to initialize the controller after its root element has been processed
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        themeChoiceBox.getItems().addAll("Science", "Technology", "Engineering", "Mathematics");
        connectToDatabase();
    }

    // Handle the action of the back button
    @FXML
    private void handleBackButtonAction(ActionEvent event) {
        if (!NavigationHistory.isEmpty()) {
            Scene previousScene = NavigationHistory.pop();
            primaryStage.setScene(previousScene);
            primaryStage.show();
        }
    }

    // Set the home page controller
    public void setHomePageController(EducatorHomePageController homePageController) {
        this.homePageController = homePageController;
    }

    // Validate the URL format
    private boolean isValidURL(String url) {
        String regex = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }
}
