package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Utilities.PaneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 */
public class ContainerPaneCTRL implements Initializable {

    @FXML private Button dashboardBtn;
    @FXML private Button logOutAdminBtn;
    @FXML private Pane contentPane;
    @FXML private Button fingerprintsBtn;
    @FXML private Button backupBtn; // *** RENAMED (was reportsBtn) ***
    
    // Main dropdown buttons
    @FXML private Button attendanceMainBtn;
    @FXML private Button holidaysMainBtn;
    @FXML private Button employeesMainBtn;
    @FXML private Button setupMainBtn;
    @FXML private Button dailyMainBtn; // *** NEW ***
    
    // Dropdown containers
    @FXML private VBox holidaysDropdown;
    @FXML private VBox employeesDropdown;
    @FXML private VBox setupDropdown;
    @FXML private VBox dailyDropdown; // *** NEW ***
    
    // Dropdown arrows
    @FXML private ImageView holidaysArrow;
    @FXML private ImageView employeesArrow;
    @FXML private ImageView setupArrow;
    
    // Subsection buttons
    @FXML private Button addHolidayBtn;
    @FXML private Button manageHolidayBtn;
    @FXML private Button addEmployeeBtn;
    @FXML private Button manageEmployeeBtn;
    // @FXML private Button assignmentBtn; // *** REMOVED ***
    @FXML private Button departmentBtn;
    @FXML private Button positionBtn;
    @FXML private Button shiftBtn;
    @FXML private Button timeoffBtn;
    @FXML private Button noticeBtn; // *** NEW ***
    @FXML private Button activityLogsBtn;
    @FXML private Button otAuthBtn;
    @FXML private VBox navButtonsContainer;
    
    // Attendance dropdown
    @FXML private VBox attendanceDropdown;
    @FXML private Button dailyAttendanceBtn;
    @FXML private Button attendanceReportBtn;

    private Object currentController;
    private AnchorPane view;
    private PaneUtil paneUtil = new PaneUtil();
    private ArrayList<Button> buttonList = new ArrayList<>();
    private ArrayList<Button> subSectionButtonList = new ArrayList<>(); // New list for sub-sections

    // Static reference to access the container controller
    private static ContainerPaneCTRL instance;
    
    public Object getCurrentController() {
        return currentController;
    }
    
    public ContainerPaneCTRL() {
        instance = this;
    }

    public static ContainerPaneCTRL getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String dashboardPath = paneUtil.ADMIN_DASHBOARD;

        // If user is records officer, hide certain containers
        if (Session.getInstance().getLoggedInUser().getPrivilege().equalsIgnoreCase("records officer")) {
            // Hide appropriate sections for records officer
            hideSection(attendanceMainBtn, attendanceDropdown);
            hideSection(setupMainBtn, setupDropdown);
            hideSection(employeesMainBtn, employeesDropdown);
            hideSection(dailyMainBtn, dailyDropdown); // *** NEW ***
            holidaysMainBtn.setVisible(false); // Hide holidays for records officer
            fingerprintsBtn.setVisible(false);
            backupBtn.setVisible(false); // *** MODIFIED (was reportsBtn) ***
            dashboardPath = paneUtil.RO_DASHBOARD;
        }

        // Load dashboard pane
        try {
            view = FXMLLoader.load(getClass().getResource(dashboardPath));
            contentPane.getChildren().setAll(view);
            highlightButton(dashboardBtn);
        } catch (IOException ex) {
            Logger.getLogger(ContainerPaneCTRL.class.getName()).log(Level.SEVERE, null, ex);
        }

        // List navigation buttons (include holidaysMainBtn as a direct button)
        Button[] buttonArray = {dashboardBtn, attendanceMainBtn, holidaysMainBtn, employeesMainBtn, dailyMainBtn, fingerprintsBtn, backupBtn}; // *** MODIFIED ***
        buttonList = new ArrayList<>(Arrays.asList(buttonArray));

        // Initialize sub-section buttons list (remove holiday sub-buttons)
        Button[] subSectionArray = {
            dailyAttendanceBtn, attendanceReportBtn,
            addEmployeeBtn, manageEmployeeBtn,  
            noticeBtn, activityLogsBtn,
            departmentBtn, positionBtn, shiftBtn, timeoffBtn,
            otAuthBtn
        };
        subSectionButtonList = new ArrayList<>(Arrays.asList(subSectionArray));

        // Initially collapse all dropdowns
        collapseAllDropdowns();
    }

    private void hideSection(Button mainButton, VBox dropdown) {
        mainButton.setVisible(false);
        mainButton.setManaged(false);
        dropdown.setVisible(false);
        dropdown.setManaged(false);
    }
    @FXML
    private void openOtAuth(ActionEvent event) {
        loadFXML(paneUtil.ADMIN_OT_AUTH_PANE);
        clearButtonHighlights();
        highlightSubSectionButton(otAuthBtn); // <--- Add this!
    }
    
    @FXML
    private void toggleAttendanceDropdown(ActionEvent event) {
        boolean isVisible = attendanceDropdown.isVisible();
        collapseAllDropdowns();
        if (!isVisible) {
            attendanceDropdown.setVisible(true);
            attendanceDropdown.setManaged(true);
        }
    }

    @FXML
    private void toggleEmployeesDropdown(ActionEvent event) {
        boolean isVisible = employeesDropdown.isVisible();
        collapseAllDropdowns();
        if (!isVisible) {
            employeesDropdown.setVisible(true);
            employeesDropdown.setManaged(true);
        } 
    }

    // *** NEW METHOD ***
    @FXML
    private void toggleDailyDropdown(ActionEvent event) {
        boolean isVisible = dailyDropdown.isVisible();
        collapseAllDropdowns();
        if (!isVisible) {
            dailyDropdown.setVisible(true);
            dailyDropdown.setManaged(true);
        } 
    }

    @FXML
    private void toggleSetupDropdown(ActionEvent event) {
        boolean isVisible = setupDropdown.isVisible();
        collapseAllDropdowns();
        if (!isVisible) {
            setupDropdown.setVisible(true);
            setupDropdown.setManaged(true);
        } 
    }

    

    private void collapseAllDropdowns() {
        attendanceDropdown.setVisible(false);
        attendanceDropdown.setManaged(false);
        employeesDropdown.setVisible(false);
        employeesDropdown.setManaged(false);
        dailyDropdown.setVisible(false); // *** NEW ***
        dailyDropdown.setManaged(false); // *** NEW ***
        setupDropdown.setVisible(false);
        setupDropdown.setManaged(false);
    }
    
   @FXML
    private void openAttendancePane(ActionEvent event) throws IOException {
        collapseAllDropdowns();
        loadFXML(paneUtil.ADMIN_ATTENDANCE);
        highlightButton(attendanceMainBtn);
    }
    @FXML
    private void openActivityLogsPane(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_ACTIVITY_LOGS);
        clearButtonHighlights();
        highlightSubSectionButton(activityLogsBtn);
    }

   @FXML
    private void openHolidaysPane(ActionEvent event) throws IOException {
        collapseAllDropdowns();
        loadFXML(paneUtil.ADMIN_EMP_CALENDAR_PANE);
        highlightButton(holidaysMainBtn);
    }

    // Employees Section
    @FXML
    private void openAddEmployee(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_ADD_EMP);
        clearButtonHighlights();
        highlightSubSectionButton(addEmployeeBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    @FXML
    private void openManageEmployee(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_EMP_MGMT);
        clearButtonHighlights();
        highlightSubSectionButton(manageEmployeeBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    // *** NEW: Daily Section ***
    @FXML
    private void openNoticePane(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_DAILY_NOTICE);
        clearButtonHighlights();
        highlightSubSectionButton(noticeBtn);
    }

    
    
    // Setup Section
    @FXML
    private void openTimeoffPane(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_EMP_TIMEOFF_PANE);
        clearButtonHighlights();
        highlightSubSectionButton(timeoffBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    // *** REMOVED openAssignmentsPane method ***

    @FXML
    private void openDepartmentsPane(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_DEPARTMENTS);
        clearButtonHighlights();
        highlightSubSectionButton(departmentBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    @FXML
    private void openPositionsPane(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_POSITIONS);
        clearButtonHighlights();
        highlightSubSectionButton(positionBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    @FXML
    private void openShiftsPane(ActionEvent event) throws IOException {
        loadFXML(paneUtil.ADMIN_SHIFTS);
        clearButtonHighlights();
        highlightSubSectionButton(shiftBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    // Existing navigation methods
    @FXML
    private void openDashboardPane(ActionEvent event) throws IOException {
        collapseAllDropdowns();
        loadDashboard();
    }
    @FXML
    private void openDailyAttendance(ActionEvent event) throws IOException {
        // For now, use existing attendance pane - replace with daily attendance specific pane when available
        loadFXML(paneUtil.ADMIN_ATTENDANCE);
        clearButtonHighlights();
        highlightSubSectionButton(dailyAttendanceBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    @FXML
    private void openAttendanceReport(ActionEvent event) throws IOException {
        // This button still works and loads the reports pane
        loadFXML(paneUtil.ADMIN_ATT_REPORTS);
        clearButtonHighlights();
        highlightSubSectionButton(attendanceReportBtn);
        // Don't collapse dropdown - this fixes the issue
    }

    @FXML
    private void openFingerprintsPane(ActionEvent event) throws IOException {
        collapseAllDropdowns();
        loadFXML(paneUtil.ADMIN_FINGERPRINTS);
        highlightButton(fingerprintsBtn);
    }

    // *** NEW METHOD (Replaced openReportsPane) ***
    @FXML
    private void openBackupPane(ActionEvent event) throws IOException {
        collapseAllDropdowns();
        loadFXML(paneUtil.ADMIN_SYSTEM_BACKUP); // Load the new backup pane
        highlightButton(backupBtn); // Highlight the new backup button
    }

    @FXML
    private void logOut(ActionEvent event) {
        Session.getInstance().clearSession();
        paneUtil.changeScene(logOutAdminBtn, paneUtil.LOGIN_PANE);
        Stage stage = (Stage) logOutAdminBtn.getScene().getWindow();
        stage.setMaximized(true);
    }

    // Existing utility methods
    public void loadAddEmployee() {
        loadFXML(paneUtil.ADMIN_ADD_EMP);
        clearButtonHighlights();
        highlightSubSectionButton(addEmployeeBtn);
    }
    
    public void setContent(Parent content) {
        contentPane.getChildren().setAll(content);
        clearButtonHighlights();
        clearSubSectionHighlights();
    }
    
    public void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();
            currentController = loader.getController();
            contentPane.getChildren().setAll(newView);
            clearButtonHighlights();
        } catch (IOException ex) {
            Logger.getLogger(ContainerPaneCTRL.class.getName()).log(Level.SEVERE, null, ex);
            currentController = null;
        }
    }

    public <T> T loadFXMLAndGetController(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();
            T controller = loader.getController();
            currentController = controller;
            contentPane.getChildren().setAll(newView);
            clearButtonHighlights();
            return controller;
        } catch (IOException ex) {
            Logger.getLogger(ContainerPaneCTRL.class.getName()).log(Level.SEVERE, null, ex);
            currentController = null;
            return null;
        }
    }

    public void loadProfileUpdate() {
        loadFXML("/View/ADMIN_ProfileUpdate.fxml");
    }

    public void loadDashboard() {
        loadFXML(paneUtil.ADMIN_DASHBOARD);
        highlightButton(dashboardBtn);
    }

    public Pane getContentPane() {
        return contentPane;
    }

   // Button styling methods
    private void setPressedStyle(Button button) {
        // Clear all possible style classes first
        button.getStyleClass().removeAll("navButton", "navButton-pressed", "subNavButton", "subNavButton-pressed");

        if (subSectionButtonList.contains(button)) {
            // It's a sub-button, apply the NEW pressed class
            button.getStyleClass().add("subNavButton-pressed");
        } else {
            // It's a main button, apply the existing pressed class
            button.getStyleClass().add("navButton-pressed");
        }
        
        button.setStyle("");
    }


   private void setDefaultStyle(Button button) {
        // *** MODIFIED: Added new class to the removal list ***
        button.getStyleClass().removeAll("navButton", "navButton-pressed", "subNavButton", "subNavButton-pressed");
       
        if (subSectionButtonList.contains(button)) {
            button.getStyleClass().add("subNavButton");
        } else {
            button.getStyleClass().add("navButton");
        }
        button.setStyle("");
    }
    private void highlightButton(Button selectedButton) {
        clearButtonHighlights();
        clearSubSectionHighlights();
        setPressedStyle(selectedButton);
    }
    private void highlightSubSectionButton(Button selectedSubButton) {
        // Clear all sub-section highlights first
        clearSubSectionHighlights();

        // Set the selected sub-section button to pressed state
        setPressedStyle(selectedSubButton);
    }
    private void clearSubSectionHighlights() {
        for (Button btn : subSectionButtonList) {
            setDefaultStyle(btn);
        }
    }

    public void clearButtonHighlights() {
        for (Button btn : buttonList) {
            setDefaultStyle(btn);
        }
    }
}