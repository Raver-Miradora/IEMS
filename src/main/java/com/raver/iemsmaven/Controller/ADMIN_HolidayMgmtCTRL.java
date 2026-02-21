package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.Special_Calendar;
import com.raver.iemsmaven.Utilities.Modal;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.sql.Date;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.layout.HBox;

public class ADMIN_HolidayMgmtCTRL implements Initializable {

    @FXML
    private TableColumn<Special_Calendar, Date> dateCol;
    @FXML
    private TableColumn<Special_Calendar, String> holidayNameCol, descriptionCol, attachmentCol, actionsCol;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Special_Calendar> holidayTable;

    private ObservableList<Special_Calendar> holidayList;
    private FilteredList<Special_Calendar> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the list first
        holidayList = FXCollections.observableArrayList();
        
        Platform.runLater(() -> {
            setTable();
            setupSearchFilter();
        });
    }

    public void setTable() {
        // Set cell value factories
        holidayNameCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        attachmentCol.setCellValueFactory(new PropertyValueFactory<>("attachment"));
        
        // Custom cell factory for date column to show only one date
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        dateCol.setCellFactory(new Callback<TableColumn<Special_Calendar, Date>, TableCell<Special_Calendar, Date>>() {
            @Override
            public TableCell<Special_Calendar, Date> call(TableColumn<Special_Calendar, Date> param) {
                return new TableCell<Special_Calendar, Date>() {
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.toString());
                        }
                    }
                };
            }
        });

        // Custom cell factory for actions column
        actionsCol.setCellFactory(new Callback<TableColumn<Special_Calendar, String>, TableCell<Special_Calendar, String>>() {
            @Override
            public TableCell<Special_Calendar, String> call(TableColumn<Special_Calendar, String> param) {
                return new TableCell<Special_Calendar, String>() {
                    final Button editBtn = new Button("edit");
                    final Button deactivateBtn = new Button("deactivate");
                    final HBox actionBox = new HBox(5, editBtn, deactivateBtn);

                    {
                        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5px 10px;");
                        deactivateBtn.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5px 10px;");
                        
                        editBtn.setOnAction((ActionEvent event) -> {
                            Special_Calendar holiday = getTableView().getItems().get(getIndex());
                            editHoliday(holiday);
                        });
                        
                        deactivateBtn.setOnAction((ActionEvent event) -> {
                            Special_Calendar holiday = getTableView().getItems().get(getIndex());
                            deactivateHoliday(holiday);
                        });
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(actionBox);
                        }
                    }
                };
            }
        });

        // Load holiday data
        loadHolidayData();
    }

    private void loadHolidayData() {
        holidayList.setAll(Special_Calendar.getSpecialCalendar());
    }

    private void setupSearchFilter() {
        // Initialize filteredData with holidayList
        filteredData = new FilteredList<>(holidayList, p -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(holiday -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                if (holiday.getType().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (holiday.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (holiday.getStartDate().toString().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
        
        SortedList<Special_Calendar> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(holidayTable.comparatorProperty());
        holidayTable.setItems(sortedData);
    }

    private void editHoliday(Special_Calendar holiday) {
        // For now, just show a message - you can implement edit functionality later
        Modal.showModal("Edit Holiday", "Edit functionality for: " + holiday.getDescription());
    }

    private void deactivateHoliday(Special_Calendar holiday) {
        boolean actionIsConfirmed = Modal.actionConfirmed("Deactivate", "Do you want to proceed?", 
                "This will deactivate the holiday: " + holiday.getDescription());
        if (actionIsConfirmed) {
            Special_Calendar.deactivateSpecialCalendar(holiday.getId());
            loadHolidayData();
            Modal.showModal("Success", "Holiday deactivated successfully");
        }
    }

    @FXML
    private void handleSelectBtn(MouseEvent event) {
        // Handle row selection if needed
    }
}