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
    @FXML private Label welcomeText;
    @FXML private Label startText;
    @FXML private ImageView flag;

    private Image imgFR;
    private Image imgGB;
    private final DataService dataService = DataService.getInstance();

    @FXML
    private void switch_language() {
        if (dataService.getLanguageId().equals("1")) {
            dataService.setLanguageId("2");
        } else {
            dataService.setLanguageId("1");
        }
        applyLanguage();
    }

    private void applyLanguage() {
        if (dataService.getLanguageId().equals("2")) { // English
            startText.setText("Tap to continue");
            welcomeText.setText("Master Chen's noodles");
            flag.setImage(imgFR); // Montre le drapeau FR pour changer vers FR
        } else { // Français
            startText.setText("Toucher pour continuer");
            welcomeText.setText("Les nouilles de Maître Chen");
            flag.setImage(imgGB); // Montre le drapeau GB pour changer vers EN
        }
    }

    @FXML
    private void goToDashboard(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard-view.fxml"));
            Parent dashboardRoot = loader.load();

            // --- MODIFICATION ---
            // Au lieu de créer une nouvelle Scene et de refaire setFullScreen :
            // On récupère la scène actuelle et on remplace juste son contenu.
            welcomeText.getScene().setRoot(dashboardRoot);

            // Plus besoin de toucher au Stage, il reste en FullScreen !

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        imgFR = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/fr.png")));
        imgGB = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/gb.png")));

        applyLanguage();

        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), startText);
        fade.setFromValue(0.1);
        fade.setToValue(0.9);
        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);
        fade.play();
    }
}