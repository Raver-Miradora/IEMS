package com.raver.iemsmaven.Controller;

// 1. CHANGE IMPORT
import javafx.scene.control.DatePicker; 
// Remove: import com.dlsc.gemsfx.daterange.DateRangePicker;
// Remove: import com.dlsc.gemsfx.daterange.DateRange;

import com.raver.iemsmaven.Model.Special_Calendar;
import com.raver.iemsmaven.Utilities.DateUtil;
import com.raver.iemsmaven.Utilities.Filter;
import com.raver.iemsmaven.Utilities.Modal;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.time.LocalDate;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Session.Session;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ADMIN_UserCalendarCTRL implements Initializable {

    @FXML private TableColumn<Special_Calendar, String> calTypeCol, descCol, attachmentCol;
    @FXML private TableColumn<Special_Calendar, Date> endDCol, startDCol;
    @FXML private ChoiceBox<String> typeComBox;
    @FXML private ChoiceBox<String> scheduleTypeComBox;
    
    @FXML private Button insertBtn, clearBtn, updateBtn, deactivateBtn;
    @FXML private TableView<Special_Calendar> specialCalTable;
    
    @FXML private ChoiceBox<String> holidayTypeComBox; 
    @FXML private Label holidayTypeLabel;
    @FXML private TextArea descField; //
    @FXML private TextField attachmentField;

    // 2. CHANGE FXML FIELD
    @FXML private DatePicker datePicker; 

    @FXML
    private void handleSelectBtn(MouseEvent event) {
        Special_Calendar selectedItem = specialCalTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            if (selectedItem.getStartDate() != null) {
                LocalDate startDate = DateUtil.sqlDateToLocalDate(selectedItem.getStartDate());
                datePicker.setValue(startDate);
            }

            typeComBox.setValue(selectedItem.getType());
            descField.setText(selectedItem.getDescription());
            attachmentField.setText(selectedItem.getAttachment());
            scheduleTypeComBox.setValue(selectedItem.getScheduleType());
            
            // Set Holiday Type if applicable
            if (selectedItem.getHolidayType() != null) {
                holidayTypeComBox.setValue(selectedItem.getHolidayType());
            } else {
                holidayTypeComBox.setValue("Regular Holiday"); // Default fallback
            }
            
            updateBtn.setDisable(false);
            deactivateBtn.setDisable(false);
        } else {
            specialCalTable.getSelectionModel().clearSelection();
            clear();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> setTable());
        
        ObservableList<String> calendarType = FXCollections.observableArrayList();
        calendarType.addAll("Holiday", "Suspension", "Other");
        typeComBox.setItems(calendarType);
        typeComBox.setValue("Holiday");
        
        ObservableList<String> scheduleTypes = FXCollections.observableArrayList(
            "Full Day", "AM Only (Suspension)", "PM Only (Suspension)"
        );
        scheduleTypeComBox.setItems(scheduleTypes);
        scheduleTypeComBox.setValue("Full Day"); 

        datePicker.setValue(LocalDate.now());
        
        // Initialize Holiday Types
        ObservableList<String> holidayTypes = FXCollections.observableArrayList(
            "Regular Holiday", 
            "Special Non-Working Holiday", 
            "Special Working Holiday"
        );
        if(holidayTypeComBox != null) {
            holidayTypeComBox.setItems(holidayTypes);
            holidayTypeComBox.setValue("Regular Holiday");
        }

        // Add Listener to show/hide Holiday Type based on Type
        typeComBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isHoliday = "Holiday".equals(newVal);
            if (holidayTypeComBox != null) {
                holidayTypeComBox.setVisible(isHoliday);
                holidayTypeComBox.setManaged(isHoliday);
            }
            if (holidayTypeLabel != null) {
                holidayTypeLabel.setVisible(isHoliday);
                holidayTypeLabel.setManaged(isHoliday);
            }
        });
        
        // Trigger initial visibility
        if (holidayTypeComBox != null) {
            holidayTypeComBox.setVisible(true);
            holidayTypeComBox.setManaged(true);
        }
        if (holidayTypeLabel != null) {
            holidayTypeLabel.setVisible(true);
            holidayTypeLabel.setManaged(true);
        }
    }
    
    public void setTable(){
        calTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        attachmentCol.setCellValueFactory(new PropertyValueFactory<>("attachment"));
        startDCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        
        updateBtn.setDisable(true);
        deactivateBtn.setDisable(true);
        specialCalTable.setItems(Special_Calendar.getSpecialCalendar());
    }
    
    public void clearManage(ActionEvent event){
        clear();
    }
    
   public void clear(){
        typeComBox.setValue("Holiday"); 
        descField.setText("");
        attachmentField.setText("");
        descField.setPromptText("Add description . . .");
        attachmentField.setPromptText("Add Link . . .");
        scheduleTypeComBox.setValue("Full Day");
        datePicker.setValue(LocalDate.now());
        
        if (holidayTypeComBox != null) holidayTypeComBox.setValue("Regular Holiday");
        
        updateBtn.setDisable(true);
        deactivateBtn.setDisable(true);
    }
    
    @FXML
    private void updateSpecialCalendar(ActionEvent event) throws SQLException  {
        Special_Calendar selectedItem = specialCalTable.getSelectionModel().getSelectedItem();
        int selectedId = 0;
        if (selectedItem != null) {
            selectedId = selectedItem.getId();
        } else {
             Modal.showModal("Failed", "Please select an event to update.");
             return;
        }

        if (datePicker.getValue() == null) {
            Modal.showModal("Failed", "Please select a date");
            return;
        }

        String type = typeComBox.getValue();
        String description = descField.getText();
        String attachment = attachmentField.getText();
        String scheduleType = scheduleTypeComBox.getValue();
        String holidayType = null;
        
        if ("Holiday".equals(type)) {
            holidayType = holidayTypeComBox.getValue();
        }

        if (scheduleType == null || scheduleType.isEmpty()) {
            Modal.showModal("Failed", "Please select a schedule type");
            return;
        }

        Date startDate = Date.valueOf(datePicker.getValue());
        Date endDate = Date.valueOf(datePicker.getValue());

        boolean actionIsConfirmed = Modal.actionConfirmed("Update", "Do you want to proceed?", "This will update also to all employee records");
        if (actionIsConfirmed) {
            // Pass holidayType to update method
            Special_Calendar.updateSpecialCalendar(selectedId, type, description, attachment, startDate, endDate, scheduleType, holidayType);
            
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String logDescription = "Updated special calendar event (ID: " + selectedId + ") to: " + description;
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), logDescription);
            } catch (Exception logEx) {
                 System.err.println("Failed to log: " + logEx.getMessage());
            }
            setTable();
            clear();
        }
    }
    
    @FXML
    private void addSpecialCalendar(ActionEvent event) {
        if (datePicker.getValue() == null) {
            Modal.showModal("Failed", "Please select a date");
            return;
        }
        
        
        String type = typeComBox.getValue();
        String description = descField.getText();
        String attachment = attachmentField.getText();
        String scheduleType = scheduleTypeComBox.getValue();
        String holidayType = null;
        
        if ("Holiday".equals(type)) {
            holidayType = holidayTypeComBox.getValue();
            if (holidayType == null) {
                Modal.showModal("Failed", "Please select a holiday type");
                return;
            }
        }

        Date startDate = Date.valueOf(datePicker.getValue());
        Date endDate = Date.valueOf(datePicker.getValue());

        if(type == null){
            Modal.showModal("Failed", "Please select type");
            return;
        }
        if (scheduleType == null || scheduleType.isEmpty()) {
            Modal.showModal("Failed", "Please select a schedule type");
            return;
        }
        if(description.isEmpty()){
            Modal.showModal("Failed", "Please add description");
            return;
        }
        if(Special_Calendar.specialCalendarDescriptionExists(description)){
            Modal.showModal("Failed", "Special Calendar already exists \n    Please change description");
            return;
        }

        String specialCalendarName = "";
        boolean isOverlapping = false;

        ObservableList<Special_Calendar> specialCalendarList = Special_Calendar.getSpecialCalendar();
        for(Special_Calendar specialCalendar : specialCalendarList){
            if(Filter.DATE.isOverlapping(startDate.toString(), endDate.toString(), 
                specialCalendar.getStartDate().toString(), specialCalendar.getEndDate().toString())){
                specialCalendarName = specialCalendar.getDescription();
                isOverlapping = true;
                break; 
            }
        }

        if(isOverlapping){
            Modal.showModal("Failed", "Date Overlaps with: '" + specialCalendarName + "'");
        } else {
            if(Modal.actionConfirmed("Confirm Action", "Do you want to proceed?", "This will add also to all employee records")){
                try{
                    // Pass holidayType to add method
                    Special_Calendar.addSpecialCalendar(type, description, attachment, startDate, endDate, scheduleType, holidayType);
                    
                    try {
                        User currentAdmin = Session.getInstance().getLoggedInUser();
                        String logDescription = "Added new special calendar event: " + description + " (" + startDate.toString() + ")";
                        ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), logDescription);
                    } catch (Exception logEx) {
                        System.err.println("Failed to log: " + logEx.getMessage());
                    }
                    setTable();
                    clear();
                } catch(SQLException ex){
                    Modal.showModal("Failed", "Database Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
    
    @FXML
    private void deactivateSpecialCalendar(ActionEvent event) {
        Special_Calendar selectedItem = specialCalTable.getSelectionModel().getSelectedItem();
        int selectedId = 0;
        if (selectedItem != null) {
            selectedId = selectedItem.getId();
        } else {
             Modal.showModal("Failed", "Please select an event to deactivate.");
             return;
        }
        
        boolean actionIsConfirmed = Modal.actionConfirmed("Deactivate", "Do you want to proceed?", "This will deactivate the event for all employees");
        if(actionIsConfirmed){
            Special_Calendar.deactivateSpecialCalendar(selectedId);
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                // Get the description from the item that was selected
                String eventDescription = (selectedItem != null) ? selectedItem.getDescription() : "ID: " + selectedId;
                String logDescription = "Deactivated special calendar event: " + eventDescription;
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), logDescription);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Deactivate Special Calendar' activity: " + logEx.getMessage());
            }
            setTable();
            clear();
        }
    }
}