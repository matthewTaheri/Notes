package com.example.a4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_note2.*


class NoteActivity : AppCompatActivity() {
    var id: Long = 0
    var dbHelper: DbHelper? = null
    var noteName: String? = null
    var note: String? = null
    var date: String? = null
    var uri: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note2)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        init()
        readData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.note_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_note_delete) {
            AlertDialog.Builder(this).setMessage("Do you want to delete this ?").setPositiveButton("yes") { dialog, which ->
                val thread = Thread(Runnable { dbHelper!!.deleteData(id.toString()) })
                thread.start()
                finish()
            }.setNegativeButton("no", null).show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey("id")) {
                id = bundle.getLong("id")
            }
        }
        dbHelper = DbHelper(this)
        val fab = findViewById<FloatingActionButton>(R.id.edit_fb)
        fab.setOnClickListener {
            val intent = Intent(this@NoteActivity, EditActivity::class.java).apply {
                putExtra("note", note)
                putExtra("noteName", noteName)
                if (uri != null) putExtra("uri", uri)
                putExtra("id", id)
            }
            startActivity(intent)
        }
    }

    private fun readData() {
        val thread = Thread(Runnable {
            val db = dbHelper!!.readableDatabase
            val selection = DbHelper.COLUMN_ID + " =?"
            val selectionArgs = arrayOf(id.toString())
            val cursor = db.query(DbHelper.TABLE_NAME, null, selection, selectionArgs,
                    null, null, null)
            while (cursor.moveToNext()) {
                noteName = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_TITLE))
                note = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_NOTE))
                date = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DATE))
                uri = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IMAGE_URI))
            }
            runOnUiThread {
                if (uri != null) {
                    Picasso.get().load(Uri.parse(uri)).into(header)
                    header.setImageURI(Uri.parse(uri))
                }
                collapsing_toolbar.title = noteName
                note_textView.text = note
                note_date_textView.text = date
            }
            cursor.close()
            db.close()
        })
        thread.start()
    }

}