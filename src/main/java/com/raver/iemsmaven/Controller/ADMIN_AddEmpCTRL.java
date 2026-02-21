package com.raver.iemsmaven.Controller;

import com.dlsc.gemsfx.TimePicker;
import com.raver.iemsmaven.Model.Assignment;
import com.raver.iemsmaven.Model.CivilServiceEligibility;
import com.raver.iemsmaven.Model.Department;
import com.raver.iemsmaven.Model.EducationalBackground;
import com.raver.iemsmaven.Model.Position;
import com.raver.iemsmaven.Model.Shift;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Model.WorkExperience;
import com.raver.iemsmaven.Model.EmployeeDocument;
import com.raver.iemsmaven.Utilities.DatabaseUtil;
import com.raver.iemsmaven.Utilities.Filter;
import com.raver.iemsmaven.Utilities.ImageUtil;
import com.raver.iemsmaven.Utilities.Modal;
import com.raver.iemsmaven.Utilities.PaneUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;


public class ADMIN_AddEmpCTRL implements Initializable {

    @FXML private TextField FnameField, LnameField, MnameField;
    @FXML private TextField emailField;
    @FXML private TextField contactNumField;
    @FXML private ChoiceBox<String> userSuffixChoiceBox;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private Button addEmployeeBtn, selectImageBtn;
    @FXML private ImageView userImage;
    @FXML private ChoiceBox<String> privilegeChoiceBox;
    @FXML private ChoiceBox<String> sexChoiceBox;
    
    // CS Form No. 212 fields
    @FXML private TextField placeOfBirthField;
    @FXML private ChoiceBox<String> civilStatusChoiceBox;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private ChoiceBox<String> bloodTypeChoiceBox;
    @FXML private TextField gsisIdField;
    @FXML private TextField pagibigIdField;
    @FXML private TextField philhealthNoField;
    @FXML private TextField sssNoField;
    @FXML private TextField tinNoField;
    @FXML private TextField agencyEmployeeNoField;
    @FXML private ChoiceBox<String> citizenshipChoiceBox;
    @FXML private TextField telephoneNoField;
    
    // Dual citizenship fields
    @FXML private ChoiceBox<String> dualCitizenshipTypeChoiceBox;
    @FXML private TextField dualCitizenshipCountryField;
    @FXML private VBox dualCitizenshipSection;

    // Address fields - REMOVED subdivision fields since they don't exist in FXML
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

    // Family Background fields
    @FXML private TextField spouseSurnameField;
    @FXML private TextField spouseFirstNameField;
    @FXML private TextField spouseMiddleNameField;
    @FXML private ChoiceBox<String> spouseNameExtensionField;
    @FXML private TextField spouseOccupationField;
    @FXML private TextField spouseEmployerField;
    @FXML private TextArea spouseBusinessAddressField;
    @FXML private TextField spouseTelephoneField;

    @FXML private TextField fatherSurnameField;
    @FXML private TextField fatherFirstNameField;
    @FXML private TextField fatherMiddleNameField;
    @FXML private ChoiceBox<String> fatherNameExtensionField;

    @FXML private TextField motherSurnameField;
    @FXML private TextField motherFirstNameField;
    @FXML private TextField motherMiddleNameField;
    
    @FXML private TextArea childrenTextArea;
    @FXML private TextArea educationTextArea;
    
    // Educational Background Fields ---
    @FXML private TextField elemSchoolField, elemDegreeField, elemUnitsField, elemYearGradField, elemHonorsField;
    @FXML private DatePicker elemFromDate, elemToDate;
    @FXML private TextField secSchoolField, secDegreeField, secUnitsField, secYearGradField, secHonorsField;
    @FXML private DatePicker secFromDate, secToDate;
    @FXML private TextField vocSchoolField, vocDegreeField, vocUnitsField, vocYearGradField, vocHonorsField;
    @FXML private DatePicker vocFromDate, vocToDate;
    @FXML private TextField colSchoolField, colDegreeField, colUnitsField, colYearGradField, colHonorsField;
    @FXML private DatePicker colFromDate, colToDate;
    @FXML private TextField gradSchoolField, gradDegreeField, gradUnitsField, gradYearGradField, gradHonorsField;
    @FXML private DatePicker gradFromDate, gradToDate;
    
    // Civil Service & Work Experience Containers ---
    @FXML private VBox eligibilityContainer;
    @FXML private VBox workExperienceContainer;
    
    // Validation labels
    @FXML private Label emailValidationLabel;
    @FXML private Label contactNumValidationLabel;
    @FXML private Label nameValidationLabel;
    
   // Assignment fields
    @FXML private ChoiceBox<Department> departmentChoiceBox;
    @FXML private ChoiceBox<Position> positionChoiceBox;
    @FXML private ChoiceBox<Shift> shiftChoiceBox;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private CheckBox addAssignmentCheckbox;
    
    // Document fields
    @FXML private VBox documentsContainer;
    @FXML private TextField googleDriveUrlField;

    // Document file data
    private Map<String, byte[]> documentFiles = new HashMap<>();
    private Map<String, String> documentFileNames = new HashMap<>();

    @FXML
    private Button backButton;
    @FXML
    private VBox childrenContainer;
    @FXML 
    private Label formTitleLabel;
    @FXML
    private Button importPdsButton;
    private ITesseract tesseract;
    private List<ADMIN_RowChildCTRL> childNodes = new ArrayList<>();
    private List<ADMIN_RowEligibilityCTRL> eligibilityNodes = new ArrayList<>();
    private List<ADMIN_RowWorkExpCTRL> workExperienceNodes = new ArrayList<>();
   @FXML
    private void addChildField() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ADMIN_RowChild.fxml")); // Adjust path if needed
            Node childNode = loader.load();
            ADMIN_RowChildCTRL controller = loader.getController();

            // Pass a reference of this main controller to the row controller
            controller.setMainController(this);

            // Add the loaded node (the HBox) to the container in your UI
            childrenContainer.getChildren().add(childNode);

            // Add the controller to your list
            childNodes.add(controller);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle loading error (e.g., show an alert)
            Modal.alert("Error", "Could not load child input row: " + e.getMessage());
        }
    }

    // --- Add this NEW method to handle removal ---
    public void removeChildController(ADMIN_RowChildCTRL controllerToRemove) {
        if (childNodes != null) {
            childNodes.remove(controllerToRemove);
        }
}
    // --- NEW: Method to Add Civil Service Eligibility Row ---
    @FXML
    private void addEligibilityField(ActionEvent event) {
        // --- FIX: Call the helper method that loads the FXML ---
        // This is the same method the PDS import uses.
        addAndConfigureEligibilityRow();
    }
    
    // --- NEW: Method to Add Work Experience Row ---
    // --- NEW: Method to Add Work Experience Row ---
    @FXML
    private void addWorkExperienceField(ActionEvent event) {
        // --- FIX: Call the helper method that loads the FXML ---
        // This is the same method the PDS import uses.
        addAndConfigureWorkRow();
    }
    @FXML
    private void goBack(ActionEvent event) {
        ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
        if (containerCtrl != null) {
            containerCtrl.loadFXML(paneUtil.ADMIN_EMP_MGMT);
        }
    }

    byte[] imageBytes;
    DatabaseUtil dbMethods = new DatabaseUtil();
    PaneUtil paneUtil = new PaneUtil();
    boolean editMode = false;
    int selectedUserId = 0;
    User selectedUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        tesseract = new Tesseract();
        try {
            tesseract.setDatapath(new File(".").getCanonicalPath() + File.separator + "tessdata");
            tesseract.setLanguage("eng"); // Set language to English
        } catch (IOException e) {
            e.printStackTrace();
            Modal.alert("OCR Error", "Could not initialize Tesseract OCR. 'tessdata' folder not found.");
        }
    
        initializeChoiceBoxes();
        initializeEducationFields();
        

        initializeTableViews();
        initializeAssignmentFields();
        initializeDocumentsSection();
        
        citizenshipChoiceBox.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                updateDualCitizenshipVisibility();
            }
        );

        setupDatePicker();

        updateDualCitizenshipVisibility();

        setupRealTimeValidation();
        
        // --- START: INITIAL ASSIGNMENT SETUP ---
        
        // 1. Populate Department & Shift Dropdowns
        departmentChoiceBox.setItems(Department.getActiveDepartments());
        shiftChoiceBox.setItems(Shift.getActiveShifts());

        // 2. Add Listener: Filter Positions when Department is selected
        departmentChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Fetch positions linked to this department
                positionChoiceBox.setItems(Position.getPositionsByDepartmentId(newVal.getId()));
                positionChoiceBox.setDisable(false);
            } else {
                positionChoiceBox.getItems().clear();
                positionChoiceBox.setDisable(true);
            }
        });

        // 3. Add Listener: Auto-fill Start/End Time when Shift is selected
        shiftChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                startTimeField.setText(newVal.getStartTime());
                endTimeField.setText(newVal.getEndTime());
            }
        });

        // 4. Add Listener: Toggle fields based on Checkbox
        addAssignmentCheckbox.selectedProperty().addListener((obs, oldVal, isSelected) -> {
            toggleAssignmentFields(isSelected);
        });
        
        // Initialize state
        toggleAssignmentFields(addAssignmentCheckbox.isSelected());
    }
    private void toggleAssignmentFields(boolean enable) {
        departmentChoiceBox.setDisable(!enable);
        positionChoiceBox.setDisable(!enable); // Will be re-enabled by dept selection
        shiftChoiceBox.setDisable(!enable);
        startTimeField.setDisable(!enable);
        endTimeField.setDisable(!enable);
        
        // Visual cue (optional opacity change)
        double opacity = enable ? 1.0 : 0.5;
        departmentChoiceBox.getParent().setOpacity(opacity);
    }
    private void initializeAssignmentFields() {
        departmentChoiceBox.setItems(Department.getActiveDepartments()); 

        shiftChoiceBox.setItems(Shift.getActiveShifts()); 

        departmentChoiceBox.setOnAction(event -> {
            Department selectedDept = departmentChoiceBox.getValue();
            if (selectedDept != null) {
                positionChoiceBox.setItems(Position.getPositionsByDepartmentId(selectedDept.getId()));
            } else {
                positionChoiceBox.setItems(FXCollections.observableArrayList());
            }
            positionChoiceBox.setValue(null);
        });

        shiftChoiceBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newShift) -> {
            if (newShift != null) {

                startTimeField.setText(formatTimeForInput(newShift.getStartTime()));
                endTimeField.setText(formatTimeForInput(newShift.getEndTime()));
            }
        });

        startTimeField.setText("08:00");
        endTimeField.setText("17:00");

        // Add time validation
        setupTimeValidation();
    }

    private String formatTimeForInput(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return "";
        if (timeStr.length() >= 5) {
            return timeStr.substring(0, 5);
        }
        return timeStr;
    }
    @FXML
    private void handleImportPDS(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Personal Data Sheet (PDS)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        File selectedFile = fileChooser.showOpenDialog(importPdsButton.getScene().getWindow());

        if (selectedFile != null) {
            // Show a "processing" modal
            Modal.showLoadingModal("Processing PDS...", "Please wait while the system extracts data from the PDF.");

            // Create the background task
            PdfParseTask task = new PdfParseTask(selectedFile);

            // When the task succeeds, close the modal and populate the form
            task.setOnSucceeded(e -> {
                Modal.closeLoadingModal();
                Map<String, String> data = task.getValue();
                if (data != null && !data.isEmpty()) {
                    populateForm(data);
                    Modal.inform("Import Complete", "PDS data has been extracted. Please review and correct any fields before saving.", "");
                } else {
                    Modal.alert("Import Failed", "Could not extract any data from the provided PDF.");
                }
            });
            task.setOnFailed(e -> {
                Modal.closeLoadingModal();
                Modal.alert("Import Error", "An error occurred during OCR processing: " .concat(task.getException().getMessage()));
                task.getException().printStackTrace();
            });

            // Start the task on a new thread
            new Thread(task).start();
        }
    }
    public void removeEligibilityController(ADMIN_RowEligibilityCTRL controllerToRemove) {
        if (eligibilityNodes != null) {
            eligibilityNodes.remove(controllerToRemove);
        }
    }

    public void removeWorkExperienceController(ADMIN_RowWorkExpCTRL controllerToRemove) {
        if (workExperienceNodes != null) {
            workExperienceNodes.remove(controllerToRemove);
        }
    }
    /**
     * 5. THE BACKGROUND TASK FOR EXTRACTING DATA FROM FILLABLE PDF FIELDS
     * Uses exact field names provided by the user from Adobe Acrobat.
     */
    class PdfParseTask extends Task<Map<String, String>> {
        private final File pdfFile;

        public PdfParseTask(File pdfFile) {
            this.pdfFile = pdfFile;
        }

        @Override
        protected Map<String, String> call() throws Exception {
            Map<String, String> data = new HashMap<>();

            try (PDDocument document = PDDocument.load(pdfFile)) {
                PDDocumentCatalog docCatalog = document.getDocumentCatalog();
                PDAcroForm acroForm = docCatalog.getAcroForm();

                if (acroForm == null) {
                    throw new Exception("The provided PDF does not contain fillable form fields.");
                }

                // --- I. PERSONAL INFORMATION ---
                data.put("surname", getFieldValue(acroForm, "2 Surname"));
                data.put("firstname", getFieldValue(acroForm, "2 Firstname"));
                data.put("middlename", getFieldValue(acroForm, "2 MiddleName"));
                data.put("suffix", getFieldValue(acroForm, "2 NameExtension")); // Assuming this is a dropdown or text field

                data.put("birthdate", getFieldValue(acroForm, "3 DATE OF BIRTH")); // Acrobat usually saves dates as text MM/DD/YYYY
                data.put("placeofbirth", getFieldValue(acroForm, "4 PLACE OF BIRTH"));

                // Sex (Check radio button state - Acrobat often uses "Off" or the button's name/export value)
                if ("Yes".equals(getFieldValue(acroForm, "5 SexMale")) || "On".equals(getFieldValue(acroForm, "5 SexMale"))) { // Adjust "Yes"/"On" based on Acrobat export value
                     data.put("sex", "Male");
                } else if ("Yes".equals(getFieldValue(acroForm, "5 SexFemale")) || "On".equals(getFieldValue(acroForm, "5 SexFemale"))) {
                     data.put("sex", "Female");
                } else {
                     data.put("sex", ""); // Neither selected
                }


                // Civil Status (Check radio button state)
                if ("Yes".equals(getFieldValue(acroForm, "6 CivilStatus Single")) || "On".equals(getFieldValue(acroForm, "6 CivilStatus Single"))) {
                    data.put("civilstatus", "Single");
                } else if ("Yes".equals(getFieldValue(acroForm, "6 CivilStatus Married")) || "On".equals(getFieldValue(acroForm, "6 CivilStatus Married"))) {
                    data.put("civilstatus", "Married");
                } else if ("Yes".equals(getFieldValue(acroForm, "6 CivilStatus Widowed")) || "On".equals(getFieldValue(acroForm, "6 CivilStatus Widowed"))) {
                    data.put("civilstatus", "Widowed");
                } else if ("Yes".equals(getFieldValue(acroForm, "6 CivilStatus Separated")) || "On".equals(getFieldValue(acroForm, "6 CivilStatus Separated"))) {
                    data.put("civilstatus", "Separated");
                } else if ("Yes".equals(getFieldValue(acroForm, "6 CivilStatus Others")) || "On".equals(getFieldValue(acroForm, "6 CivilStatus Others"))) {
                    // Note: Your FXML doesn't seem to have 'Other'. Handle as needed.
                    data.put("civilstatus", "Other");
                } else {
                     data.put("civilstatus", ""); // None selected
                }


                data.put("height", getFieldValue(acroForm, "7 Height"));
                data.put("weight", getFieldValue(acroForm, "8 Weight"));
                data.put("bloodtype", getFieldValue(acroForm, "9 BloodType"));

                // Government IDs
                data.put("gsis_id", getFieldValue(acroForm, "10 GSIS ID NO"));
                data.put("pagibig_id", getFieldValue(acroForm, "11 PagIbig ID No"));
                data.put("philhealth_no", getFieldValue(acroForm, "12 PhilHealth No"));
                data.put("sss_no", getFieldValue(acroForm, "13 SSS No"));
                data.put("tin_no", getFieldValue(acroForm, "14 Tin No"));
                data.put("agency_employee_no", getFieldValue(acroForm, "15 AGENCY EMPLOYEE NO"));

                // Citizenship (Check radio button state)
                if ("Yes".equals(getFieldValue(acroForm, "16 Citizenship Filipino")) || "On".equals(getFieldValue(acroForm, "16 Citizenship Filipino"))) {
                    data.put("citizenship", "Filipino");
                } else if ("Yes".equals(getFieldValue(acroForm, "16 Citizenship Dual")) || "On".equals(getFieldValue(acroForm, "16 Citizenship Dual"))) {
                    data.put("citizenship", "Dual Citizenship");
                    // Check Dual Citizenship Type
                    if ("Yes".equals(getFieldValue(acroForm, "16 Citizenship Dual By Birth")) || "On".equals(getFieldValue(acroForm, "16 Citizenship Dual By Birth"))) {
                        data.put("dual_citizenship_type", "By Birth"); // Match your ChoiceBox value
                    } else if ("Yes".equals(getFieldValue(acroForm, "16 Citizenship DualBy Naturalization")) || "On".equals(getFieldValue(acroForm, "16 Citizenship DualBy Naturalization"))) {
                        data.put("dual_citizenship_type", "By Naturalization"); // Match your ChoiceBox value
                    }
                    data.put("dual_citizenship_country", getFieldValue(acroForm, "16 Citizenship Dual IndicateCountry"));
                } else {
                    data.put("citizenship", "");
                }


                // Residential Address (Using #0 for House No as per your list)
                // Note: Subdivision/Village is present in PDF but maybe not in FXML? Add if needed.
                data.put("res_house_no", getFieldValue(acroForm, "17 House/Block/Lot No"));
                data.put("res_street", getFieldValue(acroForm, "17 Street"));
                data.put("res_subdivision", getFieldValue(acroForm, "17 Subdivision/Village")); // Added this - map to FXML if you have it
                data.put("res_barangay", getFieldValue(acroForm, "17 Barangay"));
                data.put("res_city", getFieldValue(acroForm, "17 City/Municipality"));
                data.put("res_province", getFieldValue(acroForm, "17 Province"));
                data.put("res_zip", getFieldValue(acroForm, "17 ZipCode"));

                // Permanent Address (Using #1 for House No as per your list?)
                // Assuming "18 House/Block/Lot No#1" corresponds to permanent, adjust if needed.
                // It seems your list repeats names for section 18. I'm assuming the order matters.
                data.put("perm_house_no", getFieldValue(acroForm, "18 House/Block/Lot No")); // Or "17 House/Block/Lot No#1" if that's permanent? Clarify needed.
                data.put("perm_street", getFieldValue(acroForm, "18 Street"));
                data.put("perm_subdivision", getFieldValue(acroForm, "18 Subdivision/Village")); // Added this
                data.put("perm_barangay", getFieldValue(acroForm, "18 Barangay"));
                data.put("perm_city", getFieldValue(acroForm, "18 City/Municipality"));
                data.put("perm_province", getFieldValue(acroForm, "18 Province"));
                data.put("perm_zip", getFieldValue(acroForm, "18 ZipCode"));


                // Contact Information
                data.put("telephone", getFieldValue(acroForm, "19 TELEPHONE NO"));
                data.put("mobile", getFieldValue(acroForm, "20 MOBILE NO"));
                data.put("email", getFieldValue(acroForm, "21 EMAIL ADDRESS"));

                // --- II. FAMILY BACKGROUND ---
                data.put("spouse_surname", getFieldValue(acroForm, "22 SPOUSES SURNAME"));
                data.put("spouse_firstname", getFieldValue(acroForm, "22 SPOUSES FIRST NAME"));
                data.put("spouse_middlename", getFieldValue(acroForm, "22 SPOUSES MIDDLE NAME"));
                data.put("spouse_suffix", getFieldValue(acroForm, "22 SPOUSES Name Extension"));
                data.put("spouse_occupation", getFieldValue(acroForm, "22 OCCUPATION"));
                data.put("spouse_employer", getFieldValue(acroForm, "22 EMPLOYERBUSINESS NAME"));
                data.put("spouse_business_address", getFieldValue(acroForm, "22 BUSINESS ADDRESS"));
                data.put("spouse_telephone", getFieldValue(acroForm, "22 TELEPHONE NO"));

                data.put("father_surname", getFieldValue(acroForm, "24 FATHERS SURNAME")); 
                data.put("father_firstname", getFieldValue(acroForm, "24 FATHERSFIRST NAME")); 
                data.put("father_middlename", getFieldValue(acroForm, "24 FATHERSMIDDLE NAME")); 
                data.put("father_suffix", getFieldValue(acroForm, "24 Fathers Name Extension"));

                // Mother's Maiden Name (Maiden name is surname BEFORE marriage)
                data.put("mother_maiden_surname", getFieldValue(acroForm, "25 MOTHERS SURNAME")); // This should be the maiden surname
                data.put("mother_firstname", getFieldValue(acroForm, "25 MOTHERS FIRST NAME"));
                data.put("mother_middlename", getFieldValue(acroForm, "25 MOTHERS MIDDLE NAME"));
                // The field "25 MOTHERS MAIDEN NAME" might be redundant or the actual maiden name field - check PDF.

                for (int i = 1; i <= 12; i++) {
                    // Use the exact names from your list
                    String nameFieldName = "23 Name of CHILDREN " + i;
                    String dobFieldName = "23 DATE OF BIRTH " + i;

                    String childName = getFieldValue(acroForm, nameFieldName);
                    String childDob = getFieldValue(acroForm, dobFieldName);

                    // Only store if name exists
                    if (childName != null && !childName.isEmpty()) {
                        data.put("child_name_" + i, childName);
                        data.put("child_dob_" + i, childDob);
                    } else if (i > 1 && data.containsKey("child_name_" + (i-1))) {
                         // Stop if we hit the first empty name after finding previous children
                         break;
                    } else if (childName == null || childName.isEmpty()) {
                        // If the very first child name is empty, still continue maybe? Or break?
                        // Depending on how Acrobat handles empty repeating fields. Let's try breaking.
                         break;
                    }
                }

                // --- III. EDUCATIONAL BACKGROUND --- (Example for Elementary)
                // You'll need to repeat this structure for Secondary, Vocational, College, GradStudies
                data.put("elem_school", getFieldValue(acroForm, "26 Name of School Elementary 1"));
                data.put("elem_degree", getFieldValue(acroForm, "26 Elementary Course")); // Should likely be empty/NA
                data.put("elem_from", getFieldValue(acroForm, "26 Period Of Attendance From Elem")); // Date string
                data.put("elem_to", getFieldValue(acroForm, "26 Period Of Attendance to Elem"));     // Date string
                data.put("elem_units", getFieldValue(acroForm, "26 Level/Units Elem"));
                data.put("elem_year_grad", getFieldValue(acroForm, "26 YearGraduated Elem"));
                data.put("elem_honors", getFieldValue(acroForm, "26 Scholarship Elem")); // PDF Label is Scholarship/Honors
                
                // Secondary
                data.put("sec_school", getFieldValue(acroForm, "26 Name of School Secondary 2"));
                data.put("sec_degree", getFieldValue(acroForm, "26 Secondary Course"));
                data.put("sec_from", getFieldValue(acroForm, "26 Period Of Attendance From Secondary"));
                data.put("sec_to", getFieldValue(acroForm, "26 Period Of Attendance to Secondary"));
                data.put("sec_units", getFieldValue(acroForm, "26 Level/Units Secondary"));
                data.put("sec_year_grad", getFieldValue(acroForm, "26 YearGraduated Secondary"));
                data.put("sec_honors", getFieldValue(acroForm, "26 Scholarship Secondary"));

                // Vocational
                data.put("voc_school", getFieldValue(acroForm, "26 Name of School Vocational 3"));
                data.put("voc_degree", getFieldValue(acroForm, "26 Vocational Course"));
                data.put("voc_from", getFieldValue(acroForm, "26 Period Of Attendance From Vocational"));
                data.put("voc_to", getFieldValue(acroForm, "26 Period Of Attendance to vocational")); // Typo in your list? 'vocational' vs 'Vocational'
                data.put("voc_units", getFieldValue(acroForm, "26 Level/Units Vocational"));
                data.put("voc_year_grad", getFieldValue(acroForm, "26 YearGraduated Vocational"));
                data.put("voc_honors", getFieldValue(acroForm, "26 ScholarshipVocational"));

                // College
                data.put("col_school", getFieldValue(acroForm, "26 Name of School College 4"));
                data.put("col_degree", getFieldValue(acroForm, "26 College Course"));
                data.put("col_from", getFieldValue(acroForm, "26 Period Of Attendance From College"));
                data.put("col_to", getFieldValue(acroForm, "26 Period Of Attendance to college")); // Typo in your list? 'college' vs 'College'
                data.put("col_units", getFieldValue(acroForm, "26 Level/Units COllege"));
                data.put("col_year_grad", getFieldValue(acroForm, "26 YearGraduated College"));
                data.put("col_honors", getFieldValue(acroForm, "26 Scholarship Collage")); // Typo in your list? 'Collage' vs 'College'

                // Graduate Studies
                data.put("grad_school", getFieldValue(acroForm, "26 Name of School GradStudies 5"));
                data.put("grad_degree", getFieldValue(acroForm, "26 GradStudies Course"));
                data.put("grad_from", getFieldValue(acroForm, "26 Period Of Attendance from GradStudies")); // Typo in your list? 'from' vs 'From'
                data.put("grad_to", getFieldValue(acroForm, "26 Period Of Attendance to GradStudies"));
                data.put("grad_units", getFieldValue(acroForm, "26 Level/Units GradStudies"));
                data.put("grad_year_grad", getFieldValue(acroForm, "26 YearGraduated GradStudies"));
                data.put("grad_honors", getFieldValue(acroForm, "26 Scholarship GradStudies"));

                // --- IV. CIVIL SERVICE ELIGIBILITY ---
                // The PDS form has 7 rows for eligibility
                for (int i = 1; i <= 7; i++) {
                    String nameKey = "eligibility_name_" + i;
                    String ratingKey = "eligibility_rating_" + i;
                    String dateKey = "eligibility_date_" + i;
                    String placeKey = "eligibility_place_" + i;
                    String licenseKey = "eligibility_license_" + i;
                    String validityKey = "eligibility_validity_" + i;

                    // Construct the PDF field names based on the extracted pattern
                    String nameField = "27 CAREER SERVICE RA 1080 BOARD BAR UNDER SPECIAL LAWS CES CSEE BARANGAY ELIGIBILITY  DRIVERS LICENSERow" + i;
                    String ratingField = "RATING If ApplicableRow" + i;
                    String dateField = "DATE OF EXAMINATION  CONFERMENTRow" + i;
                    String placeField = "PLACE OF EXAMINATION  CONFERMENTRow" + i;
                    String licenseField = "NUMBERRow" + i; // License NUMBER
                    String validityField = "Date of ValidityRow" + i; // License Validity Date

                    // Extract the values
                    String name = getFieldValue(acroForm, nameField);
                    String rating = getFieldValue(acroForm, ratingField);
                    String date = getFieldValue(acroForm, dateField);
                    String place = getFieldValue(acroForm, placeField);
                    String license = getFieldValue(acroForm, licenseField);
                    String validity = getFieldValue(acroForm, validityField);

                    // Only add to map if the main field (name) has data
                    if (name != null && !name.isEmpty()) {
                        data.put(nameKey, name);
                        data.put(ratingKey, rating);
                        data.put(dateKey, date);
                        data.put(placeKey, place);
                        data.put(licenseKey, license);
                        data.put(validityKey, validity);
                    }
                }


                // --- V. WORK EXPERIENCE ---
                // The PDS form has 28 rows for work experience
                for (int i = 1; i <= 28; i++) {
                    String fromKey = "work_from_" + i;
                    String toKey = "work_to_" + i;
                    String positionKey = "work_position_" + i;
                    String companyKey = "work_company_" + i;
                    String salaryKey = "work_salary_" + i;
                    String gradeKey = "work_grade_" + i;
                    String statusKey = "work_status_" + i;
                    String govtKey = "work_govt_" + i;

                    // Construct the PDF field names
                    String fromField = "FromRow" + i;
                    String toField = "ToRow" + i;
                    String positionField = "POSITION TITLE Write in fullDo not abbreviateRow" + i;
                    String companyField = "DEPARTMENT  AGENCY  OFFICE  COMPANY Write in fullDo not abbreviateRow" + i;
                    String salaryField = "MONTHLY SALARYRow" + i;
                    String gradeField = "SALARY JOB PAY GRADE if applicable STEP Format 000 INCREMENTRow" + i;
                    String statusField = "STATUS OF APPOINTMENTRow" + i;
                    String govtField = "GOVT SERVICE Y NRow" + i; // This will be "Y" or "N"

                    // Extract the values
                    String from = getFieldValue(acroForm, fromField);
                    String to = getFieldValue(acroForm, toField);
                    String position = getFieldValue(acroForm, positionField);
                    String company = getFieldValue(acroForm, companyField);

                    // Only add to map if the main field (position or company) has data
                    if ((position != null && !position.isEmpty()) || (company != null && !company.isEmpty())) {
                        data.put(fromKey, from);
                        data.put(toKey, to);
                        data.put(positionKey, position);
                        data.put(companyKey, company);
                        data.put(salaryKey, getFieldValue(acroForm, salaryField));
                        data.put(gradeKey, getFieldValue(acroForm, gradeField));
                        data.put(statusKey, getFieldValue(acroForm, statusField));
                        data.put(govtKey, getFieldValue(acroForm, govtField));
                    }
                }
                return data;

            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("Failed to read PDF file.", e);
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception("An error occurred during PDF processing.", e);
            }
        }

        /**
         * Helper method to safely get a field's value. Handles nulls.
         */
        private String getFieldValue(PDAcroForm acroForm, String fieldName) {
            PDField field = acroForm.getField(fieldName);
            if (field != null) {
                String value = field.getValueAsString();
                // Checkbox/Radio often return "Off" when not checked, normalize to empty string
                if ("Off".equalsIgnoreCase(value)) {
                    return "";
                }
                return (value != null) ? value.trim() : "";
            } else {
                System.err.println("Warning: PDF Field not found: '" + fieldName + "'");
                return ""; // Return empty string if field doesn't exist
            }
        }
    } // End of PdfParseTask class
    /**
     * Fills the FXML form fields with data extracted from the PDF task.
     */
    private void populateForm(Map<String, String> data) {

        // --- I. PERSONAL INFORMATION ---
        LnameField.setText(data.get("surname"));
        FnameField.setText(data.get("firstname"));
        MnameField.setText(data.get("middlename"));
        userSuffixChoiceBox.setValue(normalizeSuffix(data.get("suffix"))); // normalizeSuffix helper is still useful

        dateOfBirthPicker.setValue(parseDate(data.get("birthdate"))); // parseDate helper still works
        placeOfBirthField.setText(data.get("placeofbirth"));

        // Sex - Check the value from the map
        String sexValue = data.get("sex"); // Will be "Male", "Female", or "" based on task logic
        if ("Male".equals(sexValue) || "Female".equals(sexValue)) {
            sexChoiceBox.setValue(sexValue);
        } else {
            sexChoiceBox.getSelectionModel().clearSelection(); // Clear if unknown/not selected
        }

        // Civil Status - Check value
        String civilStatusValue = data.get("civilstatus"); // Will be "Single", "Married", etc. or ""
        if (!civilStatusValue.isEmpty()) {
            civilStatusChoiceBox.setValue(civilStatusValue); // Assumes PDF values match ChoiceBox items
        } else {
            civilStatusChoiceBox.getSelectionModel().clearSelection();
        }

        heightField.setText(data.get("height"));
        weightField.setText(data.get("weight"));
        bloodTypeChoiceBox.setValue(data.get("bloodtype")); // Assumes PDF value matches ChoiceBox item

        // Government IDs
        gsisIdField.setText(data.get("gsis_id"));
        pagibigIdField.setText(data.get("pagibig_id"));
        philhealthNoField.setText(data.get("philhealth_no"));
        sssNoField.setText(data.get("sss_no"));
        tinNoField.setText(data.get("tin_no"));
        agencyEmployeeNoField.setText(data.get("agency_employee_no"));

        // Citizenship
        String citizenship = data.get("citizenship");
        if (citizenship != null && !citizenship.isEmpty()) {
            citizenshipChoiceBox.setValue(citizenship); // Assumes PDF value ("Filipino", "Dual Citizenship") matches
            if (citizenship.equals("Dual Citizenship")) {
                dualCitizenshipSection.setVisible(true);
                dualCitizenshipSection.setManaged(true);
                // Set Dual Citizenship Type (assumes PDF value matches ChoiceBox item)
                dualCitizenshipTypeChoiceBox.setValue(data.get("dual_citizenship_type"));
                dualCitizenshipCountryField.setText(data.get("dual_citizenship_country"));
            } else {
                dualCitizenshipSection.setVisible(false);
                dualCitizenshipSection.setManaged(false);
                // Clear dual citizenship fields if not applicable
                dualCitizenshipTypeChoiceBox.getSelectionModel().clearSelection();
                dualCitizenshipCountryField.clear();
            }
        } else {
            citizenshipChoiceBox.getSelectionModel().clearSelection();
            dualCitizenshipSection.setVisible(false);
            dualCitizenshipSection.setManaged(false);
        }

        // Addresses
        residentialHouseNoField.setText(data.get("res_house_no"));
        residentialStreetField.setText(data.get("res_street"));
        // TODO: Add FXML field for Subdivision if needed and populate data.get("res_subdivision")
        residentialBarangayField.setText(data.get("res_barangay"));
        residentialCityField.setText(data.get("res_city"));
        residentialProvinceField.setText(data.get("res_province"));
        residentialZipCodeField.setText(data.get("res_zip"));

        permanentHouseNoField.setText(data.get("perm_house_no"));
        permanentStreetField.setText(data.get("perm_street"));
        // TODO: Add FXML field for Subdivision if needed and populate data.get("perm_subdivision")
        permanentBarangayField.setText(data.get("perm_barangay"));
        permanentCityField.setText(data.get("perm_city"));
        permanentProvinceField.setText(data.get("perm_province"));
        permanentZipCodeField.setText(data.get("perm_zip"));

        // Contact Information
        telephoneNoField.setText(data.get("telephone"));
        contactNumField.setText(data.get("mobile"));
        emailField.setText(data.get("email"));

        // --- II. FAMILY BACKGROUND ---
        spouseSurnameField.setText(data.get("spouse_surname"));
        spouseFirstNameField.setText(data.get("spouse_firstname"));
        spouseMiddleNameField.setText(data.get("spouse_middlename"));
        spouseNameExtensionField.setValue(normalizeSuffix(data.get("spouse_suffix")));
        spouseOccupationField.setText(data.get("spouse_occupation"));
        spouseEmployerField.setText(data.get("spouse_employer"));
        spouseBusinessAddressField.setText(data.get("spouse_business_address"));
        spouseTelephoneField.setText(data.get("spouse_telephone"));

        fatherSurnameField.setText(data.get("father_surname"));
        fatherFirstNameField.setText(data.get("father_firstname"));
        fatherMiddleNameField.setText(data.get("father_middlename"));
        fatherNameExtensionField.setValue(normalizeSuffix(data.get("father_suffix")));

        motherSurnameField.setText(data.get("mother_maiden_surname"));
        motherFirstNameField.setText(data.get("mother_firstname"));
        motherMiddleNameField.setText(data.get("mother_middlename"));

        // --- Populate Children (Example for first child) ---
        // This assumes you have dynamically added child fields.
        // If childrenContainer is empty, you need to add the first row.
        // You'll need more robust logic to handle adding/populating multiple children.
        childrenContainer.getChildren().clear();
        if (childNodes != null) {
            childNodes.clear();
        } else {
            // This list should be initialized at the class level, but as a safeguard:
            childNodes = new ArrayList<>();
            System.err.println("Warning: childNodes list was null during population. Initialized new list.");
        }

        // 2. Loop through the maximum number of children possible (1 to 12 based on PDS)
        for (int i = 1; i <= 12; i++) {
            // 3. Construct the keys based on the loop index (from your PdfParseTask)
            String childNameKey = "child_name_" + i;
            String childDobKey = "child_dob_" + i;

            // 4. Get the data from the map
            String childName = data.get(childNameKey);
            String childDobStr = data.get(childDobKey);

            // 5. Only add and populate a row IF data exists for this child number
            if (childName != null && !childName.isEmpty()) {

                // Call the helper to add a row and get its controller
                ADMIN_RowChildCTRL newChildCtrl = addAndConfigureChildRow();

                if (newChildCtrl != null) {
                    // 6. Use the controller's methods to set data directly
                    newChildCtrl.setChildName(childName);
                    newChildCtrl.setBirthDate(parseDate(childDobStr)); // Use your existing date parser
                } else {
                    // Handle error if row creation failed
                    System.err.println("Failed to add child row for data: " + childName);
                    break; // Stop processing further children if one fails
                }
            } else if (i == 1 && (childName == null || childName.isEmpty())) {
                // If the very first child is null or empty, no need to check 2-12.
                break;
            } else if (i > 1 && (childName == null || childName.isEmpty())) {
                 // Stop if we hit the first empty name *after* finding at least one child
                 break;
            }
        }


        // --- Populate Education (Example for Elementary) ---
        elemSchoolField.setText(data.get("elem_school"));
        elemDegreeField.setText(data.get("elem_degree")); // Likely N/A
        elemFromDate.setValue(parseDate(data.get("elem_from")));
        elemToDate.setValue(parseDate(data.get("elem_to")));
        elemUnitsField.setText(data.get("elem_units"));
        elemYearGradField.setText(data.get("elem_year_grad"));
        elemHonorsField.setText(data.get("elem_honors"));
        
        secSchoolField.setText(data.get("sec_school"));
        secDegreeField.setText(data.get("sec_degree"));
        secFromDate.setValue(parseDate(data.get("sec_from"))); // Assuming parseDate handles date strings
        secToDate.setValue(parseDate(data.get("sec_to")));
        secUnitsField.setText(data.get("sec_units"));
        secYearGradField.setText(data.get("sec_year_grad"));
        secHonorsField.setText(data.get("sec_honors"));

        // --- Populate Vocational ---
        vocSchoolField.setText(data.get("voc_school"));
        vocDegreeField.setText(data.get("voc_degree"));
        vocFromDate.setValue(parseDate(data.get("voc_from")));
        vocToDate.setValue(parseDate(data.get("voc_to")));
        vocUnitsField.setText(data.get("voc_units"));
        vocYearGradField.setText(data.get("voc_year_grad"));
        vocHonorsField.setText(data.get("voc_honors"));

        // --- Populate College ---
        colSchoolField.setText(data.get("col_school"));
        colDegreeField.setText(data.get("col_degree"));
        colFromDate.setValue(parseDate(data.get("col_from")));
        colToDate.setValue(parseDate(data.get("col_to")));
        colUnitsField.setText(data.get("col_units"));
        colYearGradField.setText(data.get("col_year_grad"));
        colHonorsField.setText(data.get("col_honors"));

        // --- Populate Graduate Studies ---
        gradSchoolField.setText(data.get("grad_school"));
        gradDegreeField.setText(data.get("grad_degree"));
        gradFromDate.setValue(parseDate(data.get("grad_from")));
        gradToDate.setValue(parseDate(data.get("grad_to")));
        gradUnitsField.setText(data.get("grad_units"));
        gradYearGradField.setText(data.get("grad_year_grad"));
        gradHonorsField.setText(data.get("grad_honors"));

        // --- Populate Eligibility ---
        
        // 1. Clear existing rows
        eligibilityContainer.getChildren().clear();
        if (eligibilityNodes != null) { // Assumes: List<ADMIN_RowEligibilityCTRL> eligibilityNodes;
            eligibilityNodes.clear();
        } else {
            eligibilityNodes = new ArrayList<>(); // Initialize if null
        }

        // 2. Loop through the 7 possible rows
        for (int i = 1; i <= 7; i++) {
            String nameKey = "eligibility_name_" + i;
            String name = data.get(nameKey);

            // 3. Only add a row if the name exists
            if (name != null && !name.isEmpty()) {
                // 4. Call your helper to add the FXML row
                ADMIN_RowEligibilityCTRL newEligCtrl = addAndConfigureEligibilityRow(); // You need to create this helper
                
                if (newEligCtrl != null) {
                    // 5. Populate the row using its controller
                    newEligCtrl.setEligibilityName(name);
                    newEligCtrl.setRating(data.get("eligibility_rating_" + i));
                    newEligCtrl.setExamDate(parseDate(data.get("eligibility_date_" + i)));
                    newEligCtrl.setExamPlace(data.get("eligibility_place_" + i));
                    newEligCtrl.setLicenseNumber(data.get("eligibility_license_" + i));
                    newEligCtrl.setValidityDate(parseDate(data.get("eligibility_validity_" + i)));
                } else {
                    break; // Stop if row creation fails
                }
            } else {
                break; // Stop at the first empty slot
            }
        }


        // --- Populate Work Experience ---

        // 1. Clear existing rows
        workExperienceContainer.getChildren().clear();
        if (workExperienceNodes != null) { // Assumes: List<ADMIN_RowWorkExpCTRL> workExperienceNodes;
            workExperienceNodes.clear();
        } else {
            workExperienceNodes = new ArrayList<>(); // Initialize if null
        }

        // 2. Loop through the 28 possible rows
        for (int i = 1; i <= 28; i++) {
            String positionKey = "work_position_" + i;
            String position = data.get(positionKey);
            String companyKey = "work_company_" + i;
            String company = data.get(companyKey);

            // 3. Only add a row if position or company exists
            if ((position != null && !position.isEmpty()) || (company != null && !company.isEmpty())) {
                // 4. Call your helper to add the FXML row
                ADMIN_RowWorkExpCTRL newWorkCtrl = addAndConfigureWorkRow(); // You need to create this helper

                if (newWorkCtrl != null) {
                    // 5. Populate the row using its controller
                    newWorkCtrl.setStartDate(parseDate(data.get("work_from_" + i)));
                    newWorkCtrl.setEndDate(parseDate(data.get("work_to_" + i)));
                    newWorkCtrl.setPositionTitle(position);
                    newWorkCtrl.setCompany(company);
                    newWorkCtrl.setMonthlySalary(parseSalary(data.get("work_salary_" + i))); // Helper to convert String to Double
                    newWorkCtrl.setSalaryGrade(data.get("work_grade_" + i));
                    newWorkCtrl.setAppointmentStatus(data.get("work_status_" + i));
                    newWorkCtrl.setGovernmentService("Y".equalsIgnoreCase(data.get("work_govt_" + i))); // Convert "Y" to true
                } else {
                    break; // Stop if row creation fails
                }
            } else {
                break; // Stop at the first empty slot
            }
        }
    }

    private LocalDate parseDate(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        // PDS format is MM/DD/YYYY.
        // Remove all non-numeric/slash characters that OCR might add.
        String cleanText = text.replaceAll("[^0-9/]", "");
        
        // Define possible formats OCR might see
        DateTimeFormatter format1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter format2 = DateTimeFormatter.ofPattern("M/d/yyyy");

        try {
            // Try the standard format first
            return LocalDate.parse(cleanText, format1);
        } catch (DateTimeParseException e1) {
            try {
                // Try a more lenient format
                return LocalDate.parse(cleanText, format2);
            } catch (DateTimeParseException e2) {
                System.err.println("Could not parse date: " + text);
                return null;
            }
        }
    }
    private String normalizeSuffix(String text) {
        if (text == null || text.isEmpty()) {
            return "None"; 
        }
        
        String cleanText = text.toUpperCase().replaceAll("[^A-Z]", "");
        
        switch (cleanText) {
            case "JR":
                return "Jr.";
            case "SR":
                return "Sr.";
            case "I":
                return "I";
            case "II":
                return "II";
            case "III":
                return "III";
            // Add any others you have
            default:
                return "None";
        }
    }

    private void setupTimeValidation() {
        // Add listeners to validate time format
        startTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTimeField(startTimeField, newValue);
        });

        endTimeField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTimeField(endTimeField, newValue);
        });
    }

    private void validateTimeField(TextField field, String value) {
        if (value == null || value.isEmpty()) {
            setFieldValidation(field, false, "Time is required");
            return;
        }

        // Validate time format (HH:MM in 24-hour format)
        if (!value.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            setFieldValidation(field, false, "Invalid time format. Use HH:MM (24-hour)");
        } else {
            setFieldValidation(field, true, "Valid time format");
        }
    }
    private void initializeChoiceBoxes() {
        // User suffix
        userSuffixChoiceBox.setValue("None");
        userSuffixChoiceBox.getItems().addAll("None", "Jr.", "Sr.", "II", "III", "IV");

        spouseNameExtensionField.setValue("None");
        spouseNameExtensionField.getItems().addAll("None", "Jr.", "Sr.", "II", "III", "IV");

        // Father name extension
        fatherNameExtensionField.setValue("None");
        fatherNameExtensionField.getItems().addAll("None", "Jr.", "Sr.", "II", "III", "IV");

        // Privilege - ONLY EMPLOYEE for new accounts
        privilegeChoiceBox.setValue("Employee");
        privilegeChoiceBox.getItems().addAll("Employee"); // Removed "Admin"

        // Sex - set to null instead of "Select"
        sexChoiceBox.setValue(null);
        sexChoiceBox.getItems().addAll(null, "Male", "Female");

        // Civil Status - set to null instead of "Select"
        civilStatusChoiceBox.setValue(null);
        civilStatusChoiceBox.getItems().addAll(null, "Single", "Married", "Widowed", "Separated", "Other/s:");

        // Blood Type - set to null instead of "Select"
        bloodTypeChoiceBox.setValue(null);
        bloodTypeChoiceBox.getItems().addAll(null, "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

        // Citizenship - set to null instead of "Select"
        citizenshipChoiceBox.setValue(null);
        citizenshipChoiceBox.getItems().addAll(null, "Filipino", "Dual Citizenship");

        // Dual Citizenship Type
         if (dualCitizenshipTypeChoiceBox != null) {
            dualCitizenshipTypeChoiceBox.setValue("Select");
            dualCitizenshipTypeChoiceBox.getItems().addAll("Select", "by birth", "by naturalization");
        }
    }
    
    private void initializeTableViews() {
       // Your existing table view initialization
    }

    private void setupDatePicker() {
        // Set date format to match CS Form (MM/dd/yyyy)
        dateOfBirthPicker.setPromptText("mm/dd/yyyy");
        
        // Create a converter to display date in MM/dd/yyyy format
        dateOfBirthPicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
    }

    private void updateDualCitizenshipVisibility() {
          if (dualCitizenshipSection == null) return; // Add null check
        String citizenship = citizenshipChoiceBox.getValue();
        if ("Dual Citizenship".equals(citizenship)) {
            dualCitizenshipSection.setVisible(true);
            dualCitizenshipSection.setManaged(true);
        } else {
            dualCitizenshipSection.setVisible(false);
            dualCitizenshipSection.setManaged(false);
            // Reset dual citizenship fields when hidden
            if (dualCitizenshipTypeChoiceBox != null) {
                dualCitizenshipTypeChoiceBox.setValue("Select");
            }
            if (dualCitizenshipCountryField != null) {
                dualCitizenshipCountryField.clear();
            }
        }
        
    }
    private boolean validateAssignmentFields() {
        if (!addAssignmentCheckbox.isSelected()) {
            return true; // Assignment is optional
        }

        if (departmentChoiceBox.getValue() == null) {
            showModal("Validation Error", "Please select a department for the assignment");
            return false;
        }

        if (positionChoiceBox.getValue() == null) {
            showModal("Validation Error", "Please select a position for the assignment");
            return false;
        }

        if (shiftChoiceBox.getValue() == null) {
            showModal("Validation Error", "Please select a shift for the assignment");
            return false;
        }

        if (startTimeField.getText() == null || startTimeField.getText().trim().isEmpty() ||
            endTimeField.getText() == null || endTimeField.getText().trim().isEmpty()) {
            showModal("Validation Error", "Please set both start and end times");
            return false;
        }

        // Validate time format
        if (!startTimeField.getText().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$") ||
            !endTimeField.getText().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showModal("Validation Error", "Please use valid time format (HH:MM in 24-hour format)");
            return false;
        }

        // Parse times to check if start is before end
        try {
            LocalTime startTime = LocalTime.parse(startTimeField.getText());
            LocalTime endTime = LocalTime.parse(endTimeField.getText());

            if (startTime.isAfter(endTime)) {
                showModal("Validation Error", "Start time cannot be after end time");
                return false;
            }
        } catch (Exception e) {
            showModal("Validation Error", "Invalid time format. Please use HH:MM format");
            return false;
        }

        return true;
    }
    // ===== REAL-TIME VALIDATION =====
   private void setupRealTimeValidation() {
        // Email validation
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmail(newValue);
        });

        // Contact number validation - FIXED: Better regex and validation logic
        contactNumField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateContactNumber(newValue);
        });

        // Name validation
        FnameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRequiredField(FnameField, newValue, "First Name");
        });

        LnameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateRequiredField(LnameField, newValue, "Last Name");
        });

        // Numeric field validation
        heightField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNumericField(heightField, newValue, "Height");
        });

        weightField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNumericField(weightField, newValue, "Weight");
        });
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            setFieldValidation(emailField, false, "Email is required");
        } else if (!isValidEmail(email)) {
            setFieldValidation(emailField, false, "Invalid email format");
        } else {
            setFieldValidation(emailField, true, "Valid email");
        }
    }

    private void validateContactNumber(String contactNum) {
        if (contactNum == null || contactNum.isEmpty()) {
            setFieldValidation(contactNumField, false, "Contact number is required");
        } else if (!contactNum.matches("^[0-9+\\-\\(\\)\\s]{7,15}$")) {
            setFieldValidation(contactNumField, false, "Invalid contact number format. Use numbers, +, -, (, ) or spaces");
        } else {
            // Additional check: must contain at least 7 digits
            String digitsOnly = contactNum.replaceAll("[^0-9]", "");
            if (digitsOnly.length() < 7) {
                setFieldValidation(contactNumField, false, "Contact number must have at least 7 digits");
            } else if (digitsOnly.length() > 15) {
                setFieldValidation(contactNumField, false, "Contact number too long");
            } else {
                setFieldValidation(contactNumField, true, "Valid contact number");
            }
        }
    }

    private void validateRequiredField(TextField field, String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            setFieldValidation(field, false, fieldName + " is required");
        } else {
            setFieldValidation(field, true, "Valid " + fieldName.toLowerCase());
        }
    }

    private void validateNumericField(TextField field, String value, String fieldName) {
        if (value != null && !value.isEmpty()) {
            try {
                // Allow decimal numbers
                if (value.matches("-?\\d+(\\.\\d+)?")) {
                    double numValue = Double.parseDouble(value);
                    if (numValue >= 0) { // Assuming height/weight can't be negative
                        setFieldValidation(field, true, "Valid " + fieldName.toLowerCase());
                    } else {
                        setFieldValidation(field, false, fieldName + " cannot be negative");
                    }
                } else {
                    setFieldValidation(field, false, fieldName + " must be a valid number");
                }
            } catch (NumberFormatException e) {
                setFieldValidation(field, false, fieldName + " must be a valid number");
            }
        } else {
            // Clear validation when field is empty
            clearFieldValidation(field);
        }
    }
    private void clearFieldValidation(TextField field) {
        if (field != null) {
            field.getStyleClass().removeAll("error", "success");
            field.setTooltip(null);
        }
    }

       private void setFieldValidation(TextField field, boolean isValid, String message) {
        if (field == null) return;

        // First, remove any existing validation styles
        field.getStyleClass().removeAll("error", "success");

        // Add the appropriate style
        if (isValid) {
            field.getStyleClass().add("success");
        } else {
            field.getStyleClass().add("error");
        }

        // Create and set tooltip
        Tooltip tooltip = new Tooltip(message);
        if (isValid) {
            tooltip.setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; -fx-font-size: 12px;");
        } else {
            tooltip.setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-size: 12px;");
        }
        field.setTooltip(tooltip);

        // Force CSS update
        field.applyCss();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    // ===== EXISTING METHODS (UPDATED) =====
    private String buildChildrenInfo() {
        // If dynamic child rows exist, build from them
        if (childNodes != null && !childNodes.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ADMIN_RowChildCTRL c : childNodes) {
                String name = c.getChildName();
                java.time.LocalDate dob = c.getBirthDate();
                if ((name != null && !name.trim().isEmpty()) || dob != null) {
                    if (sb.length() > 0) sb.append(";");
                    sb.append(name != null ? name.trim() : "");
                    sb.append("|");
                    sb.append(dob != null ? dob.toString() : "");
                }
            }
            String out = sb.toString().trim();
            return out.isEmpty() ? null : out;
        }

        // Fallback: if you also allow free text in childrenTextArea, use that
        if (childrenTextArea != null && !childrenTextArea.getText().trim().isEmpty()) {
            return childrenTextArea.getText().trim();
        }

        return null;
    }
    @FXML
    private void addEmployee(ActionEvent event) throws IOException {
        // --- 1. Validation Phase ---
        if (!validateAllFields()) {
            return;
        }
        
        // Validate Initial Assignment (Only if checked)
        if (addAssignmentCheckbox.isSelected()) {
            if (departmentChoiceBox.getValue() == null || 
                positionChoiceBox.getValue() == null || 
                shiftChoiceBox.getValue() == null) {
                
                showModal("Validation Error", "Please select a Department, Position, and Shift for the initial assignment.");
                return;
            }
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // --- 2. Gather Data into Lists of Objects ---

            // Create Educational Background List
            List<EducationalBackground> educations = new ArrayList<>();
            // Elementary
            if (!elemSchoolField.getText().isBlank()) {
                educations.add(new EducationalBackground("Elementary", elemSchoolField.getText(), elemDegreeField.getText(), 
                    elemFromDate.getValue(), elemToDate.getValue(), elemUnitsField.getText(), 
                    elemYearGradField.getText(), elemHonorsField.getText()));
            }
            // Secondary
            if (!secSchoolField.getText().isBlank()) {
                educations.add(new EducationalBackground("Secondary", secSchoolField.getText(), secDegreeField.getText(), 
                    secFromDate.getValue(), secToDate.getValue(), secUnitsField.getText(), 
                    secYearGradField.getText(), secHonorsField.getText()));
            }
            // Vocational
            if (!vocSchoolField.getText().isBlank()) {
                educations.add(new EducationalBackground("Vocational", vocSchoolField.getText(), vocDegreeField.getText(), 
                    vocFromDate.getValue(), vocToDate.getValue(), vocUnitsField.getText(), 
                    vocYearGradField.getText(), vocHonorsField.getText()));
            }
            // College
            if (!colSchoolField.getText().isBlank()) {
                educations.add(new EducationalBackground("College", colSchoolField.getText(), colDegreeField.getText(), 
                    colFromDate.getValue(), colToDate.getValue(), colUnitsField.getText(), 
                    colYearGradField.getText(), colHonorsField.getText()));
            }
            // Graduate
            if (!gradSchoolField.getText().isBlank()) {
                educations.add(new EducationalBackground("Graduate Studies", gradSchoolField.getText(), gradDegreeField.getText(), 
                    gradFromDate.getValue(), gradToDate.getValue(), gradUnitsField.getText(), 
                    gradYearGradField.getText(), gradHonorsField.getText()));
            }

            // Create Civil Service Eligibility List
            List<CivilServiceEligibility> eligibilities = new ArrayList<>();
            if (eligibilityNodes != null) {
                for (ADMIN_RowEligibilityCTRL controller : eligibilityNodes) {
                    String name = controller.getEligibilityName();
                    if (name != null && !name.trim().isEmpty()) {
                        eligibilities.add(new CivilServiceEligibility(
                            name, controller.getRating(), controller.getExamDate(),
                            controller.getExamPlace(), controller.getLicenseNumber(), controller.getValidityDate()
                        ));
                    }
                }
            }

            // Create Work Experience List
            List<WorkExperience> experiences = new ArrayList<>();
            if (workExperienceNodes != null) {
                for (ADMIN_RowWorkExpCTRL controller : workExperienceNodes) {
                    String position = controller.getPositionTitle();
                    String company = controller.getCompany();
                    if ((position != null && !position.trim().isEmpty()) || (company != null && !company.trim().isEmpty())) {
                        experiences.add(new WorkExperience(
                            controller.getStartDate(), controller.getEndDate(), position, company,
                            controller.getMonthlySalary(), controller.getSalaryGrade(),
                            controller.getAppointmentStatus(), controller.isGovernmentService()
                        ));
                    }
                }
            }

            // --- 3. Create the Main User Object ---

            // Collect children info
            StringBuilder childrenInfoBuilder = new StringBuilder();
            if (childNodes != null && !childNodes.isEmpty()) {
                for (ADMIN_RowChildCTRL childCtrl : childNodes) {
                    try {
                        String name = childCtrl.getChildName();
                        LocalDate dob = childCtrl.getBirthDate();
                        if (name != null && !name.trim().isEmpty()) {
                            if (childrenInfoBuilder.length() > 0) childrenInfoBuilder.append(";");
                            childrenInfoBuilder.append(name.trim()).append("|").append(dob != null ? dob.toString() : "");
                        }
                    } catch (Exception e) {
                        System.err.println("Error reading child row: " + e.getMessage());
                    }
                }
            }
            // Fallback for text area
            if (childrenInfoBuilder.length() == 0 && childrenTextArea != null && !childrenTextArea.getText().trim().isEmpty()) {
                childrenInfoBuilder.append(childrenTextArea.getText().trim());
            }

            // Handle Nullable Dropdowns
            String bloodType = "Select".equals(bloodTypeChoiceBox.getValue()) ? null : bloodTypeChoiceBox.getValue();
            String civilStatus = "Select".equals(civilStatusChoiceBox.getValue()) ? null : civilStatusChoiceBox.getValue();
            String sex = "Select".equals(sexChoiceBox.getValue()) ? null : sexChoiceBox.getValue();
            
            // Handle Citizenship
            String citizenship = citizenshipChoiceBox.getValue();
            if (!"Dual Citizenship".equals(citizenship)) {
                if (dualCitizenshipTypeChoiceBox != null) dualCitizenshipTypeChoiceBox.setValue("Select");
                if (dualCitizenshipCountryField != null) dualCitizenshipCountryField.clear();
            }

            String childrenInfo = childrenInfoBuilder.length() == 0 ? null : childrenInfoBuilder.toString();

            // Create User Instance
            User newUser = new User(
                imageBytes, emailField.getText(), "defaultPassword123", privilegeChoiceBox.getValue(),
                FnameField.getText(), MnameField.getText(), LnameField.getText(), userSuffixChoiceBox.getValue(),
                sex, dateOfBirthPicker.getValue(), contactNumField.getText(),
                buildAddressString(residentialHouseNoField.getText(), residentialStreetField.getText(), null,
                    residentialBarangayField.getText(), residentialCityField.getText(), residentialProvinceField.getText(), residentialZipCodeField.getText()),
                placeOfBirthField.getText(), civilStatus,
                heightField.getText().isEmpty() ? null : Double.parseDouble(heightField.getText()),
                weightField.getText().isEmpty() ? null : Double.parseDouble(weightField.getText()),
                bloodType, gsisIdField.getText(), pagibigIdField.getText(), philhealthNoField.getText(),
                sssNoField.getText(), tinNoField.getText(), agencyEmployeeNoField.getText(), citizenship,
                buildAddressString(permanentHouseNoField.getText(), permanentStreetField.getText(), null,
                    permanentBarangayField.getText(), permanentCityField.getText(), permanentProvinceField.getText(), permanentZipCodeField.getText()),
                telephoneNoField.getText(),
                spouseSurnameField.getText(), spouseFirstNameField.getText(), spouseMiddleNameField.getText(),
                spouseNameExtensionField.getValue(), spouseOccupationField.getText(), spouseEmployerField.getText(),
                spouseBusinessAddressField.getText(), spouseTelephoneField.getText(),
                fatherSurnameField.getText(), fatherFirstNameField.getText(), fatherMiddleNameField.getText(),
                fatherNameExtensionField.getValue(),
                motherSurnameField.getText(), motherFirstNameField.getText(), motherMiddleNameField.getText(),
                childrenInfo
            );

            int userId;

            // --- 4. Database Insertion Logic ---
            if (editMode) {
                // Update Logic (Excluded assignment creation to prevent overwriting existing history)
                saveDocuments(selectedUserId);
                User.updateUserWithoutPassword(
                    selectedUserId,
                    FnameField.getText(), MnameField.getText(), LnameField.getText(), userSuffixChoiceBox.getValue(),
                    emailField.getText(), privilegeChoiceBox.getValue(), contactNumField.getText(), sex,
                    dateOfBirthPicker.getValue(),
                    buildAddressString(residentialHouseNoField.getText(), residentialStreetField.getText(), null,
                        residentialBarangayField.getText(), residentialCityField.getText(), residentialProvinceField.getText(), residentialZipCodeField.getText()),
                    imageBytes,
                    placeOfBirthField.getText(), civilStatus,
                    heightField.getText().isEmpty() ? null : Double.parseDouble(heightField.getText()),
                    weightField.getText().isEmpty() ? null : Double.parseDouble(weightField.getText()),
                    bloodType, gsisIdField.getText(), pagibigIdField.getText(), philhealthNoField.getText(),
                    sssNoField.getText(), tinNoField.getText(), agencyEmployeeNoField.getText(), citizenship,
                    buildAddressString(permanentHouseNoField.getText(), permanentStreetField.getText(), null,
                        permanentBarangayField.getText(), permanentCityField.getText(), permanentProvinceField.getText(), permanentZipCodeField.getText()),
                    telephoneNoField.getText(),
                    spouseSurnameField.getText(), spouseFirstNameField.getText(), spouseMiddleNameField.getText(),
                    spouseNameExtensionField.getValue(), spouseOccupationField.getText(), spouseEmployerField.getText(),
                    spouseBusinessAddressField.getText(), spouseTelephoneField.getText(),
                    fatherSurnameField.getText(), fatherFirstNameField.getText(), fatherMiddleNameField.getText(),
                    fatherNameExtensionField.getValue(),
                    motherSurnameField.getText(), motherFirstNameField.getText(), motherMiddleNameField.getText(),
                    childrenInfo
                );
                showModal("Success", "User Updated Successfully!");
                
            } else {
                // Add New User Logic
                User.addUser(newUser, educations, eligibilities, experiences);
                
                // Get the ID of the user just created
                // Note: User.getNextUserId() logic might be fragile in high concurrency. 
                // Better to fetch by unique Agency ID if possible, but keeping your logic for consistency.
                userId = User.getNextUserId() - 1; 
                saveDocuments(userId);

                // *** NEW: Add Initial Assignment ***
                if (addAssignmentCheckbox.isSelected()) {
                    int posId = positionChoiceBox.getValue().getId();
                    int shiftId = shiftChoiceBox.getValue().getId();
                    
                    // Use text fields if manually entered, else use Shift defaults
                    String start = startTimeField.getText().isEmpty() ? 
                                   shiftChoiceBox.getValue().getStartTime() : startTimeField.getText();
                    String end = endTimeField.getText().isEmpty() ? 
                                 shiftChoiceBox.getValue().getEndTime() : endTimeField.getText();
                    
                    String today = java.time.LocalDate.now().toString();

                    // Call the Assignment model to insert using the SAME connection (for transaction safety)
                    Assignment.addAssignment(conn, userId, posId, shiftId, start, end, today);
                }
                // **********************************

                // Activity Logging
                try {
                    User currentUser = Session.getInstance().getLoggedInUser(); 
                    String activityBy = currentUser.getPrivilege(); 
                    String userName = currentUser.getFullName(); 
                    
                    String employeeName = LnameField.getText() + ", " + FnameField.getText();
                    String description = "Added new employee: " + employeeName + " (ID: " + newUser.getAgencyEmployeeNo() + ")"; 
                    ActivityLog.logActivity(activityBy, userName, description); 
                } catch (Exception e) {
                    System.err.println("Failed to log 'Add Employee' activity: " + e.getMessage());
                }
                
                showModal("Success", "Employee added successfully!");
            }

            conn.commit(); // Commit all changes (User + Assignment)

            // Return to employee management screen
            ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
            if (containerCtrl != null) {
                containerCtrl.loadFXML(paneUtil.ADMIN_EMP_MGMT);
            }

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            showModal("Database Error", "Failed to save data: " + e.getMessage());
        } catch (NumberFormatException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            showModal("Validation Error", "Please check numeric fields (height, weight) for valid numbers.");
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void saveInitialAssignment() throws SQLException {
        // 1. Retrieve the User ID of the person we just added
        // We look them up by the Agency Employee No (which must be unique)
        String agencyNo = agencyEmployeeNoField.getText().trim();
        int newUserId = getUserIdByAgencyNo(agencyNo);

        if (newUserId == -1) {
            System.err.println("Error: Could not find newly created user for assignment.");
            return;
        }

        // 2. Get Selected IDs
        int deptId = departmentChoiceBox.getValue().getId(); // Not used directly in assignment table but useful for logic
        int posId = positionChoiceBox.getValue().getId();
        int shiftId = shiftChoiceBox.getValue().getId();

        // 3. Get Times (Use text fields in case admin manually overrode the defaults)
        String start = startTimeField.getText().trim();
        String end = endTimeField.getText().trim();
        String dateAssigned = java.time.LocalDate.now().toString();

        // 4. Save to Database
        try (java.sql.Connection conn = DatabaseUtil.getConnection()) {
            Assignment.addAssignment(conn, newUserId, posId, shiftId, start, end, dateAssigned);
            System.out.println("Initial assignment created for User ID: " + newUserId);
        }
    }

    // Helper to fetch ID (Add this to your Controller if not already present in User model)
    private int getUserIdByAgencyNo(String agencyNo) {
        String query = "SELECT user_id FROM user WHERE agency_employee_no = ?";
        try (java.sql.Connection conn = DatabaseUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, agencyNo);
            java.sql.ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Not found
    }

    private void addInitialAssignment(Connection conn, int userId) throws SQLException {
        Department selectedDept = departmentChoiceBox.getValue();
        Position selectedPos = positionChoiceBox.getValue();
        Shift selectedShift = shiftChoiceBox.getValue();

        if (selectedDept != null && selectedPos != null && selectedShift != null) {
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String dateAssigned = currentDate.format(formatter);

            // Check if position already exists (for overload shifts)
            boolean positionExists = Assignment.positionAlreadyExists(userId, selectedPos.getId());

            if (positionExists && !selectedShift.getShiftName().toLowerCase().contains("overload")) {
                showModal("Assignment Warning", "This position is already assigned to the user. Consider using an overload shift.");
                return;
            }

            // Check for overlapping assignments
            boolean isOverlapping = false;
            ObservableList<Assignment> assignments = Assignment.getActiveAssignmentsByUserId(userId);
            for (Assignment assignment : assignments) {
                if (Filter.TIME.isOverlapping(assignment.getStartTime() + "", assignment.getEndTime() + "", startTime, endTime)) {
                    isOverlapping = true;
                    break;
                }
            }

            if (isOverlapping) {
                if (Modal.actionConfirmed("Overlapping Assignment", "Overlapping assignment detected. Continue?", "This will add an overlapping assignment")) {
                    Assignment.addAssignment(conn, userId, selectedPos.getId(), selectedShift.getId(), startTime, endTime, dateAssigned);
                }
            } else {
                Assignment.addAssignment(conn, userId, selectedPos.getId(), selectedShift.getId(), startTime, endTime, dateAssigned);
            }
        }
    }
private TextField findTextFieldInGridPane(GridPane gridPane, int childIndex) {
    if (childIndex < gridPane.getChildren().size()) {
        Node node = gridPane.getChildren().get(childIndex);
        return node instanceof TextField ? (TextField) node : null;
    }
    return null;
}

private DatePicker findDatePickerInGridPane(GridPane gridPane, int childIndex) {
    if (childIndex < gridPane.getChildren().size()) {
        Node node = gridPane.getChildren().get(childIndex);
        return node instanceof DatePicker ? (DatePicker) node : null;
    }
    return null;
}

private ChoiceBox<String> findChoiceBoxInGridPane(GridPane gridPane, int childIndex) {
    if (childIndex < gridPane.getChildren().size()) {
        Node node = gridPane.getChildren().get(childIndex);
        return node instanceof ChoiceBox ? (ChoiceBox<String>) node : null;
    }
    return null;
}

    private boolean validateAllFields() {
        boolean isValid = true;
        List<String> errors = new ArrayList<>();

        // Validate required fields
        if (FnameField.getText() == null || FnameField.getText().trim().isEmpty()) {
            setFieldValidation(FnameField, false, "First Name is required");
            errors.add("First Name");
            isValid = false;
        }

        if (LnameField.getText() == null || LnameField.getText().trim().isEmpty()) {
            setFieldValidation(LnameField, false, "Last Name is required");
            errors.add("Last Name");
            isValid = false;
        }

        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            setFieldValidation(emailField, false, "Email is required");
            errors.add("Email");
            isValid = false;
        } else if (!isValidEmail(emailField.getText())) {
            setFieldValidation(emailField, false, "Invalid email format");
            errors.add("Invalid email format");
            isValid = false;
        }

        if (contactNumField.getText() == null || contactNumField.getText().trim().isEmpty()) {
            setFieldValidation(contactNumField, false, "Contact number is required");
            errors.add("Contact Number");
            isValid = false;
        }

        // Validate numeric fields
        if (!heightField.getText().isEmpty()) {
            try {
                Double.parseDouble(heightField.getText());
            } catch (NumberFormatException e) {
                setFieldValidation(heightField, false, "Height must be a number");
                errors.add("Height must be a number");
                isValid = false;
            }
        }

        if (!weightField.getText().isEmpty()) {
            try {
                Double.parseDouble(weightField.getText());
            } catch (NumberFormatException e) {
                setFieldValidation(weightField, false, "Weight must be a number");
                errors.add("Weight must be a number");
                isValid = false;
            }
        }

        // Show appropriate error message
        if (!isValid) {
            if (errors.contains("First Name") || errors.contains("Last Name") || 
                errors.contains("Email") || errors.contains("Contact Number")) {
                showModal("Validation Error", "Please fill up the required fields first.");
            } else {
                showModal("Validation Error", "Please fix the errors in the form: " + String.join(", ", errors));
            }
        }

        return isValid;
    }

    private String buildAddressString(String houseNo, String street, String subdivision, String barangay, String city, String province, String zipCode) {
        StringBuilder address = new StringBuilder();

        if (houseNo != null && !houseNo.trim().isEmpty()) {
            address.append(houseNo.trim());
        }

        if (street != null && !street.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(street.trim());
        }

        // Removed subdivision since it doesn't exist in the form

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

        String result = address.toString();
        System.out.println("Built address: " + result);
        return result;
    }

    // Rest of your existing methods remain the same...
    // (generatePrompt, showModal, clearFields, selectImg, readImageFile, setDataForEdit, setAddressFields)

    private String generatePrompt(User user, String repeatedPassword, String mode) {
        ArrayList<String> promptList = new ArrayList<>();
        String filterPrompt = "";

        String fnamePrompt = Filter.REQUIRED.name(user.getFname(), "First Name");
        String mnamePrompt = Filter.OPTIONAL.name(user.getMname(), "Middle Name");
        String lnamePrompt = Filter.REQUIRED.name(user.getLname(), "Last Name");
        String emailPrompt = Filter.REQUIRED.email(user.getEmail());

        String emailInUsePrompt = "";
        String passwordPrompt = "";

        if (mode.equalsIgnoreCase("add")) {
            if (!"Employee".equals(user.getPrivilege())) {
                passwordPrompt = Filter.REQUIRED.password(user.getPassword(), repeatedPassword);
            } else {
                passwordPrompt = Filter.OPTIONAL.password(user.getPassword(), repeatedPassword);
            }
            emailInUsePrompt = Filter.REQUIRED.isEmailInUse(user.getEmail());
        } else {
            passwordPrompt = Filter.OPTIONAL.password(user.getPassword(), repeatedPassword);
            emailInUsePrompt = Filter.REQUIRED.isEmailInUseExceptCurrentUser(user.getEmail(), selectedUser.getEmail());
        }

        String privilegePrompt = Filter.REQUIRED.privilege(user.getPrivilege());

        promptList.add(fnamePrompt);
        promptList.add(mnamePrompt);
        promptList.add(lnamePrompt);
        promptList.add(emailPrompt);
        promptList.add(passwordPrompt);
        promptList.add(privilegePrompt);
        promptList.add(emailInUsePrompt);

        for (int i = 0; i < promptList.size(); i++) {
            if (!(promptList.get(i).equals("")) && i == promptList.size() - 1) {
                filterPrompt += promptList.get(i);
            } else if (!(promptList.get(i).equals(""))) {
                filterPrompt += promptList.get(i) + "\n";
            }
        }

        return filterPrompt;
    }

    private void showModal(String title, String message) {
        Stage successStage = new Stage();
        successStage.initModality(Modality.APPLICATION_MODAL);
        successStage.setTitle(title);

        Label successLabel = new Label(message);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> successStage.close());

        VBox modalContent = new VBox(successLabel, closeButton);
        modalContent.setSpacing(10);
        modalContent.setAlignment(Pos.CENTER);

        Scene successScene = new Scene(modalContent, 350, 250);
        successStage.setScene(successScene);
        successStage.show();
    }

    private void clearFields() {
        userImage.setImage(new Image(getClass().getResourceAsStream("../Images/default_user_img.jpg")));
        emailField.clear();
        privilegeChoiceBox.setValue("Employee");
        FnameField.clear();
        MnameField.clear();
        LnameField.clear();
        userSuffixChoiceBox.setValue("None");
        sexChoiceBox.setValue("Select");
        dateOfBirthPicker.setValue(null);
        contactNumField.clear();

        // Clear CS Form No. 212 fields
        placeOfBirthField.clear();
        civilStatusChoiceBox.setValue("Select");
        heightField.clear();
        weightField.clear();
        bloodTypeChoiceBox.setValue("Select");
        gsisIdField.clear();
        pagibigIdField.clear();
        philhealthNoField.clear();
        sssNoField.clear();
        tinNoField.clear();
        agencyEmployeeNoField.clear();
        citizenshipChoiceBox.setValue("Select");
        telephoneNoField.clear();

        // Clear address fields
        residentialHouseNoField.clear();
        residentialStreetField.clear();
        residentialBarangayField.clear();
        residentialCityField.clear();
        residentialProvinceField.clear();
        residentialZipCodeField.clear();

        permanentHouseNoField.clear();
        permanentStreetField.clear();
        permanentBarangayField.clear();
        permanentCityField.clear();
        permanentProvinceField.clear();
        permanentZipCodeField.clear();

        // Clear dual citizenship fields
        if(dualCitizenshipTypeChoiceBox != null) dualCitizenshipTypeChoiceBox.setValue("Select");
        if(dualCitizenshipCountryField != null) dualCitizenshipCountryField.clear();

        // Clear family background fields
        spouseSurnameField.clear();
        spouseFirstNameField.clear();
        spouseMiddleNameField.clear();
        spouseNameExtensionField.setValue("None");
        spouseOccupationField.clear();
        spouseEmployerField.clear();
        spouseBusinessAddressField.clear();
        spouseTelephoneField.clear();

        fatherSurnameField.clear();
        fatherFirstNameField.clear();
        fatherMiddleNameField.clear();
        fatherNameExtensionField.setValue("None");

        motherSurnameField.clear();
        motherFirstNameField.clear();
        motherMiddleNameField.clear();
        
        departmentChoiceBox.setValue(null);
        positionChoiceBox.setValue(null);
        shiftChoiceBox.setValue(null);
        startTimeField.setText("08:00");
        endTimeField.setText("17:00");
        addAssignmentCheckbox.setSelected(true);

        // Clear children container
        if (childrenContainer != null) {
            childrenContainer.getChildren().clear();
        }

        // --- NEW: Clear Educational Background Fields ---
        clearEducationFields();

        // --- NEW: Clear Dynamic Containers ---
        if(eligibilityContainer != null) eligibilityContainer.getChildren().clear();
        if(workExperienceContainer != null) workExperienceContainer.getChildren().clear();
        
        updateDualCitizenshipVisibility();
        
        // Clear validation styles
        clearValidationStyles();
    }
    private void initializeEducationFields() {
        // Elementary - set N/A placeholders
        elemDegreeField.setPromptText("N/A");
        elemUnitsField.setPromptText("N/A");

        // Secondary - set N/A placeholders  
        secDegreeField.setPromptText("N/A");
        secUnitsField.setPromptText("N/A");

        // Vocational - set Course/Units placeholders
        vocDegreeField.setPromptText("Course");
        vocUnitsField.setPromptText("Units");

        // College - set Course/Units placeholders
        colDegreeField.setPromptText("Course");
        colUnitsField.setPromptText("Units");

        // Graduate - set Course/Units placeholders
        gradDegreeField.setPromptText("Course");
        gradUnitsField.setPromptText("Units");
    }
    
    // --- NEW: Helper method to clear education fields ---
    private void clearEducationFields() {
        TextField[] textFields = {
            elemSchoolField, elemDegreeField, elemUnitsField, elemYearGradField, elemHonorsField,
            secSchoolField, secDegreeField, secUnitsField, secYearGradField, secHonorsField,
            vocSchoolField, vocDegreeField, vocUnitsField, vocYearGradField, vocHonorsField,
            colSchoolField, colDegreeField, colUnitsField, colYearGradField, colHonorsField,
            gradSchoolField, gradDegreeField, gradUnitsField, gradYearGradField, gradHonorsField
        };
        for (TextField tf : textFields) {
            if(tf != null) tf.clear();
        }

        DatePicker[] datePickers = {
            elemFromDate, elemToDate, secFromDate, secToDate, vocFromDate, vocToDate,
            colFromDate, colToDate, gradFromDate, gradToDate
        };
        for (DatePicker dp : datePickers) {
            if(dp != null) dp.setValue(null);
        }
    }
    private void clearValidationStyles() {
        TextField[] fields = {FnameField, LnameField, emailField, contactNumField, heightField, weightField};
        for (TextField field : fields) {
            if (field != null) {
                field.getStyleClass().removeAll("error", "success");
                field.setTooltip(null);
            }
        }
    }

    @FXML
    private void selectImg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            try {
                imageBytes = readImageFile(selectedFile);
                if (imageBytes != null) {
                    System.out.println("Image selected and read as bytes.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String filePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            userImage.setImage(image);
        }
    }

    private byte[] readImageFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }
    public void setDataForView(User user, Stage stage, ADMIN_EmpMgmtCTRL adminEmpMgmtCTRL) {
        System.out.println("=== setDataForView called ===");
        System.out.println("User: " + (user != null ? user.getFname() + " " + user.getLname() : "null"));

        editMode = false;
        selectedUser = user;

        if (user != null) {
            selectedUserId = user.getId();

            // Set form to view mode (non-editable)
            setFormViewMode(true);

            // Basic information
            FnameField.setText(user.getFname());
            MnameField.setText(user.getMname());
            LnameField.setText(user.getLname());

            String suffix = user.getSuffix();
            if (suffix == null || suffix.equals("")) {
                suffix = "None";
            }
            userSuffixChoiceBox.setValue(suffix);
            emailField.setText(user.getEmail());
            privilegeChoiceBox.setValue(user.getPrivilege());
            contactNumField.setText(user.getContactNum());
            sexChoiceBox.setValue(user.getSex() != null ? user.getSex() : null);
            dateOfBirthPicker.setValue(user.getBirthDate());

            // CS Form No. 212 fields
            placeOfBirthField.setText(user.getPlaceOfBirth());
            civilStatusChoiceBox.setValue(user.getCivilStatus());
            heightField.setText(user.getHeight() != null ? user.getHeight().toString() : "");
            weightField.setText(user.getWeight() != null ? user.getWeight().toString() : "");
            bloodTypeChoiceBox.setValue(user.getBloodType());
            gsisIdField.setText(user.getGsisId());
            pagibigIdField.setText(user.getPagibigId());
            philhealthNoField.setText(user.getPhilhealthNo());
            sssNoField.setText(user.getSssNo());
            tinNoField.setText(user.getTinNo());
            agencyEmployeeNoField.setText(user.getAgencyEmployeeNo());
            citizenshipChoiceBox.setValue(user.getCitizenship());
            telephoneNoField.setText(user.getTelephoneNo());

            // Parse and set address fields
            setAddressFields(user.getResidentialAddress(), true);
            setAddressFields(user.getPermanentAddress(), false);

            // Family background fields
            spouseSurnameField.setText(user.getSpouseSurname());
            spouseFirstNameField.setText(user.getSpouseFirstName());
            spouseMiddleNameField.setText(user.getSpouseMiddleName());
            spouseNameExtensionField.setValue(user.getSpouseNameExtension() != null ? user.getSpouseNameExtension() : "None");
            spouseOccupationField.setText(user.getSpouseOccupation());
            spouseEmployerField.setText(user.getSpouseEmployer());
            spouseBusinessAddressField.setText(user.getSpouseBusinessAddress());
            spouseTelephoneField.setText(user.getSpouseTelephone());

            fatherSurnameField.setText(user.getFatherSurname());
            fatherFirstNameField.setText(user.getFatherFirstName());
            fatherMiddleNameField.setText(user.getFatherMiddleName());
            fatherNameExtensionField.setValue(user.getFatherNameExtension() != null ? user.getFatherNameExtension() : "None");

            motherSurnameField.setText(user.getMotherSurname());
            motherFirstNameField.setText(user.getMotherFirstName());
            motherMiddleNameField.setText(user.getMotherMiddleName());

            // Load children
            loadChildrenData(user.getChildrenInfo());

            // Load educational background
            loadEducationalBackground(user.getEducationalBackgrounds());

            // Load civil service eligibility
            loadCivilServiceEligibility(user.getCivilServiceEligibilities());

            // Load work experience
            loadWorkExperience(user.getWorkExperiences());

            // Set image
            if (user.getImage() != null) {
                userImage.setImage(ImageUtil.byteArrayToImage(user.getImage()));
            }

            // Update UI for view mode
            addEmployeeBtn.setVisible(false);
            backButton.setText("Close");
            backButton.setOnAction(e -> {
                if (stage != null) {
                    stage.close();
                } else {
                    // Go back to employee management
                    ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
                    if (containerCtrl != null) {
                        containerCtrl.loadFXML(paneUtil.ADMIN_EMP_MGMT);
                    }
                }
            });

            // Update form title for view mode
            if (formTitleLabel != null) {
                formTitleLabel.setText("View Employee - " + user.getFname() + " " + user.getLname());
            }
        }

        updateDualCitizenshipVisibility();
    }
    private void setFormViewMode(boolean viewMode) {
        // Disable all input fields
        FnameField.setEditable(!viewMode);
        MnameField.setEditable(!viewMode);
        LnameField.setEditable(!viewMode);
        userSuffixChoiceBox.setDisable(viewMode);
        emailField.setEditable(!viewMode);
        contactNumField.setEditable(!viewMode);
        dateOfBirthPicker.setDisable(viewMode);
        privilegeChoiceBox.setDisable(viewMode);
        sexChoiceBox.setDisable(viewMode);

        // CS Form fields
        placeOfBirthField.setEditable(!viewMode);
        civilStatusChoiceBox.setDisable(viewMode);
        heightField.setEditable(!viewMode);
        weightField.setEditable(!viewMode);
        bloodTypeChoiceBox.setDisable(viewMode);
        gsisIdField.setEditable(!viewMode);
        pagibigIdField.setEditable(!viewMode);
        philhealthNoField.setEditable(!viewMode);
        sssNoField.setEditable(!viewMode);
        tinNoField.setEditable(!viewMode);
        agencyEmployeeNoField.setEditable(!viewMode);
        citizenshipChoiceBox.setDisable(viewMode);
        telephoneNoField.setEditable(!viewMode);

        // Address fields
        residentialHouseNoField.setEditable(!viewMode);
        residentialStreetField.setEditable(!viewMode);
        residentialBarangayField.setEditable(!viewMode);
        residentialCityField.setEditable(!viewMode);
        residentialProvinceField.setEditable(!viewMode);
        residentialZipCodeField.setEditable(!viewMode);
        permanentHouseNoField.setEditable(!viewMode);
        permanentStreetField.setEditable(!viewMode);
        permanentBarangayField.setEditable(!viewMode);
        permanentCityField.setEditable(!viewMode);
        permanentProvinceField.setEditable(!viewMode);
        permanentZipCodeField.setEditable(!viewMode);

        // Family background fields
        spouseSurnameField.setEditable(!viewMode);
        spouseFirstNameField.setEditable(!viewMode);
        spouseMiddleNameField.setEditable(!viewMode);
        spouseNameExtensionField.setDisable(viewMode);
        spouseOccupationField.setEditable(!viewMode);
        spouseEmployerField.setEditable(!viewMode);
        spouseBusinessAddressField.setEditable(!viewMode);
        spouseTelephoneField.setEditable(!viewMode);
        fatherSurnameField.setEditable(!viewMode);
        fatherFirstNameField.setEditable(!viewMode);
        fatherMiddleNameField.setEditable(!viewMode);
        fatherNameExtensionField.setDisable(viewMode);
        motherSurnameField.setEditable(!viewMode);
        motherFirstNameField.setEditable(!viewMode);
        motherMiddleNameField.setEditable(!viewMode);
        
        departmentChoiceBox.setDisable(viewMode);
        positionChoiceBox.setDisable(viewMode);
        shiftChoiceBox.setDisable(viewMode);
        startTimeField.setEditable(!viewMode);
        endTimeField.setEditable(!viewMode);
        addAssignmentCheckbox.setDisable(viewMode);

        // Educational fields
        setEducationFieldsEditable(!viewMode);

        // Disable dynamic section buttons in view mode
        if (viewMode) {
            // Hide add buttons for dynamic sections
            hideAddButtons();

            // Make dynamic containers non-editable
            setDynamicContainersViewMode();
        }

        // Disable image selection in view mode
        selectImageBtn.setDisable(viewMode);

        // Change style to indicate view mode
        if (viewMode) {
            addStyleClassToFields("-fx-background-color: #f5f5f5; -fx-text-fill: #333;");
        }
    }

    private void setEducationFieldsEditable(boolean editable) {
        TextField[] educationTextFields = {
            elemSchoolField, elemDegreeField, elemUnitsField, elemYearGradField, elemHonorsField,
            secSchoolField, secDegreeField, secUnitsField, secYearGradField, secHonorsField,
            vocSchoolField, vocDegreeField, vocUnitsField, vocYearGradField, vocHonorsField,
            colSchoolField, colDegreeField, colUnitsField, colYearGradField, colHonorsField,
            gradSchoolField, gradDegreeField, gradUnitsField, gradYearGradField, gradHonorsField
        };

        for (TextField field : educationTextFields) {
            if (field != null) {
                field.setEditable(editable);
            }
        }

        DatePicker[] educationDatePickers = {
            elemFromDate, elemToDate, secFromDate, secToDate, vocFromDate, vocToDate,
            colFromDate, colToDate, gradFromDate, gradToDate
        };

        for (DatePicker picker : educationDatePickers) {
            if (picker != null) {
                picker.setDisable(!editable);
            }
        }
    }

    private void hideAddButtons() {
        // This will hide the "Add" buttons in the dynamic sections
        // You'll need to add fx:id to these buttons in your FXML
        // For now, we'll traverse the container to find and hide buttons
        hideAddButtonsInContainer(childrenContainer);
        hideAddButtonsInContainer(eligibilityContainer);
        hideAddButtonsInContainer(workExperienceContainer);
    }

    private void hideAddButtonsInContainer(Parent container) {
        if (container == null) return;

        // Use getChildrenUnmodifiable() which is public
        for (Node node : container.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                if (button.getText() != null && 
                    (button.getText().contains("Add") || button.getText().contains("Remove"))) {
                    button.setVisible(false);
                }
            } else if (node instanceof Parent) {
                hideAddButtonsInContainer((Parent) node);
            }
        }
    }


    private void setDynamicContainersViewMode() {
        setContainerViewMode(childrenContainer);
        setContainerViewMode(eligibilityContainer);
        setContainerViewMode(workExperienceContainer);
    }

    private void setContainerViewMode(Parent container) {
        if (container == null) return;

        // Use getChildrenUnmodifiable() which is public
        for (Node node : container.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                ((TextField) node).setEditable(false);
            } else if (node instanceof DatePicker) {
                ((DatePicker) node).setDisable(true);
            } else if (node instanceof ChoiceBox) {
                ((ChoiceBox<?>) node).setDisable(true);
            } else if (node instanceof TextArea) {
                ((TextArea) node).setEditable(false);
            } else if (node instanceof Button) {
                // Hide remove buttons in view mode
                if (((Button) node).getText() != null && 
                    ((Button) node).getText().contains("Remove")) {
                    node.setVisible(false);
                }
            } else if (node instanceof Parent) {
                setContainerViewMode((Parent) node);
            }
        }
    }

    private void addStyleClassToFields(String style) {
        // Apply style to all text fields and choice boxes to indicate view mode
        for (Node node : getAllNodes()) {
            if (node instanceof TextField || node instanceof ChoiceBox || node instanceof DatePicker) {
                node.setStyle(style);
            }
        }
    }

    private List<Node> getAllNodes() {
      List<Node> nodes = new ArrayList<>();
      // Get the root container - adjust this based on your actual structure
      Parent root = userImage.getParent(); // Start from a known node and go up to root
      while (root != null && !(root instanceof ScrollPane) && root.getParent() != null) {
          root = root.getParent();
      }
      if (root != null) {
          addAllDescendents(root, nodes);
      }
      return nodes;
  }

   private void addAllDescendents(Parent parent, List<Node> nodes) {
        // Use getChildrenUnmodifiable() which is public
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent) {
                addAllDescendents((Parent) node, nodes);
            }
        }
    }
    public void setDataForEdit(User user, Stage stage, ADMIN_EmpMgmtCTRL adminEmpMgmtCTRL, boolean isEditMode) {
        System.out.println("=== setDataForEdit called ===");
        System.out.println("User: " + (user != null ? user.getFname() + " " + user.getLname() : "null"));
        System.out.println("Edit mode: " + isEditMode);

        editMode = isEditMode;
        selectedUser = user;
        if (isEditMode) {
            formTitleLabel.setText("Edit Employee - " + user.getFname() + " " + user.getLname());
        }
        if (isEditMode && user != null) {
            selectedUserId = user.getId();

            // For edit mode, allow admin privilege selection
            privilegeChoiceBox.getItems().clear();
            privilegeChoiceBox.getItems().addAll("Employee", "Admin");

            String suffix = user.getSuffix();
            if (suffix == null || suffix.equals("")) {
                suffix = "None";
            }

            // Debug basic information
            System.out.println("Setting basic info...");

            // Basic information
            FnameField.setText(user.getFname());
            MnameField.setText(user.getMname());
            LnameField.setText(user.getLname());
            userSuffixChoiceBox.setValue(suffix);
            emailField.setText(user.getEmail());
            privilegeChoiceBox.setValue(user.getPrivilege());
            contactNumField.setText(user.getContactNum());
            sexChoiceBox.setValue(user.getSex() != null ? user.getSex() : null);
            dateOfBirthPicker.setValue(user.getBirthDate());

            // CS Form No. 212 fields
            System.out.println("Setting CS Form fields...");
            placeOfBirthField.setText(user.getPlaceOfBirth());
            civilStatusChoiceBox.setValue(user.getCivilStatus());
            heightField.setText(user.getHeight() != null ? user.getHeight().toString() : "");
            weightField.setText(user.getWeight() != null ? user.getWeight().toString() : "");
            bloodTypeChoiceBox.setValue(user.getBloodType());
            gsisIdField.setText(user.getGsisId());
            pagibigIdField.setText(user.getPagibigId());
            philhealthNoField.setText(user.getPhilhealthNo());
            sssNoField.setText(user.getSssNo());
            tinNoField.setText(user.getTinNo());
            agencyEmployeeNoField.setText(user.getAgencyEmployeeNo());
            citizenshipChoiceBox.setValue(user.getCitizenship());
            telephoneNoField.setText(user.getTelephoneNo());

            // Parse and set address fields
            System.out.println("Setting addresses...");
            System.out.println("Residential: " + user.getResidentialAddress());
            System.out.println("Permanent: " + user.getPermanentAddress());

            setAddressFields(user.getResidentialAddress(), true);
            setAddressFields(user.getPermanentAddress(), false);

            // Family background fields
            System.out.println("Setting family background...");
            spouseSurnameField.setText(user.getSpouseSurname());
            spouseFirstNameField.setText(user.getSpouseFirstName());
            spouseMiddleNameField.setText(user.getSpouseMiddleName());
            spouseNameExtensionField.setValue(user.getSpouseNameExtension() != null ? user.getSpouseNameExtension() : "None");
            spouseOccupationField.setText(user.getSpouseOccupation());
            spouseEmployerField.setText(user.getSpouseEmployer());
            spouseBusinessAddressField.setText(user.getSpouseBusinessAddress());
            spouseTelephoneField.setText(user.getSpouseTelephone());

            fatherSurnameField.setText(user.getFatherSurname());
            fatherFirstNameField.setText(user.getFatherFirstName());
            fatherMiddleNameField.setText(user.getFatherMiddleName());
            fatherNameExtensionField.setValue(user.getFatherNameExtension() != null ? user.getFatherNameExtension() : "None");

            motherSurnameField.setText(user.getMotherSurname());
            motherFirstNameField.setText(user.getMotherFirstName());
            motherMiddleNameField.setText(user.getMotherMiddleName());

            // Load children
            System.out.println("Loading children: " + user.getChildrenInfo());
            loadChildrenData(user.getChildrenInfo());

            // Load educational background
            System.out.println("Loading education...");
            loadEducationalBackground(user.getEducationalBackgrounds());

            // Load civil service eligibility
            System.out.println("Loading civil service...");
            loadCivilServiceEligibility(user.getCivilServiceEligibilities());

            // Load work experience
            System.out.println("Loading work experience...");
            loadWorkExperience(user.getWorkExperiences());

            // Set image
            if (user.getImage() != null) {
                userImage.setImage(ImageUtil.byteArrayToImage(user.getImage()));
            }

            addEmployeeBtn.setText("Save Changes");
            System.out.println("=== setDataForEdit completed ===");
        }

        updateDualCitizenshipVisibility();

        if (stage != null) {
            stage.setOnHiding(event -> {
                if (adminEmpMgmtCTRL != null) {
                    adminEmpMgmtCTRL.loadUserTable();
                }
            });
        }
    }
    // --- Helper to parse salary String to Double ---
    private Double parseSalary(String salaryStr) {
        if (salaryStr == null || salaryStr.isEmpty()) {
            return null;
        }
        try {
            // Remove commas, currency symbols, etc. before parsing
            String cleanSalary = salaryStr.replaceAll("[^\\d.]", "");
            return Double.parseDouble(cleanSalary);
        } catch (NumberFormatException e) {
            System.err.println("Could not parse salary: " + salaryStr);
            return null;
        }
    }
    private ADMIN_RowEligibilityCTRL addAndConfigureEligibilityRow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ADMIN_RowEligibility.fxml")); // ADJUST PATH
            Node node = loader.load();
            ADMIN_RowEligibilityCTRL controller = loader.getController();

            // controller.setMainController(this); // Set up main controller reference
            
            eligibilityContainer.getChildren().add(node);
            eligibilityNodes.add(controller);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            Modal.alert("Error", "Could not load eligibility row: " + e.getMessage());
            return null;
        }
    }
    private ADMIN_RowWorkExpCTRL addAndConfigureWorkRow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ADMIN_RowWorkExp.fxml")); // ADJUST PATH
            Node node = loader.load();
            ADMIN_RowWorkExpCTRL controller = loader.getController();

            // controller.setMainController(this); // Set up main controller reference

            workExperienceContainer.getChildren().add(node);
            workExperienceNodes.add(controller);
            return controller;
        } catch (IOException e) {
            e.printStackTrace();
            Modal.alert("Error", "Could not load work experience row: " + e.getMessage());
            return null;
        }
    }

    // Add these helper methods to ADMIN_AddEmpCTRL class:

private void loadChildrenData(String childrenInfo) {
    // Clear existing rows first (Important when loading data)
    childrenContainer.getChildren().clear();
    if (childNodes != null) {
        childNodes.clear();
    } else {
        childNodes = new ArrayList<>(); // Initialize if null
    }

    if (childrenInfo != null && !childrenInfo.trim().isEmpty()) {
        String[] children = childrenInfo.split(";;");
        for (String child : children) {
            if (!child.trim().isEmpty()) {
                String[] parts = child.split("\\|");
                if (parts.length >= 2) {
                    // Call the helper to add a row and get its controller
                    ADMIN_RowChildCTRL newChildCtrl = addAndConfigureChildRow();

                    if (newChildCtrl != null) {
                        // Use the controller's methods to set data directly
                        newChildCtrl.setChildName(parts[0]);
                        if (parts.length > 1 && !parts[1].isEmpty()) {
                            try {
                                // Use your existing parseDate or standard LocalDate parsing
                                newChildCtrl.setBirthDate(LocalDate.parse(parts[1]));
                            } catch (Exception e) {
                                System.err.println("Could not parse child date: " + parts[1]);
                                // Optionally set date picker to null or leave blank
                                newChildCtrl.setBirthDate(null);
                            }
                        } else {
                             newChildCtrl.setBirthDate(null); // Handle case where DOB might be missing
                        }
                    } else {
                        // Handle error if row creation failed
                        System.err.println("Failed to add child row for data: " + child);
                        break; // Stop processing further children if one fails
                    }
                }
            }
        }
    }
}

    private void loadEducationalBackground(List<EducationalBackground> educations) {
        if (educations != null) {
            for (EducationalBackground edu : educations) {
                switch (edu.getLevel()) {
                    case "Elementary":
                        elemSchoolField.setText(edu.getSchoolName());
                        elemDegreeField.setText(edu.getDegreeCourse());
                        elemFromDate.setValue(edu.getStartDate());
                        elemToDate.setValue(edu.getEndDate());
                        elemUnitsField.setText(edu.getUnitsEarned());
                        elemYearGradField.setText(edu.getYearGraduated());
                        elemHonorsField.setText(edu.getHonors());
                        break;
                    case "Secondary":
                        secSchoolField.setText(edu.getSchoolName());
                        secDegreeField.setText(edu.getDegreeCourse());
                        secFromDate.setValue(edu.getStartDate());
                        secToDate.setValue(edu.getEndDate());
                        secUnitsField.setText(edu.getUnitsEarned());
                        secYearGradField.setText(edu.getYearGraduated());
                        secHonorsField.setText(edu.getHonors());
                        break;
                    case "Vocational":
                        vocSchoolField.setText(edu.getSchoolName());
                        vocDegreeField.setText(edu.getDegreeCourse());
                        vocFromDate.setValue(edu.getStartDate());
                        vocToDate.setValue(edu.getEndDate());
                        vocUnitsField.setText(edu.getUnitsEarned());
                        vocYearGradField.setText(edu.getYearGraduated());
                        vocHonorsField.setText(edu.getHonors());
                        break;
                    case "College":
                        colSchoolField.setText(edu.getSchoolName());
                        colDegreeField.setText(edu.getDegreeCourse());
                        colFromDate.setValue(edu.getStartDate());
                        colToDate.setValue(edu.getEndDate());
                        colUnitsField.setText(edu.getUnitsEarned());
                        colYearGradField.setText(edu.getYearGraduated());
                        colHonorsField.setText(edu.getHonors());
                        break;
                    case "Graduate Studies":
                        gradSchoolField.setText(edu.getSchoolName());
                        gradDegreeField.setText(edu.getDegreeCourse());
                        gradFromDate.setValue(edu.getStartDate());
                        gradToDate.setValue(edu.getEndDate());
                        gradUnitsField.setText(edu.getUnitsEarned());
                        gradYearGradField.setText(edu.getYearGraduated());
                        gradHonorsField.setText(edu.getHonors());
                        break;
                }
            }
        }
    }
    private ADMIN_RowChildCTRL addAndConfigureChildRow() {
    try {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ADMIN_RowChild.fxml")); // Adjust path if needed
        Node childNode = loader.load();
        ADMIN_RowChildCTRL controller = loader.getController();

        // Pass a reference of this main controller to the row controller
        controller.setMainController(this);

        // Add the loaded node (the HBox) to the container in your UI
        childrenContainer.getChildren().add(childNode);

        // Add the controller to your list
        childNodes.add(controller);

        // Return the controller so the caller can populate it
        return controller;

    } catch (IOException e) {
        e.printStackTrace();
        Modal.alert("Error", "Could not load child input row: " + e.getMessage());
        return null; // Indicate failure
    }
}
    @FXML
private void addChildField(ActionEvent event) {
    addAndConfigureChildRow(); // Simply call the helper
}


    private void loadCivilServiceEligibility(List<CivilServiceEligibility> eligibilities) {
        // 1. Clear existing rows first
        eligibilityContainer.getChildren().clear();
        if (eligibilityNodes != null) {
            eligibilityNodes.clear();
        } else {
            eligibilityNodes = new ArrayList<>();
        }

        if (eligibilities != null) {
            for (CivilServiceEligibility eligibility : eligibilities) {
                // 2. Call the helper to add the FXML row and get its controller
                ADMIN_RowEligibilityCTRL newEligCtrl = addAndConfigureEligibilityRow();

                if (newEligCtrl != null) {
                    // 3. Populate the new row using its controller's setters
                    newEligCtrl.setEligibilityName(eligibility.getEligibilityName());
                    newEligCtrl.setRating(eligibility.getRating());
                    newEligCtrl.setExamDate(eligibility.getExamDate());
                    newEligCtrl.setExamPlace(eligibility.getExamPlace());
                    newEligCtrl.setLicenseNumber(eligibility.getLicenseNumber());
                    newEligCtrl.setValidityDate(eligibility.getValidityDate());
                }
            }
        }
    }
    private void loadWorkExperience(List<WorkExperience> experiences) {
        // 1. Clear existing rows first
        workExperienceContainer.getChildren().clear();
        if (workExperienceNodes != null) {
            workExperienceNodes.clear();
        } else {
            workExperienceNodes = new ArrayList<>();
        }
        
        if (experiences != null) {
            for (WorkExperience experience : experiences) {
                // 2. Call the helper to add the FXML row and get its controller
                ADMIN_RowWorkExpCTRL newWorkCtrl = addAndConfigureWorkRow();

                if (newWorkCtrl != null) {
                    // 3. Populate the new row using its controller's setters
                    newWorkCtrl.setStartDate(experience.getStartDate());
                    newWorkCtrl.setEndDate(experience.getEndDate());
                    newWorkCtrl.setPositionTitle(experience.getPositionTitle());
                    newWorkCtrl.setCompany(experience.getCompany());
                    newWorkCtrl.setMonthlySalary(experience.getMonthlySalary());
                    newWorkCtrl.setSalaryGrade(experience.getSalaryGrade());
                    newWorkCtrl.setAppointmentStatus(experience.getAppointmentStatus());
                    newWorkCtrl.setGovernmentService(experience.isGovernmentService());
                }
            }
        }
    }
    private void setAddressFields(String address, boolean isResidential) {
        if (address == null || address.trim().isEmpty()) {
            System.out.println("Address is null or empty for " + (isResidential ? "residential" : "permanent"));
            // Clear all fields first
            if (isResidential) {
                residentialHouseNoField.clear();
                residentialStreetField.clear();
                residentialBarangayField.clear();
                residentialCityField.clear();
                residentialProvinceField.clear();
                residentialZipCodeField.clear();
            } else {
                permanentHouseNoField.clear();
                permanentStreetField.clear();
                permanentBarangayField.clear();
                permanentCityField.clear();
                permanentProvinceField.clear();
                permanentZipCodeField.clear();
            }
            return;
        }

        System.out.println("Parsing " + (isResidential ? "residential" : "permanent") + " address: " + address);

        // More robust parsing that handles missing components
        String[] parts = address.split(",");

        if (isResidential) {
            // Clear all fields first
            residentialHouseNoField.clear();
            residentialStreetField.clear();
            residentialBarangayField.clear();
            residentialCityField.clear();
            residentialProvinceField.clear();
            residentialZipCodeField.clear();

            // Set fields based on available parts
            if (parts.length > 0) residentialHouseNoField.setText(cleanAddressPart(parts[0]));
            if (parts.length > 1) residentialStreetField.setText(cleanAddressPart(parts[1]));
            if (parts.length > 2) residentialBarangayField.setText(cleanAddressPart(parts[2]));
            if (parts.length > 3) residentialCityField.setText(cleanAddressPart(parts[3]));
            if (parts.length > 4) residentialProvinceField.setText(cleanAddressPart(parts[4]));
            if (parts.length > 5) residentialZipCodeField.setText(cleanAddressPart(parts[5]));
        } else {
            // PERMANENT ADDRESS FIX: Clear all fields first
            permanentHouseNoField.clear();
            permanentStreetField.clear();
            permanentBarangayField.clear();
            permanentCityField.clear();
            permanentProvinceField.clear();
            permanentZipCodeField.clear();

            // Set fields based on available parts
            if (parts.length > 0) permanentHouseNoField.setText(cleanAddressPart(parts[0]));
            if (parts.length > 1) permanentStreetField.setText(cleanAddressPart(parts[1]));
            if (parts.length > 2) permanentBarangayField.setText(cleanAddressPart(parts[2]));
            if (parts.length > 3) permanentCityField.setText(cleanAddressPart(parts[3]));
            if (parts.length > 4) permanentProvinceField.setText(cleanAddressPart(parts[4]));
            if (parts.length > 5) permanentZipCodeField.setText(cleanAddressPart(parts[5]));

            System.out.println("Permanent address fields set:");
            System.out.println("House: " + permanentHouseNoField.getText());
            System.out.println("Street: " + permanentStreetField.getText());
            System.out.println("Barangay: " + permanentBarangayField.getText());
            System.out.println("City: " + permanentCityField.getText());
            System.out.println("Province: " + permanentProvinceField.getText());
            System.out.println("Zip: " + permanentZipCodeField.getText());
        }
    }

    private String cleanAddressPart(String part) {
        if (part == null) return "";
        return part.trim();
    }
    private void initializeDocumentsSection() {
    // Create document rows
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
        chooseFileBtn.setStyle("-fx-font-size: 12px;");

        Label fileNameLabel = new Label("No file chosen");
        fileNameLabel.setPrefWidth(200);

        // Store references for later access
        chooseFileBtn.setUserData(documentType);
        fileNameLabel.setUserData(documentType);

        chooseFileBtn.setOnAction(e -> handleDocumentFileSelection(documentType, fileNameLabel));

        documentRow.getChildren().addAll(docLabel, chooseFileBtn, fileNameLabel);
        documentsContainer.getChildren().add(documentRow);
    }

    private void handleDocumentFileSelection(String documentType, Label fileNameLabel) {
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
                documentFiles.put(documentType, fileData);
                documentFileNames.put(documentType, selectedFile.getName());
                fileNameLabel.setText(selectedFile.getName());
            } catch (IOException ex) {
                showModal("Error", "Failed to read file: " + ex.getMessage());
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

    private void saveDocuments(int userId) throws SQLException {
        // Save Google Drive URL as a special document type
        if (googleDriveUrlField.getText() != null && !googleDriveUrlField.getText().trim().isEmpty()) {
            EmployeeDocument driveDoc = new EmployeeDocument();
            driveDoc.setUserId(userId);
            driveDoc.setDocumentType("Google Drive");
            driveDoc.setFileName("Google Drive Link");
            driveDoc.setFileData(null);
            driveDoc.setGoogleDriveUrl(googleDriveUrlField.getText().trim());
            User.saveOrUpdateDocument(driveDoc);
        }

        // Save other documents
        for (String docType : documentFiles.keySet()) {
            EmployeeDocument document = new EmployeeDocument();
            document.setUserId(userId);
            document.setDocumentType(docType);
            document.setFileName(documentFileNames.get(docType));
            document.setFileData(documentFiles.get(docType));
            document.setGoogleDriveUrl(null);
            User.saveOrUpdateDocument(document);
        }
    }
}