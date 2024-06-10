/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 * This class provides services to retrieve parent-child relationships from the database.
 * Author: Jian Wen Lee
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelationshipService {

    /**
     * Retrieves the list of parent-child relationships for a given user.
     * @param userId The ID of the user for whom the relationships are to be retrieved.
     * @return A list of ParentChildRelationship objects representing the relationships.
     */
    public static List<ParentChildRelationship> getRelationshipsForUser(int userId) {
        List<ParentChildRelationship> relationships = new ArrayList<>(); // Initialize the list to hold the relationships

        // Connect to the database
        try (Connection connection = DatabaseConnector.getConnection()) {
            // SQL query to retrieve parent-child relationships along with usernames
            String sql = "SELECT pcr.parent_id, pcr.child_id, " +
                         "(SELECT username FROM User WHERE user_id = pcr.parent_id) AS parent_username, " +
                         "(SELECT username FROM User WHERE user_id = pcr.child_id) AS child_username " +
                         "FROM ParentChildRelationship pcr " +
                         "WHERE pcr.parent_id = ? OR pcr.child_id = ?";
            
            // Prepare the SQL statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId); // Set the userId for the parent_id parameter
            statement.setInt(2, userId); // Set the userId for the child_id parameter
            ResultSet resultSet = statement.executeQuery(); // Execute the query

            // Iterate over the result set and populate the relationships list
            while (resultSet.next()) {
                int parentId = resultSet.getInt("parent_id"); // Get parent ID
                int childId = resultSet.getInt("child_id"); // Get child ID
                String parentUsername = resultSet.getString("parent_username"); // Get parent username
                String childUsername = resultSet.getString("child_username"); // Get child username
                relationships.add(new ParentChildRelationship(parentId, childId, parentUsername, childUsername)); // Add relationship to the list
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print stack trace if an SQL exception occurs
        }

        return relationships; // Return the list of relationships
    }
}
