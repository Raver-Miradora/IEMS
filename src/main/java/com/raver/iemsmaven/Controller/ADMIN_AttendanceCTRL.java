/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Utilities.DatabaseUtil;
import com.raver.iemsmaven.Utilities.DateUtil;
import com.raver.iemsmaven.Utilities.PaneUtil;
import com.raver.iemsmaven.Model.*;
import static com.raver.iemsmaven.Model.Attendance.getAdministrative;
import static com.raver.iemsmaven.Model.Attendance.getAttendance;
import static com.raver.iemsmaven.Model.Attendance.getInstruction;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.sql.Date;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

import com.dlsc.gemsfx.YearMonthPicker;
import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import java.time.YearMonth;
import javafx.scene.control.TableCell;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.print.PrinterJob;
import javafx.print.PageLayout;
import javafx.scene.transform.Scale;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.text.TextAlignment;
import javafx.geometry.Insets;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.raver.iemsmaven.Utilities.Modal;
import java.time.DayOfWeek;
import javafx.beans.binding.Bindings;
import static org.apache.commons.lang3.StringUtils.truncate;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;
import java.nio.charset.StandardCharsets;
import com.raver.iemsmaven.Utilities.Config;
/**
 *
 * @author User
 */
public class ADMIN_AttendanceCTRL implements Initializable {
    @FXML
    private TableView<Attendance> adminTableView;
    @FXML
    private TableColumn<Attendance, String> status;
    @FXML
    private TableColumn<Attendance, String> nameCol, amInCol, amOutCol, pmInCol, pmOutCol, otInCol, otOutCol, posCol;
    @FXML
    private TableColumn<Attendance, Date> dateCol;
    @FXML
    private TableColumn<Attendance, String> empIdCol;
    @FXML
    private Button printBtn;
    @FXML
    private Button exportExcelBtn;
    @FXML
    private Button exportPdfBtn;
    @FXML
    private DateRangePicker dateRangePicker;
    @FXML
    private ChoiceBox<Department> departmentChoiceBox;
    

    DatabaseUtil dbMethods = new DatabaseUtil();
    PaneUtil method = new PaneUtil();
    @FXML
    private Button exportTextBtn;
    private final Department ALL_DEPARTMENTS = new Department(0, "All Departments");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateRangePicker.setStyle("-fx-content-display: graphic-only;");

        dateRangePicker.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null) {
                // Lookup the button and remove text
                Node button = dateRangePicker.lookup(".button");
                if (button != null) {
                    button.setStyle("-fx-content-display: graphic-only;");
                }
            }
        });
        exportPdfBtn.setOnAction(e -> handleExportPdf());
        adminTableView.itemsProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("agencyEmployeeNo"));
        empIdCol.setStyle("-fx-alignment: CENTER; -fx-text-fill: #3b3b3b;");
        
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        amInCol.setCellValueFactory(new PropertyValueFactory<>("timeInAm"));
        amOutCol.setCellValueFactory(new PropertyValueFactory<>("timeOutAm"));
        pmInCol.setCellValueFactory(new PropertyValueFactory<>("timeInPm"));
        pmOutCol.setCellValueFactory(new PropertyValueFactory<>("timeOutPm"));
        otInCol.setCellValueFactory(new PropertyValueFactory<>("overtimeIn"));
        otOutCol.setCellValueFactory(new PropertyValueFactory<>("overtimeOut"));
        status.setCellValueFactory(new PropertyValueFactory<>("attendance_status"));
        posCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        
        nameCol.setCellFactory(column -> new TableCell<Attendance, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                } else {
                    int currentIndex = getIndex();
                    if (currentIndex > 0 && getTableView().getItems() != null && currentIndex < getTableView().getItems().size()) { // Added null/bounds check
                        Attendance currentAttendance = getTableView().getItems().get(currentIndex);
                        Attendance previousAttendance = getTableView().getItems().get(currentIndex - 1);
                        // Add null checks for names
                        if (currentAttendance.getName() != null && currentAttendance.getName().equals(previousAttendance.getName())) {
                            setText("");
                        } else {
                            setText(item);
                        }
                    } else {
                        setText(item);
                    }
                }
            }
        });

        // Set custom cell factories to format time display (without AM/PM)
        setTimeCellFactory12Hour(amInCol);
        setTimeCellFactory12Hour(amOutCol);
        setTimeCellFactory12Hour(pmInCol);
        setTimeCellFactory12Hour(pmOutCol);
        setTimeCellFactory12Hour(otInCol);
        setTimeCellFactory12Hour(otOutCol);
        
        setupFilterControls();
        
        // Initialize date range picker
        initializeDateRangePicker();

        loadAttendanceTable();
        adminTableView.itemsProperty().addListener((obs, oldVal, newVal) -> updateButtonStates());
        updateButtonStates();

    }
    

    private void initializeDateRangePicker() {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now();
        DateRange defaultRange = new DateRange(startDate, endDate);
        dateRangePicker.setValue(defaultRange);

        dateRangePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getStartDate() != null && newVal.getEndDate() != null) {
                loadAttendanceTable(); 
            } else {
                adminTableView.setItems(FXCollections.observableArrayList()); // Clear table if range is invalid/cleared
                updateButtonStates(); 
            }
        });
    }
    private void setupFilterControls() {
        departmentChoiceBox.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department dept) {
                return (dept == null || dept.getDepartmentName() == null) ? "" : dept.getDepartmentName();
            }
            @Override
            public Department fromString(String string) { return null; }
        });
        
        departmentChoiceBox.getItems().add(ALL_DEPARTMENTS);
        departmentChoiceBox.getItems().addAll(Department.getActiveDepartments());
        departmentChoiceBox.setValue(ALL_DEPARTMENTS);

        // Add listener to reload table when department changes
        departmentChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadAttendanceTable();
        });
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

   
    private void setTimeCellFactory12Hour(TableColumn<Attendance, String> column) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a"); 
        column.setCellFactory(col -> new TableCell<Attendance, String>() {
            @Override
            protected void updateItem(String time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null || time.trim().isEmpty() || time.equals("00:00:00")) {
                    setText("--");
                } else {
                    try {
                        LocalTime localTime = LocalTime.parse(time);
                        setText(localTime.format(timeFormatter)); 
                    } catch (Exception e) {
                        setText(time); 
                    }
                }
            }
        });
    }

   
  
    @FXML
    public void loadAttendanceTable() {
        LocalDate startDate = null;
        LocalDate endDate = null;
        DateRange dateRange = dateRangePicker.getValue();
        
        Department selectedDeptObj = departmentChoiceBox.getValue();
        if (selectedDeptObj == null) {
            selectedDeptObj = ALL_DEPARTMENTS; // Ensure not null
        }
        
        if (dateRange != null && dateRange.getStartDate() != null && dateRange.getEndDate() != null) {
            startDate = dateRange.getStartDate();
            endDate = dateRange.getEndDate();
        } else {
            adminTableView.setItems(FXCollections.observableArrayList());
            updateButtonStates();
            return;
        }

        // 1. Fetch all data needed in advance
        Map<LocalDate, Boolean> holidayMap = getHolidayMap(startDate, endDate);
        Map<Integer, Map<LocalDate, String>> userLeaveMaps = prefetchAllUserLeaves(startDate, endDate);
        
        // 2. Fetch all raw attendance data for all users in the range and department
        // We will replace the old getAttendanceByDateRange with this logic
        ObservableList<Attendance> rawAttendance = getRawAttendanceData(startDate, endDate, selectedDeptObj);
        
        // 3. Create a map for fast lookup: Map<UserID, Map<LocalDate, Attendance>>
        Map<Integer, Map<LocalDate, Attendance>> attendanceMap = new HashMap<>();
        Set<Integer> userIdsWithAttendance = new HashSet<>();
        for (Attendance att : rawAttendance) {
            userIdsWithAttendance.add(att.getId());
            Map<LocalDate, Attendance> userMap = attendanceMap.computeIfAbsent(att.getId(), k -> new HashMap<>());
            userMap.put(((java.sql.Date) att.getDate()).toLocalDate(), att);
        }

        // 4. Get the complete list of users who *should* be in the report
        ObservableList<User> usersToDisplay = User.getActiveEmployeesWithDepartmentAndPosition();
        
        // 5. Build the final list by looping through all users and all days
        ObservableList<Attendance> finalAttendanceList = FXCollections.observableArrayList();
        
        for (User user : usersToDisplay) {
            // Filter by department
            if (selectedDeptObj.getId() != 0 && !selectedDeptObj.getDepartmentName().equals(user.getDepartment())) {
                continue; // Skip user if not in selected department
            }
            
            // Get this user's specific leave map
            Map<LocalDate, String> userLeaveMap = userLeaveMaps.getOrDefault(user.getId(), Collections.emptyMap());

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                String status = "Absent"; // Default status
                Attendance newAttEntry = null;

                // Check for existing attendance record
                if (attendanceMap.containsKey(user.getId()) && attendanceMap.get(user.getId()).containsKey(date)) {
                    newAttEntry = attendanceMap.get(user.getId()).get(date);
                    status = newAttEntry.getAttendance_status(); // Use status from DB (Present, Late, etc.)
                
                // Check for leave
                } else if (userLeaveMap.containsKey(date)) {
                    status = userLeaveMap.get(date); // e.g., "Sick Leave", "Vacation Leave"
                
                // Check for holiday
                } else if (holidayMap.containsKey(date)) {
                    status = "Holiday";
                
                // Check for weekend
                } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    status = "Rest Day";
                }

                // Create the row for the table
                if (newAttEntry == null) {
                    // Create a new blank entry for Absent, Holiday, Leave, or Rest Day
                    newAttEntry = new Attendance(
                        user.getFullName(),
                        java.sql.Date.valueOf(date),
                        null, null, null, null, // AM/PM times
                        status
                    );
                    newAttEntry.setId(user.getId());
                    newAttEntry.setAgencyEmployeeNo(user.getAgencyEmployeeNo());
                    newAttEntry.setPosition(user.getPosition());
                } else {
                    // We used the existing record, just ensure its status is final
                    // (The query already sets Present/Late/No Out, so we just need to catch leaves/holidays that might *also* have attendance)
                    if (userLeaveMap.containsKey(date)) {
                         newAttEntry.setAttendance_status(userLeaveMap.get(date) + " (w/ Record)");
                    } else if (holidayMap.containsKey(date)) {
                         newAttEntry.setAttendance_status(newAttEntry.getAttendance_status() + " (Holiday)");
                    } else if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                         newAttEntry.setAttendance_status(newAttEntry.getAttendance_status() + " (Rest Day)");
                    }
                }
                
                finalAttendanceList.add(newAttEntry);
            }
        }
        
        // 6. Sort the final list (by Date DESC, then Name ASC)
        FXCollections.sort(finalAttendanceList, (a1, a2) -> {
            int dateCompare = a2.getDate().compareTo(a1.getDate());
            if (dateCompare != 0) {
                return dateCompare;
            }
            String name1 = a1.getName() == null ? "" : a1.getName();
            String name2 = a2.getName() == null ? "" : a2.getName();
            return name1.compareTo(name2);
        });

        // 7. Set the table items
        adminTableView.setItems(finalAttendanceList);
        updateButtonStates();
    }

    private ObservableList<Attendance> getRawAttendanceData(LocalDate startDate, LocalDate endDate, Department department) {
        ObservableList<Attendance> attendance = FXCollections.observableArrayList();
        
        // We inject AES_DECRYPT for every time column
        // We utilize CAST(... AS CHAR) to ensure the result is treated as a Time String, not a Blob
        String sql = "SELECT u.user_id, CONCAT(u.user_lname, ', ', u.user_fname) AS name, c.date, " +
                     "p.position_name AS position, d.department_name AS department, " +
                     "AES_DECRYPT(u.agency_employee_no, ?) AS agency_employee_no, " + // Key #1
            
                     // Decrypt AM IN
                     "MAX(CASE WHEN c.time_notation = 'AM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) AS am_in, " + // Key #2
                     // Decrypt AM OUT
                     "MAX(CASE WHEN c.time_notation = 'AM' THEN CAST(AES_DECRYPT(c.time_out, ?) AS CHAR) END) AS am_out, " + // Key #3
                     // Decrypt PM IN
                     "MAX(CASE WHEN c.time_notation = 'PM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) AS pm_in, " + // Key #4
                     // Decrypt PM OUT
                     "MAX(CASE WHEN c.time_notation = 'PM' THEN CAST(AES_DECRYPT(c.time_out, ?) AS CHAR) END) AS pm_out, " + // Key #5
                     
                     // Decrypt Overtime
                     "MAX(CAST(AES_DECRYPT(c.overtime_in, ?) AS CHAR)) AS ot_in, " + // Key #6
                     "MAX(CAST(AES_DECRYPT(c.overtime_out, ?) AS CHAR)) AS ot_out, " + // Key #7
                     
                     // Status Logic (Must also check decrypted values)
                     "CASE " +
                     "  WHEN MAX(c.attendance_status) = 3 THEN 'No Out' " +
                     "  WHEN MAX(c.attendance_status) = 2 THEN 'Late' " +
                     "  WHEN (MAX(CASE WHEN c.time_notation = 'AM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) IS NOT NULL OR " + // Key #8
                     "        MAX(CASE WHEN c.time_notation = 'PM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) IS NOT NULL OR " + // Key #9
                     "        MAX(CAST(AES_DECRYPT(c.overtime_in, ?) AS CHAR)) IS NOT NULL) THEN 'Present' " + // Key #10
                     "  ELSE 'Absent' " +
                     "END AS attendance_status " +
      
                     "FROM attendance c " +
                     "JOIN user u ON c.user_id = u.user_id " +
                     "JOIN assignment a ON u.user_id = a.user_id AND a.status = 1 " +
                     "JOIN position p ON a.position_id = p.position_id " +
                     "JOIN department d ON p.department_id = d.department_id " +
                     "WHERE u.user_status = 1 AND c.date BETWEEN ? AND ? "; // Date Params
        
        if (department != null && department.getId() != 0) {
            sql += "AND d.department_id = ? ";
        }

        sql += "GROUP BY u.user_id, c.date, p.position_name, d.department_name, agency_employee_no " + 
               "ORDER BY c.date DESC, name ASC";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            String key = Config.getSecretKey(); // Get the decryption key
            int paramIndex = 1;
            
            // 1. Set Key for Agency ID
            statement.setString(paramIndex++, key); 
            
            // 2-7. Set Key for Time Columns (AM/PM/OT In/Out)
            for(int i=0; i<6; i++) {
                statement.setString(paramIndex++, key);
            }
            
            // 8-10. Set Key for Status Checks (AM In, PM In, OT In)
            for(int i=0; i<3; i++) {
                statement.setString(paramIndex++, key);
            }

            // Set Dates
            statement.setDate(paramIndex++, java.sql.Date.valueOf(startDate));
            statement.setDate(paramIndex++, java.sql.Date.valueOf(endDate));
            
            // Set Department if selected
            if (department != null && department.getId() != 0) {
                statement.setInt(paramIndex++, department.getId());
            }

            ResultSet rs = statement.executeQuery();
         
            while (rs.next()) {
                Attendance a = new Attendance(
                    rs.getString("name"), rs.getDate("date"),
                    rs.getString("am_in"), rs.getString("am_out"),
                    rs.getString("pm_in"), rs.getString("pm_out"),
                    rs.getString("attendance_status")
                );
                a.setId(rs.getInt("user_id"));
                // Decrypted agency number is now a standard string result
                a.setAgencyEmployeeNo(bytesToString(rs.getBytes("agency_employee_no"))); 
                a.setOvertimeIn(rs.getString("ot_in"));
                a.setOvertimeOut(rs.getString("ot_out"));
                a.setPosition(rs.getString("position"));
                a.setDeptName(rs.getString("department"));
                attendance.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Failed to load attendance data: " + e.getMessage());
        }
        return attendance;
    }
    


    // Helper: format a raw name ("First Last" or other) into display "Last, First"
    private String formatDisplayName(String rawName) {
        if (rawName == null) return "";
        rawName = rawName.trim();
        // If it's already in the form "Last, First" just return
        if (rawName.contains(",")) {
            return rawName;
        }
        String[] parts = rawName.split("\\s+");
        if (parts.length == 1) return parts[0];
        String last = parts[parts.length - 1];
        StringBuilder first = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (i > 0) first.append(" ");
            first.append(parts[i]);
        }
        return last + ", " + first.toString();
    }

    // Helper: extract last name from a display name "Last, First" (or raw names as fallback)
    private String extractLastNameFromDisplay(String displayName) {
        if (displayName == null) return "";
        if (displayName.contains(",")) {
            return displayName.split(",")[0].trim();
        }
        // fallback: take last token
        String[] parts = displayName.trim().split("\\s+");
        return parts[parts.length - 1];
    }

    private void updateButtonStates() {
        boolean hasData = adminTableView.getItems() != null && 
                         !adminTableView.getItems().isEmpty();

        printBtn.setDisable(!hasData);
        exportTextBtn.setDisable(!hasData);
        exportExcelBtn.setDisable(!hasData);
        exportPdfBtn.setDisable(!hasData);
    }
    @FXML
    private void handleExportPdf() {
        if (adminTableView.getItems() == null || adminTableView.getItems().isEmpty()) {
            Modal.alert("Nothing to Export", "There is no log data currently displayed.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Logs to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("attendance_logs_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf");

        File file = fileChooser.showSaveDialog(adminTableView.getScene().getWindow());
        if (file == null) {
            return;
        }

        // --- Define Fonts ---
        // Use fully qualified names to avoid conflict with javafx.scene.text.Font
        com.itextpdf.text.Font lguFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        com.itextpdf.text.Font deptFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
        com.itextpdf.text.Font dateFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GRAY);
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE);
        com.itextpdf.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK); // Smaller font for data

        // Use Landscape mode to fit all columns
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36); // Left, Right, Top, Bottom margins

        try (FileOutputStream fos = new FileOutputStream(file)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            // --- 1. Add LGU Name ---
            Paragraph lgu = new Paragraph("LGU LAGONOY", lguFont);
            lgu.setAlignment(Element.ALIGN_CENTER);
            document.add(lgu);

            // --- 2. Add Report Title ---
            Paragraph title = new Paragraph("Attendance Logs", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // --- 3. Add Department ---
            String deptName = "All Departments"; // Default
            // Check if the departmentChoiceBox is available and has a value
            if (departmentChoiceBox != null && departmentChoiceBox.getValue() != null) {
                 deptName = departmentChoiceBox.getValue().toString();
                 if (deptName.isEmpty() || deptName.equalsIgnoreCase("All")) {
                     deptName = "All Departments";
                 }
            }
            Paragraph dept = new Paragraph("Department: " + deptName, deptFont);
            dept.setAlignment(Element.ALIGN_CENTER);
            document.add(dept);

            // --- 4. Add Date Range ---
            Paragraph dateRange = new Paragraph(getSelectedDateRangeText(), dateFont);
            dateRange.setAlignment(Element.ALIGN_CENTER);
            document.add(dateRange);

            // --- 5. Add Generated Timestamp ---
            Paragraph generated = new Paragraph("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), dateFont);
            generated.setAlignment(Element.ALIGN_CENTER);
            document.add(generated);
            
            document.add(new Paragraph("\n")); // Add space before table

            // --- 6. Create Table ---
            int numColumns = adminTableView.getColumns().size();
            PdfPTable table = new PdfPTable(numColumns);
            table.setWidthPercentage(100);

            // Define relative widths (11 columns: ID, Name, Date, 6x Time, Status, Pos)
            float[] columnWidths = {1.0f, 2.3f, 1.2f, 1f, 1f, 1f, 1f, 1f, 1f, 1.2f, 2.8f}; 
            table.setWidths(columnWidths);

            // --- 7. Write Table Headers ---
            for (TableColumn<Attendance, ?> col : adminTableView.getColumns()) {
                PdfPCell headerCell = new PdfPCell(new Phrase(col.getText(), headerFont));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }
            table.setHeaderRows(1);

            // --- 8. Write Table Data ---
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            for (Attendance att : adminTableView.getItems()) {
                // Add Employee ID cell
                table.addCell(createPdfCell(att.getAgencyEmployeeNo(), bodyFont, Element.ALIGN_CENTER)); // Changed from att.getId()
                
                table.addCell(createPdfCell(att.getName(), bodyFont, Element.ALIGN_LEFT));
                table.addCell(createPdfCell(att.getDate() != null ? att.getDate().toString() : "", bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(formatTableTime(att.getTimeInAm(), timeFmt), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(formatTableTime(att.getTimeOutAm(), timeFmt), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(formatTableTime(att.getTimeInPm(), timeFmt), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(formatTableTime(att.getTimeOutPm(), timeFmt), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(formatTableTime(att.getOvertimeIn(), timeFmt), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(formatTableTime(att.getOvertimeOut(), timeFmt), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(att.getAttendance_status(), bodyFont, Element.ALIGN_CENTER));
                table.addCell(createPdfCell(att.getPosition(), bodyFont, Element.ALIGN_LEFT));
            }

            document.add(table);
            document.close();
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String target = getReportTargetDescription(); // Use helper
                String description = "Generated PDF attendance log for " + target;
                
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), description); 
            } catch (Exception logEx) {
                System.err.println("Failed to log PDF Export: " + logEx.getMessage());
            }
            Modal.inform("Export Successful", "Logs exported successfully to PDF file.", file.getAbsolutePath());

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Modal.alert("Export Failed", "Could not write to PDF file: " + e.getMessage());
        }
    }
    private static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Helper method to create a formatted PdfPCell for the PDF table.
     */
    private PdfPCell createPdfCell(String text, com.itextpdf.text.Font font, int horizontalAlignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font)); // Added null check for safety
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }
    private String formatTableTime(String timeStr, DateTimeFormatter formatter) {
        if (timeStr == null || timeStr.trim().isEmpty() || timeStr.equals("00:00:00") || timeStr.equals("--")) {
            return "--"; // Return "--" for empty/null values
        }
        try {
            LocalTime localTime = LocalTime.parse(timeStr);
            return localTime.format(formatter);
        } catch (Exception e) {
            return timeStr; 
        }
    }

    @FXML
    private void handlePrint() {
        // Show preview modal
        Stage owner = (Stage) adminTableView.getScene().getWindow();
        showPrintPreview(owner);
    }

    private void showPrintPreview(Stage owner) {
        if (adminTableView.getItems() == null || adminTableView.getItems().isEmpty()) {
            showErrorAlert("Nothing to Print", "There are no records to print. Make a selection from the tree view first.");
            return;
        }

        // Build printable node (title + printable table)
        VBox printable = buildPrintableNode();

        // Wrap in a scroll pane for previewing large tables
        ScrollPane scroll = new ScrollPane(printable);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        // Zoom slider (50% - 150%)
        Slider zoom = new Slider(0.5, 1.5, 1.0);
        zoom.setPrefWidth(250);
        Label zoomLabel = new Label("Zoom:");
        HBox zoomBox = new HBox(8, zoomLabel, zoom);
        zoomBox.setPadding(new Insets(6));
        zoomBox.setAlignment(Pos.CENTER_LEFT);

        // Buttons: Print and Close
        Button printBtn = new Button("Print");
        Button closeBtn = new Button("Close");

        HBox controls = new HBox(10, zoomBox, printBtn, closeBtn);
        controls.setPadding(new Insets(8));
        controls.setAlignment(Pos.CENTER_RIGHT);

        BorderPane root = new BorderPane();
        root.setCenter(scroll);
        root.setBottom(controls);

        Scene scene = new Scene(root, 1000, 700);
        Stage previewStage = new Stage();
        previewStage.initOwner(owner);
        previewStage.initModality(Modality.APPLICATION_MODAL);
        previewStage.setTitle("Print Preview");
        previewStage.setScene(scene);

        // Apply scale transform to printable for zooming
        Scale scale = new Scale(1, 1);
        printable.getTransforms().add(scale);
        zoom.valueProperty().addListener((obs, oldVal, newVal) -> {
            scale.setX(newVal.doubleValue());
            scale.setY(newVal.doubleValue());
        });

        // Print handler: open system dialog and print the printable node (current scaled view)
        printBtn.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job == null) {
                showErrorAlert("Print Error", "No printers available on this system.");
                return;
            }

            // show system dialog
            boolean proceed = job.showPrintDialog(previewStage);
            if (!proceed) return;

            try {
                // Ensure layout and CSS applied before measuring/printing
                printable.applyCss();
                printable.layout();

                // Fit to page if needed (scale to printable area if larger)
                PageLayout pageLayout = job.getJobSettings().getPageLayout();
                double printableWidth = pageLayout.getPrintableWidth();
                double printableHeight = pageLayout.getPrintableHeight();

                // Use bounds after layout
                double nodeWidth = printable.getBoundsInParent().getWidth();
                double nodeHeight = printable.getBoundsInParent().getHeight();

                // compute fit-scale (preserve current zoom)
                double currentZoom = zoom.getValue();
                double fitScale = 1.0;
                if (nodeWidth > 0 && nodeHeight > 0) {
                    double scaleX = printableWidth / (nodeWidth / currentZoom);
                    double scaleY = printableHeight / (nodeHeight / currentZoom);
                    fitScale = Math.min(1.0, Math.min(scaleX, scaleY));
                }

                // apply temporary print-scale (multiply by current zoom)
                Scale printScale = new Scale(currentZoom * fitScale, currentZoom * fitScale);
                printable.getTransforms().add(printScale);

                boolean success = job.printPage(printable);

                // remove temporary print scale
                printable.getTransforms().remove(printScale);

                if (success) {
                    job.endJob();
                    showSuccessAlert("Print Started", "The attendance report has been sent to the printer.");
                    previewStage.close(); // optional: close preview after successful print
                } else {
                    showErrorAlert("Print Failed", "Printing did not complete successfully.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showErrorAlert("Print Error", "An unexpected error occurred during printing: " + ex.getMessage());
            }
        });

        // Close preview
        closeBtn.setOnAction(e -> previewStage.close());

        previewStage.showAndWait();
    }

    private VBox buildPrintableNode() {
        // --- Get Header Info ---
        String headerText = getFormattedReportHeader();
        Label headerLabel = new Label(headerText);
        headerLabel.setStyle("-fx-font-size: 12px; -fx-text-alignment: center;");
        headerLabel.setAlignment(Pos.CENTER);
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setTextAlignment(TextAlignment.CENTER);

        // --- Create Table ---
        TableView<Attendance> printableTable = createPrintableTableView();

        // --- Combine in VBox ---
        VBox printable = new VBox(10, headerLabel, printableTable); // Use 10px spacing
        printable.setPadding(new Insets(20)); // Use Insets for padding
        printable.setPrefWidth(900);
        printable.setAlignment(Pos.TOP_CENTER);

        return printable;
    }

    private String getSelectedDateRangeText() {
        try {
            com.dlsc.gemsfx.daterange.DateRange dr = dateRangePicker.getValue();
            if (dr == null) return "";

            LocalDate start = dr.getStartDate();
            LocalDate end = dr.getEndDate();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (start != null && end != null) {
                if (start.equals(end)) {
                    return "Date: " + start.format(fmt);
                } else {
                    return "Date Range: " + start.format(fmt) + " — " + end.format(fmt);
                }
            } else if (start != null) {
                return "Date: " + start.format(fmt);
            } else if (end != null) {
                return "Date: " + end.format(fmt);
            } else {
                return "";
            }
        } catch (Exception ex) {
            // Safe fallback if dateRangePicker isn't set or unexpected value
            return "";
        }
    }
   private TableView<Attendance> createPrintableTableView() {
        TableView<Attendance> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        String baseStyle = "-fx-font-size: 9pt;";
        String centerStyle = "-fx-alignment: CENTER; -fx-font-size: 9pt;";

        // --- Manually Recreate ALL Columns ---
        
        TableColumn<Attendance, String> idCol = new TableColumn<>("Emp. ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("agencyEmployeeNo")); 
        idCol.setPrefWidth(100);
        idCol.setStyle(centerStyle);

        TableColumn<Attendance, String> nameCol = new TableColumn<>("Name");
        nameCol.setPrefWidth(230.0);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setStyle(baseStyle);
        // Note: The "show name once" cell factory is not copied to the print version.

        TableColumn<Attendance, Date> dateCol = new TableColumn<>("Date");
        dateCol.setPrefWidth(120.0);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setStyle(centerStyle);

        TableColumn<Attendance, String> amInCol = new TableColumn<>("AM IN");
        amInCol.setPrefWidth(85.0);
        amInCol.setCellValueFactory(new PropertyValueFactory<>("timeInAm"));
        amInCol.setStyle(centerStyle);

        TableColumn<Attendance, String> amOutCol = new TableColumn<>("AM OUT");
        amOutCol.setPrefWidth(85.0);
        amOutCol.setCellValueFactory(new PropertyValueFactory<>("timeOutAm"));
        amOutCol.setStyle(centerStyle);

        TableColumn<Attendance, String> pmInCol = new TableColumn<>("PM IN");
        pmInCol.setPrefWidth(85.0);
        pmInCol.setCellValueFactory(new PropertyValueFactory<>("timeInPm"));
        pmInCol.setStyle(centerStyle);

        TableColumn<Attendance, String> pmOutCol = new TableColumn<>("PM OUT");
        pmOutCol.setPrefWidth(85.0);
        pmOutCol.setCellValueFactory(new PropertyValueFactory<>("timeOutPm"));
        pmOutCol.setStyle(centerStyle);

        TableColumn<Attendance, String> otInCol = new TableColumn<>("OT IN");
        otInCol.setPrefWidth(85.0);
        otInCol.setCellValueFactory(new PropertyValueFactory<>("overtimeIn"));
        otInCol.setStyle(centerStyle);

        TableColumn<Attendance, String> otOutCol = new TableColumn<>("OT OUT");
        otOutCol.setPrefWidth(85.0);
        otOutCol.setCellValueFactory(new PropertyValueFactory<>("overtimeOut"));
        otOutCol.setStyle(centerStyle);

        TableColumn<Attendance, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(110.0);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("attendance_status"));
        statusCol.setStyle(centerStyle);

        TableColumn<Attendance, String> posCol = new TableColumn<>("Position");
        posCol.setPrefWidth(280.0);
        posCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        posCol.setStyle(baseStyle);

        // Add all new columns in order
        table.getColumns().addAll(idCol, nameCol, dateCol, amInCol, amOutCol, pmInCol, pmOutCol, otInCol, otOutCol, statusCol, posCol);
        
        // Copy data items
        table.setItems(FXCollections.observableArrayList(adminTableView.getItems()));

        // Force height calculation
        table.setFixedCellSize(20);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(Bindings.size(table.getItems()).add(1.05))); // Header row + buffer
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());

        return table;
    }


   @FXML
    private void handleExportExcel() {
        if (adminTableView.getItems() == null || adminTableView.getItems().isEmpty()) {
            Modal.alert("Nothing to Export", "There is no log data currently displayed.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report to Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"));
        
        String initialFileName = "attendance_logs_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        Department dept = departmentChoiceBox.getValue();
        if (dept != null && dept.getId() != 0) {
             initialFileName = dept.getDepartmentName().replace(" ", "_") + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        }
        fileChooser.setInitialFileName(initialFileName);


        Stage stage = (Stage) adminTableView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Attendance Logs");
                int rowIndex = 0;

                // --- Create Styles ---
                org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                titleFont.setBold(true);
                titleFont.setFontHeightInPoints((short) 16);
                CellStyle titleStyle = workbook.createCellStyle();
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);
                
                org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                CellStyle headerStyle = createHeaderStyle(workbook); // Your existing helper
                
                CellStyle centerStyle = createCenterStyle(workbook); // Your existing helper
                CellStyle defaultStyle = createNormalStyle(workbook); // Your existing helper

                // --- Write Header Info ---
                String[] headerLines = getFormattedReportHeader().split("\n");
                int numColumnsForMerge = adminTableView.getColumns().size() > 0 ? adminTableView.getColumns().size() - 1 : 10; // Get column count for merge
                
                for (String line : headerLines) {
                    Row row = sheet.createRow(rowIndex++);
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                    cell.setCellValue(line);
                    
                    if (line.equals("LGU LAGONOY") || line.equals("Attendance Logs")) {
                        cell.setCellStyle(titleStyle);
                    }
                    // Merge header cells
                    sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, numColumnsForMerge));
                }
                rowIndex++; // Add a blank row

                // --- Write Table Headers ---
                Row tableHeaderRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (TableColumn<Attendance, ?> col : adminTableView.getColumns()) {
                    org.apache.poi.ss.usermodel.Cell cell = tableHeaderRow.createCell(colIndex++);
                    cell.setCellValue(col.getText());
                    cell.setCellStyle(headerStyle);
                }

                // --- Write Table Data ---
                DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
                for (Attendance att : adminTableView.getItems()) {
                    Row dataRow = sheet.createRow(rowIndex++);
                    colIndex = 0;
                    
                    
                    createCell(dataRow, colIndex++, att.getAgencyEmployeeNo(), centerStyle); 
                    createCell(dataRow, colIndex++, att.getName(), defaultStyle);
                    createCell(dataRow, colIndex++, att.getDate() != null ? att.getDate().toString() : "", centerStyle);
                    createCell(dataRow, colIndex++, formatTableTime(att.getTimeInAm(), timeFmt), centerStyle);
                    createCell(dataRow, colIndex++, formatTableTime(att.getTimeOutAm(), timeFmt), centerStyle);
                    createCell(dataRow, colIndex++, formatTableTime(att.getTimeInPm(), timeFmt), centerStyle);
                    createCell(dataRow, colIndex++, formatTableTime(att.getTimeOutPm(), timeFmt), centerStyle);
                    createCell(dataRow, colIndex++, formatTableTime(att.getOvertimeIn(), timeFmt), centerStyle);
                    createCell(dataRow, colIndex++, formatTableTime(att.getOvertimeOut(), timeFmt), centerStyle);
                    createCell(dataRow, colIndex++, att.getAttendance_status(), centerStyle);
                    createCell(dataRow, colIndex++, att.getPosition(), defaultStyle);
                }

                // --- Auto-size Columns (with correction for Column 0) ---
                for (int i = 0; i < adminTableView.getColumns().size(); i++) {
                    if (i == 0) {
                        // Manually set "Emp. ID" column width
                        sheet.setColumnWidth(i, 256 * 15); // Approx 15 characters wide
                    } else {
                        // Autosize all other columns
                        sheet.autoSizeColumn(i);
                        // Optional: Add minimum widths for columns like Name or Position if autosize is too small
                        if (i == 1 || i == 10) { // If Name or Position
                             if (sheet.getColumnWidth(i) < 256 * 25) { // Min width of 25 chars
                                 sheet.setColumnWidth(i, 256 * 25);
                             }
                        }
                    }
                }
                try {
                    User currentAdmin = Session.getInstance().getLoggedInUser();
                    String target = getReportTargetDescription(); // Use helper
                    String description = "Generated Excel (XLSX) attendance log for " + target;
                    
                  ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), description); 
                } catch (Exception logEx) {
                    System.err.println("Failed to log Excel Export: " + logEx.getMessage());
                }
                workbook.write(fileOut);
                
            } catch (IOException e) {
                e.printStackTrace();
                Modal.alert("Export Failed", "Could not write to Excel file: " + e.getMessage());
            } catch (Exception e) {
                 e.printStackTrace();
                Modal.alert("Export Error", "An unexpected error occurred: " + e.getMessage());
            }

            if (file != null) { // Check file again just in case, though it's checked above
                 Modal.inform("Export Successful", "Logs exported successfully to Excel file.", file.getAbsolutePath());
            }
        }
    }

   private void exportToExcel(File file) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fileOut = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("Attendance Logs");
            int rowIndex = 0;

            // --- Create Styles ---
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            
            org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            CellStyle headerStyle = createHeaderStyle(workbook); // Your existing helper
            
            CellStyle centerStyle = createCenterStyle(workbook); // Your existing helper
            CellStyle defaultStyle = createNormalStyle(workbook); // Your existing helper
            
            // --- Write Header Info ---
            String[] headerLines = getFormattedReportHeader().split("\n");
            for (String line : headerLines) {
                Row row = sheet.createRow(rowIndex++);
                org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                cell.setCellValue(line);
                
                if (line.equals("LGU LAGONOY") || line.equals("Attendance Logs")) {
                    cell.setCellStyle(titleStyle);
                }
                // Merge header cells across the width of the table (11 columns)
                sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 10));
            }
            rowIndex++; // Add a blank row

            // --- Write Table Headers ---
            Row tableHeaderRow = sheet.createRow(rowIndex++);
            int colIndex = 0;
            for (TableColumn<Attendance, ?> col : adminTableView.getColumns()) {
                org.apache.poi.ss.usermodel.Cell cell = tableHeaderRow.createCell(colIndex++);
                cell.setCellValue(col.getText());
                cell.setCellStyle(headerStyle);
            }

            // --- Write Table Data ---
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            for (Attendance att : adminTableView.getItems()) {
                Row dataRow = sheet.createRow(rowIndex++);
                colIndex = 0;
                
                // Note: The "show name once" logic is NOT applied to Excel export
                createCell(dataRow, colIndex++, att.getAgencyEmployeeNo(), centerStyle);
                createCell(dataRow, colIndex++, att.getName(), defaultStyle);
                createCell(dataRow, colIndex++, att.getDate() != null ? att.getDate().toString() : "", centerStyle);
                createCell(dataRow, colIndex++, formatTableTime(att.getTimeInAm(), timeFmt), centerStyle);
                createCell(dataRow, colIndex++, formatTableTime(att.getTimeOutAm(), timeFmt), centerStyle);
                createCell(dataRow, colIndex++, formatTableTime(att.getTimeInPm(), timeFmt), centerStyle);
                createCell(dataRow, colIndex++, formatTableTime(att.getTimeOutPm(), timeFmt), centerStyle);
                createCell(dataRow, colIndex++, formatTableTime(att.getOvertimeIn(), timeFmt), centerStyle);
                createCell(dataRow, colIndex++, formatTableTime(att.getOvertimeOut(), timeFmt), centerStyle);
                createCell(dataRow, colIndex++, att.getAttendance_status(), centerStyle);
                createCell(dataRow, colIndex++, att.getPosition(), defaultStyle);
            }

            // --- Auto-size Columns ---
            for (int i = 0; i < adminTableView.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }
             // Manually set width for summary column (column 0) to be wider
            sheet.setColumnWidth(0, Math.max(sheet.getColumnWidth(0), 256 * 40));

            workbook.write(fileOut);
        }
    }
   // --- Helper methods for Excel Export ---
    private void createCell(Row row, int colIndex, String value, CellStyle style) {
         // Use fully qualified name for POI Cell
         org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIndex);
         cell.setCellValue(value != null ? value : "");
         if (style != null) {
             cell.setCellStyle(style);
         }
     }
    private void createNumericCell(Row row, int colIndex, Number value, CellStyle style) {
         // Use fully qualified name for POI Cell
         org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIndex);
         if (value != null) {
              if (value instanceof Double) {
                 cell.setCellValue(value.doubleValue());
              } else if (value instanceof Integer) {
                  cell.setCellValue(value.intValue());
              } else if (value instanceof Long) { 
                   cell.setCellValue(value.longValue());
              }
         } else {
              cell.setCellValue("");
         }
         if (style != null) {
             cell.setCellStyle(style);
         }
     }

    private String getDisplayNameForExport(int index, Attendance currentAttendance) {
        if (index > 0) {
            // Check if this is the same employee as the previous row
            Attendance previousAttendance = adminTableView.getItems().get(index - 1);
            if (currentAttendance.getName() != null && 
                currentAttendance.getName().equals(previousAttendance.getName())) {
                // Same employee as previous row, show empty (like the table view)
                return "";
            }
        }
        // Different employee or first row, show name
        return currentAttendance.getName() != null ? currentAttendance.getName() : "";
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        org.apache.poi.ss.usermodel.Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        return style;
    }

    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCenterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private String formatTimeForExcel(String time) {
        if (time == null || time.trim().isEmpty() || time.equals("00:00:00") || time.equals("--")) {
            return "--";  // Match the table view display exactly
        }
        try {
            LocalTime localTime = LocalTime.parse(time);
            // Use the same 12-hour format without AM/PM as the table view
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
            return localTime.format(formatter);
        } catch (Exception e) {
            return time; // Fallback to original text
        }
    }
    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    

    @FXML
    private void handleExportText() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to Text File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        fileChooser.setInitialFileName("attendance_report_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt");

        Stage stage = (Stage) adminTableView.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                try {
                    User currentAdmin = Session.getInstance().getLoggedInUser();
                    String target = getReportTargetDescription(); // Use helper
                    String description = "Generated TXT attendance log for " + target;
                    
                    ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFullName(), description);
                } catch (Exception logEx) {
                    System.err.println("Failed to log TXT Export: " + logEx.getMessage());
                }
                exportToText(file);
                showSuccessAlert("Export Successful", 
                    "Attendance data exported successfully to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Export Failed", 
                    "Failed to export attendance data: " + e.getMessage());
            }
        }
    }

    private void exportToText(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // --- Write Header Info ---
            writer.write(getFormattedReportHeader() + "\n\n");

            // --- Write Table Data ---
            writer.write("DETAILS\n");
            writer.write("=================\n");

            // Define fixed-width format strings
            String headerFormat = "%-10s %-25s %-12s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-20s\n";
            String dataFormat   = "%-10s %-25s %-12s %-10s %-10s %-10s %-10s %-10s %-10s %-10s %-20s\n";
            String separatorLine= "---------- ------------------------- ------------ ---------- ---------- ---------- ---------- ---------- ---------- ---------- --------------------\n";
            
            // Write column headers
            writer.write(String.format(headerFormat,
                "Emp. ID", "Name", "Date", "AM IN", "AM OUT", "PM IN", "PM OUT",
                "OT IN", "OT OUT", "Status", "Position"));
            writer.write(separatorLine);

            // Write data rows
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            // Note: We are iterating through the *table's* items, which are already sorted
            for (Attendance att : adminTableView.getItems()) {
                // The name grouping logic is NOT applied to text exports for clarity
                writer.write(String.format(dataFormat,
                    truncateString(att.getAgencyEmployeeNo(), 10),
                    truncateString(att.getName(), 25),
                    att.getDate() != null ? att.getDate().toString() : "",
                    formatTableTime(att.getTimeInAm(), timeFmt),
                    formatTableTime(att.getTimeOutAm(), timeFmt),
                    formatTableTime(att.getTimeInPm(), timeFmt),
                    formatTableTime(att.getTimeOutPm(), timeFmt),
                    formatTableTime(att.getOvertimeIn(), timeFmt),
                    formatTableTime(att.getOvertimeOut(), timeFmt),
                    truncateString(att.getAttendance_status(), 10),
                    truncateString(att.getPosition(), 20)
                ));
            }
        }
    }

    private String formatTimeForText(String time) {
        if (time == null || time.trim().isEmpty() || time.equals("00:00:00") || time.equals("--")) {
            return "--";
        }
        try {
            LocalTime localTime = LocalTime.parse(time);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
            return localTime.format(formatter);
        } catch (Exception e) {
            return time;
        }
    }

    private String truncateString(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 1) + ".";
    }
    private String getReportTargetDescription() {
        Department dept = departmentChoiceBox.getValue();
        String target;

        if (dept != null && dept.getId() != 0) {
            target = "department: " + dept.getDepartmentName();
        } else {
            target = "All Departments";
        }
        return target;
    }
    private String getFormattedReportHeader() {
        String deptName = "All Departments";
        if (departmentChoiceBox != null && departmentChoiceBox.getValue() != null) {
             deptName = departmentChoiceBox.getValue().toString();
             if (deptName.isEmpty() || deptName.equalsIgnoreCase("All") || deptName.equalsIgnoreCase("All Departments")) {
                 deptName = "All Departments";
             }
        }
        
        String dateRange = "N/A";
         try {
            com.dlsc.gemsfx.daterange.DateRange dr = dateRangePicker.getValue();
            if (dr != null && dr.getStartDate() != null && dr.getEndDate() != null) {
                LocalDate start = dr.getStartDate();
                LocalDate end = dr.getEndDate();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (start.equals(end)) {
                    dateRange = "Date: " + start.format(fmt);
                } else {
                    dateRange = "Date Range: " + start.format(fmt) + " — " + end.format(fmt);
                }
            }
        } catch (Exception ex) {
            // ignore
        }

        String generatedOn = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        StringBuilder sb = new StringBuilder();
        sb.append("LGU LAGONOY\n");
        sb.append("Attendance Logs\n");
        sb.append("Department: ").append(deptName).append("\n");
        sb.append(dateRange).append("\n");
        sb.append("Generated: ").append(generatedOn).append("\n");
        
        return sb.toString();
    }
    /**
     * NEW HELPER (Copied from Reports):
     * Gets a map of all holidays within the date range.
     */
    private Map<LocalDate, Boolean> getHolidayMap(LocalDate reportStartDate, LocalDate reportEndDate) {
        Map<LocalDate, Boolean> holidays = new HashMap<>();
        String sql = "SELECT sc.start_date, sc.end_date " +
                     "FROM special_calendar sc " +
                     "WHERE sc.status = 1 " + 
                     "AND sc.start_date <= ? " + 
                     "AND sc.end_date >= ?";   

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(reportEndDate));   
            ps.setDate(2, java.sql.Date.valueOf(reportStartDate)); 

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate holidayStart = rs.getDate("start_date").toLocalDate();
                LocalDate holidayEnd = rs.getDate("end_date").toLocalDate();

                for (LocalDate date = holidayStart; !date.isAfter(holidayEnd); date = date.plusDays(1)) {
                    if (!date.isBefore(reportStartDate) && !date.isAfter(reportEndDate)) {
                        holidays.put(date, true);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching holiday map: " + e.getMessage());
        }
        return holidays;
    }

    /**
     * NEW HELPER (Copied from Reports):
     * Pre-fetches leave information for all users in the date range.
     */
    private Map<Integer, Map<LocalDate, String>> prefetchAllUserLeaves(LocalDate reportStartDate, LocalDate reportEndDate) {
        Map<Integer, Map<LocalDate, String>> userLeaveMaps = new HashMap<>();
        String sql = "SELECT ut.user_id, ut.start_date, ut.end_date, t.timeoff_type " +
                     "FROM user_timeoff ut " +
                     "JOIN timeoff t ON ut.timeoff_id = t.timeoff_id " +
                     "WHERE ut.status = 1 " +
                     "AND ut.start_date <= ? " +
                     "AND ut.end_date >= ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(reportEndDate));
            ps.setDate(2, java.sql.Date.valueOf(reportStartDate));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                LocalDate leaveStart = rs.getDate("start_date").toLocalDate();
                LocalDate leaveEnd = rs.getDate("end_date").toLocalDate();
                String leaveType = rs.getString("timeoff_type");
                
                Map<LocalDate, String> leaveMap = userLeaveMaps.computeIfAbsent(userId, k -> new HashMap<>());
                
                for (LocalDate date = leaveStart; !date.isAfter(leaveEnd); date = date.plusDays(1)) {
                    if (!date.isBefore(reportStartDate) && !date.isAfter(reportEndDate)) {
                        leaveMap.put(date, leaveType);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error pre-fetching user leaves: " + e.getMessage());
        }
        return userLeaveMaps;
    }

    /**
     * NEW HELPER (Copied from Reports):
     * Parses a time string (HH:MM:SS) into a LocalTime object.
     */
    private LocalTime parseTime(String time) {
        if (time == null || time.trim().isEmpty() || time.equals("00:00:00") || time.equals("--")) {
            return null;
        }
        try {
            return LocalTime.parse(time);
        } catch (Exception e) {
            return null;
        }
    }
}