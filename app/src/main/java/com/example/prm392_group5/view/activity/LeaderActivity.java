package com.example.prm392_group5.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Project;
import com.example.prm392_group5.presenter.ProjectContract;
import com.example.prm392_group5.presenter.ProjectPresenter;
import com.example.prm392_group5.view.adapter.ProjectAdapter;

import java.util.ArrayList;
import java.util.List;

public class LeaderActivity extends AppCompatActivity implements ProjectContract.View {

    private RecyclerView recyclerViewProjects;
    private TextView tvTitle, tvEmptyMessage;
    private ImageView headerImage, leaderImage;
    private ProjectPresenter presenter;
    private ProjectAdapter adapter;
    private List<Project> projectList;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leader);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initData();
        setupRecyclerView();
        setupAnimations();
        loadLeaderProjects();
    }

    private void initViews() {
        recyclerViewProjects = findViewById(R.id.recyclerViewLeaderProjects);
        tvTitle = findViewById(R.id.tvLeaderTitle);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        headerImage = findViewById(R.id.headerImage);
        leaderImage = findViewById(R.id.leaderImage);
        
        // Profile and Logout buttons
        Button btnProfile = findViewById(R.id.btnProfile);
        Button btnLogout = findViewById(R.id.btnLogout);
        
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(LeaderActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        
        btnLogout.setOnClickListener(v -> logout());
    }

    private void initData() {
        presenter = new ProjectPresenter(this);
        projectList = new ArrayList<>();
        
        // Get current user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
    }

    private void setupRecyclerView() {
        adapter = new ProjectAdapter(projectList);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProjects.setAdapter(adapter);

        // Set click listener to navigate to project details/tasks
        adapter.setOnProjectClickListener(project -> {
            Intent intent = new Intent(LeaderActivity.this, TaskActivity.class);
            intent.putExtra("projectId", project.uid);
            intent.putExtra("projectName", project.name);
            startActivity(intent);
        });

        // Set action listeners for project management
        adapter.setProjectActionListener(new ProjectAdapter.ProjectActionListener() {
            @Override
            public void onEditProject(Project project) {
                // Navigate to ProjectActivity for editing
                Intent intent = new Intent(LeaderActivity.this, ProjectActivity.class);
                intent.putExtra("editProject", true);
                intent.putExtra("projectId", project.uid);
                startActivity(intent);
            }

            @Override
            public void onDeleteProject(Project project) {
                // Only allow deletion if current user is the leader
                if (currentUserId.equals(project.leaderId)) {
                    presenter.deleteProject(project.uid);
                } else {
                    Toast.makeText(LeaderActivity.this, "You can only delete projects you lead", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onManageMembers(Project project) {
                // Navigate to member management (if implemented)
                Toast.makeText(LeaderActivity.this, "Member management coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLeaderProjects() {
        if (!currentUserId.isEmpty()) {
            presenter.getProjectsByLeader(currentUserId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState() {
        if (projectList.isEmpty()) {
            tvEmptyMessage.setVisibility(TextView.VISIBLE);
            recyclerViewProjects.setVisibility(RecyclerView.GONE);
        } else {
            tvEmptyMessage.setVisibility(TextView.GONE);
            recyclerViewProjects.setVisibility(RecyclerView.VISIBLE);
        }
    }

    // ProjectContract.View implementations
    @Override
    public void onProjectCreated() {
        // Not used in this activity
    }

    @Override
    public void onProjectLoaded(Project project) {
        // Not used in this activity
    }

    @Override
    public void onProjectUpdated() {
        Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
        loadLeaderProjects(); // Refresh the list
    }

    @Override
    public void onProjectDeleted() {
        Toast.makeText(this, "Project deleted successfully", Toast.LENGTH_SHORT).show();
        loadLeaderProjects(); // Refresh the list
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
        updateEmptyState();
    }

    @Override
    public void onMemberAdded() {
        // Not used in this activity
    }

    @Override
    public void onMemberRemoved() {
        // Not used in this activity
    }

    @Override
    public void onProjectsByLeaderLoaded(List<Project> projects) {
        onProjectListLoaded(projects);
    }

    @Override
    public void onProjectsByMemberLoaded(List<Project> projects) {
        // Not used in this activity
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh projects when returning to this activity
        loadLeaderProjects();
    }

    private void setupAnimations() {
        // Apply the same animations as ManagerActivity
        Animation fadeScaleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in);
        Animation slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        headerImage.startAnimation(slideDownAnim);
        leaderImage.startAnimation(fadeScaleAnim);
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Show logout message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to MainActivity and clear activity stack
        Intent intent = new Intent(LeaderActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}