package com.example.projet;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet.Drawing;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrawingAdapter extends RecyclerView.Adapter<DrawingAdapter.DrawingViewHolder> {
    private List<Drawing> drawingList;
    private Context context;

    public DrawingAdapter(List<Drawing> drawingList) {
        this.drawingList = drawingList;
    }

    @NonNull
    @Override
    public DrawingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawing, parent, false);
        return new DrawingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawingViewHolder holder, int position) {
        // Récupérer le dessin à cette position
        Drawing drawing = drawingList.get(position);

        // Attribuer les données du dessin à la vue
        holder.titleTextView.setText(drawing.getTitle());
        holder.authorTextView.setText(drawing.getAuthor());
        holder.dateTextView.setText(drawing.getDate());

        // Charger l'image à partir de la ressource
        //holder.imageView.setImageResource(drawing.getImageResourceId());
        // Charger l'image à partir de la ressource drawable avec Picasso
        Picasso.get().load(drawing.getImageResourceId()).into(holder.imageView);

        // Ajouter un gestionnaire d'événements pour l'élément de dessin
        holder.drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ouvrez DrawingActivity avec l'ID de la ressource de l'image
                Context context = holder.itemView.getContext();
                Intent intent = new Intent(context, DrawingActivity.class);
                intent.putExtra("imageResourceId", drawing.getImageResourceId());
                context.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return drawingList.size();
    }


    static class DrawingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView dateTextView;
        public Button drawButton;

        public DrawingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.drawingImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            drawButton = itemView.findViewById(R.id.drawButton);
        }
    }
}
