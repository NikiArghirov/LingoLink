/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private int languageId;
    
    // Constructor
    public User(int userId, String username, String email, String password, int languageId) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.languageId = languageId;
    }
    
    // Getters
    public int getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public int getLanguageId() {
        return languageId;
    }
    
    // Setters 
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }
    
    @Override
    public String toString() {
        return "User [ID=" + userId + ", Username=" + username + ", Email=" + email + ", LanguageID=" + languageId + "]";
    }
}