/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
    
    public String getName(){
        return username;
    }
    
    public int getId(){
        return userId;
    }
    
    public ArrayList<LocalDate> getBusyDates() {
        ArrayList<LocalDate> busyDates = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection()) {
            String eventQuery = "SELECT booking_date FROM UserBookingEvent WHERE user_id = ?";
            try (PreparedStatement pstmtEvent = connection.prepareStatement(eventQuery)) {
                pstmtEvent.setInt(1, this.getId());
                try (ResultSet rsEvent = pstmtEvent.executeQuery()) {
                    while (rsEvent.next()) {
                        LocalDate bookingDate = rsEvent.getDate("booking_date").toLocalDate();
                        busyDates.add(bookingDate);
                    }
                }
            }

            String destinationQuery = "SELECT booking_date FROM UserBookingDestination WHERE student_id = ?";
            try (PreparedStatement pstmtDestination = connection.prepareStatement(destinationQuery)) {
                pstmtDestination.setInt(1, this.getId());
                try (ResultSet rsDestination = pstmtDestination.executeQuery()) {
                    while (rsDestination.next()) {
                        LocalDate bookingDate = rsDestination.getDate("booking_date").toLocalDate();
                        busyDates.add(bookingDate);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return busyDates;
    }

    
    public ArrayList<String> viewStudentProfileList() {
            ArrayList<String> studentList = new ArrayList<>();

            try (Connection connection = DatabaseConnector.getConnection()) {
                String query = "SELECT username FROM User WHERE role_id = 3";
                try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            String username = rs.getString("username");
                            if(!this.getName().equals(username)){
                            studentList.add(username);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return studentList;
        }
    
    
}
