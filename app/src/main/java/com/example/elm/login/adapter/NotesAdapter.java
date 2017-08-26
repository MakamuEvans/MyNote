package com.example.elm.login.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elm.login.AddNote;
import com.example.elm.login.FullNote;
import com.example.elm.login.Navigation;
import com.example.elm.login.R;
import com.example.elm.login.model.Note;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by elm on 7/17/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private static final int EMPTY_VIEW = 1;
    private static final int DATA_VIEW = 2;
    public List<Note> allnotes;

    public NotesAdapter(List<Note> allnotes) {
        this.allnotes = allnotes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        if (viewType == EMPTY_VIEW){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_zero, parent, false);
            holder = new emptyViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_note_card, parent, false);
            holder = new myViewHolder(view);
        }
        return  holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //context = holder
        int viewType = getItemViewType(position);
        if (viewType == EMPTY_VIEW){

        }else {
            myViewHolder holder1 = (myViewHolder) holder;
            Note notes = allnotes.get(position);
            holder1.title.setText(notes.getTitle());
            holder1.dated.setText(notes.getCreated_at());
            String htmlText = notes.getNote();
            if (htmlText != null){
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    holder1.note.setText(Html.fromHtml(htmlText,Html.FROM_HTML_MODE_LEGACY));
                } else {
                    holder1.note.setText(Html.fromHtml(htmlText));
                }
            }
            if (notes.getUploadflag() || notes.getFavaouriteflag() || notes.getUpdateflag()){
                holder1.imageView.setImageResource(R.mipmap.ic_cloud);
            }else {
                holder1.imageView.setImageResource(R.mipmap.ic_cloud_done);
            }
            if (notes.getFavourite()){
                holder1.fav.setImageResource(R.mipmap.ic_action_pink);
            }else {
                holder1.fav.setImageResource(R.mipmap.ic_action_favorite_pink);
            }
            setFadeAnimation(holder.itemView);
        }
    }

    private void setFadeAnimation(View view){
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        view.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        if (allnotes.size() == 0){
            return 1;
        }else {
            return allnotes.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (allnotes.size() == 0){
            return EMPTY_VIEW;
        }else {
            return DATA_VIEW;
        }
    }

    public class emptyViewHolder extends RecyclerView.ViewHolder{

        public emptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView title, note, dated;
        public ImageView imageView,fav, del;

        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            note = (TextView) itemView.findViewById(R.id.card_notes);
            dated = (TextView) itemView.findViewById(R.id.card_dated);
            imageView = (ImageView) itemView.findViewById(R.id.uploadstatus);
            fav = (ImageView) itemView.findViewById(R.id.card_fav);
            del = (ImageView) itemView.findViewById(R.id.card_del);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Note note = allnotes.get(pos);
                    Intent intent = new Intent(v.getContext(), FullNote.class);
                    intent.putExtra("noteId", note.getId());
                    v.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return false;
                }
            });

            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Note note = allnotes.get(pos);
                    Note note1 = Note.findById(Note.class, note.getId());
                    note1.setFavaouriteflag(true);
                    Boolean status = note1.getFavourite() == false ? true:false;
                    note1.setFavourite(status);
                    note1.save();

                    if (note1.getFavourite()){
                        fav.setImageResource(R.mipmap.ic_action_pink);
                        Toast.makeText(v.getContext(), note.getTitle()+" added to favourite", Toast.LENGTH_SHORT).show();
                    }else {
                        fav.setImageResource(R.mipmap.ic_action_favorite_pink);
                        Toast.makeText(v.getContext(), note.getTitle()+" removed from favourite", Toast.LENGTH_SHORT).show();
                    }

                    Intent intent = new Intent();
                    intent.setAction("favourite");
                    v.getContext().sendBroadcast(intent);
                }
            });

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Note note = allnotes.get(pos);
                    delete(pos, note.getId(),v);
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
        notifyItemRangeChanged(0, allnotes.size());
        notifyItemInserted(0);
    }

    public void updateItem(Note note){
        Log.e("atview", String.valueOf(note.getId()));
        Note data = null;
        for (Note n: allnotes){
            Log.e("id", String.valueOf(n.getId()));
            if (n.getId().equals(note.getId())){
                Log.e("found", String.valueOf(allnotes.indexOf(n)));
                int position = allnotes.indexOf(n);
                notifyItemChanged(position, note);
                break;
            }
        }
    }

    public void removeItem(int position){
        allnotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, allnotes.size());
    }

    public void delete(final int pos, final Long id, final View view){
        final Note note = Note.findById(Note.class, id);

        new AlertDialog.Builder(view.getContext())
                .setTitle("Are you sure?")
                .setMessage("You are about to delete Note "+note.getTitle())
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        note.setDeleteflag(true);
                        note.save();

                        removeItem(pos);

                        Intent intent = new Intent();
                        intent.setAction("delete");
                        view.getContext().sendBroadcast(intent);
                    }
                })
                .create()
                .show();
    }

    public void deleteNote(Note note){
        Note data = null;
        for (Note n: allnotes){
            Log.e("id", String.valueOf(n.getId()));
            if (n.getId().equals(note.getId())){
                Log.e("found", String.valueOf(allnotes.indexOf(n)));
                int position = allnotes.indexOf(n);
                removeItem(position);
                break;
            }
        }
    }


}
