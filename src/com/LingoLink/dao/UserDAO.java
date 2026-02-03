/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static boolean authentication(String username, String password) {
        try {
            Connection connect = DatabaseConnection.getConnection();
            System.out.println("Database connection successful");

            PreparedStatement statement = connect.prepareStatement("SELECT * FROM User WHERE Username = ? AND Password = ?");

            statement.setString(1, username);
            statement.setString(2, password);

            System.out.println("Querying for Username: '" + username + "', Password: '" + password + "'");

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("UserID");
                String dbUsername = rs.getString("Username");
                System.out.println("Login successful for user: " + dbUsername + " (ID: " + userId + ")");
                return true;
            } else {
                System.out.println("Login failed - no matching record found");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserIdByCredentials(String username, String password) {
        try {
            Connection connect = DatabaseConnection.getConnection();

            PreparedStatement statement = connect.prepareStatement("SELECT UserID FROM User WHERE Username = ? AND Password = ?");

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("UserID");
                System.out.println("User ID found: " + userId + " for username: " + username);
                return userId;
            } else {
                System.out.println("No user ID found for credentials");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return -1;
        }
    }

    public static void testAllUsers() {
        try {
            Connection connect = DatabaseConnection.getConnection();

            PreparedStatement statement = connect.prepareStatement("SELECT * FROM User");
            ResultSet rs = statement.executeQuery();

            System.out.println("=== ALL USERS IN DATABASE ===");
            while (rs.next()) {
                System.out.println("UserID: " + rs.getInt("UserID")
                        + ", Username: '" + rs.getString("Username") + "'"
                        + ", Password: '" + rs.getString("Password") + "'"
                        + ", Email: '" + rs.getString("Email") + "'"
                        + ", LanguageID: " + rs.getInt("LanguageID"));
            }
            System.out.println("=== END USER LIST ===");

        } catch (SQLException e) {
            System.out.println("Error listing users: " + e.getMessage());
        }
    }

    public static String getUsernameById(int userId) {
        try {
            Connection connect = DatabaseConnection.getConnection();

            PreparedStatement statement = connect.prepareStatement("SELECT Username FROM User WHERE UserID = ?");

            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("Username");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return null;
    }

    public static int getLanguageIdByUserId(int userId) {
        try {
            Connection connect = DatabaseConnection.getConnection();

            PreparedStatement statement = connect.prepareStatement("SELECT LanguageID FROM User WHERE UserID = ?");

            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("LanguageID");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        return -1;
    }

    public static boolean registerUser(String username, String password, String email, int languageId) {
    System.out.println("=== START REGISTER USER ===");
    System.out.println("Username: " + username);
    
    try {
        Connection connect = DatabaseConnection.getConnection();
        System.out.println("Database connection successful");
        
        // FIRST: Get the maximum UserID and calculate next one
        PreparedStatement maxStmt = connect.prepareStatement("SELECT MAX(UserID) as maxId FROM User");
        ResultSet maxRs = maxStmt.executeQuery();
        int nextUserId = 1;
        if (maxRs.next()) {
            nextUserId = maxRs.getInt("maxId") + 1;
            System.out.println("Current max UserID: " + (nextUserId - 1));
        }
        System.out.println("Next UserID will be: " + nextUserId);
        
        // Check if username already exists
        PreparedStatement checkStmt = connect.prepareStatement("SELECT UserID FROM User WHERE Username = ?");
        checkStmt.setString(1, username);
        ResultSet checkRs = checkStmt.executeQuery();
        
        if (checkRs.next()) {
            System.out.println("Username already exists");
            return false;
        }
        
        // Insert with explicit UserID
        PreparedStatement insertStmt = connect.prepareStatement(
            "INSERT INTO User (UserID, Username, Password, Email, LanguageID) VALUES (?, ?, ?, ?, ?)"
        );
        
        insertStmt.setInt(1, nextUserId);
        insertStmt.setString(2, username);
        insertStmt.setString(3, password);
        insertStmt.setString(4, email);
        insertStmt.setInt(5, languageId);
        
        System.out.println("Executing INSERT with UserID: " + nextUserId);
        int rowsAffected = insertStmt.executeUpdate();
        
        if (rowsAffected > 0) {
            System.out.println("Registration successful! UserID: " + nextUserId);
            return true;
        } else {
            System.out.println("Registration failed");
            return false;
        }
        
    } catch(SQLException e) {
        System.out.println("Database error: " + e.getMessage());
        System.out.println("SQL State: " + e.getSQLState());
        System.out.println("Error Code: " + e.getErrorCode());
        e.printStackTrace();
        return false;
    } finally {
        System.out.println("=== END REGISTER USER ===\n");
    }
}

    // Test method for registration
    public static void testRegistration() {
        System.out.println("\n=== TESTING REGISTRATION ===");

        // Create a unique username
        String testUsername = "testuser_" + System.currentTimeMillis();
        String testPassword = "testpass123";
        String testEmail = testUsername + "@test.com";
        int testLanguageId = 2; // Romanian

        System.out.println("Test username: " + testUsername);
        System.out.println("Test language ID: " + testLanguageId);

        boolean success = registerUser(testUsername, testPassword, testEmail, testLanguageId);

        if (success) {
            System.out.println("TEST: Registration SUCCESSFUL");

            // Try to login with new user
            boolean canLogin = authentication(testUsername, testPassword);
            System.out.println("Can login with new user: " + canLogin);
        } else {
            System.out.println("TEST: Registration FAILED");
        }

        // Show all users
        testAllUsers();
    }

    // Test database connection
    public static void testConnection() {
        try {
            Connection connect = DatabaseConnection.getConnection();
            System.out.println("Database connection TEST: SUCCESS");
            connect.close();
        } catch (SQLException e) {
            System.out.println("Database connection TEST: FAILED");
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        System.out.println("=== USERDAO TEST ===");

        // Test connection
        testConnection();

        // Test registration
        testRegistration();

        // Show all users
        testAllUsers();
    }
    public static void describeUserTable() {
    try {
        Connection connect = DatabaseConnection.getConnection();
        PreparedStatement statement = connect.prepareStatement("DESCRIBE User");
        ResultSet rs = statement.executeQuery();
        
        System.out.println("=== USER TABLE STRUCTURE ===");
        while (rs.next()) {
            System.out.println("Column: " + rs.getString("Field") + 
                             ", Type: " + rs.getString("Type") + 
                             ", Null: " + rs.getString("Null") + 
                             ", Key: " + rs.getString("Key") + 
                             ", Default: " + rs.getString("Default") + 
                             ", Extra: " + rs.getString("Extra"));
        }
        System.out.println("=== END TABLE STRUCTURE ===");
        
    } catch(SQLException e) {
        System.out.println("Error describing table: " + e.getMessage());
    }
}
}
