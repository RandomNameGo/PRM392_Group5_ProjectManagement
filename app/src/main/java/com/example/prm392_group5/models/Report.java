package com.example.prm392_group5.models;

public class Report {
    public String reportBy;
    public String content;
    public long timestamp;

    public Report() {}

    public Report(String reportBy, String content, long timestamp) {
        this.reportBy = reportBy;
        this.content = content;
        this.timestamp = timestamp;
    }
}
