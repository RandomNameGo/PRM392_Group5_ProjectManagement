package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onTaskCompleteChanged(Task task, boolean isCompleted);
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
    private boolean isMemberMode = false;
    private String currentUserId = "";
    private String currentUserRole = "";

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public void setMemberMode(boolean isMemberMode) {
        this.isMemberMode = isMemberMode;
        notifyDataSetChanged();
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
        notifyDataSetChanged();
    }

    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
        notifyDataSetChanged();
    }

    private String getCurrentUserRole() {
        return currentUserRole;
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
        private TextView tvTaskTitle, tvTaskDescription, tvTaskStatus, tvTaskAssignee, tvTaskDeadline;
        private CheckBox cbTaskComplete;
        private Button btnEdit, btnDelete, btnReports, btnIssues;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskStatus = itemView.findViewById(R.id.tvTaskStatus);
            tvTaskAssignee = itemView.findViewById(R.id.tvTaskAssignee);
            tvTaskDeadline = itemView.findViewById(R.id.tvTaskDeadline);
            cbTaskComplete = itemView.findViewById(R.id.cbTaskComplete);
            btnEdit = itemView.findViewById(R.id.btnEditTask);
            btnDelete = itemView.findViewById(R.id.btnDeleteTask);
            btnReports = itemView.findViewById(R.id.btnViewReports);
            btnIssues = itemView.findViewById(R.id.btnViewIssues);
        }

        public void bind(Task task) {
            tvTaskTitle.setText(task.title);
            tvTaskDescription.setText(task.description);
            
            // Use actual status (which includes overdue check)
            String actualStatus = task.getActualStatus();
            tvTaskStatus.setText(actualStatus);
            tvTaskAssignee.setText(task.assignedTo.isEmpty() ? "Unassigned" : task.assignedTo);
            
            // Set deadline display
            tvTaskDeadline.setText("Deadline: " + task.getFormattedDeadline());
            
            // Set deadline text color based on status
            if (task.isOverdue() && !"Done".equals(task.status)) {
                tvTaskDeadline.setTextColor(0xFFE74C3C); // Red for overdue
            } else {
                tvTaskDeadline.setTextColor(0xFF95A5A6); // Gray for normal
            }

            // Set status color
            int statusColor = getStatusColor(actualStatus);
            tvTaskStatus.setBackgroundColor(statusColor);

            // Set checkbox state and visibility
            cbTaskComplete.setChecked("Done".equals(task.status));
            
            // Always show checkbox for members, control enabling based on assignment
            cbTaskComplete.setVisibility(View.VISIBLE);
            
            // Enable checkbox only for the assigned member and if task is not completed
            if (currentUserId.equals(task.assignedTo) && !("Done".equals(task.status))) {
                cbTaskComplete.setEnabled(true);
            } else {
                cbTaskComplete.setEnabled(false); // Disable for non-assigned users or completed tasks
            }

            // Hide edit and delete buttons for members
            if (isMemberMode) {
                btnEdit.setVisibility(View.GONE);
                btnDelete.setVisibility(View.GONE);
            } else {
                btnEdit.setVisibility(View.VISIBLE);
                btnDelete.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onTaskClick(task);
                }
            });

            cbTaskComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (actionListener != null) {
                    // Get the current position to update the specific task
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Task currentTask = taskList.get(position);
                        actionListener.onTaskCompleteChanged(currentTask, isChecked);
                    }
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
                case "Overdue":
                    return 0xFF8B0000; // Dark Red
                default:
                    return 0xFF95A5A6; // Gray
            }
        }
    }
}