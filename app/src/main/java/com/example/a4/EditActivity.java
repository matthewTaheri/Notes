package com.example.a4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {
    EditText nameOfNote, noteItself;
    TextInputLayout editNoteLayout,editNameLayout;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit2);
        init();
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        if (item.getItemId() == R.id.action_edit) {
            if  (nameOfNote.length() == 0) {
                editNameLayout.setError("enter name of your note");
                return false;
            }
            if (noteItself.length() == 0){
                editNoteLayout.setError("enter your note");
                return false;
            }
            final String name44 = nameOfNote.getText().toString().trim();
            final String note44 = noteItself.getText().toString();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            final String date44 = dateFormat.format(date);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    new DbHelper(EditActivity.this).updateData(id, name44, note44, date44);
                }
            });
            thread.start();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        nameOfNote = findViewById(R.id.nameOfNote);
        noteItself = findViewById(R.id.noteItself);
        editNameLayout = findViewById(R.id.textInputLayout_name_edit);
        editNoteLayout = findViewById(R.id.textInputLayout_note_edit);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            String name = "";
            String note = "";
            if (bundle.containsKey("note")) note = bundle.getString("note");
            if (bundle.containsKey("noteName")) name = bundle.getString("noteName");
            if (bundle.containsKey("id")) id = bundle.getLong("id");
            nameOfNote.setText(name);
            noteItself.setText(note);
            noteItself.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() !=0) editNoteLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            nameOfNote.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length()!=0) editNameLayout.setError(null);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }
}
