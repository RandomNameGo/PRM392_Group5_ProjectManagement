package com.example.prm392_group5.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Project;
import com.example.prm392_group5.presenter.ProjectContract;
import com.example.prm392_group5.presenter.ProjectPresenter;
import com.example.prm392_group5.view.adapter.MemberProjectAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity implements ProjectContract.View {

    private String currentUserId;
    private RecyclerView recyclerViewProjects;
    private MemberProjectAdapter adapter;
    private ProjectPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        recyclerViewProjects = findViewById(R.id.recyclerViewProjects);
        recyclerViewProjects.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemberProjectAdapter(new ArrayList<>());
        recyclerViewProjects.setAdapter(adapter);

        presenter = new ProjectPresenter(this);

        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");



        presenter.getProjectsByMember(currentUserId);
    }

    @Override
    public void onProjectsByMemberLoaded(List<Project> projects) {
        adapter.updateProjects(projects);
        Log.d("Presenter", "Fetching projects for: " + currentUserId);
        Log.d("MemberActivity", "Projects loaded: " + projects.size());

    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    @Override public void onProjectCreated() {}
    @Override public void onProjectLoaded(Project project) {}
    @Override public void onProjectUpdated() {}
    @Override public void onProjectDeleted() {}
    @Override public void onProjectListLoaded(List<Project> projects) {}
    @Override public void onMemberAdded() {}
    @Override public void onMemberRemoved() {}
    @Override public void onProjectsByLeaderLoaded(List<Project> projects) {}
}
