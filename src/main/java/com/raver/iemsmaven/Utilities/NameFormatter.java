package com.raver.iemsmaven.Utilities;

import com.raver.iemsmaven.Model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NameFormatter {
    
    private static final Set<String> NAME_SUFFIXES = Set.of(
        "jr", "jr.", "sr", "sr.", "ii", "iii", "iv", "v"
    );

    public static String getFormattedName(User user) {
        if (user == null) return "";

        String lastName = user.getLname();
        String firstName = user.getFname();
        String middleName = user.getMname();
        String suffix = user.getSuffix();

        StringBuilder formattedName = new StringBuilder();

        // Handle last name
        if (lastName != null && !lastName.trim().isEmpty()) {
            lastName = removeSuffixes(lastName.trim());
            formattedName.append(lastName);
        }

        // Handle first name
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (formattedName.length() > 0) {
                formattedName.append(", ");
            }
            formattedName.append(firstName.trim());
        }

        // Handle middle initial
        if (middleName != null && !middleName.trim().isEmpty()) {
            String middleInitial = middleName.trim().substring(0, 1).toUpperCase() + ".";
            if (formattedName.length() > 0 && firstName != null && !firstName.trim().isEmpty()) {
                formattedName.append(" ").append(middleInitial);
            }
        }

        // Handle suffix - add at the end
        if (suffix != null && !suffix.trim().isEmpty()) {
            String cleanSuffix = suffix.trim();
            if (formattedName.length() > 0) {
                formattedName.append(" ").append(cleanSuffix);
            }
        }

        // Fallback
        if (formattedName.length() == 0) {
            if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) {
                return formatDisplayName(user.getFullName());
            } else {
                return "User " + user.getId();
            }
        }

        return formattedName.toString();
    }

    private static String removeSuffixes(String name) {
        if (name == null || name.trim().isEmpty()) return name;

        String[] parts = name.split("\\s+");
        List<String> cleanParts = new ArrayList<>();

        for (String part : parts) {
            String cleanPart = part.replaceAll("[.,]", "").toLowerCase();
            if (!NAME_SUFFIXES.contains(cleanPart)) {
                cleanParts.add(part);
            }
        }

        return String.join(" ", cleanParts);
    }

    private static String formatDisplayName(String rawName) {
        if (rawName == null || rawName.trim().isEmpty()) return "";
        rawName = rawName.trim();

        if (rawName.contains(",")) return rawName;
        
        String[] parts = rawName.split("\\s+");
        if (parts.length == 1) return parts[0];
        
        String last = parts[parts.length - 1];
        StringBuilder first = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) first.append(" ");
            first.append(parts[i]);
        }
        return last + ", " + first.toString();
    }
}