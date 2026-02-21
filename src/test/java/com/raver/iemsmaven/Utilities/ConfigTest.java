package com.raver.iemsmaven.Utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Config.
 * Tests default values and the getDbName() URL parser.
 * Note: These tests require config.properties in the classpath.
 */
class ConfigTest {

    @Test
    @DisplayName("Secret key is loaded and not empty")
    void getSecretKey_isNotEmpty() {
        String key = Config.getSecretKey();
        assertNotNull(key);
        assertFalse(key.isEmpty());
    }

    @Test
    @DisplayName("DB URL is loaded")
    void getDbUrl_isNotNull() {
        String url = Config.getDbUrl();
        assertNotNull(url);
        assertTrue(url.startsWith("jdbc:mysql://"));
    }

    @Test
    @DisplayName("DB user is loaded")
    void getDbUser_isNotNull() {
        assertNotNull(Config.getDbUser());
    }

    @Test
    @DisplayName("DB password is loaded (may be empty)")
    void getDbPassword_isNotNull() {
        assertNotNull(Config.getDbPassword());
    }

    @Test
    @DisplayName("getDbName extracts database name from URL")
    void getDbName_extractsName() {
        String dbName = Config.getDbName();
        assertNotNull(dbName);
        assertFalse(dbName.isEmpty());
        // Should not contain slashes or protocol parts
        assertFalse(dbName.contains("/"));
        assertFalse(dbName.contains(":"));
    }
}
