/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 *
 * @author Jian Wen Lee
 */
public class ParentChildRelationship {
    private int parentId;
    private int childId;
    private String parentUsername;
    private String childUsername;

    public ParentChildRelationship(int parentId, int childId, String parentUsername, String childUsername) {
        this.parentId = parentId;
        this.childId = childId;
        this.parentUsername = parentUsername;
        this.childUsername = childUsername;
    }

    public int getParentId() {
        return parentId;
    }

    public int getChildId() {
        return childId;
    }

    public String getParentUsername() {
        return parentUsername;
    }

    public String getChildUsername() {
        return childUsername;
    }
}

