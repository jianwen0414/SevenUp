/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.util.ArrayList;
import java.util.List;

public class Parent extends User {
    private List<Student> children;

    public Parent(int userId, String email, String username, String password, int roleId, double x, double y) {
        super(userId, email, username, password, roleId, x, y);
        this.children = new ArrayList<>();
    }

    public void addChild(Student child) {
        if (!children.contains(child)) {
            children.add(child);
        }
    }

    public void removeChild(Student child) {
        children.remove(child);
    }

    public List<Student> getChildren() {
        return children;
    }

}
