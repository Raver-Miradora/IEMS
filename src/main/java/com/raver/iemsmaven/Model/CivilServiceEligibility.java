package com.raver.iemsmaven.Model;

import java.time.LocalDate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CivilServiceEligibility {
    private String eligibilityName;
    private String rating;
    private LocalDate examDate; // Should be LocalDate
    private String examPlace;
    private String licenseNumber;
    private LocalDate validityDate; // Should be LocalDate

    // Constructor
    public CivilServiceEligibility(String eligibilityName, String rating, LocalDate examDate, String examPlace, String licenseNumber, LocalDate validityDate) {
        this.eligibilityName = eligibilityName;
        this.rating = rating;
        this.examDate = examDate;
        this.examPlace = examPlace;
        this.licenseNumber = licenseNumber;
        this.validityDate = validityDate;
    }
    public static void saveEligibilityList(Connection conn, int userId, List<CivilServiceEligibility> eligibilityList) throws SQLException {
        // 1. Delete all existing eligibility records for this user
        String deleteSql = "DELETE FROM civil_service_eligibility WHERE user_id = ?";
        try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
            psDelete.setInt(1, userId);
            psDelete.executeUpdate();
        }

        // 2. If the new list is null or empty, we're done.
        if (eligibilityList == null || eligibilityList.isEmpty()) {
            return;
        }

        // 3. Insert the new records
        // Assumes your table columns match the model properties
        String insertSql = "INSERT INTO civil_service_eligibility " +
                           "(user_id, eligibility_name, rating, exam_date, exam_place, license_number, validity_date) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (CivilServiceEligibility el : eligibilityList) {
                // Only save if the eligibility name is present
                if (el.getEligibilityName() != null && !el.getEligibilityName().isBlank()) {
                    ps.setInt(1, userId);
                    ps.setString(2, el.getEligibilityName());
                    ps.setString(3, el.getRating());
                    ps.setDate(4, (el.getExamDate() != null) ? Date.valueOf(el.getExamDate()) : null);
                    ps.setString(5, el.getExamPlace());
                    ps.setString(6, el.getLicenseNumber());
                    ps.setDate(7, (el.getValidityDate() != null) ? Date.valueOf(el.getValidityDate()) : null);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    // Getters - should return LocalDate
    public String getEligibilityName() { return eligibilityName; }
    public String getRating() { return rating; }
    public LocalDate getExamDate() { return examDate; } // Should return LocalDate
    public String getExamPlace() { return examPlace; }
    public String getLicenseNumber() { return licenseNumber; }
    public LocalDate getValidityDate() { return validityDate; } // Should return LocalDate
    public void setEligibilityName(String eligibilityName) { this.eligibilityName = eligibilityName; }
    public void setRating(String rating) { this.rating = rating; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }
    public void setExamPlace(String examPlace) { this.examPlace = examPlace; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setValidityDate(LocalDate validityDate) { this.validityDate = validityDate; }

    // Helper methods for display in tables
    public String getExamDateDisplay() {
        if (examDate != null) {
            return examDate.toString(); // Or format as needed
        }
        return "";
    }

    public String getValidityDateDisplay() {
        if (validityDate != null) {
            return validityDate.toString(); // Or format as needed
        }
        return "";
    }
}