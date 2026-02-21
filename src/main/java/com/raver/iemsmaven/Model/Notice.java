package com.raver.iemsmaven.Model;

import com.raver.iemsmaven.Utilities.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Notice {

    private int notice_id;
    private String notice_title;
    private String notice_content;
    private int status;

    public Notice(int notice_id, String notice_title, String notice_content, int status) {
        this.notice_id = notice_id;
        this.notice_title = notice_title;
        this.notice_content = notice_content;
        this.status = status;
    }

    // Getters
    public int getNotice_id() { return notice_id; }
    public String getNotice_title() { return notice_title; }
    public String getNotice_content() { return notice_content; }
    public int getStatus() { return status; }
    
    public String getStatusString() {
        return (status == 1) ? "Active" : "Inactive";
    }

    // --- DATABASE METHODS ---

    /**
     * Gets all notices based on filters for the management table.
     */
    public static ObservableList<Notice> getNotices(String searchTerm, String statusFilter) {
        ObservableList<Notice> noticeList = FXCollections.observableArrayList();
        
        StringBuilder sql = new StringBuilder("SELECT * FROM notices WHERE 1=1 ");

        if ("Active".equals(statusFilter)) {
            sql.append("AND status = 1 ");
        } else if ("Inactive".equals(statusFilter)) {
            sql.append("AND status = 0 ");
        }

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND (notice_title LIKE ? OR notice_content LIKE ?) ");
        }

        sql.append("ORDER BY created_at DESC"); // Show newest first

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                String searchLike = "%" + searchTerm.trim() + "%";
                ps.setString(1, searchLike);
                ps.setString(2, searchLike);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                noticeList.add(new Notice(
                    rs.getInt("notice_id"),
                    rs.getString("notice_title"),
                    rs.getString("notice_content"),
                    rs.getInt("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return noticeList;
    }

    /**
     * Adds a new notice to the database.
     */
    public static void addNotice(String title, String content) throws SQLException {
        String sql = "INSERT INTO notices (notice_title, notice_content, status) VALUES (?, ?, 1)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.executeUpdate();
        }
    }

    /**
     * Updates an existing notice.
     */
    public static void updateNotice(int id, String title, String content) throws SQLException {
        String sql = "UPDATE notices SET notice_title = ?, notice_content = ? WHERE notice_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    /**
     * Inverts the status of a notice.
     */
    public static void invertNoticeStatus(int id) throws SQLException {
        String sql = "UPDATE notices SET status = 1 - status WHERE notice_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    /**
     * Gets all ACTIVE notices for the login screen.
     */
    public static ObservableList<Notice> getActiveNotices() {
        ObservableList<Notice> noticeList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM notices WHERE status = 1 ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                noticeList.add(new Notice(
                    rs.getInt("notice_id"),
                    rs.getString("notice_title"),
                    rs.getString("notice_content"),
                    rs.getInt("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return noticeList;
    }
}
