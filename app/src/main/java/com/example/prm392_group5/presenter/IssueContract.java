package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.Issue;

import java.util.List;

public interface IssueContract {
    interface View {
        void onIssueCreated();
        void onIssueLoaded(Issue issue);
        void onIssueUpdated();
        void onIssueDeleted();
        void onError(String error);
        void onIssueListLoaded(List<Issue> issueList);
    }

    interface Presenter {
        void createIssue(String projectId, String taskId, String issueId, Issue issue);
        void getIssue(String projectId, String taskId, String issueId);
        void updateIssue(String projectId, String taskId, String issueId, Issue issue);
        void deleteIssue(String projectId, String taskId, String issueId);
        void getAllIssues(String projectId, String taskId);
    }
}