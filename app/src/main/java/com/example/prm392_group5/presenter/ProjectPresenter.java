package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.FirebaseRepository;
import com.example.prm392_group5.models.Project;

import java.util.List;

public class ProjectPresenter implements ProjectContract.Presenter {
    private final ProjectContract.View view;
    private final FirebaseRepository repository;

    public ProjectPresenter(ProjectContract.View view) {
        this.view = view;
        this.repository = new FirebaseRepository();
    }

    @Override
    public void createProject(String projectId, Project project) {
        repository.createProject(projectId, project, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onProjectCreated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getProject(String projectId) {
        repository.getProject(projectId, new FirebaseRepository.ProjectCallback() {
            @Override
            public void onSuccess(Project project) {
                view.onProjectLoaded(project);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void updateProject(String projectId, Project project) {
        repository.updateProject(projectId, project, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onProjectUpdated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void deleteProject(String projectId) {
        repository.deleteProject(projectId, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onProjectDeleted();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getAllProjects() {
        repository.getAllProjects(new FirebaseRepository.ProjectListCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                view.onProjectListLoaded(projects);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void addMemberToProject(String projectId, String memberId) {
        repository.addMemberToProject(projectId, memberId, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onMemberAdded();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void removeMemberFromProject(String projectId, String memberId) {
        repository.removeMemberFromProject(projectId, memberId, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onMemberRemoved();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getProjectsByLeader(String leaderId) {
        repository.getProjectsByLeader(leaderId, new FirebaseRepository.ProjectListCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                view.onProjectsByLeaderLoaded(projects);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getProjectsByMember(String memberId) {
        repository.getProjectsByMember(memberId, new FirebaseRepository.ProjectListCallback() {
            @Override
            public void onSuccess(List<Project> projects) {
                view.onProjectsByMemberLoaded(projects);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }
}