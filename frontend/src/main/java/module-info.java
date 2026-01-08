module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // 1. Autoriser l'utilisation de HTTP et JSON
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.example.frontend to javafx.fxml;

    // 2. Autoriser Jackson à remplir vos objets (Article, Menu...)
    opens com.example.frontend.models to com.fasterxml.jackson.databind;

    exports com.example.frontend;
}