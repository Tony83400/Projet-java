package com.example.frontend;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
            dataService.setLanguageId("0");
        } else {
            dataService.setLanguageId("1");
        }
        applyLanguage();
    }

    private void applyLanguage() {
        if (dataService.getLanguageId().equals("0")) { // English
            startText.setText("Tap to continue");
            welcomeText.setText("Master Chen's noodles");
            flag.setImage(imgFR);
        } else {
            startText.setText("Toucher pour continuer");
            welcomeText.setText("Les nouilles de Maître Chen");
            flag.setImage(imgGB);
        }
    }

<<<<<<< Updated upstream
=======
    @FXML
    private void goToDashboard(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard-view.fxml"));
            Parent dashboardRoot = loader.load();
            Stage stage = (Stage) welcomeText.getScene().getWindow();
            Scene dashboardScene = new Scene(dashboardRoot);
            stage.setScene(dashboardScene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
>>>>>>> Stashed changes

    public void initialize() {

        imgFR = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/fr.png")));
        imgGB = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/gb.png")));
<<<<<<< Updated upstream
=======

        applyLanguage();

>>>>>>> Stashed changes
        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), startText);


        fade.setFromValue(0.1);
        fade.setToValue(0.9);


        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);

        fade.play();
    }


}