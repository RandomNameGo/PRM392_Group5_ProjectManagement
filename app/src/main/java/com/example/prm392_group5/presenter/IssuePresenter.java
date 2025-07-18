package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.FirebaseRepository;
import com.example.prm392_group5.models.Issue;

import java.util.List;

public class IssuePresenter implements IssueContract.Presenter {
    private final IssueContract.View view;
    private final FirebaseRepository repository;

    public IssuePresenter(IssueContract.View view) {
        this.view = view;
        this.repository = new FirebaseRepository();
    }

    @Override
    public void createIssue(String projectId, String taskId, String issueId, Issue issue) {
        repository.createIssue(projectId, taskId, issueId, issue, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onIssueCreated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getIssue(String projectId, String taskId, String issueId) {
        repository.getIssue(projectId, taskId, issueId, new FirebaseRepository.IssueCallback() {
            @Override
            public void onSuccess(Issue issue) {
                view.onIssueLoaded(issue);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void updateIssue(String projectId, String taskId, String issueId, Issue issue) {
        repository.updateIssue(projectId, taskId, issueId, issue, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onIssueUpdated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void deleteIssue(String projectId, String taskId, String issueId) {
        repository.deleteIssue(projectId, taskId, issueId, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onIssueDeleted();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getAllIssues(String projectId, String taskId) {
        repository.getAllIssues(projectId, taskId, new FirebaseRepository.IssueListCallback() {
            @Override
            public void onSuccess(List<Issue> issues) {
                view.onIssueListLoaded(issues);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }
}