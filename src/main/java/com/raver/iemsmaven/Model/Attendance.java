/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Model;
import com.raver.iemsmaven.Model.AttendanceAction;
import com.raver.iemsmaven.Utilities.DatabaseUtil;
import com.raver.iemsmaven.Utilities.Config;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author User
 */
public class Attendance {
    private int id;
    private String agencyEmployeeNo;
    private String dtrDate;
    private int day;
    private Date date;
    private String attendance_status;
    private String timeIn;
    private String timeOut;
    private String timeInAm,timeOutAm;
    private String timeInPm,timeOutPm;
    private String name;
    private String notationAM;
    private String notationPM;
    private String deptName;
    private String notation;
    private String position;
    public static String defaultValue = "All";
    private int totalEmployees;
    private double percentageLoggedIn;
    private double percentageNotLoggedIn;
    private String time;
    private String type;
    private String overtimeIn;
    private String overtimeOut;
    private String overtimeNotation;
    private int lateMinutes;
    private int undertimeMinutes;
    private static final LocalTime LUNCH_START = LocalTime.of(12, 0);
    private static final LocalTime PM_START_CUTOFF = LocalTime.of(13, 0); // 1:00 PM
    private static final LocalTime DISMISSAL_TIME = LocalTime.of(17, 0);  // 5:00 PM
    
    public int getDay() {
        return day;
    }
    public String getAgencyEmployeeNo() {
        return agencyEmployeeNo;
    }

    public void setAgencyEmployeeNo(String agencyEmployeeNo) {
        this.agencyEmployeeNo = agencyEmployeeNo;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDtrDate() {
        return dtrDate;
    }

    public void setDtrDate(String dtrDate) {
        this.dtrDate = dtrDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Attendance(String name,  String time, String type, Date date, String position) {
        this.date = date;
        this.name = name;
        this.time = time;
        this.type = type;
        this.position = position;
    }

    public Attendance(int totalEmployees, double percentageLoggedIn, double percentageNotLoggedIn){
        this.totalEmployees = totalEmployees;
        this.percentageLoggedIn=percentageLoggedIn;
        this.percentageNotLoggedIn=percentageNotLoggedIn;
    }

    public static int getTotalEmployees() {
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT COUNT(*) as total FROM user WHERE user_status = 1";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
    public int getLateMinutes() {
        return lateMinutes;
    }

    public void setLateMinutes(int lateMinutes) {
        this.lateMinutes = lateMinutes;
    }

    public int getUndertimeMinutes() {
        return undertimeMinutes;
    }

    public void setUndertimeMinutes(int undertimeMinutes) {
        this.undertimeMinutes = undertimeMinutes;
    }
    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public double getPercentageLoggedIn() {
        return percentageLoggedIn;
    }

    public double getPercentageNotLoggedIn() {
        return percentageNotLoggedIn;
    }

    public void setPercentageLoggedIn(double percentageLoggedIn) {
        this.percentageLoggedIn = percentageLoggedIn;
    }

    public void setPercentageNotLoggedIn(double percentageNotLoggedIn) {
        this.percentageNotLoggedIn = percentageNotLoggedIn;
    }
    
    public Attendance() {
        
    }

    public Attendance(String dtrDate, int day, int id, String name, String timeInAm, String timeOutAm, String timeInPm, String timeOutPm) {
        this.dtrDate = dtrDate;
        this.day = day;
        this.id=id;
        this.name = name;
        this.timeInAm = timeInAm;
        this.timeOutAm = timeOutAm;
        this.timeInPm = timeInPm;
        this.timeOutPm = timeOutPm;
    }
    public Attendance(int id, Date date, String name, String timeIn, String notation) {
        this.id=id;
        this.date = date;
        this.timeIn = timeIn;
        this.name = name;
        this.notation = notation;
    }
    
    public Attendance(String name){
        this.name = name;
        this.timeInAm = "";
        this.timeOutAm = "";
        this.timeInPm = "";
        this.timeOutPm = "";
        this.overtimeIn = "";
        this.overtimeOut = "";
    }
    public Attendance(int id, String name){
        this.id = id;
        this.name = name;
    }
    
    public Attendance(Date date){
        this.date=date;
    }
     public Attendance (String name, Date date, String timeInAm, String timeOutAm, String timeInPm, String timeOutPm, String attendance_status){
        this.name = name;
        this.date = date;
        this.timeInAm = timeInAm;
        this.timeOutAm = timeOutAm;
        this.timeInPm = timeInPm;
        this.timeOutPm = timeOutPm;
        this.attendance_status = attendance_status;
    }

    public void setNotationAM(String notationAM) {
        this.notationAM = notationAM;
    }

    public void setNotationPM(String notationPM) {
        this.notationPM = notationPM;
    }

    public String getNotationAM() {
        return notationAM;
    }

    public String getNotationPM() {
        return notationPM;
    }
    public Attendance (String name, Date date, String timeIn, String timeOut, String notation, String attendance_status){
        this.name = name;
        this.date = date;
        this.attendance_status = attendance_status;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.notation = notation;
    }
    public int getId() {
        return id;
      }
      public String getName() {
        return name;
      }
      public String getNotation() {
        return notation;
    }
       public String getDeptName() {
        return deptName;
    }
     public Date getDate() {
        return date;
    }
     public String getAttendance_status() {
        return attendance_status;
    }
     public String getTimeIn() {
        return timeIn;
    }
     public String getTimeOut() {
        return timeOut;
    }
     public String getTimeInAm() {
        return timeInAm;
    }
     public String getTimeOutAm() {
        return timeOutAm;
    }
     public String getTimeInPm() {
        return timeInPm;
    }
     public String getTimeOutPm() {
        return timeOutPm;
    }
    
     
public void setId(int id) {
    this.id = id;
} 
public void setName(String name) {
    this.name = name;
}

public void setNotation(String notation) {
    this.notation = notation;
}

public void setDeptName(String deptName) {
    this.deptName = deptName;
}

public void setDate(Date date) {
    this.date = date;
}

public void setAttendance_status(String attendance_status) {
    this.attendance_status = attendance_status;
}

public void setTimeIn(String timeIn) {
    this.timeIn = timeIn;
}

public void setTimeOut(String timeOut) {
    this.timeOut = timeOut;
}   
public void setTimeInAm(String timeInAm) {
    this.timeInAm = timeInAm;
}

public void setTimeOutAm(String timeOutAm) {
    this.timeOutAm = timeOutAm;
}   
public void setTimeInPm(String timeInPm) {
    this.timeInPm = timeInPm;
}

public void setTimeOutPm(String timeOutPm) {
    this.timeOutPm = timeOutPm;
}
public String getPosition() {
    return position;
}
public void setPosition(String position) {
    this.position = position;
}
public String getOvertimeIn() {
    return overtimeIn;
}

public void setOvertimeIn(String overtimeIn) {
    this.overtimeIn = overtimeIn;
}

public String getOvertimeOut() {
    return overtimeOut;
}

public void setOvertimeOut(String overtimeOut) {
    this.overtimeOut = overtimeOut;
}

public String getOvertimeNotation() {
    return overtimeNotation;
}

public void setOvertimeNotation(String overtimeNotation) {
    this.overtimeNotation = overtimeNotation;
}


    public Attendance (String dtrDate, int day, String name,  String timeIn, String timeOut){
        this.dtrDate = dtrDate;
        this.day = day;
        this.name = name;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }
    private static String to12Hour(String time24) {
        if (time24 == null || time24.trim().isEmpty() || time24.equals("00:00:00")) {
            return " ";
        }
        try {
            // parse the 24-hour time and format to 12-hour with AM/PM
            LocalTime lt = LocalTime.parse(time24);
            return lt.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        } catch (Exception e) {
            // fallback: return original string if parse fails
            return time24;
        }
    }

    public static ObservableList<Attendance> getOLAMForDocx(){
        ObservableList<Attendance>dtrOLDocx = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){
            
            ResultSet rs = statement.executeQuery("SELECT * FROM `overload_view_am`");
            
            while (rs.next()) {
                dtrOLDocx.add(new Attendance(
                        rs.getString("dtrDate"),
                        rs.getInt("day"),
                        rs.getString("name"),
                        rs.getString("timeIn"),
                        rs.getString("timeOut")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dtrOLDocx;
    }

    public static ObservableList<Attendance> getOLPMForDocx(){
        ObservableList<Attendance>dtrOLDocx = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){
            
            ResultSet rs = statement.executeQuery("SELECT * FROM `overload_view_pm`");
            
            while (rs.next()) {
                dtrOLDocx.add(new Attendance(
                        rs.getString("dtrDate"),
                        rs.getInt("day"),
                        rs.getString("name"),
                        rs.getString("timeIn"),
                        rs.getString("timeOut")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dtrOLDocx;
    }
    
    public static ObservableList<Attendance> getDtrForDocx(){
        ObservableList<Attendance>dtrDocx = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){
            
            ResultSet rs = statement.executeQuery("SELECT * FROM `attendance_summary_view`");
            
            while (rs.next()) {
                dtrDocx.add(new Attendance(
                        rs.getString("dtrDate"),
                        rs.getInt("day"),
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("timeInAm"),
                        rs.getString("timeOutAm"),
                        rs.getString("timeInPm"),
                        rs.getString("timeOutPm")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dtrDocx;
    }

    public static ObservableList<Attendance> getYearforLabel(){
        ObservableList<Attendance>empName = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){
            
            ResultSet rs = statement.executeQuery("SELECT date FROM attendance "+
            "GROUP BY YEAR(date);");
            
            while (rs.next()) {
                empName.add(new Attendance(
                        rs.getDate("date")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empName;
    }
     public static ObservableList<Attendance> getEmpName(){
        ObservableList<Attendance>empName = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){
            
            ResultSet rs = statement.executeQuery("SELECT user_id as id, CONCAT(user_fname, ' ', user_lname) AS name FROM user "+
            "WHERE user_status = 1 GROUP BY user_id ORDER BY id;");
            while (rs.next()) {
               
                empName.add(new Attendance(
                        rs.getInt("id"),
                    rs.getString("name")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empName;
    }
     public static Map<String, Integer> getLatesByDayOfWeek() {
        Map<String, Integer> daysData = new LinkedHashMap<>();

        // Initialize with 0 to ensure all days appear in the chart even if count is 0
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            daysData.put(day, 0);
        }
        String query = "SELECT DAYNAME(date) as day_name, COUNT(*) as count " +
                       "FROM attendance " +
                       "WHERE attendance_status = 2 " + // 2 = Late
                       "GROUP BY day_name " +
                       "ORDER BY FIELD(day_name, 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')";

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            while (rs.next()) {
                String day = rs.getString("day_name");
                int count = rs.getInt("count");
                if (daysData.containsKey(day)) {
                    daysData.put(day, count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return daysData;
    }

    public static ObservableList<Attendance> getAttendance(){
        ObservableList<Attendance> attendance = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){

            ResultSet rs = statement.executeQuery(
                "SELECT CONCAT(u.user_fname, ' ', u.user_lname) AS name, c.date, " +
                "c.time_in as timeIn, c.time_out as timeOut, c.time_notation as notation, " +
                "c.attendance_status, p.position_name AS position, d.department_name AS department " + 
                "FROM attendance c " +
                "JOIN user u ON c.user_id = u.user_id " +
                "JOIN assignment a ON u.user_id = a.user_id " +
                "JOIN position p ON a.position_id = p.position_id " +
                "JOIN department d ON p.department_id = d.department_id " +
                "WHERE u.user_status = 1 GROUP BY c.attendance_id ORDER BY c.attendance_id;"
            );

            while (rs.next()) {
                int statusInt = rs.getInt("attendance_status");
                String inRaw = rs.getString("timeIn");
                String outRaw = rs.getString("timeOut");

                String in = to12Hour(inRaw);
                String out = to12Hour(outRaw);

                String statusString;
                if (statusInt == 1) {
                    statusString = "   ✓";
                } else if (statusInt == 2) {
                    statusString = "Late";
                } else if (statusInt == 3) {
                    statusString = "No Out";
                } else {
                    statusString = "Present";
                }

                Attendance a = new Attendance(
                   rs.getString("name"),
                   rs.getDate("date"),
                   in,
                   out,
                   rs.getString("notation"),
                   statusString
                );
                a.setPosition(rs.getString("position"));
                a.setDeptName(rs.getString("department")); 
                attendance.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendance;
    }
    
    public static Attendance getTodayRecord(int userId) {
    String key = Config.getSecretKey();
    Attendance record = null;
    
    // We fetch raw encrypted/decrypted times to check which fields are filled
    String query = "SELECT attendance_id, " +
            "CAST(AES_DECRYPT(time_in, ?) AS CHAR) as t_in, " +
            "CAST(AES_DECRYPT(time_out, ?) AS CHAR) as t_out, " +
            "time_notation, " +
            "overtime_in, overtime_out " + // Assuming OT isn't encrypted based on your provided schema usage
            "FROM attendance WHERE user_id = ? AND date = CURDATE()";

    try (Connection connection = DatabaseUtil.getConnection();
         PreparedStatement ps = connection.prepareStatement(query)) {
        
        ps.setString(1, key);
        ps.setString(2, key);
        ps.setInt(3, userId);

        try (ResultSet rs = ps.executeQuery()) {
            // We iterate to find specific AM and PM records if multiple exist
            // (Though typically there is 1 row for AM and 1 for PM, or 1 consolidated row depending on schema)
            // Based on your code, it seems you allow multiple rows (one for AM, one for PM)
            
            record = new Attendance(userId, ""); // Empty container
            
            while (rs.next()) {
                String notation = rs.getString("time_notation");
                String tIn = rs.getString("t_in");
                String tOut = rs.getString("t_out");
                String oIn = rs.getString("overtime_in");
                String oOut = rs.getString("overtime_out");

                if ("AM".equalsIgnoreCase(notation)) {
                    record.setTimeInAm(tIn);
                    record.setTimeOutAm(tOut);
                } else if ("PM".equalsIgnoreCase(notation)) {
                    record.setTimeInPm(tIn);
                    record.setTimeOutPm(tOut);
                } else if ("OT".equalsIgnoreCase(notation)) {
                    // If OT is stored as a separate row
                     record.setOvertimeIn(oIn);
                     record.setOvertimeOut(oOut);
                }
                
                // If OT is stored in columns on the same row (hybrid schema), check here too
                if (oIn != null) record.setOvertimeIn(oIn);
                if (oOut != null) record.setOvertimeOut(oOut);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return record;
    }
    
    public static AttendanceAction determineAutoAction(int userId) {
        Attendance record = getTodayRecord(userId);
        LocalTime now = LocalTime.now();

        LocalTime shiftStart = LocalTime.of(8, 0);
        LocalTime shiftEnd = LocalTime.of(17, 0);
        LocalTime lunchStart = LocalTime.of(12, 0);

        ObservableList<Assignment> assignments = Assignment.getActiveAssignmentsByUserId(userId);
        if (assignments != null && !assignments.isEmpty()) {
            Assignment asm = assignments.get(0);
            if (asm.getStartTime() != null) shiftStart = asm.getStartTime();
            if (asm.getEndTime() != null) shiftEnd = asm.getEndTime();
            lunchStart = shiftStart.plusHours(4);
        }

        long minutesBetween = java.time.temporal.ChronoUnit.MINUTES.between(lunchStart, shiftEnd);
        LocalTime pmCutoff = lunchStart.plusMinutes(minutesBetween / 2);

        boolean hasAmIn = record != null && isValidTime(record.getTimeInAm());
        boolean hasAmOut = record != null && isValidTime(record.getTimeOutAm());
        boolean hasPmIn = record != null && isValidTime(record.getTimeInPm());
        boolean hasPmOut = record != null && isValidTime(record.getTimeOutPm());
        boolean hasOtIn = record != null && isValidTime(record.getOvertimeIn());
        boolean hasOtOut = record != null && isValidTime(record.getOvertimeOut());

        if (isScanningTooFast(record, now)) {
            return AttendanceAction.DENIED_TOO_EARLY;
        }

        if (!hasAmIn && !hasPmIn) {
            if (now.isAfter(pmCutoff)) {
                return AttendanceAction.TIME_IN_PM;
            }
            if (now.isBefore(shiftStart.minusHours(2))) {
                return AttendanceAction.DENIED_TOO_EARLY;
            }
            return AttendanceAction.TIME_IN_AM;
        }

        if (hasAmIn && !hasAmOut) {
            if (now.isAfter(pmCutoff)) {
                return AttendanceAction.TIME_OUT_PM;
            }
            return AttendanceAction.TIME_OUT_AM;
        }

        if (hasAmIn && hasAmOut && !hasPmIn) {
            String amOutStr = record.getTimeOutAm();
            if (amOutStr != null && !amOutStr.isBlank()) {
                try {
                    LocalTime amOutTime = LocalTime.parse(amOutStr);
                    if (amOutTime.isAfter(pmCutoff)) {
                        return AttendanceAction.DENIED_ALREADY_COMPLETED;
                    }
                } catch (Exception e) { }
            }

            if (now.isBefore(shiftEnd)) {
                return AttendanceAction.TIME_IN_PM;
            }

            return AttendanceAction.DENIED_ALREADY_COMPLETED;
        }

        if (hasPmIn && !hasPmOut) {
            return AttendanceAction.TIME_OUT_PM;
        }

        if (hasPmOut) {
            if (isOtAuthorized(userId, LocalDate.now())) { 
                if (!hasOtIn) return AttendanceAction.OVERTIME_IN;
                if (!hasOtOut) return AttendanceAction.OVERTIME_OUT;
                return AttendanceAction.DENIED_ALREADY_COMPLETED;
            } else {
                // If they are not in the 'overtime_authorization' table, block them.
                return AttendanceAction.DENIED_OUTSIDE_SHIFT; 
            }
        }
        return AttendanceAction.TIME_IN_AM;
    }



    private static boolean isScanningTooFast(Attendance record, LocalTime now) {
        if (record == null) return false;
        try {
            String lastTimeStr = null;
            // Check fields in reverse order of typical day
            if (isValidTime(record.getOvertimeIn()) && !isValidTime(record.getOvertimeOut())) lastTimeStr = record.getOvertimeIn();
            else if (isValidTime(record.getTimeInPm()) && !isValidTime(record.getTimeOutPm())) lastTimeStr = record.getTimeInPm();
            else if (isValidTime(record.getTimeOutAm()) && !isValidTime(record.getTimeInPm())) lastTimeStr = record.getTimeOutAm(); // Check OUT times too
            else if (isValidTime(record.getTimeInAm()) && !isValidTime(record.getTimeOutAm())) lastTimeStr = record.getTimeInAm();

            if (lastTimeStr != null) {
                // If your DB saves as HH:mm:ss, parse it
                LocalTime lastTime = LocalTime.parse(lastTimeStr);
                long secondsDiff = Math.abs(java.time.temporal.ChronoUnit.SECONDS.between(lastTime, now));
                return secondsDiff < 60; // 60 Seconds Delay
            }
        } catch (Exception e) { return false; }
        return false;
    }

    private static boolean isValidTime(String t) {
        return t != null && !t.equals("00:00:00") && !t.trim().isEmpty() && !t.contains("--");
    }
        
        public static ObservableList<Attendance> getAdministrative(){
            ObservableList<Attendance> attendance = FXCollections.observableArrayList();
            try (Connection connection = DatabaseUtil.getConnection();
                Statement statement = connection.createStatement()){

                ResultSet rs = statement.executeQuery(
                    "SELECT CONCAT(u.user_fname, ' ', u.user_lname) AS name, c.date, " +
                    "c.time_in as timeIn, c.time_out as timeOut, c.time_notation as notation, c.attendance_status, p.position_name AS position " +
                    "FROM attendance c " +
                    "JOIN user u ON c.user_id = u.user_id " +
                    "JOIN assignment a ON u.user_id = a.user_id " +
                    "JOIN position p ON a.position_id = p.position_id " +
                    "JOIN department d ON p.department_id = d.department_id " +
                    "WHERE d.department_id = 1 AND u.user_status = 1 GROUP BY c.attendance_id ORDER BY c.attendance_id;"
                );

                while (rs.next()) {
                    int statusInt = rs.getInt("attendance_status");
                    String inRaw = rs.getString("timeIn");
                    String outRaw = rs.getString("timeOut");

                    String in = to12Hour(inRaw);
                    String out = to12Hour(outRaw);

                    String statusString;
                    if (statusInt == 1) {
                        statusString = "   ✓";
                    } else if (statusInt == 2) {
                        statusString = "Late";
                    } else if (statusInt == 3) {
                        statusString = "No Out";
                    } else {
                        statusString = "Present";
                    }

                    Attendance a = new Attendance(
                        rs.getString("name"),
                        rs.getDate("date"),
                        in,
                        out,
                        rs.getString("notation"),
                        statusString
                    );
                    a.setPosition(rs.getString("position"));
                    attendance.add(a);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return attendance;
        }

         
         public static ObservableList<Attendance> getEmpToPieChart(){
            ObservableList<Attendance> attendance = FXCollections.observableArrayList();
            try (Connection connection = DatabaseUtil.getConnection();
                Statement statement = connection.createStatement()){
                String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                ResultSet rs = statement.executeQuery("SELECT total_employees as count, percentage_logged_in as logged, percentage_not_logged_in as notLogged  FROM employee_status_view;");
                
                while (rs.next()){
                   
                            attendance.add(new Attendance(
                        rs.getInt("count"),
                        rs.getDouble("logged"),
                        rs.getDouble("notLogged")
                         ));
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
             
             return attendance;
         }

        public static ObservableList<Attendance> getRecentAttendance() {
            ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();
            String key = Config.getSecretKey();

            // We decrypt fields inside the query so MAX() works on the Time String, not the Blob
            String query = 
                "SELECT u.user_id, CONCAT(u.user_lname, ', ', u.user_fname) AS name, " +
                "MAX(CASE WHEN a.time_notation = 'AM' AND CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) END) AS am_in, " +
                "MAX(CASE WHEN a.time_notation = 'AM' AND CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) END) AS am_out, " +
                "MAX(CASE WHEN a.time_notation = 'PM' AND CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) END) AS pm_in, " +
                "MAX(CASE WHEN a.time_notation = 'PM' AND CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) END) AS pm_out, " +
                "MAX(CASE WHEN a.overtime_notation = 'OT' AND CAST(AES_DECRYPT(a.overtime_in, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.overtime_in, ?) AS CHAR) END) AS ot_in, " +
                "MAX(CASE WHEN a.overtime_notation = 'OT' AND CAST(AES_DECRYPT(a.overtime_out, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.overtime_out, ?) AS CHAR) END) AS ot_out, " +

                "GREATEST(" +
                "  COALESCE(MAX(CASE WHEN a.time_notation = 'AM' THEN CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) END), '00:00:00'), " +
                "  COALESCE(MAX(CASE WHEN a.time_notation = 'AM' THEN CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) END), '00:00:00'), " +
                "  COALESCE(MAX(CASE WHEN a.time_notation = 'PM' THEN CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) END), '00:00:00'), " +
                "  COALESCE(MAX(CASE WHEN a.time_notation = 'PM' THEN CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) END), '00:00:00'), " +
                "  COALESCE(MAX(CASE WHEN a.overtime_notation = 'OT' THEN CAST(AES_DECRYPT(a.overtime_in, ?) AS CHAR) END), '00:00:00'), " +
                "  COALESCE(MAX(CASE WHEN a.overtime_notation = 'OT' THEN CAST(AES_DECRYPT(a.overtime_out, ?) AS CHAR) END), '00:00:00')" +
                ") AS latest_activity " +

                "FROM user u " +
                "LEFT JOIN attendance a ON u.user_id = a.user_id AND a.date = CURDATE() " +
                "WHERE u.user_status = 1 " +
                "GROUP BY u.user_id, name " +
                "HAVING am_in IS NOT NULL OR am_out IS NOT NULL OR pm_in IS NOT NULL OR pm_out IS NOT NULL OR ot_in IS NOT NULL OR ot_out IS NOT NULL " +
                "ORDER BY latest_activity DESC, name ASC";

            try (Connection connection = DatabaseUtil.getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {

                // We have to set the key for EVERY ? placeholder in the query above
                int paramIndex = 1;
                // AM IN (2 times)
                ps.setString(paramIndex++, key); ps.setString(paramIndex++, key);
                // AM OUT (2 times)
                ps.setString(paramIndex++, key); ps.setString(paramIndex++, key);
                // PM IN (2 times)
                ps.setString(paramIndex++, key); ps.setString(paramIndex++, key);
                // PM OUT (2 times)
                ps.setString(paramIndex++, key); ps.setString(paramIndex++, key);
                // OT IN (2 times)
                ps.setString(paramIndex++, key); ps.setString(paramIndex++, key);
                // OT OUT (2 times)
                ps.setString(paramIndex++, key); ps.setString(paramIndex++, key);

                // GREATEST Clause keys
                ps.setString(paramIndex++, key); // am in
                ps.setString(paramIndex++, key); // am out
                ps.setString(paramIndex++, key); // pm in
                ps.setString(paramIndex++, key); // pm out
                ps.setString(paramIndex++, key); // ot in
                ps.setString(paramIndex++, key); // ot out

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Attendance attendance = new Attendance(rs.getString("name"));
                        // Since we CAST as CHAR in SQL, rs.getString works normally here
                        attendance.setTimeInAm(rs.getString("am_in"));
                        attendance.setTimeOutAm(rs.getString("am_out"));
                        attendance.setTimeInPm(rs.getString("pm_in"));
                        attendance.setTimeOutPm(rs.getString("pm_out"));
                        attendance.setOvertimeIn(rs.getString("ot_in"));
                        attendance.setOvertimeOut(rs.getString("ot_out"));
                        attendanceList.add(attendance);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return attendanceList;
        }

       public static ObservableList<Attendance> getRecentAttendanceWithPosition() {
        ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();
        String key = Config.getSecretKey();

        // We use a CTE (Common Table Expression) to decrypt the data first
        // CAST(... AS CHAR) is crucial so MySQL treats the decrypted result as a Time String, not a binary blob
        String query = 
            "WITH LatestActivity AS (" +
            "  SELECT u.user_id, " +
            "         CONCAT(u.user_lname, ', ', u.user_fname) AS name, " + 
            "         p.position_name AS position, " +
            "         AES_DECRYPT(u.agency_employee_no, ?) AS agency_employee_no, " + // Key #1

            // Decrypt AM IN
            "         MAX(CASE WHEN a.time_notation = 'AM' AND CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) END) AS am_in, " + // Key #2, #3
            // Decrypt AM OUT
            "         MAX(CASE WHEN a.time_notation = 'AM' AND CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) END) AS am_out, " + // Key #4, #5
            // Decrypt PM IN
            "         MAX(CASE WHEN a.time_notation = 'PM' AND CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) END) AS pm_in, " + // Key #6, #7
            // Decrypt PM OUT
            "         MAX(CASE WHEN a.time_notation = 'PM' AND CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) END) AS pm_out, " + // Key #8, #9
            // Decrypt OT IN
            "         MAX(CASE WHEN a.overtime_notation = 'OT' AND CAST(AES_DECRYPT(a.overtime_in, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.overtime_in, ?) AS CHAR) END) AS ot_in, " + // Key #10, #11
            // Decrypt OT OUT
            "         MAX(CASE WHEN a.overtime_notation = 'OT' AND CAST(AES_DECRYPT(a.overtime_out, ?) AS CHAR) != '00:00:00' THEN CAST(AES_DECRYPT(a.overtime_out, ?) AS CHAR) END) AS ot_out, " + // Key #12, #13

            // Calculate Latest Time for Sorting (Must decrypt here too)
            "         GREATEST(" +
            "           COALESCE(MAX(CAST(AES_DECRYPT(a.time_in, ?) AS CHAR)), '00:00:00'), " + // Key #14
            "           COALESCE(MAX(CAST(AES_DECRYPT(a.time_out, ?) AS CHAR)), '00:00:00'), " + // Key #15
            "           COALESCE(MAX(CAST(AES_DECRYPT(a.overtime_in, ?) AS CHAR)), '00:00:00'), " + // Key #16
            "           COALESCE(MAX(CAST(AES_DECRYPT(a.overtime_out, ?) AS CHAR)), '00:00:00')" + // Key #17
            "         ) AS latest_time " +
            
            "  FROM user u " +
            "  LEFT JOIN attendance a ON u.user_id = a.user_id AND a.date = CURDATE() " +
            "  LEFT JOIN assignment ass ON u.user_id = ass.user_id AND ass.status = 1 " +
            "  LEFT JOIN position p ON ass.position_id = p.position_id " +
            "  WHERE u.user_status = 1 " +
            "  GROUP BY u.user_id, name, position, u.agency_employee_no" + 
            ") " +
            "SELECT * FROM LatestActivity " +
            "WHERE am_in IS NOT NULL OR am_out IS NOT NULL OR pm_in IS NOT NULL OR pm_out IS NOT NULL OR ot_in IS NOT NULL OR ot_out IS NOT NULL " +
            "ORDER BY latest_time DESC, name ASC";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            
            // We must inject the key for EVERY ? placeholder in the exact order they appear above
            int i = 1;
            statement.setString(i++, key); // 1. Agency No
            
            statement.setString(i++, key); // 2. AM IN Check
            statement.setString(i++, key); // 3. AM IN Select
            
            statement.setString(i++, key); // 4. AM OUT Check
            statement.setString(i++, key); // 5. AM OUT Select
            
            statement.setString(i++, key); // 6. PM IN Check
            statement.setString(i++, key); // 7. PM IN Select
            
            statement.setString(i++, key); // 8. PM OUT Check
            statement.setString(i++, key); // 9. PM OUT Select
            
            statement.setString(i++, key); // 10. OT IN Check
            statement.setString(i++, key); // 11. OT IN Select
            
            statement.setString(i++, key); // 12. OT OUT Check
            statement.setString(i++, key); // 13. OT OUT Select
            
            statement.setString(i++, key); // 14. Greatest Time In
            statement.setString(i++, key); // 15. Greatest Time Out
            statement.setString(i++, key); // 16. Greatest OT In
            statement.setString(i++, key); // 17. Greatest OT Out

            ResultSet rs = statement.executeQuery(); 

            while (rs.next()) {
                Attendance attendance = new Attendance(rs.getString("name"));
                attendance.setId(rs.getInt("user_id"));
 
                // Decrypt Agency No
                attendance.setAgencyEmployeeNo(bytesToString(rs.getBytes("agency_employee_no")));
                
                // These columns are now simple Strings because of CAST(... AS CHAR) in SQL
                attendance.setTimeInAm(rs.getString("am_in"));
                attendance.setTimeOutAm(rs.getString("am_out"));
                attendance.setTimeInPm(rs.getString("pm_in"));
                attendance.setTimeOutPm(rs.getString("pm_out"));
                attendance.setOvertimeIn(rs.getString("ot_in"));
                attendance.setOvertimeOut(rs.getString("ot_out"));
                attendance.setPosition(rs.getString("position"));

                attendanceList.add(attendance);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }
    private static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static int getTimeInCountToday(){
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()){

            String query = 
                "SELECT COUNT(*) as time_in_count " +
                "FROM attendance " +
                "WHERE date = CURDATE() " +
                "AND (time_in IS NOT NULL AND time_in != '00:00:00')";

            ResultSet rs = statement.executeQuery(query);

            if (rs.next()){
                count = rs.getInt("time_in_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    // Count time out events for today
    public static int getTimeOutCountToday(){
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()){

            String query = 
                "SELECT COUNT(*) as time_out_count " +
                "FROM attendance " +
                "WHERE date = CURDATE() " +
                "AND (time_out IS NOT NULL AND time_out != '00:00:00')";

            ResultSet rs = statement.executeQuery(query);

            if (rs.next()){
                count = rs.getInt("time_out_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }


        public static ObservableList<Attendance> getInstruction(){
            ObservableList<Attendance> attendance = FXCollections.observableArrayList();
            try (Connection connection = DatabaseUtil.getConnection();
                Statement statement = connection.createStatement()){

                ResultSet rs = statement.executeQuery(
                    "SELECT CONCAT(u.user_fname, ' ', u.user_lname) AS name, c.date, " +
                    "c.time_in as timeIn, c.time_out as timeOut, c.time_notation as notation, c.attendance_status, p.position_name AS position " +
                    "FROM attendance c " +
                    "JOIN user u ON c.user_id = u.user_id " +
                    "JOIN assignment a ON u.user_id = a.user_id " +
                    "JOIN position p ON a.position_id = p.position_id " +
                    "JOIN department d ON p.department_id = d.department_id " +
                    "WHERE d.department_id = 2 AND u.user_status = 1 GROUP BY c.attendance_id ORDER BY c.attendance_id;"
                );

                while (rs.next()) {
                    int statusInt = rs.getInt("attendance_status");
                    String inRaw = rs.getString("timeIn");
                    String outRaw = rs.getString("timeOut");

                    String in = to12Hour(inRaw);
                    String out = to12Hour(outRaw);

                    String statusString;
                    if (statusInt == 1) {
                        statusString = "   ✓";
                    } else if (statusInt == 2) {
                        statusString = "Late";
                    } else if (statusInt == 3) {
                        statusString = "No Out";
                    } else {
                        statusString = "Present";
                    }

                    Attendance a = new Attendance(
                        rs.getString("name"),
                        rs.getDate("date"),
                        in,
                        out,
                        rs.getString("notation"),
                        statusString
                    );
                    a.setPosition(rs.getString("position"));
                    attendance.add(a);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return attendance;
        }

        public static ObservableList<Attendance> getAttendanceByFilters(String departmentName, Integer year, Integer month) {
           ObservableList<Attendance> attendance = FXCollections.observableArrayList();
           try (Connection connection = DatabaseUtil.getConnection()) {

               StringBuilder query = new StringBuilder(
                   "SELECT CONCAT(u.user_fname, ' ', u.user_lname) AS name, " +
                   "c.date, " +
                   "p.position_name AS position, " +
                   "d.department_name AS department, " +
                   "MAX(CASE WHEN c.time_notation = 'AM' THEN c.time_in END) AS am_in, " +
                   "MAX(CASE WHEN c.time_notation = 'AM' THEN c.time_out END) AS am_out, " +
                   "MAX(CASE WHEN c.time_notation = 'PM' THEN c.time_in END) AS pm_in, " +
                   "MAX(CASE WHEN c.time_notation = 'PM' THEN c.time_out END) AS pm_out, " +
                   "MAX(c.overtime_in) AS ot_in, " +
                   "MAX(c.overtime_out) AS ot_out, " +
                   "CASE " +
                   "  WHEN MAX(c.attendance_status) = 3 THEN 'No Out' " +
                   "  WHEN MAX(c.attendance_status) = 2 THEN 'Late' " +
                   "  WHEN (MAX(CASE WHEN c.time_notation = 'AM' THEN c.time_in END) IS NOT NULL OR " +
                   "        MAX(CASE WHEN c.time_notation = 'PM' THEN c.time_in END) IS NOT NULL) THEN 'Present' " +
                   "  ELSE 'Absent' " +
                   "END AS attendance_status " +
                   "FROM attendance c " +
                   "JOIN user u ON c.user_id = u.user_id " +
                   "JOIN assignment a ON u.user_id = a.user_id " +
                   "JOIN position p ON a.position_id = p.position_id " +
                   "JOIN department d ON p.department_id = d.department_id " +
                   "WHERE u.user_status = 1 "
               );

               // Add department filter
               if (departmentName != null && !departmentName.equalsIgnoreCase("All")) {
                   query.append("AND d.department_name = ? ");
               }

               // Add date filter
               if (year != null && month != null) {
                   query.append("AND YEAR(c.date) = ? AND MONTH(c.date) = ? ");
               }

               query.append("GROUP BY u.user_id, c.date, p.position_name, d.department_name ");
               query.append("ORDER BY c.date DESC, name ASC");

               PreparedStatement statement = connection.prepareStatement(query.toString());
               int paramIndex = 1;

               if (departmentName != null && !departmentName.equalsIgnoreCase("All")) {
                   statement.setString(paramIndex++, departmentName);
               }

               if (year != null && month != null) {
                   statement.setInt(paramIndex++, year);
                   statement.setInt(paramIndex++, month);
               }

               ResultSet rs = statement.executeQuery();

               while (rs.next()) {
                   // Create attendance record with the new format
                   Attendance a = new Attendance(
                       rs.getString("name"),
                       rs.getDate("date"),
                       rs.getString("am_in"),
                       rs.getString("am_out"),
                       rs.getString("pm_in"),
                       rs.getString("pm_out"),
                       rs.getString("attendance_status")
                   );
                   a.setOvertimeIn(rs.getString("ot_in"));
                   a.setOvertimeOut(rs.getString("ot_out"));
                   a.setPosition(rs.getString("position"));
                   a.setDeptName(rs.getString("department"));
                   attendance.add(a);
               }

           } catch (SQLException e) {
               e.printStackTrace();
           }
           return attendance;
       }
        public static ObservableList<Attendance> getAttendancebyDate(){
        ObservableList<Attendance> attendance = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
            Statement statement = connection.createStatement()){
            
            ResultSet rs = statement.executeQuery("SELECT CONCAT(u.user_fname, ' ', u.user_lname) AS name, c.date, "
            + "c.time_in as timeIn, c.time_out as timeOut, c.time_notation as notation, c.attendance_status \n" +
            "FROM attendance c \n" +
            "JOIN user u ON c.user_id = u.user_id "+
            "JOIN assignment a ON u.user_id = a.user_id " +
            "JOIN position p ON a.position_id = p.position_id " +
            "JOIN department d ON p.department_id = d.department_id " +
            "WHERE u.user_status = 1 GROUP BY c.attendance_id ORDER BY c.attendance_id;");
            
            while (rs.next()) {
                int statusInt = rs.getInt("attendance_status");
                String in= rs.getString("timeIn");
                String out= rs.getString("timeOut");
                String notationPM = rs.getString("notation");
                String statusString;
                    if(notationPM.equals("PM")){
                        String[] splitIn = in.split(":");
                        String[] splitOut = out.split(":");
                        int convertIn = Integer.parseInt(splitIn[0]);
                        int convertOut = Integer.parseInt(splitOut[0]);
                        if(convertIn >= 13){
                            convertIn = convertIn - 12;
                            if(convertIn>9){
                                in = (convertIn+"")+ ":"+splitIn[1]+":"+splitIn[2];
                            }else{
                                in = "0"+(convertIn+"")+ ":"+splitIn[1]+":"+splitIn[2];
                            }
                        }if(convertOut >= 13){
                            convertOut = convertOut - 12;
                            if(convertOut>9){
                                out = (convertOut+"")+ ":"+splitOut[1]+":"+splitOut[2];
                            }else{
                                out = "0"+(convertOut+"")+ ":"+splitOut[1]+":"+splitOut[2];
                            }
                        }
                        
                    }
                   if(out.equals("00:00:00")){
                       out = " ";
                   }

                if (statusInt == 1) {
                    statusString = "  ✓";
                } else if (statusInt == 2) {
                    statusString = "Late";
                } else if (statusInt == 3) {
                    statusString = "Undertime";
                } else {
                    statusString = "No Out";
                }

                attendance.add(new Attendance(
                    rs.getString("name"),
                    rs.getDate("date"),
                  in,
                 out, 
                 rs.getString("notation"),
         statusString
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendance;
    }
    public static ObservableList<Attendance> getOvertimeRecords(int user_id, int year, int month) {
        ObservableList<Attendance> overtimeRecords = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection()) {

            String query = "SELECT a.date, " +
                          "CONCAT(u.user_fname, ' ', u.user_lname) AS name, " +
                          "a.overtime_in, a.overtime_out, " +
                          "a.overtime_notation " +
                          "FROM attendance a " +
                          "JOIN user u ON a.user_id = u.user_id " +
                          "WHERE a.user_id = ? AND YEAR(a.date) = ? AND MONTH(a.date) = ? " +
                          "AND a.overtime_in IS NOT NULL " +
                          "ORDER BY a.date";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user_id);
            preparedStatement.setInt(2, year);
            preparedStatement.setInt(3, month);

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                // Use an existing constructor - for example, the one that takes name and date
                Attendance record = new Attendance(
                    rs.getString("name"),
                    rs.getDate("date"),
                    "", "", "", "", ""  // Provide empty strings for required parameters
                );

                String overtimeIn = rs.getString("overtime_in");
                String overtimeOut = rs.getString("overtime_out");

                if (overtimeIn != null && !overtimeIn.equals("00:00:00")) {
                    record.setOvertimeIn(formatTimeForDisplay(overtimeIn));
                } else {
                    record.setOvertimeIn("    --");
                }

                if (overtimeOut != null && !overtimeOut.equals("00:00:00")) {
                    record.setOvertimeOut(formatTimeForDisplay(overtimeOut));
                } else {
                    record.setOvertimeOut("    --");
                }

                record.setOvertimeNotation(rs.getString("overtime_notation"));
                overtimeRecords.add(record);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return overtimeRecords;
    }
        
    public static ObservableList<Attendance> getAttendancebyLate(int user_id){
        ObservableList<Attendance> attendanceList = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection()){

            // Updated query to include overtime fields
            String query = "SELECT a.date, " +
                          "CONCAT(u.user_fname, ' ', u.user_lname) AS name, " +
                          "a.time_in, a.time_out, " +
                          "a.time_notation, " +
                          "a.attendance_status, " +
                          "a.overtime_in, a.overtime_out, " +
                          "a.overtime_notation " +
                          "FROM attendance a " +
                          "JOIN user u ON a.user_id = u.user_id " +
                          "WHERE a.user_id = ? " +
                          "ORDER BY a.date, a.time_notation, a.time_in";

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, user_id);
            ResultSet rs = preparedStatement.executeQuery();

            // Map to store attendance by date
            Map<Date, Attendance> attendanceMap = new HashMap<>();

            while (rs.next()) {
                Date date = rs.getDate("date");
                String name = rs.getString("name");
                String timeIn = rs.getString("time_in");
                String timeOut = rs.getString("time_out");
                String notation = rs.getString("time_notation");
                String overtimeIn = rs.getString("overtime_in");
                String overtimeOut = rs.getString("overtime_out");
                String overtimeNotation = rs.getString("overtime_notation");
                int statusInt = rs.getInt("attendance_status");

                String statusString;
                if (statusInt == 1) {
                    statusString = "Present";
                } else if (statusInt == 2) {
                    statusString = "Late";
                } else if (statusInt == 3) {
                    statusString = "Undertime";
                } else if (statusInt == 4) {
                    statusString = "Late";
                } else {
                    statusString = "No Out";
                }

                // Check if we already have an entry for this date
                Attendance record = attendanceMap.get(date);
                if (record == null) {
                    // Create new record with default values
                    record = new Attendance(
                        name,
                        date,
                        "    --",  // Default AM IN
                        "    --",  // Default AM OUT
                        "    --",  // Default PM IN
                        "    --",  // Default PM OUT
                        statusString
                    );
                    attendanceMap.put(date, record);
                }

                // Update the record based on notation (AM/PM)
                if ("AM".equals(notation)) {
                    if (timeIn != null && !timeIn.equals("00:00:00")) {
                        record.setTimeInAm(formatTimeForDisplay(timeIn));
                    }
                    if (timeOut != null && !timeOut.equals("00:00:00")) {
                        record.setTimeOutAm(formatTimeForDisplay(timeOut));
                    }
                } else if ("PM".equals(notation)) {
                    if (timeIn != null && !timeIn.equals("00:00:00")) {
                        record.setTimeInPm(formatTimeForDisplay(timeIn));
                    }
                    if (timeOut != null && !timeOut.equals("00:00:00")) {
                        record.setTimeOutPm(formatTimeForDisplay(timeOut));
                    }
                }

                // Set overtime fields
                if (overtimeIn != null && !overtimeIn.equals("00:00:00")) {
                    record.setOvertimeIn(formatTimeForDisplay(overtimeIn));
                } else {
                    record.setOvertimeIn("    --");
                }

                if (overtimeOut != null && !overtimeOut.equals("00:00:00")) {
                    record.setOvertimeOut(formatTimeForDisplay(overtimeOut));
                } else {
                    record.setOvertimeOut("    --");
                }

                record.setOvertimeNotation(overtimeNotation);

                // Update status - prioritize non-Present statuses
                if (!"Present".equals(statusString)) {
                    record.setAttendance_status(statusString);
                }
            }

            // Add all records to the observable list
            attendanceList.addAll(attendanceMap.values());

            // Sort by date
            attendanceList.sort((a1, a2) -> a1.getDate().compareTo(a2.getDate()));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }

    // Get employees present today (those with at least one time record)
    public static int getPresentToday() {
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT COUNT(DISTINCT user_id) as present_count " +
                          "FROM attendance " +
                          "WHERE date = CURDATE() " +
                          "AND (time_in IS NOT NULL AND time_in != '00:00:00' " +
                          "OR time_out IS NOT NULL AND time_out != '00:00:00' " +
                          "OR overtime_in IS NOT NULL AND overtime_in != '00:00:00')";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                count = rs.getInt("present_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Get employees absent today (active employees without any attendance record today)
    public static int getAbsentToday() {
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT COUNT(*) as absent_count " +
                          "FROM user u " +
                          "WHERE u.user_status = 1 " +
                          "AND u.user_id NOT IN (" +
                          "    SELECT DISTINCT user_id FROM attendance WHERE date = CURDATE()" +
                          ")";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                count = rs.getInt("absent_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    // Get employees on leave today (you'll need to implement this based on your leave system)
    public static int getOnLeaveToday() {
        // This is a placeholder - you'll need to implement based on your leave tracking system
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            // Example query - adjust based on your actual leave table structure
            String query = "SELECT COUNT(*) as leave_count " +
                          "FROM leaves l " +
                          "WHERE CURDATE() BETWEEN l.start_date AND l.end_date " +
                          "AND l.status = 'approved'";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                count = rs.getInt("leave_count");
            }
        } catch (SQLException e) {
            // If leave table doesn't exist yet, return 0
            System.out.println("Leave tracking not implemented yet: " + e.getMessage());
        }
        return count;
    }

    // Add this helper method to your Attendance class
    private static String formatTimeForDisplay(String time) {
        if (time == null || time.trim().isEmpty() || time.equals("00:00:00")) {
            return "    --";
        }
        try {
            LocalTime localTime = LocalTime.parse(time);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            return localTime.format(formatter);
        } catch (Exception e) {
            return time; 
        }
    }
   public static void timeIn(int userId, String time) {
    String key = Config.getSecretKey(); // Get Key
    
    try (Connection connection = DatabaseUtil.getConnection()) {
        connection.setAutoCommit(false);
        
        // decrypt time_in/out in the SELECT to check for existing records
        String selectQuery = "SELECT attendance_id, " +
                             "AES_DECRYPT(time_in, ?) as time_in, " +
                             "AES_DECRYPT(time_out, ?) as time_out " +
                             "FROM attendance WHERE user_id = ? AND date = ? " +
                             "ORDER BY attendance_id DESC LIMIT 1 FOR UPDATE";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setString(1, key);
            selectStmt.setString(2, key);
            selectStmt.setInt(3, userId);
            selectStmt.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));

            try (ResultSet rs = selectStmt.executeQuery()) {
                LocalTime parsedTime = LocalTime.parse(time);
                String notation = (parsedTime.getHour() < 12) ? "AM" : "PM";

                if (rs.next()) {
                    // Read decrypted values using bytesToString
                    String existingTimeIn = bytesToString(rs.getBytes("time_in"));
                    String existingTimeOut = bytesToString(rs.getBytes("time_out"));
                    
                    boolean timeInEmpty = (existingTimeIn == null) || existingTimeIn.trim().isEmpty() || "00:00:00".equals(existingTimeIn);
                    boolean timeOutEmpty = (existingTimeOut == null) || existingTimeOut.trim().isEmpty() || "00:00:00".equals(existingTimeOut);

                    if (timeInEmpty && timeOutEmpty) {
                        int attendanceId = rs.getInt("attendance_id");
                        // UPDATE with ENCRYPTION
                        try (PreparedStatement updateStmt = connection.prepareStatement(
                                "UPDATE attendance SET time_in = AES_ENCRYPT(?, ?), time_notation = ? WHERE attendance_id = ?")) {
                            updateStmt.setString(1, time);
                            updateStmt.setString(2, key);
                            updateStmt.setString(3, notation);
                            updateStmt.setInt(4, attendanceId);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // INSERT with ENCRYPTION
                        try (PreparedStatement insertStmt = connection.prepareStatement(
                                "INSERT INTO attendance (user_id, date, time_in, time_notation) VALUES (?, ?, AES_ENCRYPT(?, ?), ?)")) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                            insertStmt.setString(3, time);
                            insertStmt.setString(4, key);
                            insertStmt.setString(5, notation);
                            insertStmt.executeUpdate();
                        }
                    }
                } else {
                    // INSERT with ENCRYPTION (First record of day)
                    try (PreparedStatement insertStmt = connection.prepareStatement(
                            "INSERT INTO attendance (user_id, date, time_in, time_notation) VALUES (?, ?, AES_ENCRYPT(?, ?), ?)")) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                        insertStmt.setString(3, time);
                        insertStmt.setString(4, key);
                        insertStmt.setString(5, notation);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
        connection.commit();
    } catch (SQLException e) {
        e.printStackTrace(); // Handle rollback in catch block if needed (as per your original code)
    }
}

public static void timeOut(int userId, String time) {
    String key = Config.getSecretKey();

    try (Connection connection = DatabaseUtil.getConnection()) {
        connection.setAutoCommit(false);
        
        // Decrypt in WHERE clause to find the active login
        // Note: We use CAST(AES_DECRYPT... AS CHAR) to ensure proper string comparison in SQL
        String selectQuery = "SELECT attendance_id FROM attendance " +
                             "WHERE user_id = ? AND date = ? " +
                             "AND AES_DECRYPT(time_in, ?) IS NOT NULL " +
                             "AND CAST(AES_DECRYPT(time_in, ?) AS CHAR) != '00:00:00' " +
                             "AND (time_out IS NULL OR CAST(AES_DECRYPT(time_out, ?) AS CHAR) = '00:00:00') " +
                             "ORDER BY attendance_id DESC LIMIT 1 FOR UPDATE";

        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, userId);
            selectStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
            selectStmt.setString(3, key);
            selectStmt.setString(4, key);
            selectStmt.setString(5, key);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    int attendanceId = rs.getInt("attendance_id");
                    // UPDATE with ENCRYPTION
                    try (PreparedStatement updateStmt = connection.prepareStatement(
                            "UPDATE attendance SET time_out = AES_ENCRYPT(?, ?) WHERE attendance_id = ?")) {
                        updateStmt.setString(1, time);
                        updateStmt.setString(2, key);
                        updateStmt.setInt(3, attendanceId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Logic for creating new record if no Time In found (Optional, based on your original logic)
                    LocalTime parsedTime = LocalTime.parse(time);
                    String notation = (parsedTime.getHour() < 12) ? "AM" : "PM";
                    try (PreparedStatement insertStmt = connection.prepareStatement(
                            "INSERT INTO attendance (user_id, date, time_in, time_out, time_notation) " +
                            "VALUES (?, ?, AES_ENCRYPT('00:00:00', ?), AES_ENCRYPT(?, ?), ?)")) {
                        insertStmt.setInt(1, userId);
                        insertStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                        insertStmt.setString(3, key);
                        insertStmt.setString(4, time);
                        insertStmt.setString(5, key);
                        insertStmt.setString(6, notation);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
        connection.commit();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public static void timeOutRecentRecord(int userId, String time) {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String selectQuery = "SELECT attendance_id FROM attendance WHERE user_id = ? AND date = ? AND time_out IS NULL ORDER BY attendance_id DESC LIMIT 1";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, userId);
                selectStmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int attendanceId = rs.getInt("attendance_id");
                        String updateQuery = "UPDATE attendance SET time_out = ? WHERE attendance_id = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setString(1, time);
                            updateStmt.setInt(2, attendanceId);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        System.out.println("No recent record to time out for user: " + userId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    public static boolean userHasTimeInToday(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? " +
                     "AND time_in IS NOT NULL AND time_in != '00:00:00' " +
                     "AND (time_out IS NULL OR time_out = '00:00:00')")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean userHasTimeOutToday(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? " +
                     "AND time_out IS NOT NULL AND time_out != '00:00:00' " +
                     "AND time_in IS NOT NULL AND time_in != '00:00:00'")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean userHasTimeOutTodayBetween(int userId, String startTime, String endTime, String notation) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     //"SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? AND time_in IS NOT NULL ORDER by time_in desc limit 1")) {
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? AND time_out BETWEEN ? AND ? AND time_notation = ?")){
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            statement.setString(3, startTime);
            statement.setString(4, endTime);
            statement.setString(5, notation);
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0; // Check if there is at least one record

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Assume no time-in record on error
        }
    }
    public static boolean userHasMissingTimeOut(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? " +
                     "AND time_out IS NULL AND time_in IS NOT NULL AND time_in != '00:00:00' " +
                     "AND date = ?")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean userHasReachedDailyAttendanceLimit(int userId, int attendanceLimit) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? AND time_in IS NOT NULL AND time_out IS NOT NULL")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) >= attendanceLimit; // Check if there is at least one record
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Assume no time-out record on error
        }
    }

    public static String getHoursSinceLastTimeInToday(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT time_in FROM attendance WHERE user_id = ? AND date = ? AND time_in IS NOT NULL AND time_in != '00:00:00' AND (time_out IS NULL OR time_out = '00:00:00') ORDER BY time_in DESC LIMIT 1")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return "No time-in record found";
                }
                String timeInStr = resultSet.getString("time_in");
                LocalTime earlierTime = LocalTime.parse(timeInStr);
                LocalTime laterTime = LocalTime.now();
                long hours = ChronoUnit.HOURS.between(earlierTime, laterTime);
                long minutes = ChronoUnit.MINUTES.between(earlierTime, laterTime) % 60;
                return hours + " HOUR(S) AND " + minutes + " MINUTES";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "DATABASE ERROR: Unable to fetch time-in record";
        }
    }


//    public static String getHoursSinceLastTimeIn(int userId){
//        try (Connection connection = DatabaseUtil.getConnection();
//             PreparedStatement statement = connection.prepareStatement(
//                     //"SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? AND time_in IS NOT NULL ORDER by time_in desc limit 1")) {
//                     "SELECT time_in FROM attendance WHERE user_id = ? AND time_in IS NOT NULL AND time_out IS NULL;")){
//            statement.setInt(1, userId);
//            ResultSet resultSet = statement.executeQuery();
//            resultSet.next();
//
//
//            LocalTime earlierTime = LocalTime.parse(resultSet.getString("time_in"));
//            LocalTime laterTime = LocalTime.now();
//
//            // Calculate the time difference
//            long hours = ChronoUnit.HOURS.between(earlierTime, laterTime);
//            long minutes = ChronoUnit.MINUTES.between(earlierTime, laterTime) % 60;
//
//
//            return hours + " HOUR(S) AND " + minutes + " MINUTES";
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return "DATABASE ERROR: No time-in record found";
//        }
//
//    }
    public static boolean userHasMissingTimeOutToday(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? " +
                     "AND time_out IS NULL AND time_in IS NOT NULL AND time_in != '00:00:00'")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static String getHoursSinceLastTimeIn(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT date, time_in FROM attendance WHERE user_id = ? AND time_in IS NOT NULL AND time_out IS NULL ORDER BY date DESC, time_in DESC LIMIT 1")) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                LocalDateTime earlierDateTime = LocalDateTime.of(
                        resultSet.getDate("date").toLocalDate(),
                        LocalTime.parse(resultSet.getString("time_in"))
                );
                LocalDateTime laterDateTime = LocalDateTime.now();

                // Calculate the time difference
                long totalHours = ChronoUnit.HOURS.between(earlierDateTime, laterDateTime);

                long daysAsHours = ChronoUnit.DAYS.between(earlierDateTime, laterDateTime) * 24;
                long hours = totalHours % 24;
                long minutes = ChronoUnit.MINUTES.between(earlierDateTime, laterDateTime) % 60;

                return (daysAsHours + hours) + " HOURS AND " + minutes + " MINUTES";
            } else {
                return "No time-in record found";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "DATABASE ERROR: Unable to fetch time-in record";
        }
    }
    
    public static void timeInOT(int userId, String time, String notation) {
        // 1. Get the Key
        String key = Config.getSecretKey(); 
        java.sql.Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);

            // Check for existing record
            String selectQuery = "SELECT attendance_id, " +
                    "CAST(AES_DECRYPT(overtime_in, ?) AS CHAR) as overtime_in, " +
                    "CAST(AES_DECRYPT(overtime_out, ?) AS CHAR) as overtime_out " +
                    "FROM attendance WHERE user_id = ? AND date = ? ORDER BY attendance_id DESC LIMIT 1 FOR UPDATE";

            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setString(1, key);
                selectStmt.setString(2, key);
                selectStmt.setInt(3, userId);
                selectStmt.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int attendanceId = rs.getInt("attendance_id");
                        String existingOtIn = rs.getString("overtime_in");
                        String existingOtOut = rs.getString("overtime_out");

                        boolean otInEmpty = existingOtIn == null || existingOtIn.trim().isEmpty() || "00:00:00".equals(existingOtIn);
                        boolean otOutEmpty = existingOtOut == null || existingOtOut.trim().isEmpty() || "00:00:00".equals(existingOtOut);

                        if (otInEmpty && otOutEmpty) {
                            // UPDATE: Use AES_ENCRYPT
                            try (PreparedStatement updateStmt = connection.prepareStatement(
                                    "UPDATE attendance SET overtime_in = AES_ENCRYPT(?, ?), overtime_notation = ? WHERE attendance_id = ?")) {
                                updateStmt.setString(1, time);
                                updateStmt.setString(2, key); // Key for encryption
                                updateStmt.setString(3, notation);
                                updateStmt.setInt(4, attendanceId);
                                updateStmt.executeUpdate();
                            }
                        } else if (!otInEmpty && otOutEmpty) {
                            System.out.println("Overtime already timed in for user: " + userId);
                        } else {
                            // NEW ROW (e.g. 2nd OT shift): Encrypt defaults AND new time
                            try (PreparedStatement insertStmt = connection.prepareStatement(
                                    "INSERT INTO attendance (user_id, date, time_in, time_notation, overtime_in, overtime_notation) " +
                                    "VALUES (?, ?, AES_ENCRYPT('00:00:00', ?), 'OT', AES_ENCRYPT(?, ?), ?)")) {
                                insertStmt.setInt(1, userId);
                                insertStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                                insertStmt.setString(3, key); // Key for default time_in
                                insertStmt.setString(4, time);
                                insertStmt.setString(5, key); // Key for overtime_in
                                insertStmt.setString(6, notation);
                                insertStmt.executeUpdate();
                            }
                        }
                    } else {
                        // NEW ROW (First Record of day is OT): Encrypt defaults AND new time
                        try (PreparedStatement insertStmt = connection.prepareStatement(
                                "INSERT INTO attendance (user_id, date, time_in, time_notation, overtime_in, overtime_notation) " +
                                "VALUES (?, ?, AES_ENCRYPT('00:00:00', ?), 'OT', AES_ENCRYPT(?, ?), ?)")) {
                            insertStmt.setInt(1, userId);
                            insertStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                            insertStmt.setString(3, key); // Key for default time_in
                            insertStmt.setString(4, time);
                            insertStmt.setString(5, key); // Key for overtime_in
                            insertStmt.setString(6, notation);
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ignore) {}
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try { connection.setAutoCommit(true); connection.close(); } catch (SQLException ignore) {}
            }
        }
    }

    public static void timeOutOT(int userId, String time) {
        // 1. Get the Key
        String key = Config.getSecretKey();
        java.sql.Connection connection = null;
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);

            // Check for active OT session (OT IN exists, OT OUT is missing/empty)
            String selectQuery = "SELECT attendance_id FROM attendance " +
                                 "WHERE user_id = ? AND date = ? " +
                                 "AND CAST(AES_DECRYPT(overtime_in, ?) AS CHAR) != '00:00:00' " +
                                 "AND (overtime_out IS NULL OR CAST(AES_DECRYPT(overtime_out, ?) AS CHAR) = '00:00:00') " +
                                 "ORDER BY attendance_id DESC LIMIT 1 FOR UPDATE";

            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, userId);
                selectStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                selectStmt.setString(3, key);
                selectStmt.setString(4, key);

                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int attendanceId = rs.getInt("attendance_id");
                        // UPDATE: Use AES_ENCRYPT
                        try (PreparedStatement updateStmt = connection.prepareStatement(
                                "UPDATE attendance SET overtime_out = AES_ENCRYPT(?, ?) WHERE attendance_id = ?")) {
                            updateStmt.setString(1, time);
                            updateStmt.setString(2, key);
                            updateStmt.setInt(3, attendanceId);
                            updateStmt.executeUpdate();
                        }
                    } else {
                        // Fallback: Insert full new row with OT OUT (rare case)
                        try (PreparedStatement insertStmt = connection.prepareStatement(
                                "INSERT INTO attendance (user_id, date, time_in, time_notation, overtime_in, overtime_out, overtime_notation) " +
                                "VALUES (?, ?, AES_ENCRYPT('00:00:00', ?), 'OT', AES_ENCRYPT('00:00:00', ?), AES_ENCRYPT(?, ?), 'OT')")) 
                        {
                            insertStmt.setInt(1, userId);
                            insertStmt.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                            insertStmt.setString(3, key); // default time_in
                            insertStmt.setString(4, key); // default ot_in
                            insertStmt.setString(5, time);
                            insertStmt.setString(6, key); // ot_out encryption
                            insertStmt.executeUpdate();
                        }
                    }
                }
            }
            connection.commit();
        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ignore) {}
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try { connection.setAutoCommit(true); connection.close(); } catch (SQLException ignore) {}
            }
        }
    }
    public static boolean userHasTimeInOTToday(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? AND overtime_in IS NOT NULL AND overtime_out IS NULL")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if user has overtime time out today
    public static boolean userHasTimeOutOTToday(int userId) {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM attendance WHERE user_id = ? AND date = ? AND overtime_out IS NOT NULL")) {
            statement.setInt(1, userId);
            statement.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static ObservableList<Attendance> getOvertimeForDocx() {
        ObservableList<Attendance> overtimeDocx = FXCollections.observableArrayList();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT a.user_id as id, CONCAT(u.user_fname, ' ', u.user_lname) AS name, " +
                          "DATE_FORMAT(a.date, '%M %Y') as dtrDate, DAY(a.date) as day, " +
                          "a.overtime_in as timeIn, a.overtime_out as timeOut " +
                          "FROM attendance a " +
                          "JOIN user u ON a.user_id = u.user_id " +
                          "WHERE a.overtime_in IS NOT NULL AND a.overtime_in != '00:00:00'";

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                overtimeDocx.add(new Attendance(
                        rs.getString("dtrDate"),
                        rs.getInt("day"),
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("timeIn"),
                        rs.getString("timeOut"),
                        "", ""  // Empty strings for AM/PM fields
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return overtimeDocx;
    }



    public static class Notation{
        public static final String AM = "AM";
        public static final String PM = "PM";
    }
    public static int getPresentTodayCount() {
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT COUNT(DISTINCT user_id) as present_count " +
                          "FROM attendance " +
                          "WHERE date = CURDATE() " +
                          "AND (time_in IS NOT NULL AND time_in != '00:00:00' " +
                          "OR time_out IS NOT NULL AND time_out != '00:00:00' " +
                          "OR overtime_in IS NOT NULL AND overtime_in != '00:00:00')";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                count = rs.getInt("present_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get the count of employees absent today (active employees without any attendance record today)
     */
    public static int getAbsentTodayCount() {
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT COUNT(*) as absent_count " +
                          "FROM user u " +
                          "WHERE u.user_status = 1 " +
                          "AND u.user_id NOT IN (" +
                          "    SELECT DISTINCT user_id FROM attendance WHERE date = CURDATE()" +
                          ")";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                count = rs.getInt("absent_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get the count of employees on approved leave today
     */
    public static int getOnLeaveTodayCount() {
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            // First, check if leave_requests table exists
            String checkTableQuery = 
                "SELECT COUNT(*) as table_exists FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_name = 'leave_requests'";

            ResultSet tableCheck = statement.executeQuery(checkTableQuery);
            boolean tableExists = false;
            if (tableCheck.next()) {
                tableExists = tableCheck.getInt("table_exists") > 0;
            }

            if (tableExists) {
                // Query for employees on approved leave today
                String query = "SELECT COUNT(DISTINCT user_id) as leave_count " +
                              "FROM leave_requests " +
                              "WHERE CURDATE() BETWEEN start_date AND end_date " +
                              "AND status = 'approved' " +
                              "AND user_id IN (SELECT user_id FROM user WHERE user_status = 1)";
                ResultSet rs = statement.executeQuery(query);

                if (rs.next()) {
                    count = rs.getInt("leave_count");
                }
            } else {
                System.out.println("Leave requests table not found. Returning 0 for on leave count.");
            }

        } catch (SQLException e) {
            System.out.println("Error checking leave requests: " + e.getMessage());
            // Return 0 if there's any error
        }
        return count;
    }

    /**
     * Get total count of active employees
     */
    public static int getTotalActiveEmployeesCount() {
        int count = 0;
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT COUNT(*) as total_count FROM user WHERE user_status = 1";
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {
                count = rs.getInt("total_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Get detailed attendance summary for today
     */
    public static Map<String, Integer> getTodayAttendanceSummary() {
        Map<String, Integer> summary = new HashMap<>();

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            // Total active employees
            String totalQuery = "SELECT COUNT(*) as total FROM user WHERE user_status = 1";
            ResultSet totalRs = statement.executeQuery(totalQuery);
            if (totalRs.next()) {
                summary.put("total", totalRs.getInt("total"));
            }

            // Present today (employees with at least one attendance record)
            String presentQuery = 
                "SELECT COUNT(DISTINCT user_id) as present " +
                "FROM attendance " +
                "WHERE date = CURDATE() " +
                "AND (time_in IS NOT NULL AND time_in != '00:00:00' " +
                "OR time_out IS NOT NULL AND time_out != '00:00:00' " +
                "OR overtime_in IS NOT NULL AND overtime_in != '00:00:00')";
            ResultSet presentRs = statement.executeQuery(presentQuery);
            if (presentRs.next()) {
                summary.put("present", presentRs.getInt("present"));
            }

            // Absent today (active employees without any attendance record)
            String absentQuery = 
                "SELECT COUNT(*) as absent " +
                "FROM user u " +
                "WHERE u.user_status = 1 " +
                "AND u.user_id NOT IN (" +
                "    SELECT DISTINCT user_id FROM attendance WHERE date = CURDATE()" +
                ")";
            ResultSet absentRs = statement.executeQuery(absentQuery);
            if (absentRs.next()) {
                summary.put("absent", absentRs.getInt("absent"));
            }

            // On leave today
            String leaveQuery = 
                "SELECT COUNT(DISTINCT user_id) as on_leave " +
                "FROM leave_requests " +
                "WHERE CURDATE() BETWEEN start_date AND end_date " +
                "AND status = 'approved' " +
                "AND user_id IN (SELECT user_id FROM user WHERE user_status = 1)";
            try {
                ResultSet leaveRs = statement.executeQuery(leaveQuery);
                if (leaveRs.next()) {
                    summary.put("onLeave", leaveRs.getInt("on_leave"));
                } else {
                    summary.put("onLeave", 0);
                }
            } catch (SQLException e) {
                summary.put("onLeave", 0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Set default values in case of error
            summary.put("total", 0);
            summary.put("present", 0);
            summary.put("absent", 0);
            summary.put("onLeave", 0);
        }

        return summary;
    }

    /**
     * Get gender distribution for active employees
     */
    public static Map<String, Integer> getGenderDistribution() {
        Map<String, Integer> distribution = new HashMap<>();

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            String query = "SELECT sex, COUNT(*) as count " +
                          "FROM user " +
                          "WHERE user_status = 1 " +
                          "GROUP BY sex";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                String gender = rs.getString("sex");
                int count = rs.getInt("count");
                distribution.put(gender != null ? gender : "Unknown", count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ensure we have entries for both Male and Female even if count is 0
        distribution.putIfAbsent("Male", 0);
        distribution.putIfAbsent("Female", 0);

        return distribution;
    }
    
    public static Map<String, Integer> getDepartmentAttendanceToday() {
        Map<String, Integer> deptData = new HashMap<>();
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            // Join User -> Assignment -> Position -> Department
            // Count distinct users present today per department
            String query = "SELECT d.department_name, COUNT(DISTINCT a.user_id) as count " +
                           "FROM attendance a " +
                           "JOIN user u ON a.user_id = u.user_id " +
                           "JOIN assignment ass ON u.user_id = ass.user_id " +
                           "JOIN position p ON ass.position_id = p.position_id " +
                           "JOIN department d ON p.department_id = d.department_id " +
                           "WHERE a.date = CURDATE() " +
                           "AND ass.status = 1 " + // Ensure active assignment
                           "AND (a.time_in IS NOT NULL AND a.time_in != '00:00:00') " +
                           "GROUP BY d.department_name";

            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                deptData.put(rs.getString("department_name"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deptData;
    }

    /**
     * NEW: Get count of On-Time vs Late employees for today
     * Replaces simple Login Percentile
     */
    public static Map<String, Integer> getPunctualityStats() {
        Map<String, Integer> stats = new HashMap<>();
        int lateCount = 0;
        int onTimeCount = 0;

        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement()) {

            // Assuming attendance_status 2 = Late, 1 = On Time/Check
            // You can also calculate this via time logic if status isn't reliable
            String query = "SELECT attendance_status, COUNT(*) as count " +
                           "FROM attendance " +
                           "WHERE date = CURDATE() " +
                           "AND time_in IS NOT NULL AND time_in != '00:00:00' " +
                           "GROUP BY attendance_status";

            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                int status = rs.getInt("attendance_status");
                int count = rs.getInt("count");

                // Adjust these IDs based on your database logic (1=Present, 2=Late, etc.)
                if (status == 2) { 
                    lateCount += count;
                } else {
                    onTimeCount += count;
                }
            }
            stats.put("Late", lateCount);
            stats.put("On Time", onTimeCount);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
    public static Map<String, Integer> getTotalEmployeesByDepartment() {
        Map<String, Integer> deptTotals = new HashMap<>();
        String query = "SELECT d.department_name, COUNT(u.user_id) as total " +
                       "FROM user u " +
                       "JOIN assignment a ON u.user_id = a.user_id AND a.status = 1 " +
                       "JOIN position p ON a.position_id = p.position_id " +
                       "JOIN department d ON p.department_id = d.department_id " +
                       "WHERE u.user_status = 1 " +
                       "GROUP BY d.department_name";
                       
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
             
            while(rs.next()) {
                deptTotals.put(rs.getString("department_name"), rs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deptTotals;
    }
    // ---------------------------------------------------------
    // NEW METHODS FOR TOP PERFORMERS WIDGET
    // ---------------------------------------------------------

    /**
     * Finds the employee with the earliest average Time-In for the current month.
     * Criteria: Must be "Present" (status 1) to count.
     */
    public static Attendance getEarlyBirdForMonth() {
        Attendance earlyBird = null;
        String key = Config.getSecretKey();
        
        // Map to store total seconds and day count for average calculation
        Map<Integer, Long> totalSecondsMap = new HashMap<>();
        Map<Integer, Integer> dayCountMap = new HashMap<>();
        Map<Integer, String> nameMap = new HashMap<>();

        String query = 
            "SELECT u.user_id, CONCAT(u.user_fname, ' ', u.user_lname) AS name, " +
            "CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) as decrypted_time, " +
            "a.time_notation " +
            "FROM attendance a " +
            "JOIN user u ON a.user_id = u.user_id " +
            "WHERE MONTH(a.date) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(a.date) = YEAR(CURRENT_DATE()) " +
            "AND a.attendance_status = 1 " + // Only count On-Time/Present days
            "AND u.user_status = 1";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, key); // Set key for decryption

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String name = rs.getString("name");
                    String timeStr = rs.getString("decrypted_time");
                    String notation = rs.getString("time_notation");

                    if (timeStr != null && !timeStr.equals("00:00:00")) {
                        try {
                            // Convert 12h format to Seconds since midnight for averaging
                            LocalTime time = LocalTime.parse(timeStr);
                            // Adjust for PM if necessary (though early birds are usually AM)
                            if ("PM".equalsIgnoreCase(notation) && time.getHour() < 12) {
                                time = time.plusHours(12);
                            } else if ("AM".equalsIgnoreCase(notation) && time.getHour() == 12) {
                                time = time.withHour(0);
                            }

                            long seconds = time.toSecondOfDay();

                            totalSecondsMap.put(userId, totalSecondsMap.getOrDefault(userId, 0L) + seconds);
                            dayCountMap.put(userId, dayCountMap.getOrDefault(userId, 0) + 1);
                            nameMap.putIfAbsent(userId, name);

                        } catch (Exception e) {
                            continue; // Skip parse errors
                        }
                    }
                }
            }

            // Find the minimum average
            double minAverageSeconds = Double.MAX_VALUE;
            int winningUserId = -1;

            for (Integer id : totalSecondsMap.keySet()) {
                int days = dayCountMap.get(id);
                if (days > 0) {
                    double avg = (double) totalSecondsMap.get(id) / days;
                    if (avg < minAverageSeconds) {
                        minAverageSeconds = avg;
                        winningUserId = id;
                    }
                }
            }

            if (winningUserId != -1) {
                // Return an Attendance object populated with User info
                earlyBird = new Attendance(winningUserId, nameMap.get(winningUserId));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Return a dummy object if no one found to prevent null pointer
        if (earlyBird == null) {
            earlyBird = new Attendance(0, "No Data");
        }
        
        return earlyBird;
    }

    /**
     * Calculates the specific average time string (e.g., "07:45 AM") for a user.
     */
    public static String getAverageEarlyTime(int userId) {
        String key = Config.getSecretKey();
        long totalSeconds = 0;
        int count = 0;

        String query = 
            "SELECT CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) as decrypted_time, a.time_notation " +
            "FROM attendance a " +
            "WHERE a.user_id = ? " +
            "AND MONTH(a.date) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(a.date) = YEAR(CURRENT_DATE()) " +
            "AND a.attendance_status = 1"; // Only count present/on-time

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, key);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String timeStr = rs.getString("decrypted_time");
                    String notation = rs.getString("time_notation");

                    if (timeStr != null && !timeStr.equals("00:00:00")) {
                        try {
                            LocalTime time = LocalTime.parse(timeStr);
                            if ("PM".equalsIgnoreCase(notation) && time.getHour() < 12) {
                                time = time.plusHours(12);
                            } else if ("AM".equalsIgnoreCase(notation) && time.getHour() == 12) {
                                time = time.withHour(0);
                            }
                            totalSeconds += time.toSecondOfDay();
                            count++;
                        } catch (Exception e) { continue; }
                    }
                }
            }

            if (count > 0) {
                long avgSeconds = totalSeconds / count;
                LocalTime avgTime = LocalTime.ofSecondOfDay(avgSeconds);
                return avgTime.format(DateTimeFormatter.ofPattern("hh:mm a"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "--:--";
    }
    /**
     * Finds the "Dynamo" (Employee with most hours logged) for the month.
     */
    public static Attendance getMostHoursForMonth() {
        Attendance dynamo = null;
        String key = Config.getSecretKey();
        Map<Integer, Long> totalMinutesMap = new HashMap<>();
        Map<Integer, String> nameMap = new HashMap<>();

        // Fetch all time-in/out for the current month
        String query = 
            "SELECT u.user_id, CONCAT(u.user_fname, ' ', u.user_lname) AS name, " +
            "CAST(AES_DECRYPT(a.time_in, ?) AS CHAR) as t_in, " +
            "CAST(AES_DECRYPT(a.time_out, ?) AS CHAR) as t_out, " +
            "a.time_notation " +
            "FROM attendance a " +
            "JOIN user u ON a.user_id = u.user_id " +
            "WHERE MONTH(a.date) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(a.date) = YEAR(CURRENT_DATE()) " +
            "AND a.attendance_status = 1"; // Present only

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, key);
            ps.setString(2, key);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String tIn = rs.getString("t_in");
                    String tOut = rs.getString("t_out");
                    String notation = rs.getString("time_notation");
                    
                    if (tIn != null && tOut != null && !tIn.equals("00:00:00") && !tOut.equals("00:00:00")) {
                        try {
                            LocalTime in = LocalTime.parse(tIn);
                            LocalTime out = LocalTime.parse(tOut);
                            
                            // Adjust for PM logic if needed (simple approximation)
                            if ("PM".equals(notation) && in.getHour() < 12) in = in.plusHours(12);
                            if ("PM".equals(notation) && out.getHour() < 12) out = out.plusHours(12);
                            // Handle crossing noon (AM in, PM out) scenario requires more complex logic, 
                            // but this fits your current structure.
                            
                            long minutes = ChronoUnit.MINUTES.between(in, out);
                            if (minutes > 0) {
                                totalMinutesMap.put(id, totalMinutesMap.getOrDefault(id, 0L) + minutes);
                                nameMap.putIfAbsent(id, rs.getString("name"));
                            }
                        } catch (Exception e) { continue; }
                    }
                }
            }
            
            // Find Max
            long maxMinutes = -1;
            int winnerId = -1;
            
            for (Map.Entry<Integer, Long> entry : totalMinutesMap.entrySet()) {
                if (entry.getValue() > maxMinutes) {
                    maxMinutes = entry.getValue();
                    winnerId = entry.getKey();
                }
            }
            
            if (winnerId != -1) {
                dynamo = new Attendance(winnerId, nameMap.get(winnerId));
                long hours = maxMinutes / 60;
                dynamo.setNotation(hours + " Hours Logged"); // Reusing notation field
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dynamo == null) {
            dynamo = new Attendance(0, "No Data");
            dynamo.setNotation("0 Hours Logged");
        }
        return dynamo;
    }

    /**
     * Finds the "Iron Man" (Perfect Attendance) for the month.
     * Criteria: Most days present, 0 Lates, 0 Absences (implied by max presence).
     */
    public static Attendance getPerfectAttendanceForMonth() {
        Attendance ironMan = null;

        // Query: Count days present, sum lates. Filter for 0 lates. Order by days present DESC.
        String query = 
            "SELECT u.user_id, CONCAT(u.user_fname, ' ', u.user_lname) AS name, " +
            "COUNT(a.attendance_id) as days_present, " +
            "SUM(CASE WHEN a.attendance_status = 2 THEN 1 ELSE 0 END) as lates_count " +
            "FROM attendance a " +
            "JOIN user u ON a.user_id = u.user_id " +
            "WHERE MONTH(a.date) = MONTH(CURRENT_DATE()) " +
            "AND YEAR(a.date) = YEAR(CURRENT_DATE()) " +
            "AND u.user_status = 1 " +
            "GROUP BY u.user_id " +
            "HAVING lates_count = 0 " + // Must have ZERO lates
            "ORDER BY days_present DESC " + // Prioritize those who came to work the most
            "LIMIT 1";

        try (Connection connection = DatabaseUtil.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("name");
                int streak = rs.getInt("days_present");

                ironMan = new Attendance(id, name);
                // We store the "Streak" text in the notation field or a generic field to retrieve it later
                ironMan.setNotation(streak + " Days Streak"); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (ironMan == null) {
            ironMan = new Attendance(0, "No Data");
            ironMan.setNotation("0 Days Streak");
        }

        return ironMan;
    }
    
    public static boolean isOtAuthorized(int userId, LocalDate date) {
        String query = "SELECT auth_id FROM overtime_authorization WHERE user_id = ? AND auth_date = ? AND status = 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static List<Integer> getAuthorizedUserIds(LocalDate date) {
        List<Integer> ids = new ArrayList<>();
        String query = "SELECT user_id FROM overtime_authorization WHERE auth_date = ? AND status = 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                ids.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
    public static void saveOtAuthorizations(List<Integer> authorizedUserIds, LocalDate date) {
        String deleteSql = "DELETE FROM overtime_authorization WHERE auth_date = ?";
        String insertSql = "INSERT INTO overtime_authorization (user_id, auth_date) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction

            // A. Clear existing for this date to avoid duplicates/conflicts
            try (PreparedStatement delPs = conn.prepareStatement(deleteSql)) {
                delPs.setDate(1, java.sql.Date.valueOf(date));
                delPs.executeUpdate();
            }

            // B. Insert new selections
            try (PreparedStatement insPs = conn.prepareStatement(insertSql)) {
                for (Integer uid : authorizedUserIds) {
                    insPs.setInt(1, uid);
                    insPs.setDate(2, java.sql.Date.valueOf(date));
                    insPs.addBatch();
                }
                insPs.executeBatch();
            }

            conn.commit(); // Commit Transaction
            System.out.println("OT Authorizations updated for " + date);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ObservableList<Attendance> getOtAuthorizationsByUser(int userId) {
        ObservableList<Attendance> list = FXCollections.observableArrayList();
        String query = "SELECT auth_id, auth_date, status FROM overtime_authorization WHERE user_id = ? ORDER BY auth_date DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Attendance att = new Attendance();
                att.setId(rs.getInt("auth_id"));
                att.setDate(rs.getDate("auth_date"));
                // We reuse 'attendance_status' field or a new one to store the status string
                att.setAttendance_status(rs.getInt("status") == 1 ? "Authorized" : "Revoked"); 
                list.add(att);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static boolean addOtAuthorization(int userId, LocalDate date) {
        // Use INSERT IGNORE to prevent crashing on duplicates
        String query = "INSERT IGNORE INTO overtime_authorization (user_id, auth_date, status) VALUES (?, ?, 1)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void deleteOtAuthorization(int authId) {
        String query = "DELETE FROM overtime_authorization WHERE auth_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, authId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
