package com.raver.iemsmaven.Model;

import java.time.LocalDate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Child {
    private String name;
    private LocalDate birthDate;

    // Constructor
    public Child(String name, LocalDate birthDate) {
        this.name = name;
        this.birthDate = birthDate;
    }

    // Getters
    public String getName() {
        return name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    // Optional: Setters if needed later
    public void setName(String name) {
        this.name = name;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    // Optional: toString for debugging
    @Override
    public String toString() {
        return "Child{" +
               "name='" + name + '\'' +
               ", birthDate=" + birthDate +
               '}';
    }
    public static void saveChildren(Connection conn, int userId, List<Child> children) throws SQLException {
        // If the list is null or empty, there's nothing to do.
        if (children == null || children.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO children (user_id, child_name, child_dob) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Child child : children) {
                ps.setInt(1, userId);
                ps.setString(2, child.getName());
                
                if (child.getBirthDate() != null) {
                    ps.setDate(3, Date.valueOf(child.getBirthDate()));
                } else {
                    ps.setNull(3, java.sql.Types.DATE);
                }
                ps.addBatch(); // Add this insert operation to the batch
            }
            ps.executeBatch(); // Execute all insert operations at once
        }
    }
    public static void updateChildrenForUser(Connection conn, int userId, List<Child> children) throws SQLException {
        // 1. Delete existing children for this user
        String deleteSql = "DELETE FROM children WHERE user_id = ?";
        try (PreparedStatement psDelete = conn.prepareStatement(deleteSql)) {
            psDelete.setInt(1, userId);
            psDelete.executeUpdate();
        }

        // 2. Insert the new children (if any)
        // We can reuse the saveChildren method for this
        saveChildren(conn, userId, children);
    }
}