package com.example.projet;

import android.provider.BaseColumns;

public final class UserContract {
    // Constructeur privé pour empêcher l'instanciation de la classe
    private UserContract() {}

    // Définition du schéma de la table des utilisateurs
    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_IS_PREMIUM = "is_premium";
        public static final String COLUMN_CONNECTED = "connect";
    }
}

