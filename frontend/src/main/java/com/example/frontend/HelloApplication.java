package com.example.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);
        Image icon = new Image(getClass().getResourceAsStream("/com/example/frontend/img/fr.png"));
        stage.getIcons().add(icon);
        stage.setTitle("Borne de Commandes");
        stage.setScene(scene);
        //stage.setMaximized(true);
        //setFullScreen(true);
        stage.show();
    }
}
