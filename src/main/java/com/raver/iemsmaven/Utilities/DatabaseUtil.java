/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Utilities;

import java.sql.*;

/**
 *
 * @author admin
 */
public class DatabaseUtil {
    
    public static Connection getConnection(){
        Connection connection;
        try{
            connection = DriverManager.getConnection(
                Config.getDbUrl(),
                Config.getDbUser(),
                Config.getDbPassword()
            );
            return connection;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static void executeQuery(String query){
        Connection con = getConnection();
        Statement st;
        try{
            st = con.createStatement();
            st.executeUpdate(query);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
