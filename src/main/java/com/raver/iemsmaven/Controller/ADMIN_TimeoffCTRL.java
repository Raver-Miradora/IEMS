/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.Attendance;
import com.raver.iemsmaven.Model.Department;
import com.raver.iemsmaven.Model.Timeoff;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Utilities.DateUtil;
import com.raver.iemsmaven.Utilities.ImageUtil;
import com.raver.iemsmaven.Utilities.Modal;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import javafx.beans.property.SimpleStringProperty;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;
import static javafx.scene.input.KeyCode.S;
import javafx.util.StringConverter; 

/**
 *
 * @author User
 */
public class ADMIN_TimeoffCTRL implements Initializable{

    @FXML
    private TableColumn<Timeoff, String> calTypeCol,descCol, attachmentCol;
    @FXML
    private TableColumn<Timeoff, Date> endDCol, startDCol;

    @FXML
    private ChoiceBox<String> typeComBox;
    @FXML
    private TextField descField, attachmentField;
    @FXML
    private Button insertBtn, clearBtn, updateBtn,deactivateBtn;
    @FXML
    private TableView<Timeoff> specialCalTable;
    @FXML
    private Label empIdLabel, userOffIdLabel, timeOffIDLabel, userOffIdLabelTag;
    
    User selectedUser;
    Timeoff selectedUserTimeoff;

    @FXML
    private TableColumn<Timeoff, Integer> totalCol;
    @FXML
    private Label userNameLabel;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label manageUserLabel;
    @FXML
    private DateRangePicker dateRangePicker;

    @FXML private ComboBox<String> scheduleTypeCombo;
    @FXML
    private ComboBox<Department> departmentCombo;
    @FXML
    private TableView<User> employeeTable;
    @FXML
    private TableColumn<User, String> col_empId;
    @FXML
    private TableColumn<User, String> col_empName;

    private final Department ALL_DEPARTMENTS = new Department(0, "All Departments");

    private static final Set<String> NAME_SUFFIXES = Set.of(
        "jr", "jr.", "sr", "sr.", "ii", "iii", "iv", "v"
    );

    public void showTimeoffTable(int user_id){
        ObservableList<Timeoff> filteredData = FXCollections.observableArrayList();
        for (Timeoff timeoff : Timeoff.getTimeoffByUserId(user_id)){
            filteredData.add(timeoff);
        }
        specialCalTable.setItems(filteredData);
    }

    @FXML
    private void handleSelectBtn(MouseEvent event) {
        selectedUserTimeoff = specialCalTable.getSelectionModel().getSelectedItem();

        updateBtn.setDisable(false);
        deactivateBtn.setDisable(false);
        userOffIdLabelTag.setDisable(false);

        if (selectedUserTimeoff != null) {
            LocalDate startDate = DateUtil.sqlDateToLocalDate(selectedUserTimeoff.getStartDate());
            LocalDate endDate = DateUtil.sqlDateToLocalDate(selectedUserTimeoff.getEndDate());
            DateRange dateRange = new DateRange(startDate, endDate);
            dateRangePicker.setValue(dateRange);
            
            if (selectedUserTimeoff.getScheduleType() != null) {
                scheduleTypeCombo.setValue(selectedUserTimeoff.getScheduleType());
            } else {
                scheduleTypeCombo.setValue("Full Day");
            }
            scheduleTypeCombo.setDisable(true);
            
            typeComBox.setValue(selectedUserTimeoff.getType());
            descField.setText(selectedUserTimeoff.getDescription());
            attachmentField.setText(selectedUserTimeoff.getAttachment());
            timeOffIDLabel.setText(selectedUserTimeoff.getUserOffId()+"");

            insertBtn.setDisable(true);
            typeComBox.setDisable(true);
        } else {
            typeComBox.setValue("");
            descField.setText("");
            descField.setPromptText("Add description . . .");
        }
    }
    
    @FXML
    private void updateTimeoff(ActionEvent event) throws SQLException {
        int userTimeoffId = Integer.parseInt(timeOffIDLabel.getText());
        String desription = descField.getText();
        String attachment = attachmentField.getText();
        int userId = Integer.parseInt(empIdLabel.getText());
        int offID = selectedUserTimeoff.getOffId();

        Date startDate = Date.valueOf(dateRangePicker.getValue().getStartDate());
        Date endDate = Date.valueOf(dateRangePicker.getValue().getEndDate());
        String scheduleType = scheduleTypeCombo.getValue();
        if (scheduleType == null) {
            scheduleType = "Full Day";
        }

        boolean actionIsConfirmed = Modal.actionConfirmed("Update", "Do you want to proceed?", "This will update the selected timeoff record");
        if (actionIsConfirmed) {
            // Now scheduleType variable exists and can be passed
            Timeoff.updateTimeoff(userTimeoffId, userId, offID, desription, attachment, startDate, endDate, scheduleType);
            
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String employeeName = userNameLabel.getText();
                String leaveType = selectedUserTimeoff.getType(); 
                String logDescription = "Updated " + leaveType + " (ID: " + userTimeoffId + ") for employee: " + employeeName;
                
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), logDescription);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Update Timeoff' activity: " + logEx.getMessage());
            }
            showTimeoffTable(userId);
            clear();
        }
    }

    @FXML
    private void addTimeoff(ActionEvent event) {
        // Validate employee selection
        if (empIdLabel.getText() == null || empIdLabel.getText().trim().isEmpty() || empIdLabel.getText().equals("0")) {
            Modal.showModal("Error", "Please select an employee from the table.");
            return;
        }
        String selectedType = typeComBox.getValue();
        if (selectedType == null || selectedType.trim().isEmpty() || 
            selectedType.equals("On Leave") || userOffIdLabel.getText().isEmpty()) {
            Modal.showModal("Error", "Please select a timeoff type from the dropdown.");
            return;
        }

        // Check if date range is selected
        if (dateRangePicker.getValue() == null) {
            Modal.showModal("Error", "Please select a date range.");
            return;
        }

        Date startDate = Date.valueOf(dateRangePicker.getValue().getStartDate());
        Date endDate = Date.valueOf(dateRangePicker.getValue().getEndDate());

        String description = descField.getText();
        String attachment = attachmentField.getText();
        String scheduleType = scheduleTypeCombo.getValue();
        if (scheduleType == null) scheduleType = "Full Day";
        try {
            int userId = Integer.parseInt(empIdLabel.getText());
            int offID = Integer.parseInt(userOffIdLabel.getText());

            Timeoff.addTimeoff(userId, offID, description, attachment, startDate, endDate, scheduleType);
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String employeeName = userNameLabel.getText(); // Get employee name from label
                String leaveType = typeComBox.getValue();
                String logDescription = "Added " + leaveType + " for employee: " + employeeName + " (" + startDate.toString() + " to " + endDate.toString() + ")";
                
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), logDescription);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Add Timeoff' activity: " + logEx.getMessage());
            }
            Modal.showModal("Success", "Timeoff Added Successfully");
            showTimeoffTable(userId);
            clear();
        } catch(NumberFormatException ex) {
            Modal.showModal("Error", "Invalid input. Please check your selections.");
            ex.printStackTrace();
        } catch(SQLException ex) {
            Modal.showModal("Failed", "Database Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void deactivateTimeoff(ActionEvent event) {
        // Check if timeOffIDLabel has valid content
        if (timeOffIDLabel.getText() == null || timeOffIDLabel.getText().trim().isEmpty()) {
            Modal.showModal("Error", "No timeoff record selected.");
            return;
        }

        try {
            int userId = Integer.parseInt(empIdLabel.getText());
            int offID = Integer.parseInt(timeOffIDLabel.getText());

            boolean actionIsConfirmed = Modal.actionConfirmed("Deactivate", "Do you want to proceed?", "This will deactivate the selected assignment record");
            if(actionIsConfirmed){
                Timeoff.deactivateTimeoff(offID);
                try {
                    User currentAdmin = Session.getInstance().getLoggedInUser();
                    String employeeName = userNameLabel.getText();
                    String leaveType = selectedUserTimeoff.getType();
                    String logDescription = "Deactivated " + leaveType + " (ID: " + offID + ") for employee: " + employeeName;
                    
                    ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), logDescription);
                } catch (Exception logEx) {
                    System.err.println("Failed to log 'Deactivate Timeoff' activity: " + logEx.getMessage());
                }
                showTimeoffTable(userId);
                clear();
            }
        } catch(NumberFormatException ex) {
            Modal.showModal("Error", "Invalid timeoff ID.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userImageView.setVisible(false);
        userNameLabel.setVisible(false);
        manageUserLabel.setVisible(false);

        setupFilterControls();
        setupEmployeeTable();
        loadEmployeeTable();

        ObservableList<String> timeoffList = FXCollections.observableArrayList(
            "Vacation Leave",
            "Sick Leave",
            "Maternity Leave",
            "Paternity Leave",
            "Solo Parent Leave",
            "Rehabilitation Leave",
            "Study Leave",
            "Forced Leave",
            "Special Privilege Leave",
            "Terminal Leave",
            "Leave Without Pay",
            "Official Business"
        );
        ObservableList<String> schedules = FXCollections.observableArrayList(
            "Full Day", "AM Only", "PM Only"
        );
        scheduleTypeCombo.setItems(schedules);
        scheduleTypeCombo.setValue("Full Day");
        typeComBox.setItems(timeoffList);

        calTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        attachmentCol.setCellValueFactory(new PropertyValueFactory<>("attachment"));
        startDCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        attachmentField.setPromptText("Enter Reference No. / File Location");

        specialCalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                String attachmentValue = newSelection.getAttachment();
                if (attachmentValue == null || attachmentValue.isEmpty()) {
                    attachmentField.setPromptText("Enter Reference No. / File Location");
                }
            }
        });

        typeComBox.setOnAction(event -> {
            if (typeComBox.getValue() != null) {
                String selectedValue = typeComBox.getValue();

                switch(selectedValue) {
                    case "Vacation Leave":
                        userOffIdLabel.setText("1");
                        break;
                    case "Sick Leave":
                        userOffIdLabel.setText("2");
                        break;
                    case "Maternity Leave":
                        userOffIdLabel.setText("3");
                        break;
                    case "Paternity Leave":
                        userOffIdLabel.setText("4");
                        break;
                    case "Solo Parent Leave":
                        userOffIdLabel.setText("5"); 
                        break;
                    case "Rehabilitation Leave":
                        userOffIdLabel.setText("6"); 
                        break;
                    case "Study Leave":
                        userOffIdLabel.setText("7"); 
                        break;
                    case "Forced Leave":
                        userOffIdLabel.setText("8"); 
                        break;
                    case "Special Privilege Leave":
                        userOffIdLabel.setText("9"); 
                        break;
                    case "Terminal Leave":
                        userOffIdLabel.setText("10"); 
                        break;
                    case "Leave Without Pay":
                        userOffIdLabel.setText("11"); 
                        break;
                    case "Official Business":
                        userOffIdLabel.setText("12");
                        break;
                    default:
                        userOffIdLabel.setText("");
                        break;
                }
            } else {
                userOffIdLabel.setText("");
            }
        });

        updateBtn.setDisable(true);
        deactivateBtn.setDisable(true);
        userOffIdLabelTag.setDisable(true);
    }
    private void setupFilterControls() {
        // --- Department ComboBox ---
        departmentCombo.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department dept) {
                return (dept == null || dept.getDepartmentName() == null) ? "" : dept.getDepartmentName();
            }
            @Override
            public Department fromString(String string) { return null; }
        });
        departmentCombo.getItems().add(ALL_DEPARTMENTS);
        departmentCombo.getItems().addAll(Department.getActiveDepartments());
        departmentCombo.setValue(ALL_DEPARTMENTS);
        
        // Add listener to repopulate employees when department changes
        departmentCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadEmployeeTable();
        });
    }

    /**
     * Configures the new Employee TableView.
     */
    private void setupEmployeeTable() {
        col_empId.setCellValueFactory(new PropertyValueFactory<>("agencyEmployeeNo"));
        col_empId.setStyle("-fx-alignment: CENTER;");
        
        col_empName.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String display = u == null ? "" : getFormattedName(u);
            return new SimpleStringProperty(display);
        });

        // Add listener to handle employee selection
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // This logic replaces the old selectEmployeeFromTree method
                User employee = newSelection;
                showTimeoffTable(employee.getId());
                selectedUser = User.getUserByUserId(employee.getId());
                clear();

                empIdLabel.setText(String.valueOf(employee.getId()));
                userOffIdLabel.setText("");
                deactivateBtn.setDisable(true);
                updateBtn.setDisable(true);
                userOffIdLabelTag.setDisable(true);
                
                typeComBox.setValue(null);

                userNameLabel.setText(getFormattedName(selectedUser));
                if (selectedUser.getImage() != null) {
                    userImageView.setImage(ImageUtil.byteArrayToImage(selectedUser.getImage()));
                }

                manageUserLabel.setVisible(true);
                userImageView.setVisible(true);
                userNameLabel.setVisible(true);

                specialCalTable.refresh();
            }
        });
    }

    /**
     * Loads/reloads the employee table based on the department filter.
     */
    public void loadEmployeeTable() {
        Department selectedDept = departmentCombo.getValue();
        ObservableList<User> usersToDisplay = FXCollections.observableArrayList();
        
        ObservableList<User> allUsers = User.getActiveEmployeesWithDepartmentAndPosition();

        for (User user : allUsers) {
            // Check if user matches selected department
            if (selectedDept == null || selectedDept.getId() == 0) {
                usersToDisplay.add(user); // Add all if "All Departments"
            } else if (user.getDepartment() != null && user.getDepartment().equals(selectedDept.getDepartmentName())) {
                usersToDisplay.add(user); // Add if department matches
            }
        }
        
        // Sort employees alphabetically by formatted name
        FXCollections.sort(usersToDisplay, (u1, u2) -> {
            return getFormattedName(u1).compareToIgnoreCase(getFormattedName(u2));
        });

        employeeTable.setItems(usersToDisplay);
    }

    // --- Removed initializeTreeViews(), setupTreeViewSelection(), getEmployeeIdFromName() ---
// END MODIFICATION

    // Helper to get formatted name (Last, First M.)
    private String getFormattedName(User user) {
        if (user == null) return "";

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

    /**
     * Removes common suffixes from a name part (like Jr., Sr., II, III, etc.)
     */
    private String removeSuffixes(String name) {
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

    private String formatDisplayName(String rawName) {
        if (rawName == null || rawName.trim().isEmpty()) return "";
        rawName = rawName.trim();

        if (rawName.contains(",")) {
            return rawName;
        }

        rawName = removeSuffixes(rawName);

        String[] parts = rawName.split("\\s+");
        if (parts.length == 1) return parts[0];

        if (parts.length >= 2) {
            String last = parts[parts.length - 1];
            StringBuilder first = new StringBuilder();
            for (int i = 0; i < parts.length - 1; i++) {
                if (i > 0) first.append(" ");
                first.append(parts[i]);
            }
            return last + ", " + first.toString();
        }

        return rawName;
    }
    
    @FXML
    public void clearManage(ActionEvent event){
        clear();
    }

    public void clear(){
        typeComBox.setValue(null);
        attachmentField.setText("");
        descField.setText("");
        userOffIdLabel.setText("");
        timeOffIDLabel.setText("");
        descField.setPromptText("Add description . . .");
        attachmentField.setPromptText("Add Link . . .");
        insertBtn.setDisable(false);
        typeComBox.setDisable(false);
        updateBtn.setDisable(true); // Keep disabled until selection
        deactivateBtn.setDisable(true); // Keep disabled until selection
        userOffIdLabelTag.setDisable(true);

        // Clear date range
        dateRangePicker.setValue(null);
    }
    
}