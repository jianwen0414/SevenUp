/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author User
 */
public abstract class User {
    protected int userId;
    protected String email;
    protected String username;
    protected String password;
    protected int roleId;
    protected double locationCoordinateX;
    protected double locationCoordinateY;

    public User(int userId, String email, String username, String password, int roleId, double x, double y) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.roleId = roleId;
        this.locationCoordinateX = x;
        this.locationCoordinateY = y;
    }

    // Getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public double getLocationCoordinateX() {
        return locationCoordinateX;
    }

    public void setLocationCoordinateX(double x) {
        this.locationCoordinateX = x;
    }

    public double getLocationCoordinateY() {
        return locationCoordinateY;
    }

    public void setLocationCoordinateY(double y) {
        this.locationCoordinateY = y;
    }
    
    public ArrayList<String> viewProfileList(int roleId) {
        ArrayList<String> profileList = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection()) {
            String query = "SELECT username FROM User WHERE role_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, roleId); // Set the role_id parameter
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String username = rs.getString("username");
                        if (!this.getUsername().equals(username)) {
                            profileList.add(username);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return profileList;
    }
}

