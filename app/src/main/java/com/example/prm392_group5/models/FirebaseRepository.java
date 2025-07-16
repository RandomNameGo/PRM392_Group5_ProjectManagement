package com.example.prm392_group5.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseRepository {
    private final DatabaseReference rootRef;

    public FirebaseRepository() {
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    //----------------------User----------------------//

    // Create user
    public void createUser(String uid, User user, SimpleCallback callback) {
        rootRef.child("users").child(uid).setValue(user)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get user
    public void getUser(String uid, UserCallback callback) {
        rootRef.child("users").child(uid).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("User not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get all user
    public void getAllUsers(UserListCallback callback) {
        rootRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    userList.add(user);
                }
                callback.onSuccess(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    // Update user
    public void updateUser(String uid, User user, SimpleCallback callback) {
        rootRef.child("users").child(uid).setValue(user)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Delete user
    public void deleteUser(String uid, SimpleCallback callback) {
        rootRef.child("users").child(uid).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //----------------------Project----------------------//

    // Create project
    public void createProject(String projectId, Project project, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).setValue(project)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get project
    public void getProject(String projectId, ProjectCallback callback) {
        rootRef.child("projects").child(projectId).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Project project = snapshot.getValue(Project.class);
                        callback.onSuccess(project);
                    } else {
                        callback.onFailure("Project not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Update project
    public void updateProject(String projectId, Project project, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).setValue(project)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Delete project
    public void deleteProject(String projectId, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get all project
    public void getAllProjects(ProjectListCallback callback) {
        rootRef.child("projects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Project> projectList = new ArrayList<>();
                for (DataSnapshot projectSnap : snapshot.getChildren()) {
                    Project project = projectSnap.getValue(Project.class);
                    projectList.add(project);
                }
                callback.onSuccess(projectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    //Get project by leader
    public void getProjectsByLeader(String leaderId, ProjectListCallback callback) {
        rootRef.child("projects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Project> result = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Project project = snap.getValue(Project.class);
                    if (project != null && leaderId.equals(project.leaderId)) {
                        result.add(project);
                    }
                }
                callback.onSuccess(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    //Get project by member
    public void getProjectsByMember(String memberId, ProjectListCallback callback) {
        rootRef.child("projects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Project> result = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Project project = snap.getValue(Project.class);
                    if (project != null && project.members != null && project.members.containsKey(memberId)) {
                        result.add(project);
                    }
                }
                callback.onSuccess(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    //Add member
    public void addMemberToProject(String projectId, String memberId, SimpleCallback callback) {
        DatabaseReference memberRef = rootRef.child("projects").child(projectId).child("members").child(memberId);
        memberRef.setValue(true)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Remove member
    public void removeMemberFromProject(String projectId, String memberId, SimpleCallback callback) {
        DatabaseReference memberRef = rootRef.child("projects").child(projectId).child("members").child(memberId);
        memberRef.removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //----------------------Interface----------------------//

    public interface UserListCallback {
        void onSuccess(List<User> users);
        void onFailure(String error);
    }

    public interface UserCallback {
        void onSuccess(User user);
        void onFailure(String error);
    }

    public interface ProjectCallback {
        void onSuccess(Project project);
        void onFailure(String error);
    }

    public interface ProjectListCallback {
        void onSuccess(List<Project> projects);
        void onFailure(String error);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
