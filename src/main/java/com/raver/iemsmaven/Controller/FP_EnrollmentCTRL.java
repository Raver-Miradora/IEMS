package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Fingerprint.EnrollmentThread;
import com.raver.iemsmaven.Fingerprint.Selection;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Utilities.ImageUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static com.raver.iemsmaven.Fingerprint.Prompt.promptLabel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Duration;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class FP_EnrollmentCTRL implements Initializable {

    @FXML
    private Label readerStatusLabel;
    @FXML
    private ImageView fingerprintImage;
    @FXML
    private Label enrollFingerprintLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private ImageView userImageView;
    private boolean isClosing = false;
    
    private int userIdToEnroll;
    private Stage stage; // *** NEW: Store the stage ***
    private Timeline countdownTimeline; // *** NEW: Countdown timer ***

    EnrollmentThread enrollmentThread;
    
    // *** FIX 1: Add this class-level variable to store the user ***
    private User userForEnrollment; 

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        promptLabel = enrollFingerprintLabel;
        
        setReaderStatusLabel();
        
        // *** NEW: Auto-close if no reader is connected ***
        if (!Selection.readerIsConnected()) {
            enrollFingerprintLabel.setText("No fingerprint reader connected.");
            enrollFingerprintLabel.setStyle("-fx-text-fill: red;");
            // Start a 3-second timer to close the window
            startAutoCloseTimer(3, "No reader");
        }

        enrollFingerprintLabel.textProperty().addListener((obs, oldText, newText) -> {
            if (!isClosing && newText != null && newText.toLowerCase().contains("enrollment complete")) {
                isClosing = true;
                
                // *** FIX 3: This logging code will now work ***
                try {
                    User currentAdmin = Session.getInstance().getLoggedInUser();
                    
                    // Use the new class variable 'userForEnrollment'
                    String employeeName = getFormattedName(this.userForEnrollment); 
                    String employeeAgencyId = this.userForEnrollment.getAgencyEmployeeNo(); 

                    // Create the correct log description
                    String logDescription = "Successfully enrolled new fingerprints for employee: " + employeeName + " (ID: " + employeeAgencyId + ")";

                    // Log the activity
                    ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), logDescription);
                } catch (Exception logEx) {
                    System.err.println("Failed to log 'Fingerprint Enrollment' activity: " + logEx.getMessage());
                }
                
                startAutoCloseTimer(5, "Enrollment complete");
            }
        });
    }

    // (This helper method is correct, no changes needed)
    private String getFormattedName(User user) {
        if (user == null) return "";
        if (user.getId() == 0) return user.getFullName(); // Handle "All Employees"

        String lastName = user.getLname();
        String firstName = user.getFname();
        String middleName = user.getMname();
        StringBuilder formattedName = new StringBuilder();

        if (lastName != null && !lastName.trim().isEmpty()) {
            formattedName.append(lastName.trim());
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (formattedName.length() > 0) formattedName.append(", ");
            formattedName.append(firstName.trim());
        }
        if (middleName != null && !middleName.trim().isEmpty()) {
            String middleInitial = middleName.trim().substring(0, 1).toUpperCase() + ".";
            formattedName.append(" ").append(middleInitial);
        }
        if (formattedName.length() == 0) {
            return "User " + user.getId();
        }
        return formattedName.toString();
    }
    
    private void setReaderStatusLabel(){
        String newText = "Reader Status:"; // Start with clean text
        if(Selection.readerIsConnected()){
            newText += " Connected";
            readerStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }else{
            newText += " Disconnected";
            readerStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
            
        readerStatusLabel.setText(newText);
    }
    
    public void setDataForEnrollment(User user, Stage stage, ADMIN_FingerprintsCTRL adminFingerprintsCTRL){
        // *** FIX 2: Save the incoming 'user' to our class variable ***
        this.userForEnrollment = user; 
        
        this.stage = stage; // *** NEW: Store the stage reference ***
        userIdToEnroll = user.getId();

        nameLabel.setText(user.getFullNameWithInitial());
        userImageView.setImage(ImageUtil.byteArrayToImage(user.getImage()));
        
        // Only start the thread if the reader is connected
        if(Selection.readerIsConnected()) {
            enrollmentThread = new EnrollmentThread(fingerprintImage, userIdToEnroll);
            enrollmentThread.start();
        }
        stage.setOnHiding(event -> {
            if (enrollmentThread != null) {
                enrollmentThread.stopEnrollmentThread();
            }
            if (countdownTimeline != null) {
                countdownTimeline.stop(); 
            }
            adminFingerprintsCTRL.loadUserTable();
            adminFingerprintsCTRL.loadUserDetails(user); // 'user' is still valid here
        });
    }

    private void startAutoCloseTimer(int seconds, String reason) {
        System.out.println("Starting " + seconds + "s auto-close timer. Reason: " + reason);
        
        // Stop any existing enrollment thread
        if (enrollmentThread != null) {
            enrollmentThread.stopEnrollmentThread();
        }

        // Stop any existing timer
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        final IntegerProperty i = new SimpleIntegerProperty(seconds);
        
        String originalMessage = enrollFingerprintLabel.getText();
        enrollFingerprintLabel.textProperty().unbind(); 
        enrollFingerprintLabel.textProperty().bind(
            new SimpleStringProperty("Closing in ").concat(i.asString()).concat("s... " + originalMessage)
        );

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            i.set(i.get() - 1);
            if (i.get() <= 0) {
                countdownTimeline.stop();
                if (stage != null) {
                    Platform.runLater(() -> stage.close());
                }
            }
        }));
        
        countdownTimeline.setCycleCount(seconds);
        countdownTimeline.play();
    }
}