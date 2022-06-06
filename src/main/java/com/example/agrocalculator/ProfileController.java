package com.example.agrocalculator;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.IOException;

import static com.example.agrocalculator.MainApplication.setScene;

public class ProfileController {
    public Label name;
    public Label phone;
    public Label email;

    public void backToCalc(ActionEvent actionEvent) throws IOException {
        setScene("calculator-view.fxml");
    }
}
