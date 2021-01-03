package com.example.elmohammadymarket.Model;

public class Admin {
    private String username;
    private String email;
    private String token;
    private String userId;

    public Admin() {
    }

    public Admin(String username, String email, String token, String userId) {
        this.username = username;
        this.email = email;
        this.token = token;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
