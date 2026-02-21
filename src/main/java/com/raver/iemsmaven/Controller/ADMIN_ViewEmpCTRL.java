package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.*;
import com.raver.iemsmaven.Utilities.ImageUtil;
import com.raver.iemsmaven.Utilities.Modal;
import com.raver.iemsmaven.Utilities.PaneUtil;
import java.io.File;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.stage.FileChooser;

public class ADMIN_ViewEmpCTRL implements Initializable {
    @FXML private Label positionHeaderLabel;
    @FXML private Label scoreLabel;
    @FXML private Label scoreMessageLabel;
    @FXML private Label attendanceRateLabel;
    @FXML private Label latesLabel;
    @FXML private BarChart<String, Number> attendanceBarChart;
    @FXML private PieChart statusPieChart;
    @FXML private Label formTitleLabel;
    @FXML private ImageView userImageView;
    @FXML private ImageView userImageViewLarge;
    
    // Personal Information Labels
    @FXML private Label surnameLabel;
    @FXML private Label firstNameLabel;
    @FXML private Label middleNameLabel;
    @FXML private Label suffixLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label placeOfBirthLabel;
    @FXML private Label sexLabel;
    @FXML private Label heightLabel;
    @FXML private Label civilStatusLabel;
    @FXML private Label weightLabel;
    @FXML private Label bloodTypeLabel;
    @FXML private Label citizenshipLabel;
    
    // Contact Information Labels
    @FXML private Label mobileNoLabel;
    @FXML private Label telephoneNoLabel;
    @FXML private Label emailLabel;
    
    // Government IDs Labels
    @FXML private Label gsisIdLabel;
    @FXML private Label pagibigIdLabel;
    @FXML private Label philhealthNoLabel;
    @FXML private Label sssNoLabel;
    @FXML private Label tinNoLabel;
    @FXML private Label agencyEmployeeNoLabel;
    
    // Address Labels
    @FXML private Label residentialHouseNoLabel;
    @FXML private Label residentialStreetLabel;
    @FXML private Label residentialBarangayLabel;
    @FXML private Label residentialCityLabel;
    @FXML private Label residentialProvinceLabel;
    @FXML private Label residentialZipCodeLabel;
    
    @FXML private Label permanentHouseNoLabel;
    @FXML private Label permanentStreetLabel;
    @FXML private Label permanentBarangayLabel;
    @FXML private Label permanentCityLabel;
    @FXML private Label permanentProvinceLabel;
    @FXML private Label permanentZipCodeLabel;
    
    // System Account
    @FXML private Label privilegeLabel;
    
    // TableViews
    @FXML private TableView<SpouseInfo> spouseTable;
    @FXML private TableView<ParentInfo> parentsTable;
    @FXML private TableView<ChildInfo> childrenTable;
    @FXML private TableView<EducationalBackground> educationTable;
    @FXML private TableView<CivilServiceEligibility> eligibilityTable;
    @FXML private TableView<WorkExperience> workExperienceTable;
    
    // Table Columns
    @FXML private TableColumn<SpouseInfo, String> spouseNameCol;
    @FXML private TableColumn<SpouseInfo, String> spouseOccupationCol;
    @FXML private TableColumn<SpouseInfo, String> spouseEmployerCol;
    @FXML private TableColumn<SpouseInfo, String> spouseBusinessAddressCol;
    @FXML private TableColumn<SpouseInfo, String> spouseTelephoneCol;
    
    @FXML private TableColumn<ParentInfo, String> parentTypeCol;
    @FXML private TableColumn<ParentInfo, String> parentNameCol;
    
    @FXML private TableColumn<ChildInfo, String> childNameCol;
    @FXML private TableColumn<ChildInfo, String> childDobCol;
    
    @FXML private TableColumn<EducationalBackground, String> eduLevelCol;
    @FXML private TableColumn<EducationalBackground, String> eduSchoolCol;
    @FXML private TableColumn<EducationalBackground, String> eduDegreeCol;
    @FXML private TableColumn<EducationalBackground, String> eduPeriodCol;
    @FXML private TableColumn<EducationalBackground, String> eduUnitsCol;
    @FXML private TableColumn<EducationalBackground, String> eduYearGradCol;
    @FXML private TableColumn<EducationalBackground, String> eduHonorsCol;
    
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityNameCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityRatingCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityExamDateCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityExamPlaceCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityLicenseCol;
    @FXML private TableColumn<CivilServiceEligibility, String> eligibilityValidityCol;
    
    @FXML private TableColumn<WorkExperience, String> workPeriodCol;
    @FXML private TableColumn<WorkExperience, String> workPositionCol;
    @FXML private TableColumn<WorkExperience, String> workCompanyCol;
    @FXML private TableColumn<WorkExperience, String> workSalaryCol;
    @FXML private TableColumn<WorkExperience, String> workGradeCol;
    @FXML private TableColumn<WorkExperience, String> workStatusCol;
    @FXML private TableColumn<WorkExperience, String> workGovServiceCol;
    
    @FXML private Label departmentLabel;
    @FXML private Label positionLabel;
    @FXML private Label shiftLabel;
    @FXML private Label timeRangeLabel;
    
    @FXML private Label otLabel;
    @FXML private Label analyticsSummaryLabel;

    private User currentUser;
    private PaneUtil paneUtil = new PaneUtil();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    
    // Document fields
    @FXML private VBox documentsContainer;
    @FXML private Label googleDriveUrlLabel;
    
    @FXML private javafx.scene.chart.LineChart<String, Number> arrivalTrendChart;
    @FXML private javafx.scene.chart.BarChart<String, Number> dayPatternChart;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeTables();
    }

    private void initializeTables() {
        // Spouse Table
        spouseNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        spouseOccupationCol.setCellValueFactory(new PropertyValueFactory<>("occupation"));
        spouseEmployerCol.setCellValueFactory(new PropertyValueFactory<>("employer"));
        spouseBusinessAddressCol.setCellValueFactory(new PropertyValueFactory<>("businessAddress"));
        spouseTelephoneCol.setCellValueFactory(new PropertyValueFactory<>("telephone"));

        // Parents Table
        parentTypeCol.setCellValueFactory(new PropertyValueFactory<>("parentType"));
        parentNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        // Children Table
        childNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        childDobCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        // Education Table
        eduLevelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        eduSchoolCol.setCellValueFactory(new PropertyValueFactory<>("schoolName"));
        eduDegreeCol.setCellValueFactory(new PropertyValueFactory<>("degreeCourse"));
        eduPeriodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        eduUnitsCol.setCellValueFactory(new PropertyValueFactory<>("unitsEarned"));
        eduYearGradCol.setCellValueFactory(new PropertyValueFactory<>("yearGraduated"));
        eduHonorsCol.setCellValueFactory(new PropertyValueFactory<>("honors"));

        // Eligibility Table
        eligibilityNameCol.setCellValueFactory(new PropertyValueFactory<>("eligibilityName"));
        eligibilityRatingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        eligibilityExamDateCol.setCellValueFactory(new PropertyValueFactory<>("examDateDisplay"));
        eligibilityValidityCol.setCellValueFactory(new PropertyValueFactory<>("validityDateDisplay"));
        eligibilityExamPlaceCol.setCellValueFactory(new PropertyValueFactory<>("examPlace"));
        eligibilityLicenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));


        workPeriodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        workPositionCol.setCellValueFactory(new PropertyValueFactory<>("positionTitle"));
        workCompanyCol.setCellValueFactory(new PropertyValueFactory<>("company"));

        // FIX: Monthly Salary column - use proper display method
        workSalaryCol.setCellValueFactory(cellData -> {
            WorkExperience work = cellData.getValue();
            if (work != null && work.getMonthlySalary() != null) {
                return new SimpleStringProperty(String.format("₱%,.2f", work.getMonthlySalary()));
            } else {
                return new SimpleStringProperty("");
            }
        });

        // FIX: Salary Grade column
        workGradeCol.setCellValueFactory(cellData -> {
            WorkExperience work = cellData.getValue();
            if (work != null && work.getSalaryGrade() != null && !work.getSalaryGrade().trim().isEmpty()) {
                return new SimpleStringProperty(work.getSalaryGrade());
            } else {
                return new SimpleStringProperty("");
            }
        });

        // FIX: Appointment Status column
        workStatusCol.setCellValueFactory(cellData -> {
            WorkExperience work = cellData.getValue();
            if (work != null && work.getAppointmentStatus() != null && !work.getAppointmentStatus().trim().isEmpty()) {
                return new SimpleStringProperty(work.getAppointmentStatus());
            } else {
                return new SimpleStringProperty("");
            }
        });

        // FIX: Government Service column
        workGovServiceCol.setCellValueFactory(cellData -> {
            WorkExperience work = cellData.getValue();
            if (work != null) {
                return new SimpleStringProperty(work.isGovernmentService() ? "Yes" : "No");
            } else {
                return new SimpleStringProperty("");
            }
        });
       
    }

    public void setEmployeeData(User user) {
        this.currentUser = user;
        loadEmployeeData();
    }

    private void loadEmployeeData() {
        if (currentUser == null) return;

        // Set form title with properly formatted name
        String formattedName = formatFullName(currentUser.getFname(), currentUser.getMname(), 
                                            currentUser.getLname(), currentUser.getSuffix());
        formTitleLabel.setText("Employee Details - " + formattedName);
        
        if (currentUser.getWorkExperiences() != null) {
            System.out.println("=== WORK EXPERIENCE DATA ===");
            for (WorkExperience work : currentUser.getWorkExperiences()) {
                System.out.println("Position: " + work.getPositionTitle());
                System.out.println("Company: " + work.getCompany());
                System.out.println("Monthly Salary: " + work.getMonthlySalary());
                System.out.println("Salary Grade: " + work.getSalaryGrade());
                System.out.println("Appointment Status: " + work.getAppointmentStatus());
                System.out.println("Government Service: " + work.isGovernmentService());
                System.out.println("---");
            }
            workExperienceTable.setItems(FXCollections.observableArrayList(currentUser.getWorkExperiences()));
        }
        // Set basic personal information
        surnameLabel.setText(currentUser.getLname());
        firstNameLabel.setText(currentUser.getFname());
        middleNameLabel.setText(currentUser.getMname());
        
        // Fix the suffix display - don't show "None"
        String suffix = currentUser.getSuffix();
        if (suffix == null || suffix.isEmpty() || "None".equals(suffix)) {
            suffixLabel.setText("");
        } else {
            suffixLabel.setText(suffix);
        }
        
        if (currentUser.getBirthDate() != null) {
            birthDateLabel.setText(currentUser.getBirthDate().format(dateFormatter));
        }
        
        placeOfBirthLabel.setText(getDisplayValue(currentUser.getPlaceOfBirth()));
        sexLabel.setText(getDisplayValue(currentUser.getSex()));
        
        // Height and Weight
        if (currentUser.getHeight() != null) {
            heightLabel.setText(currentUser.getHeight() + " m");
        } else {
            heightLabel.setText("");
        }
        
        if (currentUser.getWeight() != null) {
            weightLabel.setText(currentUser.getWeight() + " kg");
        } else {
            weightLabel.setText("");
        }
        if (currentUser.getPosition() != null && !currentUser.getPosition().isEmpty()) {
            positionHeaderLabel.setText(currentUser.getPosition() + " | " + currentUser.getDepartment());
        } else {
            positionHeaderLabel.setText("Employee Details");
        }
        
        civilStatusLabel.setText(getDisplayValue(currentUser.getCivilStatus()));
        bloodTypeLabel.setText(getDisplayValue(currentUser.getBloodType()));
        citizenshipLabel.setText(getDisplayValue(currentUser.getCitizenship()));

        // Contact Information
        mobileNoLabel.setText(getDisplayValue(currentUser.getContactNum()));
        telephoneNoLabel.setText(getDisplayValue(currentUser.getTelephoneNo()));
        emailLabel.setText(getDisplayValue(currentUser.getEmail()));

        // Government IDs
        gsisIdLabel.setText(getDisplayValue(currentUser.getGsisId()));
        pagibigIdLabel.setText(getDisplayValue(currentUser.getPagibigId()));
        philhealthNoLabel.setText(getDisplayValue(currentUser.getPhilhealthNo()));
        sssNoLabel.setText(getDisplayValue(currentUser.getSssNo()));
        tinNoLabel.setText(getDisplayValue(currentUser.getTinNo()));
        agencyEmployeeNoLabel.setText(getDisplayValue(currentUser.getAgencyEmployeeNo()));

        // System Account
        privilegeLabel.setText(getDisplayValue(currentUser.getPrivilege()));

        // Parse and set addresses
        parseAndSetAddress(currentUser.getResidentialAddress(), true);
        parseAndSetAddress(currentUser.getPermanentAddress(), false);

        // Set image
        if (currentUser.getImage() != null) {
            userImageView.setImage(ImageUtil.byteArrayToImage(currentUser.getImage()));
            userImageViewLarge.setImage(ImageUtil.byteArrayToImage(currentUser.getImage()));
        }

        // Load family background
        loadFamilyBackground();
        loadDocuments();
        loadCurrentAssignment();
        loadScorecard();
        
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
    private void loadScorecard() {
        if (currentUser == null) return;

        new Thread(() -> {
            int year = LocalDate.now().getYear();
            // Assuming this returns list of all employees; filter manually loop
            ObservableList<Attendance> yearAttendance = Attendance.getAttendanceByFilters("All", year, null);
            
            // Stats Variables
            long totalPresent = 0;
            long totalAbsences = 0;
            long totalLates = 0;
            double totalOtHours = 0.0; // NEW: OT Counter
            
            int absenceSpells = 0;
            boolean currentlyInAbsenceSpell = false;
            
            int[] monthlyPresence = new int[12]; 
            Map<String, Integer> latesByDay = new java.util.TreeMap<>(); 
            javafx.scene.chart.XYChart.Series<String, Number> arrivalSeries = new javafx.scene.chart.XYChart.Series<>();
            
            String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
            for (String d : daysOfWeek) latesByDay.put(d, 0);

            String uLname = currentUser.getLname().toLowerCase();
            String uFname = currentUser.getFname().toLowerCase();

            for (Attendance att : yearAttendance) {
                // Name Matching
                String attName = att.getName().toLowerCase();
                if (!attName.contains(uLname) || !attName.contains(uFname)) continue; 

                String status = att.getAttendance_status().toLowerCase();
                LocalDate date = ((java.sql.Date)att.getDate()).toLocalDate();
                
                // --- Present Logic ---
                if (status.contains("present") || status.contains("late") || status.contains("no out")) {
                    totalPresent++;
                    currentlyInAbsenceSpell = false; 
                    
                    int monthIdx = date.getMonthValue() - 1;
                    monthlyPresence[monthIdx]++;
                    
                    // Arrival Trend
                    if (att.getTimeInAm() != null && !att.getTimeInAm().contains("--")) {
                        double timeVal = parseTimeToDecimal(att.getTimeInAm());
                        if (timeVal > 0) {
                            String dateLbl = date.format(DateTimeFormatter.ofPattern("MMM dd"));
                            arrivalSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(dateLbl, timeVal));
                        }
                    }
                    
                    // NEW: OT Calculation
                    if (att.getOvertimeIn() != null && att.getOvertimeOut() != null && 
                        !att.getOvertimeIn().contains("--") && !att.getOvertimeOut().contains("--")) {
                        double start = parseTimeToDecimal(att.getOvertimeIn());
                        double end = parseTimeToDecimal(att.getOvertimeOut());
                        if (end > start) {
                            totalOtHours += (end - start);
                        }
                    }
                } 
                
                // --- Late Logic ---
                if (status.contains("late")) {
                    totalLates++;
                    String dayName = date.getDayOfWeek().toString(); 
                    for(String d : daysOfWeek) {
                        if (dayName.equalsIgnoreCase(d)) {
                            latesByDay.put(d, latesByDay.get(d) + 1);
                        }
                    }
                }
                
                // --- Absent Logic ---
                if (status.contains("absent")) {
                    totalAbsences++;
                    if (!currentlyInAbsenceSpell) {
                        absenceSpells++;
                        currentlyInAbsenceSpell = true;
                    }
                }
            }

            // Calculations
            int bradfordScore = (absenceSpells * absenceSpells) * (int)totalAbsences;
            int estimatedWorkDays = LocalDate.now().getDayOfYear() - (LocalDate.now().getDayOfYear() / 7 * 2); 
            double rate = (estimatedWorkDays > 0) ? ((double)totalPresent / estimatedWorkDays) * 100 : 0;

            // --- NEW: Generate Executive Summary Text ---
            String scoreStatus = (bradfordScore <= 50) ? "excellent" : (bradfordScore <= 200) ? "concerning" : "critical";
            
            String worstDay = "N/A";
            int maxLateVal = 0;
            for (Map.Entry<String, Integer> entry : latesByDay.entrySet()) {
                if (entry.getValue() > maxLateVal) {
                    maxLateVal = entry.getValue();
                    worstDay = entry.getKey();
                }
            }
            
            String summaryText = String.format(
                "EXECUTIVE SUMMARY: %s has an attendance rate of %.0f%%. Their reliability score is %d, which is considered %s. " +
                "They have accumulated %.1f hours of overtime this year. " + 
                (maxLateVal > 0 ? "Be aware that this employee is most frequently late on " + worstDay + "s." : "No significant tardiness patterns detected."),
                currentUser.getLname(), rate, bradfordScore, scoreStatus, totalOtHours
            );

            // Final Variables for UI
            final long finalLates = totalLates;
            final long finalPresent = totalPresent;
            final long finalAbsences = totalAbsences;
            final double finalOt = totalOtHours;

            javafx.application.Platform.runLater(() -> {
                // Update Cards
                scoreLabel.setText(String.valueOf(bradfordScore));
                updateScoreColor(bradfordScore); 
                latesLabel.setText(String.valueOf(finalLates));
                attendanceRateLabel.setText(String.format("%.0f%%", rate));
                
                // NEW: Update OT Label
                if (otLabel != null) otLabel.setText(String.format("%.1f", finalOt));
                
                // NEW: Update Summary Label
                if (analyticsSummaryLabel != null) analyticsSummaryLabel.setText(summaryText);

                // Update Charts
                javafx.scene.chart.XYChart.Series<String, Number> monthSeries = new javafx.scene.chart.XYChart.Series<>();
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                for (int i = 0; i < 12; i++) {
                    monthSeries.getData().add(new javafx.scene.chart.XYChart.Data<>(months[i], monthlyPresence[i]));
                }
                attendanceBarChart.getData().clear();
                attendanceBarChart.getData().add(monthSeries);

                // FIX: Add Numbers to Pie Chart Labels
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
                if (finalPresent > 0) pieData.add(new PieChart.Data("Present: " + finalPresent, finalPresent));
                if (finalAbsences > 0) pieData.add(new PieChart.Data("Absent: " + finalAbsences, finalAbsences));
                if (finalLates > 0) pieData.add(new PieChart.Data("Late: " + finalLates, finalLates));
                statusPieChart.setData(pieData);
                
                if (arrivalTrendChart != null) {
                    arrivalTrendChart.getData().clear();
                    arrivalSeries.setName("Arrival Time");
                    arrivalTrendChart.getData().add(arrivalSeries);
                }

                if (dayPatternChart != null) {
                    javafx.scene.chart.XYChart.Series<String, Number> daySeries = new javafx.scene.chart.XYChart.Series<>();
                    for (String d : daysOfWeek) {
                        daySeries.getData().add(new javafx.scene.chart.XYChart.Data<>(d.substring(0, 3), latesByDay.get(d)));
                    }
                    dayPatternChart.getData().clear();
                    dayPatternChart.getData().add(daySeries);
                }
            });

        }).start();
    }
    private void updateScoreColor(int score) {
        if (score <= 50) {
            scoreLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 48;");
            scoreMessageLabel.setText("Excellent Attendance");
        } else if (score <= 200) {
            scoreLabel.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-font-size: 48;");
            scoreMessageLabel.setText("Monitor / Warning");
        } else {
            scoreLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 48;");
            scoreMessageLabel.setText("Action Required");
        }
    }

    // Helper to parse "08:30 AM" to 8.5
    private double parseTimeToDecimal(String timeStr) {
        try {
            // Assume format "HH:mm" or "HH:mm:ss" or "hh:mm a"
            // Simple parsing logic: get Hour and Minute
            if(timeStr == null) return 0;
            timeStr = timeStr.replace(" AM", "").replace(" PM", "").trim();
            String[] parts = timeStr.split(":");
            double hour = Double.parseDouble(parts[0]);
            double minute = Double.parseDouble(parts[1]);
            return hour + (minute / 60.0);
        } catch (Exception e) {
            return 0;
        }
    }
    private void loadCurrentAssignment() {
        try {
            ObservableList<Assignment> activeAssignments = Assignment.getActiveAssignmentsByUserId(currentUser.getId());
            if (activeAssignments != null && !activeAssignments.isEmpty()) {
                Assignment assignment = activeAssignments.get(0);
                departmentLabel.setText(getDisplayValue(assignment.getDepartment()));
                positionLabel.setText(getDisplayValue(assignment.getPosition()));
                shiftLabel.setText(getDisplayValue(assignment.getShift()));
                timeRangeLabel.setText(getDisplayValue(assignment.getTimeRange()));
            } else {
                departmentLabel.setText("Not assigned");
                positionLabel.setText("Not assigned");
                shiftLabel.setText("Not assigned");
                timeRangeLabel.setText("Not assigned");
            }
        } catch (Exception e) {
            e.printStackTrace();
            departmentLabel.setText("Error loading assignment");
            positionLabel.setText("Error loading assignment");
            shiftLabel.setText("Error loading assignment");
            timeRangeLabel.setText("Error loading assignment");
        }
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
        
        // Only add suffix if it's not null, empty, or "None"
        if (suffix != null && !suffix.trim().isEmpty() && !"None".equals(suffix)) {
            if (name.length() > 0) name.append(" ");
            name.append(suffix.trim());
        }
        
        return name.toString();
    }

    private String getDisplayValue(String value) {
        return (value == null || value.trim().isEmpty() || "Select".equals(value)) ? "" : value.trim();
    }

    private void parseAndSetAddress(String address, boolean isResidential) {
        if (address == null || address.trim().isEmpty()) {
            // Clear all address fields
            if (isResidential) {
                residentialHouseNoLabel.setText("");
                residentialStreetLabel.setText("");
                residentialBarangayLabel.setText("");
                residentialCityLabel.setText("");
                residentialProvinceLabel.setText("");
                residentialZipCodeLabel.setText("");
            } else {
                permanentHouseNoLabel.setText("");
                permanentStreetLabel.setText("");
                permanentBarangayLabel.setText("");
                permanentCityLabel.setText("");
                permanentProvinceLabel.setText("");
                permanentZipCodeLabel.setText("");
            }
            return;
        }

        // More robust address parsing that handles missing components
        String[] parts = address.split(",");

        if (isResidential) {
            residentialHouseNoLabel.setText(parts.length > 0 ? cleanAddressPart(parts[0]) : "");
            residentialStreetLabel.setText(parts.length > 1 ? cleanAddressPart(parts[1]) : "");
            residentialBarangayLabel.setText(parts.length > 2 ? cleanAddressPart(parts[2]) : "");
            residentialCityLabel.setText(parts.length > 3 ? cleanAddressPart(parts[3]) : "");
            residentialProvinceLabel.setText(parts.length > 4 ? cleanAddressPart(parts[4]) : "");
            // Zip code might be the last part or combined with province
            if (parts.length > 5) {
                residentialZipCodeLabel.setText(cleanAddressPart(parts[5]));
            } else if (parts.length > 4) {
                // Try to extract zip code from province field if it's combined
                String provincePart = parts[4].trim();
                String[] provinceZip = provincePart.split("\\s+");
                if (provinceZip.length > 1) {
                    // Assume last part is zip code
                    residentialZipCodeLabel.setText(provinceZip[provinceZip.length - 1]);
                    // Rebuild province without zip code
                    StringBuilder province = new StringBuilder();
                    for (int i = 0; i < provinceZip.length - 1; i++) {
                        if (i > 0) province.append(" ");
                        province.append(provinceZip[i]);
                    }
                    residentialProvinceLabel.setText(province.toString());
                } else {
                    residentialZipCodeLabel.setText("");
                }
            } else {
                residentialZipCodeLabel.setText("");
            }
        } else {
            // Similar logic for permanent address
            permanentHouseNoLabel.setText(parts.length > 0 ? cleanAddressPart(parts[0]) : "");
            permanentStreetLabel.setText(parts.length > 1 ? cleanAddressPart(parts[1]) : "");
            permanentBarangayLabel.setText(parts.length > 2 ? cleanAddressPart(parts[2]) : "");
            permanentCityLabel.setText(parts.length > 3 ? cleanAddressPart(parts[3]) : "");
            permanentProvinceLabel.setText(parts.length > 4 ? cleanAddressPart(parts[4]) : "");
            if (parts.length > 5) {
                permanentZipCodeLabel.setText(cleanAddressPart(parts[5]));
            } else if (parts.length > 4) {
                String provincePart = parts[4].trim();
                String[] provinceZip = provincePart.split("\\s+");
                if (provinceZip.length > 1) {
                    permanentZipCodeLabel.setText(provinceZip[provinceZip.length - 1]);
                    StringBuilder province = new StringBuilder();
                    for (int i = 0; i < provinceZip.length - 1; i++) {
                        if (i > 0) province.append(" ");
                        province.append(provinceZip[i]);
                    }
                    permanentProvinceLabel.setText(province.toString());
                } else {
                    permanentZipCodeLabel.setText("");
                }
            } else {
                permanentZipCodeLabel.setText("");
            }
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
                                             null); // Mother typically doesn't have suffix
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

    @FXML
    private void goBack(ActionEvent event) {
        ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
        if (containerCtrl != null) {
            containerCtrl.loadFXML(paneUtil.ADMIN_EMP_MGMT);
        }
    }

    // Helper classes for table data (same as before)
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
        public String getOccupation() { return occupation; }
        public String getEmployer() { return employer; }
        public String getBusinessAddress() { return businessAddress; }
        public String getTelephone() { return telephone; }
    }

    public static class ParentInfo {
        private String parentType;
        private String fullName;

        public ParentInfo(String parentType, String fullName) {
            this.parentType = parentType;
            this.fullName = fullName;
        }

        public String getParentType() { return parentType; }
        public String getFullName() { return fullName; }
    }

    public static class ChildInfo {
        private String name;
        private String dateOfBirth;

        public ChildInfo(String name, String dateOfBirth) {
            this.name = name;
            this.dateOfBirth = dateOfBirth;
        }

        public String getName() { return name; }
        public String getDateOfBirth() { return dateOfBirth; }
    }
    private void loadDocuments() {
        documentsContainer.getChildren().clear(); // Clear old rows
        if (currentUser != null && currentUser.getEmployeeDocuments() != null) {
            for (EmployeeDocument doc : currentUser.getEmployeeDocuments()) {
                if ("Google Drive".equals(doc.getDocumentType())) {
                    googleDriveUrlLabel.setText(doc.getGoogleDriveUrl());
                } else {
                    // MODIFIED: Pass the entire 'doc' object
                    createDocumentViewRow(doc); 
                }
            }
        }
    }

    private void createDocumentViewRow(EmployeeDocument doc) {
        HBox documentRow = new HBox(10);
        documentRow.setAlignment(Pos.CENTER_LEFT);
        documentRow.setStyle("-fx-padding: 5; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        // Use the doc object for details
        Label docLabel = new Label(doc.getDocumentType() + " File");
        docLabel.setPrefWidth(150);
        docLabel.setStyle("-fx-font-weight: bold;");

        Label fileNameLabel = new Label(doc.getFileName());
        fileNameLabel.setPrefWidth(200);

        Button viewButton = new Button("View");
        viewButton.setPrefHeight(35.0);
        viewButton.setMinWidth(80.0);
        viewButton.setStyle("-fx-font-size: 14px;");

        Button downloadButton = new Button("Download");
        downloadButton.setPrefHeight(35.0);
        downloadButton.setMinWidth(80.0);
        // Kept your background color, but removed the small font size
        downloadButton.setStyle("-fx-font-size: 14px; -fx-background-color: #27ae60; -fx-text-fill: white;");

        boolean hasFile = doc.getFileData() != null;
        viewButton.setVisible(hasFile);
        downloadButton.setVisible(hasFile);

        if (hasFile) {
            viewButton.setOnAction(e -> viewDocument(doc)); 
            
            downloadButton.setOnAction(e -> downloadDocument(doc));
        }

        documentRow.getChildren().addAll(docLabel, fileNameLabel, viewButton, downloadButton);
        documentsContainer.getChildren().add(documentRow);
    }

    private void viewDocument(EmployeeDocument doc) {
        if (doc != null && doc.getFileData() != null) {
            try {
                String fileName = doc.getFileName();
                String prefix = fileName;
                String suffix = null;

                int dotIndex = fileName.lastIndexOf('.');
                if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
                    prefix = fileName.substring(0, dotIndex);
                    suffix = fileName.substring(dotIndex); // e.g., ".xlsx" or ".pdf"
                }

                
                File tempFile = File.createTempFile(prefix + "-", suffix);
               
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                 
                    fos.write(doc.getFileData());
                }

                // Now, Desktop.open() will use Excel for ".xlsx" files, not Notepad
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop.getDesktop().open(tempFile);
                }
            } catch (IOException ex) {
                Modal.alert("Error", "Failed to open document: " + ex.getMessage());
            }
        }
    }
    private void downloadDocument(EmployeeDocument doc) {
        if (doc == null || doc.getFileData() == null) {
            Modal.alert("Error", "No file data found for this document.");
            return;
        }

        // 1. Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Document");
        fileChooser.setInitialFileName(doc.getFileName()); // Suggest the original name

        // 2. Show the save dialog
        File file = fileChooser.showSaveDialog(documentsContainer.getScene().getWindow());

        // 3. If the user picked a location, save the file
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {

                fos.write(doc.getFileData()); 

                Modal.inform("Success", "File downloaded successfully!", file.getAbsolutePath());
            } catch (IOException e) {
                Modal.alert("Error", "Failed to save file: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}