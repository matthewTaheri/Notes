package com.example.a4;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;


public class Adaptor extends ListAdapter<DataClass,Adaptor.MyViewHolder> {
    Context context;

    Adaptor( Context context){
        super(DIFF_CALLBACK);
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<DataClass> DIFF_CALLBACK = new DiffUtil.ItemCallback<DataClass>() {
        @Override
        public boolean areItemsTheSame(@NonNull DataClass oldItem, @NonNull DataClass newItem) {
            return oldItem.getId()==newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DataClass oldItem, @NonNull DataClass newItem) {
            return oldItem.getNoteName().equals(newItem.getNoteName())&&
                    oldItem.getNote().equals(newItem.getNote());
        }
    };

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cardview_adaptor,parent,false);
        return new MyViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


     class MyViewHolder extends RecyclerView.ViewHolder  {
        TextView noteName,note,date_tv;
        Context context ;

         MyViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            noteName = itemView.findViewById(R.id.noteName_tv);
            note = itemView.findViewById(R.id.note_tv);
            date_tv = itemView.findViewById(R.id.date_tv);
        }
        void bind(final DataClass dataClass){
            noteName.setText(dataClass.getNoteName());
            note.setText(dataClass.getNote());
            date_tv.setText(dataClass.getDate());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,NoteActivity.class);
                    intent.putExtra("id",dataClass.getId());
                    context.startActivity(intent);
                }
            });
            Log.d("tag4",dataClass.getNoteName());
        }

     }


}
