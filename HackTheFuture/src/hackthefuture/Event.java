/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author User
 */
public class Event {
    private int eventId;
    private String title;
    private String description;
    private String venue;
    private java.time.LocalDate date;
    private java.time.LocalTime time;

    public Event(int eventId, String title, String description, String venue, java.time.LocalDate date, java.time.LocalTime time) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.venue = venue;
        this.date = date;
        this.time = time;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }


}
