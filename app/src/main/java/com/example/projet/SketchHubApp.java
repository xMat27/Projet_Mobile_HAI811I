package com.example.projet;

import java.util.ArrayList;

// Classe principale de l'application SketchHub
public class SketchHubApp {
    // Liste des utilisateurs enregistrés
    private ArrayList<User> users;

    public SketchHubApp() {
        this.users = new ArrayList<>();
        // Initialisation des utilisateurs (exemple)
        this.users.add(new User("utilisateur1", "utilisateur1@example.com", "Ville1", "Description 1", false, false));
        this.users.add(new User("utilisateur2", "utilisateur2@example.com", "Ville2", "Description 2", true, false));
    }

    // Méthode pour authentifier un utilisateur
    public User authenticateUser(String username, String email) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getEmail().equals(email)) {
                System.out.println("Utilisateur authentifié : " + username);
                return user;
            }
        }
        System.out.println("Authentification échouée pour l'utilisateur : " + username);
        return null;
    }

    // Méthode pour inscrire un nouvel utilisateur
    public void registerUser(String username, String email, String city, String description, boolean isPremium) {
        users.add(new User(username, email, city, description, isPremium, true));
        System.out.println("Utilisateur enregistré : " + username);
    }

    public static void main(String[] args) {
        // Création de l'application SketchHub
        SketchHubApp app = new SketchHubApp();

        // Authentification d'un utilisateur
        User user = app.authenticateUser("utilisateur1", "utilisateur1@example.com");

        // Inscription d'un nouvel utilisateur
        app.registerUser("nouvel_utilisateur", "nouvel_utilisateur@example.com", "Nouvelle Ville", "Nouvelle description", false);
    }
}
