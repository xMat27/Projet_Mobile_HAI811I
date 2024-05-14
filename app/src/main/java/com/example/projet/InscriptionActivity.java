package com.example.projet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class InscriptionActivity extends AppCompatActivity {
    // Views
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText cityEditText;
    private EditText descriptionEditText;
    private Button inscriptionButton;

    // Database
    private UserDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        // Initialisation des vues
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        cityEditText = findViewById(R.id.cityEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        inscriptionButton = findViewById(R.id.inscriptionButton);

        // Initialisation de la base de données
        dbHelper = new UserDBHelper(this);

        // Gestionnaire d'événements pour le bouton d'inscription
        inscriptionButton.setOnClickListener(view -> {
            // Récupérer les valeurs saisies
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String city = cityEditText.getText().toString();
            String description = descriptionEditText.getText().toString();

            // Créer un nouvel utilisateur
            User user = new User(username, email, city, description, false, true); // false pour indiquer que l'utilisateur n'est pas premium

            // Ajouter l'utilisateur à la base de données
            long newRowId = addUser(user);

            // Vérifier si l'utilisateur a été ajouté avec succès
            if (newRowId != -1) {
                // Afficher un message de succès
                Toast.makeText(InscriptionActivity.this, "Inscription réussie pour " + email, Toast.LENGTH_SHORT).show();

                // Rediriger vers l'activité principale
                Intent intent = new Intent(InscriptionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Afficher un message d'erreur
                Toast.makeText(InscriptionActivity.this, "Erreur lors de l'inscription", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Méthode pour ajouter un utilisateur à la base de données
    private long addUser(User user) {
        // Obtenir la base de données en mode écriture
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Créer un nouvel enregistrement d'utilisateur
        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_USERNAME, user.getUsername());
        values.put(UserContract.UserEntry.COLUMN_EMAIL, user.getEmail());
        values.put(UserContract.UserEntry.COLUMN_CITY, user.getCity());
        values.put(UserContract.UserEntry.COLUMN_DESCRIPTION, user.getDescription());
        values.put(UserContract.UserEntry.COLUMN_IS_PREMIUM, user.isPremium() ? 1 : 0); // 1 pour true, 0 pour false
        values.put(UserContract.UserEntry.COLUMN_CONNECTED, 1);

        // Ajouter l'utilisateur à la table des utilisateurs
        long newRowId = db.insert(UserContract.UserEntry.TABLE_NAME, null, values);

        // Fermer la base de données
        db.close();

        return newRowId;
    }
}
