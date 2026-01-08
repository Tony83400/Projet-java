package com.example.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination; // Pour bloquer la sortie via Echap si voulu
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        // On retire la taille fixe pour laisser le fullscreen gérer
        Scene scene = new Scene(fxmlLoader.load());

        Image icon = new Image(getClass().getResourceAsStream("/com/example/frontend/img/fr.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Borne de Commandes");
        stage.setScene(scene);

        // --- CORRECTIONS FULLSCREEN ---
        stage.setMaximized(true);
        stage.setFullScreen(true);
        // C'est cette ligne qui enlève le message "Press ESC..."
        stage.setFullScreenExitHint("");
        // Optionnel : Empêcher de sortir avec Echap (utile pour une borne)
        // stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        stage.show();
    }
}