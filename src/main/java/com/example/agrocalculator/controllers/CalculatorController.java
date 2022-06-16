package com.example.agrocalculator.controllers;

import com.example.agrocalculator.model.DialogWindow;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.agrocalculator.MainApplication.setScene;

public class CalculatorController {
    //Объявление всех полей ввода и вывода
    public Label answerLabel;
    public TextField productivityInput;
    public VBox answerWrapper;
    public TextField areaInput;
    public TextField plowingDepthInput;
    public TextField soilDensityInput;
    public TextField nitrogenInput;
    public TextField phosphorusInput;
    public TextField potassiumInput;

    //При загрузке поле для ответа становится невидимым
    public void initialize() {
        answerWrapper.setVisible(false);
    }

    //Расчет, вызывается по соответсующей кнопке
    public void calculate(ActionEvent actionEvent) {
        TextField[] inputs = {
                productivityInput, areaInput,
                plowingDepthInput, soilDensityInput,
                nitrogenInput, phosphorusInput,
                potassiumInput
        };
        ArrayList<Double> numbers = extractNumbers(inputs);
        if(numbers.size() == inputs.length) {
            answerLabel.setText("При указанных параметрах почвы следует посадить культуры: a, b, и c");
            answerWrapper.setVisible(true);
        } else {
            new DialogWindow("CalculationError").showDialog();
        }
    }

    //Округление до сотой части дроби
    private double customRound(double n) {
        n = n * 100;
        int result = (int) Math.round(n);
        return (double) result / 100;
    }

    //Получение чисел из полей ввода
    private ArrayList<Double> extractNumbers(TextField[] inputs) {
        ArrayList<Double> numbers = new ArrayList<>();
        try {
            for (TextField input : inputs) {
                numbers.add(Double.parseDouble(input.getText().replaceAll(",", ".")));
            }
        } catch (NumberFormatException ignored) {

        }
        return numbers;
    }

    //Переход на страницу профиля
    public void goToProfile(ActionEvent actionEvent) throws IOException {
        setScene("profile.fxml");
    }

    //Смена на калькулятор культуры
    public void goToOtherCalculator(ActionEvent actionEvent) throws IOException {
        setScene("calculator2-view.fxml");
    }
}
