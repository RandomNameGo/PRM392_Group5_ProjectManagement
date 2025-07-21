package com.example.prm392_group5.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Project;
import com.example.prm392_group5.presenter.ProjectContract;
import com.example.prm392_group5.presenter.ProjectPresenter;
import com.example.prm392_group5.view.adapter.ProjectAdapter;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity implements ProjectContract.View {

    private RecyclerView recyclerViewMemberProjects;
    private ProjectAdapter adapter;
    private List<Project> projectList;
    private ProjectPresenter presenter;
    private String currentUserId;
    private String currentUserRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        initViews();
        initData();
        setupRecyclerView();
        setupAnimations();

        // Load projects for the current member
        loadMemberProjects();
    }

    private void initViews() {
        recyclerViewMemberProjects = findViewById(R.id.recyclerViewMemberProjects);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> logout());
    }

    private void initData() {
        presenter = new ProjectPresenter(this);
        projectList = new ArrayList<>();

        // Get current user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
        currentUserRole = prefs.getString("role", "");
    }

    private void setupRecyclerView() {
        adapter = new ProjectAdapter(projectList);
        adapter.setMemberMode(true); // Hide action buttons for member view
        recyclerViewMemberProjects.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMemberProjects.setAdapter(adapter);

        // Set click listener to navigate to TaskActivity
        adapter.setOnProjectClickListener(project -> {
            Intent intent = new Intent(MemberActivity.this, TaskActivity.class);
            intent.putExtra("projectId", project.uid);
            intent.putExtra("projectName", project.name);
            startActivity(intent);
        });
    }

    private void loadMemberProjects() {
        // Load projects where the current user is a member
        presenter.getProjectsByMember(currentUserId);
    }

    // ProjectContract.View implementations
    @Override
    public void onProjectCreated() {
        // Not used in member activity
    }

    @Override
    public void onProjectLoaded(Project project) {
        // Not used in member activity
    }

    @Override
    public void onProjectUpdated() {
        Toast.makeText(this, "Project updated successfully", Toast.LENGTH_SHORT).show();
        loadMemberProjects(); // Refresh the list
    }

    @Override
    public void onProjectDeleted() {
        // Not used in member activity
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
        // Not used in member activity
    }

    @Override
    public void onMemberRemoved() {
        // Not used in member activity
    }

    @Override
    public void onProjectsByLeaderLoaded(List<Project> projects) {
        // Not used in member activity
    }

    @Override
    public void onProjectsByMemberLoaded(List<Project> projects) {
        onProjectListLoaded(projects);
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
        Intent intent = new Intent(MemberActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh projects when returning to this activity
        loadMemberProjects();
    }

    private void setupAnimations() {
        // Animations similar to ManagerActivity
        ImageView headerImage = findViewById(R.id.headerImage);
        ImageView memberImage = findViewById(R.id.memberImage);

        Animation fadeScaleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in);
        Animation slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        headerImage.startAnimation(slideDownAnim);
        memberImage.startAnimation(fadeScaleAnim);
    }
}
