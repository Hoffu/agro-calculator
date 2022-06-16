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
    //Объявление полей ввода
    public TextField loginEntry;
    public TextField passwordEntry;

    //Вызывается по кнопке Войти
    public void login(ActionEvent actionEvent) throws IOException {
        //Получаем хэш пароля пользователя с указанным логином
        String encodedHash = new DataBaseConnection().userLogin(loginEntry.getText());
        boolean matches = false;
        //Если пользователь есть, проверяем его пароль
        if(encodedHash.length() > 0) {
            //Введенный пароль хэшируется и сверяется с хэшем из БД
            Jargon2.Verifier verifier = jargon2Verifier();
            matches = verifier.hash(encodedHash).password(passwordEntry.getText().getBytes()).verifyEncoded();
        }
        //Если совпало, то переходим в калькулятор
        if(matches) {
            setScene("calculator-view.fxml");
        } else {
            //Иначе айди текущего пользователя затирается и выводится ошибка
            currentUserId = -1;
            new DialogWindow("LoginError").showDialog();
        }
    }

    //Вызывается по кнопке Зарегистрироваться
    public void registration(ActionEvent actionEvent) throws IOException {
        setScene("registrationPage.fxml");
    }
}
