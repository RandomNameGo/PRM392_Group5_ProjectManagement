package com.example.prm392_group5.models;

public class Issue {
    public String issueBy;
    public String content;
    public long timestamp;
    public boolean resolved;
    public Issue() {}

    public Issue(String issueBy, String content, long timestamp, boolean resolved) {
        this.issueBy = issueBy;
        this.content = content;
        this.timestamp = timestamp;
        this.resolved = resolved;
    }
}
