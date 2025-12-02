/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {
    
    // Get a single question by ID
    public Question getQuestionById(int questionId) {
        Question question = null;
        String sql = "SELECT * FROM Questions WHERE QuestionID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                question = new Question(
                    rs.getInt("QuestionID"),
                    rs.getInt("QuizID"),
                    rs.getString("text"),
                    rs.getString("correctAnswer"),
                    rs.getString("difficulty")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return question;
    }
    
    // Get all questions for a specific quiz
    public List<Question> getQuestionsByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM Questions WHERE QuizID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, quizId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question(
                    rs.getInt("QuestionID"),
                    rs.getInt("QuizID"),
                    rs.getString("text"),
                    rs.getString("correctAnswer"),
                    rs.getString("difficulty")
                );
                questions.add(question);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }
    
    // Get random question by difficulty
    public Question getRandomQuestionByDifficulty(String difficulty) {
        Question question = null;
        String sql = "SELECT * FROM Questions WHERE difficulty = ? ORDER BY RAND() LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, difficulty);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                question = new Question(
                    rs.getInt("QuestionID"),
                    rs.getInt("QuizID"),
                    rs.getString("text"),
                    rs.getString("correctAnswer"),
                    rs.getString("difficulty")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return question;
    }
    
    // NEW METHOD: Get questions for a specific unit
    public List<Question> getQuestionsByUnitId(int unitId) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.* FROM Questions q " +
                    "JOIN Quiz qu ON q.QuizID = qu.QuizID " +
                    "WHERE qu.UnitID = ? " +
                    "ORDER BY q.QuestionID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, unitId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question(
                    rs.getInt("QuestionID"),
                    rs.getInt("QuizID"),
                    rs.getString("text"),
                    rs.getString("correctAnswer"),
                    rs.getString("difficulty")
                );
                questions.add(question);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting questions by unit: " + e.getMessage());
        }
        return questions;
    }
    
    // NEW METHOD: Get quiz ID for a specific unit
    public int getQuizIdByUnitId(int unitId) {
        String sql = "SELECT QuizID FROM Quiz WHERE UnitID = ? LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, unitId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("QuizID");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting quiz ID: " + e.getMessage());
        }
        return -1; // Return -1 if not found
    }
    
    // NEW METHOD: Get unit name by ID
    public String getUnitNameById(int unitId) {
        String sql = "SELECT Name FROM Unit WHERE UnitID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, unitId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("Name");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unit name: " + e.getMessage());
        }
        return "Unit " + unitId;
    }
    
    // NEW METHOD: Check if answer is correct
    public boolean checkAnswer(int questionId, String userAnswer) {
        String sql = "SELECT correctAnswer FROM Questions WHERE QuestionID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, questionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String correctAnswer = rs.getString("correctAnswer");
                return userAnswer != null && 
                       userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking answer: " + e.getMessage());
        }
        return false;
    }
    
    // NEW METHOD: Get total questions count for a unit
    public int getQuestionCountByUnitId(int unitId) {
        String sql = "SELECT COUNT(*) as count FROM Questions q " +
                    "JOIN Quiz qu ON q.QuizID = qu.QuizID " +
                    "WHERE qu.UnitID = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, unitId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting question count: " + e.getMessage());
        }
        return 0;
    }
    
    // NEW METHOD: Get questions by unit and difficulty
    public List<Question> getQuestionsByUnitAndDifficulty(int unitId, String difficulty) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT q.* FROM Questions q " +
                    "JOIN Quiz qu ON q.QuizID = qu.QuizID " +
                    "WHERE qu.UnitID = ? AND q.difficulty = ? " +
                    "ORDER BY q.QuestionID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, unitId);
            pstmt.setString(2, difficulty);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Question question = new Question(
                    rs.getInt("QuestionID"),
                    rs.getInt("QuizID"),
                    rs.getString("text"),
                    rs.getString("correctAnswer"),
                    rs.getString("difficulty")
                );
                questions.add(question);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting questions by unit and difficulty: " + e.getMessage());
        }
        return questions;
    }
}