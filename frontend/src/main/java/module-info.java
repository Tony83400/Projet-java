module com.example.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires api.users;


    opens com.example.frontend to javafx.fxml;
    exports com.example.frontend;
}