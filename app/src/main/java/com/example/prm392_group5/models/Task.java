package com.example.prm392_group5.models;

import java.util.HashMap;

public class Task {
    public String title;
    public String description;
    public String assignedTo;
    public String status;
    public HashMap<String, Report> reports;

    public Task() {}

    public Task(String title, String description, String assignedTo, String status) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.status = status;
        this.reports = new HashMap<>();
    }
}
