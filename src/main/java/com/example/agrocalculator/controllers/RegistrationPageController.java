package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.User;
import com.kosprov.jargon2.api.Jargon2;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

import static com.example.agrocalculator.MainApplication.setScene;
import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;
import static com.kosprov.jargon2.api.Jargon2.jargon2Verifier;

public class RegistrationPageController {
    public TextField emailInput;
    public TextField phoneInput;
    public TextField nameInput;
    public TextField passwordInput;
    public TextField passwordConfirmationInput;

    public void registrationCompleted(ActionEvent actionEvent) {
        if(emailInput.getText().length() == 0 || !emailInput.getText().contains("@") ||
                phoneInput.getText().length() != 11 || nameInput.getText().length() == 0 ||
                passwordInput.getText().length() < 8 ||
                !Objects.equals(passwordInput.getText(), passwordConfirmationInput.getText())) {

            System.out.println("Не правильно заполнены поля");
        } else {
            byte[] password = passwordInput.getText().getBytes();
            Jargon2.Hasher hasher = jargon2Hasher()
                    .type(Jargon2.Type.ARGON2d) // Data-dependent hashing
                    .memoryCost(65536)  // 64MB memory cost
                    .timeCost(3)        // 3 passes through memory
                    .parallelism(4)     // use 4 lanes and 4 threads
                    .saltLength(16)     // 16 random bytes salt
                    .hashLength(16);    // 16 bytes output hash

            String encodedHash = hasher.password(password).encodedHash();
            User user = new User(emailInput.getText(), phoneInput.getText(), nameInput.getText(), encodedHash);
            new DataBaseConnection().addUser(user);

            System.out.printf("Hash: %s%n", encodedHash);

            Jargon2.Verifier verifier = jargon2Verifier();
            boolean matches = verifier.hash(encodedHash).password(passwordInput.getText().getBytes()).verifyEncoded();
            System.out.printf("Matches: %s%n", matches);

            try {
                setScene("calculator-view.fxml");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
