/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.raver.iemsmaven.Controller;


import com.dlsc.gemsfx.daterange.DateRange;
import com.dlsc.gemsfx.daterange.DateRangePicker;
import com.raver.iemsmaven.Model.Attendance;
import com.raver.iemsmaven.Model.Department;
import com.raver.iemsmaven.Model.Special_Calendar;
import com.raver.iemsmaven.Model.Timeoff;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Utilities.DatabaseUtil;
import com.raver.iemsmaven.Utilities.Modal;
import com.raver.iemsmaven.Utilities.PaneUtil;
import com.raver.iemsmaven.Utilities.DateUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.ListView;
import javafx.util.StringConverter;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import javafx.stage.FileChooser;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javafx.util.Callback;

import static com.raver.iemsmaven.Model.Attendance.*;
import static com.raver.iemsmaven.Model.Special_Calendar.getCalendarByUserId;
import javafx.beans.binding.Bindings;
import javafx.scene.transform.Scale;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;

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
import org.apache.poi.ss.util.CellRangeAddress;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Model.Assignment;
import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Utilities.Config;
import java.nio.charset.StandardCharsets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.stage.DirectoryChooser;
/**
 *
 * @author User
 */
public class ADMIN_AttReportsCTRL implements Initializable {
    
    // New FXML Fields
    @FXML
    private ComboBox<Department> departmentCombo;
    @FXML
    private ComboBox<User> employeeCombo;
    @FXML
    private Button showReportBtn;
    @FXML private Button printBtn;
    @FXML private Button exportTxtBtn;
    @FXML private Button exportXlsxBtn;
    @FXML private Button exportPdfBtn;
    @FXML private PieChart distributionChart;
    @FXML private BarChart<String, Number> topLatesChart;
    @FXML
    private TableView<Object> reportTable; 
    private boolean isDetailedView = false;
    @FXML private Label reliabilityScoreLabel;
    @FXML private Label reliabilityMessageLabel;
    
    @FXML private javafx.scene.chart.LineChart<String, Number> tardinessTrendChart;
    @FXML private Label trendContextLabel;
    @FXML private ListView<String> habitualLatesListView;
    private Map<String, Integer> summaryDailyTardinessMap = new TreeMap<>();
    
    // Existing FXML Fields
    @FXML
    private Button resetBtn, allDTRBtn;
    @FXML
    private DateRangePicker dateRangePicker;
   
    private static final LocalTime SHIFT_AM_START = LocalTime.of(8, 0);
    private static final LocalTime SHIFT_AM_END = LocalTime.of(12, 0);
    private static final LocalTime SHIFT_PM_START = LocalTime.of(13, 0); // 1:00 PM
    private static final LocalTime SHIFT_PM_END = LocalTime.of(17, 0);   // 5:00 PM
    private static final long LUNCH_BREAK_MINUTES = 60;
    private static final long FULL_DAY_MINUTES = (4 * 60) + (4 * 60);
    private String currentSummaryText = "Select filters and click 'Show Report' to generate.";
    
    DatabaseUtil dbMethods = new DatabaseUtil();
    PaneUtil method = new PaneUtil();
    LocalDate currentDate = LocalDate.now();
    

    // Map for DTR generation
    private Map<Integer, String> employeeIdToName = new HashMap<>();
    // Dummy "All" objects for ComboBoxes
    private final Department ALL_DEPARTMENTS = new Department(0, "All Departments");
    private final User ALL_EMPLOYEES = new User(0, "All Employees", null, null, null, null);

    @Override
    public void initialize(URL location, ResourceBundle rb) {
        try {
            
            initializeDateRangePicker();
                      
            setupFilterControls();

            showReportBtn.setOnAction(e -> handleShowReport());
            resetBtn.setOnAction(e -> handleResetFilters(e));
            printBtn.setOnAction(e -> handlePrint()); 
            exportTxtBtn.setOnAction(e -> handleExportTxt()); 
            exportXlsxBtn.setOnAction(e -> handleExportXlsx());
            exportPdfBtn.setOnAction(e -> handleExportPdf());
            reportTable.itemsProperty().addListener((obs, oldVal, newVal) -> updateActionButtonsState());
            updateActionButtonsState();
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   @FXML
    private void handleExportTxt() {
        if (reportTable.getItems() == null || reportTable.getItems().isEmpty()) {
            Modal.alert("Nothing to Export", "There is no report data currently displayed to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report to Text File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        String initialFileName = "attendance_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
       
        Department dept = departmentCombo.getValue();
        User emp = employeeCombo.getValue();
        if (emp != null && emp.getId() != 0) {
             initialFileName = emp.getFullName().replace(" ", "_").replace(",", "") + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        } else if (dept != null && dept.getId() != 0) {
             initialFileName = dept.getDepartmentName().replace(" ", "_") + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        }
        fileChooser.setInitialFileName(initialFileName);

        Stage stage = (Stage) reportTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                // --- Write Header ---
                writer.write(getFormattedReportHeader() + "\n\n"); // Use new helper

                // --- Write Table Data ---
                writer.write("DETAILS\n");
                writer.write("=================\n");

                String headerFormat;
                String dataFormat;
                String separatorLine;

                if (isDetailedView) {
                    // Date(10) Day(5) AM In(10) AM Out(10) PM In(10) PM Out(10) OT In(10) OT Out(10) Hrs(8) Lates(12) Undertime(10) OT(8) Status(var)
                    headerFormat = "%-10s %-5s %-10s %-10s %-10s %-10s %-10s %-10s %8s %-12s %10s %8s %s\n";
                    dataFormat   = "%-10s %-5s %-10s %-10s %-10s %-10s %-10s %-10s %8.2f %-12s %10.2f %8.2f %s\n";
                    separatorLine= "---------- ----- ---------- ---------- ---------- ---------- ---------- ---------- -------- ------------ ---------- -------- ------\n";
                } else {
                    // EmpID(8) Name(25) Present(10) Abs(10) Late(10) LateM(12) UnderM(14) OT(10) Score(8)
                    headerFormat = "%-8s %-25s %10s %10s %10s %12s %14s %10s %8s\n";
                    dataFormat   = "%-8s %-25s %10d %10d %10d %12d %14d %10.2f %8d\n";
                    separatorLine= "-------- ------------------------- ---------- ---------- ---------- ------------ -------------- ---------- --------\n";
                }

                List<String> headers = reportTable.getColumns().stream()
                                             .map(TableColumn::getText)
                                             .collect(Collectors.toList());
                writer.write(String.format(headerFormat, headers.toArray()));
                writer.write(separatorLine);

                for (Object item : reportTable.getItems()) {
                    if (item instanceof EmployeeSummary && !isDetailedView) {
                        EmployeeSummary es = (EmployeeSummary) item;
                        writer.write(String.format(dataFormat,
                            es.getEmpId(), truncate(es.getEmployeeName(), 25), es.getDaysPresent(),
                            es.getAbsences(), es.getLatesCount(), es.getLatesMins(),
                            es.getUndertimeMins(), es.getTotalOtHrs(),
                            es.getBradfordScore()
                        ));
                    } else if (item instanceof DailyLog && isDetailedView) {
                        DailyLog dl = (DailyLog) item;
                        writer.write(String.format(dataFormat,
                            dl.getDate(), dl.getDay(), dl.getAmIn(), dl.getAmOut(),
                            dl.getPmIn(), dl.getPmOut(), dl.getOtIn(), dl.getOtOut(),
                            dl.getHrsWorked(), dl.getLateTimeFormatted(), dl.getUndertimeHours(),
                            dl.getOtHrs(), dl.getStatus()
                        ));
                    }
                }
                
                // --- Write Summary ---
                writer.write("\n\nREPORT SUMMARY\n");
                writer.write("=================\n");
                writer.write(currentSummaryText.replace("**", ""));
                writer.write(getAnalyticsSummaryString());
                
                try {
                    User currentAdmin = Session.getInstance().getLoggedInUser();
                    String target = getReportTargetDescription(); // Use helper
                    String description = "Generated TXT attendance report for " + target;
                    ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
                } catch (Exception logEx) {
                    System.err.println("Failed to log TXT Export: " + logEx.getMessage());
                }
                Modal.inform("Export Successful", "Report exported successfully to text file.", file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                Modal.alert("Export Failed", "Could not write to text file: " + e.getMessage());
            }
        }
    }
    private static String bytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    // Helper method to truncate strings for fixed-width columns
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength);
    }

    @FXML
    private void handleExportXlsx() {
        if (reportTable.getItems() == null || reportTable.getItems().isEmpty()) {
            Modal.alert("Nothing to Export", "There is no report data currently displayed to export.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report to Excel File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"));
        String initialFileName = "attendance_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        Department dept = departmentCombo.getValue();
        User emp = employeeCombo.getValue();
        if (emp != null && emp.getId() != 0) {
             initialFileName = emp.getFullName().replace(" ", "_").replace(",", "") + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        } else if (dept != null && dept.getId() != 0) {
             initialFileName = dept.getDepartmentName().replace(" ", "_") + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        }
        fileChooser.setInitialFileName(initialFileName);
        
        Stage stage = (Stage) reportTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Attendance Report");
                int rowIndex = 0;

                org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
                titleFont.setBold(true); titleFont.setFontHeightInPoints((short) 16);
                CellStyle titleStyle = workbook.createCellStyle();
                titleStyle.setFont(titleFont);
                titleStyle.setAlignment(HorizontalAlignment.CENTER);

                org.apache.poi.ss.usermodel.Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                CellStyle headerStyle = createHeaderStyle(workbook); // Your existing helper
                CellStyle centerStyle = createCenterStyle(workbook);
                CellStyle defaultStyle = createNormalStyle(workbook);
                CellStyle summaryBoldStyle = workbook.createCellStyle();
                summaryBoldStyle.setFont(boldFont);

                // --- Write Header Info ---
                String[] headerLines = getFormattedReportHeader().split("\n");
                int numColumns = reportTable.getColumns().size();
                
                for (String line : headerLines) {
                    Row row = sheet.createRow(rowIndex++);
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                    cell.setCellValue(line);
                    if (line.equals("LGU LAGONOY") || line.contains("Report")) { // Bold LGU and Title lines
                        cell.setCellStyle(titleStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, numColumns - 1));
                }
                rowIndex++; // Add a blank row

                // --- Write Table Headers ---
                Row tableHeaderRow = sheet.createRow(rowIndex++);
                int colIndex = 0;
                for (TableColumn<Object, ?> col : reportTable.getColumns()) {
                    org.apache.poi.ss.usermodel.Cell cell = tableHeaderRow.createCell(colIndex++);
                    cell.setCellValue(col.getText());
                    cell.setCellStyle(headerStyle);
                }

                // --- Write Table Data ---
                for (Object item : reportTable.getItems()) {
                    Row row = sheet.createRow(rowIndex++);
                    colIndex = 0;
                    if (item instanceof EmployeeSummary) {
                        EmployeeSummary es = (EmployeeSummary) item;
                        createCell(row, colIndex++, es.getEmpId(), centerStyle); // Center Emp ID
                        createCell(row, colIndex++, es.getEmployeeName(), defaultStyle);
                        createNumericCell(row, colIndex++, es.getDaysPresent(), centerStyle);
                        createNumericCell(row, colIndex++, es.getAbsences(), centerStyle);
                        createNumericCell(row, colIndex++, es.getLatesCount(), centerStyle);
                        createNumericCell(row, colIndex++, es.getLatesMins(), centerStyle);
                        createNumericCell(row, colIndex++, es.getUndertimeMins(), centerStyle);
                        createNumericCell(row, colIndex++, es.getTotalOtHrs(), centerStyle);
                        createNumericCell(row, colIndex++, es.getBradfordScore(), centerStyle);
                    } else if (item instanceof DailyLog) {
                        DailyLog dl = (DailyLog) item;
                        createCell(row, colIndex++, dl.getDate(), centerStyle); // Center Date
                         createCell(row, colIndex++, dl.getDay(), centerStyle);
                        createCell(row, colIndex++, dl.getAmIn(), centerStyle);
                         createCell(row, colIndex++, dl.getAmOut(), centerStyle);
                         createCell(row, colIndex++, dl.getPmIn(), centerStyle);
                         createCell(row, colIndex++, dl.getPmOut(), centerStyle);
                         createCell(row, colIndex++, dl.getOtIn(), centerStyle);
                        createCell(row, colIndex++, dl.getOtOut(), centerStyle);
                         createNumericCell(row, colIndex++, dl.getHrsWorked(), centerStyle);
                         createCell(row, colIndex++, dl.getLateTimeFormatted(), centerStyle);
                         createNumericCell(row, colIndex++, dl.getUndertimeHours(), centerStyle);
                         createNumericCell(row, colIndex++, dl.getOtHrs(), centerStyle);
                        createCell(row, colIndex++, dl.getStatus(), defaultStyle);
                    }
                }
                
                rowIndex++; // Add blank row before summary

                // --- Write Summary ---
                String[] summaryLines = currentSummaryText.split("\n");
                for (String line : summaryLines) {
                    Row row = sheet.createRow(rowIndex++);
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                    if (line.contains("**")) {
                        cell.setCellValue(line.replace("**", "").trim());
                        cell.setCellStyle(summaryBoldStyle);
                    } else {
                        cell.setCellValue(line.trim());
                    }
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, numColumns - 1));
                }
                rowIndex++; // Spacer
                Row analyticsTitleRow = sheet.createRow(rowIndex++);
                org.apache.poi.ss.usermodel.Cell analyticsTitle = analyticsTitleRow.createCell(0);
                analyticsTitle.setCellValue("ANALYTICS OVERVIEW");
                analyticsTitle.setCellStyle(summaryBoldStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, numColumns - 1));

                // Parse the analytics string to write row by row
                String[] analyticsLines = getAnalyticsSummaryString().replace("\n\nANALYTICS OVERVIEW\n===================\n", "").split("\n");

                for (String line : analyticsLines) {
                    if(line.trim().isEmpty()) continue;

                    Row row = sheet.createRow(rowIndex++);
                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
                    cell.setCellValue(line.trim());

                    // Make headers bold
                    if(line.contains("DISTRIBUTION:") || line.contains("TOP EMPLOYEES")) {
                        cell.setCellStyle(summaryBoldStyle);
                    }
                    sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, numColumns - 1));
                }

                // --- Auto-size Columns (FIXED) ---
                for (int i = 0; i < numColumns; i++) {
                    if (i == 0) {
                        // Manually set column 0 width (for Emp. ID)
                        sheet.setColumnWidth(i, 256 * 15); // Approx 15 characters
                    } else {
                        // Autosize all other columns
                        sheet.autoSizeColumn(i);
                        // Add minimum width for Name/Status if autosize is too small
                        if ( (isDetailedView && (i == 12)) || (!isDetailedView && (i == 1)) ) { // Name or Status col
                             if (sheet.getColumnWidth(i) < 256 * 25) {
                                 sheet.setColumnWidth(i, 256 * 25);
                             }
                        }
                    }
                }
                try {
                    User currentAdmin = Session.getInstance().getLoggedInUser();
                    String target = getReportTargetDescription(); // Use helper
                    String description = "Generated Excel (XLSX) attendance report for " + target;
                    ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
                } catch (Exception logEx) {
                    System.err.println("Failed to log XLSX Export: " + logEx.getMessage());
                }
                
                workbook.write(fileOut);
                Modal.inform("Export Successful", "Report exported successfully to Excel file.", file.getAbsolutePath());

            } catch (IOException e) {
                e.printStackTrace();
                Modal.alert("Export Failed", "Could not write to Excel file: " + e.getMessage());
            } catch (Exception e) { // Catch other potential errors
                 e.printStackTrace();
                Modal.alert("Export Error", "An unexpected error occurred: " + e.getMessage());
            }
        }
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
    @FXML
    private void handleExportPdf() {
        if (reportTable.getItems() == null || reportTable.getItems().isEmpty()) {
            Modal.alert("Nothing to Export", "There is no report data currently displayed.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Report to PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));
        // ... (filename logic) ...
        String initialFileName = "attendance_report_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
        Department dept = departmentCombo.getValue();
        User emp = employeeCombo.getValue();
        if (emp != null && emp.getId() != 0) {
             initialFileName = emp.getFullName().replace(" ", "_").replace(",", "") + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
        } else if (dept != null && dept.getId() != 0) {
             initialFileName = dept.getDepartmentName().replace(" ", "_") + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";
        }
        fileChooser.setInitialFileName(initialFileName);
        
        File file = fileChooser.showSaveDialog((Stage) reportTable.getScene().getWindow());
        if (file == null) return;

        // --- Define Fonts ---
        com.itextpdf.text.Font lguFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.DARK_GRAY);
        com.itextpdf.text.Font summaryBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);
        com.itextpdf.text.Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7, BaseColor.WHITE);
        com.itextpdf.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 6, BaseColor.BLACK);

        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            PdfWriter.getInstance(document, fos);
            document.open();

            // --- 1. Add Header ---
            String[] headerLines = getFormattedReportHeader().split("\n");
            com.itextpdf.text.Font currentFont = lguFont;
            for (String line : headerLines) {
                if(line.startsWith("Employee ID:") || line.startsWith("Department:") || line.startsWith("Report Period:") || line.startsWith("Generated On:")) {
                    currentFont = infoFont;
                } else if(line.contains("Report")) {
                    currentFont = titleFont;
                }
                Paragraph p = new Paragraph(line, currentFont);
                p.setAlignment(Element.ALIGN_CENTER);
                document.add(p);
            }
            document.add(new Paragraph("\n")); // Add space

            // --- 2. Create Table ---
            int numColumns = reportTable.getColumns().size();
            PdfPTable table = new PdfPTable(numColumns);
            table.setWidthPercentage(100);

            // --- Set Column Widths based on view ---
            if (isDetailedView) {
                // Date, Day, AM In/Out, PM In/Out, OT In/Out, Hrs, Late, Under, OT, Status
                float[] widths = {1.2f, 0.6f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1.2f, 1f, 1f, 2.5f};
                table.setWidths(widths);
            } else {
                // Emp ID, Name, Present, Abs, Late(C), Late(M), Under(M), OT(Hrs)
                float[] widths = {0.8f, 2.5f, 1f, 1f, 1f, 1.2f, 1.4f, 1f, 1f};
                table.setWidths(widths);
            }

            // --- 3. Write Table Headers ---
            for (TableColumn<Object, ?> col : reportTable.getColumns()) {
                PdfPCell headerCell = new PdfPCell(new Phrase(col.getText(), headerFont));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerCell.setPadding(4);
                table.addCell(headerCell);
            }
            table.setHeaderRows(1);

            // --- 4. Write Table Data ---
            DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
            for (Object item : reportTable.getItems()) {
                 if (item instanceof EmployeeSummary && !isDetailedView) {
                    EmployeeSummary es = (EmployeeSummary) item;
                    table.addCell(createPdfCell(es.getEmpId(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(es.getEmployeeName(), bodyFont, Element.ALIGN_LEFT));
                    table.addCell(createPdfCell(String.valueOf(es.getDaysPresent()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.valueOf(es.getAbsences()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.valueOf(es.getLatesCount()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.valueOf(es.getLatesMins()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.valueOf(es.getUndertimeMins()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.format("%.2f", es.getTotalOtHrs()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.valueOf(es.getBradfordScore()), bodyFont, Element.ALIGN_CENTER));
                } else if (item instanceof DailyLog && isDetailedView) {
                    DailyLog dl = (DailyLog) item;
                    table.addCell(createPdfCell(dl.getDate(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getDay(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getAmIn(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getAmOut(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getPmIn(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getPmOut(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getOtIn(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getOtOut(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.format("%.2f", dl.getHrsWorked()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getLateTimeFormatted(), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.format("%.2f", dl.getUndertimeHours()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(String.format("%.2f", dl.getOtHrs()), bodyFont, Element.ALIGN_CENTER));
                    table.addCell(createPdfCell(dl.getStatus(), bodyFont, Element.ALIGN_LEFT));
                }
            }
            document.add(table);
            document.add(new Paragraph("\n")); // Space before summary

            // --- 5. Add Summary ---
            String[] summaryLines = currentSummaryText.split("\n");
            for (String line : summaryLines) {
                com.itextpdf.text.Font font = line.contains("SUMMARY:") ? summaryBoldFont : summaryFont;
                Paragraph p = new Paragraph(line.replace("**", "").trim(), font);
                p.setAlignment(Element.ALIGN_LEFT); // Align summary text left
                document.add(p);
            }
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String target = getReportTargetDescription(); // Use helper
                String description = "Generated PDF attendance report for " + target;
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
            } catch (Exception logEx) {
                System.err.println("Failed to log PDF Export: " + logEx.getMessage());
            }
            document.add(new Paragraph("\n")); // Space
            String[] analyticsLines = getAnalyticsSummaryString().split("\n");
            for (String line : analyticsLines) {
                // Apply Bold font to headers like "ANALYTICS OVERVIEW", "ATTENDANCE DISTRIBUTION"
                com.itextpdf.text.Font font = (line.contains("OVERVIEW") || line.contains("DISTRIBUTION") || line.contains("TOP EMPLOYEES")) 
                    ? summaryBoldFont : summaryFont;

                Paragraph p = new Paragraph(line.trim(), font);
                p.setAlignment(Element.ALIGN_LEFT);
                document.add(p);
            }
            
            document.close();
            Modal.inform("Export Successful", "Report exported successfully to PDF file.", file.getAbsolutePath());

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Modal.alert("Export Failed", "Could not write to PDF file: " + e.getMessage());
        }
    }
    private PdfPCell createPdfCell(String text, com.itextpdf.text.Font font, int horizontalAlignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    // --- Helper methods for Excel Export (Updated with fully qualified POI Cell) ---
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
              } else if (value instanceof Long) { // Added Long case
                   cell.setCellValue(value.longValue());
              }
         } else {
              cell.setCellValue("");
         }
         if (style != null) {
             cell.setCellStyle(style);
         }
     }
    private void updateActionButtonsState() {
        boolean hasData = reportTable.getItems() != null && !reportTable.getItems().isEmpty();
        printBtn.setDisable(!hasData);
        exportTxtBtn.setDisable(!hasData);
        exportXlsxBtn.setDisable(!hasData);
        exportPdfBtn.setDisable(!hasData);
        allDTRBtn.setDisable(!hasData);
    }
    

    /**
     * Initializes the DateRangePicker with the current month.
     */
    private void initializeDateRangePicker() {
        // Set default date range to current month
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now();
        DateRange defaultRange = new DateRange(startDate, endDate);
        dateRangePicker.setValue(defaultRange);
    }
    
    /**
     * Sets up the Department and Employee ComboBoxes.
     */
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

        // Add listener to repopulate employees when department changes
        departmentCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateEmployeeCombo();
            updateActionButtonsState();
        });
        employeeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
             updateActionButtonsState(); 
        });
        
        // Initial load of employee combo
        updateEmployeeCombo();
    }
    
    private void updateEmployeeCombo() {
        employeeIdToName.clear(); // Clear map for DTR generation
        Department selectedDept = departmentCombo.getValue();
        ObservableList<User> employees = FXCollections.observableArrayList();
        employees.add(ALL_EMPLOYEES);

        ObservableList<User> allUsers = User.getActiveEmployeesWithDepartmentAndPosition();
        for (User user : allUsers) {
            
            String formattedName = (user.getLname() != null ? user.getLname() : "").trim();
            formattedName += ", " + (user.getFname() != null ? user.getFname() : "").trim();
            if (user.getMname() != null && !user.getMname().trim().isEmpty()) {
                formattedName += " " + user.getMname().trim().substring(0, 1) + ".";
            }
            if (user.getSuffix() != null && !user.getSuffix().trim().isEmpty()) {
                formattedName += " " + user.getSuffix().trim();
            }
            // Use the formatted name for the map and the ComboBox display
            employeeIdToName.put(user.getId(), formattedName);
            user.setFullName(formattedName); // Set this so the ComboBox displays it

            // Check if user matches selected department
            if (selectedDept == null || selectedDept.getId() == 0) { // <-- CORRECTED
                employees.add(user);
            } else if (user.getDepartment() != null && user.getDepartment().equals(selectedDept.getDepartmentName())) {
                employees.add(user);
            }
        }
        
        employeeCombo.setItems(employees);
        employeeCombo.setValue(ALL_EMPLOYEES);
    }
    @FXML
    private void handlePrint() {
        if (reportTable.getItems() == null || reportTable.getItems().isEmpty()) {
            Modal.alert("Nothing to Print", "There is no report data currently displayed to print.");
            return;
        }

        Stage owner = (Stage) reportTable.getScene().getWindow();
        showPrintPreview(owner);
    }
    private void showPrintPreview(Stage owner) {
        // Build the printable node (summary + table)
        VBox printableNode = buildPrintableNode();
        printableNode.setStyle("-fx-background-color: white;"); // Ensure white background for printing

        ScrollPane scrollPane = new ScrollPane(printableNode);
        scrollPane.setFitToWidth(true);

        // Zoom controls
        Slider zoomSlider = new Slider(0.5, 1.5, 1.0);
        zoomSlider.setPrefWidth(200);
        Label zoomLabel = new Label("Zoom:");
        HBox zoomBox = new HBox(5, zoomLabel, zoomSlider);
        zoomBox.setAlignment(Pos.CENTER_LEFT);

        // Action buttons
        Button printActionButton = new Button("Print");
        Button closeButton = new Button("Close");
        HBox buttonBox = new HBox(10, printActionButton, closeButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        HBox bottomBar = new HBox(20, zoomBox, buttonBox);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setAlignment(Pos.CENTER_LEFT); // Align zoom left, buttons right (via HBox inner alignment)

        BorderPane rootLayout = new BorderPane();
        rootLayout.setCenter(scrollPane);
        rootLayout.setBottom(bottomBar);

        Scene previewScene = new Scene(rootLayout, 1000, 700); // Adjust size as needed
        Stage previewStage = new Stage();
        previewStage.initOwner(owner);
        previewStage.initModality(Modality.APPLICATION_MODAL);
        previewStage.setTitle("Print Preview");
        previewStage.setScene(previewScene);

        // Apply scale transform for zooming
        Scale scaleTransform = new Scale(1, 1);
        printableNode.getTransforms().add(scaleTransform);
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            scaleTransform.setX(newVal.doubleValue());
            scaleTransform.setY(newVal.doubleValue());
        });

        // Print action
        printActionButton.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job == null) {
                Modal.alert("Print Error", "No printers available.");
                return;
            }

            boolean proceed = job.showPrintDialog(previewStage);
            if (!proceed) {
                return;
            }

            // --- Fit content to page ---
            PageLayout pageLayout = job.getJobSettings().getPageLayout();
            double printableWidth = pageLayout.getPrintableWidth();
            double printableHeight = pageLayout.getPrintableHeight();

            // Ensure layout calculations are done
            printableNode.applyCss();
            printableNode.layout();

            double nodeWidth = printableNode.getBoundsInParent().getWidth();
            double nodeHeight = printableNode.getBoundsInParent().getHeight();
            double currentZoom = zoomSlider.getValue(); // Use current zoom as base

            double scaleX = (nodeWidth > 0) ? printableWidth / (nodeWidth / currentZoom) : 1.0;
            double scaleY = (nodeHeight > 0) ? printableHeight / (nodeHeight / currentZoom) : 1.0;
            double printScaleFactor = Math.min(1.0, Math.min(scaleX, scaleY)); // Don't scale up, only down if needed

            // Apply temporary scale for printing
            Scale printScale = new Scale(currentZoom * printScaleFactor, currentZoom * printScaleFactor);
            printableNode.getTransforms().add(printScale);
            // --- End fit content ---

            boolean success = job.printPage(printableNode);

            // --- Remove temporary scale ---
             printableNode.getTransforms().remove(printScale);
             // Reapply original zoom scale (important if preview stays open)
             scaleTransform.setX(currentZoom);
             scaleTransform.setY(currentZoom);
            // --- End remove temporary scale ---

            if (success) {
                job.endJob();
                Modal.inform("Print Started", "Report sent to the printer.", "");
                previewStage.close(); // Close preview after printing
            } else {
               Modal.alert("Print Failed", "Could not complete the print job.");
            }
        });

        // Close action
        closeButton.setOnAction(e -> previewStage.close());

        previewStage.showAndWait();
    }

    private VBox buildPrintableNode() {
        Text headerText = new Text(getFormattedReportHeader());
        headerText.setFont(Font.font("System", 12));
        headerText.setTextAlignment(TextAlignment.CENTER);
        headerText.setWrappingWidth(550); 
        VBox.setMargin(headerText, new Insets(0, 0, 15, 0));

        Text summaryText = new Text(currentSummaryText.replace("**", ""));
        summaryText.setFont(Font.font("System", 11));
        summaryText.setWrappingWidth(550);
        VBox.setMargin(summaryText, new Insets(0, 0, 15, 0));

        // --- NEW: Analytics Text Node ---
        Text analyticsText = new Text(getAnalyticsSummaryString());
        analyticsText.setFont(Font.font("System", 11));
        analyticsText.setWrappingWidth(550);
        VBox.setMargin(analyticsText, new Insets(15, 0, 15, 0));

        TableView<Object> printableTable = createPrintableTable();

        // Add analyticsText to the VBox children
        VBox printableContent = new VBox(10, headerText, printableTable, summaryText, analyticsText); 
        printableContent.setPadding(new Insets(20));
        printableContent.setAlignment(Pos.TOP_CENTER);
        printableContent.setPrefWidth(550);

        return printableContent;
    }
    private TableView<Object> createPrintableTable() {
        TableView<Object> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        String commonCellStyle = "-fx-alignment: CENTER; -fx-font-size: 9pt;"; // Common style for print

        if (isDetailedView) {
            // --- Recreate Detailed View Columns ---
            TableColumn<Object, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            dateCol.setPrefWidth(100); dateCol.setStyle("-fx-font-size: 9pt;"); // Base style

            TableColumn<Object, String> dayCol = new TableColumn<>("Day");
            dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
            dayCol.setPrefWidth(60); dayCol.setStyle("-fx-font-size: 9pt;");

            TableColumn<Object, String> amInCol = new TableColumn<>("AM In");
            amInCol.setCellValueFactory(new PropertyValueFactory<>("amIn"));
            amInCol.setPrefWidth(90); amInCol.setStyle(commonCellStyle);

            TableColumn<Object, String> amOutCol = new TableColumn<>("AM Out");
            amOutCol.setCellValueFactory(new PropertyValueFactory<>("amOut"));
            amOutCol.setPrefWidth(90); amOutCol.setStyle(commonCellStyle);

            TableColumn<Object, String> pmInCol = new TableColumn<>("PM In");
            pmInCol.setCellValueFactory(new PropertyValueFactory<>("pmIn"));
            pmInCol.setPrefWidth(90); pmInCol.setStyle(commonCellStyle);

            TableColumn<Object, String> pmOutCol = new TableColumn<>("PM Out");
            pmOutCol.setCellValueFactory(new PropertyValueFactory<>("pmOut"));
            pmOutCol.setPrefWidth(90); pmOutCol.setStyle(commonCellStyle);

            TableColumn<Object, String> otInCol = new TableColumn<>("OT In");
            otInCol.setCellValueFactory(new PropertyValueFactory<>("otIn"));
            otInCol.setPrefWidth(90); otInCol.setStyle(commonCellStyle);

            TableColumn<Object, String> otOutCol = new TableColumn<>("OT Out");
            otOutCol.setCellValueFactory(new PropertyValueFactory<>("otOut"));
            otOutCol.setPrefWidth(90); otOutCol.setStyle(commonCellStyle);

            TableColumn<Object, Double> hrsCol = new TableColumn<>("Hrs Worked");
            hrsCol.setCellValueFactory(new PropertyValueFactory<>("hrsWorked"));
            hrsCol.setPrefWidth(100); hrsCol.setStyle(commonCellStyle);

            TableColumn<Object, String> lateTimeCol = new TableColumn<>("Lates (Time)");
            lateTimeCol.setCellValueFactory(new PropertyValueFactory<>("lateTimeFormatted"));
            lateTimeCol.setPrefWidth(120); lateTimeCol.setStyle(commonCellStyle);

            TableColumn<Object, Double> undertimeHrsCol = new TableColumn<>("Undertime (Hrs)");
            undertimeHrsCol.setCellValueFactory(new PropertyValueFactory<>("undertimeHours"));
            undertimeHrsCol.setPrefWidth(130); undertimeHrsCol.setStyle(commonCellStyle);

            TableColumn<Object, Double> otCol = new TableColumn<>("OT (Hrs)");
            otCol.setCellValueFactory(new PropertyValueFactory<>("otHrs"));
            otCol.setPrefWidth(100); otCol.setStyle(commonCellStyle);

            TableColumn<Object, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
            statusCol.setPrefWidth(150); statusCol.setStyle("-fx-font-size: 9pt;"); // Base style

            table.getColumns().addAll(dateCol, dayCol, amInCol, amOutCol, pmInCol, pmOutCol, otInCol, otOutCol, hrsCol, lateTimeCol, undertimeHrsCol, otCol, statusCol);

        } else {
            // --- Recreate Summary View Columns ---
            TableColumn<Object, String> empIdCol = new TableColumn<>("Emp. ID");
            empIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));
            empIdCol.setPrefWidth(80); empIdCol.setStyle("-fx-font-size: 9pt;");

            TableColumn<Object, String> nameCol = new TableColumn<>("Employee Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
            nameCol.setPrefWidth(250); nameCol.setStyle("-fx-font-size: 9pt;");

            TableColumn<Object, Integer> presentCol = new TableColumn<>("Days Present");
            presentCol.setCellValueFactory(new PropertyValueFactory<>("daysPresent"));
            presentCol.setStyle(commonCellStyle);

            TableColumn<Object, Integer> absencesCol = new TableColumn<>("Absences");
            absencesCol.setCellValueFactory(new PropertyValueFactory<>("absences"));
            absencesCol.setStyle(commonCellStyle);

            TableColumn<Object, Integer> latesCountCol = new TableColumn<>("Lates (Count)");
            latesCountCol.setCellValueFactory(new PropertyValueFactory<>("latesCount"));
            latesCountCol.setStyle(commonCellStyle);

            TableColumn<Object, Integer> latesMinsCol = new TableColumn<>("Lates (Mins)");
            latesMinsCol.setCellValueFactory(new PropertyValueFactory<>("latesMins"));
            latesMinsCol.setPrefWidth(120); latesMinsCol.setStyle(commonCellStyle);

            TableColumn<Object, Integer> undertimeMinsCol = new TableColumn<>("Undertime (Mins)");
            undertimeMinsCol.setCellValueFactory(new PropertyValueFactory<>("undertimeMins"));
            undertimeMinsCol.setPrefWidth(140); undertimeMinsCol.setStyle(commonCellStyle);

            TableColumn<Object, Double> otHrsCol = new TableColumn<>("Total OT (Hrs)");
            otHrsCol.setCellValueFactory(new PropertyValueFactory<>("totalOtHrs"));
            otHrsCol.setStyle(commonCellStyle);

            table.getColumns().addAll(empIdCol, nameCol, presentCol, absencesCol, latesCountCol, latesMinsCol, undertimeMinsCol, otHrsCol);
        }

        // Copy data items
        if (reportTable.getItems() != null) {
            table.setItems(FXCollections.observableArrayList(reportTable.getItems()));
        } else {
            table.setItems(FXCollections.observableArrayList()); // Ensure it has an empty list
        }


        // Try to force the table to calculate its layout height
        table.setFixedCellSize(30); // Adjust based on font size
        // Use fully qualified name for Bindings if import wasn't added
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(javafx.beans.binding.Bindings.size(table.getItems()).add(1.05))); // Header row + buffer
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());


        return table;
    }
    /**
     * Main handler for the "Show Report" button.
     * Determines which report to show (Summary or Detailed).
     */
   @FXML
    private void handleShowReport() {
        User selectedEmployee = employeeCombo.getValue();
        
        reportTable.setPlaceholder(new Label("Generating report..."));

        if (selectedEmployee == null || selectedEmployee.getId() == 0) {
            isDetailedView = false; 
            setupSummaryReportTable();
            loadSummaryReportData();
        } else {
            isDetailedView = true; 
            setupDetailedReportTable();
            loadDetailedReportData(selectedEmployee);
        }
        updateActionButtonsState();
        updateVisualAnalytics();
    }
   @FXML
    private void handleResetFilters(ActionEvent event) {
        departmentCombo.setValue(ALL_DEPARTMENTS);
        initializeDateRangePicker();

        reportTable.getColumns().clear();
        reportTable.setItems(null);
        
        // CHANGED: Update variable, not label
        currentSummaryText = "Select filters and click 'Show Report' to generate."; 
        
        updateActionButtonsState();
        
        // Add this to clear charts on reset
        distributionChart.getData().clear();
        topLatesChart.getData().clear();
    }
private void setupSummaryReportTable() {
        reportTable.getColumns().clear();
        reportTable.setPlaceholder(new Label("No columns in table"));

        // IMPORTANT: This ensures columns fill the space nicely without horizontal scroll
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Object, String> empIdCol = new TableColumn<>("ID");
        empIdCol.setCellValueFactory(new PropertyValueFactory<>("empId"));
        empIdCol.setPrefWidth(60); // Reduced
        empIdCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        nameCol.setPrefWidth(100); // Fixed to 100 as requested
        nameCol.setStyle("-fx-alignment: CENTER-LEFT;");

        TableColumn<Object, Integer> presentCol = new TableColumn<>("Present");
        presentCol.setCellValueFactory(new PropertyValueFactory<>("daysPresent"));
        presentCol.setPrefWidth(70);
        presentCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Integer> absencesCol = new TableColumn<>("Absent");
        absencesCol.setCellValueFactory(new PropertyValueFactory<>("absences"));
        absencesCol.setPrefWidth(60);
        absencesCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Integer> latesCountCol = new TableColumn<>("Lates");
        latesCountCol.setCellValueFactory(new PropertyValueFactory<>("latesCount"));
        latesCountCol.setPrefWidth(60);
        latesCountCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Integer> latesMinsCol = new TableColumn<>("Late(m)");
        latesMinsCol.setCellValueFactory(new PropertyValueFactory<>("latesMins"));
        latesMinsCol.setPrefWidth(70);
        latesMinsCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Integer> undertimeMinsCol = new TableColumn<>("UT(m)");
        undertimeMinsCol.setCellValueFactory(new PropertyValueFactory<>("undertimeMins"));
        undertimeMinsCol.setPrefWidth(70);
        undertimeMinsCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Double> otHrsCol = new TableColumn<>("OT(hr)");
        otHrsCol.setCellValueFactory(new PropertyValueFactory<>("totalOtHrs"));
        otHrsCol.setPrefWidth(70);
        otHrsCol.setStyle("-fx-alignment: CENTER;");
        
        // --- BRADFORD FACTOR COLUMN ---
        TableColumn<Object, Integer> bradfordCol = new TableColumn<>("Score");
        bradfordCol.setCellValueFactory(new PropertyValueFactory<>("bradfordScore"));
        bradfordCol.setPrefWidth(70);
        bradfordCol.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold;");
        bradfordCol.setCellFactory(column -> new TableCell<Object, Integer>() {
            @Override
            protected void updateItem(Integer score, boolean empty) {
                super.updateItem(score, empty);
                if (empty || score == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(score));
                    // Dynamic coloring for the score
                    if (score <= 50) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // Green
                    } else if (score <= 200) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Orange
                    } else if (score <= 500) {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Red
                    } else {
                        setStyle("-fx-alignment: CENTER; -fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-background-color: #fadbd8;"); // Dark Red
                    }
                }
            }
        });

        reportTable.getColumns().addAll(empIdCol, nameCol, presentCol, absencesCol, latesCountCol, latesMinsCol, undertimeMinsCol, otHrsCol, bradfordCol);
    }
   private void updateVisualAnalytics() {
        // Clear previous data
        distributionChart.getData().clear();
        topLatesChart.getData().clear();
        tardinessTrendChart.getData().clear(); 
        updateHabitualLatesList();

        ObservableList<Object> items = reportTable.getItems();
        if (items == null || items.isEmpty()) return;

        // --- 1. PIE CHART & BAR CHART (Existing Logic) ---
        int totalPresent = 0;
        int totalAbsences = 0;
        int totalLates = 0;

        for (Object item : items) {
            if (item instanceof EmployeeSummary) {
                EmployeeSummary sum = (EmployeeSummary) item;
                totalPresent += sum.getDaysPresent();
                totalAbsences += sum.getAbsences();
                totalLates += sum.getLatesCount();
            } else if (item instanceof DailyLog) {
                DailyLog log = (DailyLog) item;
                if (log.getStatus().contains("Present")) totalPresent++;
                else if (log.getStatus().contains("Absent")) totalAbsences++;
                if (log.getStatus().contains("Late")) totalLates++;
            }
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        if (totalPresent > 0) pieData.add(new PieChart.Data("Present: " + totalPresent, totalPresent));
        if (totalAbsences > 0) pieData.add(new PieChart.Data("Absent: " + totalAbsences, totalAbsences));
        if (totalLates > 0) pieData.add(new PieChart.Data("Late: " + totalLates, totalLates));
        distributionChart.setData(pieData);

        if (!isDetailedView) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Lates Count");
            List<EmployeeSummary> sortedList = new ArrayList<>();
            for (Object item : items) {
                if (item instanceof EmployeeSummary) sortedList.add((EmployeeSummary) item);
            }
            sortedList.sort((e1, e2) -> Integer.compare(e2.getLatesCount(), e1.getLatesCount()));
            int limit = Math.min(sortedList.size(), 5);
            for (int i = 0; i < limit; i++) {
                EmployeeSummary emp = sortedList.get(i);
                if (emp.getLatesCount() > 0) {
                    String name = truncate(emp.getEmployeeName(), 10);
                    series.getData().add(new XYChart.Data<>(name, emp.getLatesCount()));
                }
            }
            topLatesChart.getData().add(series);
        }

        // --- 2. NEW: TARDINESS TREND (Line Chart) ---
        XYChart.Series<String, Number> trendSeries = new XYChart.Series<>();
        trendSeries.setName("Minutes Late");

        if (isDetailedView) {
            // SINGLE EMPLOYEE VIEW
            trendContextLabel.setText("Individual Performance");
            for (Object item : items) {
                if (item instanceof DailyLog) {
                    DailyLog log = (DailyLog) item;
                    String dateLabel = log.getDate().substring(0, 5); 
                    trendSeries.getData().add(new XYChart.Data<>(dateLabel, log.getLateMins()));
                }
            }
        } else {
            // SUMMARY VIEW (All Employees)
            trendContextLabel.setText("Total Department Tardiness (Sum)");
            // Use the map we populated in loadSummaryReportData
            for (Map.Entry<String, Integer> entry : summaryDailyTardinessMap.entrySet()) {
                trendSeries.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        }
        
        tardinessTrendChart.getData().add(trendSeries);
    }
    private void loadSummaryReportData() {
        LocalDate startDate = dateRangePicker.getValue().getStartDate();
        LocalDate endDate = dateRangePicker.getValue().getEndDate();
        Department selectedDept = departmentCombo.getValue();

        summaryDailyTardinessMap.clear();
        DateTimeFormatter chartDateFmt = DateTimeFormatter.ofPattern("MM/dd");

        Map<Integer, User> targetEmployees = new HashMap<>();
        Map<Integer, Assignment> userAssignments = new HashMap<>();

        ObservableList<User> allActiveUsers = User.getActiveEmployeesWithDepartmentAndPosition();
        for (User user : allActiveUsers) {
            boolean match = false;
            if (selectedDept == null || selectedDept.getId() == 0) {
                match = true;
            } else if (user.getDepartment() != null && user.getDepartment().equals(selectedDept.getDepartmentName())) {
                match = true;
            }

            if (match) {
                targetEmployees.put(user.getId(), user);

                ObservableList<Assignment> asgs = Assignment.getActiveAssignmentsByUserId(user.getId());
                if(asgs != null && !asgs.isEmpty()){
                    userAssignments.put(user.getId(), asgs.get(0));
                }
            }
        }

        Map<Integer, AggregatedSummary> employeeSummaries = new HashMap<>();
        Map<Integer, String> employeeIdToDisplayNameMap = new HashMap<>();
        Map<Integer, String> employeeIdToAgencyNoMap = new HashMap<>();

        for (User user : targetEmployees.values()) {
            employeeSummaries.put(user.getId(), new AggregatedSummary());

            String formattedName = (user.getLname() != null ? user.getLname() : "").trim();
            formattedName += ", " + (user.getFname() != null ? user.getFname() : "").trim();
            if (user.getMname() != null && !user.getMname().trim().isEmpty()) {
                formattedName += " " + user.getMname().trim().substring(0, 1) + ".";
            }
            if (user.getSuffix() != null && !user.getSuffix().trim().isEmpty()) {
                formattedName += " " + user.getSuffix().trim();
            }

            employeeIdToDisplayNameMap.put(user.getId(), formattedName);
            employeeIdToAgencyNoMap.put(user.getId(), user.getAgencyEmployeeNo());
        }

        ObservableList<Attendance> allDailyAttendance = fetchDailyAttendanceForSummary(selectedDept, startDate, endDate);

        Map<Integer, Map<LocalDate, Attendance>> userAttendanceMap = new HashMap<>();
        for (Attendance dailyAtt : allDailyAttendance) {
            int userId = dailyAtt.getId();
            if (!targetEmployees.containsKey(userId)) continue; 
            LocalDate date = ((java.sql.Date) dailyAtt.getDate()).toLocalDate();
            userAttendanceMap.computeIfAbsent(userId, k -> new HashMap<>()).put(date, dailyAtt);
        }

        Map<LocalDate, Boolean> holidayMap = getHolidayMap(startDate, endDate);
        Map<Integer, Map<LocalDate, String>> userLeaveMaps = prefetchAllUserLeaves(startDate, endDate);

        for (Integer userId : targetEmployees.keySet()) {
            AggregatedSummary summary = employeeSummaries.get(userId);
            Map<LocalDate, Attendance> individualAttMap = userAttendanceMap.getOrDefault(userId, Collections.emptyMap());
            Map<LocalDate, String> individualLeaveMap = userLeaveMaps.getOrDefault(userId, Collections.emptyMap());

            Assignment asm = userAssignments.get(userId);

            LocalTime userShiftStart = (asm != null && asm.getStartTime() != null) ? asm.getStartTime() : LocalTime.of(8, 0);
            LocalTime userShiftEnd = (asm != null && asm.getEndTime() != null) ? asm.getEndTime() : LocalTime.of(17, 0);

            boolean isMorningOnly = userShiftEnd.isBefore(LocalTime.of(13, 1)); 
            boolean isAfternoonOnly = userShiftStart.isAfter(LocalTime.of(11, 59)); 

            long expectedWorkMinutes = ChronoUnit.MINUTES.between(userShiftStart, userShiftEnd);

            if (!isMorningOnly && !isAfternoonOnly && expectedWorkMinutes > 300) {
                expectedWorkMinutes -= 60; 
            }

            summary.isCurrentlyAbsent = false;

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
                boolean isHoliday = holidayMap.containsKey(date);
                boolean isOnLeave = individualLeaveMap.containsKey(date);

                boolean isScheduledWorkDay = !isWeekend && !isHoliday && !isOnLeave;

                Attendance dailyAtt = individualAttMap.get(date);
                LocalTime amInTime = null, amOutTime = null, pmInTime = null, pmOutTime = null, otInTime = null, otOutTime = null;
                boolean isPresent = false;

                if (dailyAtt != null) {
                    amInTime = parseTime(dailyAtt.getTimeInAm());
                    amOutTime = parseTime(dailyAtt.getTimeOutAm());
                    pmInTime = parseTime(dailyAtt.getTimeInPm());
                    pmOutTime = parseTime(dailyAtt.getTimeOutPm());
                    otInTime = parseTime(dailyAtt.getOvertimeIn());
                    otOutTime = parseTime(dailyAtt.getOvertimeOut());

                    isPresent = (amInTime != null || pmInTime != null || otInTime != null);
                }

                if (isPresent) {
                    summary.daysPresent++;
                    summary.isCurrentlyAbsent = false;
                }

                if (isScheduledWorkDay) {
                    summary.scheduledWorkDays++;
                    if (isPresent) {
                        long dailyLateMins = 0;

                        if (!isAfternoonOnly) {
                            if (amInTime != null && amInTime.isAfter(userShiftStart)) {
                                dailyLateMins += ChronoUnit.MINUTES.between(userShiftStart, amInTime);
                            }
                        }

                        if (!isMorningOnly) {
                            LocalTime targetPmStart = isAfternoonOnly ? userShiftStart : LocalTime.of(13, 0);

                            if (pmInTime != null && pmInTime.isAfter(targetPmStart)) {
                                dailyLateMins += ChronoUnit.MINUTES.between(targetPmStart, pmInTime);
                            }
                        }

                        if (dailyLateMins > 0) {
                            summary.latesCount++;
                            summary.totalLateMinutes += dailyLateMins;

                            String dateKey = date.format(chartDateFmt);
                            summaryDailyTardinessMap.merge(dateKey, (int)dailyLateMins, Integer::sum);
                        }

                        long minutesWorked = 0;
                        if (amInTime != null && amOutTime != null) {
                            minutesWorked += ChronoUnit.MINUTES.between(amInTime, amOutTime);
                        }
                        if (pmInTime != null && pmOutTime != null) {
                            minutesWorked += ChronoUnit.MINUTES.between(pmInTime, pmOutTime);
                        }

                        if (minutesWorked > 0 && minutesWorked < expectedWorkMinutes) {
                            summary.totalUndertimeMinutes += (expectedWorkMinutes - minutesWorked);
                        }

                    } else {
                        summary.absences++;
                        if (!summary.isCurrentlyAbsent) {
                            summary.absenceSpells++;
                            summary.isCurrentlyAbsent = true;
                        }
                    }
                } else {
                }

                if (otInTime != null && otOutTime != null) {
                    long otMinutes = ChronoUnit.MINUTES.between(otInTime, otOutTime);
                    if (otMinutes > 0) {
                         summary.totalOtHours += (otMinutes / 60.0);
                    }
                }
            } 
        }

        ObservableList<Object> finalSummaryList = FXCollections.observableArrayList();
        for (Map.Entry<Integer, AggregatedSummary> entry : employeeSummaries.entrySet()) {
            int userId = entry.getKey();
            AggregatedSummary aggSummary = entry.getValue();
            String userName = employeeIdToDisplayNameMap.get(userId);
            String agencyNo = employeeIdToAgencyNoMap.get(userId);

            finalSummaryList.add(new EmployeeSummary(
                agencyNo,
                userName,
                aggSummary.daysPresent,
                aggSummary.absences,
                aggSummary.latesCount,
                (int) aggSummary.totalLateMinutes,  
                (int) aggSummary.totalUndertimeMinutes,  
                Math.round(aggSummary.totalOtHours * 100.0) / 100.0, 
                aggSummary.absenceSpells 
            ));
        }

        finalSummaryList.sort((obj1, obj2) -> {
            String id1 = (obj1 instanceof EmployeeSummary) ? ((EmployeeSummary) obj1).getEmpId() : "";
            String id2 = (obj2 instanceof EmployeeSummary) ? ((EmployeeSummary) obj2).getEmpId() : "";
            if (id1 == null) id1 = "";
            if (id2 == null) id2 = "";
            return id1.compareTo(id2);
        });

        reportTable.setItems(finalSummaryList);
        updateSummaryLabelForMultiple(finalSummaryList, selectedDept, startDate, endDate);
    }

    private void updateSummaryLabelForMultiple(ObservableList<Object> data, Department dept, LocalDate start, LocalDate end) {
        if (data == null || data.isEmpty()) {
            currentSummaryText = "No data available for the selected criteria.";
            return;
        }
        int totalPresent = 0; int totalAbsences = 0; int totalLatesCount = 0;
        long totalLatesMins = 0; long totalUndertimeMins = 0; double totalOtHrs = 0.0;

        for (Object item : data) {
            if (item instanceof EmployeeSummary) {
                EmployeeSummary summary = (EmployeeSummary) item;
                totalPresent += summary.getDaysPresent();
                totalAbsences += summary.getAbsences();
                totalLatesCount += summary.getLatesCount();
                totalLatesMins += summary.getLatesMins(); // ADDED
                totalUndertimeMins += summary.getUndertimeMins(); // ADDED
                totalOtHrs += summary.getTotalOtHrs();
            }
        }

        // Format Undertime and Late Minutes Totals
        long undertimeHours = totalUndertimeMins / 60;
        long undertimeRemainingMinutes = totalUndertimeMins % 60;
        String undertimeFormatted = String.format("%d hrs %02d mins", undertimeHours, undertimeRemainingMinutes);

        long lateHours = totalLatesMins / 60;
        long lateRemainingMinutes = totalLatesMins % 60;
        String lateMinsFormatted = String.format("%d hrs %02d mins", lateHours, lateRemainingMinutes);
        
        if (reliabilityScoreLabel != null) {
            reliabilityScoreLabel.setText("--");
            reliabilityScoreLabel.setStyle("-fx-text-fill: #333333;");
            reliabilityMessageLabel.setText("Select a specific employee");
            reliabilityMessageLabel.setStyle("-fx-text-fill: #999999;");
        }
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String reportPeriod = start.format(dateFormatter) + " - " + end.format(dateFormatter);
        String generatedOn = LocalDate.now().format(dateFormatter);
        String deptName = (dept == null || dept.getId() == 0) ? "All Departments" : dept.getDepartmentName();
        StringBuilder sb = new StringBuilder();
//        sb.append("**Employee Attendance Summary Report**\n");
//        sb.append("Department: ").append(deptName).append("\n");
//        sb.append("Report Period: ").append(reportPeriod).append("\n");
//        sb.append("Generated On: ").append(generatedOn).append("\n\n");
        sb.append("                          **SUMMARY:**\n");
        sb.append("- Total Days Present: ").append(totalPresent).append("\n");
        sb.append("- Total Absences: ").append(totalAbsences).append("\n");
        sb.append("- Total Lates (Count): ").append(totalLatesCount).append("\n"); // Clarified label
        sb.append("- Total Lates (Time): ").append(lateMinsFormatted).append("\n"); // ADDED
        sb.append("- Total Undertime: ").append(undertimeFormatted).append("\n"); // ADDED
        sb.append("- Total OT (Hrs): ").append(String.format("%.2f", totalOtHrs)).append("\n");
        currentSummaryText = sb.toString();
        updateVisualAnalytics();
    }
    
    private String buildSummaryQuery() {
        Department selectedDept = departmentCombo.getValue();

        // This query now only counts lates based on status
        String sql = "SELECT " +
            "u.user_id, " +
            "CONCAT(u.user_lname, ', ', u.user_fname) AS name, " +
            "COUNT(DISTINCT CASE WHEN (a.time_in IS NOT NULL AND a.time_in != '00:00:00') OR (a.overtime_in IS NOT NULL AND a.overtime_in != '00:00:00') THEN a.date END) AS days_present, " + // Count days with any time-in
            "SUM(CASE WHEN a.attendance_status = 2 THEN 1 ELSE 0 END) AS lates_count, " +
            // "SUM(COALESCE(a.late_minutes, 0)) AS lates_mins, " + // <-- REMOVED
            // "SUM(COALESCE(a.undertime_minutes, 0)) AS undertime_mins, " + // <-- REMOVED
            "SUM( " +
            "    CASE " +
            "        WHEN a.overtime_in IS NOT NULL AND a.overtime_in != '00:00:00' AND a.overtime_out IS NOT NULL AND a.overtime_out != '00:00:00' " +
            "        THEN ROUND(TIME_TO_SEC(TIMEDIFF(a.overtime_out, a.overtime_in)) / 3600, 2) " +
            "        ELSE 0 " +
            "    END " +
            ") AS total_ot_hrs " +
            "FROM user u " +
            "JOIN assignment asg ON u.user_id = asg.user_id AND asg.status = 1 " +
            "JOIN position p ON asg.position_id = p.position_id " +
            "JOIN department d ON p.department_id = d.department_id " +
            "LEFT JOIN attendance a ON u.user_id = a.user_id AND a.date BETWEEN ? AND ? " +
            "WHERE u.user_status = 1 ";

        if (selectedDept != null && selectedDept.getId() != 0) {
            sql += "AND d.department_id = ? ";
        }

        sql += "GROUP BY u.user_id, name ORDER BY name";
        return sql;
    }
    
    // Helper to get holiday map
    // Helper to get holiday map (Updated for date ranges)
    private Map<LocalDate, Boolean> getHolidayMap(LocalDate reportStartDate, LocalDate reportEndDate) {
        Map<LocalDate, Boolean> holidays = new HashMap<>();
        // Corrected Query: Uses start_date/end_date and checks ranges
        String sql = "SELECT sc.start_date, sc.end_date " +
                     "FROM special_calendar sc " +
                     "WHERE sc.status = 1 " + // Assuming status 1 means it's an active/valid holiday
                     "AND sc.start_date <= ? " + // Holiday starts before or on report end
                     "AND sc.end_date >= ?";   // Holiday ends after or on report start

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(reportEndDate));   // Use reportEndDate
            ps.setDate(2, java.sql.Date.valueOf(reportStartDate)); // Use reportStartDate

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate holidayStart = rs.getDate("start_date").toLocalDate();
                LocalDate holidayEnd = rs.getDate("end_date").toLocalDate();

                // Iterate through the dates within this specific holiday record
                for (LocalDate date = holidayStart; !date.isAfter(holidayEnd); date = date.plusDays(1)) {
                    // Check if this holiday date falls within the report's date range
                    if (!date.isBefore(reportStartDate) && !date.isAfter(reportEndDate)) {
                        // Add the valid holiday date to the map
                        holidays.put(date, true);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching holiday map: " + e.getMessage());
            e.printStackTrace();
        }
        return holidays;
    }
    private int getLeaveDaysForUser(int userId, LocalDate reportStartDate, LocalDate reportEndDate) {
        Set<LocalDate> leaveDaysSet = new HashSet<>(); // Use a Set to avoid double-counting days
        // Corrected Query: Uses user_timeoff table and checks ranges
        String sql = "SELECT ut.start_date, ut.end_date " +
                     "FROM user_timeoff ut " +
                     "WHERE ut.user_id = ? " +
                     "AND ut.status = 1 " + // Assuming status 1 means approved
                     "AND ut.start_date <= ? " + // Leave starts before or on report end
                     "AND ut.end_date >= ?";   // Leave ends after or on report start

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(reportEndDate));   // Use reportEndDate for start_date comparison
            ps.setDate(3, java.sql.Date.valueOf(reportStartDate)); // Use reportStartDate for end_date comparison

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate leaveStart = rs.getDate("start_date").toLocalDate();
                LocalDate leaveEnd = rs.getDate("end_date").toLocalDate();

                // Iterate through the dates within this specific leave record
                for (LocalDate date = leaveStart; !date.isAfter(leaveEnd); date = date.plusDays(1)) {
                    // Check if this leave date falls within the report's date range
                    if (!date.isBefore(reportStartDate) && !date.isAfter(reportEndDate)) {
                        // Check if it's a weekday (optional, but good practice)
                        DayOfWeek dayOfWeek = date.getDayOfWeek();
                        if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                             // Add the valid leave day to the set
                             leaveDaysSet.add(date);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching leave days for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
        // The size of the set gives the total unique leave days within the report range
        return leaveDaysSet.size();
    }


    private void setupDetailedReportTable() {
        reportTable.getColumns().clear();
        reportTable.setPlaceholder(new Label("Generating detailed log..."));
        
        // PREVENT OVERFLOW: Fit columns to table width
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Object, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(90); // Reduced
        dateCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> dayCol = new TableColumn<>("Day");
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        dayCol.setPrefWidth(50); // Reduced
        dayCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> amInCol = new TableColumn<>("AM In");
        amInCol.setCellValueFactory(new PropertyValueFactory<>("amIn"));
        amInCol.setPrefWidth(80); 
        amInCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> amOutCol = new TableColumn<>("AM Out");
        amOutCol.setCellValueFactory(new PropertyValueFactory<>("amOut"));
        amOutCol.setPrefWidth(80); 
        amOutCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> pmInCol = new TableColumn<>("PM In");
        pmInCol.setCellValueFactory(new PropertyValueFactory<>("pmIn"));
        pmInCol.setPrefWidth(80); 
        pmInCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> pmOutCol = new TableColumn<>("PM Out");
        pmOutCol.setCellValueFactory(new PropertyValueFactory<>("pmOut"));
        pmOutCol.setPrefWidth(80); 
        pmOutCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> otInCol = new TableColumn<>("OT In");
        otInCol.setCellValueFactory(new PropertyValueFactory<>("otIn"));
        otInCol.setPrefWidth(80); 
        otInCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> otOutCol = new TableColumn<>("OT Out");
        otOutCol.setCellValueFactory(new PropertyValueFactory<>("otOut"));
        otOutCol.setPrefWidth(80); 
        otOutCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Double> hrsCol = new TableColumn<>("Hrs");
        hrsCol.setCellValueFactory(new PropertyValueFactory<>("hrsWorked"));
        hrsCol.setPrefWidth(60); 
        hrsCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> lateTimeCol = new TableColumn<>("Late(m)");
        lateTimeCol.setCellValueFactory(new PropertyValueFactory<>("lateTimeFormatted"));
        lateTimeCol.setPrefWidth(90); 
        lateTimeCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Double> undertimeHrsCol = new TableColumn<>("UT(hr)");
        undertimeHrsCol.setCellValueFactory(new PropertyValueFactory<>("undertimeHours"));
        undertimeHrsCol.setPrefWidth(70); 
        undertimeHrsCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, Double> otCol = new TableColumn<>("OT(hr)");
        otCol.setCellValueFactory(new PropertyValueFactory<>("otHrs"));
        otCol.setPrefWidth(60); 
        otCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Object, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        statusCol.setStyle("-fx-alignment: CENTER-LEFT;");
        
        statusCol.setCellFactory(column -> new TableCell<Object, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String lowerItem = item.toLowerCase();

                    // Priority Styling
                    if (lowerItem.contains("absent")) {
                        // Red text, Bold
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else if (lowerItem.contains("late")) {
                        // Orange text
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    } else if (lowerItem.contains("holiday")) {
                        // Blue text
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #2980b9; -fx-font-weight: bold;");
                    } else if (lowerItem.contains("rest day")) {
                        // Grey text
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
                    } else if (lowerItem.contains("present")) {
                        // Green text
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #27ae60;");
                    } else if (lowerItem.contains("error") || lowerItem.contains("incomplete")) {
                        // Dark Red background for errors
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: white; -fx-background-color: #c0392b;");
                    } else {
                        // Default
                        setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: black;");
                    }
                }
            }
        });

        reportTable.getColumns().addAll(dateCol, dayCol, amInCol, amOutCol, pmInCol, pmOutCol, otInCol, otOutCol, hrsCol, lateTimeCol, undertimeHrsCol, otCol, statusCol);
    }
    private void loadDetailedReportData(User user) {
        ObservableList<Object> dailyLogs = FXCollections.observableArrayList();
        LocalDate startDate = dateRangePicker.getValue().getStartDate();
        LocalDate endDate = dateRangePicker.getValue().getEndDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");

        Map<LocalDate, Attendance> attMap = getAttendanceMapForUser(user.getId(), startDate, endDate);
        Map<LocalDate, String> leaveMap = getLeaveMapForUser(user.getId(), startDate, endDate);
        Map<LocalDate, Boolean> holidayMap = getHolidayMap(startDate, endDate);

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.format(dtf);
            String dayStr = date.getDayOfWeek().toString().substring(0, 3);
            String amIn = "--"; String amOut = "--"; String pmIn = "--";
            String pmOut = "--"; String otIn = "--"; String otOut = "--";
            double regHrsWorked = 0.0; double otHrsWorked = 0.0;
            int lateMinsCalc = 0; 
            String lateTimeFormatted = "--";
            double undertimeHoursCalc = 0.0;
            String status = "";

            boolean isWeekend = date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
            boolean isHoliday = holidayMap.containsKey(date);
            boolean isOnLeave = leaveMap.containsKey(date);
            
            // A day is a "Scheduled Work Day" only if it is NOT a weekend, NOT a holiday, and NOT a leave
            boolean isScheduledWorkDay = !isWeekend && !isHoliday && !isOnLeave;

            if (attMap.containsKey(date)) {
                Attendance att = attMap.get(date);
                LocalTime amInTime = parseTime(att.getTimeInAm()); 
                LocalTime amOutTime = parseTime(att.getTimeOutAm());
                LocalTime pmInTime = parseTime(att.getTimeInPm()); 
                LocalTime pmOutTime = parseTime(att.getTimeOutPm());
                LocalTime otInTime = parseTime(att.getOvertimeIn());
                LocalTime otOutTime = parseTime(att.getOvertimeOut());

                amIn = (amInTime != null) ? amInTime.format(timeFormatter) : "--"; 
                amOut = (amOutTime != null) ? amOutTime.format(timeFormatter) : "--";
                pmIn = (pmInTime != null) ? pmInTime.format(timeFormatter) : "--"; 
                pmOut = (pmOutTime != null) ? pmOutTime.format(timeFormatter) : "--";
                otIn = (otInTime != null) ? otInTime.format(timeFormatter) : "--"; 
                otOut = (otOutTime != null) ? otOutTime.format(timeFormatter) : "--";

                long amMinutes = 0; long pmMinutes = 0;
                if (amInTime != null && amOutTime != null) amMinutes = ChronoUnit.MINUTES.between(amInTime, amOutTime);
                if (pmInTime != null && pmOutTime != null) pmMinutes = ChronoUnit.MINUTES.between(pmInTime, pmOutTime);
                
                if (amMinutes < 0) amMinutes = 0;
                if (pmMinutes < 0) pmMinutes = 0;
                
                long totalMinutesWorked = amMinutes + pmMinutes;
                regHrsWorked = Math.round((totalMinutesWorked / 60.0) * 100.0) / 100.0;

                if (otInTime != null && otOutTime != null) {
                    long otMinutes = ChronoUnit.MINUTES.between(otInTime, otOutTime);
                    if(otMinutes < 0) otMinutes = 0;
                    otHrsWorked = Math.round((otMinutes / 60.0) * 100.0) / 100.0;
                }

                // --- Calculate Lates (Only on Scheduled Work Days) ---
                if (isScheduledWorkDay && amInTime != null && amInTime.isAfter(SHIFT_AM_START)) {
                    lateMinsCalc = (int) ChronoUnit.MINUTES.between(SHIFT_AM_START, amInTime);
                    long lateHrs = lateMinsCalc / 60;
                    long lateRemMins = lateMinsCalc % 60;
                    lateTimeFormatted = String.format("%d hr %02d min", lateHrs, lateRemMins);
                } else {
                    lateMinsCalc = 0;
                    lateTimeFormatted = "--";
                }

                // --- Calculate Undertime (Only on Scheduled Work Days) ---
                if (isScheduledWorkDay && totalMinutesWorked > 0 && totalMinutesWorked < FULL_DAY_MINUTES) {
                     long undertimeMinutes = FULL_DAY_MINUTES - totalMinutesWorked;
                     undertimeHoursCalc = Math.round((undertimeMinutes / 60.0) * 100.0) / 100.0;
                } else {
                      undertimeHoursCalc = 0.0;
                }

                if (leaveMap.containsKey(date)) {
                    String leaveInfo = leaveMap.get(date); // Now this string should contain type AND schedule, e.g. "Sick Leave|PM Only"
                    String[] parts = leaveInfo.split("\\|");
                    String type = parts[0];
                    String schedule = parts.length > 1 ? parts[1] : "Full Day";

                    if ("Full Day".equalsIgnoreCase(schedule)) {
                        amIn = type; amOut = ""; pmIn = ""; pmOut = "";
                        status = type; 
                    } else if ("AM Only".equalsIgnoreCase(schedule)) {
                        amIn = type; amOut = ""; 
                        // Keep PM logs if they exist
                        if (pmInTime != null) pmIn = pmInTime.format(timeFormatter);
                        if (pmOutTime != null) pmOut = pmOutTime.format(timeFormatter);
                        status = type + " (AM) / Present (PM)";
                    } else if ("PM Only".equalsIgnoreCase(schedule)) {
                        // Keep AM logs
                        if (amInTime != null) amIn = amInTime.format(timeFormatter);
                        if (amOutTime != null) amOut = amOutTime.format(timeFormatter);
                        pmIn = type; pmOut = "";
                        status = "Present (AM) / " + type + " (PM)";
                    }
                }
                
                if (isHoliday) {
                    // FIX: If it is a holiday, set status to Holiday even if they have logs
                    status = "Holiday (Duty)"; // Or just "Holiday" if you prefer
                } else if (isWeekend) {
                    status = "Rest Day (Duty)";
                } else if (isOnLeave) {
                    status = leaveMap.get(date) + " (Duty)"; // Unusual, but possible
                } else {
                    // Normal Work Day Status
                    String baseStatus;
                    if (att.getAttendance_status() != null && att.getAttendance_status().equals("Late")) {
                        baseStatus = "Present (Late)";
                    } else if ((amInTime != null && amOutTime == null) || (pmInTime != null && pmOutTime == null)) {
                        baseStatus = "Error: Incomplete";
                    } else {
                        baseStatus = "Present";
                    }

                    List<String> qualifiers = new ArrayList<>();
                    if (undertimeHoursCalc > 0) qualifiers.add("Undertime");
                    if (otHrsWorked > 0) qualifiers.add("OT");
                    
                    status = baseStatus;
                    if (!qualifiers.isEmpty()) status += " (" + String.join(", ", qualifiers) + ")";
                }

            } else {
                // --- No Attendance Record ---
                if (leaveMap.containsKey(date)) status = leaveMap.get(date);
                else if (holidayMap.containsKey(date)) status = "Holiday";
                else if (isWeekend) status = "Rest Day";
                else status = "Absent";
            }

            dailyLogs.add(new DailyLog(dateStr, dayStr, amIn, amOut, pmIn, pmOut, otIn, otOut, regHrsWorked, lateMinsCalc, lateTimeFormatted, undertimeHoursCalc, otHrsWorked, status));
        }

        reportTable.setItems(dailyLogs);
        updateSummaryLabelForSingle(dailyLogs, user, startDate, endDate);
    }
    private void updateSummaryLabelForSingle(ObservableList<Object> data, User user, LocalDate start, LocalDate end) {
        if (data == null || data.isEmpty()) {
           currentSummaryText = "No data available for the selected employee and date range.";
           return;
        }

        int totalScheduledWorkDays = 0;
        int totalPresent = 0;
        int totalAbsences = 0;
        int totalLates = 0;
        double totalRegHrsWorked = 0.0;
        double totalOtHrsWorked = 0.0; 
        long totalUndertimeMinutes = 0;
        
        // --- BRADFORD FACTOR VARIABLES ---
        int absenceSpells = 0;
        boolean currentlyInAbsenceSpell = false;
        // --------------------------------

        Map<LocalDate, Boolean> holidayMap = getHolidayMap(start, end);
        Map<LocalDate, String> leaveMap = getLeaveMapForUser(user.getId(), start, end);

        // Map data for easy lookup by date
        Map<LocalDate, DailyLog> logMap = new HashMap<>();
        for(Object item : data) {
             if (item instanceof DailyLog) {
                DailyLog log = (DailyLog) item;
                try {
                    LocalDate logDate = LocalDate.parse(log.getDate(), DateTimeFormatter.ofPattern("MM/dd/yy"));
                    logMap.put(logDate, log);
                } catch (Exception e) {}
            }
        }

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            DailyLog log = logMap.get(date);
            String status = (log != null) ? log.getStatus() : "Absent";
            String statusLower = status.toLowerCase();

            // --- BRADFORD FACTOR LOGIC ---
            // If strictly Absent (unauthorized), trigger spell count
            if (status.equals("Absent")) {
                if (!currentlyInAbsenceSpell) {
                    absenceSpells++;
                    currentlyInAbsenceSpell = true;
                }
            } 
            // If Present, break the spell
            else if (statusLower.contains("present") || statusLower.contains("(ot)")) {
                currentlyInAbsenceSpell = false;
            }
            // Note: Holidays/Rest Days do not break the spell, nor do they start one.
            // -----------------------------

            DayOfWeek dayOfWeek = date.getDayOfWeek();
            boolean isWeekend = (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
            
            if (!isWeekend && !holidayMap.containsKey(date) && !leaveMap.containsKey(date)) {
                totalScheduledWorkDays++;
                
                if (log != null) {
                    if (statusLower.contains("present") || statusLower.contains("(ot)") ) {
                        totalPresent++;
                        double regHours = log.getHrsWorked();
                         if (regHours < 8.0 && regHours > 0) { 
                             totalUndertimeMinutes += (long)((8.0 - regHours) * 60);
                         }
                    } else if (status.equals("Absent")) {
                        totalAbsences++;
                    }
                    if (statusLower.contains("late")) {
                         totalLates++;
                    }
                    totalRegHrsWorked += log.getHrsWorked();
                    totalOtHrsWorked += log.getOtHrs();
                } else {
                     totalAbsences++;
                }
            } else {
                 if(log != null) {
                     totalRegHrsWorked += log.getHrsWorked();
                     totalOtHrsWorked += log.getOtHrs();
                      if (statusLower.contains("late")) {
                           totalLates++;
                      }
                 }
            }
        }

        // Calculate Score: S^2 x D
        int bradfordScore = (absenceSpells * absenceSpells) * totalAbsences;
        
        if (reliabilityScoreLabel != null) {
            reliabilityScoreLabel.setText(String.valueOf(bradfordScore));
            
            // Dynamic Coloring & Message
            if (bradfordScore <= 50) {
                reliabilityScoreLabel.setStyle("-fx-text-fill: #2ecc71;"); // Green
                reliabilityMessageLabel.setText("Excellent Attendance");
                reliabilityMessageLabel.setStyle("-fx-text-fill: #2ecc71;");
            } else if (bradfordScore <= 200) {
                reliabilityScoreLabel.setStyle("-fx-text-fill: #f39c12;"); // Orange
                reliabilityMessageLabel.setText("Monitor / Verbal Warning");
                reliabilityMessageLabel.setStyle("-fx-text-fill: #f39c12;");
            } else if (bradfordScore <= 500) {
                reliabilityScoreLabel.setStyle("-fx-text-fill: #e74c3c;"); // Red
                reliabilityMessageLabel.setText("Action Required");
                reliabilityMessageLabel.setStyle("-fx-text-fill: #e74c3c;");
            } else {
                reliabilityScoreLabel.setStyle("-fx-text-fill: #c0392b;"); // Dark Red
                reliabilityMessageLabel.setText("Severe / Disciplinary");
                reliabilityMessageLabel.setStyle("-fx-text-fill: #c0392b;");
            }
        }

        long undertimeHours = totalUndertimeMinutes / 60;
        long undertimeRemainingMinutes = totalUndertimeMinutes % 60;
        String undertimeFormatted = String.format("%d hrs %02d mins", undertimeHours, undertimeRemainingMinutes);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Employee ID: ").append(user.getAgencyEmployeeNo()).append("\n");
        sb.append("                          **SUMMARY:**\n");
        sb.append("- Total Sched. Work Days: ").append(totalScheduledWorkDays).append("\n");
        sb.append("- Total Days Present: ").append(totalPresent).append("\n");
        sb.append("- Total No. of Lates: ").append(totalLates).append("\n");
        sb.append("- Total Undertime: ").append(undertimeFormatted).append("\n");
        sb.append("- Total Hrs Worked: ").append(String.format("%.2f", totalRegHrsWorked)).append("\n");
        sb.append("- Total OT Hours: ").append(String.format("%.2f", totalOtHrsWorked)).append("\n");
        sb.append("- Total Absences: ").append(totalAbsences).append("\n");
        sb.append("- Absence Spells: ").append(absenceSpells).append("\n");
        sb.append("- Reliability Score: ").append(bradfordScore).append("\n");
        
        currentSummaryText = sb.toString();
    }
    
    private void updateHabitualLatesList() {
        if (habitualLatesListView == null) return;
        
        habitualLatesListView.getItems().clear();
        ObservableList<Object> items = reportTable.getItems();
        
        if (items == null || items.isEmpty()) {
            habitualLatesListView.setPlaceholder(new Label("No data generated"));
            return;
        }

        int threshold = 10; // Handbook Chapter 1, Sec II.H threshold
        int count = 0;

        if (!isDetailedView) {
            // --- Summary View (All Employees) ---
            for (Object item : items) {
                if (item instanceof EmployeeSummary) {
                    EmployeeSummary es = (EmployeeSummary) item;
                    if (es.getLatesCount() >= threshold) {
                        String entry = String.format("%s (%d Lates)", truncate(es.getEmployeeName(), 20), es.getLatesCount());
                        habitualLatesListView.getItems().add(entry);
                        count++;
                    }
                }
            }
        } else {
            // --- Detailed View (Single Employee) ---
            // Calculate total lates for the displayed period
            int totalLates = 0;
            String empName = "";
            
            // Get name from combo box since table items are logs
            if (employeeCombo.getValue() != null) {
                empName = employeeCombo.getValue().getFullName();
            }

            for (Object item : items) {
                if (item instanceof DailyLog) {
                    DailyLog dl = (DailyLog) item;
                    if (dl.getStatus().toLowerCase().contains("late")) {
                        totalLates++;
                    }
                }
            }

            if (totalLates >= threshold) {
                String entry = String.format("%s (%d Lates)", truncate(empName, 20), totalLates);
                habitualLatesListView.getItems().add(entry);
                count++;
            }
        }

        if (count == 0) {
            habitualLatesListView.setPlaceholder(new Label("No habitual lates detected."));
        }
    }
    
    private Map<LocalDate, Attendance> getAttendanceMapForUser(int userId, LocalDate start, LocalDate end) {
        Map<LocalDate, Attendance> map = new HashMap<>();
        // This is based on the old controller's getAttendanceByDateRange
        ObservableList<Attendance> list = getAttendanceByDateRange(userId, start, end);
        for (Attendance att : list) {
            map.put(((java.sql.Date) att.getDate()).toLocalDate(), att);
        }
        return map;
    }
    
    private Map<LocalDate, String> getLeaveMapForUser(int userId, LocalDate reportStartDate, LocalDate reportEndDate) {
        Map<LocalDate, String> map = new HashMap<>();
        // Modified query to fetch schedule_type
        String sql = "SELECT ut.start_date, ut.end_date, t.timeoff_type, ut.schedule_type " + 
                     "FROM user_timeoff ut " +
                     "JOIN timeoff t ON ut.timeoff_id = t.timeoff_id " + 
                     "WHERE ut.user_id = ? " +
                     "AND ut.status = 1 " + 
                     "AND ut.start_date <= ? " +
                     "AND ut.end_date >= ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, java.sql.Date.valueOf(reportEndDate));
            ps.setDate(3, java.sql.Date.valueOf(reportStartDate));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDate leaveStart = rs.getDate("start_date").toLocalDate();
                LocalDate leaveEnd = rs.getDate("end_date").toLocalDate();
                String leaveType = rs.getString("timeoff_type");
                String scheduleType = rs.getString("schedule_type"); // Fetch schedule type
                
                // Construct value as "Type|Schedule"
                String mapValue = leaveType + "|" + (scheduleType != null ? scheduleType : "Full Day");

                for (LocalDate date = leaveStart; !date.isAfter(leaveEnd); date = date.plusDays(1)) {
                    if (!date.isBefore(reportStartDate) && !date.isAfter(reportEndDate)) {
                        map.put(date, mapValue); // Store combined string
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
    
    private String getEarliestTime(String time1, String time2) {
        LocalTime t1 = parseTime(time1);
        LocalTime t2 = parseTime(time2);
        if (t1 == null) return formatTime(t2);
        if (t2 == null) return formatTime(t1);
        return formatTime(t1.isBefore(t2) ? t1 : t2);
    }
    
    private String getLatestTime(String time1, String time2) {
        LocalTime t1 = parseTime(time1);
        LocalTime t2 = parseTime(time2);
        if (t1 == null) return formatTime(t2);
        if (t2 == null) return formatTime(t1);
        return formatTime(t1.isAfter(t2) ? t1 : t2);
    }
    
    private double calculateHours(String in, String out) {
        LocalTime t1 = parseTime(in);
        LocalTime t2 = parseTime(out);
        if (t1 == null || t2 == null) return 0.0;
        long minutes = ChronoUnit.MINUTES.between(t1, t2);
        // Assuming 1 hour lunch break if worked > 5 hours
        if (minutes > 300) minutes -= 60; 
        return Math.round((minutes / 60.0) * 100.0) / 100.0;
    }

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
    
    private String formatTime(LocalTime time) {
        if (time == null) return "--";
        try {
            return time.format(DateTimeFormatter.ofPattern("hh:mm a"));
        } catch (Exception e) {
            return "--";
        }
    }
    private String getReportTargetDescription() {
        User emp = employeeCombo.getValue();
        Department dept = departmentCombo.getValue();
        String target;

        if (emp != null && emp.getId() != 0) { // [cite: 407]
            target = "employee: " + emp.getFullName() + " (ID: " + emp.getAgencyEmployeeNo() + ")";
        } else if (dept != null && dept.getId() != 0) { // [cite: 406]
            target = "department: " + dept.getDepartmentName();
        } else {
            target = "All Employees";
        }
        return target;
    }
    private String getAnalyticsSummaryString() {
        ObservableList<Object> items = reportTable.getItems();
        if (items == null || items.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        sb.append("\n\nANALYTICS OVERVIEW\n");
        sb.append("===================\n");

        // 1. Distribution Stats
        int totalItems = 0;
        int present = 0;
        int absent = 0;
        int late = 0;

        for (Object item : items) {
            if (item instanceof EmployeeSummary) {
                EmployeeSummary sum = (EmployeeSummary) item;
                present += sum.getDaysPresent();
                absent += sum.getAbsences();
                late += sum.getLatesCount();
                // For summary view, total "items" isn't rows, but total days tracked. 
                // We approximate distribution based on total events (Present + Absent).
                totalItems += (sum.getDaysPresent() + sum.getAbsences());
            } else if (item instanceof DailyLog) {
                DailyLog log = (DailyLog) item;
                totalItems++;
                if (log.getStatus().contains("Present")) present++;
                else if (log.getStatus().contains("Absent")) absent++;
                if (log.getStatus().contains("Late")) late++;
            }
        }

        if (totalItems > 0) {
            double presentPct = ((double) present / totalItems) * 100;
            double absentPct = ((double) absent / totalItems) * 100;
            double latePct = (present > 0) ? ((double) late / present) * 100 : 0; // Late is a subset of Present usually, or separate metric

            sb.append("ATTENDANCE DISTRIBUTION:\n");
            sb.append(String.format("- Present: %d (%.1f%%)\n", present, presentPct));
            sb.append(String.format("- Absent:  %d (%.1f%%)\n", absent, absentPct));
            sb.append(String.format("- Late:    %d (%.1f%% of Present days)\n", late, latePct));
        }

        // 2. Top Lates (Only for Summary View)
        if (!isDetailedView) {
            sb.append("\nTOP EMPLOYEES BY LATENESS:\n");
            List<EmployeeSummary> sortedList = new ArrayList<>();
            for (Object item : items) {
                if (item instanceof EmployeeSummary) sortedList.add((EmployeeSummary) item);
            }
            // Sort descending by Late Count
            sortedList.sort((e1, e2) -> Integer.compare(e2.getLatesCount(), e1.getLatesCount()));

            int count = 0;
            for (EmployeeSummary emp : sortedList) {
                if (emp.getLatesCount() > 0 && count < 5) {
                    sb.append(String.format("%d. %s - %d lates (%d mins)\n", 
                        ++count, emp.getEmployeeName(), emp.getLatesCount(), emp.getLatesMins()));
                }
            }
            if (count == 0) sb.append("- No lates recorded.\n");
        }

        return sb.toString();
    }
    private Set<Integer> getEmployeeIdsForDTR() {
        Set<Integer> employeeIdsToGenerate = new HashSet<>();
        User selectedEmployee = employeeCombo.getValue();
        Department selectedDept = departmentCombo.getValue();

        if (selectedEmployee != null && selectedEmployee.getId() != 0) {
            // Case 1: Specific employee selected
            employeeIdsToGenerate.add(selectedEmployee.getId());
        } else {
            // Case 2: "All Employees" selected, use department filter
            for (User user : employeeCombo.getItems()) {
                if (user.getId() == 0) continue; // Skip "All Employees" dummy
                
               // If "All Departments" is selected, add everyone
                if (selectedDept == null || selectedDept.getId() == 0) { // <-- CORRECTED
                    employeeIdsToGenerate.add(user.getId());
                }
                // Otherwise, only add users from the visible list (who match the dept)
                else {
                    employeeIdsToGenerate.add(user.getId());
                }
            }
        }
        return employeeIdsToGenerate;
    }

   @FXML
    public void generateDTR(ActionEvent event) {
        Set<Integer> employeeIdsToGenerate = getEmployeeIdsForDTR();
        if (employeeIdsToGenerate.isEmpty()) {
            Modal.alert("No Employee Selected", "Please select at least one employee to generate DTR.");
            return;
        }

        // 1. Ask the user where to save FIRST
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Destination Folder for DTRs");
        File selectedDirectory = directoryChooser.showDialog((Stage) allDTRBtn.getScene().getWindow());

        if (selectedDirectory == null) {
            return; // User cancelled
        }

        String message = employeeIdsToGenerate.size() == 1
                ? "This will generate the selected employee DTR including overtime"
                : "This will generate DTR with overtime for " + employeeIdsToGenerate.size() + " selected employees";
        
        boolean actionIsConfirmed = Modal.actionConfirmed("Generate DTR", "Do you want to generate?", message);
        
        if (actionIsConfirmed) {
            // Pass the selected folder to the generation method
            generateDOCX(employeeIdsToGenerate, selectedDirectory);
        }
    }
    private void generateDOCX(Set<Integer> employeeIds, File userSelectedFolder) {
        try {
            int successCount = 0;
            int totalCount = employeeIds.size();
            LocalDate startDate = dateRangePicker.getValue().getStartDate();
            LocalDate endDate = dateRangePicker.getValue().getEndDate();
            
            String folderName = startDate.getMonth().toString() + "_" + startDate.getYear() + "_DTR";
            File dtrFolder = new File(userSelectedFolder, folderName);

            if (!dtrFolder.exists()) {
                if (!dtrFolder.mkdirs()) {
                    Modal.alert("Error", "Could not create folder: " + dtrFolder.getAbsolutePath());
                    return;
                }
            }

            for (Integer employeeId : employeeIds) {
                User currentUser = User.getUserByUserId(employeeId); 
                if (currentUser == null) continue;
                
                // --- FIX 1: Name Format (LASTNAME, FIRST NAME M.I. EXTENSION) ---
                String lname = currentUser.getLname() != null ? currentUser.getLname().trim() : "";
                String fname = currentUser.getFname() != null ? currentUser.getFname().trim() : "";
                String mname = currentUser.getMname();
                String suffix = currentUser.getSuffix();
                
                String mi = (mname != null && !mname.isEmpty()) ? mname.substring(0, 1) + "." : "";
                String ext = (suffix != null && !suffix.equalsIgnoreCase("None")) ? suffix : "";
                
                String fullNameFormatted = lname + ", " + fname + " " + mi + " " + ext;
                String employeeName = fullNameFormatted.toUpperCase().trim(); // ALL CAPS

                // --- FIX 2: Position (All Caps) ---
                String position = getEmployeePosition(employeeId);
                if (position == null) position = "";
                position = position.toUpperCase();

                // 2. Load Template
                java.io.InputStream templateStream = getClass().getResourceAsStream("/DTR.docx");
                if (templateStream == null) {
                    File externalFile = new File("DTR.docx");
                    if (externalFile.exists()) {
                        templateStream = new FileInputStream(externalFile);
                    } else {
                        Modal.alert("Error", "Template Not Found: DTR.docx could not be found.");
                        return;
                    }
                }

                // 3. Process Document
                try (XWPFDocument doc = new XWPFDocument(templateStream)) {
                
                    // --- FIX 3: Month Format (DECEMBER 2025) ---
                    String monthYearText = startDate.getMonth().toString() + " " + startDate.getYear();
                    
                    String targetNameText = "Name:";
                    String targetMonthText = "For the month of";

                    boolean targetNameFound = false;
                    boolean targetMonthFound = false;
                    
                    // Counters to distinguish between Left (1st) and Right (2nd) copies
                    int nameOccurrence = 0;
                    int monthOccurrence = 0;
                    
                    ObservableList<Attendance> employeeAttendance = getAttendanceByDateRange(employeeId, startDate, endDate);
                    
                    for (IBodyElement element : doc.getBodyElements()) {
                        if (element instanceof XWPFParagraph) {
                            XWPFParagraph paragraph = (XWPFParagraph) element;
                            String text = paragraph.getText();

                            // --- SECTION A: NAME & POSITION ---
                            if (text.contains(targetNameText)) {
                                nameOccurrence++;
                                
                                // Reduce spacing to prevent spill-over
                                paragraph.setSpacingAfter(0); 
                                
                                // Clear existing
                                for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                                    paragraph.removeRun(i);
                                }
                                
                                // 1. "Name:" Label
                                XWPFRun run1 = paragraph.createRun();
                                // FIX: Add Tab for 2nd Copy to prevent overlap
                                if (nameOccurrence == 2) {
                                    run1.addTab(); 
                                }
                                run1.setText("Name:   "); 
                                run1.setFontFamily("Times New Roman");
                                run1.setFontSize(11);

                                // 2. Name Value (Underlined, No Bold, ALL CAPS)
                                XWPFRun nameValue = paragraph.createRun();
                                nameValue.setText(employeeName);
                                nameValue.setFontFamily("Times New Roman");
                                nameValue.setFontSize(11);
                                nameValue.setUnderline(UnderlinePatterns.SINGLE); 
                                nameValue.setBold(false); 

                                // 3. Break for Position Line
                                nameValue.addBreak(); 

                                // 4. "Position:" Label
                                XWPFRun run2 = paragraph.createRun();
                                // FIX: Add Tab for 2nd Copy
                                if (nameOccurrence == 2) {
                                    run2.addTab();
                                }
                                run2.setText("Position: "); 
                                run2.setFontFamily("Times New Roman");
                                run2.setFontSize(11);

                                // 5. Position Value (Underlined, No Bold, ALL CAPS)
                                XWPFRun posValue = paragraph.createRun();
                                posValue.setText(position.isEmpty() ? "________________" : position);
                                posValue.setFontFamily("Times New Roman");
                                posValue.setFontSize(11);
                                if (!position.isEmpty()) {
                                    posValue.setUnderline(UnderlinePatterns.SINGLE); 
                                }
                                posValue.setBold(false); 

                                targetNameFound = true;
                            }
                            
                            // --- SECTION B: MONTH ---
                            if (text.contains(targetMonthText)) {
                                monthOccurrence++;
                                
                                for (int i = paragraph.getRuns().size() - 1; i >= 0; i--) {
                                    paragraph.removeRun(i);
                                }
                                
                                // Label
                                XWPFRun run = paragraph.createRun();
                                // FIX: Add Tab for 2nd Copy
                                if (monthOccurrence == 2) {
                                    run.addTab();
                                }
                                run.setText("For the month of:  ");
                                run.setFontFamily("Times New Roman");
                                run.setFontSize(11);

                                // Value (Underlined, No Bold, ALL CAPS)
                                XWPFRun monthValue = paragraph.createRun();
                                monthValue.setText(monthYearText.toUpperCase());
                                monthValue.setFontFamily("Times New Roman");
                                monthValue.setFontSize(11);
                                monthValue.setUnderline(UnderlinePatterns.SINGLE); 
                                monthValue.setBold(false); 
                                
                                targetMonthFound = true;
                            }
                        } else if (element instanceof XWPFTable) {
                            populateDTRTable(doc, (XWPFTable) element, employeeAttendance, startDate, employeeId);
                        }
                    }

                    // 4. Save
                    if (targetNameFound && targetMonthFound) {
                        String safeName = employeeName.replace(", ", "_").replace(".", "").replace(" ", "_");
                        String fileName = safeName + "_" + startDate.getMonth().toString() + ".docx";
                        
                        File outputFile = new File(dtrFolder, fileName);
                        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
                            doc.write(fileOutputStream);
                            successCount++;
                        }
                    }
                } catch (Exception ex) {
                    System.err.println("Error processing DTR for " + employeeId);
                    ex.printStackTrace();
                } finally {
                    if (templateStream != null) templateStream.close();
                }
            }

            if (successCount > 0) {
                Modal.inform("Generation Complete", successCount + " DTR(s) generated.", "Location: " + dtrFolder.getAbsolutePath());
                try {
                    if (java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().open(dtrFolder);
                    }
                } catch (Exception ignored) {}
            } else {
                Modal.alert("Generation Failed", "No DTR files were generated.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Modal.alert("Error", "Failed to generate DTR: " + e.getMessage());
        }
    }
    /**
     * Helper to get the active position of an employee directly from DB.
     */
    private String getEmployeePosition(int userId) {
        String query = "SELECT p.position_name FROM assignment a " +
                       "JOIN position p ON a.position_id = p.position_id " +
                       "WHERE a.user_id = ? AND a.status = 1 LIMIT 1";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String pos = rs.getString("position_name");
                return pos != null ? pos : "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
   private void populateDTRTable(XWPFDocument doc, XWPFTable table, ObservableList<Attendance> employeeAttendance, LocalDate startDate, int employeeId) {
        ObservableList<Timeoff> dtrTimeoff = Timeoff.getTimeoffByUserId(employeeId);
        ObservableList<Special_Calendar> allSpecialEvents = Special_Calendar.getSpecialCalendar();
        Map<LocalDate, Special_Calendar> specialEventMap = new HashMap<>();
        for (Special_Calendar event : allSpecialEvents) {
            LocalDate start = event.getStartDate().toLocalDate();
            LocalDate end = event.getEndDate().toLocalDate();
            for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
                specialEventMap.put(date, event);
            }
        }

        Map<LocalDate, Attendance> attendanceMap = new HashMap<>();
        for (Attendance att : employeeAttendance) {
            LocalDate attDate = ((java.sql.Date) att.getDate()).toLocalDate();
            attendanceMap.put(attDate, att);
        }

        YearMonth yearMonth = YearMonth.from(startDate);
        int daysInMonth = yearMonth.lengthOfMonth();
        int rowIndex = 2; 

        for (int day = 1; day <= daysInMonth; day++) {
            if (rowIndex > 32) break;
            LocalDate currentDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), day);
            XWPFTableRow targetRow = table.getRow(rowIndex);
            
            if (targetRow == null) continue; 

            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            boolean isWeekend = (dayOfWeek == java.time.DayOfWeek.SATURDAY || dayOfWeek == java.time.DayOfWeek.SUNDAY);

            Special_Calendar specialEvent = specialEventMap.get(currentDate);
            Timeoff activeLeave = null;
            
            if (specialEvent == null) { 
                for (Timeoff leave : dtrTimeoff) {
                    if (!currentDate.isBefore(DateUtil.sqlDateToLocalDate(leave.getStartDate())) && 
                        !currentDate.isAfter(DateUtil.sqlDateToLocalDate(leave.getEndDate()))) {
                        activeLeave = leave;
                        break;
                    }
                }
            }
            
            Attendance attendance = attendanceMap.get(currentDate);
            boolean isWorkingHoliday = false;
            if (specialEvent != null && "Special Working Holiday".equalsIgnoreCase(specialEvent.getHolidayType())) {
                isWorkingHoliday = true;
            }

            if (activeLeave != null) {
                String type = activeLeave.getType().toUpperCase();
                String schedule = activeLeave.getScheduleType(); 
                if (schedule == null) schedule = "Full Day";

                if ("Full Day".equalsIgnoreCase(schedule)) {
                    mergeCellsHorizontally(table, rowIndex, 1, 6);
                    setCellText(targetRow.getCell(1), type, ParagraphAlignment.LEFT);
                } 
                else if ("AM Only".equalsIgnoreCase(schedule)) {
                    mergeCellsHorizontally(table, rowIndex, 1, 2);
                    setCellText(targetRow.getCell(1), type + " (AM)", ParagraphAlignment.CENTER);
                    
                    setCellText(targetRow.getCell(3), formatTimeForDTR(attendance != null ? attendance.getTimeInPm() : null));
                    setCellText(targetRow.getCell(4), formatTimeForDTR(attendance != null ? attendance.getTimeOutPm() : null));
                    setCellText(targetRow.getCell(5), formatTimeForDTR(attendance != null ? attendance.getOvertimeIn() : null));
                    setCellText(targetRow.getCell(6), formatTimeForDTR(attendance != null ? attendance.getOvertimeOut() : null));
                } 
                else if ("PM Only".equalsIgnoreCase(schedule)) {
                    setCellText(targetRow.getCell(1), formatTimeForDTR(attendance != null ? attendance.getTimeInAm() : null));
                    setCellText(targetRow.getCell(2), formatTimeForDTR(attendance != null ? attendance.getTimeOutAm() : null));
                    
                    mergeCellsHorizontally(table, rowIndex, 3, 4);
                    setCellText(targetRow.getCell(3), type + " (PM)", ParagraphAlignment.CENTER);
                    
                    setCellText(targetRow.getCell(5), ""); 
                    setCellText(targetRow.getCell(6), "");
                }
            } 
            else if (specialEvent != null && !isWorkingHoliday) {
                String scheduleType = specialEvent.getScheduleType();
                String description = specialEvent.getDescription().toUpperCase();

                if ("PM Only (Suspension)".equals(scheduleType)) {
                    setCellText(targetRow.getCell(1), formatTimeForDTR(attendance != null ? attendance.getTimeInAm() : null));
                    setCellText(targetRow.getCell(2), formatTimeForDTR(attendance != null ? attendance.getTimeOutAm() : null));
                    mergeCellsHorizontally(table, rowIndex, 3, 4);
                    setCellText(targetRow.getCell(3), "SUSPENSION"); 
                    setCellText(targetRow.getCell(5), ""); 
                    setCellText(targetRow.getCell(6), "");
                } else if ("AM Only (Suspension)".equals(scheduleType)) {
                    mergeCellsHorizontally(table, rowIndex, 1, 2);
                    setCellText(targetRow.getCell(1), "SUSPENSION"); 
                    setCellText(targetRow.getCell(3), formatTimeForDTR(attendance != null ? attendance.getTimeInPm() : null));
                    setCellText(targetRow.getCell(4), formatTimeForDTR(attendance != null ? attendance.getTimeOutPm() : null));
                    setCellText(targetRow.getCell(5), "");
                    setCellText(targetRow.getCell(6), "");
                } else {
                    mergeCellsHorizontally(table, rowIndex, 1, 6);
                    setCellText(targetRow.getCell(1), description, ParagraphAlignment.LEFT); 
                }

            } 
            else if (attendance != null && hasTimeData(attendance)) {
                setCellText(targetRow.getCell(1), formatTimeForDTR(attendance.getTimeInAm()));
                setCellText(targetRow.getCell(2), formatTimeForDTR(attendance.getTimeOutAm()));
                setCellText(targetRow.getCell(3), formatTimeForDTR(attendance.getTimeInPm()));
                setCellText(targetRow.getCell(4), formatTimeForDTR(attendance.getTimeOutPm()));
                setCellText(targetRow.getCell(5), formatTimeForDTR(attendance.getOvertimeIn()));
                setCellText(targetRow.getCell(6), formatTimeForDTR(attendance.getOvertimeOut()));
            } 
            else if (isWorkingHoliday) {
                mergeCellsHorizontally(table, rowIndex, 1, 6);
                setCellText(targetRow.getCell(1), specialEvent.getDescription().toUpperCase(), ParagraphAlignment.LEFT);
            }
            else if (isWeekend) {
                mergeCellsHorizontally(table, rowIndex, 1, 6);
                setCellText(targetRow.getCell(1), dayOfWeek == java.time.DayOfWeek.SATURDAY ? "SATURDAY" : "SUNDAY", ParagraphAlignment.LEFT); 
            } else {
                setCellText(targetRow.getCell(1), "");
                setCellText(targetRow.getCell(2), "");
                setCellText(targetRow.getCell(3), "");
                setCellText(targetRow.getCell(4), "");
                setCellText(targetRow.getCell(5), "");
                setCellText(targetRow.getCell(6), "");
            }
            
            rowIndex++;
        }
        
         while (rowIndex <= 32) {
            XWPFTableRow targetRow = table.getRow(rowIndex);
            if (targetRow == null) break;
            setCellText(targetRow.getCell(1), "");
            setCellText(targetRow.getCell(2), "");
            setCellText(targetRow.getCell(3), "");
            setCellText(targetRow.getCell(4), "");
            setCellText(targetRow.getCell(5), "");
            setCellText(targetRow.getCell(6), "");
            rowIndex++;
        }
    }
    private void setCellText(XWPFTableCell cell, String text) {
        setCellText(cell, text, ParagraphAlignment.CENTER); // Default to CENTER
    }

    private void setCellText(XWPFTableCell cell, String text, ParagraphAlignment align) {
        if (text == null) text = "";

        CTTcPr tcPr = cell.getCTTc().getTcPr();
        if (tcPr == null) tcPr = cell.getCTTc().addNewTcPr();

        CTVerticalJc vJc = tcPr.getVAlign();
        if (vJc == null) vJc = tcPr.addNewVAlign();
        vJc.setVal(STVerticalJc.CENTER);

        XWPFParagraph p = cell.getParagraphs().get(0);
        if (p == null) p = cell.addParagraph();
        p.setAlignment(align); 

        XWPFRun run;
        if (p.getRuns().isEmpty()) {
            run = p.createRun();
        } else {
            run = p.getRuns().get(0);
        }

        run.setText(text, 0); 
        // --- MODIFIED: Font and Size ---
        run.setFontFamily("Calibri (Body)");
        run.setFontSize(11);
        // --- END MODIFICATION ---

        for (int i = p.getRuns().size() - 1; i > 0; i--) {
            p.removeRun(i);
        }
        
        for (int i = cell.getParagraphs().size() - 1; i > 0; i--) {
            cell.removeParagraph(i);
        }
    }
    
  
   public class EmployeeSummary {
        private final SimpleStringProperty empId;
        private final SimpleStringProperty employeeName;
        private final SimpleIntegerProperty daysPresent;
        private final SimpleIntegerProperty absences;
        private final SimpleIntegerProperty latesCount;
        private final SimpleIntegerProperty latesMins;
        private final SimpleIntegerProperty undertimeMins; 
        private final SimpleDoubleProperty totalOtHrs;
        private final SimpleIntegerProperty bradfordScore;

        // Update constructor to remove latesMins and undertimeMins parameters
        public EmployeeSummary(String empId, String name, int present, int absences, int latesCount, int latesMins, int undertimeMins, double otHrs, int spells) {
            this.empId = new SimpleStringProperty(empId);
            this.employeeName = new SimpleStringProperty(name);
            this.daysPresent = new SimpleIntegerProperty(present);
            this.absences = new SimpleIntegerProperty(absences);
            this.latesCount = new SimpleIntegerProperty(latesCount);
            this.latesMins = new SimpleIntegerProperty(latesMins);       
            this.undertimeMins = new SimpleIntegerProperty(undertimeMins); 
            this.totalOtHrs = new SimpleDoubleProperty(otHrs);
            int score = (spells * spells) * absences;
            this.bradfordScore = new SimpleIntegerProperty(score);
        }

        public String getEmpId() { return empId.get(); }
        public String getEmployeeName() { return employeeName.get(); }
        public int getDaysPresent() { return daysPresent.get(); }
        public int getAbsences() { return absences.get(); }
        public int getLatesCount() { return latesCount.get(); }
        public int getLatesMins() { return latesMins.get(); } 
        public int getUndertimeMins() { return undertimeMins.get(); }
        public double getTotalOtHrs() { return totalOtHrs.get(); }
        public int getBradfordScore() { return bradfordScore.get(); }
    }

    /**
     * Inner class to hold data for the Detailed Daily Log.
     * Must be public with getters for PropertyValueFactory.
     */
   public class DailyLog {
        private final SimpleStringProperty date;
        private final SimpleStringProperty day;
        private final SimpleStringProperty amIn; 
        private final SimpleStringProperty amOut;
        private final SimpleStringProperty pmIn; 
        private final SimpleStringProperty pmOut; 
        private final SimpleStringProperty otIn; 
        private final SimpleStringProperty otOut;
        private final SimpleDoubleProperty hrsWorked;
        private final SimpleStringProperty lateTimeFormatted; 
        private final SimpleDoubleProperty undertimeHours;
        private final SimpleIntegerProperty lateMins;
        private final SimpleDoubleProperty otHrs;   
        private final SimpleStringProperty status;

       public DailyLog(String date, String day, String amIn, String amOut, String pmIn, String pmOut, String otIn, String otOut, double hrs, int lateMins, String lateTimeFormatted, double undertimeHours, double ot, String status) {
            this.date = new SimpleStringProperty(date);
            this.day = new SimpleStringProperty(day);
            this.amIn = new SimpleStringProperty(amIn);
            this.amOut = new SimpleStringProperty(amOut);
            this.pmIn = new SimpleStringProperty(pmIn);
            this.pmOut = new SimpleStringProperty(pmOut);
            this.otIn = new SimpleStringProperty(otIn);
            this.otOut = new SimpleStringProperty(otOut);
            this.hrsWorked = new SimpleDoubleProperty(hrs);
            this.lateMins = new SimpleIntegerProperty(lateMins); 
            this.lateTimeFormatted = new SimpleStringProperty(lateTimeFormatted); 
            this.undertimeHours = new SimpleDoubleProperty(undertimeHours); 
            this.otHrs = new SimpleDoubleProperty(ot);
            this.status = new SimpleStringProperty(status);
        }

      public String getDate() { return date.get(); }
        public String getDay() { return day.get(); }
        public String getAmIn() { return amIn.get(); }
        public String getAmOut() { return amOut.get(); }
        public String getPmIn() { return pmIn.get(); }
        public String getPmOut() { return pmOut.get(); }
        public String getOtIn() { return otIn.get(); }
        public String getOtOut() { return otOut.get(); }
        public double getHrsWorked() { return hrsWorked.get(); }
        public int getLateMins() { return lateMins.get(); } 
        public String getLateTimeFormatted() { return lateTimeFormatted.get(); } 
        public double getUndertimeHours() { return undertimeHours.get(); } 
        public double getOtHrs() { return otHrs.get(); }
        public String getStatus() { return status.get(); }
    }
    
    private ObservableList<Attendance> getAttendanceByDateRange(Integer employeeId, LocalDate startDate, LocalDate endDate) {
        ObservableList<Attendance> attendance = FXCollections.observableArrayList();
        String key = Config.getSecretKey(); // Get Key

        try (Connection connection = DatabaseUtil.getConnection()) {
            StringBuilder query = new StringBuilder(
                "SELECT CONCAT(u.user_lname, ', ', u.user_fname) AS name, " +
                "c.date, " +
                "p.position_name AS position, " +
                "d.department_name AS department, " +
                
                // Decrypt AM IN
                "MAX(CASE WHEN c.time_notation = 'AM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) AS am_in, " + // Key #1
                // Decrypt AM OUT
                "MAX(CASE WHEN c.time_notation = 'AM' THEN CAST(AES_DECRYPT(c.time_out, ?) AS CHAR) END) AS am_out, " + // Key #2
                // Decrypt PM IN
                "MAX(CASE WHEN c.time_notation = 'PM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) AS pm_in, " + // Key #3
                // Decrypt PM OUT
                "MAX(CASE WHEN c.time_notation = 'PM' THEN CAST(AES_DECRYPT(c.time_out, ?) AS CHAR) END) AS pm_out, " + // Key #4
                
                // Decrypt OT IN
                "MAX(CAST(AES_DECRYPT(c.overtime_in, ?) AS CHAR)) AS ot_in, " + // Key #5
                // Decrypt OT OUT
                "MAX(CAST(AES_DECRYPT(c.overtime_out, ?) AS CHAR)) AS ot_out, " + // Key #6
                
                // Status Logic (Must also use decrypted values)
                "CASE " +
                "  WHEN MAX(c.attendance_status) = 3 THEN 'No Out' " +
                "  WHEN MAX(c.attendance_status) = 2 THEN 'Late' " +
                "  WHEN (MAX(CASE WHEN c.time_notation = 'AM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) IS NOT NULL OR " + // Key #7
                "        MAX(CASE WHEN c.time_notation = 'PM' THEN CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) END) IS NOT NULL) THEN 'Present' " + // Key #8
                "  ELSE 'Absent' " +
                "END AS attendance_status " +
        
                "FROM attendance c " +
                "JOIN user u ON c.user_id = u.user_id " +
                "JOIN assignment a ON u.user_id = a.user_id AND a.status = 1 " +
                "JOIN position p ON a.position_id = p.position_id " +
                "JOIN department d ON p.department_id = d.department_id " +
                "WHERE u.user_status = 1 AND u.user_id = ? "
            );

            if (startDate != null && endDate != null) {
                query.append("AND c.date BETWEEN ? AND ? ");
            }

            query.append("GROUP BY u.user_id, c.date, p.position_name, d.department_name ");
            query.append("ORDER BY c.date DESC");

            PreparedStatement statement = connection.prepareStatement(query.toString());
            
            // Set Keys (8 keys total)
            int paramIndex = 1;
            for(int i=0; i<8; i++) {
                statement.setString(paramIndex++, key);
            }

            // Set User ID
            statement.setInt(paramIndex++, employeeId);

            // Set Dates
            if (startDate != null && endDate != null) {
                statement.setDate(paramIndex++, java.sql.Date.valueOf(startDate));
                statement.setDate(paramIndex++, java.sql.Date.valueOf(endDate));
            }

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Attendance a = new Attendance(
                    rs.getString("name"),
                    rs.getDate("date"),
                    rs.getString("am_in"),
                    rs.getString("am_out"),
                    rs.getString("pm_in"),
                    rs.getString("pm_out"),
                    rs.getString("attendance_status")
                );
                a.setOvertimeIn(rs.getString("ot_in"));
                a.setOvertimeOut(rs.getString("ot_out"));
                a.setPosition(rs.getString("position"));
                a.setDeptName(rs.getString("department"));
                attendance.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendance;
    }

    private static void mergeCellsHorizontally(XWPFTable table, int row, int startCol, int endCol) {
        if (table == null || row < 0 || row >= table.getNumberOfRows()) {
            return;
        }
        XWPFTableRow tableRow = table.getRow(row);
        if (tableRow == null) {
            return;
        }
        if (startCol < 0 || endCol < startCol || endCol >= tableRow.getTableCells().size()) {
            return;
        }

        for (int col = startCol; col <= endCol; col++) {
            XWPFTableCell cell = tableRow.getCell(col);
            if (cell == null) continue;
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr == null) tcPr = cell.getCTTc().addNewTcPr();
            CTHMerge hMerge = tcPr.getHMerge();
            if (hMerge == null) hMerge = tcPr.addNewHMerge();

            if (col == startCol) {
                hMerge.setVal(STMerge.RESTART); // Start merge
            } else {
                hMerge.setVal(STMerge.CONTINUE); // Continue merge
            }
        }
    }
    
    private boolean hasTimeData(Attendance attendance) {
        if (attendance == null) return false;
        boolean hasAmIn = attendance.getTimeInAm() != null && !attendance.getTimeInAm().isEmpty() && !attendance.getTimeInAm().equals("--") && !attendance.getTimeInAm().equals("00:00:00");
        boolean hasAmOut = attendance.getTimeOutAm() != null && !attendance.getTimeOutAm().isEmpty() && !attendance.getTimeOutAm().equals("--") && !attendance.getTimeOutAm().equals("00:00:00");
        boolean hasPmIn = attendance.getTimeInPm() != null && !attendance.getTimeInPm().isEmpty() && !attendance.getTimeInPm().equals("--") && !attendance.getTimeInPm().equals("00:00:00");
        boolean hasPmOut = attendance.getTimeOutPm() != null && !attendance.getTimeOutPm().isEmpty() && !attendance.getTimeOutPm().equals("--") && !attendance.getTimeOutPm().equals("00:00:00");
        boolean hasOtIn = attendance.getOvertimeIn() != null && !attendance.getOvertimeIn().isEmpty() && !attendance.getOvertimeIn().equals("--") && !attendance.getOvertimeIn().equals("00:00:00");
        boolean hasOtOut = attendance.getOvertimeOut() != null && !attendance.getOvertimeOut().isEmpty() && !attendance.getOvertimeOut().equals("--") && !attendance.getOvertimeOut().equals("00:00:00");
        return hasAmIn || hasAmOut || hasPmIn || hasPmOut || hasOtIn || hasOtOut;
    }
    
    private String formatTimeForDTR(String time) {
        if (time == null || time.trim().isEmpty() || time.equals("00:00:00") || time.equals("--")) {
            return "";
        }
        try {
            LocalTime localTime = LocalTime.parse(time);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            return localTime.format(formatter);
        } catch (Exception e) {
            return time;
        }
    }
    private ObservableList<Attendance> fetchDailyAttendanceForSummary(Department dept, LocalDate start, LocalDate end) {
        ObservableList<Attendance> dailyAttendance = FXCollections.observableArrayList();
        String key = Config.getSecretKey();

        // FIXED: Cleaned up SQL String concatenation
        String sql = "SELECT u.user_id, CONCAT(u.user_lname, ', ', u.user_fname) AS name, c.date, " +
                     "p.position_name AS position, d.department_name AS department, " +
                     "AES_DECRYPT(u.agency_employee_no, ?) AS agency_employee_no, " + // Key #1
                     
                     // Decrypt Time Columns
                     "CAST(AES_DECRYPT(c.time_in, ?) AS CHAR) AS time_in, " +         // Key #2
                     "CAST(AES_DECRYPT(c.time_out, ?) AS CHAR) AS time_out, " +       // Key #3
                     "c.time_notation, " +
                     "CAST(AES_DECRYPT(c.overtime_in, ?) AS CHAR) AS overtime_in, " + // Key #4
                     "CAST(AES_DECRYPT(c.overtime_out, ?) AS CHAR) AS overtime_out, " + // Key #5
                     
                     "c.overtime_notation, " +
                     "c.attendance_status " +
                     "FROM attendance c " +
                     "JOIN user u ON c.user_id = u.user_id " +
                     "JOIN assignment a ON u.user_id = a.user_id AND a.status = 1 " +
                     "JOIN position p ON a.position_id = p.position_id " +
                     "JOIN department d ON p.department_id = d.department_id " +
                     "WHERE u.user_status = 1 AND c.date BETWEEN ? AND ? ";

        if (dept != null && dept.getId() != 0) {
            sql += "AND d.department_id = ? ";
        }
        sql += "ORDER BY u.user_id, c.date, c.time_notation";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            
            // 1. Set Key for Agency ID
            ps.setString(paramIndex++, key); 
            
            // 2-5. Set Key for Time Columns
            for(int i=0; i<4; i++) {
                ps.setString(paramIndex++, key);
            }

            ps.setDate(paramIndex++, java.sql.Date.valueOf(start));
            ps.setDate(paramIndex++, java.sql.Date.valueOf(end));
            
            if (dept != null && dept.getId() != 0) {
                ps.setInt(paramIndex++, dept.getId());
            }

            ResultSet rs = ps.executeQuery();
            Map<String, Attendance> consolidatedRecords = new HashMap<>();
        
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                java.sql.Date recordDate = rs.getDate("date");
                String name = rs.getString("name");
                String agencyNo = bytesToString(rs.getBytes("agency_employee_no"));
                String position = rs.getString("position");
                String deptName = rs.getString("department");
                
                // Get decrypted values directly
                String timeIn = rs.getString("time_in");
                String timeOut = rs.getString("time_out");
                String notation = rs.getString("time_notation");
                String overtimeIn = rs.getString("overtime_in");
                String overtimeOut = rs.getString("overtime_out");
                String overtimeNotation = rs.getString("overtime_notation");
                
                int currentStatusInt = rs.getInt("attendance_status");

                String initialStatusString;
                if (currentStatusInt == 2) initialStatusString = "Late";
                else if (currentStatusInt == 3) initialStatusString = "No Out";
                else if (timeIn != null || overtimeIn != null) initialStatusString = "Present";
                else initialStatusString = "Absent";

                String recordKey = userId + "-" + recordDate.toString();

                Attendance record = consolidatedRecords.computeIfAbsent(recordKey, k -> new Attendance(
                        name, 
                        recordDate,
                        null, null, null, null,
                        initialStatusString 
                ));
                record.setId(userId);
                record.setAgencyEmployeeNo(agencyNo);
                record.setPosition(position);
                record.setDeptName(deptName);
                
                if ("AM".equals(notation)) {
                    record.setTimeInAm(timeIn);
                    record.setTimeOutAm(timeOut);
                } else if ("PM".equals(notation)) {
                    record.setTimeInPm(timeIn);
                    record.setTimeOutPm(timeOut);
                }
                
                if (overtimeIn != null) record.setOvertimeIn(overtimeIn);
                if (overtimeOut != null) record.setOvertimeOut(overtimeOut);
                if (overtimeNotation != null) record.setOvertimeNotation(overtimeNotation);
                
                if(currentStatusInt == 2) {
                    record.setAttendance_status("Late");
                } else if (currentStatusInt == 3 && !"Late".equals(record.getAttendance_status())){
                    record.setAttendance_status("No Out");
                }    
                if ((record.getTimeInAm() != null || record.getTimeInPm() != null || record.getOvertimeIn() != null)
                    && !"Late".equals(record.getAttendance_status()) && !"No Out".equals(record.getAttendance_status())) {
                     record.setAttendance_status("Present");
                }
            } 
            
            // FIXED: Add all values AFTER the loop finishes
            dailyAttendance.addAll(consolidatedRecords.values());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dailyAttendance;
    }

    private static class AggregatedSummary {
        int scheduledWorkDays = 0;
        int daysPresent = 0;
        int absences = 0;
        int latesCount = 0;
        long totalLateMinutes = 0;
        long totalUndertimeMinutes = 0;
        double totalOtHours = 0.0;
        
        int absenceSpells = 0;     
        boolean isCurrentlyAbsent = false; 
    }

    /**
    * Pre-fetches leave information for all relevant users in the date range.
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

                // Get or create the leave map for this user
                Map<LocalDate, String> leaveMap = userLeaveMaps.computeIfAbsent(userId, k -> new HashMap<>());

                for (LocalDate date = leaveStart; !date.isAfter(leaveEnd); date = date.plusDays(1)) {
                    if (!date.isBefore(reportStartDate) && !date.isAfter(reportEndDate)) {
                        leaveMap.put(date, leaveType);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error pre-fetching user leaves: " + e.getMessage());
            e.printStackTrace();
        }
        return userLeaveMaps;
    }
    private String getFormattedReportHeader() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        
        // 1. Get Department and Employee
        String deptName = "All Departments";
        String empName = "All Employees";
        String empId = "N/A";
        
        if (departmentCombo.getValue() != null && departmentCombo.getValue().getId() != 0) {
             deptName = departmentCombo.getValue().getDepartmentName();
        }
        
        if (employeeCombo.getValue() != null && employeeCombo.getValue().getId() != 0) {
            User user = employeeCombo.getValue();
            empName = user.getFullName();
            empId = user.getAgencyEmployeeNo();
            // If a specific employee is selected, use their department
            if(user.getDepartment() != null) {
                deptName = user.getDepartment();
            }
        }

        // 2. Get Date Range
        String reportPeriod = "N/A";
        if (dateRangePicker.getValue() != null) {
            LocalDate start = dateRangePicker.getValue().getStartDate();
            LocalDate end = dateRangePicker.getValue().getEndDate();
            if(start != null && end != null) {
                 reportPeriod = start.format(dateFormatter) + " - " + end.format(dateFormatter);
            }
        }
        
        // 3. Get Generated On
        String generatedOn = LocalDate.now().format(dateFormatter);

        // 4. Build String
        StringBuilder sb = new StringBuilder();
        sb.append("LGU LAGONOY\n");
        if(isDetailedView) {
            sb.append("Daily Time Record Report\n\n");
            sb.append("Employee ID: ").append(empId).append("\n");
            sb.append("Employee: ").append(empName).append("\n");
        } else {
             sb.append("Employee Attendance Summary Report\n\n");
        }
        sb.append("Department: ").append(deptName).append("\n");
        sb.append("Report Period: ").append(reportPeriod).append("\n");
        sb.append("Generated On: ").append(generatedOn).append("\n");
        
        return sb.toString();
    }
}