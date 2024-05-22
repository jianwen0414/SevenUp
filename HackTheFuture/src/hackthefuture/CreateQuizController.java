package hackthefuture;

/*
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

    private int currentUserId; // Store the current user's ID

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

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
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
            // Check if currentUserId exists in the user table
            if (!doesUserExist(currentUserId)) {
                throw new SQLException("User ID " + currentUserId + " does not exist.");
            }

            // Retrieve theme ID based on the selected theme name
            int themeId = getThemeId(themeName);

            // SQL query to insert quiz details into the Quiz table
            String sql = "INSERT INTO Quiz (quiz_title, quiz_description, theme_id, quiz_content) VALUES (?, ?, ?, ?)";

            // Prepare the statement with RETURN_GENERATED_KEYS
            preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, description);
            preparedStatement.setInt(3, themeId); // Insert theme ID
            preparedStatement.setString(4, quizLink);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated quiz_id
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int quizId = generatedKeys.getInt(1);
                    // Insert a record into the QuizCreationRecord table
                    insertQuizCreationRecord(currentUserId, quizId);
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

    private void insertQuizCreationRecord(int creatorId, int quizId) throws SQLException {
        String sql = "INSERT INTO QuizCreationRecord (creator_id, quiz_id) VALUES (?, ?)";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, creatorId);
        preparedStatement.setInt(2, quizId);
        preparedStatement.executeUpdate();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Quiz");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
*/

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
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

    private int currentUserId; // Store the current user's ID

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

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
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
            // Check if currentUserId exists in the user table
            if (!doesUserExist(currentUserId)) {
                throw new SQLException("User ID " + currentUserId + " does not exist.");
            }

            // Retrieve theme ID based on the selected theme name
            int themeId = getThemeId(themeName);

            // Create an instance of the Quiz class
            Quiz quiz = new Quiz(0, title, description, themeId, quizLink);

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
                    insertQuizCreationRecord(currentUserId, quizId);
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

    private void insertQuizCreationRecord(int creatorId, int quizId) throws SQLException {
        String sql = "INSERT INTO QuizCreationRecord (creator_id, quiz_id) VALUES (?, ?)";
        preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, creatorId);
        preparedStatement.setInt(2, quizId);
        preparedStatement.executeUpdate();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Create Quiz");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

