package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.FirebaseRepository;
import com.example.prm392_group5.models.Task;

import java.util.List;

public class TaskPresenter implements TaskContract.Presenter {
    private final TaskContract.View view;
    private final FirebaseRepository repository;

    public TaskPresenter(TaskContract.View view) {
        this.view = view;
        this.repository = new FirebaseRepository();
    }

    @Override
    public void createTask(String projectId, String taskId, Task task) {
        repository.createTask(projectId, taskId, task, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onTaskCreated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getTask(String projectId, String taskId) {
        repository.getTask(projectId, taskId, new FirebaseRepository.TaskCallback() {
            @Override
            public void onSuccess(Task task) {
                view.onTaskLoaded(task);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void updateTask(String projectId, String taskId, Task task) {
        repository.updateTask(projectId, taskId, task, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onTaskUpdated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void deleteTask(String projectId, String taskId) {
        repository.deleteTask(projectId, taskId, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onTaskDeleted();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getAllTasks(String projectId) {
        repository.getAllTasks(projectId, new FirebaseRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                view.onTaskListLoaded(tasks);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void assignTaskToUser(String projectId, String taskId, String userId) {
        repository.assignTaskToUser(projectId, taskId, userId, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onTaskAssigned();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getTasksByAssignedUser(String userId) {
        repository.getTasksByAssignedUser(userId, new FirebaseRepository.TaskListCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                view.onTaskListLoaded(tasks);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }
}