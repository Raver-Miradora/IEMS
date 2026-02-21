package com.raver.iemsmaven.Model;

import com.raver.iemsmaven.Utilities.DatabaseUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActivityLog {

    // Properties for TableView binding
    private final StringProperty timestamp;
    private final StringProperty activityBy;
    private final StringProperty name;
    private final StringProperty activity;

    /**
     * Constructor for creating ActivityLog objects from database data.
     * @param timestamp The formatted date and time string.
     * @param activityBy The role of the user (e.g., "Admin").
     * @param name The name of the user.
     * @param activity The description of the activity.
     */
    public ActivityLog(String timestamp, String activityBy, String name, String activity) {
        this.timestamp = new SimpleStringProperty(timestamp);
        this.activityBy = new SimpleStringProperty(activityBy);
        this.name = new SimpleStringProperty(name);
        this.activity = new SimpleStringProperty(activity);
    }

    // --- Property Getters (for TableView) ---
    
    public StringProperty timestampProperty() {
        return timestamp;
    }

    public StringProperty activityByProperty() {
        return activityBy;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty activityProperty() {
        return activity;
    }
    
    // --- Standard Getters ---

    public String getTimestamp() {
        return timestamp.get();
    }

    public String getActivityBy() {
        return activityBy.get();
    }

    public String getName() {
        return name.get();
    }

    public String getActivity() {
        return activity.get();
    }

    // --- Database Methods ---

    /**
     * Saves a new activity log to the database.
     * @param activityBy The role of the user (e.g., "Admin").
     * @param userName The name of the logged-in user.
     * @param description A description of the action performed.
     */
    public static void logActivity(String activityBy, String userName, String description) {
        // We only insert these three because log_id is AUTO_INCREMENT 
        // and log_timestamp is DEFAULT CURRENT_TIMESTAMP
        String sql = "INSERT INTO activity_logs (activity_by, user_name, activity_description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, activityBy);
            pstmt.setString(2, userName);
            pstmt.setString(3, description);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error logging activity: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves all activity logs from the database, ordered by newest first.
     * @return An ObservableList of ActivityLog objects for the TableView.
     */
    public static ObservableList<ActivityLog> getAllLogs() {
        ObservableList<ActivityLog> logList = FXCollections.observableArrayList();
        // Order by timestamp DESC to show the newest logs first
        String sql = "SELECT log_timestamp, activity_by, user_name, activity_description FROM activity_logs ORDER BY log_timestamp DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Define a formatter for our timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

            while (rs.next()) {
                // Get the timestamp from the DB
                Timestamp ts = rs.getTimestamp("log_timestamp");
                String formattedTimestamp = "N/A";

                // Convert timestamp to a formatted string
                if (ts != null) {
                    LocalDateTime ldt = ts.toLocalDateTime();
                    formattedTimestamp = ldt.format(formatter);
                }

                String activityBy = rs.getString("activity_by");
                String userName = rs.getString("user_name");
                String description = rs.getString("activity_description");

                // Create a new ActivityLog object and add it to the list
                logList.add(new ActivityLog(formattedTimestamp, activityBy, userName, description));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching activity logs: " + e.getMessage());
            e.printStackTrace();
        }
        
        return logList;
    }
}