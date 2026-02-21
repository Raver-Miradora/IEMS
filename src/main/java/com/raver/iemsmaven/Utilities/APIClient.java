package com.raver.iemsmaven.Utilities;

import okhttp3.*;
import com.google.gson.Gson; // You need Gson for JSON parsing
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class APIClient {
    // REPLACE THIS WITH YOUR ACTUAL AZURE IP
    private static final String BASE_URL = "http://20.187.150.106/api/"; 
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    // Generic POST request helper
    private static String post(String endpoint, String jsonInput) throws IOException {
        RequestBody body = RequestBody.create(jsonInput, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // READ THE RESPONSE BODY FIRST
            String responseBody = response.body().string();

            // IF ERROR, PRINT IT TO CONSOLE SO WE CAN SEE IT
            if (!response.isSuccessful()) {
                System.err.println("❌ SERVER ERROR DETAILS: " + responseBody); 
                throw new IOException("Unexpected code " + response);
            }
            return responseBody;
        }
    }

    // Generic GET request helper
    private static String get(String endpoint) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + endpoint)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        }
    }

    // --- METHODS FOR YOUR APP ---

    // 1. Sync Fingerprints (Download all from Cloud)
    public static List<Map<String, Object>> getAllFingerprints() {
        try {
            String jsonResponse = get("get_fingerprints.php");
            // Parse JSON response to get the "data" array
            Map<String, Object> result = gson.fromJson(jsonResponse, new TypeToken<Map<String, Object>>(){}.getType());
            
            if ("success".equals(result.get("status"))) {
                return (List<Map<String, Object>>) result.get("data");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to sync fingerprints from cloud.");
        }
        return null;
    }

    // 2. Log Attendance (Upload Time In/Out)
    public static boolean logAttendance(int userId, String type) {
        try {
            // Create JSON payload
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            Map<String, String> data = Map.of(
                "user_id", String.valueOf(userId),
                "timestamp", timestamp,
                "type", type // "IN" or "OUT"
            );
            
            String jsonInput = gson.toJson(data);
            String response = post("log_attendance.php", jsonInput);
            
            System.out.println("API Response: " + response);
            return response.contains("success");
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Request failed (Store locally?)
        }
    }
    public static List<Map<String, Object>> getAllUsers() {
        Request request = new Request.Builder()
                .url(BASE_URL + "get_all_users.php")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonData = response.body().string();
                // Use Gson to parse JSON into a List of Maps
                return gson.fromJson(jsonData, new TypeToken<List<Map<String, Object>>>(){}.getType());
            }
        } catch (Exception e) {
            System.err.println("API Error (getAllUsers): " + e.getMessage());
        }
        return null;
    }
}