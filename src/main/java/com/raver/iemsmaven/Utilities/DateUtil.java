package com.raver.iemsmaven.Utilities;

import java.time.LocalDate;

public class DateUtil {
    
    public static LocalDate sqlDateToLocalDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return date.toLocalDate();
    }
}