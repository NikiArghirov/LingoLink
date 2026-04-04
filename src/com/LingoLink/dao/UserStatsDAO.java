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
        
        // Fixed SQL query - correctly counts units taken vs passed
        String sql = "SELECT " +
                    "u.Username, " +
                    "u.Email, " +
                    "u.LanguageID, " +
                    "COUNT(DISTINCT p.UnitID) as total_units_taken, " +
                    "SUM(CASE WHEN p.percentageComplete >= 70 THEN 1 ELSE 0 END) as units_passed, " +
                    "ROUND(AVG(p.percentageComplete), 1) as average_score, " +
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
                
                int totalUnits = rs.getInt("total_units_taken");
                int unitsPassed = rs.getInt("units_passed");
                
                // Fix: Ensure units_passed doesn't exceed total_units
                if (unitsPassed > totalUnits) {
                    unitsPassed = totalUnits;
                }
                
                stats.setTotalUnitsTaken(totalUnits);
                stats.setUnitsPassed(unitsPassed);
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
    
    // Alternative: Get stats by counting correctly without GROUP BY issues
    public UserStats getUserStatsAlternative(int userId) {
        UserStats stats = new UserStats();
        stats.setUserId(userId);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Get user basic info
            String userSql = "SELECT Username, Email, LanguageID FROM User WHERE UserID = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, userId);
            ResultSet userRs = userStmt.executeQuery();
            
            if (userRs.next()) {
                stats.setUsername(userRs.getString("Username"));
                stats.setEmail(userRs.getString("Email"));
                stats.setLanguageId(userRs.getInt("LanguageID"));
            }
            
            // Get progress stats
            String progressSql = "SELECT " +
                                "COUNT(DISTINCT UnitID) as total_units, " +
                                "SUM(CASE WHEN percentageComplete >= 70 THEN 1 ELSE 0 END) as passed_units, " +
                                "ROUND(AVG(percentageComplete), 1) as avg_score, " +
                                "MAX(percentageComplete) as highest, " +
                                "MIN(percentageComplete) as lowest " +
                                "FROM Progress WHERE UserID = ?";
            
            PreparedStatement progressStmt = conn.prepareStatement(progressSql);
            progressStmt.setInt(1, userId);
            ResultSet progressRs = progressStmt.executeQuery();
            
            if (progressRs.next()) {
                int totalUnits = progressRs.getInt("total_units");
                int passedUnits = progressRs.getInt("passed_units");
                
                stats.setTotalUnitsTaken(totalUnits);
                stats.setUnitsPassed(passedUnits);
                stats.setAverageScore(progressRs.getDouble("avg_score"));
                stats.setHighestScore(progressRs.getInt("highest"));
                stats.setLowestScore(progressRs.getInt("lowest"));
            } else {
                stats.setTotalUnitsTaken(0);
                stats.setUnitsPassed(0);
                stats.setAverageScore(0);
                stats.setHighestScore(0);
                stats.setLowestScore(0);
            }
            
            // Get mistakes count
            String mistakeSql = "SELECT COUNT(DISTINCT MistakeID) as mistake_count FROM Mistakes WHERE UserID = ?";
            PreparedStatement mistakeStmt = conn.prepareStatement(mistakeSql);
            mistakeStmt.setInt(1, userId);
            ResultSet mistakeRs = mistakeStmt.executeQuery();
            
            if (mistakeRs.next()) {
                stats.setTotalMistakes(mistakeRs.getInt("mistake_count"));
            } else {
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
        switch (languageId) {
            case 1: return "Spanish";
            case 2: return "Romanian";
            case 3: return "German";
            case 4: return "French";
            default: return "Unknown Language";
        }
    }
}