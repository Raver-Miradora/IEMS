package com.raver.iemsmaven.Utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the legacy Encryption class (SHA-256).
 * Tests ensure backward compatibility for legacy hash migration.
 */
@SuppressWarnings("deprecation")
class EncryptionTest {

    @Test
    @DisplayName("SHA-256 hash produces 64-char hex string")
    void hashPassword_produces64CharHex() {
        String hash = Encryption.hashPassword("password");
        assertNotNull(hash);
        assertEquals(64, hash.length(), "SHA-256 hex should be 64 characters");
        assertTrue(hash.matches("[0-9a-f]+"), "Should be lowercase hex");
    }

    @Test
    @DisplayName("Same input produces same hash (deterministic)")
    void hashPassword_deterministic() {
        String hash1 = Encryption.hashPassword("Hello123");
        String hash2 = Encryption.hashPassword("Hello123");
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Different inputs produce different hashes")
    void hashPassword_differentInputs() {
        String hash1 = Encryption.hashPassword("password1");
        String hash2 = Encryption.hashPassword("password2");
        assertNotEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Known SHA-256 of 'password' matches expected value")
    void hashPassword_knownValue() {
        // SHA-256("password") = 5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8
        String expected = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";
        assertEquals(expected, Encryption.hashPassword("password"));
    }

    @Test
    @DisplayName("Verify correct password returns true")
    void verifyPassword_correct() {
        String hash = Encryption.hashPassword("MyPassword");
        assertTrue(Encryption.verifyPassword("MyPassword", hash));
    }

    @Test
    @DisplayName("Verify wrong password returns false")
    void verifyPassword_wrong() {
        String hash = Encryption.hashPassword("MyPassword");
        assertFalse(Encryption.verifyPassword("WrongPassword", hash));
    }
}
