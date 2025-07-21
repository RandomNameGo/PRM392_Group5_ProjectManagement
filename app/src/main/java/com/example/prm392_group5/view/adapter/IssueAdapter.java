package com.example.prm392_group5.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Issue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    public interface IssueActionListener {
        void onEditIssue(Issue issue);
        void onDeleteIssue(Issue issue);
        void onToggleResolved(Issue issue);
    }

    public interface OnIssueClickListener {
        void onIssueClick(Issue issue);
    }

    private List<Issue> issueList;
    private IssueActionListener actionListener;
    private OnIssueClickListener clickListener;

    public IssueAdapter(List<Issue> issueList) {
        this.issueList = issueList;
    }

    public void setIssueActionListener(IssueActionListener listener) {
        this.actionListener = listener;
    }

    public void setOnIssueClickListener(OnIssueClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Issue issue = issueList.get(position);
        holder.bind(issue);
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public void updateIssues(List<Issue> newIssues) {
        this.issueList = newIssues;
        notifyDataSetChanged();
    }

    class IssueViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIssueContent, tvIssueBy, tvIssueDate, tvIssueStatus;
        private Button btnEdit, btnDelete, btnToggleResolved;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIssueContent = itemView.findViewById(R.id.tvIssueContent);
            tvIssueBy = itemView.findViewById(R.id.tvIssueBy);
            tvIssueDate = itemView.findViewById(R.id.tvIssueDate);
            tvIssueStatus = itemView.findViewById(R.id.tvIssueStatus);
            btnEdit = itemView.findViewById(R.id.btnEditIssue);
            btnDelete = itemView.findViewById(R.id.btnDeleteIssue);
            btnToggleResolved = itemView.findViewById(R.id.btnToggleResolved);
        }

        public void bind(Issue issue) {
            tvIssueContent.setText(issue.content);
            tvIssueBy.setText("By: " + issue.issueBy);
            
            // Format timestamp to readable date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
            tvIssueDate.setText(sdf.format(new Date(issue.timestamp)));
            
            // Set status
            tvIssueStatus.setText(issue.resolved ? "RESOLVED" : "OPEN");
            tvIssueStatus.setBackgroundColor(issue.resolved ? 0xFF27AE60 : 0xFFE74C3C);
            tvIssueStatus.setTextColor(0xFFFFFFFF);
            
            // Set toggle button text
            btnToggleResolved.setText(issue.resolved ? "Mark Open" : "Mark Resolved");

            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onIssueClick(issue);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditIssue(issue);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteIssue(issue);
                }
            });

            btnToggleResolved.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onToggleResolved(issue);
                }
            });
        }
    }
}