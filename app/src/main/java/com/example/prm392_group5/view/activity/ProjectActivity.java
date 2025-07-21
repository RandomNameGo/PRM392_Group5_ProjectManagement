package com.example.prm392_group5.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Project;
import com.example.prm392_group5.models.User;
import com.example.prm392_group5.presenter.ProjectContract;
import com.example.prm392_group5.presenter.ProjectPresenter;
import com.example.prm392_group5.presenter.UserContract;
import com.example.prm392_group5.presenter.UserPresenter;
import com.example.prm392_group5.view.adapter.ProjectAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectActivity extends AppCompatActivity implements ProjectContract.View, UserContract.View {

    private EditText etProjectName, etProjectDescription;
    private Spinner spinnerProjectLeader;
    private Button btnAddProject, btnUpdateProject, btnCancelEdit;
    private Button btnShowAll, btnShowMyProjects, btnShowAsLeader;
    private RecyclerView recyclerViewProjects;
    
    private ProjectPresenter presenter;
    private UserPresenter userPresenter;
    private ProjectAdapter adapter;
    private List<Project> projectList;
    private List<User> userList;
    private ArrayAdapter<String> leaderAdapter;
    
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

    private void setupLeaderSpinner() {
        List<String> leaderNames = new ArrayList<>();
        leaderNames.add("Select Project Leader");
        leaderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, leaderNames);
        leaderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProjectLeader.setAdapter(leaderAdapter);
    }

    private void updateLeaderSpinner() {
        List<String> leaderNames = new ArrayList<>();
        leaderNames.add("Select Project Leader");
        
        for (User user : userList) {
            leaderNames.add(user.name + " (" + user.email + ")");
        }
        
        leaderAdapter.clear();
        leaderAdapter.addAll(leaderNames);
        leaderAdapter.notifyDataSetChanged();
    }

    private String getSelectedLeaderId() {
        int selectedPosition = spinnerProjectLeader.getSelectedItemPosition();
        if (selectedPosition > 0 && selectedPosition <= userList.size()) {
            return userList.get(selectedPosition - 1).uid;
        }
        return currentUserId; // Default to current user if no selection
    }

    private void setSelectedLeader(String leaderId) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).uid.equals(leaderId)) {
                spinnerProjectLeader.setSelection(i + 1); // +1 because of "Select Project Leader" at index 0
                break;
            }
        }
    }

    private void initViews() {
        etProjectName = findViewById(R.id.etProjectName);
        etProjectDescription = findViewById(R.id.etProjectDescription);
        spinnerProjectLeader = findViewById(R.id.spinnerProjectLeader);
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
        userPresenter = new UserPresenter(this);
        projectList = new ArrayList<>();
        userList = new ArrayList<>();
        
        // Get current user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
        currentUserRole = prefs.getString("role", "");
        
        // Setup spinner adapter
        setupLeaderSpinner();
        
        // Load users for leader selection
        userPresenter.getAllUsers();
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
        String selectedLeaderId = getSelectedLeaderId();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerProjectLeader.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a project leader", Toast.LENGTH_SHORT).show();
            return;
        }

        String projectId = UUID.randomUUID().toString();
        // Current user is creator, selected user is leader
        Project project = new Project(name, description, currentUserId, selectedLeaderId);
        
        presenter.createProject(projectId, project);
    }

    private void updateProject() {
        if (editingProject == null) return;
        
        String name = etProjectName.getText().toString().trim();
        String description = etProjectDescription.getText().toString().trim();
        String selectedLeaderId = getSelectedLeaderId();

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerProjectLeader.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a project leader", Toast.LENGTH_SHORT).show();
            return;
        }

        editingProject.name = name;
        editingProject.description = description;
        editingProject.leaderId = selectedLeaderId; // Update leader
        
        presenter.updateProject(editingProject.uid, editingProject);
    }

    private void startEditProject(Project project) {
        editingProject = project;
        etProjectName.setText(project.name);
        etProjectDescription.setText(project.description);
        setSelectedLeader(project.leaderId);
        
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
        spinnerProjectLeader.setSelection(0); // Reset to "Select Project Leader"
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

    // UserContract.View implementations
    @Override
    public void onUserCreated() {
        // Not used in this activity
    }

    @Override
    public void onUserLoaded(User user) {
        // Not used in this activity
    }

    @Override
    public void onUserUpdated() {
        // Not used in this activity
    }

    @Override
    public void onUserDeleted() {
        // Not used in this activity
    }

    @Override
    public void onUserListLoaded(List<User> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        updateLeaderSpinner();
    }
}