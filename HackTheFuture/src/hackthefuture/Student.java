/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */

public class Student extends User {
    private int currentPoints=0;
    private List<Parents> parents = new ArrayList<>(); // Array to handle two parents
    private LocalDateTime lastPointsUpdate;

    public Student(int userId, String email, String username, String password, int roleId, double x, double y, int currentPoints) {
        super(userId, email, username, password, roleId, x, y);
        this.currentPoints = currentPoints;
    }
    
    public Student(int userId, String email, String username, String password, int roleId, double x, double y, int currentPoints, LocalDateTime lastPointsUpdate) {
        super(userId, email, username, password, roleId, x, y);
        this.currentPoints = currentPoints;
        this.lastPointsUpdate = lastPointsUpdate;
    }
    // Getters and setters for student-specific fields
    public List<Parents> getParents() {
        return parents;
    }

    public void addParent(Parents parent) {
        this.parents.add(parent);
    }

    public int getCurrentPoints() {
        return currentPoints;
    }
    public LocalDateTime getLastPointsUpdate() { 
        return lastPointsUpdate; 
    }
    
    
}
