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

    //----------------------Task----------------------//

    //Create task
    public void createTask(String projectId, String taskId, Task task, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId)
                .setValue(task)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Get task
    public void getTask(String projectId, String taskId, TaskCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Task task = snapshot.getValue(Task.class);
                        callback.onSuccess(task);
                    } else {
                        callback.onFailure("Task not found");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Update task
    public void updateTask(String projectId, String taskId, Task task, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId)
                .setValue(task)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Delete Task
    public void deleteTask(String projectId, String taskId, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId)
                .removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Get all task in project
    public void getAllTasks(String projectId, TaskListCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Task> taskList = new ArrayList<>();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            taskList.add(task);
                        }
                        callback.onSuccess(taskList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    //Assign Task
    public void assignTaskToUser(String projectId, String taskId, String userId, SimpleCallback callback) {
        DatabaseReference taskRef = rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("assignedTo");

        taskRef.setValue(userId)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //Get all task by member
    public void getTasksByAssignedUser(String userId, TaskListCallback callback) {
        rootRef.child("projects")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Task> result = new ArrayList<>();
                        for (DataSnapshot projectSnap : snapshot.getChildren()) {
                            DataSnapshot tasksSnap = projectSnap.child("tasks");
                            for (DataSnapshot taskSnap : tasksSnap.getChildren()) {
                                Task task = taskSnap.getValue(Task.class);
                                if (task != null && userId.equals(task.assignedTo)) {
                                    result.add(task);
                                }
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

    //----------------------Report----------------------//

    // Create report
    public void createReport(String projectId, String taskId, String reportId, Report report, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("reports").child(reportId)
                .setValue(report)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get report
    public void getReport(String projectId, String taskId, String reportId, ReportCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("reports").child(reportId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Report report = snapshot.getValue(Report.class);
                        callback.onSuccess(report);
                    } else {
                        callback.onFailure("Report not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get all reports by taskId
    public void getAllReports(String projectId, String taskId, ReportListCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("reports")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Report> reportList = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Report report = child.getValue(Report.class);
                            reportList.add(report);
                        }
                        callback.onSuccess(reportList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    // Update report
    public void updateReport(String projectId, String taskId, String reportId, Report report, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("reports").child(reportId)
                .setValue(report)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Delete report
    public void deleteReport(String projectId, String taskId, String reportId, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("reports").child(reportId)
                .removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    //----------------------Issue----------------------//


    // Create issue
    public void createIssue(String projectId, String taskId, String issueId, Issue issue, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("issues").child(issueId)
                .setValue(issue)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get issue
    public void getIssue(String projectId, String taskId, String issueId, IssueCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("issues").child(issueId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        Issue issue = snapshot.getValue(Issue.class);
                        callback.onSuccess(issue);
                    } else {
                        callback.onFailure("Issue not found.");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Get all issues by taskId
    public void getAllIssues(String projectId, String taskId, IssueListCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("issues")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Issue> issueList = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Issue issue = child.getValue(Issue.class);
                            issueList.add(issue);
                        }
                        callback.onSuccess(issueList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }

    // Update issue
    public void updateIssue(String projectId, String taskId, String issueId, Issue issue, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("issues").child(issueId)
                .setValue(issue)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Delete issue
    public void deleteIssue(String projectId, String taskId, String issueId, SimpleCallback callback) {
        rootRef.child("projects").child(projectId).child("tasks").child(taskId).child("issues").child(issueId)
                .removeValue()
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

    public interface TaskCallback {
        void onSuccess(Task task);
        void onFailure(String error);
    }

    public interface TaskListCallback {
        void onSuccess(List<Task> tasks);
        void onFailure(String error);
    }

    public interface ReportCallback {
        void onSuccess(Report report);
        void onFailure(String error);
    }

    public interface ReportListCallback {
        void onSuccess(List<Report> reports);
        void onFailure(String error);
    }

    public interface IssueCallback {
        void onSuccess(Issue issue);
        void onFailure(String error);
    }

    public interface IssueListCallback {
        void onSuccess(List<Issue> issues);
        void onFailure(String error);
    }


    public interface SimpleCallback {
        void onSuccess();
        void onFailure(String error);
    }
}
