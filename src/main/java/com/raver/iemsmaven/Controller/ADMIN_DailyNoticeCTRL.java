package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.Notice;
import com.raver.iemsmaven.Utilities.Modal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.scene.control.Control; 
import javafx.scene.control.TableCell;
import javafx.scene.text.Text;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Model.User;

public class ADMIN_DailyNoticeCTRL implements Initializable {

    @FXML private TableView<Notice> noticesTable;
    @FXML private TableColumn<Notice, Integer> col_id;
    @FXML private TableColumn<Notice, String> col_title;
    @FXML private TableColumn<Notice, String> col_content;

    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> statusFilterChoiceBox;

    @FXML private TextField titleField;
    @FXML private TextArea contentTextArea;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deactivateBtn;
    @FXML private Button clearBtn;

    private Notice selectedNotice = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup Table
        col_id.setCellValueFactory(new PropertyValueFactory<>("notice_id"));
        col_title.setCellValueFactory(new PropertyValueFactory<>("notice_title"));
        col_content.setCellValueFactory(new PropertyValueFactory<>("notice_content"));
        
        // --- Wrap text in content column ---
        // --- Wrap text in content column ---
        col_content.setCellFactory(tc -> {
            TableCell<Notice, String> cell = new TableCell<>() {
                private final Text text = new Text();
                {
                    // Set wrapping width once
                    text.wrappingWidthProperty().bind(col_content.widthProperty());
                    setGraphic(text);
                    setPrefHeight(Control.USE_COMPUTED_SIZE);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        // Set the text content for the current item
                        text.setText(item);
                        setGraphic(text);
                        setText(null); // Ensure text isn't displayed twice
                    }
                }
            };
            return cell;
        });

        // Setup Filters
        statusFilterChoiceBox.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        statusFilterChoiceBox.setValue("Active"); // Default to active

        // Add listeners to filters to reload table
        statusFilterChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> loadNoticesTable());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadNoticesTable());

        // Initial setup
        loadNoticesTable();
        clearFields(null);
    }

    private void loadNoticesTable() {
        String searchTerm = searchField.getText();
        String statusFilter = statusFilterChoiceBox.getValue();
        
        ObservableList<Notice> notices = Notice.getNotices(searchTerm, statusFilter);
        noticesTable.setItems(notices);
        noticesTable.refresh();
    }

    @FXML
    private void noticeSelected(MouseEvent event) {
        selectedNotice = noticesTable.getSelectionModel().getSelectedItem();
        if (selectedNotice != null) {
            titleField.setText(selectedNotice.getNotice_title());
            contentTextArea.setText(selectedNotice.getNotice_content());

            addBtn.setDisable(true);
            updateBtn.setDisable(false);
            deactivateBtn.setDisable(false);

            if (selectedNotice.getStatus() == 1) {
                deactivateBtn.setText("Deactivate");
                deactivateBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            } else {
                deactivateBtn.setText("Activate");
                deactivateBtn.setStyle("-fx-background-color: #00C851; -fx-text-fill: white;");
            }
        }
    }

    @FXML
    private void addNotice(ActionEvent event) {
        String title = titleField.getText();
        String content = contentTextArea.getText();
        
        if (title == null || title.trim().isEmpty()) {
            Modal.showModal("Error", "Notice Title cannot be empty.");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            Modal.showModal("Error", "Notice Content cannot be empty.");
            return;
        }

        try {
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String description = "Added new notice: " + title;
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Add Notice' activity: " + logEx.getMessage());
            }
            Notice.addNotice(title, content);
            Modal.showModal("Success", "New notice added successfully.");
            loadNoticesTable();
            clearFields(null);
        } catch (SQLException e) {
            Modal.showModal("Database Error", "Failed to add notice: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void updateNotice(ActionEvent event) {
        if (selectedNotice == null) {
            Modal.showModal("Error", "No notice selected to update.");
            return;
        }
        
        String title = titleField.getText();
        String content = contentTextArea.getText();

        if (title == null || title.trim().isEmpty()) {
            Modal.showModal("Error", "Notice Title cannot be empty.");
            return;
        }
        if (content == null || content.trim().isEmpty()) {
            Modal.showModal("Error", "Notice Content cannot be empty.");
            return;
        }

        try {
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String description = "Updated notice (ID: " + selectedNotice.getNotice_id() + "): " + title;
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Update Notice' activity: " + logEx.getMessage());
            }
           
            Notice.updateNotice(selectedNotice.getNotice_id(), title, content);
            Modal.showModal("Success", "Notice updated successfully.");
            loadNoticesTable();
            clearFields(null);
        } catch (SQLException e) {
            Modal.showModal("Database Error", "Failed to update notice: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void invertNoticeStatus(ActionEvent event) {
        if (selectedNotice == null) {
            Modal.showModal("Error", "No notice selected to change status.");
            return;
        }

        try {
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                // Check the button text to see what action was just performed
                String action = deactivateBtn.getText().equals("Deactivate") ? "Deactivated" : "Activated";
                String description = action + " notice: " + selectedNotice.getNotice_title();
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Invert Notice Status' activity: " + logEx.getMessage());
            }
            Notice.invertNoticeStatus(selectedNotice.getNotice_id());
            Modal.showModal("Success", "Notice status changed successfully.");
            loadNoticesTable();
            clearFields(null);
        } catch (SQLException e) {
            Modal.showModal("Database Error", "Failed to change notice status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void clearFields(ActionEvent event) {
        selectedNotice = null;
        titleField.clear();
        contentTextArea.clear();
        noticesTable.getSelectionModel().clearSelection();

        addBtn.setDisable(false);
        updateBtn.setDisable(true);
        deactivateBtn.setDisable(true);
        deactivateBtn.setText("Deactivate");
        deactivateBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
    }
}