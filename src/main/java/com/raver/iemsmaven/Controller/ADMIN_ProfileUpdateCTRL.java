package com.raver.iemsmaven.Controller;

import com.raver.iemsmaven.Model.User;
import com.raver.iemsmaven.Session.Session;
import com.raver.iemsmaven.Utilities.ImageUtil;
import com.raver.iemsmaven.Utilities.PaneUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ADMIN_ProfileUpdateCTRL implements Initializable {

    @FXML private ImageView profileImageView;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField suffixField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button changeImageBtn;
    @FXML private Button saveBtn;
    @FXML private Button cancelBtn;

    private PaneUtil method = new PaneUtil();
    private byte[] newImageData;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadUserData();
    }

    private void loadUserData() {
        currentUser = Session.getInstance().getLoggedInUser();
        if (currentUser != null) {
            firstNameField.setText(currentUser.getFname());
            middleNameField.setText(currentUser.getMname() != null ? currentUser.getMname() : "");
            lastNameField.setText(currentUser.getLname());
            suffixField.setText(currentUser.getSuffix() != null ? currentUser.getSuffix() : "");
            emailField.setText(currentUser.getEmail());
            
            if (currentUser.getImage() != null) {
                profileImageView.setImage(ImageUtil.byteArrayToImage(currentUser.getImage()));
            } else {
                profileImageView.setImage(new Image(getClass().getResourceAsStream("/Images/default_user_img.jpg")));
            }
        }
    }

    @FXML
    private void handleChangeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                newImageData = ImageUtil.imageFileToByteArray(selectedFile);
                Image image = ImageUtil.byteArrayToImage(newImageData);
                profileImageView.setImage(image);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                // Get the current user from session
                currentUser = Session.getInstance().getLoggedInUser();

                String fname = firstNameField.getText().trim();
                String mname = middleNameField.getText().trim().isEmpty() ? null : middleNameField.getText().trim();
                String lname = lastNameField.getText().trim();
                String suffix = suffixField.getText().trim().isEmpty() ? null : suffixField.getText().trim();
                String email = emailField.getText().trim();

                byte[] image = newImageData != null ? newImageData : currentUser.getImage();

                // Update user without password (basic info)
                User.updateUserWithoutPassword(
                    currentUser.getId(),
                    fname,
                    mname,
                    lname,
                    suffix,
                    email,
                    currentUser.getPrivilege(), // Keep original privilege
                    currentUser.getContactNum(), // Keep original contact number
                    currentUser.getSex(), // Keep original sex
                    currentUser.getBirthDate(), // Keep original birth date
                    currentUser.getResidentialAddress(), // Keep original address
                    image
                );

                // Update password separately if provided
                if (!passwordField.getText().isEmpty()) {
                    User.updateUserPassword(currentUser.getId(), passwordField.getText());
                }

                // Refresh the user in session
                User updatedUser = User.getUserByUserId(currentUser.getId());
                Session.getInstance().setLoggedInUser(updatedUser);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully!");
                handleCancel();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Database error: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCancel() {
        try {
            // Use the ContainerPaneCTRL instance to navigate back to dashboard
            ContainerPaneCTRL containerCtrl = ContainerPaneCTRL.getInstance();
            if (containerCtrl != null) {
                containerCtrl.loadDashboard();
            } else {
                // Fallback: close the current stage if we can't find container
                Stage stage = (Stage) cancelBtn.getScene().getWindow();
                stage.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to navigate back to dashboard: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        if (firstNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "First name is required.");
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Last name is required.");
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Email/Username is required.");
            return false;
        }
        
        if (!emailField.getText().equals(currentUser.getEmail())) {
            if (User.isEmailTaken(emailField.getText().trim())) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Email/Username is already taken.");
                return false;
            }
        }
        
        if (!passwordField.getText().isEmpty()) {
            if (passwordField.getText().length() < 6) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters long.");
                return false;
            }
            
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
                return false;
            }
        }
        
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}