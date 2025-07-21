package com.example.prm392_group5.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.prm392_group5.R;

public class ManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager);

        Button userBtn = findViewById(R.id.userBtn);
        Button projectBtn = findViewById(R.id.projectBtn);
        Button btnLogout = findViewById(R.id.btnLogout);

        userBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, UserActivity.class);
            startActivity(intent);
        });

        projectBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, ProjectActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> logout());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Animations
        ImageView headerImage = findViewById(R.id.headerImage);
        ImageView managerImage = findViewById(R.id.managerImage);

        Animation fadeScaleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in);
        Animation slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        headerImage.startAnimation(slideDownAnim);
        managerImage.startAnimation(fadeScaleAnim);
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
        Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}