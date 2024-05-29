package com.example.projet;

import java.io.Serializable;

public class Drawing implements Serializable {
    private int id;
    private String title;
    private String author;
    private String date;
    private int imageResourceId;

    public Drawing(String title, String author, String date, int imageResourceId) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.imageResourceId = imageResourceId;
    }

    // Getters et setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public void setImageResourceId(int imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
