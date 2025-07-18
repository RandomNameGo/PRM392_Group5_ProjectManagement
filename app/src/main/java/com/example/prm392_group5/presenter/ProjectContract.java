package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.Project;

import java.util.List;

public interface ProjectContract {
    interface View {
        void onProjectCreated();
        void onProjectLoaded(Project project);
        void onProjectUpdated();
        void onProjectDeleted();
        void onError(String error);
        void onProjectListLoaded(List<Project> projectList);
        void onMemberAdded();
        void onMemberRemoved();
        void onProjectsByLeaderLoaded(List<Project> projects);
        void onProjectsByMemberLoaded(List<Project> projects);
    }

    interface Presenter {
        void createProject(String projectId, Project project);
        void getProject(String projectId);
        void updateProject(String projectId, Project project);
        void deleteProject(String projectId);
        void getAllProjects();
        void addMemberToProject(String projectId, String memberId);
        void removeMemberFromProject(String projectId, String memberId);
        void getProjectsByLeader(String leaderId);
        void getProjectsByMember(String memberId);
    }
}