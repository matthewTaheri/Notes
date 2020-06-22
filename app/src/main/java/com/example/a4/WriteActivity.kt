package com.example.a4

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_write2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WriteActivity : AppCompatActivity() {
    lateinit var dataClass: DataClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write2)
        init()
        if (supportActionBar != null) supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.write_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        if (item.itemId == R.id.action_save) {
            if (noteName_textInput!!.length() == 0) {
                textInputLayout_name.error = "enter name of your note"
                return false
            }
            if (note_textInout!!.length() == 0) {
                textInputLayout_note.error = "enter your note"
                return false
            }
            val name = noteName_textInput.text.toString().trim { it <= ' ' }
            val context = note_textInout.text.toString()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val date2 = dateFormat.format(date)
            dataClass.note = context
            dataClass.noteName = name
            dataClass.date = date2
            Log.d("tag", dataClass.noteName)
            writeData()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        dataClass = DataClass()
        note_textInout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length != 0) textInputLayout_note.setError(null)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        noteName_textInput!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length != 0) textInputLayout_name!!.setError(null)
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun writeData() {
        val dbHelper = DbHelper(this)
        val thread = Thread(Runnable {
            val db = dbHelper.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(DbHelper.COLUMN_NAME_TITLE, dataClass.noteName)
            contentValues.put(DbHelper.COLUMN_NAME_NOTE, dataClass.note)
            contentValues.put(DbHelper.COLUMN_DATE, dataClass.date)
            val id = db.insert(DbHelper.TABLE_NAME, null, contentValues)
            Log.d("tag", id.toString())
            db.close()
        })
        thread.start()
    }
}