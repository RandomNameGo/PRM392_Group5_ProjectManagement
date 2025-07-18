package com.example.prm392_group5.view.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Report;
import com.example.prm392_group5.presenter.ReportContract;
import com.example.prm392_group5.presenter.ReportPresenter;
import com.example.prm392_group5.view.adapter.ReportAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReportActivity extends AppCompatActivity implements ReportContract.View {

    private TextView tvTaskTitle;
    private EditText etReportContent;
    private Button btnAddReport, btnUpdateReport, btnCancelEdit;
    private RecyclerView recyclerViewReports;
    
    private ReportPresenter presenter;
    private ReportAdapter adapter;
    private List<Report> reportList;
    
    private String currentUserId;
    private String currentUserRole;
    private String projectId;
    private String taskId;
    private Report editingReport = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        
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
        
        // Load reports
        presenter.getAllReports(projectId, taskId);
    }

    private void initViews() {
        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        etReportContent = findViewById(R.id.etReportContent);
        btnAddReport = findViewById(R.id.btnAddReport);
        btnUpdateReport = findViewById(R.id.btnUpdateReport);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        recyclerViewReports = findViewById(R.id.recyclerViewReports);
        
        tvTaskTitle.setText("Reports for Task: " + taskId);
    }

    private void initData() {
        presenter = new ReportPresenter(this);
        reportList = new ArrayList<>();
        
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
        currentUserRole = prefs.getString("role", "");
    }

    private void setupRecyclerView() {
        adapter = new ReportAdapter(reportList);
        recyclerViewReports.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReports.setAdapter(adapter);
        
        adapter.setReportActionListener(new ReportAdapter.ReportActionListener() {
            @Override
            public void onEditReport(Report report) {
                // Only allow editing own reports or if user is leader/manager
                if (report.reportBy.equals(currentUserId) || 
                    "leader".equals(currentUserRole) || 
                    "manager".equals(currentUserRole)) {
                    startEditReport(report);
                } else {
                    Toast.makeText(ReportActivity.this, "You can only edit your own reports", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteReport(Report report) {
                // Only allow deleting own reports or if user is leader/manager
                if (report.reportBy.equals(currentUserId) || 
                    "leader".equals(currentUserRole) || 
                    "manager".equals(currentUserRole)) {
                    showDeleteConfirmation(report);
                } else {
                    Toast.makeText(ReportActivity.this, "You can only delete your own reports", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupClickListeners() {
        btnAddReport.setOnClickListener(v -> addReport());
        btnUpdateReport.setOnClickListener(v -> updateReport());
        btnCancelEdit.setOnClickListener(v -> cancelEdit());
    }

    private void addReport() {
        String content = etReportContent.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter report content", Toast.LENGTH_SHORT).show();
            return;
        }

        String reportId = UUID.randomUUID().toString();
        Report report = new Report(currentUserId, content, System.currentTimeMillis());
        
        presenter.createReport(projectId, taskId, reportId, report);
    }

    private void updateReport() {
        if (editingReport == null) return;
        
        String content = etReportContent.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter report content", Toast.LENGTH_SHORT).show();
            return;
        }

        editingReport.content = content;
        editingReport.timestamp = System.currentTimeMillis();
        
        // Using timestamp as reportId for now - in real app, you'd have proper IDs
        presenter.updateReport(projectId, taskId, String.valueOf(editingReport.timestamp), editingReport);
    }

    private void startEditReport(Report report) {
        editingReport = report;
        etReportContent.setText(report.content);
        
        btnAddReport.setVisibility(Button.GONE);
        btnUpdateReport.setVisibility(Button.VISIBLE);
        btnCancelEdit.setVisibility(Button.VISIBLE);
    }

    private void cancelEdit() {
        editingReport = null;
        clearForm();
        
        btnAddReport.setVisibility(Button.VISIBLE);
        btnUpdateReport.setVisibility(Button.GONE);
        btnCancelEdit.setVisibility(Button.GONE);
    }

    private void clearForm() {
        etReportContent.setText("");
    }

    private void showDeleteConfirmation(Report report) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this report?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    presenter.deleteReport(projectId, taskId, String.valueOf(report.timestamp));
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ReportContract.View implementations
    @Override
    public void onReportCreated() {
        Toast.makeText(this, "Report created successfully", Toast.LENGTH_SHORT).show();
        clearForm();
        presenter.getAllReports(projectId, taskId);
    }

    @Override
    public void onReportLoaded(Report report) {
        // Handle single report load if needed
    }

    @Override
    public void onReportUpdated() {
        Toast.makeText(this, "Report updated successfully", Toast.LENGTH_SHORT).show();
        cancelEdit();
        presenter.getAllReports(projectId, taskId);
    }

    @Override
    public void onReportDeleted() {
        Toast.makeText(this, "Report deleted successfully", Toast.LENGTH_SHORT).show();
        presenter.getAllReports(projectId, taskId);
    }

    @Override
    public void onReportListLoaded(List<Report> reportList) {
        this.reportList.clear();
        this.reportList.addAll(reportList);
        adapter.updateReports(this.reportList);
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }
}