package com.example.agrocalculator.model;

import javafx.scene.control.Alert;

public class DialogWindow {
    private String headerText = "";

    public DialogWindow(String errorType) {
        switch (errorType) {
            case "LoginError" -> headerText = "Неверно введены данные, аутентификация не пройдена";
            case "RegistrationError" -> headerText = "Неправильно заполнены поля";
            case "CalculationError" -> headerText = "Неверно введены данные";
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
