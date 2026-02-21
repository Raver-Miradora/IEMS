/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.ActivityLog;
import com.raver.iemsmaven.Model.Attendance;
import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Utilities.ImageUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart; 
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Orientation;
import javafx.stage.Stage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ADMIN_DashboardCTRL implements Initializable {
    
    @FXML private TableColumn<Attendance, String> col_emp_id;
    @FXML private BarChart<String, Integer> barChart;
    @FXML private CategoryAxis categoryAxis;
    @FXML private NumberAxis numberAxis;
    
    // NEW: Department Ranking Chart
    @FXML private BarChart<String, Number> departmentRankingChart;
    @FXML private CategoryAxis deptCategoryAxis;
    @FXML private NumberAxis deptNumberAxis;

    @FXML private Label insightLabel; 
    @FXML private Label quoteLabel; // Reused for Milestones
    @FXML private PieChart pieChart;
    @FXML private Pane piePane;
    @FXML private TableView<Attendance> recentAttendanceTable;
    @FXML private TableColumn<Attendance, String> col_name;
    @FXML private TableColumn<Attendance, String> col_am_in;
    @FXML private TableColumn<Attendance, String> col_am_out;
    @FXML private TableColumn<Attendance, String> col_pm_in;
    @FXML private TableColumn<Attendance, String> col_pm_out;
    @FXML private TableColumn<Attendance, String> col_ot_in;
    @FXML private TableColumn<Attendance, String> col_ot_out;
    @FXML private TableColumn<Attendance, String> col_pos;
    @FXML private Label attendancePercentLabel; 
    @FXML private ImageView userImageView;
    @FXML private Button adminMenuButton;
    @FXML private Label totalEmployeesLabel;
    @FXML private Label presentTodayLabel;
    @FXML private Label totalAbsentLabel;
    @FXML private Label onLeaveTodayLabel;
    @FXML private VBox activityLogVBox;
    @FXML private Label earlyBirdName;
    @FXML private Label earlyBirdTime;
    @FXML private Label ironManName;
    @FXML private Label ironManStreak;
    @FXML private Label dynamoName;
    @FXML private Label dynamoStreak;
    private int milestoneIndex = 0;
    @FXML private ImageView earlyBirdImage;
    @FXML private ImageView ironManImage;
    @FXML private ImageView dynamoImage;

    @FXML
    private void handleAdminMenu(ActionEvent event) {
        try {
            ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
            if (containerCtrl != null) {
                containerCtrl.loadProfileUpdate();
            } else {
                Stage stage = (Stage) adminMenuButton.getScene().getWindow();
                Scene scene = stage.getScene();
                Parent root = scene.getRoot();
                Pane contentPane = (Pane) root.lookup("#contentPane");
                if (contentPane != null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ADMIN_ProfileUpdate.fxml"));
                    Parent profileView = loader.load();
                    contentPane.getChildren().setAll(profileView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load profile update: " + e.getMessage());
        }
    }
    private void loadTopPerformers() {
        new Thread(() -> {
            // ------------------------------------------------
            // 1. FETCH EARLY BIRD (Yellow)
            // ------------------------------------------------
            Attendance earlyBirdObj = Attendance.getEarlyBirdForMonth();
            String finalBirdName;
            String finalBirdTime;
            byte[] birdImgData = null; // Variable for image

            if (earlyBirdObj.getId() != 0) {
                finalBirdName = earlyBirdObj.getName();
                finalBirdTime = Attendance.getAverageEarlyTime(earlyBirdObj.getId()) + " (Avg)";
                // Fetch Image
                birdImgData = User.getUserImageByUserId(earlyBirdObj.getId());
            } else {
                finalBirdName = "No Data";
                finalBirdTime = "--:--";
            }

            // ------------------------------------------------
            // 2. FETCH IRON MAN (Green)
            // ------------------------------------------------
            Attendance ironManObj = Attendance.getPerfectAttendanceForMonth();
            String finalIronName = ironManObj.getName();
            String finalIronStreak = (ironManObj.getNotation() != null) ? ironManObj.getNotation() : "0 Days Streak";
            byte[] ironImgData = (ironManObj.getId() != 0) ? User.getUserImageByUserId(ironManObj.getId()) : null;

            // ------------------------------------------------
            // 3. FETCH THE DYNAMO (Blue)
            // ------------------------------------------------
            Attendance dynamoObj = Attendance.getMostHoursForMonth();
            String finalDynamoName = dynamoObj.getName();
            String finalDynamoStreak = (dynamoObj.getNotation() != null) ? dynamoObj.getNotation() : "0 Hours Logged";
            byte[] dynamoImgData = (dynamoObj.getId() != 0) ? User.getUserImageByUserId(dynamoObj.getId()) : null;

            // Need final variables for lambda
            byte[] finalBirdImg = birdImgData;
            byte[] finalIronImg = ironImgData;
            byte[] finalDynamoImg = dynamoImgData;

            // ------------------------------------------------
            // UPDATE UI
            // ------------------------------------------------
            Platform.runLater(() -> {
                if (earlyBirdName != null) {
                    // Update Text
                    earlyBirdName.setText(finalBirdName);
                    earlyBirdTime.setText(finalBirdTime);
                    ironManName.setText(finalIronName);
                    ironManStreak.setText(finalIronStreak);
                    if (dynamoName != null) { 
                        dynamoName.setText(finalDynamoName);
                        dynamoStreak.setText(finalDynamoStreak);
                    }

                    // --- UPDATE IMAGES ---
                    if (earlyBirdImage != null && finalBirdImg != null) {
                        earlyBirdImage.setImage(ImageUtil.byteArrayToImage(finalBirdImg));
                    }
                    if (ironManImage != null && finalIronImg != null) {
                        ironManImage.setImage(ImageUtil.byteArrayToImage(finalIronImg));
                    }
                    if (dynamoImage != null && finalDynamoImg != null) {
                        dynamoImage.setImage(ImageUtil.byteArrayToImage(finalDynamoImg));
                    }
                }
            });
        }).start();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (Session.getInstance().getLoggedInUser() != null) {
            userImageView.setImage(ImageUtil.byteArrayToImage(Session.getInstance().getLoggedInUser().getImage()));
        }
        loadTopPerformers();
        // RECENT ATTENDANCE TABLE
        col_emp_id.setCellValueFactory(new PropertyValueFactory<>("agencyEmployeeNo"));
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_am_in.setCellValueFactory(new PropertyValueFactory<>("timeInAm"));
        col_am_out.setCellValueFactory(new PropertyValueFactory<>("timeOutAm"));
        col_pm_in.setCellValueFactory(new PropertyValueFactory<>("timeInPm"));
        col_pm_out.setCellValueFactory(new PropertyValueFactory<>("timeOutPm"));
        col_ot_in.setCellValueFactory(new PropertyValueFactory<>("overtimeIn"));
        col_ot_out.setCellValueFactory(new PropertyValueFactory<>("overtimeOut"));
        col_pos.setCellValueFactory(new PropertyValueFactory<>("position"));

        setTimeCellFactory(col_am_in);
        setTimeCellFactory(col_am_out);
        setTimeCellFactory(col_pm_in);
        setTimeCellFactory(col_pm_out);
        setTimeCellFactory(col_ot_in);
        setTimeCellFactory(col_ot_out);
        
        recentAttendanceTable.setStyle("-fx-font-size: 16px;");
        
        // Prevent focus stealing
        recentAttendanceTable.setFocusTraversable(false);
        barChart.setFocusTraversable(false);
        pieChart.setFocusTraversable(false);
        if(departmentRankingChart != null) departmentRankingChart.setFocusTraversable(false);

        loadMetrics();
        loadRecentAttendanceTable();
        loadActivityLogs();
        
        loadMilestonesWidget();
        populateTardinessChart(); 
        populateDepartmentRankingChart(); // Load the new chart
        
        scheduleChartRefresh();
        
        // Chart Styling
        barChart.setCategoryGap(20.0);
        barChart.setBarGap(5.0);
        barChart.setAnimated(false);
        
        if (activityLogVBox != null) activityLogVBox.setFocusTraversable(false);
        if (pieChart != null) pieChart.setFocusTraversable(false);
        if (piePane != null) piePane.setFocusTraversable(false);
        if (recentAttendanceTable != null) recentAttendanceTable.setFocusTraversable(false);
        if (departmentRankingChart != null) departmentRankingChart.setFocusTraversable(false);
        
        Platform.runLater(() -> {
            try {
                // get scene root (safe because Platform.runLater runs after scene is attached)
                if (adminMenuButton != null && adminMenuButton.getScene() != null) {
                    javafx.scene.Node root = adminMenuButton.getScene().getRoot();
                    if (root instanceof javafx.scene.layout.Pane) {
                        javafx.scene.layout.Pane rootPane = (javafx.scene.layout.Pane) root;

                        // create invisible focus holder
                        javafx.scene.layout.Pane focusHolder = new javafx.scene.layout.Pane();
                        focusHolder.setId("focusHolder");
                        focusHolder.setPrefSize(1, 1);
                        focusHolder.setOpacity(0);           // invisible
                        focusHolder.setMouseTransparent(true); // ignores mouse so it doesn't block interactions
                        focusHolder.setFocusTraversable(true);

                        // add and request focus
                        rootPane.getChildren().add(focusHolder);
                        focusHolder.requestFocus();
                    }
                }
            } catch (Exception ex) {
                // don't block dashboard if something goes wrong, but log for debugging
                ex.printStackTrace();
            }
        });
    }

   private void loadMilestonesWidget() {
        // SAFETY CHECK: If the label is missing from FXML, stop here so we don't crash
        if (quoteLabel == null) {
            System.err.println("Warning: quoteLabel is null. Milestone widget skipped.");
            return;
        }

        new Thread(() -> {
            ObservableList<String> milestones = User.getUpcomingMilestones();
            
            Platform.runLater(() -> {
                // DOUBLE CHECK inside the RunLater just to be safe
                if (quoteLabel == null) return; 

                if (milestones.isEmpty()) {
                    quoteLabel.setText("No upcoming milestones.");
                } else {
                    // Create a timeline to cycle through milestones every 5 seconds
                    Timeline milestoneTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
                        // Ensure label still exists before setting text
                        if (quoteLabel != null) {
                            quoteLabel.setText(milestones.get(milestoneIndex));
                            milestoneIndex = (milestoneIndex + 1) % milestones.size();
                        }
                    }));
                    milestoneTimeline.setCycleCount(Timeline.INDEFINITE);
                    milestoneTimeline.play();
                    
                    // Set immediate first text
                    quoteLabel.setText(milestones.get(0));
                    // Optional styling
                    quoteLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
                }
            });
        }).start();
    }

    private void loadActivityLogs() {
        if (activityLogVBox == null) return;
        activityLogVBox.getChildren().clear();
        ObservableList<ActivityLog> logs = ActivityLog.getAllLogs();
        int logCount = 0;
        for (ActivityLog log : logs) {
            if (logCount >= 20) break;
            String logText = String.format("%s (%s) %s", log.getActivityBy(), log.getName(), log.getActivity());
            Text textNode = new Text(logText);
            textNode.setWrappingWidth(370);
            // Ensure text color is set explicitly
            textNode.setStyle("-fx-font-size: 16px; -fx-fill: #333333;"); 
            Separator separator = new Separator(Orientation.HORIZONTAL);
            activityLogVBox.getChildren().addAll(textNode, separator);
            logCount++;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setTimeCellFactory(TableColumn<Attendance, String> column) {
        column.setCellFactory(col -> new TableCell<Attendance, String>() {
            @Override
            protected void updateItem(String time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null || time.trim().isEmpty() || time.equals("00:00:00")) {
                    setText("");
                } else {
                    try {
                        LocalTime localTime = LocalTime.parse(time);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
                        setText(localTime.format(formatter));
                    } catch (Exception e) {
                        setText(time);
                    }
                }
            }
        });
    }

    private void populateTardinessChart() {
        new Thread(() -> {
            Map<String, Integer> tardinessData = Attendance.getLatesByDayOfWeek();

            Platform.runLater(() -> {
                barChart.getData().clear();
                XYChart.Series<String, Integer> series = new XYChart.Series<>();
                series.setName("Tardiness Frequency");

                int maxCount = 0;
                int totalLates = 0;
                String maxDay = "";

                for (Map.Entry<String, Integer> entry : tardinessData.entrySet()) {
                    int count = entry.getValue();
                    String day = entry.getKey();
                    
                    totalLates += count;
                    
                    if (count > maxCount) {
                        maxCount = count;
                        maxDay = day;
                    }

                    XYChart.Data<String, Integer> data = new XYChart.Data<>(day, count);
                    
                    data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                        if (newNode != null) {
                            if (data.getYValue().intValue() > 0 && data.getYValue().intValue() == tardinessData.values().stream().max(Integer::compare).orElse(0)) {
                                newNode.setStyle("-fx-bar-fill: #e74c3c;"); 
                            } else {
                                newNode.setStyle("-fx-bar-fill: #95a5a6;");
                            }
                        }
                    });

                    series.getData().add(data);
                }

                barChart.getData().add(series);
                barChart.setLegendVisible(false);
                categoryAxis.setLabel("Day of Week");

                if (totalLates > 0) {
                    double percentage = ((double) maxCount / totalLates) * 100;
                    String insightText = String.format("Insight: Employees are most frequently late on %ss (%.0f%% of all occurrences).", maxDay, percentage);
                    insightLabel.setText(insightText);
                    insightLabel.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold; -fx-font-style: italic; -fx-background-color: #fadbd8; -fx-padding: 5; -fx-background-radius: 5;");
                } else {
                    insightLabel.setText("Insight: No tardiness data recorded yet.");
                    insightLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            });
        }).start();
    }
    
    private void populateDepartmentRankingChart() {
        new Thread(() -> {
            Map<String, Integer> totalByDept = Attendance.getTotalEmployeesByDepartment();
            Map<String, Integer> presentByDept = Attendance.getDepartmentAttendanceToday(); 

            Platform.runLater(() -> {
                if(departmentRankingChart == null) return;
                departmentRankingChart.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Attendance Rate (%)");

                for (Map.Entry<String, Integer> entry : totalByDept.entrySet()) {
                    String deptName = entry.getKey();
                    int total = entry.getValue();
                    int present = presentByDept.getOrDefault(deptName, 0);
                    
                    // FIXED: Only show department if someone is present
                    if (present > 0) {
                        double percentage = (total > 0) ? ((double) present / total) * 100 : 0;
                        XYChart.Data<String, Number> data = new XYChart.Data<>(deptName, percentage);
                        
                        // Add Color Styling Listener
                        data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                            if (newNode != null) {
                                if (percentage >= 90) newNode.setStyle("-fx-bar-fill: #2ecc71;");
                                else if (percentage >= 50) newNode.setStyle("-fx-bar-fill: #f39c12;");
                                else newNode.setStyle("-fx-bar-fill: #e74c3c;");
                            }
                        });
                        series.getData().add(data);
                    }
                }
                departmentRankingChart.getData().add(series);
            });
        }).start();
    }

    private void loadRecentAttendanceTable(){
        ObservableList<Attendance> attendanceList = Attendance.getRecentAttendanceWithPosition();
        recentAttendanceTable.setItems(attendanceList);
    }

    private void loadMetrics(){
        new Thread(() -> {
            int totalEmployees = Attendance.getTotalActiveEmployeesCount();
            int presentToday = Attendance.getPresentTodayCount();
            int totalAbsent = Attendance.getAbsentTodayCount();
            int onLeaveToday = Attendance.getOnLeaveTodayCount();

            Platform.runLater(() -> {
                totalEmployeesLabel.setText(String.valueOf(totalEmployees));
                presentTodayLabel.setText(String.valueOf(presentToday));
                totalAbsentLabel.setText(String.valueOf(totalAbsent));
                onLeaveTodayLabel.setText(String.valueOf(onLeaveToday));
            });
        }).start();
    }

    private void scheduleChartRefresh() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(this::populatePunctualityChart, 0, 30, TimeUnit.SECONDS);
    }

    private void populatePunctualityChart() {
        new Thread(() -> {
            Map<String, Integer> stats = Attendance.getPunctualityStats();
            int late = stats.getOrDefault("Late", 0);
            int onTime = stats.getOrDefault("On Time", 0);

            int totalEmployees = Attendance.getTotalEmployees();
            int onLeave = Attendance.getOnLeaveTodayCount();
            int absent = Math.max(0, totalEmployees - (late + onTime + onLeave));
            
            final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            // Appending counts to the Name string so they show up in Legend/Labels
            if (onTime > 0) {
                pieChartData.add(new PieChart.Data("On Time: " + onTime, onTime));
            }
            if (late > 0) {
                pieChartData.add(new PieChart.Data("Late: " + late, late));
            }
            if (absent > 0) {
                pieChartData.add(new PieChart.Data("Absent: " + absent, absent));
            }

            Platform.runLater(() -> {
                pieChart.setData(pieChartData);
                attendancePercentLabel.setText(String.valueOf(late)); 
            });
        }).start();
    }
    @FXML
    private void handleBackgroundClick() {
        // Whenever the user clicks the background, force focus to the invisible node
        // This prevents the ScrollPane or Charts from staying "active"
        if (adminMenuButton.getScene() != null) {
            javafx.scene.Node focusHolder = adminMenuButton.getScene().lookup("#focusHolder");
            if (focusHolder != null) {
                focusHolder.requestFocus();
            }
        }
    }
}