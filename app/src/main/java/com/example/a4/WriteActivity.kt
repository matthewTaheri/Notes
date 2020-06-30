package com.example.a4

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_write2.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WriteActivity : AppCompatActivity() {
    private var notesDataClass: Notes = Notes()
    val PICK_IMAGE = 100
    private var imageUriWrite: Uri? = null
    val PREQCODE = 1
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
            if (noteName_textInput.length() == 0) {
                textInputLayout_name.error = "enter name of your note"
                return false
            }
            if (note_textInout.length() == 0) {
                textInputLayout_note.error = "enter your note"
                return false
            }
            val name = noteName_textInput.text.toString().trim { it <= ' ' }
            val context = note_textInout.text.toString()
            val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date()
            val date2 = dateFormat.format(date)
            notesDataClass.note = context
            notesDataClass.noteName = name
            notesDataClass.date = date2
            if (imageUriWrite != null) notesDataClass.imageUri = imageUriWrite.toString()
            Log.d("sagibug", notesDataClass.noteName)
            writeData()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        writeActivityCheckBox.setOnCheckedChangeListener { _, isChecked ->
            writeActivityAddImageView.alpha = 1f
            if (!isChecked) {
                writeActivityAddImageView.alpha = 0.5f
                writeActivityImageView.visibility = View.GONE
            }
        }
        writeActivityAddImageView.setOnClickListener {
            if (writeActivityCheckBox.isChecked) {
                if (Build.VERSION.SDK_INT >= 22) {
                    requestForPermission()
                } else openGallery()
            }
        }
        note_textInout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) textInputLayout_note.error = null
            }

            override fun afterTextChanged(s: Editable) {}
        })
        noteName_textInput!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) textInputLayout_name!!.error = null
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    fun requestForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "please accept permission", Toast.LENGTH_SHORT).show()
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PREQCODE)
            }
        } else openGallery()

    }

    fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            imageUriWrite = data?.data
            Glide.with(this).load(imageUriWrite).into(writeActivityImageView)
            writeActivityImageView.visibility = View.VISIBLE
        }
    }

    private fun writeData() {
        val dbHelper = DbHelper(this)
        val thread = Thread(Runnable {
            val db = dbHelper.writableDatabase
            val contentValues = ContentValues().apply {
                put(DbHelper.COLUMN_NAME_TITLE, notesDataClass.noteName)
                put(DbHelper.COLUMN_NAME_NOTE, notesDataClass.note)
                put(DbHelper.COLUMN_DATE, notesDataClass.date)
                put(DbHelper.COLUMN_IMAGE_URI, notesDataClass.imageUri)
            }
            val id = db.insert(DbHelper.TABLE_NAME, null, contentValues)
            Log.d("sagibug", id.toString())
            db.close()
        })
        thread.start()
    }
}