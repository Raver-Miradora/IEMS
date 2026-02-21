    package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.*;
import com.raver.iemsmaven.Utilities.Filter;
import com.raver.iemsmaven.Utilities.ImageUtil;
import com.raver.iemsmaven.Utilities.Modal;
import com.raver.iemsmaven.Utilities.NameFormatter;
import com.dlsc.gemsfx.TimePicker;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class ADMIN_AssignmentsCTRL implements Initializable {

    @FXML
    private TableColumn<User, Integer> col_id;
    @FXML
    private TableColumn<User, String> col_name;
    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<Assignment, String> col_department;
    @FXML
    private TableColumn<Assignment, String> col_position;
    @FXML
    private TableColumn<Assignment, String> col_shift;
    @FXML
    private TableColumn<Assignment, String> col_time;
    @FXML
    private TableColumn<Assignment, String> col_dateAssigned;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label userNameLabel;
    @FXML
    private ChoiceBox<Department> departmentChoiceBox;
    @FXML
    private ChoiceBox<Position> positionChoiceBox;
    @FXML
    private ChoiceBox<Shift> shiftChoiceBox;
    @FXML
    private TableView<Assignment> assignmentTable;
    @FXML
    private Button addBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button deactivateBtn;
    @FXML
    private HBox buttonContainerHBox;
    @FXML
    private ChoiceBox userAssignCntFilterChoiceBox;
    @FXML
    private ChoiceBox assignmentStatusFilterChoiceBox;

    User selectedUser = null;
    Assignment selectedAssignment = null;
    @FXML
    private TextField searchField;
    @FXML
    private Label manageUserLabel;
    @FXML
    private TimePicker startTimePicker;
    @FXML
    private TimePicker endTimePicker;
    // TreeView: only employees now
    @FXML
    private TreeView<String> employeeTreeView;

    private final Map<String, Integer> employeeDisplayNameToId = new HashMap<>();

    // Common suffixes to remove from names
    private static final Set<String> NAME_SUFFIXES = Set.of(
        "jr", "jr.", "sr", "sr.", "ii", "iii", "iv", "v"
    );

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ASSIGNMENT TABLE
        col_department.setCellValueFactory(new PropertyValueFactory<Assignment, String>("department"));
        col_position.setCellValueFactory(new PropertyValueFactory<Assignment, String>("position"));
        col_shift.setCellValueFactory(new PropertyValueFactory<Assignment, String>("shift"));
        col_time.setCellValueFactory(new PropertyValueFactory<Assignment, String>("timeRange"));
        col_dateAssigned.setCellValueFactory(new PropertyValueFactory<Assignment, String>("dateAssigned"));

        // ASSIGNMENT CHOICEBOX
        departmentChoiceBox.getItems().addAll(Department.getActiveDepartments());
        shiftChoiceBox.getItems().addAll(Shift.getActiveShifts());

        // Update position choicebox when department changes
        departmentChoiceBox.setOnAction(event -> {
            Department selectedDept = departmentChoiceBox.getValue();
            if (selectedDept != null) {
                positionChoiceBox.setItems(Position.getPositionsByDepartmentId(selectedDept.getId()));
            } else {
                positionChoiceBox.setItems(FXCollections.observableArrayList());
            }
            positionChoiceBox.setValue(null);
        });

        // ASSIGNMENT STATUS FILTER CHOICE BOX
        assignmentStatusFilterChoiceBox.getItems().addAll("Active", "Inactive");
        assignmentStatusFilterChoiceBox.setValue("Active");

        assignmentStatusFilterChoiceBox.setOnAction((event) -> {
            if (selectedUser != null) {
                loadAssignmentTable(selectedUser.getId());
            }
        });

        // Initialize employee tree view only
        initializeTreeViews();

        userImageView.setVisible(false);
        userNameLabel.setVisible(false);
        manageUserLabel.setVisible(false);

        startTimePicker.setTime(null);
        endTimePicker.setTime(null);

        // removed loadUserTable(); because we're using tree view selection now
    }

    private void initializeTreeViews() {
        employeeDisplayNameToId.clear();

        // Employees TreeView
        TreeItem<String> employeeRoot = new TreeItem<>("Employees");
        employeeRoot.setExpanded(true);

        ObservableList<User> employees = User.getActiveEmployees();
        List<TreeItem<String>> employeeItems = new ArrayList<>();
        for (User user : employees) {
            // Use NameFormatter for consistent name formatting
            String displayName = NameFormatter.getFormattedName(user);
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = "User " + user.getId();
            }
            employeeDisplayNameToId.put(displayName, user.getId());
            TreeItem<String> employeeItem = new TreeItem<>(displayName);
            employeeItem.setExpanded(false);
            employeeItems.add(employeeItem);
        }

        // Sort by last name
        Collections.sort(employeeItems, Comparator.comparing(a -> extractLastNameFromDisplay(a.getValue()).toLowerCase()));
        employeeRoot.getChildren().addAll(employeeItems);
        employeeTreeView.setRoot(employeeRoot);
        employeeTreeView.setShowRoot(true);

        // Setup selection cell factory (plain text, no checkboxes)
        setupTreeViewSelection(employeeTreeView);

        // Add mouse click listener for employee selection
        employeeTreeView.setOnMouseClicked(this::selectEmployeeFromTree);
    }

    private void setupTreeViewSelection(TreeView<String> treeView) {
        treeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(null); // No checkboxes - just plain text
                }
            }
        });
    }

    @FXML
    public void selectEmployeeFromTree(MouseEvent event) {
        TreeItem<String> selectedItem = employeeTreeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem.isLeaf()) {
            String employeeName = selectedItem.getValue();
            Integer employeeId = getEmployeeIdFromName(employeeName);

            if (employeeId != null) {
                selectedUser = User.getUserByUserId(employeeId);
                loadAssignmentTable(selectedUser.getId());

                // Use the utility class for consistent name formatting
                userNameLabel.setText(NameFormatter.getFormattedName(selectedUser));
                userImageView.setImage(ImageUtil.byteArrayToImage(selectedUser.getImage()));
                userImageView.setVisible(true);
                userNameLabel.setVisible(true);
                manageUserLabel.setVisible(true);

                clearFields();
            }
        }
    }

    // Fix the TreeView initialization method in ADMIN_AssignmentsCTRL
    private void initializeEmployeeTreeView() {
        TreeItem<String> employeeRoot = new TreeItem<>("Employees");
        employeeRoot.setExpanded(true);

        ObservableList<User> employees = User.getEmployees();
        List<TreeItem<String>> employeeItems = new ArrayList<>();

        for (User user : employees) {
            // Use the utility class for consistent name formatting
            String displayName = NameFormatter.getFormattedName(user);
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = "User " + user.getId();
            }

            TreeItem<String> employeeItem = new TreeItem<>(displayName);
            employeeItem.setExpanded(false);
            employeeItems.add(employeeItem);
        }

        // Sort by last name
        Collections.sort(employeeItems, Comparator.comparing(a -> extractLastNameFromDisplay(a.getValue()).toLowerCase()));
        employeeRoot.getChildren().addAll(employeeItems);
        employeeTreeView.setRoot(employeeRoot);
        employeeTreeView.setShowRoot(true);
    }

   private String extractLastNameFromDisplay(String displayName) {
    if (displayName == null || displayName.trim().isEmpty()) return "";
    
    // If the name is formatted as "Last, First M.I.", extract the last name part
    if (displayName.contains(",")) {
        return displayName.split(",")[0].trim();
    }
    
    // Fallback: return the entire string if no comma found
    return displayName.trim();
}

    private Integer getEmployeeIdFromName(String name) {
        if (name == null) return null;
        if (employeeDisplayNameToId.containsKey(name)) {
            return employeeDisplayNameToId.get(name);
        }

        // If not found, search through all employees
        ObservableList<User> employees = User.getActiveEmployees();
        for (User user : employees) {
            String formattedName = NameFormatter.getFormattedName(user);
            if (formattedName.equalsIgnoreCase(name)) {
                employeeDisplayNameToId.put(formattedName, user.getId());
                return user.getId();
            }
        }
        return null;
    }


    public void loadUserTable(){
        ObservableList<User> users = User.getActiveEmployees();

        //filter the users list based on their assignment count and store them in a new list
        ObservableList<User> filteredUsers = users.filtered(user -> {
            int assignmentCount = Assignment.getActiveAssignmentCountByUserId(user.getId());
            String userAssignCntFilter = userAssignCntFilterChoiceBox.getValue().toString();

            if(userAssignCntFilter.equals("All")){
                return true;
            }else if(userAssignCntFilter.equals("None")){
                return assignmentCount == 0;
            }else if(userAssignCntFilter.equals("1")){
                return assignmentCount == 1;
            }else if(userAssignCntFilter.equals("2")){
                return assignmentCount == 2;
            }else{
                return assignmentCount > 2;
            }
        });

        //then, filter based on searchFilterField, store in new list
        filteredUsers = filteredUsers.filtered(user -> {
            String searchFilter = searchField.getText().toLowerCase();
            if(searchFilter.isEmpty()){
                return true;
            }else{
                return user.getFullName().toLowerCase().contains(searchFilter);
            }
        });

        userTable.setItems(filteredUsers);
    }

    public void loadAssignmentTable(int user_id){

        if (user_id <= 0) {
            assignmentTable.setItems(FXCollections.observableArrayList());
            return;
        }
        String statusFilter = (String) assignmentStatusFilterChoiceBox.getValue();
        ObservableList<Assignment> assignments = Assignment.getAssignmentsByUserId(user_id);

        //create a new observable list to store filtered assignments
        ObservableList<Assignment> filteredAssignments = assignments.filtered(assignment -> {
            if(statusFilter.equals("Active")){
                return assignment.getStatus() == 1;
            }else if(statusFilter.equals("Inactive")){
                return assignment.getStatus() == 0;
            }else{
                return true;
            }
        });

        assignmentTable.setItems(filteredAssignments);
    }

    @FXML
    private void userSelected(MouseEvent event) {
        selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) return;
        loadAssignmentTable(selectedUser.getId());

        userNameLabel.setText(selectedUser.getFullName());
        userImageView.setImage(ImageUtil.byteArrayToImage(User.getUserImageByUserId(selectedUser.getId())));
        userImageView.setVisible(true);
        userNameLabel.setVisible(true);
        manageUserLabel.setVisible(true);

        clearFields();
    }

    @FXML
    private void assignmentSelected(MouseEvent event) {
        selectedAssignment = assignmentTable.getSelectionModel().getSelectedItem();
        if (selectedAssignment == null) return;

        String department = selectedAssignment.getDepartment();
        int departmentId = selectedAssignment.getDepartmentId();
        String position = selectedAssignment.getPosition();
        int positionId = selectedAssignment.getPositionId();
        String shift = selectedAssignment.getShift();
        int shiftId = selectedAssignment.getShiftId();
        String timeRange = selectedAssignment.getTimeRange();

        String[] times = timeRange.split(" - ");

        if (times.length == 2) {
            // Parse start and end times defensively
            try {
                String startTime = times[0];
                String endTime = times[1];
                startTimePicker.setTime(LocalTime.parse(startTime));
                endTimePicker.setTime(LocalTime.parse(endTime));
            } catch (Exception ex) {
                System.out.println("Invalid time parsing in assignmentSelected: " + ex.getMessage());
            }
        } else {
            System.out.println("Invalid time range format");
        }

        departmentChoiceBox.setValue(new Department(departmentId, department));
        positionChoiceBox.setValue(new Position(positionId, position));
        shiftChoiceBox.setValue(new Shift(shiftId, shift));

        if(selectedAssignment.getStatus() == 1){
            deactivateBtn.setText("Deactivate");
        }else{
            deactivateBtn.setText("Activate");
        }
    }

    public void showAddBtnOnly(){
        buttonContainerHBox.getChildren().clear();
        HBox.setHgrow(addBtn, javafx.scene.layout.Priority.ALWAYS);
        buttonContainerHBox.getChildren().add(addBtn);
        addBtn.setVisible(true);
        updateBtn.setVisible(false);
        deactivateBtn.setVisible(false);
    }

    public void showUpdateDeactivateBtnOnly(){
        buttonContainerHBox.getChildren().clear();
        HBox.setHgrow(updateBtn, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(deactivateBtn, javafx.scene.layout.Priority.ALWAYS);
        buttonContainerHBox.getChildren().add(updateBtn);
        buttonContainerHBox.getChildren().add(deactivateBtn);
        addBtn.setVisible(false);
        updateBtn.setVisible(true);
        deactivateBtn.setVisible(true);
    }

    public void clearFields(){
        departmentChoiceBox.setValue(null);
        positionChoiceBox.setValue(null);
        shiftChoiceBox.setValue(new Shift(""));

        startTimePicker.setTime(null);
        endTimePicker.setTime(null);
    }

    @FXML
    private void addAssignment(ActionEvent event) throws SQLException {
        if(selectedUser == null){
            Modal.showModal("Failed", "Please select a user first");
            return;
        }

        if (departmentChoiceBox.getValue() == null || positionChoiceBox.getValue() == null || shiftChoiceBox.getValue() == null || startTimePicker.getTime() == null || endTimePicker.getTime() == null) {
            Modal.showModal("Failed", "Please fill out all fields");
            return;
        }

        int userId = selectedUser.getId();
        int positionId = positionChoiceBox.getValue().getId();
        int shiftId = shiftChoiceBox.getValue().getId();

        String startTime = startTimePicker.getTime().toString();
        String endTime = endTimePicker.getTime().toString();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateAssigned = currentDate.format(formatter);

        if (Assignment.positionAlreadyExists(userId, positionId) && !shiftChoiceBox.getValue().getShiftName().toLowerCase().contains("overload")) {
            Modal.showModal("Failed", "This position is already assigned to the selected user");
            return;
        }

        boolean isOverlapping = false;

        ObservableList<Assignment> assignments = Assignment.getActiveAssignmentsByUserId(userId);
        for (Assignment assignment : assignments) {
            if (Filter.TIME.isOverlapping(assignment.getStartTime() + "", assignment.getEndTime() + "", startTime, endTime)) {
                isOverlapping = true;
                break;
            }
        }

        System.out.println("isOverlapping: " + isOverlapping);

//        if (isOverlapping) {
//            if (Modal.actionConfirmed("Overlapping Assignment", "Overlapping assignment, Continue?", "This will add an overlapping assignment")) {
//                Assignment.addAssignment(userId, positionId, shiftId, startTime, endTime, dateAssigned);
//            }
//        } else {
//            if (Modal.actionConfirmed("Add Assignment", "Do you want to proceed?", "This will add a new assignment")) {
//                Assignment.addAssignment(userId, positionId, shiftId, startTime, endTime, dateAssigned);
//            }
//        }

        loadAssignmentTable(userId);
    }

    @FXML
    private void updateAssignment(ActionEvent event){
        if (selectedAssignment == null) return;
        int assignmentId = selectedAssignment.getId();
        int positionId = positionChoiceBox.getValue().getId();
        int shiftId = shiftChoiceBox.getValue().getId();

        String startTime = startTimePicker.getTime().toString();
        String endTime = endTimePicker.getTime().toString();

        boolean actionIsConfirmed = Modal.actionConfirmed("Update", "Do you want to proeed?", "This will update the selected assignment record");
        if(actionIsConfirmed){
            try {
                Assignment.updateAssignment(assignmentId, positionId, shiftId, startTime, endTime);
            } catch (SQLException ex) {
                Logger.getLogger(ADMIN_AssignmentsCTRL.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadAssignmentTable(selectedUser.getId());
        }
    }

    @FXML
    private void invertAssignmentStatus(ActionEvent event) {
        if (selectedAssignment == null) return;

        String actionType = selectedAssignment.getStatus() == 1 ? "Deactivate" : "Activate";
        String confirmationMessage = actionType + " this assignment?";
        String actionDescription = "This action will " + actionType.toLowerCase() + " the currently selected assignment";

        if (Modal.actionConfirmed(actionType, confirmationMessage, actionDescription)) {
            Assignment.invertAssignmentStatus(selectedAssignment.getId());
            loadAssignmentTable(selectedUser.getId());
            clearFields();
        }
    }

    private String filterHour(String hour){
        String[] hourArray = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (String hourArr : hourArray) {
            if (hour.equals(hourArr)) {
                return hour;
            }
        }

        return "";
    }

    private String filterMinute(String minute) {
        int minMinute = 0;
        int maxMinute = 59;
        ArrayList<String> minuteList = new ArrayList<>();

        for (int i = minMinute; i <= maxMinute; i++) {
            minuteList.add(String.format("%02d", i));
        }

        if (minuteList.contains(minute)) {
            return minute;
        } else {
            return "";
        }
    }
}
