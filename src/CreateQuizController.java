
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateQuizController {

    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hackingthefuture";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "jianwen0414";

    // JDBC variables for opening, closing, and managing connection
    private Connection connection;
    private PreparedStatement preparedStatement;
    private static ResultSet resultSet;

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
    private void initialize() {
        // Initialize choice box options
        themeChoiceBox.getItems().addAll("Science", "Technology", "Engineering", "Mathematics");

        // Connect to the database
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle connection error
        }
    }

    @FXML
    private void handleCreateQuizButtonAction() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String quizLink = quizLinkField.getText();
        String themeName = themeChoiceBox.getValue(); // Get selected theme name

        try {
            // Retrieve theme ID based on the selected theme name
            int themeId = getThemeId(themeName);

            // SQL query to insert quiz details into the Quiz table
            String sql = "INSERT INTO Quiz (quiz_title, quiz_description, theme_id, quiz_content) VALUES (?, ?, ?, ?)";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, themeId); // Insert theme ID
            preparedStatement.setString(4, quizLink);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Quiz creation successful
                System.out.println("Quiz created successfully!");
            } else {
                // Quiz creation failed
                System.out.println("Failed to create quiz!");
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

    private int getThemeId(String themeName) throws SQLException {
        // SQL query to retrieve theme ID based on theme name
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

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Quiz");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
