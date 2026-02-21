package com.raver.iemsmaven.Utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Filter validation helpers.
 * Only tests pure validation logic (no database calls).
 */
class FilterTest {

    @Nested
    @DisplayName("REQUIRED validators")
    class RequiredTests {

        // --- name ---

        @Test
        @DisplayName("Valid name returns empty string")
        void name_valid() {
            assertEquals("", Filter.REQUIRED.name("John", "First Name"));
        }

        @Test
        @DisplayName("Empty name returns error")
        void name_empty() {
            assertEquals("First Name can't be empty", Filter.REQUIRED.name("", "First Name"));
        }

        @Test
        @DisplayName("Null name returns error")
        void name_null() {
            assertEquals("First Name can't be empty", Filter.REQUIRED.name(null, "First Name"));
        }

        @Test
        @DisplayName("Name with numbers returns error")
        void name_withNumbers() {
            assertEquals("Invalid First Name", Filter.REQUIRED.name("John123", "First Name"));
        }

        @Test
        @DisplayName("Name with special chars returns error")
        void name_withSpecialChars() {
            assertEquals("Invalid Last Name", Filter.REQUIRED.name("O'Brien", "Last Name"));
        }

        // --- email ---

        @Test
        @DisplayName("Valid email returns empty")
        void email_valid() {
            assertEquals("", Filter.REQUIRED.email("user@example.com"));
        }

        @Test
        @DisplayName("Invalid email returns error")
        void email_invalid() {
            assertEquals("Invalid email", Filter.REQUIRED.email("not-an-email"));
        }

        @Test
        @DisplayName("Null email returns error")
        void email_null() {
            assertEquals("Invalid email", Filter.REQUIRED.email(null));
        }

        @Test
        @DisplayName("Email with subdomain is valid")
        void email_subdomain() {
            assertEquals("", Filter.REQUIRED.email("user@mail.example.com"));
        }

        // --- password ---

        @Test
        @DisplayName("Valid password with matching repeat")
        void password_valid() {
            assertEquals("", Filter.REQUIRED.password("MyPass123", "MyPass123"));
        }

        @Test
        @DisplayName("Empty password returns error")
        void password_empty() {
            assertEquals("Password can't be empty", Filter.REQUIRED.password("", ""));
        }

        @Test
        @DisplayName("Null password returns error")
        void password_null() {
            assertEquals("Password can't be empty", Filter.REQUIRED.password(null, null));
        }

        @Test
        @DisplayName("Short password returns error")
        void password_tooShort() {
            String result = Filter.REQUIRED.password("Ab1", "Ab1");
            assertTrue(result.contains("at least contain 8 characters"));
        }

        @Test
        @DisplayName("No uppercase returns error")
        void password_noUppercase() {
            String result = Filter.REQUIRED.password("lowercase1", "lowercase1");
            assertTrue(result.contains("uppercase letter"));
        }

        @Test
        @DisplayName("No numbers returns error")
        void password_noNumbers() {
            String result = Filter.REQUIRED.password("NoNumbers", "NoNumbers");
            assertTrue(result.contains("contain a number"));
        }

        @Test
        @DisplayName("Mismatched passwords return error")
        void password_mismatch() {
            String result = Filter.REQUIRED.password("Password1", "Different1");
            assertTrue(result.contains("don't match"));
        }

        @Test
        @DisplayName("Multiple password issues return multiple errors")
        void password_multipleIssues() {
            String result = Filter.REQUIRED.password("abc", "xyz");
            assertTrue(result.contains("8 characters"));
            assertTrue(result.contains("uppercase"));
            assertTrue(result.contains("number"));
            assertTrue(result.contains("don't match"));
        }

        // --- privilege ---

        @Test
        @DisplayName("Select privilege returns error")
        void privilege_select() {
            assertEquals("Privilege can't be empty", Filter.REQUIRED.privilege("Select"));
        }

        @Test
        @DisplayName("Valid privilege returns empty")
        void privilege_valid() {
            assertEquals("", Filter.REQUIRED.privilege("Admin"));
        }
    }

    @Nested
    @DisplayName("OPTIONAL validators")
    class OptionalTests {

        @Test
        @DisplayName("Empty optional name is allowed")
        void name_empty() {
            assertEquals("", Filter.OPTIONAL.name("", "Middle Name"));
        }

        @Test
        @DisplayName("Null optional name is allowed")
        void name_null() {
            assertEquals("", Filter.OPTIONAL.name(null, "Middle Name"));
        }

        @Test
        @DisplayName("Valid optional name returns empty")
        void name_valid() {
            assertEquals("", Filter.OPTIONAL.name("Michael", "Middle Name"));
        }

        @Test
        @DisplayName("Invalid optional name returns error")
        void name_invalid() {
            assertEquals("Invalid Middle Name", Filter.OPTIONAL.name("Mike123", "Middle Name"));
        }

        @Test
        @DisplayName("Empty optional password is allowed")
        void password_empty() {
            assertEquals("", Filter.OPTIONAL.password("", ""));
        }

        @Test
        @DisplayName("Null optional password is allowed")
        void password_null() {
            assertEquals("", Filter.OPTIONAL.password(null, null));
        }

        @Test
        @DisplayName("Valid 11-digit contact number")
        void contactNum_valid() {
            assertEquals("", Filter.OPTIONAL.contactNum("09171234567", "Contact"));
        }

        @Test
        @DisplayName("Invalid contact number returns error")
        void contactNum_invalid() {
            assertEquals("Invalid contact number", Filter.OPTIONAL.contactNum("12345", "Contact"));
        }

        @Test
        @DisplayName("Null contact number returns error")
        void contactNum_null() {
            assertEquals("Invalid contact number", Filter.OPTIONAL.contactNum(null, "Contact"));
        }
    }

    @Nested
    @DisplayName("TIME overlap checks")
    class TimeTests {

        @Test
        @DisplayName("Overlapping time ranges")
        void isOverlapping_true() {
            assertTrue(Filter.TIME.isOverlapping("08:00", "12:00", "10:00", "14:00"));
        }

        @Test
        @DisplayName("Non-overlapping time ranges")
        void isOverlapping_false() {
            assertFalse(Filter.TIME.isOverlapping("08:00", "10:00", "11:00", "14:00"));
        }

        @Test
        @DisplayName("Adjacent time ranges do not overlap")
        void isOverlapping_adjacent() {
            // 08:00-10:00 and 10:00-12:00 — endTime1 is 09:59, so no overlap
            assertFalse(Filter.TIME.isOverlapping("08:00", "10:00", "10:00", "12:00"));
        }
    }

    @Nested
    @DisplayName("DATE overlap checks")
    class DateTests {

        @Test
        @DisplayName("Overlapping date ranges")
        void isOverlapping_true() {
            assertTrue(Filter.DATE.isOverlapping("2025-01-01", "2025-01-15", "2025-01-10", "2025-01-20"));
        }

        @Test
        @DisplayName("Non-overlapping date ranges")
        void isOverlapping_false() {
            assertFalse(Filter.DATE.isOverlapping("2025-01-01", "2025-01-10", "2025-01-15", "2025-01-20"));
        }

        @Test
        @DisplayName("Same start and end date overlap")
        void isOverlapping_sameDay() {
            assertTrue(Filter.DATE.isOverlapping("2025-06-15", "2025-06-15", "2025-06-15", "2025-06-15"));
        }
    }
}
