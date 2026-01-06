/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProgressDAO {
    
    // Save test results (insert or update)
    public boolean saveTestResult(int userId, int unitId, int percentageComplete) {
        // First, check if we should insert or update
        String checkSql = "SELECT COUNT(*) as count FROM Progress WHERE UserID = ? AND UnitID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check if record exists
            boolean recordExists = false;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, userId);
                checkStmt.setInt(2, unitId);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt("count") > 0) {
                    recordExists = true;
                }
            }
            
            if (recordExists) {
                // Update existing record
                return updateProgress(conn, userId, unitId, percentageComplete);
            } else {
                // Insert new record
                return insertProgress(conn, userId, unitId, percentageComplete);
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving test result for UserID=" + userId + ", UnitID=" + unitId + ": " + e.getMessage());
            return false;
        }
    }
    
    // Insert new progress record
    private boolean insertProgress(Connection conn, int userId, int unitId, int percentageComplete) throws SQLException {
        String sql = "INSERT INTO Progress (UserID, UnitID, percentageComplete) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, unitId);
            pstmt.setInt(3, percentageComplete);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }
    
    // Update existing progress record
    private boolean updateProgress(Connection conn, int userId, int unitId, int percentageComplete) throws SQLException {
        String sql = "UPDATE Progress SET percentageComplete = ? WHERE UserID = ? AND UnitID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, percentageComplete);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, unitId);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
        }
    }
    
    // Save only if new score is higher
    public boolean saveHighestScore(int userId, int unitId, int newPercentage) {
        int currentScore = getProgressScore(userId, unitId);
        
        // If no record exists (-1) or new score is higher
        if (currentScore == -1 || newPercentage > currentScore) {
            System.out.println("Saving new high score: " + newPercentage + "% (previous: " + 
                             (currentScore == -1 ? "none" : currentScore + "%") + ")");
            return saveTestResult(userId, unitId, newPercentage);
        } else {
            System.out.println("Not saving score " + newPercentage + "%, current high score is " + currentScore + "%");
            return false;
        }
    }
    
    // Get progress score for a user and unit
    public int getProgressScore(int userId, int unitId) {
        String sql = "SELECT percentageComplete FROM Progress WHERE UserID = ? AND UnitID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, unitId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("percentageComplete");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting progress score: " + e.getMessage());
        }
        return -1; // -1 means no record found
    }
    
    // Get all progress for a user
    public Map<Integer, Integer> getUserProgress(int userId) {
        Map<Integer, Integer> progress = new HashMap<>();
        String sql = "SELECT UnitID, percentageComplete FROM Progress WHERE UserID = ? ORDER BY UnitID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                progress.put(rs.getInt("UnitID"), rs.getInt("percentageComplete"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user progress: " + e.getMessage());
        }
        return progress;
    }
    
    // Simple version - just insert, don't check for existing
    public boolean saveSimpleTestResult(int userId, int unitId, int percentageComplete) {
        String sql = "INSERT INTO Progress (UserID, UnitID, percentageComplete) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE percentageComplete = VALUES(percentageComplete)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, unitId);
            pstmt.setInt(3, percentageComplete);
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error saving simple test result: " + e.getMessage());
            return false;
        }
    }
    
    // Check if Progress table exists
    public boolean checkProgressTableExists() {
        String sql = "SHOW TABLES LIKE 'Progress'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            return rs.next(); // Returns true if table exists
            
        } catch (SQLException e) {
            System.err.println("Error checking if Progress table exists: " + e.getMessage());
            return false;
        }
    }
    
    // Create Progress table if it doesn't exist
    public boolean createProgressTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS Progress (" +
                    "ProgressID INT AUTO_INCREMENT PRIMARY KEY," +
                    "UserID INT NOT NULL," +
                    "UnitID INT NOT NULL," +
                    "percentageComplete INT NOT NULL," +
                    "dateCompleted TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "UNIQUE KEY unique_user_unit (UserID, UnitID)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.execute();
            System.out.println("Progress table created/verified successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error creating Progress table: " + e.getMessage());
            return false;
        }
    }
}
