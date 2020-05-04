package com.tsinghua.tsinghelper.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tsinghua.tsinghelper.R;
import com.tsinghua.tsinghelper.dtos.TaskDTO;
import com.tsinghua.tsinghelper.ui.task.TaskDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private Context mContext;
    public ArrayList<TaskDTO> mTasks;

    public TaskAdapter(Context cxt, ArrayList<TaskDTO> tasks) {
        mContext = cxt;
        this.mTasks = tasks;
    }

    public void setTasks(ArrayList<TaskDTO> tasks) {
        this.mTasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.component_task_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return mTasks == null ? 0 : mTasks.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setTaskData(mTasks.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.task_item_avatar)
        ImageView mTaskAvatar;
        @BindView(R.id.task_item_title)
        TextView mTaskTitle;
        @BindView(R.id.task_item_reward)
        TextView mTaskReward;
        @BindView(R.id.task_item_deadline)
        TextView mTaskDeadline;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        public void setTaskData(TaskDTO task) {
            mTaskTitle.setText(task.getTitle());
            mTaskReward.setText(task.getReward());
            mTaskDeadline.setText(task.getDeadline());

            // TODO: set task publisher's avatar
            mTaskAvatar.setImageResource(R.drawable.ic_community_item_32dp);
        }

        @Override
        public void onClick(View v) {
            TaskDTO task = mTasks.get(getAdapterPosition());
            Intent it = new Intent(mContext, TaskDetail.class);

            it.putExtra("TASK_TITLE", task.getTitle());
            // TODO: send more information

            mContext.startActivity(it);
        }
    }
}
