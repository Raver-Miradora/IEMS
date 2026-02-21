package com.raver.iemsmaven.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import com.raver.iemsmaven.Controller.ADMIN_AddEmpCTRL;

public class ADMIN_RowWorkExpCTRL implements Initializable {

    @FXML
    private VBox workRowVBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField positionField;
    @FXML
    private TextField companyField;
    @FXML
    private TextField salaryField;
    @FXML
    private TextField salaryGradeField;
    @FXML
    private TextField statusField;
    @FXML
    private CheckBox govtServiceCheckbox;
    @FXML
    private Button removeButton;

    private ADMIN_AddEmpCTRL mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Add a listener to ensure salary field only accepts numbers
        salaryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                salaryField.setText(oldValue);
            }
        });
    }

    public void setMainController(ADMIN_AddEmpCTRL mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleRemoveRow(ActionEvent event) {
        Node parent = workRowVBox.getParent();
        if (parent instanceof VBox) {
            ((VBox) parent).getChildren().remove(workRowVBox);
            // Tell the main controller to remove this from its list
            if (mainController != null) {
                mainController.removeWorkExperienceController(this); // You will need to add this method
            }
        }
    }

    // --- GETTERS (for saving) ---
    public LocalDate getStartDate() { return startDatePicker.getValue(); }
    public LocalDate getEndDate() { return endDatePicker.getValue(); }
    public String getPositionTitle() { return positionField.getText(); }
    public String getCompany() { return companyField.getText(); }
    public String getSalaryGrade() { return salaryGradeField.getText(); }
    public String getAppointmentStatus() { return statusField.getText(); }
    public boolean isGovernmentService() { return govtServiceCheckbox.isSelected(); }
    public Double getMonthlySalary() {
        try {
            return Double.parseDouble(salaryField.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // --- SETTERS (for populating from PDF) ---
    public void setStartDate(LocalDate date) { startDatePicker.setValue(date); }
    public void setEndDate(LocalDate date) { endDatePicker.setValue(date); }
    public void setPositionTitle(String title) { positionField.setText(title != null ? title : ""); }
    public void setCompany(String company) { companyField.setText(company != null ? company : ""); }
    public void setSalaryGrade(String grade) { salaryGradeField.setText(grade != null ? grade : ""); }
    public void setAppointmentStatus(String status) { statusField.setText(status != null ? status : ""); }
    public void setGovernmentService(boolean isGovt) { govtServiceCheckbox.setSelected(isGovt); }
    public void setMonthlySalary(Double salary) {
        if (salary != null) {
            salaryField.setText(String.format("%.2f", salary)); // Format to 2 decimal places
        } else {
            salaryField.clear();
        }
    }
}