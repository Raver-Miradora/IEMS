package com.raver.iemsmaven.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class EducationalBackground {
    private String level;
    private String schoolName;
    private String degreeCourse;
    private LocalDate startDate;
    private LocalDate endDate;
    private String unitsEarned;
    private String yearGraduated;
    private String honors;
    

    // Constructor
    public EducationalBackground(String level, String schoolName, String degreeCourse, LocalDate startDate, LocalDate endDate, String unitsEarned, String yearGraduated, String honors) {
        this.level = level;
        this.schoolName = schoolName;
        this.degreeCourse = degreeCourse;
        this.startDate = startDate;
        this.endDate = endDate;
        this.unitsEarned = unitsEarned;
        this.yearGraduated = yearGraduated;
        this.honors = honors;
    }
    public String getPeriod() {
        if (startDate != null && endDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            return startDate.format(formatter) + " to " + endDate.format(formatter);
        } else if (startDate != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            return startDate.format(formatter);
        }
        return "";
    }
    public static void saveEducationList(Connection conn, int userId, List<EducationalBackground> educationList) throws SQLException {
        // 1. Delete all existing education records for this user
        String deleteSql = "DELETE FROM educational_background WHERE user_id = ?";
        try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
            psDelete.setInt(1, userId);
            psDelete.executeUpdate();
        }

        // 2. If the new list is null or empty, we're done.
        if (educationList == null || educationList.isEmpty()) {
            return;
        }

        // 3. Insert the new records
        // Assumes your table columns match the model properties
        String insertSql = "INSERT INTO educational_background " +
                           "(user_id, edu_level, school_name, degree_course, start_date, end_date, units_earned, year_graduated, honors_received) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (EducationalBackground edu : educationList) {
                // Only save if the school name is present
                if (edu.getSchoolName() != null && !edu.getSchoolName().isBlank()) {
                    ps.setInt(1, userId);
                    ps.setString(2, edu.getLevel());
                    ps.setString(3, edu.getSchoolName());
                    ps.setString(4, edu.getDegreeCourse());
                    ps.setDate(5, (edu.getStartDate() != null) ? Date.valueOf(edu.getStartDate()) : null);
                    ps.setDate(6, (edu.getEndDate() != null) ? Date.valueOf(edu.getEndDate()) : null);
                    ps.setString(7, edu.getUnitsEarned());
                    ps.setString(8, edu.getYearGraduated());
                    ps.setString(9, edu.getHonors());
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }
    // Getters
    public String getLevel() { return level; }
    public String getSchoolName() { return schoolName; }
    public String getDegreeCourse() { return degreeCourse; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getUnitsEarned() { return unitsEarned; }
    public String getYearGraduated() { return yearGraduated; }
    public String getHonors() { return honors; }
    public void setLevel(String level) { this.level = level; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }
    public void setDegreeCourse(String degreeCourse) { this.degreeCourse = degreeCourse; }
    public void setUnitsEarned(String unitsEarned) { this.unitsEarned = unitsEarned; }
    public void setYearGraduated(String yearGraduated) { this.yearGraduated = yearGraduated; }
    public void setHonors(String honors) { this.honors = honors; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}