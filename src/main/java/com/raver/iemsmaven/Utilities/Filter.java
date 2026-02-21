/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Utilities;

import com.raver.iemsmaven.Model.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 *
 * @author admin
 */public class Filter {

    public static class REQUIRED {
        public static String name(String name, String fieldName) {
            if (name == null || name.isEmpty()) {
                return fieldName + " can't be empty";
            } else if (name.matches("[a-zA-Z ]+")) {
                return "";
            } else {
                return "Invalid " + fieldName;
            }
        }

        public static String password(String password, String repeatPassword) {
            StringBuilder prompt = new StringBuilder();

            if (password == null || password.isEmpty()) {
                return "Password can't be empty";
            }

            if (password.length() < 8) {
                prompt.append("Password should at least contain 8 characters\n");
            }

            if (!containsUppercase(password)) {
                prompt.append("Password should contain an uppercase letter\n");
            }

            if (!containsNumbers(password)) {
                prompt.append("Password should contain a number\n");
            }

            if (!password.equals(repeatPassword)) {
                prompt.append("Passwords don't match\n");
            }

            return prompt.toString();
        }

        public static String email(String email) {
            if (email == null) return "Invalid email";

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

            return email.matches(emailRegex) ? "" : "Invalid email";
        }

        public static String privilege(String privilege) {
            return "Select".equals(privilege) ? "Privilege can't be empty" : "";
        }

        public static String isEmailInUse(String email) {
            try {
                return User.isEmailUsed(email) ? "Email is already in use" : "";
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static String isEmailInUseExceptCurrentUser(String email, String currentEmail) {
            try {
                return User.isEmailUsedExceptForCurrentUser(email, currentEmail)
                        ? "Email is already in use" : "";
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class OPTIONAL {
        public static String name(String name, String fieldName) {
            if (name == null || name.isEmpty() || name.matches("[a-zA-Z ]+")) {
                return "";
            } else {
                return "Invalid " + fieldName;
            }
        }

        public static String password(String password, String repeatPassword) {
            if (password == null || password.isEmpty()) return "";

            StringBuilder prompt = new StringBuilder();

            if (password.length() < 8) {
                prompt.append("Password should at least contain 8 characters\n");
            }

            if (!containsUppercase(password)) {
                prompt.append("Password should contain an uppercase letter\n");
            }

            if (!containsNumbers(password)) {
                prompt.append("Password should contain a number\n");
            }

            if (!password.equals(repeatPassword)) {
                prompt.append("Passwords don't match\n");
            }

            return prompt.toString();
        }

        public static String contactNum(String contactNum, String fieldName) {
            if (contactNum == null || !contactNum.matches("\\d{11}")) {
                return "Invalid contact number";
            }
            return "";
        }
    }

    public static class TIME {
        public static boolean isOverlapping(String start1, String end1, String start2, String end2) {
            LocalTime startTime1 = LocalTime.parse(start1);
            LocalTime endTime1 = LocalTime.parse(end1).minusMinutes(1);
            LocalTime startTime2 = LocalTime.parse(start2);
            LocalTime endTime2 = LocalTime.parse(end2).minusMinutes(1);

            System.out.println("startTime1: " + startTime1);
            System.out.println("endTime1: " + endTime1);
            System.out.println("startTime2: " + startTime2);
            System.out.println("endTime2: " + endTime2);

            return !(endTime1.isBefore(startTime2) || startTime1.isAfter(endTime2));
        }
    }

    public static class DATE {
        public static boolean isOverlapping(String start1, String end1, String start2, String end2) {
            LocalDate startDate1 = LocalDate.parse(start1);
            LocalDate endDate1 = LocalDate.parse(end1);
            LocalDate startDate2 = LocalDate.parse(start2);
            LocalDate endDate2 = LocalDate.parse(end2);

            System.out.println("startDate1: " + startDate1);
            System.out.println("endDate1: " + endDate1);
            System.out.println("startDate2: " + startDate2);
            System.out.println("endDate2: " + endDate2);

            return !(endDate1.isBefore(startDate2) || startDate1.isAfter(endDate2));
        }
    }

    private static boolean containsUppercase(String str) {
        return str != null && str.matches(".*[A-Z].*");
    }

    private static boolean containsNumbers(String str) {
        return str != null && str.matches(".*\\d.*");
    }
}