package com.example.projet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHelper extends SQLiteOpenHelper {
    // Nom de la base de données
    public static final String DATABASE_NAME = "User.db";
    // Version de la base de données
    public static final int DATABASE_VERSION = 1;

    // Requête de création de la table des utilisateurs
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                    UserContract.UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserContract.UserEntry.COLUMN_USERNAME + " TEXT," +
                    UserContract.UserEntry.COLUMN_EMAIL + " TEXT," +
                    UserContract.UserEntry.COLUMN_CITY + " TEXT," +
                    UserContract.UserEntry.COLUMN_DESCRIPTION + " TEXT," +
                    UserContract.UserEntry.COLUMN_IS_PREMIUM + " INTEGER," + // Ajoutez une virgule ici
                    UserContract.UserEntry.COLUMN_CONNECTED + " INTEGER)";

    // Requête de suppression de la table des utilisateurs
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserContract.UserEntry.TABLE_NAME;

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Créer la table des utilisateurs lors de la première création de la base de données
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Supprimer la table des utilisateurs si elle existe déjà
        db.execSQL(SQL_DELETE_ENTRIES);
        // Recréer la table des utilisateurs
        onCreate(db);
    }
}

