package com.example.projet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class DrawingAdapter extends RecyclerView.Adapter<DrawingAdapter.DrawingViewHolder> {

    private List<Drawing> drawingList;

    public DrawingAdapter(List<Drawing> drawingList) {
        this.drawingList = drawingList;
    }

    @NonNull
    @Override
    public DrawingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawing, parent, false);
        return new DrawingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawingViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Drawing drawing = drawingList.get(position);
        holder.titleTextView.setText(drawing.getTitle());
        holder.authorTextView.setText(drawing.getAuthor());
        holder.dateTextView.setText(drawing.getDate());

        // Utiliser Picasso pour charger l'image
        File storageDir = holder.itemView.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, "Drawing_" + drawing.getImageResourceId() + ".png");

        if (imageFile.exists()) {
            Picasso.get().load(imageFile).into(holder.thumbnailImageView);
        }
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
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Supprimer le dessin de la base de données et mettre à jour l'adaptateur
                DrawingDatabaseHelper dbHelper = new DrawingDatabaseHelper(holder.itemView.getContext());
                dbHelper.deleteDrawing(drawing.getDate());
                drawingList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, drawingList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawingList.size();
    }

    public void setDrawingList(List<Drawing> drawingList) {
        this.drawingList = drawingList;
        notifyDataSetChanged();
    }

    static class DrawingViewHolder extends RecyclerView.ViewHolder {
        public Button drawButton;
        public Button deleteButton;
        TextView titleTextView;
        TextView authorTextView;
        TextView dateTextView;
        ImageView thumbnailImageView;

        public DrawingViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            drawButton = itemView.findViewById(R.id.drawButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
