package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.FirebaseRepository;
import com.example.prm392_group5.models.Report;

import java.util.List;

public class ReportPresenter implements ReportContract.Presenter {
    private final ReportContract.View view;
    private final FirebaseRepository repository;

    public ReportPresenter(ReportContract.View view) {
        this.view = view;
        this.repository = new FirebaseRepository();
    }

    @Override
    public void createReport(String projectId, String taskId, String reportId, Report report) {
        repository.createReport(projectId, taskId, reportId, report, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onReportCreated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getReport(String projectId, String taskId, String reportId) {
        repository.getReport(projectId, taskId, reportId, new FirebaseRepository.ReportCallback() {
            @Override
            public void onSuccess(Report report) {
                view.onReportLoaded(report);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void updateReport(String projectId, String taskId, String reportId, Report report) {
        repository.updateReport(projectId, taskId, reportId, report, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onReportUpdated();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void deleteReport(String projectId, String taskId, String reportId) {
        repository.deleteReport(projectId, taskId, reportId, new FirebaseRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                view.onReportDeleted();
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }

    @Override
    public void getAllReports(String projectId, String taskId) {
        repository.getAllReports(projectId, taskId, new FirebaseRepository.ReportListCallback() {
            @Override
            public void onSuccess(List<Report> reports) {
                view.onReportListLoaded(reports);
            }

            @Override
            public void onFailure(String error) {
                view.onError(error);
            }
        });
    }
}