package com.example.a4

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main3.*
import java.util.*

class MainActivity : AppCompatActivity(), SearchView.OnCloseListener {
    lateinit var adaptor: Adaptor
    lateinit var notesDataClass: Notes
    lateinit var dbHelper: DbHelper
    var sortMode = DbHelper.COLUMN_ID + " DESC"
    var searchView: SearchView? = null
    var notesList: ArrayList<Notes>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        hideHomeButton()
        init()
        setSupportActionBar(toolbarMain)
    }

    private fun list() {
        adaptor = Adaptor(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adaptor
        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    val itemView = viewHolder.itemView
                    val p = Paint()
                    val icon: Bitmap
                    if (dX > 0) {
                        /* Note, ApplicationManager is a helper class I created
               myself to get a context outside an Activity class -
               feel free to use your own method */
                        icon = getBitmapFromVectorDrawable(this@MainActivity, R.drawable.ic_edit)

                        /* Set your color for positive displacement */p.setARGB(255, 0, 255, 0)

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), dX,
                                itemView.bottom.toFloat(), p)

                        // Set the image icon for Right swipe
                        c.drawBitmap(icon,
                                itemView.left.toFloat() + convertDpToPx(16),
                                itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height) / 2,
                                p)
                    } else {
                        icon = getBitmapFromVectorDrawable(this@MainActivity, R.drawable.ic_delete)

                        /* Set your color for negative displacement */p.setARGB(255, 255, 0, 0)

                        // Draw Rect with varying left side, equal to the item's right side
                        // plus negative displacement dX
                        c.drawRect(itemView.right.toFloat() + dX, itemView.top.toFloat(),
                                itemView.right.toFloat(), itemView.bottom.toFloat(), p)

                        //Set the image icon for Left swipe
                        c.drawBitmap(icon,
                                itemView.right.toFloat() - convertDpToPx(16) - icon.width,
                                itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height) / 2,
                                p)
                    }

                    // Fade out the view as it is swiped out of the parent's bounds
                    val alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
                    viewHolder.itemView.alpha = alpha
                    viewHolder.itemView.translationX = dX
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

            private fun convertDpToPx(dp: Int): Int {
                return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val dataClass4 = notesList!![position]
                if (direction == ItemTouchHelper.LEFT) {
                    val thread4 = Thread(Runnable {
                        dbHelper.deleteData(dataClass4.id.toString())
                        readData()
                        val snackbar = Snackbar
                                .make(coordi, "Item was removed from the list.", Snackbar.LENGTH_LONG)
                        snackbar.setAction("UNDO") {
                            val thread4 = Thread(Runnable {
                                val db = dbHelper.writableDatabase
                                val contentValues = ContentValues().apply {
                                    put(DbHelper.COLUMN_NAME_TITLE, dataClass4.noteName)
                                    put(DbHelper.COLUMN_NAME_NOTE, dataClass4.note)
                                    put(DbHelper.COLUMN_IMAGE_URI, dataClass4.imageUri)
                                    put(DbHelper.COLUMN_DATE, dataClass4.date)
                                    put(DbHelper.COLUMN_ID, dataClass4.id)
                                }
                                val id = db.insert(DbHelper.TABLE_NAME, null, contentValues)
                                Log.d("tag", id.toString())
                                db.close()
                                readData()
                            })
                            thread4.start()
                            recyclerView.scrollToPosition(position)
                        }
                        snackbar.setActionTextColor(Color.YELLOW)
                        snackbar.show()
                    })
                    thread4.start()
                } else {
                    val intent = Intent(this@MainActivity, EditActivity::class.java).apply {
                        putExtra("note", dataClass4.note)
                        if (dataClass4.imageUri != null) putExtra("uri", dataClass4.imageUri)
                        putExtra("noteName", dataClass4.noteName)
                        putExtra("id", dataClass4.id)
                    }
                    startActivity(intent)
                }
            }
        })
        helper.attachToRecyclerView(recyclerView)
    }

    private fun init() {
        fab.setOnClickListener { startActivity(Intent(this@MainActivity, WriteActivity::class.java)) }
        notesDataClass = Notes()
        dbHelper = DbHelper(this)
    }

    fun readData() {
        notesList = ArrayList()
        val thread2 = Thread(Runnable {
            val db = dbHelper.readableDatabase
            val cursor = db.query(DbHelper.TABLE_NAME, null, null, null,
                    null, null, sortMode)
            while (cursor.moveToNext()) {
                val noteNameString = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_TITLE))
                val noteString = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_NOTE))
                val dateString = cursor.getString(
                        cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DATE))
                val idLong = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID))
                val uriString: String? = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IMAGE_URI))
                val dataClass = Notes().apply {
                    noteName = noteNameString
                    note = noteString
                    id = idLong
                    date = dateString
                    imageUri = uriString
                }
                Log.d("tag5", dataClass.note)
                notesList!!.add(dataClass)
            }
            runOnUiThread { adaptor.submitList(notesList) }
            cursor.close()
            db.close()
        })
        thread2.start()
    }

    override fun onResume() {
        super.onResume()
        readData()
        list()
    }

    private fun searchData() {
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText != null) {
                    val thread3 = Thread(Runnable {
                        val notesList: MutableList<Notes> = ArrayList()
                        val db = dbHelper.readableDatabase
                        val searchText = "%$newText%"
                        val cursor = db.query(DbHelper.TABLE_NAME, null,
                                DbHelper.COLUMN_NAME_TITLE + " LIKE ? OR " +
                                        DbHelper.COLUMN_NAME_NOTE + " LIKE ?", arrayOf(searchText, searchText),
                                null, null, sortMode)
                        while (cursor.moveToNext()) {
                            val noteName = cursor.getString(
                                    cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_TITLE))
                            val note = cursor.getString(
                                    cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NAME_NOTE))
                            val date = cursor.getString(
                                    cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DATE))
                            val id = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID))
                            val dataClass2 = Notes()
                            dataClass2.noteName = noteName
                            dataClass2.date = date
                            dataClass2.note = note
                            dataClass2.id = id
                            Log.d("tag5", notesDataClass.note)
                            notesList.add(dataClass2)
                        }
                        runOnUiThread { adaptor.submitList(notesList) }
                        cursor.close()
                        db.close()
                    })
                    thread3.start()
                }
                return false
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val sortItem = menu.findItem(R.id.action_sort)
        sortItem.setOnMenuItemClickListener {
            val sortList = resources.getStringArray(R.array.sort_array)
            AlertDialog.Builder(this@MainActivity).setTitle("sort by")
                    .setSingleChoiceItems(sortList, -1) { dialog, which -> sortMode = if (which == 0) DbHelper.COLUMN_ID + " ASC" else if (which == 1) DbHelper.COLUMN_ID + " DESC" else DbHelper.COLUMN_NAME_TITLE + " ASC" }.setPositiveButton("ok") { dialog, which -> readData() }.show()
            false
        }
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView!!.setOnCloseListener(this)
        searchData()
        return true
    }

    override fun onClose(): Boolean {
        readData()
        return false
    }

    private fun hideHomeButton() {
        if (supportActionBar != null) {
            this@MainActivity.supportActionBar!!.setHomeButtonEnabled(false) // disable the button
            this@MainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(false) // remove the left caret
            this@MainActivity.supportActionBar!!.setDisplayShowHomeEnabled(false)
        }
    }

    companion object {
        const val ALPHA_FULL = 1.0f
        fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
            var drawable = ContextCompat.getDrawable(context!!, drawableId)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = DrawableCompat.wrap(drawable!!).mutate()
            }
            val bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth,
                    drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }
    }
}