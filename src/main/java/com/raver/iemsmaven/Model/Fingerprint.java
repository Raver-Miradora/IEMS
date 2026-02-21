/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Model;

import com.raver.iemsmaven.Utilities.DatabaseUtil;
import java.util.Date;
import com.digitalpersona.uareu.Fmd;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.raver.iemsmaven.Utilities.Config;

public class Fingerprint {
    private int userId;
    private byte[] fmd;
    private int width;
    private int height;
    private int resolution;
    private int fingerPosition;
    private int cbeffId;
    private Date registerDate;

    // --- ORIGINAL CONSTRUCTOR ---
    public Fingerprint(int userId, byte[] fmd, int width, int height, int resolution, int fingerPosition, int cbeffId, Date registerDate) {
        this.userId = userId;
        this.fmd = fmd;
        this.width = width;
        this.height = height;
        this.resolution = resolution;
        this.fingerPosition = fingerPosition;
        this.cbeffId = cbeffId;
        this.registerDate = registerDate;
    }

    // --- NEW: SIMPLIFIED CONSTRUCTOR FOR CLOUD SYNC ---
    // (Fixes "Constructor cannot be applied to given types" error)
    public Fingerprint(int userId, byte[] fmd) {
        this.userId = userId;
        this.fmd = fmd;
        // Set defaults for metadata since Cloud Sync might not provide them
        this.width = 0;
        this.height = 0;
        this.resolution = 500; // Standard resolution
        this.fingerPosition = 0;
        this.cbeffId = 0;
        this.registerDate = new Date();
    }
    
    // --- Getters and Setters ---
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public byte[] getFmd() { return fmd; }
    public void setFmd(byte[] fmd) { this.fmd = fmd; }
    public Date getRegisterDate() { return registerDate; }
    public void setRegisterDate(Date registerDate) { this.registerDate = registerDate; }

    // --- NEW: CHECK IF FINGERPRINT EXISTS LOCALLY ---
    // (Fixes "cannot find symbol method hasFingerprint" error)
    public static boolean hasFingerprint(int userId) {
        String sql = "SELECT 1 FROM fingerprint WHERE user_id = ? LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if a record exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- NEW: SAVE INSTANCE TO DATABASE ---
    // (Fixes "cannot find symbol method save()" error)
    public boolean save() {
        // Uses the same AES_ENCRYPT logic as your insertFmd method
        String sql = "INSERT INTO fingerprint (user_id, fmd, width, height, resolution, finger_position, cbeff_id, register_date) " +
                     "VALUES (?, AES_ENCRYPT(?, ?), ?, ?, ?, ?, ?, NOW())";
        
        String key = Config.getSecretKey();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, this.userId);
            pstmt.setBytes(2, this.fmd);   // The Raw Byte Data
            pstmt.setString(3, key);       // The Secret Key
            
            // Use defaults stored in the object
            pstmt.setInt(4, this.width);
            pstmt.setInt(5, this.height);
            pstmt.setInt(6, this.resolution);
            pstmt.setInt(7, this.fingerPosition);
            pstmt.setInt(8, this.cbeffId);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error saving fingerprint locally: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- ORIGINAL STATIC INSERT METHOD (Keep this for local enrollment) ---
    public static void insertFmd(int userId, Fmd fmd) {
        String insertQuery = "INSERT INTO fingerprint (user_id, fmd, width, height, resolution, finger_position, cbeff_id, register_date) " +
                             "VALUES (?, AES_ENCRYPT(?, ?), ?, ?, ?, ?, ?, DATE(NOW()))";
        String key = Config.getSecretKey();

        try (PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(insertQuery)) {
            preparedStatement.setInt(1, userId);
            byte[] fmdData = fmd.getData();
            preparedStatement.setBytes(2, fmdData);
            preparedStatement.setString(3, key);
            preparedStatement.setInt(4, fmd.getWidth());
            preparedStatement.setInt(5, fmd.getHeight());
            preparedStatement.setInt(6, fmd.getResolution());
            preparedStatement.setInt(7, fmd.getViews()[0].getFingerPosition());
            preparedStatement.setInt(8, fmd.getCbeffId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    
    
    // --- READ FINGERPRINTS (DECRYPT) ---
    public static ObservableList<Fingerprint> getFingerprints() {
        ObservableList<Fingerprint> Fingerprints = FXCollections.observableArrayList();
        String key = Config.getSecretKey();
        
        String query = "SELECT user_id, AES_DECRYPT(fmd, ?) AS fmd, width, height, resolution, finger_position, cbeff_id, register_date " +
                       "FROM fingerprint";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, key);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {                
                Fingerprints.add(new Fingerprint(
                                  rs.getInt("user_id"),
                                  rs.getBytes("fmd"), 
                                  rs.getInt("width"),
                                  rs.getInt("height"),
                                  rs.getInt("resolution"),
                                  rs.getInt("finger_position"),
                                  rs.getInt("cbeff_id"),
                                  rs.getDate("register_date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Fingerprints;
    }
    
    // --- VERIFY FINGERPRINT ---
    public static int verifyFingerprint(byte[] newFmdData) {
        String sql = "SELECT user_id FROM fingerprint WHERE fmd = AES_ENCRYPT(?, ?)";
        String key = Config.getSecretKey();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBytes(1, newFmdData); 
            pstmt.setString(2, key);       

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id"); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }
    
    // --- HELPER METHODS ---
    public static int getFingerprintCountByUserId(int id) {
        int fingerprintCount = 0;
        String sqlQuery = "SELECT COUNT(*) AS fingerprint_count FROM fingerprint WHERE user_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                fingerprintCount = rs.getInt("fingerprint_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fingerprintCount;
    }

    public static String getLastFingerprintEnrollByUserId(int id) {
        String lastEnrollDate = "";
        String sqlQuery = "SELECT MAX(register_date) AS last_enroll_date FROM fingerprint WHERE user_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                lastEnrollDate = rs.getString("last_enroll_date");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lastEnrollDate;
    }

    public static void destroyEnrolledFingerprintsByUserId(int userId){
        String sqlQuery = "DELETE FROM fingerprint WHERE user_id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            System.out.println("Enrolled fingerprints for user ID " + userId + " deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}