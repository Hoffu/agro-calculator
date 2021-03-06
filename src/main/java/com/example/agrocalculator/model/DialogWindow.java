package com.example.agrocalculator.model;

import javafx.scene.control.Alert;

//Класс, обеспечивающий вывод ошибок
public class DialogWindow {
    private String headerText = "";

    public DialogWindow(String errorType) {
        switch (errorType) {
            case "LoginError" -> headerText = "Неверно введены данные, аутентификация не пройдена";
            case "RegistrationError" -> headerText = "Неправильно заполнены поля";
            case "CalculationError" -> headerText = "Неверно введены данные";
            case "RegistrationErrorUserAlreadyExists" -> headerText = "Пользователь с таким почтовым адресом уже существует!";
        }
    }

    public void showDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(headerText);
        alert.setContentText("Попробуйте еще раз");
        alert.showAndWait();
    }
}
