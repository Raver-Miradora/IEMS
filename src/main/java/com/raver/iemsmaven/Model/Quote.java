package com.raver.iemsmaven.Model;

import com.raver.iemsmaven.Utilities.DatabaseUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Quote {

    private int quote_id;
    private String quote_content;
    private int status;

    public Quote(int quote_id, String quote_content, int status) {
        this.quote_id = quote_id;
        this.quote_content = quote_content;
        this.status = status;
    }

    // Getters and Setters
    public int getQuote_id() {
        return quote_id;
    }
    public void setQuote_id(int quote_id) {
        this.quote_id = quote_id;
    }
    public String getQuote_content() {
        return quote_content;
    }
    public void setQuote_content(String quote_content) {
        this.quote_content = quote_content;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    
    // Display status as a string
    public String getStatusString() {
        return (status == 1) ? "Active" : "Inactive";
    }

    // --- DATABASE METHODS ---

    /**
     * Gets all quotes based on filters for the management table.
     */
    public static ObservableList<Quote> getQuotes(String searchTerm, String statusFilter) {
        ObservableList<Quote> quoteList = FXCollections.observableArrayList();
        
        // Base query
        StringBuilder sql = new StringBuilder("SELECT * FROM quotes WHERE 1=1 ");

        // Add status filter
        if ("Active".equals(statusFilter)) {
            sql.append("AND status = 1 ");
        } else if ("Inactive".equals(statusFilter)) {
            sql.append("AND status = 0 ");
        }
        // "All" does not add a status clause

        // Add search term filter
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append("AND quote_content LIKE ? ");
        }

        sql.append("ORDER BY quote_id DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                ps.setString(1, "%" + searchTerm.trim() + "%");
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                quoteList.add(new Quote(
                    rs.getInt("quote_id"),
                    rs.getString("quote_content"),
                    rs.getInt("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quoteList;
    }

    /**
     * Adds a new quote to the database.
     */
    public static void addQuote(String content) throws SQLException {
        String sql = "INSERT INTO quotes (quote_content, status) VALUES (?, 1)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.executeUpdate();
        }
    }

    /**
     * Updates an existing quote.
     */
    public static void updateQuote(int id, String content) throws SQLException {
        String sql = "UPDATE quotes SET quote_content = ? WHERE quote_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, content);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * Inverts the status of a quote (Active -> Inactive or Inactive -> Active).
     */
    public static void invertQuoteStatus(int id) throws SQLException {
        String sql = "UPDATE quotes SET status = 1 - status WHERE quote_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    /**
     * Gets one random active quote for the dashboard.
     */
    public static Quote getRandomActiveQuote() {
        Quote quote = null;
        // SQL to get one random row from the active quotes
        String sql = "SELECT * FROM quotes WHERE status = 1 ORDER BY RAND() LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                quote = new Quote(
                    rs.getInt("quote_id"),
                    rs.getString("quote_content"),
                    rs.getInt("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quote;
    }
}
