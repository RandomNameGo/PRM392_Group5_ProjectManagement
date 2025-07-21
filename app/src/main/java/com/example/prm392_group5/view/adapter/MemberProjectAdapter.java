package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Project;
import com.example.prm392_group5.presenter.UserPresenter;

import java.util.List;

public class MemberProjectAdapter extends RecyclerView.Adapter<MemberProjectAdapter.ViewHolder> {

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    private List<Project> projectList;
    private OnProjectClickListener clickListener;

    public MemberProjectAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    public void setOnProjectClickListener(OnProjectClickListener listener) {
        this.clickListener = listener;
    }

    public void updateProjects(List<Project> newProjects) {
        this.projectList = newProjects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemberProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberProjectAdapter.ViewHolder holder, int position) {
        Project project = projectList.get(position);
        holder.bind(project);
    }

    @Override
    public int getItemCount() {
        return projectList != null ? projectList.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProjectName, tvProjectDescription, tvProjectMembers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvProjectDescription = itemView.findViewById(R.id.tvProjectDescription);
            tvProjectMembers = itemView.findViewById(R.id.tvProjectMembers);
        }

        public void bind(Project project) {
            tvProjectName.setText(project.name != null ? project.name : "(No title)");
            tvProjectDescription.setText(project.description != null ? project.description : "(No description)");
            if (project.members != null) {
                tvProjectMembers.setText("👥 " + project.members.size() + " members");
            } else {
                tvProjectMembers.setText("👥 0 members");
            }

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onProjectClick(project);
                }
            });
        }
    }
}
