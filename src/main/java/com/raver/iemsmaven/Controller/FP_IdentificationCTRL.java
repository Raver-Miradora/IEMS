/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Fingerprint.IdentificationThread;
import com.raver.iemsmaven.Fingerprint.CaptureThread;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class FP_IdentificationCTRL implements Initializable {
    @FXML
    private ImageView fpIdentificationImage;

    private IdentificationThread identificationThread;
    private CaptureThread captureThread;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize the threads properly
        identificationThread = new IdentificationThread(fpIdentificationImage);
        captureThread = new CaptureThread("IdentificationCapture", fpIdentificationImage, identificationThread);
        
        // Start the threads
        startFingerprintIdentification();
    }
    
    private void startFingerprintIdentification() {
        try {
            // Check if reader is available before starting
            if (com.raver.iemsmaven.Fingerprint.Selection.readerIsConnected_noLogging()) {
                if (captureThread != null && !captureThread.isAlive()) {
                    captureThread.start();
                }
                if (identificationThread != null && !identificationThread.isAlive()) {
                    identificationThread.start();
                }
                System.out.println("Fingerprint identification started successfully");
            } else {
                System.err.println("Fingerprint reader not connected. Please check the device.");
            }
        } catch (Exception e) {
            System.err.println("Failed to start fingerprint identification: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Method to stop identification (call this when closing the window)
    public void stopFingerprintIdentification() {
        try {
            if (captureThread != null) {
                captureThread.stopThread();
            }
            if (identificationThread != null) {
                identificationThread.stopThread();
            }
            System.out.println("Fingerprint identification stopped");
        } catch (Exception e) {
            System.err.println("Error stopping fingerprint identification: " + e.getMessage());
        }
    }
    
    // Cleanup when controller is destroyed
    public void shutdown() {
        stopFingerprintIdentification();
    }
    
    // Optional: Add methods to restart identification if needed
    public void restartIdentification() {
        stopFingerprintIdentification();
        
        // Create new thread instances
        identificationThread = new IdentificationThread(fpIdentificationImage);
        captureThread = new CaptureThread("IdentificationCapture", fpIdentificationImage, identificationThread);
        
        startFingerprintIdentification();
    }
}