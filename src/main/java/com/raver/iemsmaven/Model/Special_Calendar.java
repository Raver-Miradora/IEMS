/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Model;

import com.raver.iemsmaven.Utilities.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**
 *
 * @author User
 */
public class Special_Calendar {
    private int id;
    private String type;
    private String description;
    private String attachment;
    private Date startDate;
    private Date endDate;
    private String holidayType;
    
    private String scheduleType; // e.g., "Full Day", "AM Only", "PM Only"
    
    private int total;
    private String name;
    private int year;
    private int month;
    private int day;

public String getHolidayType() { return holidayType; }
    public void setHolidayType(String holidayType) { this.holidayType = holidayType; }

    public String getScheduleType() { return scheduleType; }
    public void setScheduleType(String scheduleType) { this.scheduleType = scheduleType; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAttachment() { return attachment; }
    public void setAttachment(String attachment) { this.attachment = attachment; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public static boolean holidayDateExists(Date date) {
        String sql = "SELECT COUNT(*) AS cnt FROM special_calendar WHERE start_date = ? AND status = 1";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, date);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public Special_Calendar(int id, String type, String description, Date startDate, Date endDate, int total) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.total = total;
    }
    
    // --- MODIFIED CONSTRUCTOR ---
    public Special_Calendar(int id, String type, String description, String attachment, Date startDate, Date endDate, String scheduleType, String holidayType) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.attachment = attachment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.scheduleType = scheduleType; 
        this.holidayType = holidayType;
    }
    
    // --- MODIFIED CONSTRUCTOR ---
    public Special_Calendar( String type, String description, String attachment, Date startDate, Date endDate, String scheduleType) {
        this.type = type;
        this.description = description;
        this.attachment = attachment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.scheduleType = scheduleType; // Set new field
    }
     public Special_Calendar(Date startDate,int total){
         this.startDate = startDate;
         this.total = total;
     }
     
     public Special_Calendar(String name, String type,int year, int month, int day){
        this.name = name;
        this.type = type;
        this.year = year;
        this.month = month;
        this.day = day;
     }

    public static ObservableList<Special_Calendar> getHolidayDtr(){
        ObservableList<Special_Calendar> dtrHolidayDocx = FXCollections.observableArrayList();
        String query = "SELECT name, type, month, day, YEAR(CURDATE()) AS year FROM user_calendar_schedule";

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                dtrHolidayDocx.add(new Special_Calendar(
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getInt("year"),
                        rs.getInt("month"),
                        rs.getInt("day")
                        
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dtrHolidayDocx;
    }

     
    public static ObservableList<Special_Calendar> getCalendarByUserId(int user_id) {
        ObservableList<Special_Calendar> calendar = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM `special_calendar_view` WHERE id = ?")) {

            statement.setInt(1, user_id); // Set the user_id in the PreparedStatement.

            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                calendar.add(new Special_Calendar(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getDate("startDate"),
                        rs.getDate("endDate"),
                        rs.getInt("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return calendar;
    }
    
    // --- MODIFIED METHOD ---
    public static ObservableList<Special_Calendar> getSpecialCalendar(){
        ObservableList<Special_Calendar> calendar = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){
            
           ResultSet rs = statement.executeQuery(
                "SELECT special_calendar_id as id, sc_type as type, sc_desc as description, " +
                "attachment, start_date as startDate, end_date as endDate, " +
                "schedule_type, holiday_type " + 
                "FROM special_calendar WHERE status = 1;"
            );
            
            while (rs.next()) {
                calendar.add(new Special_Calendar(
                    rs.getInt("id"),
                    rs.getString("type"),
                    rs.getString("description"),
                    rs.getString("attachment"),
                    rs.getDate("startDate"),
                    rs.getDate("endDate"),
                    rs.getString("schedule_type"),
                        rs.getString("holiday_type")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return calendar;
    }
    
    public static void updateSpecialCalendar(int id, String type, String description, String attachment, Date startDate, Date endDate, String scheduleType, String holidayType) throws SQLException {
        String updateQuery = "UPDATE special_calendar SET sc_type=?, sc_desc=?, attachment=?, start_date=?, end_date=?, schedule_type=?, holiday_type=? WHERE special_calendar_id=?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, type);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, attachment);
            preparedStatement.setDate(4, startDate);
            preparedStatement.setDate(5, endDate);
            preparedStatement.setString(6, scheduleType);
            preparedStatement.setString(7, holidayType); // <--- Update holiday type
            preparedStatement.setInt(8, id);

            preparedStatement.executeUpdate();
        }
    }
    
    public static void addSpecialCalendar(String type, String description, String attachment, Date startDate, Date endDate, String scheduleType, String holidayType) throws SQLException {
        String addQuery = "INSERT INTO special_calendar (sc_type, sc_desc, attachment, start_date, end_date, schedule_type, holiday_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(addQuery)) {

            preparedStatement.setString(1, type);
            preparedStatement.setString(2, description);
            preparedStatement.setString(3, attachment);
            preparedStatement.setDate(4, startDate);
            preparedStatement.setDate(5, endDate);
            preparedStatement.setString(6, scheduleType);
            preparedStatement.setString(7, holidayType); 
            preparedStatement.setInt(8, 1); 

            preparedStatement.executeUpdate();
        }
    }

    public static void deactivateSpecialCalendar(int special_calendar_id) {
        String query = "UPDATE special_calendar SET status = 2 WHERE special_calendar_id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, special_calendar_id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception or log it as needed
        }
    }

    //check if special calendar description already exists
    public static boolean specialCalendarDescriptionExists(String description){
        boolean exists = false;
        try (Connection connection = DatabaseUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM special_calendar WHERE sc_desc = ? AND status = 1")) {

            statement.setString(1, description);

            ResultSet rs = statement.executeQuery();

            if(rs.next()){
                exists = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
}