package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.DialogWindow;
import com.example.agrocalculator.model.User;
import com.kosprov.jargon2.api.Jargon2;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.Objects;

import static com.example.agrocalculator.MainApplication.setScene;
import static com.kosprov.jargon2.api.Jargon2.jargon2Hasher;

public class RegistrationPageController {
    //Объявляем поля ввода
    public TextField emailInput;
    public TextField phoneInput;
    public TextField nameInput;
    public TextField passwordInput;
    public TextField passwordConfirmationInput;

    //Вызывается по кнопке Зарегистрироваться
    public void registrationCompleted(ActionEvent actionEvent) {
        //Проверяем поля ввода на правильность
        if(emailInput.getText().length() == 0 || !emailInput.getText().contains("@") ||
                phoneInput.getText().length() != 11 || nameInput.getText().length() == 0 ||
                passwordInput.getText().length() < 8 ||
                !Objects.equals(passwordInput.getText(), passwordConfirmationInput.getText())) {
            //Выводим ошибку, если не верно заполнено
            new DialogWindow("RegistrationError").showDialog();
        } else {
            //Иначе введеный пароль хэшируем
            byte[] password = passwordInput.getText().getBytes();
            Jargon2.Hasher hasher = jargon2Hasher()
                    .type(Jargon2.Type.ARGON2d) // Data-dependent hashing
                    .memoryCost(65536)  // 64MB memory cost
                    .timeCost(3)        // 3 passes through memory
                    .parallelism(4)     // use 4 lanes and 4 threads
                    .saltLength(16)     // 16 random bytes salt
                    .hashLength(16);    // 16 bytes output hash

            String encodedHash = hasher.password(password).encodedHash();
            //Создаем экземпляр класса Пользователь
            User user = new User(emailInput.getText(), phoneInput.getText(), nameInput.getText(), encodedHash);
            //Загружаем пользователя в БД
            new DataBaseConnection().addUser(user);
            //Загружаем страницу для логина
            try {
                setScene("loginPage.fxml");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
