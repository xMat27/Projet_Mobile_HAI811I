package com.example.projet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Calendar;

public class DrawingActivity extends AppCompatActivity {

    private EditText etDrawingName;
    private CanvasView canvasView;
    private Button btnSave;
    private int imageResourceId;
    private String fileName;
    private DrawingDatabaseHelper dbHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        // Initialiser les vues
        canvasView = findViewById(R.id.canvasView);
        btnSave = findViewById(R.id.btnSave);
        etDrawingName = findViewById(R.id.etDrawingName);

        // Récupérer l'identifiant de l'image à partir de l'intent
        imageResourceId = getIntent().getIntExtra("imageResourceId", -1);
        fileName = "Drawing_" + imageResourceId + ".png";

        // Récupérer l'email de l'utilisateur à partir de l'intent
        userEmail = getIntent().getStringExtra("email");

        // Initialiser la base de données
        dbHelper = new DrawingDatabaseHelper(this);

        // Configurer le clic sur le bouton de sauvegarde
        btnSave.setOnClickListener(v -> saveDrawing());

        boolean isNewDrawing = getIntent().getBooleanExtra("isNewDrawing", false);
        if (isNewDrawing) {
            createNewDrawing();
        } else {
            loadDrawing();
        }
    }

    private void createNewDrawing() {
        canvasView.post(() -> {
            // Get the width and height after the view has been laid out
            int width = canvasView.getWidth();
            int height = canvasView.getHeight();

            // Check if width and height are valid
            if (width > 0 && height > 0) {
                // Create a blank bitmap with the determined width and height
                Bitmap blankBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(blankBitmap);
                canvas.drawColor(Color.WHITE); // Draw a white background
                canvasView.setBitmap(blankBitmap);
                Toast.makeText(this, "Nouveau dessin créé", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the case where width or height is not valid
                Toast.makeText(this, "Impossible de créer un nouveau dessin : largeur ou hauteur invalide", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDrawing() {
        Bitmap bitmap = canvasView.getBitmap();
        String drawingName = etDrawingName.getText().toString();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Dessin sauvegardé", Toast.LENGTH_SHORT).show();

            // Ajouter le dessin à la base de données
            String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            String title = drawingName;
            Drawing drawing = new Drawing(title, userEmail, currentDate, imageResourceId);
            dbHelper.addDrawing(drawing);

            // Revenir à MainActivity
            Intent intent = new Intent(DrawingActivity.this, MainActivity.class);
            intent.putExtra("username", getIntent().getStringExtra("username"));
            intent.putExtra("email", userEmail);
            intent.putExtra("city", getIntent().getStringExtra("city"));
            intent.putExtra("description", getIntent().getStringExtra("description"));
            intent.putExtra("isPremium", getIntent().getBooleanExtra("isPremium", false));
            intent.putExtra("connected", true);
            startActivity(intent);
            finish(); // Terminez cette activité

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors de la sauvegarde du dessin", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDrawing() {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            canvasView.setBitmap(bitmap);
            Toast.makeText(this, "Dessin chargé", Toast.LENGTH_SHORT).show();
        } else {
            // Charger l'image à partir des ressources si aucun fichier sauvegardé n'existe
            if (imageResourceId != -1) {
                InputStream inputStream = getResources().openRawResource(imageResourceId);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                canvasView.setBitmap(bitmap);
            } else {
                Toast.makeText(this, "Aucun dessin sauvegardé trouvé", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
