package com.elm.mycheck.login.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.elm.mycheck.login.R;
import com.elm.mycheck.login.ToDoDetails;
import com.elm.mycheck.login.model.Milestones;
import com.tooltip.Tooltip;

import java.util.List;

/**
 * Created by elm on 8/16/17.
 */

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.myViewHolder> {
    public List<Milestones> allTasks;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    public Context context;

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
        if (position ==0){
            new Tooltip.Builder(holder.itemView)
                    .setText("Swipe left for more options")
                    .setCancelable(true)
                    .show();
        }
        holder.title.setText(milestones.getDescription());
        if (milestones.getStatus() == false){
            holder.mFail.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.mipmap.ic_action_check_circle_not);
        }else {
            holder.mSuccess.setVisibility(View.GONE);
            holder.imageView.setImageResource(R.mipmap.ic_action_check_circle);
            holder.imageView.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.primaryAccentBlue));

        }
        viewBinderHelper.bind(holder.swipeRevealLayout, milestones.getId().toString());
    }

    @Override
    public int getItemCount() {
        return allTasks.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView imageView,mDelete,mFail;
        Button mSuccess;
        SwipeRevealLayout swipeRevealLayout;
        public myViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.milestone_card_title);
            imageView = (ImageView) itemView.findViewById(R.id.milestone_status);
            mSuccess = (Button) itemView.findViewById(R.id.milestone_success);
            mFail = (ImageView) itemView.findViewById(R.id.milestone_fail);
            swipeRevealLayout = (SwipeRevealLayout) itemView.findViewById(R.id.swiper_milestone);
            mDelete = (ImageView) itemView.findViewById(R.id.milestone_delete);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pos = getLayoutPosition();
                    final Milestones milestones = allTasks.get(pos);
                    final Context context = v.getContext();

                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Are you sure?")
                            .setMessage("You are about to delete Task "+milestones.getDescription())
                            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    milestones.delete();
                                    removeTask(pos, Long.valueOf(milestones.getTodoid()), context);
                                }
                            })
                            .create()
                            .show();
                }
            });

            mSuccess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Milestones milestones = allTasks.get(pos);
                    milestones.setStatus(true);
                    milestones.save();
                    imageView.setImageResource(R.mipmap.ic_action_check_circle);
                    mSuccess.setVisibility(View.GONE);
                    mFail.setVisibility(View.VISIBLE);

                    Intent intent = new Intent();
                    intent.setAction(ToDoDetails.newPercentage.SYNC_ACTION);
                    itemView.getContext().sendBroadcast(intent);
                }
            });

            mFail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getLayoutPosition();
                    Milestones milestones = allTasks.get(pos);
                    milestones.setStatus(false);
                    milestones.save();
                    imageView.setImageResource(R.mipmap.ic_action_check_circle_not);
                    mFail.setVisibility(View.GONE);
                    mSuccess.setVisibility(View.VISIBLE);

                    Intent intent = new Intent();
                    intent.setAction(ToDoDetails.newPercentage.SYNC_ACTION);
                    itemView.getContext().sendBroadcast(intent);
                }
            });
        }
    }

    //CRUD
    public void insert(Milestones milestones){
        this.allTasks.add(allTasks.size(), milestones);
        notifyItemInserted(allTasks.size());
        this.notifyItemRangeChanged(allTasks.size(), allTasks.size());
    }
    public void update(Milestones milestones){

    }

    public void removeTask(int position, Long id, Context context){
        allTasks.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, allTasks.size());

        Intent intent = new Intent(ToDoDetails.milestoneRemover.ACTIION_REP);
        intent.putExtra("id", id);
        context.sendBroadcast(intent);
    }

    public void delete(int position){

    }
}
