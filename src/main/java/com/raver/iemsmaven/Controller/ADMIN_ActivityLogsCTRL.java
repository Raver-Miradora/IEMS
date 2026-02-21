package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.ActivityLog;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ADMIN_ActivityLogsCTRL implements Initializable {

    @FXML
    private TableView<ActivityLog> activityLogsTable;
    @FXML
    private TableColumn<ActivityLog, String> col_timestamp;
    @FXML
    private TableColumn<ActivityLog, String> col_activityBy;
    @FXML
    private TableColumn<ActivityLog, String> col_name;
    @FXML
    private TableColumn<ActivityLog, String> col_activity;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set up the cell value factories to bind columns to ActivityLog properties
        col_timestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        col_activityBy.setCellValueFactory(new PropertyValueFactory<>("activityBy"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_activity.setCellValueFactory(new PropertyValueFactory<>("activity"));

        // Load the logs into the table
        loadActivityLogs();
        
    }

    /**
     * Fetches the logs from the ActivityLog model and populates the TableView.
     */
    public void loadActivityLogs() {
        // Get the list of logs from the database
        ObservableList<ActivityLog> logs = ActivityLog.getAllLogs();
        
        // Set the items in the table
        activityLogsTable.setItems(logs);
    }
}
