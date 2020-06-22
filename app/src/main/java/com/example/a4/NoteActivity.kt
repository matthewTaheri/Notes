package com.example.a4

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_note2.*

class NoteActivity : AppCompatActivity() {
    var id: Long = 0
    var dbHelper: DbHelper? = null
    var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    var noteName: String? = null
    var note: String? = null
    var date: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note2)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        init()
    }

    override fun onResume() {
        super.onResume()
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

    fun init() {
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey("id")) {
                id = bundle.getLong("id")
            }
        }
        dbHelper = DbHelper(this)
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        val fab = findViewById<FloatingActionButton>(R.id.edit_fb)
        fab.setOnClickListener {
            val intent = Intent(this@NoteActivity, EditActivity::class.java)
            intent.putExtra("note", note)
            intent.putExtra("noteName", noteName)
            intent.putExtra("id", id)
            startActivity(intent)
        }
    }

    fun readData() {
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
            }
            runOnUiThread {
                collapsingToolbarLayout!!.title = noteName
                note_textView.text = note
                note_date_textView.text = date
            }
            cursor.close()
            db.close()
        })
        thread.start()
    }
}