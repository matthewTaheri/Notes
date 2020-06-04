package com.example.a4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NoteActivity extends AppCompatActivity {
    long id;
    DbHelper dbHelper;
    TextView noteTv,dateTv;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String noteName,note,date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        readData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_note_delete){
            new AlertDialog.Builder(this).setMessage("Do you want to delete this ?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dbHelper.deleteData(String.valueOf(id));
                        }
                    });
                    thread.start();
                    finish();
                }
            }).setNegativeButton("no",null).show();

        }
        return super.onOptionsItemSelected(item);
    }

    void init(){
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            if (bundle.containsKey("id")){
                id=bundle.getLong("id");
            }
        }
        dbHelper = new DbHelper(this);
        noteTv= findViewById(R.id.note_textView);
        dateTv = findViewById(R.id.date_tv);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        FloatingActionButton fab = findViewById(R.id.edit_fb);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this,EditActivity.class);
                intent.putExtra("note",note);
                intent.putExtra("noteName",noteName);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });
    }

    public  void readData(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String selection = dbHelper.COLUMN_ID + " =?" ;
                String[] selectionArgs = { String.valueOf(id)};
                Cursor cursor=  db.query(dbHelper.TABLE_NAME,null,selection,selectionArgs,
                        null,null,null);
                while (cursor.moveToNext()) {
                     noteName = cursor.getString(
                            cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_TITLE));
                     note = cursor.getString(
                            cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_NOTE));
                     date = cursor.getString(
                            cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DATE));
                }
                NoteActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        collapsingToolbarLayout.setTitle(noteName);
                        noteTv.setText(note);
                        dateTv.setText(date);
                    }
                });
                cursor.close();
                db.close();
            }
        });
        thread.start();
    }
}
