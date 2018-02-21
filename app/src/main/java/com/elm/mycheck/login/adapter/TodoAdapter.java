package com.elm.mycheck.login.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.elm.mycheck.login.R;
import com.elm.mycheck.login.ToDoDetails;
import com.elm.mycheck.login.model.Milestones;
import com.elm.mycheck.login.model.Todo;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

/**
 * Created by elm on 8/16/17.
 */

public class TodoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int EMPTY_VIEW = 1;
    private static final int DATA_VIEW = 2;
    public List<Todo> allToDo;

    public TodoAdapter(List<Todo> allToDo) {
        this.allToDo = allToDo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       if (viewType != EMPTY_VIEW) {
           View view = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.todo_card, parent, false);
           return new myViewHolder(view);
       }else {
           View view = LayoutInflater.from(parent.getContext())
                   .inflate(R.layout.todo_zero, parent, false);
           return new emptyViewHolder(view);
       }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder  holder1, int position) {
        int viewType = getItemViewType(position);

        if (viewType == EMPTY_VIEW) {

        }else {
            myViewHolder holder = (myViewHolder) holder1;
            Todo todo = allToDo.get(position);
            holder.title.setText(todo.getTitle());
            Long total = Select.from(Milestones.class)
                    .where(Condition.prop("todoid").eq(todo.getId()))
                    .count();
            Long completed = Select.from(Milestones.class)
                    .where(Condition.prop("todoid").eq(todo.getId()))
                    .where(Condition.prop("status").eq(1))
                    .count();

            ColorStateList oldColor = holder.total_count.getTextColors();

            if (total == completed){
                holder.title.setTextColor(holder.itemView.getResources().getColor(R.color.colorPrimary));
            }else {
                holder.title.setTextColor(oldColor);
            }
            holder.total_count.setText(total.toString());
            holder.completed_count.setText(completed.toString());

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
        if (allToDo.size() == 0)
            return 1;
        return allToDo.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (allToDo.size() == 0){
            return EMPTY_VIEW;
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
        TextView title,total_count, completed_count;
        ImageView delete;
        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.todo_card_title);
            delete = (ImageView) itemView.findViewById(R.id.card_del);
            total_count = (TextView) itemView.findViewById(R.id.total_count);
            completed_count = (TextView) itemView.findViewById(R.id.completed_count);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Todo todo = allToDo.get(pos);
                    Intent intent = new Intent(v.getContext(), ToDoDetails.class);
                    intent.putExtra("id", todo.getId());
                    v.getContext().startActivity(intent);
                }
            });
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int pos = getLayoutPosition();
                    final Todo todo = allToDo.get(pos);

                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Are you sure?")
                            .setMessage("You are about to delete Todo list " + todo.getTitle())
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //delete todo
                                    todo.delete();
                                    //delete item list
                                    Milestones.executeQuery("DELETE FROM milestones WHERE todoid = '" + todo.getId() + "'");

                                    removeTodo(pos);
                                }
                            })
                            .create()
                            .show();


                }
            });
        }
    }

    //CRUD
    public void removeTodo(final int pos){
        allToDo.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, allToDo.size());
    }


    public void insert(Todo todo){
        this.allToDo.add(0, todo);
        notifyItemInserted(0);
        this.notifyItemRangeChanged(0, allToDo.size());
    }

    public void updateItem(Long id){
        for (Todo n: allToDo){
            if (n.getId().equals(id)){
                int position = allToDo.indexOf(n);
                Todo todo = Todo.findById(Todo.class, id);
                notifyItemChanged(position, todo);
                break;
            }
        }
    }
}
