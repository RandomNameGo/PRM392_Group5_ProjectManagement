package com.example.prm392_group5.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

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

        userBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, UserActivity.class);
            startActivity(intent);
        });

        projectBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerActivity.this, ProjectActivity.class);
            startActivity(intent);
        });

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
}