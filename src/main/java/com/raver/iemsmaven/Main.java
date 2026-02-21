package com.raver.iemsmaven;

import com.raver.iemsmaven.Fingerprint.ThreadFlags;
import com.raver.iemsmaven.Utilities.AppContext;
import com.digitalpersona.uareu.UareUException;
import com.raver.iemsmaven.Controller.LoginPaneCTRL;
import com.raver.iemsmaven.Fingerprint.Selection;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    
    private static Stage primaryStage;
    private static LoginPaneCTRL loginController;
    
    // The resolution you designed your FXML for
    private static final double DESIGN_WIDTH = 1920.0;
    private static final double DESIGN_HEIGHT = 1080.0;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        stage.setOnCloseRequest(this::handleCloseRequest);
        
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("View/LoginPane.fxml"));
        Parent root = loader.load();

        loginController = loader.getController();

        // 1. Get Visual Bounds (This accounts for the Windows Taskbar area)
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        // 2. Calculate the Scale Factor to fit the screen
        double scaleX = screenWidth / DESIGN_WIDTH;
        double scaleY = screenHeight / DESIGN_HEIGHT;
        
        // Use the smaller scale to ensure the entire UI fits (maintain aspect ratio)
        double scaleFactor = Math.min(scaleX, scaleY);
        
        // 3. Apply the Scale Transform
        Scale scale = new Scale(scaleFactor, scaleFactor);
        scale.setPivotX(0);
        scale.setPivotY(0);
        root.getTransforms().add(scale);

        // 4. Wrap in a Group (CRITICAL FIX)
        // The Group calculates bounds based on the *transformed* size.
        // This ensures the StackPane sees the scaled size (e.g. 1366x768) not 1920x1080.
        Group group = new Group(root);

        // 5. Wrap in StackPane to Center the content
        StackPane rootContainer = new StackPane(group);
        rootContainer.setStyle("-fx-background-color: black;"); // Fills empty space (letterboxing) with black

        Scene scene = new Scene(rootContainer, screenWidth, screenHeight);

        String stylesheet = getClass().getResource("/Style/admin_pane.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        AppContext.setStylesheet(stylesheet);

        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/Images/program_icon.png")));
        
        stage.setMaximized(true);
        // stage.setResizable(false); // Optional: Keeping it resizable is often safer on Windows
        
        stage.setScene(scene);
        stage.show();
        
        rootContainer.requestFocus();
    }

    private void handleCloseRequest(WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Any unsaved changes will be lost.");
        
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            performCleanShutdown();
        } else {
            event.consume();
        }
    }
    
    private void performCleanShutdown() {
        System.out.println("Initiating clean shutdown...");
        
        ThreadFlags.programIsRunning = false;
        
        if (loginController != null) {
            Platform.runLater(() -> {
                loginController.shutdown();
            });
        }
        
        try {
            Selection.closeReader();
            System.out.println("Fingerprint reader closed successfully.");
        } catch (UareUException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "Error closing fingerprint reader", ex);
        }
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        Platform.exit();
        System.exit(0);
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}