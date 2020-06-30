package com.example.a4

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_edit2.*
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    val PICK_IMAGE = 100
    private var imageUriEdit: Uri? = null
    val PREQCODE = 1
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
            if (!editActivityCheckBox.isChecked) imageUriEdit = null
            val uri44: String? = imageUriEdit.toString()
            val thread = Thread(Runnable { DbHelper(this@EditActivity).updateData(id, name44, note44, date44, uri44) })
            thread.start()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init() {
        editActivityCheckBox.setOnCheckedChangeListener { _, isChecked ->
            editActivityAddImageView.alpha = 1f
            if (!isChecked) {
                editActivityAddImageView.alpha = 0.5f
                editActivityImageView.visibility = View.GONE
            }
        }
        val bundle = intent.extras
        if (bundle != null) {
            var name: String? = ""
            var note: String? = ""
            var uriString: String? = ""
            if (bundle.containsKey("note")) note = bundle.getString("note")
            if (bundle.containsKey("noteName")) name = bundle.getString("noteName")
            if (bundle.containsKey("uri")) {
                uriString = bundle.getString("uri")
                imageUriEdit = Uri.parse(uriString)
                val cr: ContentResolver = this.contentResolver
                val projection = arrayOf(MediaStore.MediaColumns.DATA)
                var cur: Cursor? = cr.query(Uri.parse(uriString), projection, null, null, null)
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        var filePath = cur.getString(0)

                        if (File(filePath).exists()) {
                            // do something if it exists
                            Glide.with(this).load(Uri.parse(uriString)).into(editActivityImageView)
                            editActivityImageView.visibility = View.VISIBLE
                        } else {
                            // File was not found
                            editActivityImageView.visibility = View.GONE
                        }
                    } else {
                        // Uri was ok but no entry found.
                        editActivityImageView.visibility = View.GONE
                    }
                    cur.close()
                } else {
                    // content Uri was invalid or some other error occurred
                    editActivityImageView.visibility = View.GONE
                }
            }
            if (bundle.containsKey("id")) id = bundle.getLong("id")
            Glide.with(this).load(Uri.parse(uriString)).into(editActivityImageView)
            editActivityAddImageView.setOnClickListener {
                if (editActivityCheckBox.isChecked) {
                    if (Build.VERSION.SDK_INT >= 22) {
                        requestForPermission()
                    } else openGallery()
                }
            }
            nameOfNote.setText(name)
            noteItself.setText(note)
            noteItself.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty()) textInputLayout_note_edit.error = null
                }

                override fun afterTextChanged(s: Editable) {}
            })
            nameOfNote.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty()) textInputLayout_name_edit.error = null
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
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
            imageUriEdit = data?.data
            Glide.with(this).load(imageUriEdit).into(editActivityImageView)
            editActivityImageView.visibility = View.VISIBLE
        }
    }
}