package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.*;
import com.raver.iemsmaven.Model.Department;
import com.raver.iemsmaven.Model.Position;
import com.raver.iemsmaven.Model.Shift;
import com.raver.iemsmaven.Utilities.DatabaseUtil;
import com.raver.iemsmaven.Utilities.ImageUtil;
import com.raver.iemsmaven.Utilities.PaneUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;
import javafx.scene.image.Image;

public class ADMIN_EditEmpCTRL implements Initializable {

    // Personal Information Fields (Editable)
    @FXML private TextField surnameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private ChoiceBox<String> suffixChoiceBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private TextField placeOfBirthField;
    @FXML private ChoiceBox<String> sexChoiceBox;
    @FXML private TextField heightField;
    @FXML private ChoiceBox<String> civilStatusChoiceBox;
    @FXML private TextField weightField;
    @FXML private ChoiceBox<String> bloodTypeChoiceBox;
    @FXML private ChoiceBox<String> citizenshipChoiceBox;
    
    // Contact Information Fields (Editable)
    @FXML private TextField mobileNoField;
    @FXML private TextField telephoneNoField;
    @FXML private TextField emailField;
    
    // Government IDs Fields (Editable)
    @FXML private TextField gsisIdField;
    @FXML private TextField pagibigIdField;
    @FXML private TextField philhealthNoField;
    @FXML private TextField sssNoField;
    @FXML private TextField tinNoField;
    @FXML private TextField agencyEmployeeNoField;
    
    // Address Fields (Editable)
    @FXML private TextField residentialHouseNoField;
    @FXML private TextField residentialStreetField;
    @FXML private TextField residentialBarangayField;
    @FXML private TextField residentialCityField;
    @FXML private TextField residentialProvinceField;
    @FXML private TextField residentialZipCodeField;
    
    @FXML private TextField permanentHouseNoField;
    @FXML private TextField permanentStreetField;
    @FXML private TextField permanentBarangayField;
    @FXML private TextField permanentCityField;
    @FXML private TextField permanentProvinceField;
    @FXML private TextField permanentZipCodeField;
    
    
    // Image
    @FXML private ImageView userImageView;
    @FXML private ImageView userImageViewLarge;
    @FXML private Button selectImageBtn;
    
    // Family Background Tables (Editable)
    @FXML private TableView<SpouseInfo> spouseTable;
    @FXML private TableView<ParentInfo> parentsTable;
    @FXML private TableView<ChildInfo> childrenTable;
    
    // Education, Eligibility, Work Experience Tables (Editable)
    @FXML private TableView<EducationalBackground> educationTable;
    @FXML private TableView<CivilServiceEligibility> eligibilityTable;
    @FXML private TableView<WorkExperience> workExperienceTable;
    
    // Buttons
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button addSpouseBtn;
    @FXML private Button addParentBtn;
    @FXML private Button addChildBtn;
    @FXML private Button addEducationBtn;
    @FXML private Button addEligibilityBtn;
    @FXML private Button addWorkExperienceBtn;
    
    // Table Columns
    @FXML private TableColumn<SpouseInfo, String> spouseNameCol;
    @FXML private TableColumn<SpouseInfo, String> spouseOccupationCol;
    @FXML private TableColumn<SpouseInfo, String> spouseEmployerCol;
    @FXML private TableColumn<SpouseInfo, String> spouseBusinessAddressCol;
    @FXML private TableColumn<SpouseInfo, String> spouseTelephoneCol;
    @FXML private TableColumn<SpouseInfo, Void> spouseActionsCol;
    
    @FXML private TableColumn<ParentInfo, String> parentTypeCol;
    @FXML private TableColumn<ParentInfo, String> parentNameCol;
    @FXML private TableColumn<ParentInfo, Void> parentActionsCol;
    
    @FXML private TableColumn<ChildInfo, String> childNameCol;
    @FXML private TableColumn<ChildInfo, String> childDobCol;
    @FXML private TableColumn<ChildInfo, Void> childActionsCol;
    
    @FXML private TableColumn<EducationalBackground, String> eduLevelCol;
    @FXML private TableColumn<EducationalBackground, String> eduSchoolCol;
    @FXML private TableColumn<EducationalBackground, String> eduDegreeCol;
    @FXML private TableColumn<EducationalBackground, String> eduPeriodCol;
    @FXML private TableColumn<EducationalBackground, String> eduUnitsCol;
    @FXML private TableColumn<EducationalBackground, String> eduYearGradCol;
    @FXML private TableColumn<EducationalBackground, String> eduHonorsCol;
    @FXML private TableColumn<EducationalBackground, Void> eduActionsCol;
    
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityNameCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityRatingCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityExamDateCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityExamPlaceCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityLicenseCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityValidityCol;
    @FXML private TableColumn<CivilServiceEligibility, Void> eligibilityActionsCol;
    
    @FXML private TableColumn<WorkExperience, String> workPeriodCol;
    @FXML private TableColumn<WorkExperience, String> workPositionCol;
    @FXML private TableColumn<WorkExperience, String> workCompanyCol;
    @FXML private TableColumn<WorkExperience, String> workSalaryCol;
    @FXML private TableColumn<WorkExperience, String> workGradeCol;
    @FXML private TableColumn<WorkExperience, String> workStatusCol;
    @FXML private TableColumn<WorkExperience, String> workGovServiceCol;
    @FXML private TableColumn<WorkExperience, Void> workActionsCol;
    
    @FXML private ChoiceBox<Department> departmentChoiceBox;
    @FXML private ChoiceBox<Position> positionChoiceBox;
    @FXML private ChoiceBox<Shift> shiftChoiceBox;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    
    private Assignment currentAssignment;
    private User currentUser;
    private PaneUtil paneUtil = new PaneUtil();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private byte[] imageBytes;
    // Document fields
    @FXML private VBox documentsContainer;
    @FXML private TextField googleDriveUrlField;

    private Map<String, EmployeeDocument> currentDocuments = new HashMap<>();
    private Map<String, byte[]> newDocumentFiles = new HashMap<>();
    private Map<String, String> newDocumentFileNames = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeChoiceBoxes();
        initializeAssignmentChoiceBoxes();
        initializeDocumentsSection(); 
        initializeTables();
        setupTableEditableCells();
    }
    
    private void initializeChoiceBoxes() {
        // Suffix
        suffixChoiceBox.setItems(FXCollections.observableArrayList("", "Jr.", "Sr.", "II", "III", "IV"));
        
        // Sex
        sexChoiceBox.setItems(FXCollections.observableArrayList("Male", "Female"));
        
        // Civil Status
        civilStatusChoiceBox.setItems(FXCollections.observableArrayList("Single", "Married", "Widowed", "Separated", "Other"));
        
        // Blood Type
        bloodTypeChoiceBox.setItems(FXCollections.observableArrayList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"));
        
        // Citizenship
        citizenshipChoiceBox.setItems(FXCollections.observableArrayList("Filipino", "Dual Citizenship"));
    }
    private void initializeAssignmentChoiceBoxes() {
        try {
            // Initialize department ChoiceBox
            departmentChoiceBox.setItems(Department.getActiveDepartments());
            departmentChoiceBox.setConverter(new StringConverter<Department>() {
                @Override
                public String toString(Department department) {
                    return department != null ? department.getDepartmentName() : "";
                }
                @Override
                public Department fromString(String string) {
                    return null;
                }
            });

            // Initialize position ChoiceBox
            positionChoiceBox.setItems(Position.getPositions());
            positionChoiceBox.setConverter(new StringConverter<Position>() {
                @Override
                public String toString(Position position) {
                    return position != null ? position.getPosition() : "";
                }
                @Override
                public Position fromString(String string) {
                    return null;
                }
            });

            // Initialize shift ChoiceBox
            shiftChoiceBox.setItems(Shift.getActiveShifts());
            shiftChoiceBox.setConverter(new StringConverter<Shift>() {
                @Override
                public String toString(Shift shift) {
                    return shift != null ? shift.getShiftName() : "";
                }
                @Override
                public Shift fromString(String string) {
                    return null;
                }
            });
            
            shiftChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newShift) -> {
                if (newShift != null) {
                    
                    startTimeField.setText(formatTimeForInput(newShift.getStartTime()));
                    endTimeField.setText(formatTimeForInput(newShift.getEndTime()));
                }
            });

            // Add listener to update positions when department changes
            departmentChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    try {
                        positionChoiceBox.setItems(Position.getPositionsByDepartmentId(newVal.getId()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Error", "Failed to load positions for selected department.");
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to initialize assignment fields.");
        }
    }
    private String formatTimeForInput(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return "";
        if (timeStr.length() >= 5) {
            return timeStr.substring(0, 5);
        }
        return timeStr;
    }

    private void initializeTables() {
        // Spouse Table
        spouseNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        spouseOccupationCol.setCellValueFactory(new PropertyValueFactory<>("occupation"));
        spouseEmployerCol.setCellValueFactory(new PropertyValueFactory<>("employer"));
        spouseBusinessAddressCol.setCellValueFactory(new PropertyValueFactory<>("businessAddress"));
        spouseTelephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        setupActionsColumn(spouseActionsCol, spouseTable);

        // Parents Table
        parentTypeCol.setCellValueFactory(new PropertyValueFactory<>("parentType"));
        parentNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        setupActionsColumn(parentActionsCol, parentsTable);

        // Children Table
        childNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        childDobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        setupActionsColumn(childActionsCol, childrenTable);

        // Education Table
        eduLevelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        eduSchoolCol.setCellValueFactory(new PropertyValueFactory<>("schoolName"));
        eduDegreeCol.setCellValueFactory(new PropertyValueFactory<>("degreeCourse"));
        eduPeriodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        eduUnitsCol.setCellValueFactory(new PropertyValueFactory<>("unitsEarned"));
        eduYearGradCol.setCellValueFactory(new PropertyValueFactory<>("yearGraduated"));
        eduHonorsCol.setCellValueFactory(new PropertyValueFactory<>("honors"));
        setupActionsColumn(eduActionsCol, educationTable);

        // Eligibility Table
        eligibilityNameCol.setCellValueFactory(new PropertyValueFactory<>("eligibilityName"));
        eligibilityRatingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        eligibilityExamDateCol.setCellValueFactory(new PropertyValueFactory<>("examDateDisplay"));
        eligibilityExamPlaceCol.setCellValueFactory(new PropertyValueFactory<>("examPlace"));
        eligibilityLicenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        eligibilityValidityCol.setCellValueFactory(new PropertyValueFactory<>("validityDateDisplay"));
        setupActionsColumn(eligibilityActionsCol, eligibilityTable);

        // Work Experience Table
        workPeriodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        workPositionCol.setCellValueFactory(new PropertyValueFactory<>("positionTitle"));
        workCompanyCol.setCellValueFactory(new PropertyValueFactory<>("company"));
        workSalaryCol.setCellValueFactory(new PropertyValueFactory<>("monthlySalaryDisplay"));
        workGradeCol.setCellValueFactory(new PropertyValueFactory<>("salaryGrade"));
        workStatusCol.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus"));
        workGovServiceCol.setCellValueFactory(new PropertyValueFactory<>("governmentService"));
        setupActionsColumn(workActionsCol, workExperienceTable);
    }

    private <T> void setupActionsColumn(TableColumn<T, Void> column, TableView<T> tableView) {
        column.setCellFactory(param -> new TableCell<T, Void>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    T data = getTableView().getItems().get(getIndex());
                    tableView.getItems().remove(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }

    private void setupTableEditableCells() {
        // Make tables editable
        spouseTable.setEditable(true);
        parentsTable.setEditable(true);
        childrenTable.setEditable(true);
        educationTable.setEditable(true);
        eligibilityTable.setEditable(true);
        workExperienceTable.setEditable(true);

        // Setup editable columns for Education Table
        setupEditableEducationColumns();
        setupEditableEligibilityColumns();
        setupEditableWorkExperienceColumns();
        setupEditableFamilyColumns();
    }
    private void setupEditableEducationColumns() {
        makeColumnEditable(eduLevelCol, EducationalBackground::setLevel);
        makeColumnEditable(eduSchoolCol, EducationalBackground::setSchoolName);
        makeColumnEditable(eduDegreeCol, EducationalBackground::setDegreeCourse);
        makeColumnEditable(eduUnitsCol, EducationalBackground::setUnitsEarned);
        makeColumnEditable(eduYearGradCol, EducationalBackground::setYearGraduated);
        makeColumnEditable(eduHonorsCol, EducationalBackground::setHonors);

        // Keep your existing Period cell logic as it uses DatePickers
        eduPeriodCol.setCellFactory(column -> createPeriodEditCell(
                EducationalBackground::getStartDate, EducationalBackground::setStartDate,
                EducationalBackground::getEndDate, EducationalBackground::setEndDate,
                EducationalBackground::getPeriod
        ));
    }
    private void setupEditableEligibilityColumns() {
        makeColumnEditable(eligibilityNameCol, CivilServiceEligibility::setEligibilityName);
        makeColumnEditable(eligibilityRatingCol, CivilServiceEligibility::setRating);
        makeColumnEditable(eligibilityExamPlaceCol, CivilServiceEligibility::setExamPlace);
        makeColumnEditable(eligibilityLicenseCol, CivilServiceEligibility::setLicenseNumber);

        // Keep existing Date logic
        eligibilityExamDateCol.setCellFactory(column -> createDateEditCell(
                CivilServiceEligibility::getExamDate, CivilServiceEligibility::setExamDate,
                CivilServiceEligibility::getExamDateDisplay
        ));
        eligibilityValidityCol.setCellFactory(column -> createDateEditCell(
                CivilServiceEligibility::getValidityDate, CivilServiceEligibility::setValidityDate,
                CivilServiceEligibility::getValidityDateDisplay
        ));
    }
    private void setupEditableWorkExperienceColumns() {
        // 1. Standard Text Columns (Using the helper to fix "not saving" issue)
        makeColumnEditable(workPositionCol, WorkExperience::setPositionTitle);
        makeColumnEditable(workCompanyCol, WorkExperience::setCompany);
        makeColumnEditable(workGradeCol, WorkExperience::setSalaryGrade);
        makeColumnEditable(workStatusCol, WorkExperience::setAppointmentStatus);

        // 2. Period Column (Start Date - End Date)
        workPeriodCol.setCellFactory(column -> createPeriodEditCell(
                WorkExperience::getStartDate,
                WorkExperience::setStartDate,
                WorkExperience::getEndDate,
                WorkExperience::setEndDate,
                WorkExperience::getPeriod
        ));

        // 3. Salary Column (Custom logic for Currency Formatting)
        workSalaryCol.setCellFactory(column -> new TableCell<WorkExperience, String>() {
            private final TextField textField = new TextField();

            {
                // Commit on Enter
                textField.setOnAction(event -> commitSalary());
                
                // Commit on Focus Loss
                textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) { // Lost focus
                        commitSalary();
                    }
                });
            }

            private void commitSalary() {
                WorkExperience work = getTableRow().getItem();
                if (work != null) {
                    try {
                        String salaryText = textField.getText().replace("₱", "").replace(",", "").trim();
                        if (!salaryText.isEmpty()) {
                            work.setMonthlySalary(Double.parseDouble(salaryText));
                        } else {
                            work.setMonthlySalary(null);
                        }
                        // Refresh the cell text
                        commitEdit(work.getMonthlySalaryDisplay()); 
                        setText(work.getMonthlySalaryDisplay()); 
                    } catch (NumberFormatException e) {
                        showAlert("Error", "Invalid salary format. Please enter a valid number.");
                        // Revert to old value
                        setText(getItem());
                    }
                }
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (!isEmpty()) {
                    WorkExperience work = getTableRow().getItem();
                    if (work != null && work.getMonthlySalary() != null) {
                        // Show raw number for editing (no "₱" or commas)
                        textField.setText(String.format("%.2f", work.getMonthlySalary())); 
                    } else {
                        textField.setText("");
                    }
                    setGraphic(textField);
                    setText(null);
                    textField.requestFocus();
                    textField.selectAll();
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if (isEditing()) {
                        setGraphic(textField);
                        setText(null);
                    } else {
                        setGraphic(null);
                        setText(getString());
                    }
                }
            }

            private String getString() {
                return getItem() == null ? "" : getItem();
            }
        });

        // 4. Government Service Column (Yes/No Dropdown)
        workGovServiceCol.setCellFactory(column -> new TableCell<WorkExperience, String>() {
            private final ChoiceBox<String> choiceBox = new ChoiceBox<>(FXCollections.observableArrayList("Yes", "No"));

            {
                choiceBox.setOnAction(event -> {
                    WorkExperience work = getTableRow().getItem();
                    if (work != null) {
                        boolean isGov = "Yes".equals(choiceBox.getValue());
                        work.setGovernmentService(isGov);
                        // Force the table to refresh and save
                        commitEdit(work.getGovernmentService()); 
                    }
                });
            }

            @Override
            public void startEdit() {
                super.startEdit();
                if (!isEmpty()) {
                    WorkExperience work = getTableRow().getItem();
                    if (work != null) {
                        choiceBox.setValue(work.isGovernmentService() ? "Yes" : "No");
                    }
                    setGraphic(choiceBox);
                    setText(null);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    if (isEditing()) {
                        setGraphic(choiceBox);
                        setText(null);
                    } else {
                        WorkExperience work = getTableRow().getItem();
                        if (work != null) {
                            setText(work.isGovernmentService() ? "Yes" : "No");
                        } else {
                            setText(null);
                        }
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void setupEditableFamilyColumns() {
 
        spouseNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        spouseOccupationCol.setCellFactory(TextFieldTableCell.forTableColumn());
        spouseEmployerCol.setCellFactory(TextFieldTableCell.forTableColumn());
        spouseBusinessAddressCol.setCellFactory(TextFieldTableCell.forTableColumn());
        spouseTelephoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
   
        spouseNameCol.setOnEditCommit(event -> 
            event.getRowValue().setFullName(event.getNewValue()));
        spouseOccupationCol.setOnEditCommit(event -> 
            event.getRowValue().setOccupation(event.getNewValue()));
        spouseEmployerCol.setOnEditCommit(event -> 
            event.getRowValue().setEmployer(event.getNewValue()));
        spouseBusinessAddressCol.setOnEditCommit(event -> 
            event.getRowValue().setBusinessAddress(event.getNewValue()));
        spouseTelephoneCol.setOnEditCommit(event -> 
            event.getRowValue().setTelephone(event.getNewValue()));

        parentNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        parentNameCol.setOnEditCommit(event -> 
            event.getRowValue().setFullName(event.getNewValue()));
        childNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        childDobCol.setCellFactory(TextFieldTableCell.forTableColumn());

        childNameCol.setOnEditCommit(event -> 
            event.getRowValue().setName(event.getNewValue()));
        childDobCol.setOnEditCommit(event -> 
            event.getRowValue().setDateOfBirth(event.getNewValue()));
    }

    public void setEmployeeData(User user) {
        this.currentUser = user;
        loadEmployeeData();
    }

    private void loadEmployeeData() {
        if (currentUser == null) return;

        // Load basic personal information
        surnameField.setText(getDisplayValue(currentUser.getLname()));
        firstNameField.setText(getDisplayValue(currentUser.getFname()));
        middleNameField.setText(getDisplayValue(currentUser.getMname()));
        suffixChoiceBox.setValue(getDisplayValue(currentUser.getSuffix()));
        
        if (currentUser.getBirthDate() != null) {
            birthDatePicker.setValue(currentUser.getBirthDate());
        }
        
        placeOfBirthField.setText(getDisplayValue(currentUser.getPlaceOfBirth()));
        sexChoiceBox.setValue(getDisplayValue(currentUser.getSex()));
        
        if (currentUser.getHeight() != null) {
            heightField.setText(currentUser.getHeight().toString());
        }
        
        civilStatusChoiceBox.setValue(getDisplayValue(currentUser.getCivilStatus()));
        
        if (currentUser.getWeight() != null) {
            weightField.setText(currentUser.getWeight().toString());
        }
        
        bloodTypeChoiceBox.setValue(getDisplayValue(currentUser.getBloodType()));
        citizenshipChoiceBox.setValue(getDisplayValue(currentUser.getCitizenship()));

        // Contact Information
        mobileNoField.setText(getDisplayValue(currentUser.getContactNum()));
        telephoneNoField.setText(getDisplayValue(currentUser.getTelephoneNo()));
        emailField.setText(getDisplayValue(currentUser.getEmail()));

        // Government IDs
        gsisIdField.setText(getDisplayValue(currentUser.getGsisId()));
        pagibigIdField.setText(getDisplayValue(currentUser.getPagibigId()));
        philhealthNoField.setText(getDisplayValue(currentUser.getPhilhealthNo()));
        sssNoField.setText(getDisplayValue(currentUser.getSssNo()));
        tinNoField.setText(getDisplayValue(currentUser.getTinNo()));
        agencyEmployeeNoField.setText(getDisplayValue(currentUser.getAgencyEmployeeNo()));

        // Parse and set addresses
        parseAndSetAddress(currentUser.getResidentialAddress(), true);
        parseAndSetAddress(currentUser.getPermanentAddress(), false);

        // Set image
        if (currentUser.getImage() != null) {
            Image img = ImageUtil.byteArrayToImage(currentUser.getImage());
            if (userImageView != null) userImageView.setImage(img);
            if (userImageViewLarge != null) userImageViewLarge.setImage(img); 
            imageBytes = currentUser.getImage();
        }

        // Load family background
        loadFamilyBackground();
        loadDocuments();
        loadCurrentAssignment();
        
        // Load educational background
        if (currentUser.getEducationalBackgrounds() != null) {
            educationTable.setItems(FXCollections.observableArrayList(currentUser.getEducationalBackgrounds()));
        }
        
        // Load civil service eligibility
        if (currentUser.getCivilServiceEligibilities() != null) {
            eligibilityTable.setItems(FXCollections.observableArrayList(currentUser.getCivilServiceEligibilities()));
        }
        
        // Load work experience
        if (currentUser.getWorkExperiences() != null) {
            workExperienceTable.setItems(FXCollections.observableArrayList(currentUser.getWorkExperiences()));
        }
    }

    private void loadCurrentAssignment() {
        try {
            ObservableList<Assignment> activeAssignments = Assignment.getActiveAssignmentsByUserId(currentUser.getId());
            if (activeAssignments != null && !activeAssignments.isEmpty()) {
                currentAssignment = activeAssignments.get(0);

                // Set department
                boolean departmentFound = false;
                for (Department dept : departmentChoiceBox.getItems()) {
                    if (dept.getId() == currentAssignment.getDepartmentId()) {
                        departmentChoiceBox.setValue(dept);
                        departmentFound = true;
                        break;
                    }
                }
                if (!departmentFound) {
                    departmentChoiceBox.setValue(null);
                }

                // Set position - wait for department to load positions first
                if (departmentFound) {
                    // Small delay to ensure positions are loaded
                    javafx.application.Platform.runLater(() -> {
                        try {
                            boolean positionFound = false;
                            for (Position pos : positionChoiceBox.getItems()) {
                                if (pos.getId() == currentAssignment.getPositionId()) {
                                    positionChoiceBox.setValue(pos);
                                    positionFound = true;
                                    break;
                                }
                            }
                            if (!positionFound) {
                                positionChoiceBox.setValue(null);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }

                // Set shift
                boolean shiftFound = false;
                for (Shift sh : shiftChoiceBox.getItems()) {
                    if (sh.getId() == currentAssignment.getShiftId()) {
                        shiftChoiceBox.setValue(sh);
                        shiftFound = true;
                        break;
                    }
                }
                if (!shiftFound) {
                    shiftChoiceBox.setValue(null);
                }

                // Set time fields
                if (currentAssignment.getStartTime() != null) {
                    startTimeField.setText(formatTimeForInput(currentAssignment.getStartTime().toString()));
                }
                if (currentAssignment.getEndTime() != null) {
                    endTimeField.setText(formatTimeForInput(currentAssignment.getEndTime().toString()));
                }
            } else {
                // No active assignment found
                currentAssignment = null;
                departmentChoiceBox.setValue(null);
                positionChoiceBox.setValue(null);
                shiftChoiceBox.setValue(null);
                startTimeField.setText("");
                endTimeField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load current assignment.");
        }
    }


    private String getDisplayValue(String value) {
        return (value == null || value.trim().isEmpty() || "Select".equals(value)) ? "" : value.trim();
    }

  private void parseAndSetAddress(String address, boolean isResidential) {
        if (address == null || address.trim().isEmpty()) return;

        String[] parts = address.split(",");
        String house = "", street = "", brgy = "", city = "", prov = "", zip = "";

        if (parts.length > 0) house = cleanAddressPart(parts[0]);
        if (parts.length > 1) street = cleanAddressPart(parts[1]);
        if (parts.length > 2) brgy = cleanAddressPart(parts[2]);
        if (parts.length > 3) city = cleanAddressPart(parts[3]);

        if (parts.length == 5) {
            // This is the "bad data" case (e.g., "Province 1234")
            String lastPart = cleanAddressPart(parts[4]);
            int lastSpace = lastPart.lastIndexOf(' ');
            
            // Check if there's a space and the part after it is a number (a zip code)
            if (lastSpace != -1 && lastPart.substring(lastSpace + 1).matches("\\d+")) {
                prov = lastPart.substring(0, lastSpace).trim();
                zip = lastPart.substring(lastSpace + 1).trim();
            } else {
                // No zip code found, just province
                prov = lastPart;
            }
        } else if (parts.length >= 6) {
            // This is the "good data" case (e.g., "Province", "1234")
            prov = cleanAddressPart(parts[4]);
            zip = cleanAddressPart(parts[5]);
        }

        if (isResidential) {
            residentialHouseNoField.setText(house);
            residentialStreetField.setText(street);
            residentialBarangayField.setText(brgy);
            residentialCityField.setText(city);
            residentialProvinceField.setText(prov);
            residentialZipCodeField.setText(zip);
        } else {
            permanentHouseNoField.setText(house);
            permanentStreetField.setText(street);
            permanentBarangayField.setText(brgy);
            permanentCityField.setText(city);
            permanentProvinceField.setText(prov);
            permanentZipCodeField.setText(zip);
        }
    }


    private String cleanAddressPart(String part) {
        if (part == null) return "";
        return part.trim();
    }

    private void loadFamilyBackground() {
        // Load spouse information
        ObservableList<SpouseInfo> spouseList = FXCollections.observableArrayList();
        if (currentUser.getSpouseFirstName() != null && !currentUser.getSpouseFirstName().isEmpty()) {
            String spouseName = formatFullName(currentUser.getSpouseFirstName(), 
                                             currentUser.getSpouseMiddleName(), 
                                             currentUser.getSpouseSurname(), 
                                             currentUser.getSpouseNameExtension());
            
            SpouseInfo spouse = new SpouseInfo(
                spouseName,
                getDisplayValue(currentUser.getSpouseOccupation()),
                getDisplayValue(currentUser.getSpouseEmployer()),
                getDisplayValue(currentUser.getSpouseBusinessAddress()),
                getDisplayValue(currentUser.getSpouseTelephone())
            );
            spouseList.add(spouse);
        }
        spouseTable.setItems(spouseList);

        // Load parents information
        ObservableList<ParentInfo> parentsList = FXCollections.observableArrayList();
        
        if (currentUser.getFatherFirstName() != null && !currentUser.getFatherFirstName().isEmpty()) {
            String fatherName = formatFullName(currentUser.getFatherFirstName(), 
                                             currentUser.getFatherMiddleName(), 
                                             currentUser.getFatherSurname(), 
                                             currentUser.getFatherNameExtension());
            ParentInfo father = new ParentInfo("Father", fatherName);
            parentsList.add(father);
        }
        
        if (currentUser.getMotherFirstName() != null && !currentUser.getMotherFirstName().isEmpty()) {
            String motherName = formatFullName(currentUser.getMotherFirstName(), 
                                             currentUser.getMotherMiddleName(), 
                                             currentUser.getMotherSurname(), 
                                             null);
            ParentInfo mother = new ParentInfo("Mother", motherName);
            parentsList.add(mother);
        }
        parentsTable.setItems(parentsList);

        // Load children information
        ObservableList<ChildInfo> childrenList = FXCollections.observableArrayList();
        if (currentUser.getChildrenInfo() != null && !currentUser.getChildrenInfo().isEmpty()) {
            String[] children = currentUser.getChildrenInfo().split(";;");
            for (String child : children) {
                if (!child.trim().isEmpty()) {
                    String[] parts = child.split("\\|");
                    if (parts.length >= 2) {
                        ChildInfo childInfo = new ChildInfo(parts[0], parts[1]);
                        childrenList.add(childInfo);
                    }
                }
            }
        }
        childrenTable.setItems(childrenList);
    }

    private String formatFullName(String firstName, String middleName, String lastName, String suffix) {
        StringBuilder name = new StringBuilder();
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            name.append(lastName.trim());
        }
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            if (name.length() > 0) name.append(", ");
            name.append(firstName.trim());
        }
        
        if (middleName != null && !middleName.trim().isEmpty()) {
            if (name.length() > 0) name.append(" ");
            name.append(middleName.trim().substring(0, 1).toUpperCase()).append(".");
        }
        
        if (suffix != null && !suffix.trim().isEmpty() && !"None".equals(suffix)) {
            if (name.length() > 0) name.append(" ");
            name.append(suffix.trim());
        }
        
        return name.toString();
    }

    @FXML
    private void saveEmployee(ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        try {
            // Build address strings
            String residentialAddress = buildAddressString(
                    residentialHouseNoField.getText(),
                    residentialStreetField.getText(),
                    residentialBarangayField.getText(),
                    residentialCityField.getText(),
                    residentialProvinceField.getText(),
                    residentialZipCodeField.getText()
            );

            String permanentAddress = buildAddressString(
                    permanentHouseNoField.getText(),
                    permanentStreetField.getText(),
                    permanentBarangayField.getText(),
                    permanentCityField.getText(),
                    permanentProvinceField.getText(),
                    permanentZipCodeField.getText()
            );
            
            // --- FIX: Gather Family Data from Tables ---
            
            // Spouse Data
            String spouseSurname = "", spouseFirstName = "", spouseMiddleName = "", spouseSuffix = "";
            String spouseOccupation = "", spouseEmployer = "", spouseBizAddress = "", spouseTelephone = "";
            
            if (spouseTable.getItems() != null && !spouseTable.getItems().isEmpty()) {
                SpouseInfo spouse = spouseTable.getItems().get(0);
                String[] spouseNames = parseFullName(spouse.getFullName());
                spouseSurname = spouseNames[0];
                spouseFirstName = spouseNames[1];
                spouseMiddleName = spouseNames[2];
                spouseSuffix = spouseNames[3];
                spouseOccupation = spouse.getOccupation();
                spouseEmployer = spouse.getEmployer();
                spouseBizAddress = spouse.getBusinessAddress();
                spouseTelephone = spouse.getTelephone();
            }

            // Parent Data
            String fatherSurname = "", fatherFirstName = "", fatherMiddleName = "", fatherSuffix = "";
            String motherSurname = "", motherFirstName = "", motherMiddleName = "";

            if (parentsTable.getItems() != null) {
                for (ParentInfo parent : parentsTable.getItems()) {
                    String[] parentNames = parseFullName(parent.getFullName());
                    if ("Father".equalsIgnoreCase(parent.getParentType())) {
                        fatherSurname = parentNames[0];
                        fatherFirstName = parentNames[1];
                        fatherMiddleName = parentNames[2];
                        fatherSuffix = parentNames[3];
                    } else if ("Mother".equalsIgnoreCase(parent.getParentType())) {
                        motherSurname = parentNames[0];
                        motherFirstName = parentNames[1];
                        motherMiddleName = parentNames[2];
                    }
                }
            }

            // Children Data
            String childrenInfo = buildChildrenInfoString();
            
            // --- End of Fix ---

            // Update user with all fields, including family data
            User.updateUserWithoutPassword(
                    currentUser.getId(),
                    firstNameField.getText(),
                    middleNameField.getText(),
                    surnameField.getText(),
                    "None".equals(suffixChoiceBox.getValue()) ? null : suffixChoiceBox.getValue(),
                    emailField.getText(),
                    currentUser.getPrivilege(),
                    mobileNoField.getText(),
                    sexChoiceBox.getValue(),
                    birthDatePicker.getValue(),
                    residentialAddress,
                    imageBytes,
                    placeOfBirthField.getText(),
                    civilStatusChoiceBox.getValue(),
                    heightField.getText().isEmpty() ? null : Double.parseDouble(heightField.getText()),
                    weightField.getText().isEmpty() ? null : Double.parseDouble(weightField.getText()),
                    bloodTypeChoiceBox.getValue(),
                    gsisIdField.getText(),
                    pagibigIdField.getText(),
                    philhealthNoField.getText(),
                    sssNoField.getText(),
                    tinNoField.getText(),
                    agencyEmployeeNoField.getText(),
                    citizenshipChoiceBox.getValue(),
                    permanentAddress,
                    telephoneNoField.getText(),
                    
                    // FIX: Pass gathered family data instead of empty strings
                    spouseSurname, spouseFirstName, spouseMiddleName, spouseSuffix, 
                    spouseOccupation, spouseEmployer, spouseBizAddress, spouseTelephone,
                    fatherSurname, fatherFirstName, fatherMiddleName, fatherSuffix,
                    motherSurname, motherFirstName, motherMiddleName,
                    childrenInfo
            );

            // Save related data (education, eligibility, work experience)
            saveRelatedData(currentUser.getId());
            
            updateCurrentAssignment();
            saveDocuments();
            try {
                // Get the admin who is performing the action
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String activityBy = currentAdmin.getPrivilege();
                String adminName = currentAdmin.getFullName();
                
                String lastName = surnameField.getText();
                String firstName = firstNameField.getText();
                String middleName = middleNameField.getText();
                String middleInitial = "";
                
                if (middleName != null && !middleName.trim().isEmpty()) {
                    middleInitial = " " + middleName.trim().substring(0, 1).toUpperCase() + ".";
                }
                
                // Format as: Lastname, Firstname MI.
                String employeeName = lastName + ", " + firstName + middleInitial;
                
                String description = "Updated employee details for: " + employeeName + " (ID: " + currentUser.getAgencyEmployeeNo() + ")";
                ActivityLog.logActivity(activityBy, adminName, description);
            } catch (Exception e) {
                // Don't let logging failure stop the main save operation
                System.err.println("Failed to log 'Update Employee' activity: " + e.getMessage());
            }
            showAlert("Success", "Employee updated successfully!");
            goBack(null);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update employee: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "Please check numeric fields (height, weight) for valid numbers.");
        }
    }

 private void updateCurrentAssignment() throws SQLException {
        // Validate inputs
        if (departmentChoiceBox.getValue() == null || 
            positionChoiceBox.getValue() == null || 
            shiftChoiceBox.getValue() == null ||
            startTimeField.getText().isEmpty() ||
            endTimeField.getText().isEmpty()) {
            System.out.println("Assignment fields incomplete - skipping");
            return;
        }

        // RELOAD CHECK: Try to fetch active assignments
        ObservableList<Assignment> activeAssignments = Assignment.getActiveAssignmentsByUserId(currentUser.getId());

        if (activeAssignments != null && !activeAssignments.isEmpty()) {
            // CASE 1: FOUND! Update the EXISTING assignment
            Assignment existing = activeAssignments.get(0);
            Assignment.updateAssignment(
                existing.getId(), 
                positionChoiceBox.getValue().getId(),
                shiftChoiceBox.getValue().getId(),
                startTimeField.getText(),
                endTimeField.getText()
            );
        } else {
            // CASE 2: NOT FOUND (or creating new history)
            Connection conn = null;
            try {
                conn = DatabaseUtil.getConnection();
                conn.setAutoCommit(false); // Start Transaction

                // STEP A: Force Deactivate ANY existing active assignments for this user
                // This prevents the "Two Employees" duplicate error
                String deactivateQuery = "UPDATE assignment SET status = 0 WHERE user_id = ? AND status = 1";
                try (PreparedStatement ps = conn.prepareStatement(deactivateQuery)) {
                    ps.setInt(1, currentUser.getId());
                    ps.executeUpdate();
                }

                // STEP B: Insert the NEW assignment
                Assignment.addAssignment(
                    conn,
                    currentUser.getId(),
                    positionChoiceBox.getValue().getId(),
                    shiftChoiceBox.getValue().getId(),
                    startTimeField.getText(),
                    endTimeField.getText(),
                    java.time.LocalDate.now().toString()
                );
                
                conn.commit(); // Commit Transaction
            } catch (SQLException e) {
                if(conn != null) conn.rollback();
                throw e;
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
        }
    }
    private boolean isValidTimeFormat(String time) {
        return time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }
    

     private void saveRelatedData(int userId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Clear existing related data
            clearRelatedData(conn, userId);

            // Save Educational Background
            saveEducationalBackground(conn, userId);

            // Save Civil Service Eligibility
            saveCivilServiceEligibility(conn, userId);

            // Save Work Experience
            saveWorkExperience(conn, userId);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
     private void saveEducationalBackground(Connection conn, int userId) throws SQLException {
        String query = "INSERT INTO educational_background (user_id, level, school_name, degree_course, start_date, end_date, units_earned, year_graduated, honors) VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (EducationalBackground edu : educationTable.getItems()) {
                if (edu.getSchoolName() == null || edu.getSchoolName().trim().isEmpty()) {
                    continue; 
                }
                ps.setInt(1, userId);
                ps.setString(2, edu.getLevel());
                ps.setString(3, edu.getSchoolName());
                ps.setString(4, edu.getDegreeCourse());
                ps.setDate(5, edu.getStartDate() != null ? java.sql.Date.valueOf(edu.getStartDate()) : null);
                ps.setDate(6, edu.getEndDate() != null ? java.sql.Date.valueOf(edu.getEndDate()) : null);
                ps.setString(7, edu.getUnitsEarned());
                ps.setString(8, edu.getYearGraduated());
                ps.setString(9, edu.getHonors());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void saveCivilServiceEligibility(Connection conn, int userId) throws SQLException {
        String query = "INSERT INTO civil_service_eligibility (user_id, eligibility_name, rating, exam_date, exam_place, license_number, validity_date) VALUES (?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (CivilServiceEligibility elig : eligibilityTable.getItems()) {
                ps.setInt(1, userId);
                ps.setString(2, elig.getEligibilityName());
                ps.setString(3, elig.getRating());
                ps.setDate(4, elig.getExamDate() != null ? java.sql.Date.valueOf(elig.getExamDate()) : null);
                ps.setString(5, elig.getExamPlace());
                ps.setString(6, elig.getLicenseNumber());
                ps.setDate(7, elig.getValidityDate() != null ? java.sql.Date.valueOf(elig.getValidityDate()) : null);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private void saveWorkExperience(Connection conn, int userId) throws SQLException {
        String query = "INSERT INTO work_experience (user_id, start_date, end_date, position_title, company, monthly_salary, salary_grade, appointment_status, is_government_service) VALUES (?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (WorkExperience work : workExperienceTable.getItems()) {
                ps.setInt(1, userId);
                ps.setDate(2, work.getStartDate() != null ? java.sql.Date.valueOf(work.getStartDate()) : null);
                ps.setDate(3, work.getEndDate() != null ? java.sql.Date.valueOf(work.getEndDate()) : null);
                ps.setString(4, work.getPositionTitle());
                ps.setString(5, work.getCompany());

                if (work.getMonthlySalary() != null) {
                    ps.setDouble(6, work.getMonthlySalary());
                } else {
                    ps.setNull(6, java.sql.Types.DECIMAL);
                }

                ps.setString(7, work.getSalaryGrade());
                ps.setString(8, work.getAppointmentStatus());
                ps.setBoolean(9, work.isGovernmentService());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

     private void clearRelatedData(Connection conn, int userId) throws SQLException {
        String[] queries = {
            "DELETE FROM educational_background WHERE user_id = ?",
            "DELETE FROM civil_service_eligibility WHERE user_id = ?",
            "DELETE FROM work_experience WHERE user_id = ?"
        };

        for (String query : queries) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, userId);
                ps.executeUpdate();
            }
        }
    }


    private void saveFamilyBackground(int userId) throws SQLException {
        // Convert table data to lists and save using User.saveFamilyBackground
        List<SpouseInfo> spouses = new ArrayList<>(spouseTable.getItems());
        List<ParentInfo> parents = new ArrayList<>(parentsTable.getItems());
        List<ChildInfo> children = new ArrayList<>(childrenTable.getItems());

        User.saveFamilyBackground(userId, spouses, parents, children);
    }

    @FXML
    private void cancelEdit(ActionEvent event) {
        goBack(event);
    }

    @FXML
    private void goBack(ActionEvent event) {
        ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
        if (containerCtrl != null) {
            containerCtrl.loadFXML(paneUtil.ADMIN_EMP_MGMT);
        }
    }

    @FXML
    private void selectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        javafx.stage.Window window = ((Node)event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            try {
                // 1. Read file to bytes
                imageBytes = readFileToBytes(selectedFile);

                // 2. Display in BOTH ImageViews
                Image img = ImageUtil.byteArrayToImage(imageBytes);

                if (userImageView != null) userImageView.setImage(img);
                if (userImageViewLarge != null) userImageViewLarge.setImage(img);

            } catch (IOException ex) {
                showAlert("Error", "Failed to read image file: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void addSpouse(ActionEvent event) {
        // Add empty spouse row to table
        spouseTable.getItems().add(new SpouseInfo("", "", "", "", ""));
    }

    @FXML
    private void addParent(ActionEvent event) {
        // Add empty parent row to table
        parentsTable.getItems().add(new ParentInfo("", ""));
    }

    @FXML
    private void addChild(ActionEvent event) {
        // Add empty child row to table
        childrenTable.getItems().add(new ChildInfo("", ""));
    }

    @FXML
    private void addEducation(ActionEvent event) {
        // Add empty education row to table
        educationTable.getItems().add(new EducationalBackground("", "", "", null, null, "", "", ""));
    }

    @FXML
    private void addEligibility(ActionEvent event) {
        // Add empty eligibility row to table
        eligibilityTable.getItems().add(new CivilServiceEligibility("", "", null, "", "", null));
    }

    @FXML
    private void addWorkExperience(ActionEvent event) {
            // Add empty work experience row to table
            workExperienceTable.getItems().add(new WorkExperience(null, null, "", "", null, "", "", false));
        }

        private boolean validateForm() {
        // Basic validation
        if (firstNameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "First name is required.");
            return false;
        }
        if (surnameField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Last name is required.");
            return false;
        }
        if (emailField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Email is required.");
            return false;
        }

        // Optional: Add assignment validation if you want to require assignment
        // Uncomment the following lines if assignment is required
        /*
        if (departmentChoiceBox.getValue() == null || positionChoiceBox.getValue() == null || 
            shiftChoiceBox.getValue() == null || startTimeField.getText().isEmpty() || 
            endTimeField.getText().isEmpty()) {
            showAlert("Validation Error", "All assignment fields are required.");
            return false;
        }

        if (!isValidTimeFormat(startTimeField.getText()) || !isValidTimeFormat(endTimeField.getText())) {
            showAlert("Validation Error", "Please enter valid time in HH:MM format (24-hour).");
            return false;
        }
        */

        return true;
    }

    private String buildAddressString(String houseNo, String street, String barangay, String city, String province, String zipCode) {
        StringBuilder address = new StringBuilder();
        
        if (houseNo != null && !houseNo.trim().isEmpty()) {
            address.append(houseNo.trim());
        }
        
        if (street != null && !street.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(street.trim());
        }
        
        if (barangay != null && !barangay.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(barangay.trim());
        }
        
        if (city != null && !city.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(city.trim());
        }
        
        if (province != null && !province.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(province.trim());
        }
        
        if (zipCode != null && !zipCode.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(zipCode.trim());
        }
        
        return address.toString();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Helper classes for table data
    public static class SpouseInfo {
        private String fullName;
        private String occupation;
        private String employer;
        private String businessAddress;
        private String telephone;

        public SpouseInfo(String fullName, String occupation, String employer, String businessAddress, String telephone) {
            this.fullName = fullName;
            this.occupation = occupation;
            this.employer = employer;
            this.businessAddress = businessAddress;
            this.telephone = telephone;
        }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getOccupation() { return occupation; }
        public void setOccupation(String occupation) { this.occupation = occupation; }
        public String getEmployer() { return employer; }
        public void setEmployer(String employer) { this.employer = employer; }
        public String getBusinessAddress() { return businessAddress; }
        public void setBusinessAddress(String businessAddress) { this.businessAddress = businessAddress; }
        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
    }

    public static class ParentInfo {
        private String parentType;
        private String fullName;

        public ParentInfo(String parentType, String fullName) {
            this.parentType = parentType;
            this.fullName = fullName;
        }

        public String getParentType() { return parentType; }
        public void setParentType(String parentType) { this.parentType = parentType; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

    public static class ChildInfo {
        private String name;
        private String dateOfBirth;

        public ChildInfo(String name, String dateOfBirth) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    }
    private void initializeDocumentsSection() {
        createDocumentRow("Resume");
        createDocumentRow("Offer Letter");
        createDocumentRow("Joining Letter");
        createDocumentRow("Contract & Agreement");
        createDocumentRow("Other");
    }

    private void createDocumentRow(String documentType) {
        HBox documentRow = new HBox(10);
        documentRow.setAlignment(Pos.CENTER_LEFT);
        documentRow.setStyle("-fx-padding: 5; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        Label docLabel = new Label(documentType + " File");
        docLabel.setPrefWidth(150);
        docLabel.setStyle("-fx-font-weight: bold;");

        Button chooseFileBtn = new Button("Choose File");
        chooseFileBtn.setPrefHeight(35.0);
        chooseFileBtn.setMinWidth(100.0);
        chooseFileBtn.setStyle("-fx-font-size: 14px;");

        Label fileNameLabel = new Label("No file chosen");
        fileNameLabel.setPrefWidth(200);

        Button viewButton = new Button("View");
        viewButton.setPrefHeight(35.0);
        viewButton.setMinWidth(80.0);
        viewButton.setStyle("-fx-font-size: 14px;");
        viewButton.setVisible(false);

        Button removeButton = new Button("Remove");
        removeButton.setPrefHeight(35.0);
        removeButton.setMinWidth(80.0);
        removeButton.setStyle("-fx-font-size: 14px;");
        removeButton.setVisible(false);

        chooseFileBtn.setUserData(new Object[]{documentType, fileNameLabel, viewButton, removeButton});
        viewButton.setUserData(new Object[]{documentType, fileNameLabel});
        removeButton.setUserData(new Object[]{documentType, fileNameLabel, viewButton, removeButton});

        chooseFileBtn.setOnAction(e -> {
            Object[] data = (Object[]) chooseFileBtn.getUserData();
            handleDocumentFileSelection((String) data[0], (Label) data[1], (Button) data[2], (Button) data[3]);
        });

        viewButton.setOnAction(e -> {
            Object[] data = (Object[]) viewButton.getUserData();
            viewDocument((String) data[0]);
        });

        removeButton.setOnAction(e -> {
            Object[] data = (Object[]) removeButton.getUserData();
            removeDocument((String) data[0], (Label) data[1], (Button) data[2], (Button) data[3]);
        });

        documentRow.getChildren().addAll(docLabel, chooseFileBtn, fileNameLabel, viewButton, removeButton);
        documentsContainer.getChildren().add(documentRow);
    }

    private void handleDocumentFileSelection(String documentType, Label fileNameLabel, Button viewButton, Button removeButton) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select " + documentType + " File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All Files", "*.*"),
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Word Documents", "*.doc", "*.docx"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                byte[] fileData = readFileToBytes(selectedFile);
                newDocumentFiles.put(documentType, fileData);
                newDocumentFileNames.put(documentType, selectedFile.getName());
                fileNameLabel.setText(selectedFile.getName());
                viewButton.setVisible(true);
                removeButton.setVisible(true);
            } catch (IOException ex) {
                showAlert("Error", "Failed to read file: " + ex.getMessage());
            }
        }
    }

    private byte[] readFileToBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            fis.read(data);
            return data;
        }
    }

    private void viewDocument(String documentType) {
        byte[] fileData = null;
        String fileName = "temp_file";
        String extension = ".tmp";

        // 1. Check if it's a newly added file (in memory)
        if (newDocumentFiles.containsKey(documentType)) {
            fileData = newDocumentFiles.get(documentType);
            String originalName = newDocumentFileNames.get(documentType);
            if (originalName != null) {
                fileName = originalName;
                int dotIndex = originalName.lastIndexOf('.');
                if (dotIndex > 0) {
                    extension = originalName.substring(dotIndex);
                }
            }
        } 
        // 2. If not new, check existing documents (from database)
        else if (currentDocuments.containsKey(documentType)) {
            EmployeeDocument doc = currentDocuments.get(documentType);
            if (doc != null && doc.getFileData() != null) {
                fileData = doc.getFileData();
                fileName = doc.getFileName();
                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    extension = fileName.substring(dotIndex);
                }
            }
        }

        // 3. Open the file if data exists
        if (fileData != null) {
            try {
                // Create temporary file
                File tempFile = File.createTempFile("view_" + documentType.replaceAll("\\s+", ""), extension);
                tempFile.deleteOnExit(); // Auto-delete when Java closes
                
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(fileData);
                }

                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(tempFile);
                } else {
                    showAlert("Error", "Desktop is not supported. Cannot open file.");
                }
            } catch (IOException ex) {
                showAlert("Error", "Failed to open document: " + ex.getMessage());
                ex.printStackTrace();
            }
        } else {
            showAlert("Error", "No document data found to view.");
        }
    }

    private void removeDocument(String documentType, Label fileNameLabel, Button viewButton, Button removeButton) {
        currentDocuments.remove(documentType);
        newDocumentFiles.remove(documentType);
        newDocumentFileNames.remove(documentType);
        fileNameLabel.setText("No file chosen");
        viewButton.setVisible(false);
        removeButton.setVisible(false);
    }

    private void loadDocuments() {
        if (currentUser != null && currentUser.getEmployeeDocuments() != null) {
            for (EmployeeDocument doc : currentUser.getEmployeeDocuments()) {
                if ("Google Drive".equals(doc.getDocumentType())) {
                    googleDriveUrlField.setText(doc.getGoogleDriveUrl());
                } else {
                    currentDocuments.put(doc.getDocumentType(), doc);
                    // Update the UI to show existing documents
                    updateDocumentUI(doc.getDocumentType(), doc.getFileName());
                }
            }
        }
    }

    private void updateDocumentUI(String documentType, String fileName) {
        for (Node node : documentsContainer.getChildren()) {
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                Label docLabel = (Label) row.getChildren().get(0);
                if (docLabel.getText().startsWith(documentType)) {
                    Label fileNameLabel = (Label) row.getChildren().get(2);
                    Button viewButton = (Button) row.getChildren().get(3);
                    Button removeButton = (Button) row.getChildren().get(4);

                    fileNameLabel.setText(fileName);
                    viewButton.setVisible(true);
                    removeButton.setVisible(true);
                    break;
                }
            }
        }
    }

    private void saveDocuments() throws SQLException {
        // Save Google Drive URL
        if (googleDriveUrlField.getText() != null && !googleDriveUrlField.getText().trim().isEmpty()) {
            EmployeeDocument driveDoc = new EmployeeDocument();
            driveDoc.setUserId(currentUser.getId());
            driveDoc.setDocumentType("Google Drive");
            driveDoc.setFileName("Google Drive Link");
            driveDoc.setFileData(null);
            driveDoc.setGoogleDriveUrl(googleDriveUrlField.getText().trim());
            User.saveOrUpdateDocument(driveDoc);
        }

        // Save other documents
        for (String docType : newDocumentFiles.keySet()) {
            EmployeeDocument document = new EmployeeDocument();
            document.setUserId(currentUser.getId());
            document.setDocumentType(docType);
            document.setFileName(newDocumentFileNames.get(docType));
            document.setFileData(newDocumentFiles.get(docType));
            document.setGoogleDriveUrl(null);
            User.saveOrUpdateDocument(document);
        }
    }
    /**
     * Creates a reusable TableCell with a DatePicker for editing LocalDate properties.
     */
    private <T> TableCell<T, String> createDateEditCell(
            java.util.function.Function<T, LocalDate> dateGetter,
            java.util.function.BiConsumer<T, LocalDate> dateSetter,
            java.util.function.Function<T, String> displayGetter) {
        
        return new TableCell<T, String>() {
            private final DatePicker datePicker = new DatePicker();

            {
                datePicker.setOnAction(event -> {
                    T item = getTableRow().getItem();
                    if (item != null) {
                        dateSetter.accept(item, datePicker.getValue());
                        commitEdit(displayGetter.apply(item)); // Commit the new display string
                    }
                });
                
                // Set the converter for the DatePicker to avoid format issues
                datePicker.setConverter(new StringConverter<LocalDate>() {
                    @Override
                    public String toString(LocalDate date) {
                        return (date != null) ? dateFormatter.format(date) : "";
                    }

                    @Override
                    public LocalDate fromString(String string) {
                        return (string != null && !string.isEmpty()) ? LocalDate.parse(string, dateFormatter) : null;
                    }
                });
            }

            @Override
            public void startEdit() {
                if (!isEmpty()) {
                    super.startEdit();
                    T item = getTableRow().getItem();
                    datePicker.setValue(dateGetter.apply(item));
                    setGraphic(datePicker);
                    setText(null);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(getItem());
                    setGraphic(null);
                }
            }
        };
    }

    /**
     * Creates a reusable TableCell with two DatePickers for editing "Period" (start/end date) properties.
     */
    private <T> TableCell<T, String> createPeriodEditCell(
            java.util.function.Function<T, LocalDate> startDateGetter,
            java.util.function.BiConsumer<T, LocalDate> startDateSetter,
            java.util.function.Function<T, LocalDate> endDateGetter,
            java.util.function.BiConsumer<T, LocalDate> endDateSetter,
            java.util.function.Function<T, String> displayGetter) {

        return new TableCell<T, String>() {
            private final DatePicker startDatePicker = new DatePicker();
            private final DatePicker endDatePicker = new DatePicker();
            private final HBox periodBox = new HBox(5, new Label("From:"), startDatePicker, new Label("To:"), endDatePicker);

            {
                periodBox.setAlignment(Pos.CENTER_LEFT);
                
                // When either date changes, update the model and commit the edit
                javafx.event.EventHandler<ActionEvent> dateChangeHandler = event -> {
                    T item = getTableRow().getItem();
                    if (item != null) {
                        startDateSetter.accept(item, startDatePicker.getValue());
                        endDateSetter.accept(item, endDatePicker.getValue());
                        commitEdit(displayGetter.apply(item)); // Commit the new display string
                    }
                };
                
                startDatePicker.setOnAction(dateChangeHandler);
                endDatePicker.setOnAction(dateChangeHandler);
            }

            @Override
            public void startEdit() {
                if (!isEmpty()) {
                    super.startEdit();
                    T item = getTableRow().getItem();
                    startDatePicker.setValue(startDateGetter.apply(item));
                    endDatePicker.setValue(endDateGetter.apply(item));
                    setGraphic(periodBox);
                    setText(null);
                }
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(getItem());
                    setGraphic(null);
                }
            }
        };
    }
    /**
     * Parses the "Last, First M. Suffix" format from the table.
     * This is brittle, but necessary based on the current design.
     */
    private String[] parseFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return new String[]{"", "", "", ""};
        }

        String surname = "", firstName = "", middleName = "", suffix = "";
        String[] parts = fullName.split(",", 2);
        
        surname = parts[0].trim();
        
        if (parts.length > 1) {
            String rest = parts[1].trim();
            
            // Check for suffix
            String[] suffixes = {" Jr.", " Sr.", " II", " III", " IV"};
            for (String s : suffixes) {
                if (rest.endsWith(s)) {
                    suffix = s.trim();
                    rest = rest.substring(0, rest.length() - s.length()).trim();
                    break;
                }
            }
            
            // Check for middle name
            String[] nameParts = rest.split(" ");
            if (nameParts.length > 1) {
                firstName = nameParts[0].trim();
                // Assume last part is middle initial
                String lastPart = nameParts[nameParts.length - 1];
                if (lastPart.length() == 1 || lastPart.endsWith(".")) {
                    middleName = lastPart.replace(".", "").trim();
                } else {
                    // Handle cases like "First Middle"
                    firstName = rest; 
                }
            } else {
                firstName = rest;
            }
        }
        
        return new String[]{surname, firstName, middleName, suffix};
    }

    /**
     * Rebuilds the children_info string from the childrenTable.
     */
    private String buildChildrenInfoString() {
        StringBuilder childrenInfoBuilder = new StringBuilder();
        for (ChildInfo child : childrenTable.getItems()) {
            if (child.getName() != null && !child.getName().trim().isEmpty()) {
                if (childrenInfoBuilder.length() > 0) {
                    childrenInfoBuilder.append(";;");
                }
                childrenInfoBuilder.append(child.getName().trim())
                                 .append("|")
                                 .append(child.getDateOfBirth() != null ? child.getDateOfBirth() : "");
            }
        }
        return childrenInfoBuilder.toString();
    }
    
    private <S> void makeColumnEditable(TableColumn<S, String> column, java.util.function.BiConsumer<S, String> setter) {
        // 1. Define the cell factory
        column.setCellFactory(col -> new TableCell<S, String>() {
            private final TextField textField = new TextField();

            {
                // Commit on Enter key
                textField.setOnAction(e -> commitEdit(textField.getText()));

                // Commit on Focus Loss (Clicking away)
                textField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                    if (!isNowFocused) {
                        commitEdit(textField.getText());
                    }
                });
            }

            @Override
            public void startEdit() {
                super.startEdit();
                textField.setText(getItem());
                setGraphic(textField);
                setText(null);
                textField.requestFocus();
                textField.selectAll();
            }

            @Override
            public void cancelEdit() {
                super.cancelEdit();
                setText(getItem());
                setGraphic(null);
            }

            @Override
            public void commitEdit(String newValue) {
                super.commitEdit(newValue);
                // Update the model immediately
                S rowItem = getTableView().getItems().get(getIndex());
                setter.accept(rowItem, newValue);
                setText(newValue);
                setGraphic(null);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (isEditing()) {
                        if (textField != null) {
                            textField.setText(getItem());
                        }
                        setText(null);
                        setGraphic(textField);
                    } else {
                        setText(getString());
                        setGraphic(null);
                    }
                }
            }

            private String getString() {
                return getItem() == null ? "" : getItem();
            }
        });

        // 2. Keep the standard event handler as a backup
        column.setOnEditCommit(event -> {
            setter.accept(event.getRowValue(), event.getNewValue());
        });
    }
}
