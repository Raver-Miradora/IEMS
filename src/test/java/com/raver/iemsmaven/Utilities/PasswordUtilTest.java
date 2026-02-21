package com.raver.iemsmaven.Utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PasswordUtil (jBCrypt).
 */
class PasswordUtilTest {

    @Test
    @DisplayName("Hash password produces a bcrypt hash")
    void hashPassword_producesBcryptHash() {
        String hash = PasswordUtil.hashPassword("MySecure1");
        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a$"), "Hash should start with $2a$ (bcrypt)");
    }

    @Test
    @DisplayName("Same password produces different hashes (random salt)")
    void hashPassword_differentSalts() {
        String hash1 = PasswordUtil.hashPassword("SamePassword1");
        String hash2 = PasswordUtil.hashPassword("SamePassword1");
        assertNotEquals(hash1, hash2, "Each hash should have a unique salt");
    }

    @Test
    @DisplayName("Verify correct password returns true")
    void verifyPassword_correctPassword() {
        String password = "TestPass123";
        String hash = PasswordUtil.hashPassword(password);
        assertTrue(PasswordUtil.verifyPassword(password, hash));
    }

    @Test
    @DisplayName("Verify wrong password returns false")
    void verifyPassword_wrongPassword() {
        String hash = PasswordUtil.hashPassword("CorrectPassword1");
        assertFalse(PasswordUtil.verifyPassword("WrongPassword1", hash));
    }

    @Test
    @DisplayName("Verify returns false for null hash")
    void verifyPassword_nullHash() {
        assertFalse(PasswordUtil.verifyPassword("anything", null));
    }

    @Test
    @DisplayName("Verify returns false for non-bcrypt hash (legacy SHA-256)")
    void verifyPassword_legacyShaHash() {
        // A SHA-256 hex string won't start with "$2a$"
        String legacyHash = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        assertFalse(PasswordUtil.verifyPassword("password", legacyHash));
    }
}
