package com.example.projet;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class FullScreenDrawingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_drawing);

        // Récupérer l'ID de la ressource d'image du dessin
        int imageResourceId = getIntent().getIntExtra("imageResourceId", 0);

        // Afficher l'image en plein écran avec Picasso
        ImageView imageView = findViewById(R.id.imageView);
        Picasso.get().load(imageResourceId).into(imageView);
    }
}


