package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onEditTask(Task task);
        void onDeleteTask(Task task);
        void onViewReports(Task task);
        void onViewIssues(Task task);
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    private List<Task> taskList;
    private TaskActionListener actionListener;
    private OnTaskClickListener clickListener;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setTaskActionListener(TaskActionListener listener) {
        this.actionListener = listener;
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTaskTitle, tvTaskDescription, tvTaskStatus, tvTaskAssignee;
        private Button btnEdit, btnDelete, btnReports, btnIssues;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskStatus = itemView.findViewById(R.id.tvTaskStatus);
            tvTaskAssignee = itemView.findViewById(R.id.tvTaskAssignee);
            btnEdit = itemView.findViewById(R.id.btnEditTask);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
            btnReports = itemView.findViewById(R.id.btnViewReports);
            btnIssues = itemView.findViewById(R.id.btnViewIssues);
        }

        public void bind(Task task) {
            tvTaskTitle.setText(task.title);
            tvTaskDescription.setText(task.description);
            tvTaskStatus.setText(task.status);
            tvTaskAssignee.setText(task.assignedTo.isEmpty() ? "Unassigned" : task.assignedTo);

            // Set status color
            int statusColor = getStatusColor(task.status);
            tvTaskStatus.setBackgroundColor(statusColor);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onTaskClick(task);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditTask(task);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteTask(task);
                }
            });

            btnReports.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onViewReports(task);
                }
            });

            btnIssues.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onViewIssues(task);
                }
            });
        }

        private int getStatusColor(String status) {
            switch (status) {
                case "To Do":
                    return 0xFFE74C3C; // Red
                case "In Progress":
                    return 0xFFF39C12; // Orange
                case "Done":
                    return 0xFF27AE60; // Green
                default:
                    return 0xFF95A5A6; // Gray
            }
        }
    }
}