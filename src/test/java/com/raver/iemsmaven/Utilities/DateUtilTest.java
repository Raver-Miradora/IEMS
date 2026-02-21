package com.raver.iemsmaven.Utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DateUtil.
 */
class DateUtilTest {

    @Test
    @DisplayName("Convert SQL Date to LocalDate")
    void sqlDateToLocalDate_standard() {
        Date sqlDate = Date.valueOf("2025-06-15");
        LocalDate result = DateUtil.sqlDateToLocalDate(sqlDate);
        assertEquals(LocalDate.of(2025, 6, 15), result);
    }

    @Test
    @DisplayName("Null SQL Date returns null")
    void sqlDateToLocalDate_null() {
        assertNull(DateUtil.sqlDateToLocalDate(null));
    }

    @Test
    @DisplayName("Epoch date converts correctly")
    void sqlDateToLocalDate_epoch() {
        Date sqlDate = Date.valueOf("1970-01-01");
        LocalDate result = DateUtil.sqlDateToLocalDate(sqlDate);
        assertEquals(LocalDate.of(1970, 1, 1), result);
    }
}
