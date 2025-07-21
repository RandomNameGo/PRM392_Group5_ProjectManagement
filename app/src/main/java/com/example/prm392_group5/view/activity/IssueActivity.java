package com.example.prm392_group5.view.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Issue;
import com.example.prm392_group5.presenter.IssueContract;
import com.example.prm392_group5.presenter.IssuePresenter;
import com.example.prm392_group5.view.adapter.IssueAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IssueActivity extends AppCompatActivity implements IssueContract.View {

    private TextView tvTaskTitle;
    private EditText etIssueContent;
    private CheckBox cbResolved;
    private Button btnAddIssue, btnUpdateIssue, btnCancelEdit;
    private RecyclerView recyclerViewIssues;
    
    private IssuePresenter presenter;
    private IssueAdapter adapter;
    private List<Issue> issueList;
    
    private String currentUserId;
    private String currentUserRole;
    private String projectId;
    private String taskId;
    private Issue editingIssue = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        
        // Get project and task IDs from intent
        projectId = getIntent().getStringExtra("projectId");
        taskId = getIntent().getStringExtra("taskId");
        
        if (projectId == null || taskId == null) {
            Toast.makeText(this, "Invalid project or task", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        initData();
        setupRecyclerView();
        setupClickListeners();
        
        // Load issues
        presenter.getAllIssues(projectId, taskId);
    }

    private void initViews() {
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        etIssueContent = findViewById(R.id.etIssueContent);
        cbResolved = findViewById(R.id.cbResolved);
        btnAddIssue = findViewById(R.id.btnAddIssue);
        btnUpdateIssue = findViewById(R.id.btnUpdateIssue);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        recyclerViewIssues = findViewById(R.id.recyclerViewIssues);
        
        tvTaskTitle.setText("Issues for Task: " + taskId);
    }

    private void initData() {
        presenter = new IssuePresenter(this);
        issueList = new ArrayList<>();
        
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
        currentUserRole = prefs.getString("role", "");
    }

    private void setupRecyclerView() {
        adapter = new IssueAdapter(issueList);
        recyclerViewIssues.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIssues.setAdapter(adapter);
        
        adapter.setIssueActionListener(new IssueAdapter.IssueActionListener() {
            @Override
            public void onEditIssue(Issue issue) {
                // Only allow editing own issues or if user is leader/manager
                if (issue.issueBy.equals(currentUserId) || 
                    "leader".equals(currentUserRole) || 
                    "manager".equals(currentUserRole)) {
                    startEditIssue(issue);
                } else {
                    Toast.makeText(IssueActivity.this, "You can only edit your own issues", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteIssue(Issue issue) {
                // Only allow deleting own issues or if user is leader/manager
                if (issue.issueBy.equals(currentUserId) || 
                    "leader".equals(currentUserRole) || 
                    "manager".equals(currentUserRole)) {
                    showDeleteConfirmation(issue);
                } else {
                    Toast.makeText(IssueActivity.this, "You can only delete your own issues", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onToggleResolved(Issue issue) {
                // Only leaders/managers can resolve issues
                if ("leader".equals(currentUserRole) || "manager".equals(currentUserRole)) {
                    issue.resolved = !issue.resolved;
                    presenter.updateIssue(projectId, taskId, String.valueOf(issue.timestamp), issue);
                } else {
                    Toast.makeText(IssueActivity.this, "Only leaders can resolve issues", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupClickListeners() {
        btnAddIssue.setOnClickListener(v -> addIssue());
        btnUpdateIssue.setOnClickListener(v -> updateIssue());
        btnCancelEdit.setOnClickListener(v -> cancelEdit());
    }

    private void addIssue() {
        String content = etIssueContent.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter issue content", Toast.LENGTH_SHORT).show();
            return;
        }

        String issueId = UUID.randomUUID().toString();
        Issue issue = new Issue(currentUserId, content, System.currentTimeMillis(), cbResolved.isChecked());
        
        presenter.createIssue(projectId, taskId, issueId, issue);
    }

    private void updateIssue() {
        if (editingIssue == null) return;
        
        String content = etIssueContent.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter issue content", Toast.LENGTH_SHORT).show();
            return;
        }

        editingIssue.content = content;
        editingIssue.resolved = cbResolved.isChecked();
        editingIssue.timestamp = System.currentTimeMillis();
        
        // Using timestamp as issueId for now - in real app, you'd have proper IDs
        presenter.updateIssue(projectId, taskId, String.valueOf(editingIssue.timestamp), editingIssue);
    }

    private void startEditIssue(Issue issue) {
        editingIssue = issue;
        etIssueContent.setText(issue.content);
        cbResolved.setChecked(issue.resolved);
        
        btnAddIssue.setVisibility(Button.GONE);
        btnUpdateIssue.setVisibility(Button.VISIBLE);
        btnCancelEdit.setVisibility(Button.VISIBLE);
    }

    private void cancelEdit() {
        editingIssue = null;
        clearForm();
        
        btnAddIssue.setVisibility(Button.VISIBLE);
        btnUpdateIssue.setVisibility(Button.GONE);
        btnCancelEdit.setVisibility(Button.GONE);
    }

    private void clearForm() {
        etIssueContent.setText("");
        cbResolved.setChecked(false);
    }

    private void showDeleteConfirmation(Issue issue) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Issue")
                .setMessage("Are you sure you want to delete this issue?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    presenter.deleteIssue(projectId, taskId, String.valueOf(issue.timestamp));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // IssueContract.View implementations
    @Override
    public void onIssueCreated() {
        Toast.makeText(this, "Issue created successfully", Toast.LENGTH_SHORT).show();
        clearForm();
        presenter.getAllIssues(projectId, taskId);
    }

    @Override
    public void onIssueLoaded(Issue issue) {
        // Handle single issue load if needed
    }

    @Override
    public void onIssueUpdated() {
        Toast.makeText(this, "Issue updated successfully", Toast.LENGTH_SHORT).show();
        cancelEdit();
        presenter.getAllIssues(projectId, taskId);
    }

    @Override
    public void onIssueDeleted() {
        Toast.makeText(this, "Issue deleted successfully", Toast.LENGTH_SHORT).show();
        presenter.getAllIssues(projectId, taskId);
    }

    @Override
    public void onIssueListLoaded(List<Issue> issueList) {
        this.issueList.clear();
        this.issueList.addAll(issueList);
        adapter.updateIssues(this.issueList);
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }
}