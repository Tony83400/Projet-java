package com.example.frontend;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() throws IOException {
        String data = DataService.getInstance().getHelloWorld("http://localhost:7000/users");

        welcomeText.setText("Welcome to JavaFX Application! " + data);

    }
}
