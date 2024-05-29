package com.example.projet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DrawingDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "drawings.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "drawings";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_IMAGE_RESOURCE_ID = "imageResourceId";

    public DrawingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DRAWINGS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_AUTHOR + " TEXT," +
                COLUMN_DATE + " TEXT," +
                COLUMN_IMAGE_RESOURCE_ID + " INTEGER)";
        db.execSQL(CREATE_DRAWINGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addDrawing(Drawing drawing) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, drawing.getTitle());
        values.put(COLUMN_AUTHOR, drawing.getAuthor());
        values.put(COLUMN_DATE, drawing.getDate());
        values.put(COLUMN_IMAGE_RESOURCE_ID, drawing.getImageResourceId());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void deleteDrawing(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_DATE + " = ? ", new String[]{String.valueOf(date)});
        db.close();
    }

    public void deleteAllDrawings() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public List<Drawing> getAllDrawings() {
        List<Drawing> drawingList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String author = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_AUTHOR));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                int imageResourceId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RESOURCE_ID));

                Drawing drawing = new Drawing(title, author, date, imageResourceId);
                drawingList.add(drawing);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return drawingList;
    }
}
