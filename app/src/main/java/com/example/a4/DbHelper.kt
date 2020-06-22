package com.example.a4

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    fun deleteData(id: String) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID=?", arrayOf(id))
    }

    fun updateData(id: Long, noteName: String?, note: String?, date: String?) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID, id)
        contentValues.put(COLUMN_NAME_TITLE, noteName)
        contentValues.put(COLUMN_NAME_NOTE, note)
        contentValues.put(COLUMN_DATE, date)
        db.update(TABLE_NAME, contentValues, "id =?", arrayOf(id.toString()))
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"
        const val TABLE_NAME = "note_table"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_ID = "id"
        const val COLUMN_DATE = "date"
        const val COLUMN_NAME_NOTE = "note"
        private const val SQL_CREATE_ENTRIES = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_NAME_TITLE TEXT," +
                "$COLUMN_NAME_NOTE TEXT)"
        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}