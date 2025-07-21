package com.example.prm392_group5.models;

import java.util.HashMap;

public class Project {
    public String uid;

    public String name;
    public String description;
    public String createdBy;
    public String leaderId;
    public HashMap<String, Boolean> members;
    public HashMap<String, Task> tasks;

    public Project() {}

    public Project(String name, String description, String createdBy, String leaderId) {
        this.name = name;
        this.description = description;
        this.createdBy = createdBy;
        this.leaderId = leaderId;
        this.members = new HashMap<>();
        this.tasks = new HashMap<>();
    }
}
