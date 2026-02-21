package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.BackupFile;
import com.raver.iemsmaven.Utilities.Config; // Assuming you have Config for DB keys
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import com.raver.iemsmaven.Utilities.Config;

public class ADMIN_SystemBackupCTRL implements Initializable {

    @FXML private Button createBackupBtn;
    @FXML private Button restoreBackupBtn;
    @FXML private TableView<BackupFile> backupTable;
    @FXML private TableColumn<BackupFile, String> colName;
    @FXML private TableColumn<BackupFile, LocalDateTime> colDate;
    @FXML private TableColumn<BackupFile, String> colSize;
    @FXML private Label lastBackupLabel;
    @FXML private VBox progressBox;
    @FXML private ProgressBar progressBar;
    @FXML private Label progressLabel;

    // --- Configuration ---
    private static final String DB_NAME = Config.getDbName();
    private static final String DB_USER = Config.getDbUser();
    private static final String DB_PASSWORD = Config.getDbPassword();
    private static final String XAMPP_BIN_PATH = "C:\\xampp\\mysql\\bin\\";
    
    private static final String BACKUP_DIR = "C:\\IEMS_Backups\\";


    private ScheduledExecutorService scheduler;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Setup Table
        colName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("fileSize"));
        
        // Format Date Column
        colDate.setCellFactory(column -> new TableCell<BackupFile, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")));
                }
            }
        });

        // 2. Setup Selection Listener
        backupTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            restoreBackupBtn.setDisable(newVal == null);
        });

        // 3. Button Actions
        createBackupBtn.setOnAction(e -> handleCreateBackup());
        restoreBackupBtn.setOnAction(e -> handleRestoreBackup());

        // 4. Initial Checks & Loads
        ensureBackupDirectoryExists();
        runRetentionPolicy(); // Delete old files
        loadBackupsIntoTable();
        startDailyBackupScheduler(); // Start the auto-backup timer
    }

    // =============================================================
    // 1. FILE MANAGEMENT & TABLE
    // =============================================================

    private void ensureBackupDirectoryExists() {
        File dir = new File(BACKUP_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @FXML
    private void refreshTable() {
        loadBackupsIntoTable();
    }

    private void loadBackupsIntoTable() {
        File folder = new File(BACKUP_DIR);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".enc"));

        ObservableList<BackupFile> list = FXCollections.observableArrayList();
        LocalDateTime newestDate = null;

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                LocalDateTime date = LocalDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(file.lastModified()), 
                    java.time.ZoneId.systemDefault()
                );
                
                long sizeKb = file.length() / 1024;
                String sizeStr = sizeKb + " KB";

                list.add(new BackupFile(file.getName(), date, sizeStr, file.getAbsolutePath()));

                // Track newest
                if (newestDate == null || date.isAfter(newestDate)) {
                    newestDate = date;
                }
            }
        }
        
        // Sort descending (newest first)
        list.sort((a, b) -> b.getDateCreated().compareTo(a.getDateCreated()));
        backupTable.setItems(list);

        if (newestDate != null) {
            lastBackupLabel.setText("Last Backup: " + newestDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        } else {
            lastBackupLabel.setText("Last Backup: Never");
        }
    }

    // =============================================================
    // 2. RETENTION POLICY (Feature #2)
    // =============================================================
    
    private void runRetentionPolicy() {
        File folder = new File(BACKUP_DIR);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".enc"));
        
        if (listOfFiles == null) return;

        LocalDateTime threshold = LocalDateTime.now().minusDays(30); // 30 Day Policy
        int deletedCount = 0;

        for (File file : listOfFiles) {
            LocalDateTime fileDate = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(file.lastModified()), 
                java.time.ZoneId.systemDefault()
            );

            if (fileDate.isBefore(threshold)) {
                if (file.delete()) {
                    deletedCount++;
                    System.out.println("Retention Policy: Deleted old backup " + file.getName());
                }
            }
        }
        if (deletedCount > 0) {
            System.out.println("Cleaned up " + deletedCount + " old backup files.");
        }
    }

    // =============================================================
    // 3. AUTOMATIC SCHEDULER (Daily Backup)
    // =============================================================

    private void startDailyBackupScheduler() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        // Check every hour if a backup is needed
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(() -> {
                // Logic: Has a backup been created today?
                if (!hasBackupForToday()) {
                    System.out.println("Auto-Backup: Starting daily backup...");
                    handleCreateBackup(); // Re-use the manual method
                }
            });
        }, 0, 1, TimeUnit.HOURS);
    }

    private boolean hasBackupForToday() {
        if (backupTable.getItems().isEmpty()) return false;
        
        BackupFile newest = backupTable.getItems().get(0); // List is sorted new first
        LocalDateTime today = LocalDateTime.now();
        
        return newest.getDateCreated().toLocalDate().isEqual(today.toLocalDate());
    }

    // =============================================================
    // 4. BACKUP LOGIC (Encrypted)
    // =============================================================

    private void handleCreateBackup() {
        if (!isBinPathValid("mysqldump.exe")) return;

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        String filename = "backup_" + timestamp + ".enc"; // .enc for Encrypted
        File destFile = new File(BACKUP_DIR + filename);

        createBackupBtn.setDisable(true);
        progressBox.setVisible(true);
        progressLabel.setText("Creating Database Dump...");
        progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // 1. Create Temp SQL
                File tempSql = new File(BACKUP_DIR + "temp_dump.sql");
                
                List<String> command = new ArrayList<>();
                command.add(XAMPP_BIN_PATH + "mysqldump.exe");
                command.add("-u" + DB_USER);
                if (!DB_PASSWORD.isEmpty()) command.add("-p" + DB_PASSWORD);
                command.add(DB_NAME);
                command.add("--result-file=" + tempSql.getAbsolutePath());

                ProcessBuilder pb = new ProcessBuilder(command);
                Process p = pb.start();
                int exitCode = p.waitFor();

                if (exitCode != 0) throw new IOException("mysqldump failed with code " + exitCode);

                // 2. Encrypt SQL to .enc
                Platform.runLater(() -> progressLabel.setText("Encrypting Data..."));
                encryptFile(tempSql, destFile);

                // 3. Delete Temp SQL (Security)
                tempSql.delete();

                return null;
            }
        };

        task.setOnSucceeded(e -> {
            createBackupBtn.setDisable(false);
            progressBox.setVisible(false);
            loadBackupsIntoTable();
            showAlert(Alert.AlertType.INFORMATION, "Backup Complete", "Encrypted backup saved: " + filename);
        });

        task.setOnFailed(e -> {
            createBackupBtn.setDisable(false);
            progressBox.setVisible(false);
            showAlert(Alert.AlertType.ERROR, "Backup Failed", task.getException().getMessage());
        });

        new Thread(task).start();
    }

    // =============================================================
    // 5. RESTORE LOGIC (Decrypt -> Restore)
    // =============================================================

    private void handleRestoreBackup() {
        BackupFile selected = backupTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Restore");
        confirm.setHeaderText("DANGER: Overwrite Data?");
        confirm.setContentText("This will wipe the current database and restore from: " + selected.getFileName() + "\nAre you sure?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            restoreBackupBtn.setDisable(true);
            progressBox.setVisible(true);
            progressLabel.setText("Decrypting Backup...");
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    File encryptedFile = new File(selected.getFullPath());
                    File tempSql = new File(BACKUP_DIR + "temp_restore.sql");

                    // 1. Decrypt
                    decryptFile(encryptedFile, tempSql);

                    // 2. Restore from Temp SQL
                    Platform.runLater(() -> progressLabel.setText("Restoring Database..."));
                    
                    List<String> command = new ArrayList<>();
                    command.add(XAMPP_BIN_PATH + "mysql.exe");
                    command.add("-u" + DB_USER);
                    if (!DB_PASSWORD.isEmpty()) command.add("-p" + DB_PASSWORD);
                    command.add(DB_NAME);

                    ProcessBuilder pb = new ProcessBuilder(command);
                    pb.redirectInput(tempSql);
                    Process p = pb.start();
                    int exitCode = p.waitFor();

                    // 3. Cleanup
                    tempSql.delete();

                    if (exitCode != 0) throw new IOException("mysql restore failed with code " + exitCode);

                    return null;
                }
            };

            task.setOnSucceeded(e -> {
                restoreBackupBtn.setDisable(false);
                progressBox.setVisible(false);
                showAlert(Alert.AlertType.INFORMATION, "Restore Complete", "System restored to state: " + selected.getDateCreated());
            });

            task.setOnFailed(e -> {
                restoreBackupBtn.setDisable(false);
                progressBox.setVisible(false);
                showAlert(Alert.AlertType.ERROR, "Restore Failed", task.getException().getMessage());
            });

            new Thread(task).start();
        }
    }

    // =============================================================
    // UTILITIES (Encryption & General)
    // =============================================================

    private void encryptFile(File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, inputFile, outputFile);
    }

    private void decryptFile(File inputFile, File outputFile) throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, inputFile, outputFile);
    }

    private void doCrypto(int cipherMode, File inputFile, File outputFile) throws Exception {
        try {
            String hexKey = Config.getSecretKey();
            
            byte[] keyBytes = hexStringToByteArray(hexKey);

            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(cipherMode, secretKey);

            try (FileInputStream inputStream = new FileInputStream(inputFile);
                 FileOutputStream outputStream = new FileOutputStream(outputFile);
                 CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher)) {
                
                if (cipherMode == Cipher.ENCRYPT_MODE) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        cipherOutputStream.write(buffer, 0, bytesRead);
                    }
                } else {
                    try (CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = cipherInputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception("Crypto Error: " + e.getMessage());
        }
    }

    private boolean isBinPathValid(String toolName) {
        File tool = new File(XAMPP_BIN_PATH + toolName);
        if (!tool.exists()) {
            showAlert(Alert.AlertType.ERROR, "Configuration Error", "Cannot find " + toolName + " at " + XAMPP_BIN_PATH);
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}