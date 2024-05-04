/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 *
 * @author User
 */
import java.util.ArrayList;
import java.util.List;

public class Educator extends User {
    private List<Event> createdEvents;
    private List<Quiz> createdQuizzes;

    public Educator(int userId, String email, String username, String password, int roleId, double x, double y) {
        super(userId, email, username, password, roleId, x, y);
        this.createdEvents = new ArrayList<>();
        this.createdQuizzes = new ArrayList<>();
    }

    public void createEvent(Event event) {
        createdEvents.add(event);
    }

    public void removeEvent(Event event) {
        createdEvents.remove(event);
    }

    public List<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void createQuiz(Quiz quiz) {
        createdQuizzes.add(quiz);
    }


    public void removeQuiz(Quiz quiz) {
        createdQuizzes.remove(quiz);
    }

    public List<Quiz> getCreatedQuizzes() {
        return createdQuizzes;
    }
}

