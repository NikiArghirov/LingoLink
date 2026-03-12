/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

public class LeaderboardEntry {
    private int rank;
    private int userId;
    private String username;
    private int unitsCompleted;
    private int totalAttempted;
    private double averageScore;
    
    // Constructor for basic leaderboard
    public LeaderboardEntry(int rank, int userId, String username, int unitsCompleted) {
        this.rank = rank;
        this.userId = userId;
        this.username = username;
        this.unitsCompleted = unitsCompleted;
        this.totalAttempted = 0;
        this.averageScore = 0;
    }
    
    // Constructor for detailed leaderboard
    public LeaderboardEntry(int rank, int userId, String username, int unitsCompleted, 
                           int totalAttempted, double averageScore) {
        this.rank = rank;
        this.userId = userId;
        this.username = username;
        this.unitsCompleted = unitsCompleted;
        this.totalAttempted = totalAttempted;
        this.averageScore = averageScore;
    }
    
    // Getters
    public int getRank() { return rank; }
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public int getUnitsCompleted() { return unitsCompleted; }
    public int getTotalAttempted() { return totalAttempted; }
    public double getAverageScore() { return averageScore; }
    
    // Format for display
    public String getDisplayText() {
        return String.format("%d. %s - %d units completed", rank, username, unitsCompleted);
    }
    
    public String getDetailedDisplayText() {
        if (totalAttempted > 0) {
            return String.format("%d. %s - %d/%d units (Avg: %.1f%%)", 
                rank, username, unitsCompleted, totalAttempted, averageScore);
        } else {
            return String.format("%d. %s - 0 units completed", rank, username);
        }
    }
    
    @Override
    public String toString() {
        return getDisplayText();
    }
}
