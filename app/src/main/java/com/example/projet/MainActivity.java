package com.example.projet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private Button searchButton;
    private Button deleteAllButton; // Ajoutez ce bouton
    private RecyclerView drawingRecyclerView;
    private DrawingAdapter drawingAdapter;
    private List<Drawing> drawingList;
    private DrawingDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String city = getIntent().getStringExtra("city");
        String description = getIntent().getStringExtra("description");
        boolean isPremium = getIntent().getBooleanExtra("isPremium", false);
        boolean isConnect = getIntent().getBooleanExtra("connected", false);
        String drawingTitle = getIntent().getStringExtra("drawingTitle");
        User user = new User(username, email, city, description, isPremium, isConnect);

        // Vérifiez si l'utilisateur est connecté ou non
        if (!isConnect) {
            // Redirigez vers l'écran de connexion ou d'inscription
            Intent intent = new Intent(MainActivity.this, ConnexionActivity.class);
            startActivity(intent);
            finish(); // Terminez cette activité pour éviter de revenir ici si l'utilisateur appuie sur le bouton retour
            return;
        }

        setContentView(R.layout.activity_main);

        // Initialisation des vues
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
        deleteAllButton = findViewById(R.id.deleteAllButton); // Initialisation du bouton
        drawingRecyclerView = findViewById(R.id.drawingRecyclerView);

        // Initialisation de la base de données
        dbHelper = new DrawingDatabaseHelper(this);

        // Chargement des dessins depuis la base de données
        drawingList = dbHelper.getAllDrawings();

        // Initialisation de l'adaptateur RecyclerView
        drawingAdapter = new DrawingAdapter(drawingList);
        drawingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawingRecyclerView.setAdapter(drawingAdapter);

        // Gestionnaire d'événements pour le bouton de recherche
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Récupérer le texte de recherche saisi par l'utilisateur
                String searchText = searchEditText.getText().toString().trim();

                // Filtrer les dessins en fonction du texte de recherche
                filterDrawings(searchText);
            }
        });

        // Gestionnaire d'événements pour le bouton de suppression de tous les dessins
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.deleteAllDrawings();
                drawingList.clear();
                drawingAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Tous les dessins ont été supprimés", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DrawingActivity.class);
                intent.putExtra("isNewDrawing", true); // Indicate it's a new drawing
                startActivity(intent);
            }
        });
    }

    // Méthode pour filtrer les dessins en fonction du texte de recherche
    private void filterDrawings(String searchText) {
        // Liste temporaire pour stocker les dessins filtrés
        List<Drawing> filteredDrawingList = new ArrayList<>();

        // Parcourir la liste des dessins
        for (Drawing drawing : drawingList) {
            // Vérifier si le titre ou l'auteur contient le texte de recherche
            if (drawing.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                    drawing.getAuthor().toLowerCase().contains(searchText.toLowerCase())) {
                // Ajouter le dessin à la liste filtrée
                filteredDrawingList.add(drawing);
            }
        }

        // Mettre à jour l'adaptateur avec la liste filtrée
        drawingAdapter.setDrawingList(filteredDrawingList);
    }
}
