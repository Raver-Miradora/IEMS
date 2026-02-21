package com.raver.iemsmaven.Model;


import com.raver.iemsmaven.Utilities.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MonthlyAttendanceSummary {
    private String name;
    private String position;
    private String department;
    private int employeeId;
    private Map<Integer, String> dailyStatus; // day -> status
    private Map<Integer, Boolean> overtimeDays; // day -> has overtime
    
    public MonthlyAttendanceSummary(String name, String position, String department) {
        this.name = name;
        this.position = position;
        this.department = department;
        this.dailyStatus = new HashMap<>();
        this.overtimeDays = new HashMap<>();
        this.employeeId = generateEmployeeId(); // You'll need to implement this
    }
    
    // Constructor with employee ID
    public MonthlyAttendanceSummary(String name, String position, String department, int employeeId) {
        this.name = name;
        this.position = position;
        this.department = department;
        this.employeeId = employeeId;
        this.dailyStatus = new HashMap<>();
        this.overtimeDays = new HashMap<>();
    }
    
    private int generateEmployeeId() {
        // Implement logic to get employee ID from name
        // This is a placeholder - you'll need to implement based on your data
        return Math.abs(name.hashCode());
    }
    
    public void setDailyStatus(int day, String status) {
        dailyStatus.put(day, status);
    }
    
    public void setOvertime(int day, boolean hasOvertime) {
        overtimeDays.put(day, hasOvertime);
    }
    
    public String getStatusForDay(int day) {
        return dailyStatus.getOrDefault(day, "A"); // Default to "A" for absent
    }
    
    public int getPresentDays() {
        return (int) dailyStatus.values().stream()
                .filter(status -> status.startsWith("P"))
                .count();
    }
    
    public int getLateDays() {
        return (int) dailyStatus.values().stream()
                .filter(status -> "L".equals(status))
                .count();
    }
    
    public int getAbsentDays() {
        return (int) dailyStatus.values().stream()
                .filter(status -> "A".equals(status))
                .count();
    }
    
    public int getOvertimeDays() {
        return (int) overtimeDays.values().stream()
                .filter(hasOvertime -> hasOvertime)
                .count();
    }
    
    // Getters
    public String getName() { return name; }
    public String getPosition() { return position; }
    public String getDepartment() { return department; }
    public int getEmployeeId() { return employeeId; }
}