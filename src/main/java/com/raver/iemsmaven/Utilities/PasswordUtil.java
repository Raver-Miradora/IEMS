package com.raver.iemsmaven.Utilities;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * Hashes a password using the secure bcrypt algorithm.
     * @param plainPassword The password to hash.
     * @return A string containing the hashed password.
     */
    public static String hashPassword(String plainPassword) {
        // gensalt() automatically generates a random salt
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    /**
     * Verifies a plain-text password against a stored bcrypt hash.
     * @param plainPassword The password from user input.
     * @param hashedPassword The hash stored in the database.
     * @return true if the passwords match, false otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            // This is not a bcrypt hash. Handle legacy or error.
            return false; 
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
