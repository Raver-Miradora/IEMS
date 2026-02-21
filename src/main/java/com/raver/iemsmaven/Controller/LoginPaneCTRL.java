package com.raver.iemsmaven.Controller;
import com.raver.iemsmaven.Model.User; 
import com.raver.iemsmaven.Utilities.ImageUtil; 
import com.raver.iemsmaven.Utilities.StringUtil; 
import com.raver.iemsmaven.Fingerprint.IdentificationThread;
import com.raver.iemsmaven.Fingerprint.Selection;
import com.raver.iemsmaven.Fingerprint.ThreadFlags;
import com.raver.iemsmaven.Model.Assignment;
import com.raver.iemsmaven.Model.Attendance;
import com.raver.iemsmaven.Model.AttendanceAction;
import com.raver.iemsmaven.Utilities.PaneUtil;
import com.raver.iemsmaven.Utilities.SoundUtil;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import java.util.function.Consumer;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.raver.iemsmaven.Model.Notice;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox; 
import javafx.scene.paint.Color; 
import javafx.scene.text.Font; 
import javafx.scene.text.FontWeight; 
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author admin
 */

public class LoginPaneCTRL implements Initializable {

    @FXML 
    private Label PID;
    @FXML
    private AnchorPane mainAnchorPane; 
    private ExecutorService executor;
    private Timeline currentDisplayTimeline;
    
    @FXML
    private Button adminLoginBtn;
    @FXML private Pane msgPane;
    @FXML
    private ImageView fpImageview;
    @FXML
    private Label titleLabel;
    
    PaneUtil paneUtil = new PaneUtil();
    @FXML
    private Label dateLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label notationLabel;
    @FXML
    private Label scannerStatusLabel;
    @FXML
    private Label scannerStatusSubtextLabel;
   
    IdentificationThread identification;
    @FXML
    private TableView<Attendance> recentAttendanceTable;
    @FXML
    private TableColumn<Attendance, String> col_name;
    @FXML
    private TableColumn<Attendance, String> col_am_in;
    @FXML
    private TableColumn<Attendance, String> col_am_out;
    @FXML
    private TableColumn<Attendance, String> col_pm_in;
    @FXML
    private TableColumn<Attendance, String> col_pm_out;
    @FXML
    private TableColumn<Attendance, String> col_ot_in;
    @FXML
    private TableColumn<Attendance, String> col_ot_out;
    @FXML private ImageView UIV;
    @FXML private Label ATL;
    @FXML private Label TL;
    @FXML private Label SL;
    @FXML private Label NML;
    @FXML private Label EID;
    @FXML private ImageView BSIMG;
    @FXML private Label SystemMessage1;
    @FXML private Label SystemMessage2;
    private ImageView dummyImageView = new ImageView();
    @FXML
    private VBox announcementsVBox;
    
    private boolean isDisplayingResult = false;

    String fpAnimationImagePath = "/Images/fp_scan_animation.gif";
    String fpImagePath = "/Images/fp_placeholder.jpg";
    Image fpImage = new Image(fpImagePath);
    
    private void stopCurrentTimeline() {
        if (currentDisplayTimeline != null) {
            currentDisplayTimeline.stop();
            currentDisplayTimeline = null;
        }
    }

    public void setUserImage(Image image) {
        Platform.runLater(() -> UIV.setImage(image));
    }

    public void setAttendanceType(String text) {
        Platform.runLater(() -> ATL.setText(text));
    }

    public void setTime(String text) {
        Platform.runLater(() -> TL.setText(text));
    }

    public void setSurname(String text) {
        Platform.runLater(() -> SL.setText(text));
    }

    public void setNameMname(String text) {
        Platform.runLater(() -> NML.setText(text));
    }

    public void setBiometricSuccessImage(Image image) {
        Platform.runLater(() -> BSIMG.setImage(image));
    }
    public void setSystemMessage1(String text) {
        Platform.runLater(() -> SystemMessage1.setText(text));
    }
    public void setEmployeeID(String text) {
        Platform.runLater(() -> EID.setText(text));
    }

    public void setSystemMessage2(String text) {
        Platform.runLater(() -> SystemMessage2.setText(text));
    }
    public void setPosition(String text) {
        Platform.runLater(() -> PID.setText(text));
    }
    
    private void timeInOTUser(User user) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Attendance.timeInOT(user.getId(), time, "OT");
        SoundUtil.playTimeInSound();
        handleIdentificationSuccess(user, "OT Time In", LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        loadRecentAttendanceTable(); // Refresh the table
    }

    private void timeOutOTUser(User user) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Attendance.timeOutOT(user.getId(), time);
        SoundUtil.playTimeOutSound();
        handleIdentificationSuccess(user, "OT Time Out", LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        loadRecentAttendanceTable(); // Refresh the table
    }
    private void handleIdentificationDenial(User user, String message1, String message2) {
        isDisplayingResult = true;
        final User fullUser = User.getUserByUserId(user.getId());

        Platform.runLater(() -> {
            setUserImage(ImageUtil.byteArrayToImage(fullUser.getImage()));
            setSurname(fullUser.getLname() + ",");

            String middleInitial = "";
            if (fullUser.getMname() != null && !fullUser.getMname().isEmpty()) {
                middleInitial = fullUser.getMname().charAt(0) + ".";
            }
            setNameMname(fullUser.getFname() + " " + middleInitial);

            setAttendanceType("_______");
            setTime("--:-- --");

            String agencyNo = fullUser.getAgencyEmployeeNo(); 
            if (agencyNo != null && !agencyNo.isBlank()) {
                setEmployeeID(agencyNo);
            } else {
                setEmployeeID("ID: " + fullUser.getId());
            }

            String position = getCurrentPosition(fullUser.getId());
            setPosition(position);

            // Set the error messages
            setSystemMessage1(message1);
            setSystemMessage2(message2);

            // Use the Fail/Deny icon
            setBiometricSuccessImage(new Image(getClass().getResourceAsStream("/Images/fp_id_fail_icon.png")));

            // Call the specific clearer for denial
            startNewTimeline(this::clearIdentificationDisplay, 10.0);
        });
    }
    public void handleIdentificationSuccess(User user, String attendanceType, String time) {
    isDisplayingResult = true;
    
    // 1. Do NOT call resetFingerprintView() here. Let the raw print stay visible.

    final User fullUser = User.getUserByUserId(user.getId());
    
    Platform.runLater(() -> {
        // --- UI UPDATES ON JAVA FX THREAD ---
        
        // 1. Update User Image
        setUserImage(ImageUtil.byteArrayToImage(fullUser.getImage()));

        // 2. MERGE NAMES (Lastname, Firstname M.)
        String middleInitial = "";
        if (fullUser.getMname() != null && !fullUser.getMname().isEmpty()) {
            middleInitial = fullUser.getMname().charAt(0) + ".";
        }
        
        // Format: "Miradora, Raver B."
        String fullName = fullUser.getLname() + ", " + fullUser.getFname() + " " + middleInitial;
        
        setSurname(fullName); // Set full string to the top label
        setNameMname("");     // Clear the bottom label to hide it

        // 3. Update Attendance Type & Time
        setAttendanceType(attendanceType);
        setTime(time);

        // 4. Update Position
        String position = getCurrentPosition(fullUser.getId());
        setPosition(position);

        // 5. Update ID / Agency No
        String agencyNo = fullUser.getAgencyEmployeeNo(); 
        if (agencyNo != null && !agencyNo.isBlank()) {
            setEmployeeID(agencyNo);
        } else {
            setEmployeeID("ID: " + fullUser.getId());
        }

        // 6. Update Greeting Message
        String greeting;
        java.time.LocalTime now = java.time.LocalTime.now(ZoneId.of("Asia/Manila"));
        int hour = now.getHour();

        if (hour >= 5 && hour < 12) greeting = "Good Morning";
        else if (hour >= 12 && hour < 17) greeting = "Good Afternoon";
        else if (hour >= 17 && hour < 22) greeting = "Good Evening";
        else greeting = "Good Night";

        String firstName = "";
        if (fullUser.getFname() != null && !fullUser.getFname().trim().isEmpty()) {
            firstName = fullUser.getFname().trim();
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        }

        String message1 = firstName.isEmpty() ? greeting + "!" : greeting + " " + firstName + "!";
        setSystemMessage1(message1); 
        setSystemMessage2("");

        // 7. Update Status Icons & Colors
        setBiometricSuccessImage(new Image(getClass().getResourceAsStream("/Images/fp_id_success_icon.png")));
        
        // IMPORTANT: Style must be set on FX Thread
        if (msgPane != null) {
            msgPane.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5;");
        }

        // 8. Start Timer to Clear
        startNewTimeline(this::clearIdentificationDisplay, 10.0);
    });
}

    // [Replace this method in LoginPaneCTRL.java]
public void handleIdentificationFailure() {
    isDisplayingResult = true;
    // resetFingerprintView(); // Remove this so raw print stays visible

    Platform.runLater(() -> {
        SoundUtil.playFailSound();
        setSystemMessage1("Fingerprint not recognized");
        setSystemMessage2("Try Again.");
        setBiometricSuccessImage(new Image(getClass().getResourceAsStream("/Images/fp_id_fail_icon.png")));
        
        // Reset User Info placeholders
        setPosition("Position");
        setUserImage(new Image(getClass().getResourceAsStream("/Images/default_user_img1.png")));
        setEmployeeID("LGU-L000000");
        setSurname("Surname,");
        setNameMname("First Name M.");
        setAttendanceType("_______");
        setTime("--:-- -- ");

        // IMPORTANT: Style must be set on FX Thread
        if (msgPane != null) {
            msgPane.setStyle("-fx-background-color: #F44336; -fx-background-radius: 5;");
        }

        startNewTimeline(this::clearFailureDisplay, 5.0);
    });
}
   public void handleIdentificationNotPressing(User user, String message1, String message2){
       isDisplayingResult = true;
        Platform.runLater(() -> {
            // *** Set failure message in system message labels ***
            setSystemMessage1(message1);
            setSystemMessage2(message2);

            setBiometricSuccessImage(new Image(getClass().getResourceAsStream("/Images/fp_id_fail_icon.png")));
            setPosition("Position");
            setEmployeeID("LGU-L000000");

            setUserImage(new Image(getClass().getResourceAsStream("/Images/default_user_img1.png")));
            setSurname("Surname,");
            setNameMname("First Name M.");
            setAttendanceType("_______");
            setTime("--:-- -- ");

            startNewTimeline(this::clearFailureDisplay, 5.0);
        });
    }
private void startNewTimeline(Runnable clearAction, double seconds) {
    stopCurrentTimeline(); // Clean up any existing timer first

    // Create a new timer that waits for the specific 'seconds' provided
    currentDisplayTimeline = new Timeline(
        new KeyFrame(Duration.seconds(seconds), event -> {
            clearAction.run(); 
            currentDisplayTimeline = null;
        })
    );
    currentDisplayTimeline.setCycleCount(1);
    currentDisplayTimeline.play();
}



    private void timeInUser(User user) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Attendance.timeIn(user.getId(), time);
        SoundUtil.playTimeInSound();
        handleIdentificationSuccess(user, "Timed In", LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        loadRecentAttendanceTable(); // Refresh the table
    }

    private void timeOutUser(User user) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        // Use timeOut instead of timeOutRecentRecord to ensure we're updating today's record
        Attendance.timeOut(user.getId(), time);
        SoundUtil.playTimeOutSound();
        handleIdentificationSuccess(user, "Timed Out", LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")));
        loadRecentAttendanceTable(); // Refresh the table
    }

    private static String getCurrentTime(){
        LocalTime time = LocalTime.now();
        return time.toString();
    }

    // [Replace this method in LoginPaneCTRL.java]
private void clearIdentificationDisplay() {
    Platform.runLater(() -> {
        // Reset System Message Color
        if (msgPane != null) {
            msgPane.setStyle("-fx-background-color: #E6C79C; -fx-background-radius: 5;");
        }

        // Reset Fields
        setUserImage(new Image(getClass().getResourceAsStream("/Images/default_user_img1.png")));
        setAttendanceType("_______");
        setTime("--:-- -- ");
        setEmployeeID("LGU-L000000");
        setSurname("Surname,");
        setNameMname("First Name M."); // Reset to default placeholder
        setBiometricSuccessImage(null);
        setSystemMessage1("");
        setSystemMessage2("");
        setPosition("Position"); 
        
        // Reset Fingerprint View
        scannerStatusLabel.setText("Reader Connected");
        scannerStatusSubtextLabel.setText("READY FOR SCAN");
        scannerStatusLabel.setStyle("-fx-text-fill: #2c4036; -fx-font-weight: bold;");
        scannerStatusSubtextLabel.setStyle("-fx-text-fill: #2c4036; -fx-font-weight: bold;");
        
        resetFingerprintView();
    });
    isDisplayingResult = false;
}
    private void centerColumn(TableColumn<Attendance, String> col) {
        col.setStyle("-fx-alignment: CENTER;");
    }
    private void clearFailureDisplay() {
        Platform.runLater(() -> {
            setSystemMessage1("");
            setSystemMessage2("");
            scannerStatusLabel.setText("Reader Connected");
            scannerStatusSubtextLabel.setText("READY FOR SCAN"); // Changed text

            scannerStatusLabel.setStyle("-fx-text-fill: #2c4036; -fx-font-weight: bold;");
            scannerStatusSubtextLabel.setStyle("-fx-text-fill: #2c4036; -fx-font-weight: bold;");

            setBiometricSuccessImage(null);
            setPosition("Position");
            resetFingerprintView();
        });
        isDisplayingResult = false;
}
    private void resetFingerprintView() {
    Platform.runLater(() -> {
        // 1. Restore the Default Image
        fpImageview.setImage(fpImage);
        
        // 2. Remove the Circle Clip (Make it square again)
        fpImageview.setClip(null);
        
        // 3. Restore Dimensions and Ratio
        fpImageview.setFitWidth(346); 
        fpImageview.setFitHeight(364);
        fpImageview.setPreserveRatio(false);
    });
}
    
    private void clearDenialDisplay() {
        Platform.runLater(() -> {
            // Reset Text
            setSystemMessage1("");
            setSystemMessage2("");

            // Reset Scanner Status to Ready
            scannerStatusLabel.setText("Reader Connected");
            scannerStatusSubtextLabel.setText("READY FOR SCAN");
            scannerStatusLabel.setStyle("-fx-text-fill: #2c4036; -fx-font-weight: bold;");
            scannerStatusSubtextLabel.setStyle("-fx-text-fill: #2c4036; -fx-font-weight: bold;");

            // Clear User Info
            setBiometricSuccessImage(null);
            setUserImage(new Image(getClass().getResourceAsStream("/Images/default_user_img1.png")));
            setAttendanceType("_______");
            setTime("--:-- -- ");
            setEmployeeID("LGU-L000000");
            setSurname("Surname,");
            setNameMname("First Name M.");
            setPosition("Position"); 
            resetFingerprintView();
        });
        isDisplayingResult = false;
    }
        private String getCurrentPosition(int userId) {
        ObservableList<Assignment> assignments = Assignment.getActiveAssignmentsByUserId(userId);
        if (assignments != null && !assignments.isEmpty()) {
            return assignments.get(0).getPosition();
        }
        return "No Position";
    }
   private void handleUserIdentification(User user) {
        stopCurrentTimeline();
        
        if (user.getStatus() != 1) { 
            SoundUtil.playDenySound();
            handleIdentificationDenial(user, "ACCESS DENIED", "Employee Inactive");
            return; 
        }

        AttendanceAction action = Attendance.determineAutoAction(user.getId());
        
        String displayTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"));

        // 3. Execute
        switch (action) {
            case TIME_IN_AM:
                timeInUser(user); 
                break;
            case TIME_OUT_AM:
                timeOutUser(user);
                if (LocalTime.now().getHour() >= 12) {
                     handleIdentificationSuccess(user, "AM OUT (Late)", displayTime);
                }
                break;
            case TIME_IN_PM:
                timeInUser(user); 
                handleIdentificationSuccess(user, "PM IN", displayTime);
                break;
            case TIME_OUT_PM:
                timeOutUser(user);
                break;
            case OVERTIME_IN:
                timeInOTUser(user);
                break;
            case OVERTIME_OUT:
                timeOutOTUser(user);
                break;
            case DENIED_ALREADY_COMPLETED:
                SoundUtil.playDenySound();
                handleIdentificationDenial(user, "ALREADY DONE", "Shift completed.");
                break;
            case DENIED_TOO_EARLY:
                SoundUtil.playDenySound();
                handleIdentificationDenial(user, "PLEASE WAIT", "Scanning too fast.");
                break;
            default:
                SoundUtil.playFailSound();
                handleIdentificationFailure();
                break;
        }
    }
    private String determineAttendanceType(User user) {
        // Add your attendance logic here
        boolean hasTimedIn = Attendance.userHasTimeInToday(user.getId());
        return hasTimedIn ? "Timed Out" : "Timed In";
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Runnable onFingerPrintScan = this::loginCallBackMethod;

        identification = new IdentificationThread(
            fpImageview, 
            this::loginCallBackMethod,
            this::handleUserIdentification,
            this::handleIdentificationFailure,
            // 5th Argument: Callback to update UI status
            (isConnected) -> {
                if (scannerStatusLabel != null && scannerStatusSubtextLabel != null) {
                    if (isConnected) {
                        scannerStatusLabel.setText("Reader Connected");
                        // CHANGED: Fixed text, no longer depends on "currentActionMode"
                        scannerStatusSubtextLabel.setText("READY FOR SCAN");
                        scannerStatusLabel.setStyle("-fx-text-fill: #2c4036; -fx-font-weight: bold;"); 
                    } else {
                        scannerStatusLabel.setText("Reader Disconnected");
                        scannerStatusSubtextLabel.setText("WAITING FOR DEVICE");
                        scannerStatusLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;"); 
                    }
                }
            }
        );

        recentAttendanceTable.setFocusTraversable(false);
        recentAttendanceTable.setMouseTransparent(true);
        mainAnchorPane.setFocusTraversable(true);
        Platform.runLater(() -> mainAnchorPane.requestFocus());

        // set an image for fpImageview
        fpImageview.setImage(fpImage);

        // Clock display
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Platform.runLater(() -> {
                    DateTimeFormatter dateformatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd");
                    dateLabel.setText(LocalDateTime.now().format(dateformatter));

                    DateTimeFormatter timeformatter = DateTimeFormatter.ofPattern("hh:mm:ss");
                    timeLabel.setText(LocalDateTime.now().format(timeformatter));

                    DateTimeFormatter notationformatter = DateTimeFormatter.ofPattern("a");
                    notationLabel.setText(LocalDateTime.now().format(notationformatter));
                });
            }
        };
        timer.start();

        // RECENT ATTENDANCE TABLE SETUP
        col_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_am_in.setCellValueFactory(new PropertyValueFactory<>("timeInAm"));
        col_am_out.setCellValueFactory(new PropertyValueFactory<>("timeOutAm"));
        col_pm_in.setCellValueFactory(new PropertyValueFactory<>("timeInPm"));
        col_pm_out.setCellValueFactory(new PropertyValueFactory<>("timeOutPm"));
        col_ot_in.setCellValueFactory(new PropertyValueFactory<>("overtimeIn"));
        col_ot_out.setCellValueFactory(new PropertyValueFactory<>("overtimeOut"));

        setTimeCellFactory(col_am_in);
        setTimeCellFactory(col_am_out);
        setTimeCellFactory(col_pm_in);
        setTimeCellFactory(col_pm_out);
        setTimeCellFactory(col_ot_in);
        setTimeCellFactory(col_ot_out);
        
        centerColumn(col_am_in);
        centerColumn(col_am_out);
        centerColumn(col_pm_in);
        centerColumn(col_pm_out);
        centerColumn(col_ot_in);
        centerColumn(col_ot_out);

        loadRecentAttendanceTable();
        loadAnnouncements();

        identification.start();
    }
    
    private void loadAnnouncements() {
        new Thread(() -> {
            ObservableList<Notice> notices = Notice.getActiveNotices();
            
            Platform.runLater(() -> {
                announcementsVBox.getChildren().clear();
                if (notices.isEmpty()) {
                    Label noNoticeLabel = new Label("No announcements at this time.");
                    noNoticeLabel.setFont(Font.font("Arial", 16));
                    noNoticeLabel.setTextFill(Color.BLACK);
                    announcementsVBox.getChildren().add(noNoticeLabel);
                } else {
                    for (Notice notice : notices) {
                        Label title = new Label(notice.getNotice_title());
                        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                        title.setTextFill(Color.BLACK);
                        title.setWrapText(true);

                        Label content = new Label(notice.getNotice_content());
                        content.setFont(Font.font("Arial", 16));
                        content.setTextFill(Color.web("#E6C79C")); // Light gold color
                        content.setWrapText(true);
                        
                        VBox noticeBox = new VBox(5, title, content);
                        noticeBox.setStyle("-fx-padding: 5 0 10 0;");
                        announcementsVBox.getChildren().add(noticeBox);
                    }
                }
            });
        }).start();
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
                        // Remove AM/PM notation since columns already indicate the period
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
                        setText(localTime.format(formatter));
                    } catch (Exception e) {
                        setText(time); // Fallback to original text if parsing fails
                    }
                }
            }
        });
    }
    public void shutdown() {
        System.out.println("LoginPaneCTRL: Shutting down...");

        // Set the global flag to stop threads
        ThreadFlags.programIsRunning = false;

        // Shutdown the executor service first
        if (executor != null) {
            System.out.println("Shutting down executor service...");
            executor.shutdownNow(); 
            try {
                if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
                    System.out.println("Executor did not terminate in time, forcing shutdown");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for executor to shutdown");
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            executor = null;
            System.out.println("Executor service shut down successfully.");
        }

        // Stop the identification thread
        if (identification != null) {
            System.out.println("Stopping identification thread...");
            identification.stopThread();
            try {
                // Wait for the identification thread to terminate
                identification.join(2000); 
                System.out.println("Identification thread stopped successfully.");
            } catch (InterruptedException e) {
                System.out.println("Interrupted while waiting for identification thread to stop");
                Thread.currentThread().interrupt();
            }
            identification = null;
        }

        stopCurrentTimeline();
        System.out.println("LoginPaneCTRL: Shutdown complete.");
    }
    @FXML
    private void openAdminLogin(ActionEvent event) {
        try {
            // 1. Stop the fingerprint threads
            shutdown();

            // 2. Load the Admin Login Page
            FXMLLoader loader = new FXMLLoader(getClass().getResource(paneUtil.ADMIN_LOGIN_PAGE));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            // 3. Get the CURRENT window (Stage)
            Stage stage = (Stage) adminLoginBtn.getScene().getWindow();

            // 4. Switch the Scene
            stage.setScene(scene);

            // 5. MAKE IT FULL SCREEN / MAXIMIZED
            // This ensures the green background covers the whole screen
            stage.setMaximized(true); 
            // Optional: If you want strictly no window borders (kiosk mode), use:
            // stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to open Admin Login: " + e.getMessage());
        }
    }
    private void loginCallBackMethod(){
        
    }

    private void reloadDefaultFingerprintImage(){
        //add a 3 second delay before reloading the default fingerprint image
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(7000),
                ae -> fpImageview.setImage(fpImage)));
        timeline.play();
    }

    private void loadRecentAttendanceTable(){
        ObservableList<Attendance> attendanceList = Attendance.getRecentAttendance();
        
        recentAttendanceTable.setItems(attendanceList);
    }
    
    @Deprecated
    private void openFpEnrollmentPane(ActionEvent event) {
        paneUtil.openModal(paneUtil.FP_ENROLLMENT);
    }

    @Deprecated
    private void openFpIdentificationPane(ActionEvent event) {
        paneUtil.openModal(paneUtil.FP_IDENTIFICATION);
    }
}