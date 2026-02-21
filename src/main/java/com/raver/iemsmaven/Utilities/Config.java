package com.raver.iemsmaven.Utilities;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties properties = new Properties();
    private static final String SECRET_KEY;
    private static final String DB_URL;
    private static final String DB_USER;
    private static final String DB_PASSWORD;

    // Static block to load the file as soon as the class is used
    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("CRITICAL ERROR: config.properties not found in classpath.");
                throw new RuntimeException("config.properties not found in classpath");
            }
            properties.load(input);
            SECRET_KEY = properties.getProperty("database.secret.key");
            DB_URL = properties.getProperty("database.url", "jdbc:mysql://localhost:3306/test19");
            DB_USER = properties.getProperty("database.user", "root");
            DB_PASSWORD = properties.getProperty("database.password", "");
            
            if (SECRET_KEY == null || SECRET_KEY.isEmpty()) {
                throw new RuntimeException("database.secret.key not found in config.properties");
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error loading config.properties", ex);
        }
    }

    /**
     * Gets the secret encryption key from the config.properties file.
     * @return The secret key as a String.
     */
    public static String getSecretKey() {
        return SECRET_KEY;
    }

    /**
     * Gets the database JDBC URL.
     * @return The database URL.
     */
    public static String getDbUrl() {
        return DB_URL;
    }

    /**
     * Gets the database username.
     * @return The database username.
     */
    public static String getDbUser() {
        return DB_USER;
    }

    /**
     * Gets the database password.
     * @return The database password.
     */
    public static String getDbPassword() {
        return DB_PASSWORD;
    }
}