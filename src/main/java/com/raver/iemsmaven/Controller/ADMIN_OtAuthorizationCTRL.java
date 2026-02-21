package com.raver.iemsmaven.Controller;

import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import com.raver.iemsmaven.Model.Attendance;
import com.raver.iemsmaven.Model.Department;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Utilities.ImageUtil;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ADMIN_OtAuthorizationCTRL implements Initializable {

    // --- LEFT PANE (Employee List) ---
    @FXML private ComboBox<Department> departmentCombo;
    @FXML private TableView<User> employeeTable;
    @FXML private TableColumn<User, String> col_empId;
    @FXML private TableColumn<User, String> col_empName;

    // --- RIGHT PANE (Details) ---
    @FXML private Label userNameLabel;
    @FXML private ImageView userImageView;
    @FXML private TableView<Attendance> otHistoryTable;
    @FXML private TableColumn<Attendance, String> col_authDate;
    @FXML private TableColumn<Attendance, String> col_status;
    
    // Components
    @FXML private DateRangePicker dateRangePicker; 
    @FXML private CheckBox showHistoryBox; // <--- Linked to FXML
    @FXML private Button authorizeBtn;
    @FXML private Button revokeBtn;

    private ObservableList<User> masterEmployeeList;
    private User selectedUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupLeftPane();
        setupRightPane();
        loadEmployees();
    }

    private void setupLeftPane() {
        // 1. Setup Table Columns
        col_empId.setCellValueFactory(new PropertyValueFactory<>("agencyEmployeeNo")); 
        col_empName.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        // 2. Setup Department Filter
        ObservableList<Department> depts = Department.getActiveDepartments();
        depts.add(0, new Department(0, "All Departments"));
        departmentCombo.setItems(depts);
        departmentCombo.getSelectionModel().selectFirst();
        
        departmentCombo.setOnAction(e -> filterEmployees());

        // 3. Table Selection Listener
        employeeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectEmployee(newVal);
            }
        });
    }

    private void setupRightPane() {
        col_authDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("attendance_status"));
        
        dateRangePicker.setValue(null); 
        
        // Enable Multiple Selection for Batch Revoke
        otHistoryTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        // Initial State
        authorizeBtn.setDisable(true);
        revokeBtn.setDisable(true);
    }

    private void loadEmployees() {
        masterEmployeeList = User.getActiveEmployeesWithDepartmentAndPosition(); 
        employeeTable.setItems(masterEmployeeList);
    }

    private void filterEmployees() {
        Department selectedDept = departmentCombo.getSelectionModel().getSelectedItem();
        if (selectedDept == null || masterEmployeeList == null) return;

        if (selectedDept.getId() == 0) {
            employeeTable.setItems(masterEmployeeList);
        } else {
            FilteredList<User> filtered = new FilteredList<>(masterEmployeeList, u -> 
                u.getDepartment() != null && u.getDepartment().equals(selectedDept.getDepartmentName())
            );
            employeeTable.setItems(filtered);
        }
    }

    private void selectEmployee(User user) {
        this.selectedUser = user;
        
        // Update UI
        userNameLabel.setText(user.getFullName());
        // Load Image safely
        if(user.getImage() != null) {
            userImageView.setImage(ImageUtil.byteArrayToImage(user.getImage()));
        } else {
            userImageView.setImage(new Image(getClass().getResourceAsStream("/Images/default_user_img.jpg")));
        }

        // Enable buttons
        authorizeBtn.setDisable(false);
        
        // Load History
        loadOtHistory();
    }

    /**
     * Loads the OT history for the selected user.
     * Annotated with @FXML so the CheckBox can trigger it.
     */
    @FXML 
    private void loadOtHistory() {
        if(selectedUser != null) {
            ObservableList<Attendance> history = Attendance.getOtAuthorizationsByUser(selectedUser.getId());
            
            // --- FILTER LOGIC ---
            FilteredList<Attendance> filteredData = new FilteredList<>(history, p -> true);
            
            filteredData.setPredicate(att -> {
                // If "Show Past Dates" is checked, show everything
                if (showHistoryBox.isSelected()) {
                    return true;
                }
                
                // Otherwise, only show dates that are TODAY or FUTURE
                if (att.getDate() != null) {
                    LocalDate authDate = new java.sql.Date(att.getDate().getTime()).toLocalDate();
                    // Keep if date is NOT before today (so today + future)
                    return !authDate.isBefore(LocalDate.now()); 
                }
                return false;
            });

            otHistoryTable.setItems(filteredData);
        }
    }

    @FXML
    private void addAuthorization(ActionEvent event) {
        // Validation
        if (selectedUser == null) return;
        DateRange selectedRange = dateRangePicker.getValue();
        if (selectedRange == null || selectedRange.getStartDate().equals(LocalDate.MAX)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Date", "Please select a valid date range.");
            return;
        }

        LocalDate startDate = selectedRange.getStartDate();
        LocalDate endDate = selectedRange.getEndDate();
        
        int successCount = 0;

        // Loop through the date range
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            boolean success = Attendance.addOtAuthorization(selectedUser.getId(), date);
            if (success) {
                successCount++;
            }
        }

        loadOtHistory(); // Refresh table
        
        if (successCount > 0) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                "Successfully authorized Overtime for " + successCount + " day(s).");
        } else {
            showAlert(Alert.AlertType.WARNING, "Notice", 
                "No new authorizations added (dates may already be authorized).");
        }
    }

    @FXML
    private void revokeAuthorization(ActionEvent event) {
        ObservableList<Attendance> selectedItems = otHistoryTable.getSelectionModel().getSelectedItems();
        
        if (selectedItems == null || selectedItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Selection Needed", "Please select row(s) to revoke.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Revoke");
        confirm.setHeaderText("Revoking " + selectedItems.size() + " authorization(s)");
        confirm.setContentText("Are you sure you want to remove these overtime permissions?");
        
        if (confirm.showAndWait().get() == ButtonType.OK) {
            // Loop through and delete all selected
            for (Attendance item : selectedItems) {
                Attendance.deleteOtAuthorization(item.getId());
            }
            loadOtHistory(); // Refresh table
        }
    }
    
    @FXML
    private void handleTableClick() {
        revokeBtn.setDisable(otHistoryTable.getSelectionModel().getSelectedItem() == null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}