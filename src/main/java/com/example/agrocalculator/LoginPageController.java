package com.example.agrocalculator;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;

import static com.example.agrocalculator.MainApplication.setScene;

public class LoginPageController {
    public TextField loginEntry;
    public TextField passwordEntry;

    public void login(ActionEvent actionEvent) throws IOException {
        System.out.println("Login: " + loginEntry.getText());
        System.out.println("Pass: " + passwordEntry.getText());
        setScene("calculator-view.fxml");
    }

    public void registration(ActionEvent actionEvent) throws IOException {
        setScene("registrationPage.fxml");
    }
}
