package com.example.prm392_group5.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Project;
import com.example.prm392_group5.presenter.ProjectContract;
import com.example.prm392_group5.presenter.ProjectPresenter;
import com.example.prm392_group5.view.adapter.ProjectAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectActivity extends AppCompatActivity implements ProjectContract.View {

    private EditText etProjectName, etProjectDescription;
    private Button btnAddProject, btnUpdateProject, btnCancelEdit;
    private Button btnShowAll, btnShowMyProjects, btnShowAsLeader;
    private RecyclerView recyclerViewProjects;
    
    private ProjectPresenter presenter;
    private ProjectAdapter adapter;
    private List<Project> projectList;
    
    private String currentUserId;
    private String currentUserRole;
    private Project editingProject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        
        initViews();
        initData();
        setupRecyclerView();
        setupClickListeners();
        
        // Load all projects initially
        presenter.getAllProjects();
    }

    private void initViews() {
        etProjectName = findViewById(R.id.etProjectName);
        etProjectDescription = findViewById(R.id.etProjectDescription);
        btnAddProject = findViewById(R.id.btnAddProject);
        btnUpdateProject = findViewById(R.id.btnUpdateProject);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnShowAll = findViewById(R.id.btnShowAll);
        btnShowMyProjects = findViewById(R.id.btnShowMyProjects);
        btnShowAsLeader = findViewById(R.id.btnShowAsLeader);
        recyclerViewProjects = findViewById(R.id.recyclerViewProjects);
    }

    private void initData() {
        presenter = new ProjectPresenter(this);
        projectList = new ArrayList<>();
        
        // Get current user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
        currentUserRole = prefs.getString("role", "");
        
        // Current user will be set as leader automatically
    }

    private void setupRecyclerView() {
        adapter = new ProjectAdapter(projectList);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProjects.setAdapter(adapter);
        
        adapter.setProjectActionListener(new ProjectAdapter.ProjectActionListener() {
            @Override
            public void onEditProject(Project project) {
                startEditProject(project);
            }

            @Override
            public void onDeleteProject(Project project) {
                showDeleteConfirmation(project);
            }

            @Override
            public void onManageMembers(Project project) {
                // TODO: Implement member management dialog
                Toast.makeText(ProjectActivity.this, "Member management coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        
        adapter.setOnProjectClickListener(project -> {
            // Navigate to TaskActivity for this project
            Intent intent = new Intent(ProjectActivity.this, TaskActivity.class);
            intent.putExtra("projectId", project.uid);
            intent.putExtra("projectName", project.name);
            startActivity(intent);
        });
    }

    private void setupClickListeners() {
        btnAddProject.setOnClickListener(v -> addProject());
        btnUpdateProject.setOnClickListener(v -> updateProject());
        btnCancelEdit.setOnClickListener(v -> cancelEdit());
        
        btnShowAll.setOnClickListener(v -> presenter.getAllProjects());
        btnShowMyProjects.setOnClickListener(v -> presenter.getProjectsByMember(currentUserId));
        btnShowAsLeader.setOnClickListener(v -> presenter.getProjectsByLeader(currentUserId));
    }

    private void addProject() {
        String name = etProjectName.getText().toString().trim();
        String description = etProjectDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String projectId = UUID.randomUUID().toString();
        // Current user is automatically set as both creator and leader
        Project project = new Project(name, description, currentUserId, currentUserId);
        
        presenter.createProject(projectId, project);
    }

    private void updateProject() {
        if (editingProject == null) return;
        
        String name = etProjectName.getText().toString().trim();
        String description = etProjectDescription.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        editingProject.name = name;
        editingProject.description = description;
        // Keep the original leader ID when updating
        
        presenter.updateProject(editingProject.uid, editingProject);
    }

    private void startEditProject(Project project) {
        editingProject = project;
        etProjectName.setText(project.name);
        etProjectDescription.setText(project.description);
        
        btnAddProject.setVisibility(Button.GONE);
        btnUpdateProject.setVisibility(Button.VISIBLE);
        btnCancelEdit.setVisibility(Button.VISIBLE);
    }

    private void cancelEdit() {
        editingProject = null;
        clearForm();
        
        btnAddProject.setVisibility(Button.VISIBLE);
        btnUpdateProject.setVisibility(Button.GONE);
        btnCancelEdit.setVisibility(Button.GONE);
    }

    private void clearForm() {
        etProjectName.setText("");
        etProjectDescription.setText("");
    }

    private void showDeleteConfirmation(Project project) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Project")
                .setMessage("Are you sure you want to delete '" + project.name + "'?")
                .setPositiveButton("Delete", (dialog, which) -> presenter.deleteProject(project.uid))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ProjectContract.View implementations
    @Override
    public void onProjectCreated() {
        Toast.makeText(this, "Project created successfully", Toast.LENGTH_SHORT).show();
        clearForm();
        presenter.getAllProjects(); // Refresh list
    }

    @Override
    public void onProjectLoaded(Project project) {
        // Handle single project load if needed
    }

    @Override
    public void onProjectUpdated() {
        Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
        cancelEdit();
        presenter.getAllProjects(); // Refresh list
    }

    @Override
    public void onProjectDeleted() {
        Toast.makeText(this, "Project deleted successfully", Toast.LENGTH_SHORT).show();
        presenter.getAllProjects(); // Refresh list
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProjectListLoaded(List<Project> projectList) {
        this.projectList.clear();
        this.projectList.addAll(projectList);
        adapter.updateProjects(this.projectList);
    }

    @Override
    public void onMemberAdded() {
        Toast.makeText(this, "Member added successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMemberRemoved() {
        Toast.makeText(this, "Member removed successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProjectsByLeaderLoaded(List<Project> projects) {
        onProjectListLoaded(projects);
    }

    @Override
    public void onProjectsByMemberLoaded(List<Project> projects) {
        onProjectListLoaded(projects);
    }
}