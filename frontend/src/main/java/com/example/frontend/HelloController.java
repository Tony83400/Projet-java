package com.example.frontend;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class HelloController {
    @FXML
    private Label welcomeText;
    @FXML
    private ImageView flag;
    @FXML
    private Image imgFR;
    @FXML
    private Image imgGB;

    @FXML
    private String language = "French";

    @FXML
    private void switch_language() {
        if (language.equals("French")) {
            welcomeText.setText("Tap to continue");
            flag.setImage(imgGB);
            language = "English";
        } else {
            welcomeText.setText("Appuyer pour continuer");
            flag.setImage(imgFR);
            language = "French";
        }
    }

    @FXML
    private void goToDashboard(MouseEvent event) {
        try {
            // --- CORRECTION DU FICHIER CIBLE ---
            // On charge bien dashboard-view.fxml et non hello-view.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent dashboardRoot = loader.load();

            Stage stage = (Stage) welcomeText.getScene().getWindow();
            Scene dashboardScene = new Scene(dashboardRoot);

            stage.setScene(dashboardScene);

            // On réapplique le plein écran car changer de scène peut parfois le faire sauter
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur: Impossible de trouver dashboard-view.fxml !");
        }
    }

    public void initialize() {
        imgFR = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/fr.png")));
        imgGB = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/gb.png")));

        flag.setImage(imgFR);

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), welcomeText);
        fade.setFromValue(0.1);
        fade.setToValue(0.9);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }
}