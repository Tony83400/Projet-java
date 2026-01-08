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
    @FXML
    private Label welcomeText;
    @FXML
    private Label startText;
    @FXML
    private ImageView flag;
    @FXML
    private Image imgFR;
    @FXML
    private Image imgGB;

    @FXML
    private String language = "French";

    @FXML
    private void switch_language(){
        if (language.equals("French")) {
            startText.setText("Tap to continue");
            welcomeText.setText("Master Chen's noodles");
            flag.setImage(imgFR);
            language = "English";
        }
        else {
            startText.setText("Toucher pour continuer");
            welcomeText.setText("Les nouilles de Maître Chen");
            flag.setImage(imgGB);
            language = "French";
        }
    }


    public void initialize() {

        imgFR = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/fr.png")));
        imgGB = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/frontend/img/gb.png")));
        FadeTransition fade = new FadeTransition(Duration.seconds(1.5), startText);


        fade.setFromValue(0.1);
        fade.setToValue(0.9);


        fade.setCycleCount(Timeline.INDEFINITE);
        fade.setAutoReverse(true);

        fade.play();
    }


}

