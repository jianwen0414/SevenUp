/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author minzi
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/hackingthefuture"; // Replace with your database URL
        String user = "root"; // Replace with your database username
        String password = "No16juru*"; // Replace with your database password
        return DriverManager.getConnection(url, user, password);
    }
}
