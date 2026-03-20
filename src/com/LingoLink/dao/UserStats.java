/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

public class UserStats {
    private int userId;
    private String username;
    private String email;
    private int languageId;
    private int totalUnitsTaken;
    private int unitsPassed;
    private double averageScore;
    private int highestScore;
    private int lowestScore;
    private int totalMistakes;
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public int getLanguageId() { return languageId; }
    public void setLanguageId(int languageId) { this.languageId = languageId; }
    
    public int getTotalUnitsTaken() { return totalUnitsTaken; }
    public void setTotalUnitsTaken(int totalUnitsTaken) { this.totalUnitsTaken = totalUnitsTaken; }
    
    public int getUnitsPassed() { return unitsPassed; }
    public void setUnitsPassed(int unitsPassed) { this.unitsPassed = unitsPassed; }
    
    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    
    public int getHighestScore() { return highestScore; }
    public void setHighestScore(int highestScore) { this.highestScore = highestScore; }
    
    public int getLowestScore() { return lowestScore; }
    public void setLowestScore(int lowestScore) { this.lowestScore = lowestScore; }
    
    public int getTotalMistakes() { return totalMistakes; }
    public void setTotalMistakes(int totalMistakes) { this.totalMistakes = totalMistakes; }
    
    public double getPassRate() {
        if (totalUnitsTaken == 0) return 0;
        return (unitsPassed * 100.0) / totalUnitsTaken;
    }
    
    public String getFormattedStats() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════\n");
        sb.append("           USER PROFILE\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("📛 Username: ").append(username).append("\n");
        sb.append("🌍 Language: ").append(getLanguageName()).append("\n\n");
        sb.append("═══════════════════════════════════════\n");
        sb.append("           TEST STATISTICS\n");
        sb.append("═══════════════════════════════════════\n\n");
        sb.append("📊 Units Taken: ").append(totalUnitsTaken).append("\n");
        sb.append("✅ Units Passed: ").append(unitsPassed).append("\n");
        sb.append("📈 Pass Rate: ").append(String.format("%.1f", getPassRate())).append("%\n");
        sb.append("⭐ Average Score: ").append(String.format("%.1f", averageScore)).append("%\n");
        sb.append("🏆 Highest Score: ").append(highestScore).append("%\n");
        sb.append("📉 Lowest Score: ").append(lowestScore).append("%\n");
        sb.append("❌ Total Mistakes: ").append(totalMistakes).append("\n\n");
        sb.append("═══════════════════════════════════════\n");
        
        if (unitsPassed >= 5) {
            sb.append("🏅 Achievement: Bronze Learner\n");
        }
        if (unitsPassed >= 10) {
            sb.append("🥈 Achievement: Silver Scholar\n");
        }
        if (unitsPassed >= 15) {
            sb.append("🥇 Achievement: Gold Master\n");
        }
        if (unitsPassed >= 20) {
            sb.append("💎 Achievement: Language Legend\n");
        }
        
        return sb.toString();
    }
    
    private String getLanguageName() {
        switch (languageId) {
            case 1: return "Spanish";
            case 2: return "Romanian";
            case 3: return "German";
            case 4: return "French";
            default: return "Unknown Language";
        }
    }
}
