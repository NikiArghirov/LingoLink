/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.LingoLink.dao;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
/**
 *
 * @author niki
 */
public class UserDAO {
    public static boolean authentication(String User, String Password){
        try {
             Connection connect = DatabaseConnection.getConnection();
             PreparedStatement statement = connect.prepareStatement("SELECT * FROM User WHERE Username = ? AND Password = ?");
             
             statement.setString(1, User);
             statement.setString(2, Password);
             
             ResultSet rs = statement.executeQuery();
             if (rs.next()){
                 System.out.println(rs);
                 System.out.println("user true");
                 return true;
             } else{
                 System.out.println("false");
                 return false;
             }
        } catch(SQLException e) {
            System.out.println(e);
            return false;
        }
    }
}
