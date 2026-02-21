package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Utilities.Encryption;
import com.raver.iemsmaven.Utilities.PaneUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminLoginPaneCTRL implements Initializable {
    @FXML private Button loginBtn;
    @FXML private TextField emailField;
    @FXML private Label loginPrompt;
    @FXML private ImageView togglePassVisibilityImageView;
    @FXML private PasswordField passwordField;
    @FXML private TextField visiblePasswordField;
    @FXML private Button backBtn;

    PaneUtil paneUtil = new PaneUtil();
    boolean showPassword = false;

    // CSS Strings for validation
    private final String DEFAULT_STYLE = "-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 4; -fx-padding: 8;";
    private final String ERROR_STYLE = "-fx-background-color: white; -fx-border-color: #ff572d; -fx-border-radius: 4; -fx-padding: 8;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loginPrompt.setVisible(false);
        loginPrompt.setManaged(true); // Ensure it takes up space when visible

        // Bind text properties
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        passwordField.setVisible(true);
        visiblePasswordField.setVisible(false);

        // Add listeners to clear error styles when user types
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            emailField.setStyle(DEFAULT_STYLE);
            loginPrompt.setVisible(false);
        });
        
        // Listener for both password fields
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            passwordField.setStyle(DEFAULT_STYLE);
            visiblePasswordField.setStyle(DEFAULT_STYLE);
            loginPrompt.setVisible(false);
        });

        // Toggle Password Visibility Logic
        togglePassVisibilityImageView.setOnMouseClicked(event -> {
            if (showPassword) {
                togglePassVisibilityImageView.setImage(new Image("/Images/hide_password.png"));
                passwordField.setText(visiblePasswordField.getText());
                passwordField.setVisible(true);
                visiblePasswordField.setVisible(false);
                showPassword = false;
            } else {
                togglePassVisibilityImageView.setImage(new Image("/Images/show_password.png"));
                visiblePasswordField.setText(passwordField.getText());
                visiblePasswordField.setVisible(true);
                passwordField.setVisible(false);
                showPassword = true;
            }
        });
    }

    @FXML
    private void authenticate(ActionEvent event) {
        String enteredEmail = emailField.getText();
        String enteredPassword = passwordField.getText();

        boolean hasError = false;

        // UI Feedback: Check Empty Fields
        if (enteredEmail.isEmpty()) {
            emailField.setStyle(ERROR_STYLE);
            showLoginPrompt("Email is required");
            hasError = true;
        }
        if (enteredPassword.isEmpty()) {
            setErrorOnPasswordField();
            if (!hasError) showLoginPrompt("Password is required"); // Only show if email wasn't already the issue
            hasError = true;
        }

        if (hasError) return;

        // Proceed to logic
        User user = User.getUserByEmail(enteredEmail);

        // Check if user exists
        if (user == null) {
            triggerLoginFailure();
            return;
        }

        String passwordHash = user.getPassword();
        boolean passwordMatched = false;

        // --- PASSWORD CHECK LOGIC ---
        // 1. Check BCrypt
        if (com.raver.iemsmaven.Utilities.PasswordUtil.verifyPassword(enteredPassword, passwordHash)) {
            passwordMatched = true;
        } 
        // 2. Legacy Check
        else if (passwordHash != null && !passwordHash.startsWith("$2a$")) {
            String enteredPasswordHashedWithSHA;
            try {
                enteredPasswordHashedWithSHA = Encryption.hashPassword(enteredPassword);
            } catch (Exception e) {
                enteredPasswordHashedWithSHA = "";
            }

            if (passwordHash.equals(enteredPasswordHashedWithSHA)) {
                passwordMatched = true;
                // Upgrade hash
                User.updateUserPassword(user.getId(), enteredPassword); 
            }
        }

        if (passwordMatched) {
            // Success
            User userFromDb = User.getUserByUserId(user.getId());
            Session.getInstance().setLoggedInUser(userFromDb);
            proceedUserLogin(user);
        } else {
            // Failure
            triggerLoginFailure();
        }
    }

    private void triggerLoginFailure() {
        emailField.setStyle(ERROR_STYLE);
        setErrorOnPasswordField();
        showLoginPrompt("Invalid Email or Password");
        // Removed Thread.sleep() as it freezes the UI thread
    }

    private void setErrorOnPasswordField() {
        if (showPassword) {
            visiblePasswordField.setStyle(ERROR_STYLE);
        } else {
            passwordField.setStyle(ERROR_STYLE);
        }
    }

    private void showLoginPrompt(String message) {
        loginPrompt.setVisible(true);
        loginPrompt.setText(message);
    }
    
    private void proceedUserLogin(User authdUser) {
        User user = authdUser;
        String privilege = user.getPrivilege();

        if (privilege.equalsIgnoreCase("employee")) {
            triggerLoginFailure();
            showLoginPrompt("Access Denied: Admins Only");
        } 
        else if (privilege.equalsIgnoreCase("admin") || privilege.equalsIgnoreCase("records officer")) {
            System.out.println("Logged in as " + user.getEmail());
            paneUtil.changeScene(loginBtn, paneUtil.CONTAINER_PANE);
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        paneUtil.changeScene(backBtn, paneUtil.LOGIN_PANE);
    }
}