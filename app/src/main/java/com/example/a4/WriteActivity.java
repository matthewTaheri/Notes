package com.example.a4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    EditText noteName, note;
    DataClass dataClass;
    TextInputLayout nameLayout,noteLayout;
    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write2);
        init();
        if (getSupportActionBar()!=null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.write_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()== android.R.id.home) finish();
        if (item.getItemId()==R.id.action_save){
                if  (noteName.length() == 0) {
                    nameLayout.setError("enter name of your note");
                return false;
                }
                if (note.length() == 0){
                    noteLayout.setError("enter your note");
                    return false;
                }
                String name = noteName.getText().toString().trim();
                String context = note.getText().toString();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                String date2 = dateFormat.format(date);
                dataClass.setNote(context);
                dataClass.setNoteName(name);
                dataClass.setDate(date2);
                Log.d("tag",dataClass.getNoteName());
                writeData();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        note = findViewById(R.id.note);
        noteName = findViewById(R.id.noteName);
        dataClass = new DataClass();
        mainActivity = new MainActivity();
        nameLayout = findViewById(R.id.textInputLayout_name);
        noteLayout = findViewById(R.id.textInputLayout_note);
        note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              if (s.length() !=0) noteLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        noteName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()!=0) nameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void writeData() {
        final DbHelper dbHelper = new DbHelper(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put(DbHelper.COLUMN_NAME_TITLE,dataClass.getNoteName());
                contentValues.put(DbHelper.COLUMN_NAME_NOTE,dataClass.getNote());
                contentValues.put(DbHelper.COLUMN_DATE,dataClass.getDate());
                long id =db.insert(DbHelper.TABLE_NAME,null,contentValues);
                Log.d("tag",String.valueOf(id));
                db.close();
            }
        });
        thread.start();
    }
}
