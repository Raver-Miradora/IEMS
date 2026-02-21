package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.Quote;
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
import java.util.Comparator; // <-- added
import java.util.ResourceBundle;
import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Model.User;


public class ADMIN_DailyQuoteCTRL implements Initializable {

    @FXML private TableView<Quote> quotesTable;
    @FXML private TableColumn<Quote, Integer> col_id;
    @FXML private TableColumn<Quote, String> col_quote;

    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> statusFilterChoiceBox;

    @FXML private TextArea quoteTextArea;
    @FXML private Button addBtn;
    @FXML private Button updateBtn;
    @FXML private Button deactivateBtn;
    @FXML private Button clearBtn;

    private Quote selectedQuote = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setup Table
        col_id.setCellValueFactory(new PropertyValueFactory<>("quote_id"));
        col_quote.setCellValueFactory(new PropertyValueFactory<>("quote_content"));

        // Show lowest ID first (ascending)
        col_id.setSortType(TableColumn.SortType.ASCENDING); // changed from DESCENDING
        quotesTable.getSortOrder().setAll(col_id); // ensure this column is the primary sort

        // Setup Filters
        statusFilterChoiceBox.setItems(FXCollections.observableArrayList("All", "Active", "Inactive"));
        statusFilterChoiceBox.setValue("Active"); // Default to active

        // Add listeners to filters to reload table
        statusFilterChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> loadQuotesTable());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadQuotesTable());

        // Initial setup
        loadQuotesTable();
        clearFields(null);
    }

    /**
     * Loads/refreshes the TableView based on filter criteria.
     */
    private void loadQuotesTable() {
        String searchTerm = searchField.getText();
        String statusFilter = statusFilterChoiceBox.getValue();

        ObservableList<Quote> quotes = Quote.getQuotes(searchTerm, statusFilter);

        // Defensive client-side sort: ensure lowest id is first.
        if (quotes != null && !quotes.isEmpty()) {
            // Use comparingInt in case getQuote_id() returns primitive int
            quotes.sort(Comparator.comparingInt(Quote::getQuote_id));
        }

        quotesTable.setItems(quotes);

        // Re-apply the column sort as the primary sort and run the TableView sort
        quotesTable.getSortOrder().setAll(col_id);
        quotesTable.sort();
    }

    /**
     * Handles populating the form when a row is clicked in the table.
     */
    @FXML
    private void quoteSelected(MouseEvent event) {
        selectedQuote = quotesTable.getSelectionModel().getSelectedItem();
        if (selectedQuote != null) {
            quoteTextArea.setText(selectedQuote.getQuote_content());

            // Update button states
            addBtn.setDisable(true);
            updateBtn.setDisable(false);
            deactivateBtn.setDisable(false);

            // Update deactivate button text based on status
            if (selectedQuote.getStatus() == 1) {
                deactivateBtn.setText("Deactivate");
                deactivateBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
            } else {
                deactivateBtn.setText("Activate");
                deactivateBtn.setStyle("-fx-background-color: #00C851; -fx-text-fill: white;");
            }
        }
    }

    /**
     * Handles adding a new quote.
     */
    @FXML
    private void addQuote(ActionEvent event) {
        String content = quoteTextArea.getText();
        if (content == null || content.trim().isEmpty()) {
            Modal.showModal("Error", "Quote content cannot be empty.");
            return;
        }

        try {
            Quote.addQuote(content);
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String truncatedContent = (content.length() > 50) ? content.substring(0, 50) + "..." : content;
                String description = "Added new quote: \"" + truncatedContent + "\"";
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Add Quote' activity: " + logEx.getMessage());
            }
            Modal.showModal("Success", "New quote added successfully.");
            loadQuotesTable();
            clearFields(null);
        } catch (SQLException e) {
            Modal.showModal("Database Error", "Failed to add quote: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles updating the selected quote.
     */
    @FXML
    private void updateQuote(ActionEvent event) {
        if (selectedQuote == null) {
            Modal.showModal("Error", "No quote selected to update.");
            return;
        }

        String content = quoteTextArea.getText();
        if (content == null || content.trim().isEmpty()) {
            Modal.showModal("Error", "Quote content cannot be empty.");
            return;
        }

        try {
            Quote.updateQuote(selectedQuote.getQuote_id(), content);
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String description = "Updated quote (ID: " + selectedQuote.getQuote_id() + ")";
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Update Quote' activity: " + logEx.getMessage());
            }
            Modal.showModal("Success", "Quote updated successfully.");
            loadQuotesTable();
            clearFields(null);
        } catch (SQLException e) {
            Modal.showModal("Database Error", "Failed to update quote: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles activating or deactivating the selected quote.
     */
    @FXML
    private void invertQuoteStatus(ActionEvent event) {
        if (selectedQuote == null) {
            Modal.showModal("Error", "No quote selected to change status.");
            return;
        }

        try {
            Quote.invertQuoteStatus(selectedQuote.getQuote_id());
            try {
                User currentAdmin = Session.getInstance().getLoggedInUser();
                String action = deactivateBtn.getText().equals("Deactivate") ? "Deactivated" : "Activated";
                String description = action + " quote (ID: " + selectedQuote.getQuote_id() + ")";
                ActivityLog.logActivity(currentAdmin.getPrivilege(), currentAdmin.getFname() + " " + currentAdmin.getLname(), description);
            } catch (Exception logEx) {
                System.err.println("Failed to log 'Invert Quote Status' activity: " + logEx.getMessage());
            }
            Modal.showModal("Success", "Quote status changed successfully.");
            loadQuotesTable();
            clearFields(null);
        } catch (SQLException e) {
            Modal.showModal("Database Error", "Failed to change quote status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clears the form fields and resets button states.
     */
    @FXML
    private void clearFields(ActionEvent event) {
        selectedQuote = null;
        quoteTextArea.clear();
        quotesTable.getSelectionModel().clearSelection();

        addBtn.setDisable(false);
        updateBtn.setDisable(true);
        deactivateBtn.setDisable(true);
        deactivateBtn.setText("Deactivate");
        deactivateBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
    }
}
