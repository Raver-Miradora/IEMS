package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.Department;
import com.raver.iemsmaven.Model.Fingerprint;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Utilities.ImageUtil;
import com.raver.iemsmaven.Utilities.PaneUtil;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;
import javafx.scene.image.Image;

public class ADMIN_FingerprintsCTRL implements Initializable {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> col_id;
    @FXML private TableColumn<User, String> col_name;
    @FXML private TableColumn<User, String> col_position;
    @FXML private TableColumn<User, String> col_status;
    @FXML private Button enrollBtn;
    @FXML private ImageView userImageView;
    @FXML private Label fingerprintCountLabel;
    @FXML private Label lastEnrollDateLabel;
    @FXML private Label nameLabel;

    // Filter Controls
    @FXML private ComboBox<Department> departmentCombo;
    @FXML private ComboBox<User> employeeCombo;

    PaneUtil paneUtil = new PaneUtil();
    User userFromDb;

    // Dummy "All" objects for filtering
    private final Department ALL_DEPARTMENTS = new Department(0, "All Departments");
    private final User ALL_EMPLOYEES = new User(0, "All Employees", null, null, null, null);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Setup Table Columns
        col_id.setCellValueFactory(new PropertyValueFactory<>("agencyEmployeeNo"));
        col_id.setStyle("-fx-alignment: CENTER;");

        // Name column: Formatted Name
        col_name.setCellValueFactory(cellData -> {
            User u = cellData.getValue();
            String display = u == null ? "" : getFormattedName(u);
            return new SimpleStringProperty(display);
        });

        // Position & Status
        col_position.setCellValueFactory(new PropertyValueFactory<>("position"));
        col_status.setCellValueFactory(new PropertyValueFactory<>("enrollmentStatus"));

        // Custom Cell for Status Color
        col_status.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Enrolled".equals(item)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else if ("Unenrolled".equals(item)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        setupFilterControls();
        loadUserTable(); 

        nameLabel.setText("Select a User");
        lastEnrollDateLabel.setText("--/--/----");
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
        
        // --- Employee ComboBox ---
        employeeCombo.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                return (user == null || user.getFullName() == null) ? "" : user.getFullName();
            }
            @Override
            public User fromString(String string) { return null; }
        });
        
        // Listeners
        departmentCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateEmployeeCombo();
            loadUserTable(); 
        });
        
        employeeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadUserTable();
        });

        updateEmployeeCombo();
    }
    
    private void updateEmployeeCombo() {
        Department selectedDept = departmentCombo.getValue();
        ObservableList<User> employees = FXCollections.observableArrayList();
        employees.add(ALL_EMPLOYEES);

        ObservableList<User> allUsers = User.getActiveEmployeesWithDepartmentAndPosition();
        for (User user : allUsers) {
            if (selectedDept == null || selectedDept.getId() == 0) {
                employees.add(user); 
            } else if (user.getDepartment() != null && user.getDepartment().equals(selectedDept.getDepartmentName())) {
                employees.add(user); 
            }
        }
        
        FXCollections.sort(employees, (u1, u2) -> {
            if (u1.getId() == 0) return -1;
            if (u2.getId() == 0) return 1;
            return getFormattedName(u1).compareToIgnoreCase(getFormattedName(u2));
        });

        employeeCombo.setItems(employees);
        employeeCombo.setValue(ALL_EMPLOYEES);
    }

    private String getFormattedName(User user) {
        if (user == null) return "";
        if (user.getId() == 0) return user.getFullName();

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

   public void loadUserTable() {
        User selectedEmployee = employeeCombo.getValue();
        ObservableList<User> usersToDisplay;

        if (selectedEmployee != null && selectedEmployee.getId() != 0) {
            usersToDisplay = FXCollections.observableArrayList(selectedEmployee);
        } else {
            usersToDisplay = FXCollections.observableArrayList(employeeCombo.getItems());
            usersToDisplay.remove(ALL_EMPLOYEES); 
        }

        for (User user : usersToDisplay) {
            int fingerprintCount = Fingerprint.getFingerprintCountByUserId(user.getId());
            user.setEnrollmentStatus(fingerprintCount > 0 ? "Enrolled" : "Unenrolled");
        }

        usersToDisplay.sort((u1, u2) -> {
            int statusCompare = u1.getEnrollmentStatus().compareTo(u2.getEnrollmentStatus());
            if (statusCompare != 0) {
                return -statusCompare; 
            }
            return getFormattedName(u1).compareToIgnoreCase(getFormattedName(u2));
        });

        userTable.setItems(usersToDisplay);
    }

    @FXML
    private void loadUserDetailsAction(MouseEvent event) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            userFromDb = User.getUserByUserId(selectedUser.getId());
            loadUserDetails(selectedUser);
        }
    }

    public void loadUserDetails(User selectedUser) {
        int fingerprintCount = Fingerprint.getFingerprintCountByUserId(selectedUser.getId());
        String lastFingerprintEnroll = Fingerprint.getLastFingerprintEnrollByUserId(selectedUser.getId());
        byte[] userImage = User.getUserByUserId(selectedUser.getId()).getImage();

        fingerprintCountLabel.setText(String.valueOf(fingerprintCount));
        nameLabel.setText(getFormattedName(userFromDb));
        lastEnrollDateLabel.setText(lastFingerprintEnroll != null ? lastFingerprintEnroll : "N/A"); 
        
        if (userImage != null) {
             userImageView.setImage(ImageUtil.byteArrayToImage(userImage));
        } else {
             userImageView.setImage(new Image("/Images/default_user_img.jpg"));
        }

        if (fingerprintCount > 0) enrollBtn.setText("Re-Enroll");
        else enrollBtn.setText("Enroll");
    }

    @FXML
    private void erollBtnAction(ActionEvent event) {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a user from the table.");
            alert.showAndWait();
            return;
        }

        int selectedUserId = selectedUser.getId();
        String enrollBtnText = enrollBtn.getText();

        if (!(enrollBtnText.equalsIgnoreCase("Enroll"))) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Re-Enroll");
            alert.setHeaderText("Do you want to proceed?");
            alert.setContentText("This action will destroy this User's Fingerprints stored in the database");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");

            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(response -> {
                if (response == yesButton) {
                    Fingerprint.destroyEnrolledFingerprintsByUserId(selectedUserId);
                    try {
                        User currentAdmin = Session.getInstance().getLoggedInUser();
                        String employeeName = getFormattedName(selectedUser);
                        
                        String logDescription = "Cleared all enrolled fingerprints for employee: " + employeeName + " (ID: " + selectedUser.getAgencyEmployeeNo() + ")";
                        ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), logDescription);
                    } catch (Exception logEx) {
                        System.err.println("Failed to log 'Destroy Fingerprints' activity: " + logEx.getMessage());
                    }
                    openFpEnrollmentPane();
                }
            });
        } else {
            openFpEnrollmentPane();
        }
    }

    private void openFpEnrollmentPane() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;

        int selectedUserId = selectedUser.getId();
        User userFromDb = User.getUserByUserId(selectedUserId);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(paneUtil.FP_ENROLLMENT));
            Parent root = loader.load();

            FP_EnrollmentCTRL fpEnrollmentCtrl = loader.getController();

            Stage secondStage = new Stage();
            secondStage.setScene(new Scene(root));
            secondStage.initModality(Modality.APPLICATION_MODAL);

            fpEnrollmentCtrl.setDataForEnrollment(userFromDb, secondStage, this);
            secondStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}