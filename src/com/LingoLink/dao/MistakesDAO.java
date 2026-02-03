/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.LingoLink.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MistakesDAO {
    
    
    public boolean createMistakesTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS Mistakes (" +
                    "MistakeID INT AUTO_INCREMENT PRIMARY KEY," +
                    "UserID INT NOT NULL," +
                    "QuestionID INT NOT NULL," +
                    "UserAnswer VARCHAR(255)," +
                    "CorrectAnswer VARCHAR(255)," +
                    "QuestionText TEXT," +
                    "Timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (UserID) REFERENCES User(UserID)," +
                    "FOREIGN KEY (QuestionID) REFERENCES Questions(QuestionID)" +
                    ")";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("Mistakes table created/verified successfully");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error creating Mistakes table: " + e.getMessage());
            return false;
        }
    }
    
    
    public boolean checkMistakesTableExists() {
        String sql = "SHOW TABLES LIKE 'Mistakes'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            return rs.next();
            
        } catch (SQLException e) {
            System.err.println("Error checking Mistakes table: " + e.getMessage());
            return false;
        }
    }
    
  
    public boolean saveMistake(int userId, int questionId, String userAnswer, 
                              String correctAnswer, String questionText) {
        String sql = "INSERT INTO Mistakes (UserID, QuestionID, UserAnswer, CorrectAnswer, QuestionText) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, questionId);
            pstmt.setString(3, userAnswer);
            pstmt.setString(4, correctAnswer);
            pstmt.setString(5, questionText);
            
            boolean success = pstmt.executeUpdate() > 0;
            if (success) {
                System.out.println("Mistake saved for UserID: " + userId + ", QuestionID: " + questionId);
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("Error saving mistake: " + e.getMessage());
            return false;
        }
    }
    
    
    public List<String> getUserMistakes(int userId) {
        List<String> mistakes = new ArrayList<>();
        String sql = "SELECT QuestionText, UserAnswer, CorrectAnswer FROM Mistakes " +
                    "WHERE UserID = ? ORDER BY Timestamp DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String question = rs.getString("QuestionText");
                String userAnswer = rs.getString("UserAnswer");
                String correctAnswer = rs.getString("CorrectAnswer");
                
                String mistake = String.format("Q: %s | Your answer: %s | Correct: %s", 
                                              question, userAnswer, correctAnswer);
                mistakes.add(mistake);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user mistakes: " + e.getMessage());
        }
        return mistakes;
    }
    
    
    public int getMistakeCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM Mistakes WHERE UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting mistake count: " + e.getMessage());
        }
        return 0;
    }
    
   
    public boolean clearUserMistakes(int userId) {
        String sql = "DELETE FROM Mistakes WHERE UserID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            int rowsDeleted = pstmt.executeUpdate();
            System.out.println("Cleared " + rowsDeleted + " mistakes for UserID: " + userId);
            return rowsDeleted > 0;
            
        } catch (SQLException e) {
            System.err.println("Error clearing mistakes: " + e.getMessage());
            return false;
        }
    }
}

