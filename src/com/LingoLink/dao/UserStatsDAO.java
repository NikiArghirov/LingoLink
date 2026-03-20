/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

import java.sql.*;

public class UserStatsDAO {
    
    public UserStats getUserStats(int userId) {
        UserStats stats = new UserStats();
        stats.setUserId(userId);
        
        String sql = "SELECT u.Username, u.Email, u.LanguageID, " +
                    "COUNT(DISTINCT p.UnitID) as total_units_taken, " +
                    "COUNT(CASE WHEN p.percentageComplete >= 70 THEN 1 END) as units_passed, " +
                    "AVG(p.percentageComplete) as average_score, " +
                    "MAX(p.percentageComplete) as highest_score, " +
                    "MIN(p.percentageComplete) as lowest_score, " +
                    "COUNT(DISTINCT m.MistakeID) as total_mistakes " +
                    "FROM User u " +
                    "LEFT JOIN Progress p ON u.UserID = p.UserID " +
                    "LEFT JOIN Mistakes m ON u.UserID = m.UserID " +
                    "WHERE u.UserID = ? " +
                    "GROUP BY u.UserID, u.Username, u.Email, u.LanguageID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                stats.setUsername(rs.getString("Username"));
                stats.setEmail(rs.getString("Email"));
                stats.setLanguageId(rs.getInt("LanguageID"));
                stats.setTotalUnitsTaken(rs.getInt("total_units_taken"));
                stats.setUnitsPassed(rs.getInt("units_passed"));
                stats.setAverageScore(rs.getDouble("average_score"));
                stats.setHighestScore(rs.getInt("highest_score"));
                stats.setLowestScore(rs.getInt("lowest_score"));
                stats.setTotalMistakes(rs.getInt("total_mistakes"));
            } else {
                // User exists but has no progress yet
                stats.setUsername(getUsernameById(userId));
                stats.setEmail(getEmailById(userId));
                stats.setLanguageId(getLanguageIdByUserId(userId));
                stats.setTotalUnitsTaken(0);
                stats.setUnitsPassed(0);
                stats.setAverageScore(0);
                stats.setHighestScore(0);
                stats.setLowestScore(0);
                stats.setTotalMistakes(0);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user stats: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private String getUsernameById(int userId) {
        String sql = "SELECT Username FROM User WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        } catch (SQLException e) {
            System.err.println("Error getting username: " + e.getMessage());
        }
        return "Unknown";
    }
    
    private String getEmailById(int userId) {
        String sql = "SELECT Email FROM User WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Email");
            }
        } catch (SQLException e) {
            System.err.println("Error getting email: " + e.getMessage());
        }
        return "No email";
    }
    
    private int getLanguageIdByUserId(int userId) {
        String sql = "SELECT LanguageID FROM User WHERE UserID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("LanguageID");
            }
        } catch (SQLException e) {
            System.err.println("Error getting language ID: " + e.getMessage());
        }
        return 0;
    }
    
    public String getLanguageName(int languageId) {
        String sql = "SELECT LanguageName FROM Language WHERE LanguageID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, languageId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("LanguageName");
            }
        } catch (SQLException e) {
            System.err.println("Error getting language name: " + e.getMessage());
        }
        return "Unknown Language";
    }
}