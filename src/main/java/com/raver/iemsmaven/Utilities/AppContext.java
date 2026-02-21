package com.raver.iemsmaven.Utilities;

public class AppContext {
    private static String stylesheet;
    
    public static void setStylesheet(String stylesheetPath) {
        stylesheet = stylesheetPath;
    }
    
    public static String getStylesheet() {
        return stylesheet;
    }
}