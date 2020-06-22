package com.example.a4

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    var id: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit2)
        init()
        if (supportActionBar != null) supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        if (item.itemId == R.id.action_edit) {
            if (nameOfNote.length() == 0) {
                textInputLayout_name_edit!!.error = "enter name of your note"
                return false
            }
            if (noteItself.length() == 0) {
                textInputLayout_note_edit!!.error = "enter your note"
                return false
            }
            val name44 = nameOfNote.text.toString().trim { it <= ' ' }
            val note44 = noteItself.text.toString()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val date44 = dateFormat.format(date)
            val thread = Thread(Runnable { DbHelper(this@EditActivity).updateData(id, name44, note44, date44) })
            thread.start()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        val bundle = intent.extras
        if (bundle != null) {
            var name: String? = ""
            var note: String? = ""
            if (bundle.containsKey("note")) note = bundle.getString("note")
            if (bundle.containsKey("noteName")) name = bundle.getString("noteName")
            if (bundle.containsKey("id")) id = bundle.getLong("id")
            nameOfNote.setText(name)
            noteItself.setText(note)
            noteItself.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.length != 0) textInputLayout_note_edit.setError(null)
                }

                override fun afterTextChanged(s: Editable) {}
            })
            nameOfNote.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.length != 0) textInputLayout_name_edit.setError(null)
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }
}