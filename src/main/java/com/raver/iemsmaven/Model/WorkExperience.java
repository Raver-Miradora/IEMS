package com.raver.iemsmaven.Model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
public class WorkExperience {
    private LocalDate startDate;
    private LocalDate endDate;
    private String positionTitle;
    private String company;
    private Double monthlySalary; // This should be Double, not String
    private String salaryGrade;
    private String appointmentStatus;
    private boolean isGovernmentService;
    private boolean governmentService;

    // Constructor
    public WorkExperience(LocalDate startDate, LocalDate endDate, String positionTitle, String company, Double monthlySalary, String salaryGrade, String appointmentStatus, boolean isGovernmentService) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.positionTitle = positionTitle;
        this.company = company;
        this.monthlySalary = monthlySalary; // Should be Double
        this.salaryGrade = salaryGrade;
        this.appointmentStatus = appointmentStatus;
        this.isGovernmentService = isGovernmentService;
    }
    
    public static void saveWorkExperienceList(Connection conn, int userId, List<WorkExperience> workExperienceList) throws SQLException {
        // 1. Delete all existing work experience records for this user
        String deleteSql = "DELETE FROM work_experience WHERE user_id = ?";
        try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
            psDelete.setInt(1, userId);
            psDelete.executeUpdate();
        }

        // 2. If the new list is null or empty, we're done.
        if (workExperienceList == null || workExperienceList.isEmpty()) {
            return;
        }

        // 3. Insert the new records
        // Assumes your table columns match the model properties
        String insertSql = "INSERT INTO work_experience " +
                           "(user_id, start_date, end_date, position_title, company, monthly_salary, " +
                           "salary_grade, appointment_status, is_government_service) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
            for (WorkExperience work : workExperienceList) {
                // Only save if position or company is present
                if ((work.getPositionTitle() != null && !work.getPositionTitle().isBlank()) ||
                    (work.getCompany() != null && !work.getCompany().isBlank())) {

                    ps.setInt(1, userId);
                    ps.setDate(2, (work.getStartDate() != null) ? Date.valueOf(work.getStartDate()) : null);
                    ps.setDate(3, (work.getEndDate() != null) ? Date.valueOf(work.getEndDate()) : null);
                    ps.setString(4, work.getPositionTitle());
                    ps.setString(5, work.getCompany());
                    
                    if (work.getMonthlySalary() != null) {
                        ps.setDouble(6, work.getMonthlySalary());
                    } else {
                        ps.setNull(6, java.sql.Types.DOUBLE);
                    }
                    
                    ps.setString(7, work.getSalaryGrade());
                    ps.setString(8, work.getAppointmentStatus());
                    ps.setBoolean(9, work.isGovernmentService());
                    
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }
    // Getters - make sure monthlySalary returns Double
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getPositionTitle() { return positionTitle; }
    public String getCompany() { return company; }
    public Double getMonthlySalary() { return monthlySalary; } // This should return Double
    public String getSalaryGrade() { return salaryGrade; }
    public String getAppointmentStatus() { return appointmentStatus; }
    public boolean isGovernmentService() { return isGovernmentService; }
    public void setMonthlySalary(Double monthlySalary) { this.monthlySalary = monthlySalary; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }
    public void setCompany(String company) { this.company = company; }
    public void setSalaryGrade(String salaryGrade) { this.salaryGrade = salaryGrade; }
    public void setAppointmentStatus(String appointmentStatus) { this.appointmentStatus = appointmentStatus; }
    public void setGovernmentService(boolean governmentService) {
        this.isGovernmentService = governmentService; 
    }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    // Helper methods for display in tables
    public String getPeriod() {
        if (startDate == null && endDate == null) return "";
        if (startDate == null) return "To: " + endDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        if (endDate == null) return "From: " + startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        return startDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " to " + 
               endDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
    

    public String getGovernmentService() {
        return isGovernmentService ? "Yes" : "No";
    }

    public String getMonthlySalaryDisplay() {
        if (monthlySalary != null) {
            return String.format("₱%,.2f", monthlySalary);
        }
        return "";
    }
}