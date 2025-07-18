package com.example.prm392_group5.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.User;
import com.example.prm392_group5.presenter.UserContract;
import com.example.prm392_group5.presenter.UserPresenter;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements UserContract.View {

    private TextView tvUserName, tvUserEmail, tvUserRole, tvProjectCount, tvTaskCount;
    private Button btnEditProfile, btnLogout;
    
    private UserPresenter presenter;
    private String currentUserId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        initViews();
        initData();
        loadUserProfile();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvProjectCount = findViewById(R.id.tvProjectCount);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        
        btnEditProfile.setOnClickListener(v -> editProfile());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void initData() {
        presenter = new UserPresenter(this);
        
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
    }

    private void loadUserProfile() {
        if (!currentUserId.isEmpty()) {
            presenter.getUser(currentUserId);
        }
    }

    private void editProfile() {
        // TODO: Implement edit profile functionality
        Toast.makeText(this, "Edit profile coming soon", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        finish();
        // Navigate back to login
    }

    @Override
    public void onUserCreated() {
        // Not used in profile
    }

    @Override
    public void onUserLoaded(User user) {
        currentUser = user;
        displayUserInfo(user);
    }

    @Override
    public void onUserUpdated() {
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        loadUserProfile();
    }

    @Override
    public void onUserDeleted() {
        // Not used in profile
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserListLoaded(List<User> userList) {
        // Not used in profile
    }

    private void displayUserInfo(User user) {
        tvUserName.setText(user.name);
        tvUserEmail.setText(user.email);
        tvUserRole.setText(user.role.toUpperCase());
        
        // TODO: Load actual project and task counts
        tvProjectCount.setText("0");
        tvTaskCount.setText("0");
    }
}