package com.example.projet;

public class Drawing {
    private String title;
    private String author;
    private String date;
    private int imageResourceId; // Ajout de l'attribut pour l'ID de la ressource d'image

    public Drawing(String title, String author, String date, int imageResourceId) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}

