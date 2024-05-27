/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;

public class Parents extends User {
    private List<Student> children;

    public Parents(int userId, String email, String username, String password, int roleId, double x, double y) {
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
    
    public String[] bookDestination() {
        PriorityQueue <BookingDestination> bklist = new PriorityQueue<>();
        try{
            Scanner inputStream = new Scanner(new FileInputStream("BookingDestination.txt"));   int counter = 0;
            while(inputStream.hasNextLine()){
                String name = inputStream.nextLine();
                String coordinate = inputStream.nextLine();
                String[] xy = coordinate.split(", ");
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                BookingDestination bk = new BookingDestination(name,x,y,this);
                bklist.offer(bk);
            }
        }catch(FileNotFoundException e){
            System.out.println("File Not Found");
        }

        String[] bkarr = new String[9];
        int i=0;
        while(!bklist.isEmpty()){
            DecimalFormat df = new DecimalFormat("#.##");
            bkarr[i] = bklist.peek().name + "\n" + df.format(bklist.poll().euclidean) + " km away";
            i++;
        }
        return bkarr;
    }
    
    public String[] getChildrenName(){
        String[] childName = new String[children.size()];
        for(int i=0; i<children.size(); i++){
            childName[i] = children.get(i).username;
        }
        return childName;
    }
    
    public String[] bookDate(Student s){
        LocalDate startDate = LocalDate.now();
        ArrayList<LocalDate> datelist = new ArrayList<LocalDate>();
        ArrayList<LocalDate> busyDate = s.getBusyDates();
        
        for (int i = 0; i < 7; i++) {
            LocalDate temp = startDate.plusDays(i);
            boolean found = false;
            for (int j = 0; j < busyDate.size(); j++) {
                if (busyDate.get(j) != null && busyDate.get(j).equals(temp)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                datelist.add(temp);
            }
        }
        String[] dateStringArray = new String[datelist.size()];
        for (int i = 0; i < datelist.size(); i++) {
            dateStringArray[i] = datelist.get(i).toString();
        }

        return dateStringArray;
    }
    
    public Student getChildByName(String name) {
        for (Student child : children) {
            if (child.getName().equals(name)) {
                return child;
            }
        }
        return null;
    }

}
