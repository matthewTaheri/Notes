package com.example.a4;

import android.animation.Animator;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnCloseListener {
    RecyclerView recyclerView;
    Adaptor adaptor;
    DataClass dataClass;
    DbHelper dbHelper;
    String sortMode= dbHelper.COLUMN_ID + " DESC" ;
    SearchView searchView;
    Toolbar toolbar;
    List<DataClass> notesList;
    CoordinatorLayout coordinatorLayout;
    public static final float ALPHA_FULL = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        hideHomeButton();
        init();
        setSupportActionBar(toolbar);
    }


    private void list() {
        adaptor = new Adaptor(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptor);
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    Bitmap icon;

                    if (dX > 0) {
            /* Note, ApplicationManager is a helper class I created
               myself to get a context outside an Activity class -
               feel free to use your own method */

                        icon = getBitmapFromVectorDrawable(MainActivity.this,R.drawable.ic_edit);

                        /* Set your color for positive displacement */
                        p.setARGB(255, 0, 255, 0);

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);

                        // Set the image icon for Right swipe
                        c.drawBitmap(icon,
                                (float) itemView.getLeft() + convertDpToPx(16),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                p);
                    } else {
                        icon = getBitmapFromVectorDrawable(MainActivity.this,R.drawable.ic_delete);

                        /* Set your color for negative displacement */
                        p.setARGB(255, 255, 0, 0);

                        // Draw Rect with varying left side, equal to the item's right side
                        // plus negative displacement dX
                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                        //Set the image icon for Left swipe
                        c.drawBitmap(icon,
                                (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                p);
                    }

                    // Fade out the view as it is swiped out of the parent's bounds
                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            private int convertDpToPx(int dp) {
                return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            }


            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
             final int position = viewHolder.getAdapterPosition();
                final DataClass dataClass4 = notesList.get(position);
                if (direction == ItemTouchHelper.LEFT) {
                    Thread thread4 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            dbHelper.deleteData(String.valueOf(dataClass4.getId()));
                            readData();
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                            snackbar.setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Thread thread4 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                                            ContentValues contentValues = new ContentValues();
                                            contentValues.put(DbHelper.COLUMN_NAME_TITLE,dataClass4.getNoteName());
                                            contentValues.put(DbHelper.COLUMN_NAME_NOTE,dataClass4.getNote());
                                            contentValues.put(DbHelper.COLUMN_DATE,dataClass4.getDate());
                                            contentValues.put(DbHelper.COLUMN_ID,dataClass4.getId());
                                            long id =db.insert(DbHelper.TABLE_NAME,null,contentValues);
                                            Log.d("tag",String.valueOf(id));
                                            db.close();
                                            readData();
                                        }
                                    });
                                    thread4.start();
                                    recyclerView.scrollToPosition(position);
                                }
                            });
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();
                        }
                    });
                    thread4.start();
                } else {
                    Intent intent = new Intent(MainActivity.this,EditActivity.class);
                    intent.putExtra("note",dataClass4.getNote());
                    intent.putExtra("noteName",dataClass4.getNoteName());
                    intent.putExtra("id",dataClass4.getId());
                    startActivity(intent);
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, WriteActivity.class));
            }
        });
        dataClass = new DataClass();
        dbHelper = new DbHelper(this);
        coordinatorLayout = findViewById(R.id.coordi);
    }

    public void readData() {
        notesList = new ArrayList<>();
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query(dbHelper.TABLE_NAME, null, null, null,
                        null, null, sortMode);
                while (cursor.moveToNext()) {
                    String noteName = cursor.getString(
                            cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_TITLE));
                    String note = cursor.getString(
                            cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_NOTE));
                    String date = cursor.getString(
                            cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DATE));
                    Long id = cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ID));
                    DataClass dataClass = new DataClass();
                    dataClass.setNoteName(noteName);
                    dataClass.setNote(note);
                    dataClass.setId(id);
                    dataClass.setDate(date);
                    Log.d("tag5", dataClass.getNote());
                    notesList.add(dataClass);
                }
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        adaptor.submitList(notesList);
                    }
                });
                cursor.close();
                db.close();
            }
        });
        thread2.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        readData();
        list();
    }

    void searchData() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (newText != null) {
                    Thread thread3 = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final List<DataClass> notesList = new ArrayList<>();
                            SQLiteDatabase db = dbHelper.getReadableDatabase();
                            String searchText = "%" + newText + "%";
                            Cursor cursor = db.query(dbHelper.TABLE_NAME, null,
                                    dbHelper.COLUMN_NAME_TITLE + " LIKE ? OR " +
                                            dbHelper.COLUMN_NAME_NOTE + " LIKE ?", new String[]{searchText, searchText},
                                    null, null, sortMode);
                            while (cursor.moveToNext()) {
                                String noteName = cursor.getString(
                                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_TITLE));
                                String note = cursor.getString(
                                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_NOTE));
                                String date = cursor.getString(
                                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DATE));
                                Long id = cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.COLUMN_ID));
                                DataClass dataClass2 = new DataClass();
                                dataClass2.setNoteName(noteName);
                                dataClass2.setDate(date);
                                dataClass2.setNote(note);
                                dataClass2.setId(id);
                                Log.d("tag5", dataClass.getNote());
                                notesList.add(dataClass2);
                            }
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    adaptor.submitList(notesList);
                                }
                            });
                            cursor.close();
                            db.close();
                        }
                    });
                    thread3.start();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem sortItem = menu.findItem(R.id.action_sort);
        sortItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String[] sortList = getResources().getStringArray(R.array.sort_array);
                new AlertDialog.Builder(MainActivity.this).setTitle("sort by")
                        .setSingleChoiceItems(sortList,-1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) sortMode = dbHelper.COLUMN_ID + " ASC";
                        else if (which== 1) sortMode = dbHelper.COLUMN_ID + " DESC";
                        else sortMode = dbHelper.COLUMN_NAME_TITLE + " ASC";
                    }
                }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        readData();
                    }
                }).show();
                return false;
            }
        });
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnCloseListener(this);
        searchData();
        return true;
    }

    @Override
    public boolean onClose() {
        readData();
        return false;
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void hideHomeButton(){
        if (getSupportActionBar()!=null) {
            MainActivity.this.getSupportActionBar().setHomeButtonEnabled(false); // disable the button
            MainActivity.this.getSupportActionBar().setDisplayHomeAsUpEnabled(false); // remove the left caret
            MainActivity.this.getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }
}
