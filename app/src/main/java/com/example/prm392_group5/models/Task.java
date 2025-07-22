package com.example.prm392_group5.models;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Task {
    public String title;
    public String description;
    public String assignedTo;
    public String status;
    public long deadline; // Store as timestamp
    public HashMap<String, Report> reports;
    public HashMap<String, Issue> issues;

    public Task() {}

    public Task(String title, String description, String assignedTo, String status) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.status = status;
        this.deadline = 0;
        this.reports = new HashMap<>();
        this.issues = new HashMap<>();
    }

    public Task(String title, String description, String assignedTo, String status, long deadline) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.status = status;
        this.deadline = deadline;
        this.reports = new HashMap<>();
        this.issues = new HashMap<>();
    }

    // Helper method to check if task is overdue
    public boolean isOverdue() {
        if (deadline == 0 || "Done".equals(status)) {
            return false; // No deadline set or task is completed
        }
        return System.currentTimeMillis() > deadline;
    }

    // Helper method to get the actual status (including overdue)
    public String getActualStatus() {
        // If task is completed, always show "Done"
        if ("Done".equals(status)) {
            return "Done";
        }
        // If task is overdue, show "Overdue"
        if (isOverdue()) {
            return "Overdue";
        }
        // Otherwise, all active tasks are "In Progress"
        return "In Progress";
    }

    // Helper method to format deadline
    public String getFormattedDeadline() {
        if (deadline == 0) {
            return "No deadline";
        }
        Date date = new Date(deadline);
        return android.text.format.DateFormat.format("MMM dd, yyyy", date).toString();
    }
}
