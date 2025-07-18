package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.Task;

import java.util.List;

public interface TaskContract {
    interface View {
        void onTaskCreated();
        void onTaskLoaded(Task task);
        void onTaskUpdated();
        void onTaskDeleted();
        void onError(String error);
        void onTaskListLoaded(List<Task> taskList);
        void onTaskAssigned();
    }

    interface Presenter {
        void createTask(String projectId, String taskId, Task task);
        void getTask(String projectId, String taskId);
        void updateTask(String projectId, String taskId, Task task);
        void deleteTask(String projectId, String taskId);
        void getAllTasks(String projectId);
        void assignTaskToUser(String projectId, String taskId, String userId);
        void getTasksByAssignedUser(String userId);
    }
}