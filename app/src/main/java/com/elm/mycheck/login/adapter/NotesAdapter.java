package com.elm.mycheck.login.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elm.mycheck.login.FullNote;
import com.elm.mycheck.login.R;
import com.elm.mycheck.login.model.Category;
import com.elm.mycheck.login.model.Note;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.List;

/**
 * Created by elm on 7/17/17.
 */

public class NotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private static final int EMPTY_VIEW = 1;
    private static final int DATA_VIEW = 2;
    private static final int AD_VIEW = 3;
    public List<Note> allnotes;
    private String add = "ca-app-pub-3940256099942544/6300978111";

    public NotesAdapter(List<Note> allnotes, Context context) {
        this.allnotes = allnotes;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;
        if (viewType == EMPTY_VIEW){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.note_zero, parent, false);
            holder = new emptyViewHolder(view);
            return  holder;
        }else if (viewType ==AD_VIEW){
            AdView adView = new AdView(context);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(add);
            float density = context.getResources().getDisplayMetrics().density;
            int height = Math.round(AdSize.BANNER.getHeight()*density);
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, height);
            adView.setLayoutParams(params);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            view = adView;
            holder = new emptyViewHolder(view);
            return  holder;
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_note_card, parent, false);
            holder = new myViewHolder(view);
            return  holder;
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //context = holder
        int viewType = getItemViewType(position);
        if (viewType == EMPTY_VIEW || viewType == AD_VIEW){

        }else {
            myViewHolder holder1 = (myViewHolder) holder;
            Note notes = allnotes.get(position);
            holder1.title.setText(notes.getTitle());
            holder1.dated.setText(notes.getUpdated_at());
            if (notes.getCategory() == null){
                holder1.category_layer.setVisibility(View.GONE);
            }else {
                holder1.category_layer.setVisibility(View.VISIBLE);
                Category category = Category.findById(Category.class, notes.getCategory());
                holder1.category.setText(category.getTitle());
            }
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
        } else if (allnotes.get(position) == null){
            return AD_VIEW;
        }  else {
            return DATA_VIEW;
        }
    }

    public class emptyViewHolder extends RecyclerView.ViewHolder{

        public emptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        public TextView title, note, dated,category;
        public ImageView imageView,fav, del;
        public LinearLayout category_layer;

        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            note = (TextView) itemView.findViewById(R.id.card_notes);
            dated = (TextView) itemView.findViewById(R.id.card_dated);
            category = (TextView) itemView.findViewById(R.id.notes_category);
            imageView = (ImageView) itemView.findViewById(R.id.uploadstatus);
            fav = (ImageView) itemView.findViewById(R.id.card_fav);
            del = (ImageView) itemView.findViewById(R.id.card_del);
            category_layer = (LinearLayout) itemView.findViewById(R.id.category_layer);

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
                        MDToast.makeText(v.getContext(),note.getTitle()+" added to favourite", MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS ).show();
                        //Toast.makeText(v.getContext(), note.getTitle()+" added to favourite", Toast.LENGTH_SHORT).show();
                    }else {
                        fav.setImageResource(R.mipmap.ic_action_favorite_pink);
                        MDToast.makeText(v.getContext(),note.getTitle()+" removed from favourite", MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
                        //Toast.makeText(v.getContext(), note.getTitle()+" removed from favourite", Toast.LENGTH_SHORT).show();
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

    public void newData(Note note, Integer position){
        if (position == null){
            this.allnotes.add(0, note);
            notifyItemRangeChanged(0, allnotes.size());
            notifyItemInserted(0);
        }else {
            this.allnotes.add(position, note);
            notifyItemRangeChanged(position, allnotes.size());
            notifyItemInserted(position);
        }
    }

    public void updateItem(Note note){
        for (Note n: allnotes){
            if (n.getId().equals(note.getId())){
                int position = allnotes.indexOf(n);
                /*Note update = Note.findById(Note.class, note.getId());
                removeItem(position);
                newData(update, position);*/
                //int position = allnotes.indexOf(r);
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
