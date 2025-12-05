package com.example.personaltaskmanager.features.task_manager.screens;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.task_manager.data.model.Task;
import com.example.personaltaskmanager.features.task_manager.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter hiển thị danh sách Task.
 * Giữ nguyên, chỉ bổ sung hiển thị deadline.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList = new ArrayList<>();

    private final OnTaskClickListener listener;
    private final OnTaskDeleteListener deleteListener;
    private final OnTaskToggleListener toggleListener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskDeleteListener {
        void onTaskDelete(Task task);
    }

    public interface OnTaskToggleListener {
        void onTaskToggle(Task task, boolean done);
    }

    public TaskAdapter(
            OnTaskClickListener listener,
            OnTaskDeleteListener deleteListener,
            OnTaskToggleListener toggleListener
    ) {
        this.listener = listener;
        this.deleteListener = deleteListener;
        this.toggleListener = toggleListener;
    }

    /** Cập nhật danh sách */
    public void setData(List<Task> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feature_task_manager_item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Title + Deadline
        holder.textTitle.setText(task.getTitle());
        holder.textDeadline.setText(DateUtils.formatDate(task.getDeadline()));

        // NGĂN LẶP LISTENER
        holder.checkboxTask.setOnCheckedChangeListener(null);
        holder.checkboxTask.setChecked(task.isCompleted());

        // Style khi completed
        applyCompletedStyle(holder, task.isCompleted());

        // CLICK ITEM → mở Workspace (thông qua callback của TaskListActivity)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTaskClick(task);
        });

        // DELETE
        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onTaskDelete(task);
        });

        // TOGGLE CHECKBOX
        holder.checkboxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (toggleListener != null) toggleListener.onTaskToggle(task, isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /** Style completed / not completed */
    private void applyCompletedStyle(TaskViewHolder holder, boolean completed) {
        if (completed) {
            holder.textTitle.setPaintFlags(
                    holder.textTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
            );
            holder.textTitle.setAlpha(0.5f);
            holder.textDeadline.setAlpha(0.5f);

            int doneColor = holder.itemView.getResources()
                    .getColor(R.color.task_checkbox_done_tint);
            holder.checkboxTask.setButtonTintList(ColorStateList.valueOf(doneColor));
        } else {
            holder.textTitle.setPaintFlags(
                    holder.textTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
            holder.textTitle.setAlpha(1f);
            holder.textDeadline.setAlpha(1f);

            int normalColor = holder.itemView.getResources()
                    .getColor(R.color.task_checkbox_tint);
            holder.checkboxTask.setButtonTintList(ColorStateList.valueOf(normalColor));
        }
    }

    /** Holder */
    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        ImageView imgTask;
        TextView textTitle, textDeadline;
        CheckBox checkboxTask;
        ImageButton btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            imgTask = itemView.findViewById(R.id.imgTask);
            textTitle = itemView.findViewById(R.id.textTaskTitle);
            textDeadline = itemView.findViewById(R.id.textTaskDeadline);
            checkboxTask = itemView.findViewById(R.id.checkboxTask);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}