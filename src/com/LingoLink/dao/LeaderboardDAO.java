/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

import java.sql.*;
import java.util.*;

public class LeaderboardDAO {
    
    // Get leaderboard data - count of completed units per user (percentage >= 70)
    public List<LeaderboardEntry> getLeaderboard() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        
        String sql = "SELECT u.UserID, u.Username, " +
                    "COUNT(CASE WHEN p.percentageComplete >= 70 THEN 1 END) as units_completed " +
                    "FROM User u " +
                    "LEFT JOIN Progress p ON u.UserID = p.UserID " +
                    "GROUP BY u.UserID, u.Username " +
                    "ORDER BY units_completed DESC, u.Username ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int rank = 1;
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                    rank++,
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getInt("units_completed")
                );
                leaderboard.add(entry);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
        
        return leaderboard;
    }
    
    // Get leaderboard with percentage thresholds
    public List<LeaderboardEntry> getLeaderboardWithDetails() {
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        
        String sql = "SELECT u.UserID, u.Username, " +
                    "COUNT(CASE WHEN p.percentageComplete >= 70 THEN 1 END) as units_completed, " +
                    "COUNT(p.percentageComplete) as total_attempted, " +
                    "AVG(p.percentageComplete) as avg_score " +
                    "FROM User u " +
                    "LEFT JOIN Progress p ON u.UserID = p.UserID " +
                    "GROUP BY u.UserID, u.Username " +
                    "ORDER BY units_completed DESC, avg_score DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            int rank = 1;
            while (rs.next()) {
                LeaderboardEntry entry = new LeaderboardEntry(
                    rank++,
                    rs.getInt("UserID"),
                    rs.getString("Username"),
                    rs.getInt("units_completed"),
                    rs.getInt("total_attempted"),
                    rs.getDouble("avg_score")
                );
                leaderboard.add(entry);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting detailed leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
        
        return leaderboard;
    }
    
    // Get current user's rank
    public int getUserRank(int userId) {
        String sql = "SELECT user_rank FROM (" +
                    "SELECT u.UserID, " +
                    "ROW_NUMBER() OVER (ORDER BY COUNT(CASE WHEN p.percentageComplete >= 70 THEN 1 END) DESC) as user_rank " +
                    "FROM User u " +
                    "LEFT JOIN Progress p ON u.UserID = p.UserID " +
                    "GROUP BY u.UserID" +
                    ") ranked WHERE UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("user_rank");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user rank: " + e.getMessage());
        }
        return -1;
    }
    
    // Get total number of users
    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) as count FROM User";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total users: " + e.getMessage());
        }
        return 0;
    }
}
