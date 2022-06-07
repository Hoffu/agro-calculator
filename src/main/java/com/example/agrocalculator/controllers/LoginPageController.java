package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.DialogWindow;
import com.kosprov.jargon2.api.Jargon2;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;

import static com.example.agrocalculator.MainApplication.setScene;
import static com.example.agrocalculator.database.DataBaseConnection.currentUserId;
import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;

public class LoginPageController {
    public TextField loginEntry;
    public TextField passwordEntry;

    public void login(ActionEvent actionEvent) throws IOException {
        String encodedHash = new DataBaseConnection().userLogin(loginEntry.getText());
        boolean matches = false;
        if(encodedHash.length() > 0) {
            Jargon2.Verifier verifier = jargon2Verifier();
            matches = verifier.hash(encodedHash).password(passwordEntry.getText().getBytes()).verifyEncoded();
        }
        if(matches) {
            setScene("calculator-view.fxml");
        } else {
            currentUserId = -1;
            new DialogWindow("LoginError").showDialog();
        }
    }

    public void registration(ActionEvent actionEvent) throws IOException {
        setScene("registrationPage.fxml");
    }
}
