package com.example.elm.login.adapter;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.example.elm.login.R;
import com.example.elm.login.ToDoDetails;
import com.example.elm.login.model.Milestones;
import com.example.elm.login.model.Note;
import com.example.elm.login.model.Todo;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import static com.example.elm.login.R.color.colorPrimary;

/**
 * Created by elm on 8/16/17.
 */

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.myViewHolder> {
    public List<Todo> allToDo;

    public TodoAdapter(List<Todo> allToDo) {
        this.allToDo = allToDo;
    }

    @Override
    public TodoAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todo_card, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoAdapter.myViewHolder holder, int position) {
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

    private void setFadeAnimation(View view){
        ScaleAnimation animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(800);
        view.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return allToDo.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title,total_count, completed_count;
        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.todo_card_title);
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
        }
    }

    //CRUD
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
