package com.example.prm392_group5.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        userRef = FirebaseDatabase.getInstance().getReference("users");

        btnLogin.setOnClickListener(v -> login());


        // Animations
        ImageView loginImage = findViewById(R.id.loginImage);
        ImageView bottomImage = findViewById(R.id.bottomImage);

        Animation fadeScaleAnim = AnimationUtils.loadAnimation(this, R.anim.fade_scale_in);
        Animation slideUpAnim = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        loginImage.startAnimation(fadeScaleAnim);
        bottomImage.startAnimation(slideUpAnim);
    }

    private void login() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String uid = userSnap.getKey();
                    User user = userSnap.getValue(User.class);

                    if (user != null && user.email.equals(email) && user.password.equals(password)) {
                        found = true;

                        // Save uid and role to SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
                        prefs.edit()
                                .putString("uid", uid)
                                .putString("role", user.role)
                                .apply();

                        Intent intent;

                        if ("manager".equals(user.role)) {
                            intent = new Intent(MainActivity.this, ManagerActivity.class);
                        } else if ("leader".equals(user.role)) {
                            intent = new Intent(MainActivity.this, LeaderActivity.class);
                        } else if ("member".equals(user.role)) {
                            intent = new Intent(MainActivity.this, MemberActivity.class);
                        } else {
                            Toast.makeText(MainActivity.this, "Role không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        startActivity(intent);
                        finish();
                    }
                }

                if (!found) {
                    Toast.makeText(MainActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}