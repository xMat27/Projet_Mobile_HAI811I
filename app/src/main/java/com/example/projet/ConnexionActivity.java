package com.example.projet;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConnexionActivity extends AppCompatActivity {
    // Views
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView registerTextView;

    // Utilisateur
    private User user;
    private UserDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        // Initialisation des vues
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);

        // Initialisation de la base de données
        dbHelper = new UserDBHelper(this);

        // Gestionnaire d'événements pour le bouton de connexion
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupérer l'email et le mot de passe saisis
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Vérifier si les champs sont vides
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(ConnexionActivity.this, "Veuillez saisir votre email et votre mot de passe.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Authentifier l'utilisateur
                authenticateUser(email, password);
            }
        });

        // Gestionnaire d'événements pour le TextView d'inscription
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Rediriger vers l'activité d'inscription
                Intent intent = new Intent(ConnexionActivity.this, InscriptionActivity.class);
                startActivity(intent);
            }
        });
    }

    // Méthode pour authentifier l'utilisateur
    private void authenticateUser(String email, String password) {
        // Obtenir la base de données en mode lecture
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Définir les colonnes à interroger
        String[] projection = {
                UserContract.UserEntry.COLUMN_USERNAME,
                UserContract.UserEntry.COLUMN_EMAIL,
                UserContract.UserEntry.COLUMN_CITY,
                UserContract.UserEntry.COLUMN_DESCRIPTION,
                UserContract.UserEntry.COLUMN_IS_PREMIUM,
                UserContract.UserEntry.COLUMN_CONNECTED
        };

        // Filtrer les résultats où "email" est égal à l'email saisi
        String selection = UserContract.UserEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        // Exécuter la requête
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,   // Nom de la table
                projection,                         // Colonnes à retourner
                selection,                          // Clause WHERE
                selectionArgs,                      // Arguments de la clause WHERE
                null,                               // Groupe des lignes
                null,                               // Filtrer par groupe de lignes
                null                                // Ordre de tri
        );

        // Vérifier si l'utilisateur a été trouvé dans la base de données
        if (cursor.moveToNext()) {
            // Récupérer les informations de l'utilisateur à partir du curseur
            String username = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_USERNAME));
            String city = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_CITY));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_DESCRIPTION));
            boolean isPremium = cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_IS_PREMIUM)) == 1;
            boolean connect = cursor.getInt(cursor.getColumnIndexOrThrow(UserContract.UserEntry.COLUMN_CONNECTED)) == 1;


            // Créer un nouvel objet User
            user = new User(username, email, city, description, isPremium, connect);

            // Connexion réussie, connectez l'utilisateur
            user.connect();

            // Rediriger vers l'activité principale si l'utilisateur est connecté
            if (user.isConnected()) {
                Intent intent = new Intent(ConnexionActivity.this, MainActivity.class);
                intent.putExtra("username", user.getUsername());
                intent.putExtra("email", user.getEmail());
                intent.putExtra("city", user.getCity());
                intent.putExtra("description", user.getDescription());
                intent.putExtra("isPremium", user.isPremium());
                intent.putExtra("connected", user.isConnected());
                startActivity(intent);
                finish();
            }
        } else {
            // Afficher un message d'erreur si l'utilisateur n'est pas trouvé dans la base de données
            Toast.makeText(ConnexionActivity.this, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show();
        }

        // Fermer le curseur et la base de données
        cursor.close();
        db.close();
    }
}
