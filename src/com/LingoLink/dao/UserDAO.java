/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Utility.HasherFunction;

public class UserDAO {

    public static boolean authentication(String username, String password) {
        try {
            Connection connect = DatabaseConnection.getConnection();
            System.out.println("Database connection successful");

            PreparedStatement statement = connect.prepareStatement("SELECT * FROM User WHERE Username = ?");
            statement.setString(1, username);

            System.out.println("Querying for Username: '" + username + "'");

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("Password");
                int userId = rs.getInt("UserID");
                String dbUsername = rs.getString("Username");
                
                if (HasherFunction.verifyPass(password, hashedPassword)) {
                    System.out.println("Login successful for user: " + dbUsername + " (ID: " + userId + ")");
                    return true;
                } else {
                    System.out.println("Login failed - invalid password for user: " + username);
                    return false;
                }
            } else {
                System.out.println("Login failed - no user found with username: " + username);
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

            PreparedStatement statement = connect.prepareStatement("SELECT UserID, Password FROM User WHERE Username = ?");
            statement.setString(1, username);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("Password");
                int userId = rs.getInt("UserID");
                
                if (HasherFunction.verifyPass(password, hashedPassword)) {
                    System.out.println("User ID found: " + userId + " for username: " + username);
                    return userId;
                } else {
                    System.out.println("Invalid password for username: " + username);
                    return -1;
                }
            } else {
                System.out.println("No user found with username: " + username);
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return -1;
        }
    }

    public static boolean registerUser(String username, String password, String email, int languageId) {
        System.out.println("=== START REGISTER USER ===");
        System.out.println("Username: " + username);
        
        try {
            Connection connect = DatabaseConnection.getConnection();
            System.out.println("Database connection successful");
            
            String hashedPassword = HasherFunction.hashPass(password);
            System.out.println("Password hashed successfully");
            
            PreparedStatement maxStmt = connect.prepareStatement("SELECT MAX(UserID) as maxId FROM User");
            ResultSet maxRs = maxStmt.executeQuery();
            int nextUserId = 1;
            if (maxRs.next()) {
                nextUserId = maxRs.getInt("maxId") + 1;
                System.out.println("Current max UserID: " + (nextUserId - 1));
            }
            System.out.println("Next UserID will be: " + nextUserId);
            
            PreparedStatement checkStmt = connect.prepareStatement("SELECT UserID FROM User WHERE Username = ?");
            checkStmt.setString(1, username);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (checkRs.next()) {
                System.out.println("Username already exists");
                return false;
            }
            
            PreparedStatement insertStmt = connect.prepareStatement(
                "INSERT INTO User (UserID, Username, Password, Email, LanguageID) VALUES (?, ?, ?, ?, ?)"
            );
            
            insertStmt.setInt(1, nextUserId);
            insertStmt.setString(2, username);
            insertStmt.setString(3, hashedPassword);
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

    public static void migratePasswordsToHash() {
        System.out.println("=== STARTING PASSWORD MIGRATION ===");
        try {
            Connection connect = DatabaseConnection.getConnection();
            
            PreparedStatement selectStmt = connect.prepareStatement("SELECT UserID, Password FROM User");
            ResultSet rs = selectStmt.executeQuery();
            
            int migratedCount = 0;
            while (rs.next()) {
                int userId = rs.getInt("UserID");
                String currentPassword = rs.getString("Password");
                
                if (!currentPassword.startsWith("$2a$") && !currentPassword.startsWith("$2b$") && !currentPassword.startsWith("$2y$")) {
                    String hashedPassword = HasherFunction.hashPass(currentPassword);
                    
                    PreparedStatement updateStmt = connect.prepareStatement(
                        "UPDATE User SET Password = ? WHERE UserID = ?"
                    );
                    updateStmt.setString(1, hashedPassword);
                    updateStmt.setInt(2, userId);
                    updateStmt.executeUpdate();
                    
                    migratedCount++;
                    System.out.println("Migrated password for UserID: " + userId);
                } else {
                    System.out.println("UserID: " + userId + " - password already hashed");
                }
            }
            
            System.out.println("Password migration complete! Migrated " + migratedCount + " users.");
            
        } catch(SQLException e) {
            System.out.println("Migration error: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("=== END PASSWORD MIGRATION ===\n");
    }

    public static void testRegistration() {
        System.out.println("\n=== TESTING REGISTRATION WITH BCRYPT ===");

        String testUsername = "testuser_" + System.currentTimeMillis();
        String testPassword = "testpass123";
        String testEmail = testUsername + "@test.com";
        int testLanguageId = 2;

        System.out.println("Test username: " + testUsername);
        System.out.println("Test password: " + testPassword);
        System.out.println("Test language ID: " + testLanguageId);

        boolean success = registerUser(testUsername, testPassword, testEmail, testLanguageId);

        if (success) {
            System.out.println("TEST: Registration SUCCESSFUL");

            boolean canLogin = authentication(testUsername, testPassword);
            System.out.println("Can login with new user: " + canLogin);
            
            boolean wrongLogin = authentication(testUsername, "wrongpassword");
            System.out.println("Login with wrong password: " + wrongLogin);
        } else {
            System.out.println("TEST: Registration FAILED");
        }

        testAllUsers();
    }

    public static void testAllUsers() {
        try {
            Connection connect = DatabaseConnection.getConnection();
            PreparedStatement statement = connect.prepareStatement("SELECT * FROM User");
            ResultSet rs = statement.executeQuery();

            System.out.println("=== ALL USERS IN DATABASE ===");
            while (rs.next()) {
                String password = rs.getString("Password");
                String passwordDisplay = password.startsWith("$2") ? 
                    password.substring(0, 20) + "..." : password;
                
                System.out.println("UserID: " + rs.getInt("UserID")
                        + ", Username: '" + rs.getString("Username") + "'"
                        + ", Password: '" + passwordDisplay + "'"
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

    public static void main(String[] args) {
        System.out.println("=== USERDAO BCRYPT TEST ===");
        testConnection();
        testRegistration();
        testAllUsers();
        migratePasswordsToHash();
    }
}
