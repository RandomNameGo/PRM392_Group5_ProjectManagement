package com.example.prm392_group5.view.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private EditText etTaskTitle, etTaskDescription, etDeadlineDate;
    private Spinner spinnerAssignee;
    private Button btnAddTask, btnUpdateTask, btnCancelEdit, btnClearDeadline;
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
    private long selectedDeadline = 0; // Store selected deadline timestamp

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
        
        // Hide add task functionality for members
        setupMemberRestrictions();
        
        // Load tasks and users
        taskPresenter.getAllTasks(projectId);
        userPresenter.getAllUsers();
    }

    private void setupMemberRestrictions() {
        // Hide task creation form for members
        if ("member".equals(currentUserRole)) {
            // Hide the entire add task CardView section by its ID
            androidx.cardview.widget.CardView addTaskCard = findViewById(R.id.cardCreateTask);
            addTaskCard.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etDeadlineDate = findViewById(R.id.etDeadlineDate);
        spinnerAssignee = findViewById(R.id.spinnerAssignee);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnUpdateTask = findViewById(R.id.btnUpdateTask);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnClearDeadline = findViewById(R.id.btnClearDeadline);
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
        
        // Set current user ID and role for the adapter to control checkbox visibility
        adapter.setCurrentUserId(currentUserId);
        adapter.setCurrentUserRole(currentUserRole);
        
        // Set member mode to hide edit/delete buttons for members
        adapter.setMemberMode("member".equals(currentUserRole));
        
        adapter.setTaskActionListener(new TaskAdapter.TaskActionListener() {
            @Override
            public void onTaskCompleteChanged(Task task, boolean isCompleted) {
                // Only assigned members can change task completion status
                if (currentUserId.equals(task.assignedTo)) {
                    updateTaskCompletionStatus(task, isCompleted);
                } else {
                    Toast.makeText(TaskActivity.this, "Only the assigned member can change this task's completion status", Toast.LENGTH_SHORT).show();
                    // Reset checkbox to original state
                    adapter.notifyDataSetChanged();
                }
            }

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
        btnClearDeadline.setOnClickListener(v -> clearDeadline());
        
        // Deadline date picker
        etDeadlineDate.setOnClickListener(v -> showDatePicker());
        
        // Shrinkable bar click listener
        layoutCreateTaskHeader.setOnClickListener(v -> toggleCreateTaskSection());
    }

    private void setupSpinners() {
        // Status is now automatically managed - no status spinner needed
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
        
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String assignedTo = getSelectedUserId();
        String taskId = UUID.randomUUID().toString();
        // All new tasks automatically start with "In Progress" status
        Task task = new Task(title, description, assignedTo, "In Progress", selectedDeadline);
        
        taskPresenter.createTask(projectId, taskId, task);
    }

    private void updateTask() {
        if (editingTask == null) return;
        
        String title = etTaskTitle.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        editingTask.title = title;
        editingTask.description = description;
        // Status is automatically managed - don't change it during edit
        editingTask.assignedTo = getSelectedUserId();
        editingTask.deadline = selectedDeadline;
        
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
        
        // Set deadline
        selectedDeadline = task.deadline;
        updateDeadlineDisplay();
        
        // Status is automatically managed - no need to set status spinner
        
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
        spinnerAssignee.setSelection(0);
        selectedDeadline = 0;
        updateDeadlineDisplay();
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
        
        // Only cancel edit and collapse if we're in edit mode
        if (editingTask != null) {
            cancelEdit();
            collapseCreateTaskSection(); // Auto-collapse after successful update
        }
        
        // Refresh the task list to show updated status
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

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (selectedDeadline > 0) {
            calendar.setTimeInMillis(selectedDeadline);
        }
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth, 23, 59, 59); // Set to end of day
                selectedDeadline = selectedDate.getTimeInMillis();
                updateDeadlineDisplay();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void clearDeadline() {
        selectedDeadline = 0;
        updateDeadlineDisplay();
        Toast.makeText(this, "Deadline cleared", Toast.LENGTH_SHORT).show();
    }

    private void updateDeadlineDisplay() {
        if (selectedDeadline == 0) {
            etDeadlineDate.setText("");
            etDeadlineDate.setHint("Select Date");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            etDeadlineDate.setText(sdf.format(new Date(selectedDeadline)));
        }
    }

    private void updateTaskCompletionStatus(Task task, boolean isCompleted) {
        // Update task status based on checkbox state
        if (isCompleted) {
            task.status = "Done";
            Toast.makeText(this, "Task marked as completed!", Toast.LENGTH_SHORT).show();
        } else {
            task.status = "In Progress";
            Toast.makeText(this, "Task marked as in progress!", Toast.LENGTH_SHORT).show();
        }
        
        // Update the task in Firebase
        taskPresenter.updateTask(projectId, task.title, task);
        
        // Immediately update the adapter to reflect the change
        adapter.notifyDataSetChanged();
    }
}