package com.example.projet;

import java.util.ArrayList;

// Modèle de données pour un utilisateur
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String username;
    private String email;
    private String city;
    private String description;
    private boolean isPremium;
    private boolean connected;

    // Constructeur
    public User(String username, String email, String city, String description, boolean isPremium, boolean connected) {
        this.username = username;
        this.email = email;
        this.city = city;
        this.description = description;
        this.isPremium = isPremium;
        this.connected = false;
    }

    // Getters et Setters

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        city = in.readString();
        description = in.readString();
        isPremium = in.readByte() != 0;
        connected = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeString(city);
        dest.writeString(description);
        dest.writeByte((byte) (isPremium ? 1 : 0));
        dest.writeByte((byte) (connected ? 1 : 0));
    }


    // Méthode pour vérifier si l'utilisateur est connecté
    public boolean isConnected() {
        return connected;
    }

    // Méthode pour se connecter
    public void connect() {
        connected = true;
    }

    // Méthode pour se déconnecter
    public void disconnect() {
        connected = false;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}

