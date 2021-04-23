package com.example.androidcarmanager.Database;

public class Add_Notes_DB {
    String title, message;
    Long date;

    public Add_Notes_DB() {
    }

    public Add_Notes_DB(String title, String message, Long date) {
        this.title = title;
        this.message = message;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
