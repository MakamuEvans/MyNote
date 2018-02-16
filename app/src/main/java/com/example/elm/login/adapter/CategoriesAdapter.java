package com.example.elm.login.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.elm.login.R;
import com.example.elm.login.ToDoDetails;
import com.example.elm.login.model.Category;
import com.example.elm.login.model.Milestones;
import com.example.elm.login.model.Note;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elm on 8/16/17.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.myViewHolder> {
    public List<Category> allCategories;

    public CategoriesAdapter(List<Category> allCategories) {
        this.allCategories = allCategories;
    }

    @Override
    public CategoriesAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.catogory_card, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.myViewHolder holder, int position) {
        Category category = allCategories.get(position);
        holder.title.setText(category.getTitle());
    }

    @Override
    public int getItemCount() {
        return allCategories.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView delete;
        public myViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.category_title);
            delete = (ImageView) itemView.findViewById(R.id.category_del);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final int pos = getLayoutPosition();
                    final Category category = allCategories.get(pos);

                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Are you sure?")
                            .setMessage("You are about to delete Category "+category.getTitle())
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    category.delete();
                                    removeCategory(pos, category.getId(), view.getContext());

                                    List<Note> notes = new ArrayList<>();
                                    notes = Select.from(Note.class)
                                            .where(Condition.prop("deleteflag").eq(0))
                                            .where(Condition.prop("category").eq(category.getId()))
                                            .orderBy("Id DESC")
                                            .list();

                                    for (Note note:notes
                                            ) {
                                        note.setCategory(null);
                                        note.save();
                                    }
                                }
                            })
                            .create()
                            .show();

                }
            });
        }
    }

    //CRUD
   /* public void insert(Milestones milestones){
        this.allTasks.add(allTasks.size(), milestones);
        notifyItemInserted(allTasks.size());
        this.notifyItemRangeChanged(allTasks.size(), allTasks.size());
    }*/
    public void update(Milestones milestones){

    }

    public void removeCategory(int position, Long id, Context context){
        allCategories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, allCategories.size());
    }

    public void delete(int position){

    }
}
