package com.example.a4

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a4.Adaptor.MyViewHolder
import kotlinx.android.synthetic.main.cardview_adaptor.view.*
import java.io.File

class Adaptor internal constructor(var context: Context) : ListAdapter<Notes, MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_adaptor, parent, false)
        return MyViewHolder(itemView, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        fun bind(notesDataClass: Notes) {
            itemView.noteName_tv.text = notesDataClass.noteName
            itemView.note_tv.text = notesDataClass.note
            itemView.date_tv.text = notesDataClass.date
            if (notesDataClass.imageUri == null) itemView.noteImageView.visibility = View.GONE
            else {
                val cr: ContentResolver = context.contentResolver
                val projection = arrayOf(MediaStore.MediaColumns.DATA)
                var cur: Cursor? = cr.query(Uri.parse(notesDataClass.imageUri), projection, null, null, null)
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        var filePath = cur.getString(0)

                        if (File(filePath).exists()) {
                            // do something if it exists
                            Glide.with(context).load(Uri.parse(notesDataClass.imageUri)).into(itemView.noteImageView)
                            itemView.noteImageView.visibility = View.VISIBLE
                        } else {
                            // File was not found
                            itemView.noteImageView.visibility = View.GONE
                        }
                    } else {
                        // Uri was ok but no entry found.
                        itemView.noteImageView.visibility = View.GONE
                    }
                    cur.close()
                } else {
                    // content Uri was invalid or some other error occurred
                    itemView.noteImageView.visibility = View.GONE
                }
            }


            itemView.setOnClickListener {
                val intent = Intent(context, NoteActivity::class.java)
                intent.putExtra("id", notesDataClass.id)
                context.startActivity(intent)
            }
        }

    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<Notes> = object : DiffUtil.ItemCallback<Notes>() {
            override fun areItemsTheSame(oldItem: Notes, newItem: Notes): Boolean {
                return oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: Notes, newItem: Notes): Boolean {
                return oldItem.noteName == newItem.noteName && oldItem.note == newItem.note
            }
        }
    }

}