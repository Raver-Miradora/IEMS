package com.raver.iemsmaven.Utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StringUtil.
 */
class StringUtilTest {

    // --- createFullName ---

    @Test
    @DisplayName("Full name with all parts")
    void createFullName_allParts() {
        String result = StringUtil.createFullName("John", "Michael", "Doe", "Jr.");
        assertEquals("John M. Doe Jr.", result);
    }

    @Test
    @DisplayName("Full name with null middle name and suffix")
    void createFullName_nullMiddleAndSuffix() {
        String result = StringUtil.createFullName("John", null, "Doe", null);
        assertEquals("John Doe", result);
    }

    @Test
    @DisplayName("Full name with empty strings")
    void createFullName_emptyStrings() {
        String result = StringUtil.createFullName("Jane", "", "Smith", "");
        assertEquals("Jane Smith", result);
    }

    @Test
    @DisplayName("Full name with all nulls")
    void createFullName_allNull() {
        String result = StringUtil.createFullName(null, null, null, null);
        assertEquals("", result);
    }

    // --- formatFullName ---

    @Test
    @DisplayName("Format full name into surname/given parts")
    void formatFullName_standard() {
        String[] parts = StringUtil.formatFullName("John", "Michael", "Doe", "Jr.");
        assertEquals("Doe,", parts[0]);
        assertEquals("John M. Jr.", parts[1]);
    }

    @Test
    @DisplayName("Format full name with no middle or suffix")
    void formatFullName_minimal() {
        String[] parts = StringUtil.formatFullName("Jane", null, "Smith", null);
        assertEquals("Smith,", parts[0]);
        assertEquals("Jane", parts[1]);
    }

    // --- convertToInitial ---

    @Test
    @DisplayName("Convert middle name to initial")
    void convertToInitial_standard() {
        assertEquals("M.", StringUtil.convertToInitial("Michael"));
    }

    @Test
    @DisplayName("Convert single char to initial")
    void convertToInitial_singleChar() {
        assertEquals("A.", StringUtil.convertToInitial("A"));
    }

    @Test
    @DisplayName("Convert null returns empty")
    void convertToInitial_null() {
        assertEquals("", StringUtil.convertToInitial(null));
    }

    @Test
    @DisplayName("Convert empty returns empty")
    void convertToInitial_empty() {
        assertEquals("", StringUtil.convertToInitial(""));
    }

    // --- getGreeting (time-dependent, just test it doesn't fail) ---

    @Test
    @DisplayName("getGreeting returns a non-empty string")
    void getGreeting_returnsNonEmpty() {
        String greeting = StringUtil.getGreeting();
        assertNotNull(greeting);
        assertFalse(greeting.isEmpty());
        assertTrue(
            greeting.equals("Good Morning") ||
            greeting.equals("Good Afternoon") ||
            greeting.equals("Good Evening")
        );
    }
}
