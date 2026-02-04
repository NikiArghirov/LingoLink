/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

/**
 *
 * @author 4-narghirov
 */
import org.mindrot.jbcrypt.BCrypt;


public class HasherFunction {
    
    public static String hashPass(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }
    
    //public static boolean verifyPass(String plainTestPassword, String ){
        //return BCrypt.checkpw(plainTextPassword, );
    //}
}
