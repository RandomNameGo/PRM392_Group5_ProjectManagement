package com.example.prm392_group5.presenter;

import com.example.prm392_group5.models.Report;

import java.util.List;

public interface ReportContract {
    interface View {
        void onReportCreated();
        void onReportLoaded(Report report);
        void onReportUpdated();
        void onReportDeleted();
        void onError(String error);
        void onReportListLoaded(List<Report> reportList);
    }

    interface Presenter {
        void createReport(String projectId, String taskId, String reportId, Report report);
        void getReport(String projectId, String taskId, String reportId);
        void updateReport(String projectId, String taskId, String reportId, Report report);
        void deleteReport(String projectId, String taskId, String reportId);
        void getAllReports(String projectId, String taskId);
    }
}