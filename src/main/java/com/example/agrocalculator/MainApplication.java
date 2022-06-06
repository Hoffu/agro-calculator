package com.example.agrocalculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class MainApplication extends Application {
    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        setScene("loginPage.fxml");
        primaryStage.setTitle("Агро-калькулятор");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void setScene(String path) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(path));
        Scene scene = new Scene(fxmlLoader.load(), 540, 615);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
        primaryStage.setScene(scene);
    }
}
