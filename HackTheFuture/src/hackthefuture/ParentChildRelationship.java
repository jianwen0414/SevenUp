/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 * This class represents the relationship between a parent and a child.
 * It contains the IDs and usernames of both the parent and the child.
 * The class provides getter methods to access these properties.
 * 
 * Author: Jian Wen Lee
 */
public class ParentChildRelationship {
    
    // Private member variables to store the IDs and usernames
    private int parentId;
    private int childId;
    private String parentUsername;
    private String childUsername;

    /**
     * Constructor to initialize a ParentChildRelationship object.
     * 
     * @param parentId The ID of the parent.
     * @param childId The ID of the child.
     * @param parentUsername The username of the parent.
     * @param childUsername The username of the child.
     */
    public ParentChildRelationship(int parentId, int childId, String parentUsername, String childUsername) {
        this.parentId = parentId;
        this.childId = childId;
        this.parentUsername = parentUsername;
        this.childUsername = childUsername;
    }

    /**
     * Getter method to retrieve the parent ID.
     * 
     * @return The ID of the parent.
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * Getter method to retrieve the child ID.
     * 
     * @return The ID of the child.
     */
    public int getChildId() {
        return childId;
    }

    /**
     * Getter method to retrieve the parent's username.
     * 
     * @return The username of the parent.
     */
    public String getParentUsername() {
        return parentUsername;
    }

    /**
     * Getter method to retrieve the child's username.
     * 
     * @return The username of the child.
     */
    public String getChildUsername() {
        return childUsername;
    }
}
