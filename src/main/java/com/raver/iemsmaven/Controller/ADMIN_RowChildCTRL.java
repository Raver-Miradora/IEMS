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
import javafx.scene.layout.VBox; // Import VBox
import com.raver.iemsmaven.Controller.ADMIN_AddEmpCTRL;

public class ADMIN_RowChildCTRL implements Initializable {

    @FXML
    private HBox childRowHBox; // The root HBox of the row

    @FXML
    private TextField childNameField;

    @FXML
    private DatePicker childDobPicker;

    @FXML
    private Button removeChildButton;

    // Reference back to the main controller (will be set from ADMIN_AddEmpCTRL)
    private ADMIN_AddEmpCTRL mainController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization if needed (e.g., add listeners to fields)
    }

    // --- Methods to GET data from this row ---

    public String getChildName() {
        return childNameField.getText();
    }

    public LocalDate getBirthDate() {
        return childDobPicker.getValue();
    }

    // --- Methods to SET data into this row (Used by populateForm) ---

    public void setChildName(String name) {
        childNameField.setText(name != null ? name : "");
    }

    public void setBirthDate(LocalDate date) {
        childDobPicker.setValue(date);
    }

    // --- Method to set the main controller reference ---
    public void setMainController(ADMIN_AddEmpCTRL mainController) {
        this.mainController = mainController;
    }

    // --- Action Handler for the Remove Button ---
    @FXML
    private void handleRemoveChild(ActionEvent event) {
        // Find the parent VBox (childrenContainer) and remove this row (childRowHBox)
        Node parent = childRowHBox.getParent();
        if (parent instanceof VBox) {
            ((VBox) parent).getChildren().remove(childRowHBox);

            // Also remove this controller instance from the main controller's list
            if (mainController != null) {
                mainController.removeChildController(this); // Need to add this method in ADMIN_AddEmpCTRL
            }
        } else {
            System.err.println("Could not find parent VBox to remove child row.");
        }
    }
}
