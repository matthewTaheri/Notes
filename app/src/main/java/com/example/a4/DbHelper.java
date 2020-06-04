package com.example.a4;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";
    public static final String TABLE_NAME = "note_table";
    public static final String COLUMN_NAME_TITLE = "title";
    public static final String COLUMN_ID = "id";
    public  static final String COLUMN_DATE = "date";
    public static final String COLUMN_NAME_NOTE = "note";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_DATE + " TEXT," +
                    COLUMN_NAME_TITLE + " TEXT," +
                    COLUMN_NAME_NOTE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void deleteData(final String id){
                SQLiteDatabase db = getWritableDatabase();
                db.delete(TABLE_NAME,COLUMN_ID+"=?",new String[]{id});
    }

    public void updateData(Long id, String noteName,String note, String date){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID,id);
        contentValues.put(COLUMN_NAME_TITLE,noteName);
        contentValues.put(COLUMN_NAME_NOTE,note);
        contentValues.put(COLUMN_DATE,date);
        db.update(TABLE_NAME,contentValues,"id =?",new String[]{String.valueOf(id)});
    }
    }
