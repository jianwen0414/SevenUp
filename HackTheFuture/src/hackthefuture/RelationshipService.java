/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 *
 * @author Jian Wen Lee
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelationshipService {

    public static List<ParentChildRelationship> getRelationshipsForUser(int userId) {
        List<ParentChildRelationship> relationships = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection()) {
            String sql = "SELECT pcr.parent_id, pcr.child_id, " +
                         "(SELECT username FROM User WHERE user_id = pcr.parent_id) AS parent_username, " +
                         "(SELECT username FROM User WHERE user_id = pcr.child_id) AS child_username " +
                         "FROM ParentChildRelationship pcr " +
                         "WHERE pcr.parent_id = ? OR pcr.child_id = ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int parentId = resultSet.getInt("parent_id");
                int childId = resultSet.getInt("child_id");
                String parentUsername = resultSet.getString("parent_username");
                String childUsername = resultSet.getString("child_username");
                relationships.add(new ParentChildRelationship(parentId, childId, parentUsername, childUsername));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return relationships;
    }
}


    
