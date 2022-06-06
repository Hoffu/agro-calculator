package com.example.agrocalculator;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class RegistrationPageController {
    public TextField emailInput;
    public TextField phoneInput;
    public TextField nameInput;
    public TextField passwordInput;
    public TextField passwordConfirmationInput;

    public void registrationCompleted(ActionEvent actionEvent) {
//        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
//        String myPassword = "ThisIsMyPassword";
//
//        String hash = argon2.hash(2,15*1024,1, myPassword.toCharArray());
//        System.out.println(hash);
//
//        boolean validPassword = argon2.verify(hash, myPassword.toCharArray());
//        System.out.println(validPassword);
    }
}
