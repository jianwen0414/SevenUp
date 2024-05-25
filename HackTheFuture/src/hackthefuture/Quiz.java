/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hackthefuture;

/**
 *
 * @author User
 */
public class Quiz {
    private int quizId;
    private String title;
    private String description;
    private int themeId;  // themes are identified by integers
    private String content;

    public Quiz(int quizId, String title, String description, int themeId, String content) {
        this.quizId = quizId;
        this.title = title;
        this.description = description;
        this.themeId = themeId;
        this.content = content;
    }
    
    public Quiz( String title, String description, int themeId, String content) {
        this.title = title;
        this.description = description;
        this.themeId = themeId;
        this.content = content;
    }
    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
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

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}