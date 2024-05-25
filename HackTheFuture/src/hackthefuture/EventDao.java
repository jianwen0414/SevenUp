/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tan Shi Han
 */


public class EventDao {

    public static void saveEvent(Event event, User currentUser) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean isSuccess=false;
        try {
            // Prepare the INSERT statement for Event table
            String eventSql = "INSERT INTO Event (event_title, event_description, event_venue, event_date, event_time) "
                    + "VALUES (?, ?, ?, ?, ?)";

            conn = DatabaseConnector.getConnection();
            ps = conn.prepareStatement(eventSql, Statement.RETURN_GENERATED_KEYS);

            // Set values to the prepared statement
            ps.setObject(1, event.getTitle());
            ps.setObject(2, event.getDescription());
            ps.setObject(3, event.getVenue());
            ps.setDate(4, Date.valueOf(event.getDate()));
            ps.setTime(5, Time.valueOf(event.getTime()));

            // Execute the INSERT statement
            ps.executeUpdate();

            // Retrieve the generated event_id
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int eventId = rs.getInt(1);

                // Get the current user ID
                int currentUserId = currentUser.getUserId();

                // Insert record into EventCreationRecord table
                String recordSql = "INSERT INTO EventCreationRecord (creator_id, event_id) VALUES (?, ?)";
                try (PreparedStatement recordPs = conn.prepareStatement(recordSql)) {
                    recordPs.setInt(1, currentUserId);
                    recordPs.setInt(2, eventId);
                    recordPs.executeUpdate();
                    isSuccess=true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions here, if needed
        } finally {
            // Close resources
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(isSuccess){
                AlertUtils.showSuccessCreateEvent();
            }else{
                AlertUtils.showFailCreateEvent();
            }
        }
    }

    public static List<Event> getTodayEvent() {
        List<Event> res = new ArrayList<>();
        try {
            // Get the database connection from the DatabaseConnector class
            Connection connection = DatabaseConnector.getConnection();
            String sql = "SELECT * FROM Event "
                    + "WHERE event_date = CURRENT_DATE AND event_time > CURRENT_TIME "
                    + "ORDER BY event_date, event_time";
            try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int eventID = resultSet.getInt("event_id");
                    String eventName = resultSet.getString("event_title");
                    LocalDate eventDate = resultSet.getDate("event_date").toLocalDate();
                    LocalTime eventTime = resultSet.getTime("event_time").toLocalTime();
                    String eventDescription = resultSet.getString("event_description");
                    String eventVenue = resultSet.getString("event_venue");

                    res.add(new Event(eventID, eventName, eventDescription, eventVenue, eventDate, eventTime));
                }
                return res;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    
    public static List<Event> getUpcomingEvent() {
        List<Event> res = new ArrayList<>();
        try {
            // Get the database connection from the DatabaseConnector class
            Connection connection = DatabaseConnector.getConnection();
            String sql = "SELECT * FROM Event "
                    + "WHERE event_date > CURRENT_DATE  "
                    + "ORDER BY event_date LIMIT 3";
            try (PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int eventID = resultSet.getInt("event_id");
                    String eventName = resultSet.getString("event_title");
                    LocalDate eventDate = resultSet.getDate("event_date").toLocalDate();
                    LocalTime eventTime = resultSet.getTime("event_time").toLocalTime();
                    String eventDescription = resultSet.getString("event_description");
                    String eventVenue = resultSet.getString("event_venue");

                    res.add(new Event(eventID, eventName, eventDescription, eventVenue, eventDate, eventTime));
                }
                return res;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    

}
