/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Utilities;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.IOException;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;

/**
 *
 * @author admin
 */
public class PaneUtil {
    public final String LOGIN_PANE = "/View/LoginPane.fxml";
    public final String CONTAINER_PANE = "/View/ContainerPane.fxml";
    public final String RO_PANE = "/View/RO_Pane.fxml";
    public final String RO_DASHBOARD = "/View/RO_Dashboard.fxml";
    public final String ADMIN_EMP_MGMT = "/View/ADMIN_EmpMgmt.fxml";
    public final String ADMIN_ADD_EMP = "/View/ADMIN_AddEmp.fxml";
    public final String ADMIN_ASSIGNMENTS = "/View/ADMIN_Assignments.fxml";
    public final String ADMIN_DEPARTMENTS = "/View/ADMIN_Departments.fxml";
    public final String ADMIN_POSITIONS = "/View/ADMIN_Positions.fxml";
    public final String ADMIN_SHIFTS= "/View/ADMIN_Shifts.fxml";
    public final String ADMIN_ATT_REPORTS = "/View/ADMIN_AttReports.fxml";
    public final String ADMIN_ATTENDANCE = "/View/ADMIN_Attendance.fxml";
    public final String ADMIN_DASHBOARD = "/View/ADMIN_Dashboard.fxml";
    public final String ADMIN_FINGERPRINTS = "/View/ADMIN_Fingerprints.fxml";
    public final String ADMIN_EMP_CALENDAR_PANE = "/View/ADMIN_UserCalendar.fxml";
    public final String ADMIN_EMP_TIMEOFF_PANE = "/View/ADMIN_Timeoff.fxml";
    public final String ADMIN_LOGIN_PAGE = "/View/AdminLoginPane.fxml";
    public final String ADMIN_PROFILE_UPDATE = "/View/ADMIN_ProfileUpdate.fxml";   
    public final String ADMIN_VIEW_EMP = "/View/ADMIN_ViewEmp.fxml";
    public final String ADMIN_EDIT_EMP = "/View/ADMIN_EditEmp.fxml"; 
    public final String ADMIN_DAILY_ATTENDANCE = "/View/ADMIN_DailyAttendance.fxml";
    public final String ADMIN_ATTENDANCE_REPORT = "/View/ADMIN_AttendanceReport.fxml";
    public final String ADMIN_HOLIDAY_MGMT = "/View/ADMIN_HolidayMgmt.fxml";
    public final String ADMIN_DAILY_NOTICE = "/View/ADMIN_DailyNotice.fxml";
    public final String ADMIN_DAILY_QUOTE = "/View/ADMIN_DailyQuote.fxml";
    public final String ADMIN_ACTIVITY_LOGS = "/View/ADMIN_ActivityLogs.fxml";
    public final String ADMIN_SYSTEM_BACKUP = "/View/ADMIN_SystemBackup.fxml";
    public final String ADMIN_OT_AUTH_PANE = "/View/ADMIN_OtAuthorization.fxml";
    
    
    //FINGERPRINT
    public final String FP_ENROLLMENT = "/View/FP_Enrollment.fxml";
    public final String FP_IDENTIFICATION = "/View/FP_Identification.fxml";
    public final String FP_IDENTIFICATION_SUCCESS = "/View/FP_IdentificationSuccess.fxml";
    public final String FP_IDENTIFICATION_FAIL = "/View/FP_IdentificationFail.fxml";
    public final String FP_CONFIRM_ACTION = "/View/FP_ConfirmAction.fxml";
    
    private static final double DESIGN_WIDTH = 1920.0;
    private static final double DESIGN_HEIGHT = 1080.0;
    
    public Parent scaleRootToScreen(Parent root) {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // 2. Calculate Scale Factor
        double scaleX = screenWidth / DESIGN_WIDTH;
        double scaleY = screenHeight / DESIGN_HEIGHT;
        double scaleFactor = Math.min(scaleX, scaleY);

        // 3. Apply Scale Transform
        Scale scale = new Scale(scaleFactor, scaleFactor);
        scale.setPivotX(0);
        scale.setPivotY(0);
        root.getTransforms().setAll(scale); 
        
        Group group = new Group(root);

        StackPane rootContainer = new StackPane(group);
        rootContainer.setStyle("-fx-background-color: black;"); 
        rootContainer.setPrefSize(screenWidth, screenHeight);

        return rootContainer;
    }
    public void changeScene(Button button, String fxmlPath) {
        try {
            Stage stage = (Stage) button.getScene().getWindow();
            Parent currentRoot = stage.getScene().getRoot();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Parent rawRoot = loader.load();

                    Parent scaledRoot = scaleRootToScreen(rawRoot);

                    scaledRoot.setOpacity(0);

                    Scene scene = new Scene(scaledRoot);
                    String stylesheet = AppContext.getStylesheet();
                    if (stylesheet != null) {
                        scene.getStylesheets().add(stylesheet);
                    }

                    stage.setScene(scene);
                    stage.setMaximized(true);

                    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), scaledRoot);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Add this method to PaneUtil
    public void showLoading(Stage stage) {
        StackPane loadingPane = new StackPane();
        loadingPane.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        ProgressIndicator progress = new ProgressIndicator();
        loadingPane.getChildren().add(progress);
        loadingPane.setVisible(false);

        // Add to stage's scene
        if (stage.getScene().getRoot() instanceof Parent) {
            ((Parent) stage.getScene().getRoot()).getChildrenUnmodifiable().add(loadingPane);
        }
    }
    public void openPane(String fxmlPath) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Create a new stage for the second window
            Stage secondStage = new Stage();
            secondStage.setScene(new Scene(root));
            secondStage.show();

            // Set the application icon
            secondStage.getIcons().add(new Image(getClass().getResourceAsStream("/Images/program_icon.png")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadPaneInContainer(Pane container, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            container.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
}
    public void openAttenPane(String fxmlPath){
         try {
            // Close the current window
            AnchorPane view = FXMLLoader.load(getClass().getResource(fxmlPath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void loadPaneInMainContainer(Pane container, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            container.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void openModal(String fxmlPath) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Create a new stage for the modal window
            Stage modalStage = new Stage();
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);

            // Set the application icon
            modalStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/Images/program_icon.png")));

            modalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Keep this method for closing windows
    public void exitPane(Button btnName) {
        // Close the current window
        Stage stage = (Stage) btnName.getScene().getWindow();
        stage.close();
    }
    
    
    
    public void openAndClosePane(String fxmlPath, int delayInMilliseconds) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Create a new stage for the window
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Close the window after the specified delay
            //Thread.sleep(delayInMilliseconds);
            //stage.close();
            
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(delayInMilliseconds), event -> stage.close())
            );
            timeline.play();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}

