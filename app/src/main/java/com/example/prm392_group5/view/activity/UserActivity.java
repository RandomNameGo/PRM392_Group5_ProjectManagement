package com.example.prm392_group5.view.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.User;
import com.example.prm392_group5.presenter.UserContract;
import com.example.prm392_group5.presenter.UserPresenter;
import com.example.prm392_group5.view.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserActivity extends AppCompatActivity implements UserContract.View {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private UserPresenter presenter;
    private Button btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        recyclerView = findViewById(R.id.recyclerUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(
                new ArrayList<>(),
                new UserAdapter.UserActionListener() {
                    @Override
                    public void onEdit(User user, String uid) {
                        showEditUserDialog(user, uid);
                    }

                    @Override
                    public void onDelete(String uid) {
                        new AlertDialog.Builder(UserActivity.this)
                                .setTitle("Delete confirm")
                                .setMessage("Are you sure you want to delete this user?")
                                .setPositiveButton("Delete", (dialog, which) -> {
                                    presenter.deleteUser(uid);
                                })
                                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                },
                user -> showUserDetailDialog(user)
        );

        recyclerView.setAdapter(adapter);
        presenter = new UserPresenter(this);
        presenter.getAllUsers();

        btnCreate = findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(v -> showCreateUserDialog());

        // Animations
        Animation slideDownAni = AnimationUtils.loadAnimation(this, R.anim.slide_down);

        recyclerView.startAnimation(slideDownAni);
    }

    private void showCreateUserDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_user, null);

        EditText nameEdit = dialogView.findViewById(R.id.dialogName);
        EditText emailEdit = dialogView.findViewById(R.id.dialogEmail);
        EditText roleEdit = dialogView.findViewById(R.id.dialogRole);
        EditText passwordEdit = dialogView.findViewById(R.id.dialogPassword);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New User");
        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();
            String role = roleEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || role.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = UUID.randomUUID().toString();
            User user = new User(name, email, role, password);
            presenter.createUser(uid, user);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void showEditUserDialog(User oldUser, String uid) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_user, null);

        EditText nameEdit = dialogView.findViewById(R.id.dialogName);
        EditText emailEdit = dialogView.findViewById(R.id.dialogEmail);
        EditText roleEdit = dialogView.findViewById(R.id.dialogRole);
        EditText passwordEdit = dialogView.findViewById(R.id.dialogPassword);

        // Populate existing data
        nameEdit.setText(oldUser.name);
        emailEdit.setText(oldUser.email);
        roleEdit.setText(oldUser.role);
        passwordEdit.setText(oldUser.password);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit User");
        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String name = nameEdit.getText().toString().trim();
            String email = emailEdit.getText().toString().trim();
            String role = roleEdit.getText().toString().trim();
            String password = passwordEdit.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || role.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            User updatedUser = new User(name, email, role, password);
            presenter.updateUser(uid, updatedUser);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showUserDetailDialog(User user) {
        String message = "Tên: " + user.name +
                "\nEmail: " + user.email +
                "\nVai trò: " + user.role;

        new AlertDialog.Builder(this)
                .setTitle("Chi tiết người dùng")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onUserCreated() {
        Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show();
        presenter.getAllUsers(); // Refresh list
    }

    @Override
    public void onUserListLoaded(List<User> userList) {
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        String currentUid = prefs.getString("uid", "");

        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.uid != null && !user.uid.equals(currentUid)) {
                filteredList.add(user);
            }
        }

        adapter.setUserList(filteredList);

    }

    @Override public void onUserLoaded(User user) {}
    @Override public void onUserUpdated() {
        Toast.makeText(this, "User updated", Toast.LENGTH_SHORT).show();
        presenter.getAllUsers();
    }
    @Override public void onUserDeleted() {
        Toast.makeText(this, "User Deleted", Toast.LENGTH_SHORT).show();
        presenter.getAllUsers();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }
}