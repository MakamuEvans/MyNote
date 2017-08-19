package com.example.elm.login.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elm.login.R;
import com.example.elm.login.ToDoDetails;
import com.example.elm.login.model.Todo;

import java.util.List;

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
    }

    @Override
    public int getItemCount() {
        return allToDo.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.todo_card_title);
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
}
