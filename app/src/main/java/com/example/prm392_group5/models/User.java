package com.example.prm392_group5.models;

public class User {
    public String name;
    public String email;
    public String role;
    public String password;

    public User() {}  // Required for Firebase

    public User(String name, String email, String role, String password) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
    }
}
