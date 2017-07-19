package com.example.elm.login.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elm.login.R;
import com.example.elm.login.model.Note;

import java.util.List;

/**
 * Created by elm on 7/17/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.myViewHolder> {
    Context context;
    public List<Note> allnotes;

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
    }

    @Override
    public int getItemCount() {
        return allnotes.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView title, note;

        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            note = (TextView) itemView.findViewById(R.id.card_note);
        }
    }

    public void swapAll(List<Note> notes){
        allnotes.clear();
        allnotes.addAll(notes);
        this.notifyDataSetChanged();
    }
}
