/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.*;
import com.raver.iemsmaven.Model.Department; // <-- NEW IMPORT
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Utilities.DatabaseUtil;
import com.raver.iemsmaven.Utilities.Modal;
import com.raver.iemsmaven.Utilities.PaneUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Model.Assignment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.time.LocalDate;
import javafx.stage.FileChooser;
import javafx.util.StringConverter; // <-- NEW IMPORT

/**
 * FXML Controller class
 *
 * @author admin
 */
public class ADMIN_EmpMgmtCTRL implements Initializable {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> col_user_id; // <-- MODIFIED (was Integer)
    @FXML
    private TableColumn<User, String> col_name;
    @FXML
    private TableColumn<User, String> col_department;
    @FXML
    private TableColumn<User, String> col_position;
    @FXML
    private TableColumn<User, String> col_shift;
    @FXML
    private TableColumn<User, String> col_status;
    @FXML
    private TableColumn<User, String> col_actions;
    

    @FXML
    private Button addEmpBtn;
    @FXML
    private TextField searchField;
    
    // --- NEWLY ADDED ---
    @FXML
    private ComboBox<Department> departmentCombo;
    
    // --- NEW DUMMY OBJECT ---
    private final Department ALL_DEPARTMENTS = new Department(0, "All Departments");
    // ----------------------

    PaneUtil paneUtil = new PaneUtil();
    User selectedUser;

    // Common suffixes to remove from names
    private static final Set<String> NAME_SUFFIXES = Set.of(
        "jr", "jr.", "sr", "sr.", "ii", "iii", "iv", "v"
    );

    private ObservableList<User> allUsers = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        setupFilterControls(); // <-- MODIFIED (was setupSearchField)
        loadUserTable();
    }

    private void setupTableColumns() {
        // ID column - NOW USES agencyEmployeeNo
        col_user_id.setCellValueFactory(new PropertyValueFactory<>("agencyEmployeeNo"));

        // Name column with formatted name (Lastname, Firstname)
        col_name.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String display = u == null ? "" : getFormattedName(u);
            return new SimpleStringProperty(display);
        });

        // Department column
        col_department.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String dept = u == null ? "" : (u.getDepartment() != null ? u.getDepartment() : "Not Assigned");
            return new SimpleStringProperty(dept);
        });

        // Position column
        col_position.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String position = u == null ? "" : (u.getPosition() != null ? u.getPosition() : "Not Assigned");
            return new SimpleStringProperty(position);
        });

        // Shift column
        col_shift.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String shift = u == null ? "" : getEmployeeShift(u.getId());
            return new SimpleStringProperty(shift);
        });

        // Status column with plain text (no button)
        col_status.setCellFactory(column -> new TableCell<User, String>() {
            private final Label statusLabel = new Label();

            {
                statusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        if (user.isActive()) {
                            statusLabel.setText("Active");
                        } else {
                            statusLabel.setText("Archived");
                        }
                        setGraphic(statusLabel);
                    }
                }
            }
        });

        col_status.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String status = u == null ? "" : (u.isActive() ? "Active" : "Archived");
            return new SimpleStringProperty(status);
        });

        // Actions column with buttons
        col_actions.setCellFactory(column -> new TableCell<User, String>() {
            private final HBox buttonContainer = new HBox(5);
            private final Button viewButton = new Button("View Details");
            private final Button editButton = new Button("Edit");
            private final Button archiveButton = new Button("Archive");

            {
                buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);

                // Style the buttons
                viewButton.setStyle("-fx-font-size: 11px; -fx-pref-height: 25; -fx-pref-width: 90;");
                editButton.setStyle("-fx-font-size: 11px; -fx-pref-height: 25; -fx-pref-width: 60; -fx-background-color: #3498db; -fx-text-fill: white;");
                archiveButton.setStyle("-fx-font-size: 11px; -fx-pref-height: 25; -fx-pref-width: 70; -fx-background-color: #e74c3c; -fx-text-fill: white;");

                viewButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        showEmployeeDetails(user);
                    }
                });

                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        openEditUser(user);
                    }
                });

                archiveButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        archiveEmployee(user);
                    }
                });

                buttonContainer.getChildren().addAll(viewButton, editButton, archiveButton);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user != null) {
                        // Show archive button only for active users, show activate button for archived users
                        if (user.isActive()) {
                            archiveButton.setText("Archive");
                            archiveButton.setStyle("-fx-font-size: 11px; -fx-pref-height: 25; -fx-pref-width: 70; -fx-background-color: #e74c3c; -fx-text-fill: white;");
                        } else {
                            archiveButton.setText("Activate");
                            archiveButton.setStyle("-fx-font-size: 11px; -fx-pref-height: 25; -fx-pref-width: 70; -fx-background-color: #27ae60; -fx-text-fill: white;");
                        }
                        setGraphic(buttonContainer);
                    }
                }
            }
        });

        col_actions.setCellValueFactory(cellData -> new SimpleStringProperty(""));
    }
    
    private void archiveEmployee(User user) {
        String action = user.isActive() ? "Archive" : "Activate";
        String confirmMessage = user.isActive()
            ? "Are you sure you want to archive this employee? Archived employees will no longer be able to access the system because it is only accessible to admins and employees do not have system accounts."
            : "Are you sure you want to activate this employee? Activated employees will be able to access the system again.";

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle(action + " Employee");
        confirmation.setHeaderText(action + " Employee");
        confirmation.setContentText(confirmMessage);

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Flip status in DB
                User.invertUserStatus(user.getId());

                // Refresh the table (reload from DB)
                loadUserTable();
                try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String employeeName = getFormattedName(user); // Use your existing helper method

                String logDescription = action + "d employee: " + employeeName + " (ID: " + user.getAgencyEmployeeNo() + ")";
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), logDescription);
                } catch (Exception logEx) {

                    System.err.println("Failed to log 'Archive/Activate' activity: " + logEx.getMessage());
                }

                // Retrieve updated user (to show correct status in message)
                User updated = User.getUserByUserId(user.getId());
                boolean nowActive = (updated != null) ? updated.isActive() : !user.isActive(); // fallback

                // Show success message
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Success");
                success.setHeaderText("Employee " + (nowActive ? "Activated" : "Archived"));
                success.setContentText("Employee has been successfully " + (nowActive ? "activated" : "archived") + ".");
                success.showAndWait();

            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(action + " Failed");
                alert.setContentText("Failed to " + action.toLowerCase() + " employee: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    // --- MODIFIED: This now sets up BOTH filters ---
    private void setupFilterControls() {
        // --- Department ComboBox Setup ---
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
        
        // --- Add listeners to both controls ---
        departmentCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters(); // Reload table when department changes
        });
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });
    }

    public void loadUserTable() {
        allUsers = User.getAllEmployeesWithDepartmentAndPosition();
        applyFilters();
    }

    // --- MODIFIED: Now filters by department AND search, and sorts ---
    private void applyFilters() {
        ObservableList<User> filteredUsers = FXCollections.observableArrayList();
        
        // Get filter values
        Department selectedDept = departmentCombo.getValue();
        String searchText = searchField.getText().toLowerCase();
        
        for (User user : allUsers) {
            
            // 1. Check department filter
            boolean deptMatch = (selectedDept == null || selectedDept.getId() == 0 ||
                                 (user.getDepartment() != null && user.getDepartment().equals(selectedDept.getDepartmentName())));
            
            // 2. Check search filter
            boolean searchMatch = (searchText.isEmpty() || 
                getFormattedName(user).toLowerCase().contains(searchText) ||
                (user.getAgencyEmployeeNo() != null && user.getAgencyEmployeeNo().toLowerCase().contains(searchText)) ||
                (user.getDepartment() != null && user.getDepartment().toLowerCase().contains(searchText)) ||
                (user.getPosition() != null && user.getPosition().toLowerCase().contains(searchText)) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText)));
            
            // 3. Add if both match
            if (deptMatch && searchMatch) {
                filteredUsers.add(user);
            }
        }

        // --- NEW: Sort the filtered list by AgencyEmployeeNo ---
        filteredUsers.sort((u1, u2) -> {
            String id1 = u1.getAgencyEmployeeNo();
            String id2 = u2.getAgencyEmployeeNo();
            
            // Handle nulls to avoid errors
            if (id1 == null) id1 = "";
            if (id2 == null) id2 = "";
            
            // This is a standard string sort. "LGU-L00002" will come after "LGU-L00001"
            return id1.compareTo(id2);
        });
        // --- END OF SORTING ---

        // Set the filtered and sorted list to the table
        userTable.setItems(filteredUsers);
    }

    private String getFormattedName(User user) {
        if (user == null) return "";

        String lastName = user.getLname();
        String firstName = user.getFname();
        String middleName = user.getMname();

        // Remove suffixes from last name (Jr., Sr., II, III, etc.)
        if (lastName != null && !lastName.trim().isEmpty()) {
            lastName = removeSuffixes(lastName.trim());
        }

        StringBuilder formattedName = new StringBuilder();

        // Always start with last name if available
        if (lastName != null && !lastName.trim().isEmpty()) {
            formattedName.append(lastName);
        }

        // Add first name with comma separator
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (formattedName.length() > 0) {
                formattedName.append(", ");
            }
            formattedName.append(firstName.trim());
        }

        // Add middle initial if available
        if (middleName != null && !middleName.trim().isEmpty()) {
            String middleInitial = middleName.trim().substring(0, 1).toUpperCase() + ".";
            formattedName.append(" ").append(middleInitial);
        }

        // Fallback: if still empty, use user ID
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

        return cleanParts.isEmpty() ? name : String.join(" ", cleanParts);
    }

    private String getEmployeeShift(int userId) {
        // You'll need to implement this method to get the employee's shift
        // This is a placeholder - replace with actual shift retrieval logic
        try (var connection = DatabaseUtil.getConnection();
             var statement = connection.prepareStatement(
                     "SELECT s.shift_name FROM shift s " +
                     "JOIN assignment a ON s.shift_id = a.shift_id " +
                     "WHERE a.user_id = ? AND a.status = 1 LIMIT 1")) {
            statement.setInt(1, userId);
            var rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getString("shift_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Not Assigned";
    }

    private void showEmployeeDetails(User user) {
        this.selectedUser = user;
        ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
        if (containerCtrl != null) {
            ADMIN_ViewEmpCTRL viewCtrl = containerCtrl.loadFXMLAndGetController(paneUtil.ADMIN_VIEW_EMP);
            if (viewCtrl != null) {
                // Reload the user with all related data
                User completeUser = User.getUserByUserId(user.getId());
                if (completeUser != null) {
                    viewCtrl.setEmployeeData(completeUser);
                } else {
                    // Fallback: use the basic user data
                    viewCtrl.setEmployeeData(user);
                    showAlert("Warning", "Could not load complete employee data. Showing basic information only.");
                }
            } else {
                openEmployeeDetailsInNewWindow();
            }
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    private void openEmployeeDetailsInNewWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(paneUtil.ADMIN_VIEW_EMP));
            Parent root = loader.load();

            ADMIN_ViewEmpCTRL controller = loader.getController();
            // Reload the user with all related data
            User completeUser = User.getUserByUserId(selectedUser.getId());
            if (completeUser != null) {
                controller.setEmployeeData(completeUser);
            } else {
                controller.setEmployeeData(selectedUser);
            }

            Stage viewStage = new Stage();
            viewStage.setScene(new Scene(root));
            viewStage.setTitle("View Employee - " + getFormattedName(selectedUser));
            viewStage.initModality(Modality.WINDOW_MODAL);
            viewStage.initOwner(userTable.getScene().getWindow());

            viewStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot Open View Form");
            alert.setContentText("Failed to load the employee view form.");
            alert.showAndWait();
        }
    }

    private void openEditUser(User user) {
        this.selectedUser = user;
        ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
        if (containerCtrl != null) {
            ADMIN_EditEmpCTRL editCtrl = containerCtrl.loadFXMLAndGetController(paneUtil.ADMIN_EDIT_EMP);
            if (editCtrl != null) {
                User completeUser = User.getUserByUserId(user.getId());
                if (completeUser != null) {
                    editCtrl.setEmployeeData(completeUser);
                } else {
                    editCtrl.setEmployeeData(user);
                }
            } else {
                // Fallback
                openEditUserPaneInNewWindow();
            }
        }
    }

    private void openEditUserPaneInNewWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(paneUtil.ADMIN_ADD_EMP));
            Parent root = loader.load();

            ADMIN_AddEmpCTRL controller = loader.getController();
            Stage currentStage = (Stage) userTable.getScene().getWindow();
            controller.setDataForEdit(selectedUser, currentStage, this, true);

            Stage editStage = new Stage();
            editStage.setScene(new Scene(root));
            editStage.setTitle("Edit Employee - " + getFormattedName(selectedUser));
            editStage.initModality(Modality.WINDOW_MODAL);
            editStage.initOwner(currentStage);

            editStage.setOnHidden(e -> {
                refreshAfterEdit();
            });

            editStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot Open Edit Form");
            alert.setContentText("Failed to load the employee edit form.");
            alert.showAndWait();
        }
    }

    public void refreshAfterEdit() {
        loadUserTable();
    }

    @FXML
    private void openAddEmpPane(ActionEvent event) {
        ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
        if (containerCtrl != null) {
            containerCtrl.loadAddEmployee();
        }
    }
    @FXML
    private void handleImportCsv(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Employee CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(userTable.getScene().getWindow());

        if (file != null) {
            importEmployeesFromCsv(file);
        }
    }
    private void importEmployeesFromCsv(File file) {
        int successCount = 0;
        int failCount = 0;
        StringBuilder errorLog = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true; 

            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // Skip Header
                
                String[] data = line.split(","); 
                
                if (data.length < 8) {
                    failCount++;
                    errorLog.append("Skipped (missing columns): ").append(line).append("\n");
                    continue;
                }

                String agencyId = ""; 
                try {
                    // 1. Clean Data
                    agencyId = data[0].trim().replaceAll("[^a-zA-Z0-9-]", ""); 
                    String lname = data[1].trim();
                    String fname = data[2].trim();
                    String mname = data[3].trim();
                    String email = data[4].trim();
                    String deptName = data[5].trim();
                    String posName = data[6].trim();
                    String shiftName = data[7].trim();

                    System.out.println("Importing: " + agencyId); 

                    // 2. Resolve IDs (Validation)
                    int deptId = Department.getDepartmentIdByName(deptName);
                    if (deptId == -1) throw new Exception("Department not found: '" + deptName + "'");

                    int posId = Position.getPositionIdByName(posName, deptId);
                    if (posId == -1) throw new Exception("Position '" + posName + "' not found in " + deptName);

                    int shiftId = Shift.getShiftIdByName(shiftName);
                    if (shiftId == -1) throw new Exception("Shift not found: '" + shiftName + "'");

                    // 3. Create User Object
                    User user = new User();
                    user.setAgencyEmployeeNo(agencyId);
                    user.setLname(lname);
                    user.setFname(fname);
                    user.setMname(mname);
                    user.setEmail(email);
                    user.setPrivilege("Employee");
                    user.setPassword("default123"); 
                    user.setSex("Male"); 
                    user.setBirthDate(LocalDate.of(1990, 1, 1)); 
                    user.setContactNum("09000000000"); 

                    // 4. Insert User
                    // This commits to the DB immediately
                    User.addUser(user, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
                    
                    // Safety pause to ensure DB commit is readable
                    try { Thread.sleep(50); } catch (InterruptedException ie) {}

                    // 5. RETRIEVE ID (The Fix)
                    // Just get the newest ID in the system. 
                    int newUserId = getLastInsertedUserId();
                    
                    if (newUserId == -1) {
                        throw new Exception("CRITICAL: User inserted but could not retrieve ID.");
                    }

                    // 6. Create Assignment
                    createAssignmentForUser(newUserId, posId, shiftId);
                    
                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    errorLog.append("Error on ").append(agencyId).append(": ").append(e.getMessage()).append("\n");
                    e.printStackTrace();
                }
            }
            
            loadUserTable(); 
            
            String resultMsg = "Import Complete!\n\nSuccess: " + successCount + "\nFailed: " + failCount;
            if (failCount > 0) resultMsg += "\n\nSee console/log for details.";
            
            Modal.showModal("Import Summary", resultMsg);
            if (failCount > 0) System.err.println("CSV ERRORS:\n" + errorLog.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Modal.showModal("Import Error", "File error: " + e.getMessage());
        }
    }
    // --- ADD THIS HELPER ---
    private int getUserIdByNameFallback(String fname, String lname) {
        // CORRECTED: Changed 'first_name' -> 'fname', 'last_name' -> 'lname'
        String query = "SELECT user_id FROM user WHERE fname = ? AND lname = ? ORDER BY user_id DESC LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, fname);
            ps.setString(2, lname);
            
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Helper to keep code clean
    private void createAssignmentForUser(int userId, int posId, int shiftId) throws Exception {
        String today = LocalDate.now().toString();
        try (Connection assignConn = DatabaseUtil.getConnection()) {
            Assignment.addAssignment(assignConn, userId, posId, shiftId, "08:00", "17:00", today);
        }
    }

    // --- ADD THIS HELPER METHOD TO YOUR CONTROLLER ---
    private int getUserIdByAgencyNoNonTransactional(String agencyNo) {
        String query = "SELECT user_id FROM user WHERE agency_employee_no = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            
            // TRIM is crucial here
            ps.setString(1, agencyNo.trim());
            
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1; 
    }
    // Helper to get the ID of the most recently added user
    // This bypasses encryption and column name issues entirely
    private int getLastInsertedUserId() {
        String query = "SELECT user_id FROM user ORDER BY user_id DESC LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}