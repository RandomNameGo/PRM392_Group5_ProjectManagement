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

import androidx.appcompat.app.AppCompatActivity;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.User;
import com.example.prm392_group5.presenter.UserContract;
import com.example.prm392_group5.presenter.UserPresenter;

import java.util.List;

public class ProfileActivity extends AppCompatActivity implements UserContract.View {

    private TextView tvUserName, tvUserEmail, tvUserRole, tvProjectCount, tvTaskCount;
    private Button btnEditProfile, btnChangePassword, btnBackToDashboard, btnLogout;
    private ImageView headerImage, ivProfilePicture;
    
    private UserPresenter presenter;
    private String currentUserId;
    private String currentUserRole;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);
        
        initViews();
        initData();
        setupAnimations();
        loadUserProfile();
    }

    private void initViews() {
//        tvUserName = findViewById(R.id.tvUserName);
//        tvUserEmail = findViewById(R.id.tvUserEmail);
//        tvUserRole = findViewById(R.id.tvUserRole);
//        tvProjectCount = findViewById(R.id.tvProjectCount);
//        tvTaskCount = findViewById(R.id.tvTaskCount);
//        btnEditProfile = findViewById(R.id.btnEditProfile);
//        btnChangePassword = findViewById(R.id.btnChangePassword);
//        btnBackToDashboard = findViewById(R.id.btnBackToDashboard);
//        btnLogout = findViewById(R.id.btnLogout);
//        headerImage = findViewById(R.id.headerImage);
//        ivProfilePicture = findViewById(R.id.ivProfilePicture);

        btnEditProfile.setOnClickListener(v -> editProfile());
        btnChangePassword.setOnClickListener(v -> changePassword());
        btnBackToDashboard.setOnClickListener(v -> backToDashboard());
        btnLogout.setOnClickListener(v -> logout());
    }

    private void initData() {
        presenter = new UserPresenter(this);
        
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
        currentUserRole = prefs.getString("role", "");
    }

    private void setupAnimations() {
        Animation slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        Animation fadeScaleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in);
        
        headerImage.startAnimation(slideDownAnim);
        ivProfilePicture.startAnimation(fadeScaleAnim);
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

    private void changePassword() {
        // TODO: Implement change password functionality
        Toast.makeText(this, "Change password coming soon", Toast.LENGTH_SHORT).show();
    }

    private void backToDashboard() {
        Intent intent;
        switch (currentUserRole) {
            case "manager":
                intent = new Intent(this, ManagerActivity.class);
                break;
            case "leader":
                intent = new Intent(this, LeaderActivity.class);
                break;
            case "member":
                intent = new Intent(this, MemberActivity.class);
                break;
            default:
                intent = new Intent(this, MainActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        prefs.edit().clear().apply();
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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