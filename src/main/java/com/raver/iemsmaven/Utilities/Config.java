package com.raver.iemsmaven.Utilities;

import java.io.InputStream;
import java.util.Properties;

public class Config {

    private static final Properties properties = new Properties();
    private static final String SECRET_KEY;

    // Static block to load the file as soon as the class is used
    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.err.println("CRITICAL ERROR: config.properties not found in classpath.");
                throw new RuntimeException("config.properties not found in classpath");
            }
            properties.load(input);
            SECRET_KEY = properties.getProperty("database.secret.key");
            
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
}