package com.example.elm.login.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elm.login.R;
import com.example.elm.login.model.Note;

import java.util.HashMap;
import java.util.List;

/**
 * Created by elm on 7/17/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.myViewHolder> {
    Context context;
    public HashMap<Integer, Note> allnotes;

    public NotesAdapter(List<Note> allnotes) {
        this.allnotes = allnotes;
    }

    @Override
    public NotesAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_note_card, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesAdapter.myViewHolder holder, int position) {
        //context = holder
        Note notes = allnotes.get(position);
        holder.title.setText(notes.getTitle());
        holder.note.setText(notes.getNote());
        if (notes.getUploadflag()){
            holder.imageView.setImageResource(R.mipmap.ic_cloud);
        }else {
            holder.imageView.setImageResource(R.mipmap.ic_cloud_done);
        }
    }

    @Override
    public int getItemCount() {
        return allnotes.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView title, note;
        public ImageView imageView;

        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            note = (TextView) itemView.findViewById(R.id.card_note);
            imageView = (ImageView) itemView.findViewById(R.id.uploadstatus);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    allnotes.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, allnotes.size());
                }
            });
        }
    }

    public void swapAll(List<Note> notes){
        allnotes.clear();
        allnotes.addAll(notes);
        this.notifyDataSetChanged();
    }

    public void newData(Note note){
        this.allnotes.add(0, note);
        notifyItemInserted(0);
    }

    public void updateItem(Note note){
        this.allnotes.equals(note);
    }
}
