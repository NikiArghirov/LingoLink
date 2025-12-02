/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

/**
 *
 * @author nikia
 */
public class Question {
    

    private int questionId;
    private int quizId;
    private String text;
    private String correctAnswer;
    private String difficulty;
    
    // Constructor
    public Question(int questionId, int quizId, String text, String correctAnswer, String difficulty) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.text = text;
        this.correctAnswer = correctAnswer;
        this.difficulty = difficulty;
    }
    
    // Getters and Setters
    public int getQuestionId() { return questionId; }
    public int getQuizId() { return quizId; }
    public String getText() { return text; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String getDifficulty() { return difficulty; }
    
    public void setQuestionId(int questionId) { this.questionId = questionId; }
    public void setQuizId(int quizId) { this.quizId = quizId; }
    public void setText(String text) { this.text = text; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    @Override
    public String toString() {
        return "Question ID: " + questionId + 
               "\nText: " + text + 
               "\nCorrect Answer: " + correctAnswer + 
               "\nDifficulty: " + difficulty;
    }
}