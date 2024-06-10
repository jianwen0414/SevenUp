/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.sql.*;
import javafx.stage.Stage;

/**
 *
 * @author Tan Shi Han
 */
public class UserRedirectionUtils {

    public static void redirectUserToHomepage(int userId, int roleId, String userEmail, String userUsername,
            String userPassword, double locationX, double locationY,
            int userPoints, Connection connection, Stage primaryStage) throws SQLException, IOException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            switch (roleId) {
                case 1: // Educator
                    //Create an instance of educator
                    Educator currentEducator = new Educator(userId, userEmail, userUsername, userPassword, roleId, locationX, locationY);
                    // Retrieve event created by educator
                    preparedStatement = connection.prepareStatement("SELECT * FROM EventCreationRecord WHERE creator_id = ?");
                    preparedStatement.setInt(1, currentEducator.getUserId());
                    resultSet = preparedStatement.executeQuery();
                    //Create an array list to add all event_id(s)
                    List<Integer> eventList = new ArrayList<>();
                    while (resultSet.next()) {
                        eventList.add(resultSet.getInt("event_id"));
                    }
                    for (int id : eventList) {
                        preparedStatement = connection.prepareStatement("SELECT * FROM Event WHERE event_id = ?");
                        preparedStatement.setInt(1, id);
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            int eventId = resultSet.getInt("event_id");
                            String eventTitle = resultSet.getString("event_title");
                            String eventDescription = resultSet.getString("event_description");
                            String eventVenue = resultSet.getString("event_venue");
                            LocalDate eventDate = resultSet.getDate("event_date").toLocalDate();
                            LocalTime eventTime = resultSet.getTime("event_time").toLocalTime();
                            Event event = new Event(eventId, eventTitle, eventDescription, eventVenue, eventDate, eventTime);
                            //save the event to event class in order to display number of events at homepage
                            currentEducator.createEvent(event);
                        }
                    }
                    //Retrieve quizzes created by educator
                    preparedStatement = connection.prepareStatement("SELECT * FROM QuizCreationRecord WHERE creator_id = ?");
                    preparedStatement.setInt(1, currentEducator.getUserId());
                    resultSet = preparedStatement.executeQuery();
                    //Create an array list to add all quiz_id(s)
                    List<Integer> quizList = new ArrayList<>();
                    while (resultSet.next()) {
                        quizList.add(resultSet.getInt("quiz_id"));
                    }
                    for (int id : quizList) {
                        preparedStatement = connection.prepareStatement("SELECT * FROM Quiz WHERE quiz_id = ?");
                        preparedStatement.setInt(1, id);
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            int quizId = resultSet.getInt("quiz_id");
                            String quizTitle = resultSet.getString("quiz_title");
                            String quizDescription = resultSet.getString("quiz_description");
                            String quizContent = resultSet.getString("quiz_content");
                            int quizTheme = resultSet.getInt("theme_id");
                            Quiz quiz = new Quiz(quizId, quizTitle, quizDescription, quizTheme, quizContent);
                            //save the quiz to quiz class in order to display number of quizzes at homepage
                            currentEducator.createQuiz(quiz);
                        }
                    }
                    // Redirect the educator to his/her homepage
                    FXMLLoader educatorLoader = new FXMLLoader(UserRedirectionUtils.class.getResource("EducatorHomePage.fxml"));
                    Parent educatorRoot = educatorLoader.load();
                    EducatorHomePageController educatorController = educatorLoader.getController();
                    educatorController.setup(currentEducator);
                    educatorController.setPrimaryStage(primaryStage);
                    primaryStage.setScene(new Scene(educatorRoot));
                    primaryStage.show();
                    break;

                case 2: // Parents
                    //Create an instance of parents
                    Parents currentParents = new Parents(userId, userEmail, userUsername, userPassword, roleId, locationX, locationY);
                    //Retrieve child of current parent
                    preparedStatement = connection.prepareStatement("SELECT * FROM ParentChildRelationship WHERE parent_id = ?");
                    preparedStatement.setInt(1, currentParents.getUserId());
                    resultSet = preparedStatement.executeQuery();
                    //Create an array list to add all child_id(s)
                    List<Integer> childList = new ArrayList<>();
                    while (resultSet.next()) {
                        childList.add(resultSet.getInt("child_id"));
                    }
                    for (int id : childList) {
                        preparedStatement = connection.prepareStatement("SELECT * FROM User WHERE user_id = ?");
                        preparedStatement.setInt(1, id);
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            int childId = resultSet.getInt("user_id");
                            String childEmail = resultSet.getString("email");
                            String childUsername = resultSet.getString("username");
                            String childPassword = resultSet.getString("password");
                            double childX = resultSet.getDouble("location_coordinate_x");
                            double childY = resultSet.getDouble("location_coordinate_y");
                            int childPoints = resultSet.getInt("current_points");
                            Student student = new Student(childId, childEmail, childUsername, childPassword, 3, childX, childY, childPoints);
                            // add the student to parent class so that can display child name in homepage
                            currentParents.addChild(student);
                        }
                    }
                    //Redirect parent to his/her homepage
                    FXMLLoader parentLoader = new FXMLLoader(UserRedirectionUtils.class.getResource("ParentHomePage.fxml"));
                    Parent parentRoot = parentLoader.load();
                    ParentHomePageController parentController = parentLoader.getController();
                    parentController.setup(currentParents);
                    parentController.setPrimaryStage(primaryStage);
                    primaryStage.setScene(new Scene(parentRoot));
                    primaryStage.show();
                    break;

                case 3: // Student
                    //Create an instance of student
                    Student currentStudent = new Student(userId, userEmail, userUsername, userPassword, roleId, locationX, locationY, userPoints);
                    //Retrieve parents of the child/student
                    preparedStatement = connection.prepareStatement("SELECT * FROM ParentChildRelationship WHERE child_id = ?");
                    preparedStatement.setInt(1, currentStudent.getUserId());
                    resultSet = preparedStatement.executeQuery();
                    //Create an array list to store the student's parent_id(s)
                    List<Integer> parentList = new ArrayList<>();
                    while (resultSet.next()) {
                        parentList.add(resultSet.getInt("parent_id"));
                    }
                    for (int id : parentList) {
                        preparedStatement = connection.prepareStatement("SELECT * FROM User WHERE user_id = ?");
                        preparedStatement.setInt(1, id);
                        resultSet = preparedStatement.executeQuery();
                        while (resultSet.next()) {
                            int parentId = resultSet.getInt("user_id");
                            String parentEmail = resultSet.getString("email");
                            String parentUsername = resultSet.getString("username");
                            String parentPassword = resultSet.getString("password");
                            double parentX = resultSet.getDouble("location_coordinate_x");
                            double parentY = resultSet.getDouble("location_coordinate_y");
                            Parents parent = new Parents(parentId, parentEmail, parentUsername, parentPassword, 2, parentX, parentY);
                            // Add parent to the Student class to display parent name at his/her homepage
                            currentStudent.addParent(parent);
                        }
                    }
                    //Redirect student to his/her homepage
                    FXMLLoader studentLoader = new FXMLLoader(UserRedirectionUtils.class.getResource("studentProfile.fxml"));
                    Parent studentRoot = studentLoader.load();
                    StudentProfileController studentController = studentLoader.getController();
                    studentController.setup(currentStudent);
                    studentController.setPrimaryStage(primaryStage);
                    primaryStage.setScene(new Scene(studentRoot));
                    primaryStage.show();
                    break;
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}
