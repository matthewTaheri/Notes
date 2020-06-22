package com.example.a4

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.a4.Adaptor.MyViewHolder

class Adaptor internal constructor(var context: Context) : ListAdapter<DataClass, MyViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_adaptor, parent, false)
        return MyViewHolder(itemView, context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MyViewHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {
        var noteName: TextView
        var note: TextView
        var date_tv: TextView
        fun bind(dataClass: DataClass?) {
            noteName.text = dataClass!!.noteName
            note.text = dataClass.note
            date_tv.text = dataClass.date
            itemView.setOnClickListener {
                val intent = Intent(context, NoteActivity::class.java)
                intent.putExtra("id", dataClass.id)
                context.startActivity(intent)
            }
            Log.d("tag4", dataClass.noteName)
        }

        init {
            noteName = itemView.findViewById(R.id.noteName_tv)
            note = itemView.findViewById(R.id.note_tv)
            date_tv = itemView.findViewById(R.id.date_tv)
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<DataClass> = object : DiffUtil.ItemCallback<DataClass>() {
            override fun areItemsTheSame(oldItem: DataClass, newItem: DataClass): Boolean {
                return oldItem.id === newItem.id
            }

            override fun areContentsTheSame(oldItem: DataClass, newItem: DataClass): Boolean {
                return oldItem.noteName == newItem.noteName && oldItem.note == newItem.note
            }
        }
    }

}