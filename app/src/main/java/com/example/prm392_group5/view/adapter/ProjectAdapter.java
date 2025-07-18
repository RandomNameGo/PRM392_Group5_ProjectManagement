package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Project;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    public interface ProjectActionListener {
        void onEditProject(Project project);
        void onDeleteProject(Project project);
        void onManageMembers(Project project);
    }

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    private List<Project> projectList;
    private ProjectActionListener actionListener;
    private OnProjectClickListener clickListener;

    public ProjectAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    public void setProjectActionListener(ProjectActionListener listener) {
        this.actionListener = listener;
    }

    public void setOnProjectClickListener(OnProjectClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public void updateProjects(List<Project> newProjects) {
        this.projectList = newProjects;
        notifyDataSetChanged();
    }

    class ProjectViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProjectName, tvProjectDescription, tvProjectLeader;
        private Button btnEdit, btnDelete, btnManageMembers;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvProjectDescription = itemView.findViewById(R.id.tvProjectDescription);
            tvProjectLeader = itemView.findViewById(R.id.tvProjectLeader);
            btnEdit = itemView.findViewById(R.id.btnEditProject);
            btnDelete = itemView.findViewById(R.id.btnDeleteProject);
            btnManageMembers = itemView.findViewById(R.id.btnManageMembers);
        }

        public void bind(Project project) {
            tvProjectName.setText(project.name);
            tvProjectDescription.setText(project.description);
            tvProjectLeader.setText("Leader: " + project.leaderId);

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onProjectClick(project);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditProject(project);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteProject(project);
                }
            });

            btnManageMembers.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onManageMembers(project);
                }
            });
        }
    }
}