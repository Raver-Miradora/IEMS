package com.raver.iemsmaven.Utilities;

import com.dlsc.gemsfx.DialogPane;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator; // Import this
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle; // Import this

/**
 *
 * @author admin
 */
public class Modal {

    private static Stage loadingStage;

    public static void alert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        });
    }

    public static void inform(String title, String message, String detail) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(message);     // the shorter success/summary message
            alert.setContentText(detail);     // the more detailed info
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        });
    }

    public static void showModal(String title, String message) {
        Platform.runLater(() -> {
            Stage successStage = new Stage();
            successStage.initModality(Modality.APPLICATION_MODAL);
            successStage.setTitle(title);

            Label successLabel = new Label(message);
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> successStage.close());

            VBox modalContent = new VBox(successLabel, closeButton);
            modalContent.setSpacing(10);

            // Center the content
            modalContent.setAlignment(Pos.CENTER);

            Scene successScene = new Scene(modalContent, 350, 250);
            successStage.setScene(successScene);
            successStage.show();
        });
    }
    
    public static void showLoadingModal(String title, String message) {
        Platform.runLater(() -> {
            // If a loading modal is already showing, don't create another one
            if (loadingStage != null && loadingStage.isShowing()) {
                return;
            }

            loadingStage = new Stage();
            loadingStage.initModality(Modality.APPLICATION_MODAL);
            // Use UNDECORATED to hide the window's "X" button
            loadingStage.initStyle(StageStyle.UNDECORATED); 
            loadingStage.setTitle(title);

            Label loadingLabel = new Label(message);
            loadingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setPrefSize(60, 60);

            VBox modalContent = new VBox(loadingLabel, progressIndicator);
            modalContent.setSpacing(20);
            modalContent.setAlignment(Pos.CENTER);
            modalContent.setStyle("-fx-padding: 30; -fx-background-color: white; -fx-border-color: #B0B0B0; -fx-border-width: 1;");

            Scene loadingScene = new Scene(modalContent, 450, 300);
            loadingStage.setScene(loadingScene);
            // Use show() instead of showAndWait() to make it non-blocking
            loadingStage.show(); 
        });
    }

    public static void closeLoadingModal() {
        Platform.runLater(() -> {
            if (loadingStage != null && loadingStage.isShowing()) {
                loadingStage.close();
                loadingStage = null; // Clear the reference
            }
        });
    }

    public static boolean actionConfirmed(String title, String header, String content) {
        final boolean[] userResponse = {false};

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        // Get the result of the prompt
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                System.out.println("User clicked Yes");
                userResponse[0] = true;
            } else if (response == noButton) {
                System.out.println("User clicked No");
            }
        });

        return userResponse[0];
    }


    public static boolean actionConfirmed(DialogPane dialogPane, String title, String header, String content) {
        // Simple stub that shows the library dialog; return false by default.
        dialogPane.showConfirmation(title, header + "\n\n" + content);
        return false;
    }
}