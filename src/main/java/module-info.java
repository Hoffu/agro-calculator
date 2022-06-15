module com.example.agrocalculator {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires jargon2.api;
    requires java.sql;
    requires pdfbox;
    requires java.desktop;

    opens com.example.agrocalculator to javafx.fxml;
    exports com.example.agrocalculator;
    exports com.example.agrocalculator.controllers;
    opens com.example.agrocalculator.controllers to javafx.fxml;
    exports com.example.agrocalculator.model;
    opens com.example.agrocalculator.model to javafx.fxml;
    exports com.example.agrocalculator.database;
    opens com.example.agrocalculator.database to javafx.fxml;
}