package com.example.prm392_group5.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.prm392_group5.view.adapter.MemberCheckboxAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProjectActivity extends AppCompatActivity implements ProjectContract.View, UserContract.View {

    private EditText etProjectName, etProjectDescription;
    private Spinner spinnerProjectLeader;
    private Button btnAddProject, btnUpdateProject, btnCancelEdit;
    private Button btnShowAll, btnShowMyProjects, btnShowAsLeader;
    private RecyclerView recyclerViewProjects, recyclerViewMembers;
    private LinearLayout layoutCreateProjectHeader, layoutCreateProjectContent;
    private ImageView ivExpandCollapse;
    
    private ProjectPresenter presenter;
    private UserPresenter userPresenter;
    private ProjectAdapter adapter;
    private MemberCheckboxAdapter memberAdapter;
    private List<Project> projectList;
    private List<User> userList;
    private ArrayAdapter<String> leaderAdapter;
    
    private String currentUserId;
    private String currentUserRole;
    private Project editingProject = null;
    private boolean isCreateProjectExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        
        initViews();
        initData();
        setupRecyclerView();
        setupClickListeners();
        
        // Load projects based on user role
        loadInitialProjects();
    }

    private void setupLeaderSpinner() {
        List<String> leaderNames = new ArrayList<>();
        leaderNames.add("Select Project Leader");
        leaderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, leaderNames);
        leaderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProjectLeader.setAdapter(leaderAdapter);
    }

    private void setupMemberRecyclerView() {
        List<User> memberList = new ArrayList<>();
        memberAdapter = new MemberCheckboxAdapter(memberList);
        recyclerViewMembers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMembers.setAdapter(memberAdapter);
    }

    private void updateLeaderSpinner() {
        List<String> leaderNames = new ArrayList<>();
        leaderNames.add("Select Project Leader");
        
        // Only include users who have the "leader" role
        for (User user : userList) {
            if ("leader".equals(user.role)) {
                leaderNames.add(user.name + " (" + user.email + ")");
            }
        }
        
        leaderAdapter.clear();
        leaderAdapter.addAll(leaderNames);
        leaderAdapter.notifyDataSetChanged();
    }

    private void updateMemberList() {
        List<User> memberList = new ArrayList<>();
        
        // Only include users who have the "member" role
        for (User user : userList) {
            if ("member".equals(user.role)) {
                memberList.add(user);
            }
        }
        
        memberAdapter.updateMembers(memberList);
    }

    private String getSelectedLeaderId() {
        int selectedPosition = spinnerProjectLeader.getSelectedItemPosition();
        if (selectedPosition > 0) {
            // Find the leader user from filtered list
            int leaderIndex = 0;
            for (User user : userList) {
                if ("leader".equals(user.role)) {
                    if (leaderIndex == selectedPosition - 1) {
                        return user.uid;
                    }
                    leaderIndex++;
                }
            }
        }
        return currentUserId; // Default to current user if no selection
    }

    private List<String> getSelectedMemberIds() {
        return memberAdapter.getSelectedMemberIds();
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
        recyclerViewMembers = findViewById(R.id.recyclerViewMembers);
        btnAddProject = findViewById(R.id.btnAddProject);
        btnUpdateProject = findViewById(R.id.btnUpdateProject);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnShowAll = findViewById(R.id.btnShowAll);
        btnShowMyProjects = findViewById(R.id.btnShowMyProjects);
        btnShowAsLeader = findViewById(R.id.btnShowAsLeader);
        recyclerViewProjects = findViewById(R.id.recyclerViewProjects);
        
        // Shrinkable bar elements
        layoutCreateProjectHeader = findViewById(R.id.layoutCreateProjectHeader);
        layoutCreateProjectContent = findViewById(R.id.layoutCreateProjectContent);
        ivExpandCollapse = findViewById(R.id.ivExpandCollapse);
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
        
        // Setup spinner adapters
        setupLeaderSpinner();
        setupMemberRecyclerView();
        
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
            // Check if current user is a manager
            if ("manager".equals(currentUserRole)) {
                return;
            }
            
            // Navigate to TaskActivity for this project
            Intent intent = new Intent(ProjectActivity.this, TaskActivity.class);
            intent.putExtra("projectId", project.uid);
            intent.putExtra("projectName", project.name);
            startActivity(intent);
        });
    }

    private void loadInitialProjects() {
        // Load projects based on user role
        if ("member".equals(currentUserRole)) {
            // Members see only projects they are assigned to
            presenter.getProjectsByMember(currentUserId);
        } else if ("leader".equals(currentUserRole)) {
            // Leaders see projects they lead
            presenter.getProjectsByLeader(currentUserId);
        } else {
            // Managers and others see all projects
            presenter.getAllProjects();
        }
    }

    private void setupClickListeners() {
        btnAddProject.setOnClickListener(v -> addProject());
        btnUpdateProject.setOnClickListener(v -> updateProject());
        btnCancelEdit.setOnClickListener(v -> cancelEdit());
        
        btnShowAll.setOnClickListener(v -> presenter.getAllProjects());
        btnShowMyProjects.setOnClickListener(v -> presenter.getProjectsByMember(currentUserId));
        btnShowAsLeader.setOnClickListener(v -> presenter.getProjectsByLeader(currentUserId));
        
        // Shrinkable bar click listener
        layoutCreateProjectHeader.setOnClickListener(v -> toggleCreateProjectSection());
    }

    private void addProject() {
        String name = etProjectName.getText().toString().trim();
        String description = etProjectDescription.getText().toString().trim();
        String selectedLeaderId = getSelectedLeaderId();
        List<String> selectedMemberIds = getSelectedMemberIds();

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
        
        // Create project with members using the new method
        if (!selectedMemberIds.isEmpty()) {
            Toast.makeText(this, "Creating project with " + selectedMemberIds.size() + " member(s)", Toast.LENGTH_SHORT).show();
            presenter.createProjectWithMembers(projectId, project, selectedMemberIds);
        } else {
            presenter.createProject(projectId, project);
        }
    }

    private void updateProject() {
        if (editingProject == null) return;
        
        String name = etProjectName.getText().toString().trim();
        String description = etProjectDescription.getText().toString().trim();
        String selectedLeaderId = getSelectedLeaderId();
        List<String> selectedMemberIds = getSelectedMemberIds();

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
        
        // Update project with members using the new method
        if (!selectedMemberIds.isEmpty()) {
            Toast.makeText(this, "Updating project with " + selectedMemberIds.size() + " member(s)", Toast.LENGTH_SHORT).show();
            presenter.updateProjectWithMembers(editingProject.uid, editingProject, selectedMemberIds);
        } else {
            presenter.updateProject(editingProject.uid, editingProject);
        }
    }

    private void startEditProject(Project project) {
        editingProject = project;
        
        // Auto-expand the create project section for editing
        autoExpandForEdit();
        
        etProjectName.setText(project.name);
        etProjectDescription.setText(project.description);
        setSelectedLeader(project.leaderId);
        setSelectedMembers(project);
        
        btnAddProject.setVisibility(Button.GONE);
        btnUpdateProject.setVisibility(Button.VISIBLE);
        btnCancelEdit.setVisibility(Button.VISIBLE);
    }

    private void setSelectedMembers(Project project) {
        // Clear current selections first
        memberAdapter.clearSelections();
        
        // If project has members, pre-select them
        if (project.members != null && !project.members.isEmpty()) {
            // Get the current member list from adapter and select the ones in the project
            for (String memberId : project.members.keySet()) {
                // The adapter will handle the selection internally
                // We need to add a method to set selections in the adapter
                memberAdapter.setMemberSelected(memberId, true);
            }
        }
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
        memberAdapter.clearSelections(); // Clear all member selections
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
        collapseCreateProjectSection(); // Auto-collapse after successful creation
        loadInitialProjects(); // Refresh list based on user role
    }

    @Override
    public void onProjectLoaded(Project project) {
        // Handle single project load if needed
    }

    @Override
    public void onProjectUpdated() {
        Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
        cancelEdit();
        collapseCreateProjectSection(); // Auto-collapse after successful update
        loadInitialProjects(); // Refresh list based on user role
    }

    @Override
    public void onProjectDeleted() {
        Toast.makeText(this, "Project deleted successfully", Toast.LENGTH_SHORT).show();
        loadInitialProjects(); // Refresh list based on user role
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
        updateMemberList();
    }

    private void toggleCreateProjectSection() {
        if (isCreateProjectExpanded) {
            collapseCreateProjectSection();
        } else {
            expandCreateProjectSection();
        }
    }

    private void expandCreateProjectSection() {
        isCreateProjectExpanded = true;
        layoutCreateProjectContent.setVisibility(View.VISIBLE);
        
        // Rotate arrow icon
        RotateAnimation rotateAnimation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        ivExpandCollapse.startAnimation(rotateAnimation);
        
        // Change icon to collapse
        ivExpandCollapse.setImageResource(R.drawable.ic_expand_less);
    }

    private void collapseCreateProjectSection() {
        isCreateProjectExpanded = false;
        layoutCreateProjectContent.setVisibility(View.GONE);
        
        // Rotate arrow icon back
        RotateAnimation rotateAnimation = new RotateAnimation(180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        ivExpandCollapse.startAnimation(rotateAnimation);
        
        // Change icon to expand
        ivExpandCollapse.setImageResource(R.drawable.ic_expand_more);
    }

    private void autoExpandForEdit() {
        if (!isCreateProjectExpanded) {
            expandCreateProjectSection();
        }
    }
}