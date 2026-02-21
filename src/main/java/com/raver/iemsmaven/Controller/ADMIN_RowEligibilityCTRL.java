package com.raver.iemsmaven.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.raver.iemsmaven.Controller.ADMIN_AddEmpCTRL;
public class ADMIN_RowEligibilityCTRL implements Initializable {

    @FXML
    private HBox eligibilityRowHBox;
    @FXML
    private TextField eligibilityNameField;
    @FXML
    private TextField ratingField;
    @FXML
    private DatePicker examDatePicker;
    @FXML
    private TextField examPlaceField;
    @FXML
    private TextField licenseNumberField;
    @FXML
    private DatePicker validityDatePicker;
    @FXML
    private Button removeButton;

    private ADMIN_AddEmpCTRL mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // You can add listeners here if needed
    }

    public void setMainController(ADMIN_AddEmpCTRL mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleRemoveRow(ActionEvent event) {
        Node parent = eligibilityRowHBox.getParent();
        if (parent instanceof VBox) {
            ((VBox) parent).getChildren().remove(eligibilityRowHBox);
            // Tell the main controller to remove this from its list
            if (mainController != null) {
                mainController.removeEligibilityController(this); // You will need to add this method to ADMIN_AddEmpCTRL
            }
        }
    }

    // --- GETTERS (for saving) ---
    public String getEligibilityName() { return eligibilityNameField.getText(); }
    public String getRating() { return ratingField.getText(); }
    public LocalDate getExamDate() { return examDatePicker.getValue(); }
    public String getExamPlace() { return examPlaceField.getText(); }
    public String getLicenseNumber() { return licenseNumberField.getText(); }
    public LocalDate getValidityDate() { return validityDatePicker.getValue(); }

    // --- SETTERS (for populating from PDF) ---
    public void setEligibilityName(String name) { eligibilityNameField.setText(name != null ? name : ""); }
    public void setRating(String rating) { ratingField.setText(rating != null ? rating : ""); }
    public void setExamDate(LocalDate date) { examDatePicker.setValue(date); }
    public void setExamPlace(String place) { examPlaceField.setText(place != null ? place : ""); }
    public void setLicenseNumber(String number) { licenseNumberField.setText(number != null ? number : ""); }
    public void setValidityDate(LocalDate date) { validityDatePicker.setValue(date); }
}
