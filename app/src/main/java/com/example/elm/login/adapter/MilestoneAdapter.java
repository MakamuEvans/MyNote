package com.example.elm.login.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elm.login.R;
import com.example.elm.login.model.Milestones;

import java.util.List;

/**
 * Created by elm on 8/16/17.
 */

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.myViewHolder> {
    public List<Milestones> allTasks;

    public MilestoneAdapter(List<Milestones> allTasks) {
        this.allTasks = allTasks;
    }

    @Override
    public MilestoneAdapter.myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.milestone_card, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MilestoneAdapter.myViewHolder holder, int position) {
        Milestones milestones = allTasks.get(position);
        holder.title.setText(milestones.getDescription());
        if (milestones.getStatus() == false){
            holder.imageView.setImageResource(R.mipmap.ic_action_check_circle_not);
        }else {
            holder.imageView.setImageResource(R.mipmap.ic_action_check_circle);
        }
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView imageView;
        public myViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.milestone_card_title);
            imageView = (ImageView) itemView.findViewById(R.id.milestone_status);
        }
    }

    //CRUD
    public void insert(Milestones milestones){
        this.allTasks.add(0, milestones);
        notifyItemInserted(0);
        this.notifyItemRangeChanged(0, allTasks.size());
    }
    public void update(Milestones milestones){

    }

    public void delete(int position){

    }
}
