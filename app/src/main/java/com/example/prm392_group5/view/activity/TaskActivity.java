package com.example.prm392_group5.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392_group5.R;
import com.example.prm392_group5.models.Task;
import com.example.prm392_group5.models.User;
import com.example.prm392_group5.presenter.TaskContract;
import com.example.prm392_group5.presenter.TaskPresenter;
import com.example.prm392_group5.presenter.UserContract;
import com.example.prm392_group5.presenter.UserPresenter;
import com.example.prm392_group5.view.adapter.TaskAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskActivity extends AppCompatActivity implements TaskContract.View, UserContract.View {

    private EditText etTaskTitle, etTaskDescription;
    private Spinner spinnerStatus, spinnerAssignee;
    private Button btnAddTask, btnUpdateTask, btnCancelEdit;
    private RecyclerView recyclerViewTasks;
    private LinearLayout layoutCreateTaskHeader, layoutCreateTaskContent;
    private ImageView headerImage, ivTaskExpandCollapse;
    
    private TaskPresenter taskPresenter;
    private UserPresenter userPresenter;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private List<User> userList;
    
    private String currentUserId;
    private String currentUserRole;
    private String projectId;
    private Task editingTask = null;
    private boolean isCreateTaskExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        
        // Get project ID from intent
        projectId = getIntent().getStringExtra("projectId");
        if (projectId == null || projectId.isEmpty()) {
            Toast.makeText(this, "Invalid project", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        initViews();
        initData();
        setupRecyclerView();
        setupClickListeners();
        setupSpinners();
        setupAnimations();
        
        // Load tasks and users
        taskPresenter.getAllTasks(projectId);
        userPresenter.getAllUsers();
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        spinnerAssignee = findViewById(R.id.spinnerAssignee);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        
        // Shrinkable bar elements
        layoutCreateTaskHeader = findViewById(R.id.layoutCreateTaskHeader);
        layoutCreateTaskContent = findViewById(R.id.layoutCreateTaskContent);
        headerImage = findViewById(R.id.headerImage);
        ivTaskExpandCollapse = findViewById(R.id.ivTaskExpandCollapse);
    }

    private void initData() {
        taskPresenter = new TaskPresenter(this);
        userPresenter = new UserPresenter(this);
        taskList = new ArrayList<>();
        userList = new ArrayList<>();
        
        SharedPreferences prefs = getSharedPreferences("MyApp", MODE_PRIVATE);
        currentUserId = prefs.getString("uid", "");
        currentUserRole = prefs.getString("role", "");
    }

    private void setupRecyclerView() {
        adapter = new TaskAdapter(taskList);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(adapter);
        
        adapter.setTaskActionListener(new TaskAdapter.TaskActionListener() {
            @Override
            public void onEditTask(Task task) {
                if ("leader".equals(currentUserRole) || "manager".equals(currentUserRole)) {
                    startEditTask(task);
                } else {
                    Toast.makeText(TaskActivity.this, "Only leaders can edit tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteTask(Task task) {
                if ("leader".equals(currentUserRole) || "manager".equals(currentUserRole)) {
                    showDeleteConfirmation(task);
                } else {
                    Toast.makeText(TaskActivity.this, "Only leaders can delete tasks", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onViewReports(Task task) {
                // Navigate to reports
                Intent intent = new Intent(TaskActivity.this, ReportActivity.class);
                intent.putExtra("projectId", projectId);
                intent.putExtra("taskId", task.title); // Using title as ID for now
                startActivity(intent);
            }

            @Override
            public void onViewIssues(Task task) {
                // Navigate to issues
                Intent intent = new Intent(TaskActivity.this, IssueActivity.class);
                intent.putExtra("projectId", projectId);
                intent.putExtra("taskId", task.title); // Using title as ID for now
                startActivity(intent);
            }
        });
    }

    private void setupClickListeners() {
        btnAddTask.setOnClickListener(v -> addTask());
        btnUpdateTask.setOnClickListener(v -> updateTask());
        btnCancelEdit.setOnClickListener(v -> cancelEdit());
        
        // Shrinkable bar click listener
        layoutCreateTaskHeader.setOnClickListener(v -> toggleCreateTaskSection());
    }

    private void setupSpinners() {
        // Status spinner
        String[] statuses = {"To Do", "In Progress", "Done"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void setupAssigneeSpinner() {
        List<String> userNames = new ArrayList<>();
        userNames.add("Unassigned");
        for (User user : userList) {
            userNames.add(user.name + " (" + user.role + ")");
        }
        
        ArrayAdapter<String> assigneeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userNames);
        assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignee.setAdapter(assigneeAdapter);
    }

    private void addTask() {
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();
        
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String assignedTo = getSelectedUserId();
        String taskId = UUID.randomUUID().toString();
        Task task = new Task(title, description, assignedTo, status);
        
        taskPresenter.createTask(projectId, taskId, task);
    }

    private void updateTask() {
        if (editingTask == null) return;
        
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        editingTask.title = title;
        editingTask.description = description;
        editingTask.status = status;
        editingTask.assignedTo = getSelectedUserId();
        
        // Using title as taskId for now - in real app, you'd have proper IDs
        taskPresenter.updateTask(projectId, editingTask.title, editingTask);
    }

    private String getSelectedUserId() {
        int selectedIndex = spinnerAssignee.getSelectedItemPosition();
        if (selectedIndex == 0 || selectedIndex > userList.size()) {
            return ""; // Unassigned
        }
        return userList.get(selectedIndex - 1).uid;
    }

    private void startEditTask(Task task) {
        editingTask = task;
        
        // Auto-expand the create task section for editing
        autoExpandForEdit();
        
        etTaskTitle.setText(task.title);
        etTaskDescription.setText(task.description);
        
        // Set status spinner
        String[] statuses = {"To Do", "In Progress", "Done"};
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(task.status)) {
                spinnerStatus.setSelection(i);
                break;
            }
        }
        
        // Set assignee spinner
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).uid.equals(task.assignedTo)) {
                spinnerAssignee.setSelection(i + 1); // +1 for "Unassigned" option
                break;
            }
        }
        
        btnAddTask.setVisibility(Button.GONE);
        btnUpdateTask.setVisibility(Button.VISIBLE);
        btnCancelEdit.setVisibility(Button.VISIBLE);
    }

    private void cancelEdit() {
        editingTask = null;
        clearForm();
        
        btnAddTask.setVisibility(Button.VISIBLE);
        btnUpdateTask.setVisibility(Button.GONE);
        btnCancelEdit.setVisibility(Button.GONE);
    }

    private void clearForm() {
        etTaskTitle.setText("");
        etTaskDescription.setText("");
        spinnerStatus.setSelection(0);
        spinnerAssignee.setSelection(0);
    }

    private void showDeleteConfirmation(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete '" + task.title + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskPresenter.deleteTask(projectId, task.title); // Using title as ID
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // TaskContract.View implementations
    @Override
    public void onTaskCreated() {
        Toast.makeText(this, "Task created successfully", Toast.LENGTH_SHORT).show();
        clearForm();
        collapseCreateTaskSection(); // Auto-collapse after successful creation
        taskPresenter.getAllTasks(projectId);
    }

    @Override
    public void onTaskLoaded(Task task) {
        // Handle single task load if needed
    }

    @Override
    public void onTaskUpdated() {
        Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        cancelEdit();
        collapseCreateTaskSection(); // Auto-collapse after successful update
        taskPresenter.getAllTasks(projectId);
    }

    @Override
    public void onTaskDeleted() {
        Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
        taskPresenter.getAllTasks(projectId);
    }

    @Override
    public void onTaskListLoaded(List<Task> taskList) {
        this.taskList.clear();
        this.taskList.addAll(taskList);
        adapter.updateTasks(this.taskList);
    }

    @Override
    public void onTaskAssigned() {
        Toast.makeText(this, "Task assigned successfully", Toast.LENGTH_SHORT).show();
    }

    // UserContract.View implementations
    @Override
    public void onUserCreated() {
        // Not used
    }

    @Override
    public void onUserLoaded(User user) {
        // Not used
    }

    @Override
    public void onUserUpdated() {
        // Not used
    }

    @Override
    public void onUserDeleted() {
        // Not used
    }

    @Override
    public void onUserListLoaded(List<User> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        setupAssigneeSpinner();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
    }

    private void setupAnimations() {
        // Apply the same animations as ManagerActivity
        Animation slideDownAnim = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        headerImage.startAnimation(slideDownAnim);
    }

    private void toggleCreateTaskSection() {
        if (isCreateTaskExpanded) {
            collapseCreateTaskSection();
        } else {
            expandCreateTaskSection();
        }
    }

    private void expandCreateTaskSection() {
        isCreateTaskExpanded = true;
        layoutCreateTaskContent.setVisibility(View.VISIBLE);
        
        // Rotate arrow icon
        RotateAnimation rotateAnimation = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        ivTaskExpandCollapse.startAnimation(rotateAnimation);
        
        // Change icon to collapse
        ivTaskExpandCollapse.setImageResource(R.drawable.ic_expand_less);
    }

    private void collapseCreateTaskSection() {
        isCreateTaskExpanded = false;
        layoutCreateTaskContent.setVisibility(View.GONE);
        
        // Rotate arrow icon back
        RotateAnimation rotateAnimation = new RotateAnimation(180, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        ivTaskExpandCollapse.startAnimation(rotateAnimation);
        
        // Change icon to expand
        ivTaskExpandCollapse.setImageResource(R.drawable.ic_expand_more);
    }

    private void autoExpandForEdit() {
        if (!isCreateTaskExpanded) {
            expandCreateTaskSection();
        }
    }
}