package com.example.androidcarmanager.Galery_Views;

public class List_Model {
    String title;
    Long date;

    public List_Model(String title, Long date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}

