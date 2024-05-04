/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 *
 * @author User
 */

public class Student extends User {
    private int currentPoints=0;
    private Parent[] parents; // Array to handle two parents

    public Student(int userId, String email, String username, String password, int roleId, double x, double y, int currentPoints, Parent[] parents) {
        super(userId, email, username, password, roleId, x, y);
        this.currentPoints = currentPoints;
        this.parents = parents; 
    }

    // Getters and setters for student-specific fields
    public Parent[] getParents() {
        return parents;
    }

    public void setParents(Parent[] parents) {
        this.parents = parents;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }
    
    
}
