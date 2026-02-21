/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Utilities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @deprecated This class uses unsalted SHA-256 hashing which is insecure.
 * Use {@link PasswordUtil} (jBCrypt) for all new password operations.
 * This class is kept only for verifying legacy password hashes during login migration.
 * @see PasswordUtil
 * @author admin
 */
@Deprecated
public class Encryption {
    /**
     * @deprecated Use {@link PasswordUtil#hashPassword(String)} instead.
     */
    @Deprecated
    public static String hashPassword(String password) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add password bytes to digest
            md.update(password.getBytes(StandardCharsets.UTF_8));

            // Get the hashed password
            byte[] hashedPassword = md.digest();

            // Convert byte array to a string representation
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashedPassword) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Handle the exception or rethrow
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @deprecated Use {@link PasswordUtil#verifyPassword(String, String)} instead.
     * This method only exists to support legacy SHA-256 hash migration.
     */
    @Deprecated
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        // Hash the plaintext password
        String hashedInputPassword = hashPassword(plainPassword);

        // Compare the hashed passwords
        return hashedInputPassword.equals(hashedPassword);
    }
    
}
