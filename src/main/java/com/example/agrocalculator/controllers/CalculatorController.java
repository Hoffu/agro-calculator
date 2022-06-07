package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.Calculation;
import com.example.agrocalculator.model.DialogWindow;
import com.example.agrocalculator.model.RemovalOfNutrients;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static com.example.agrocalculator.MainApplication.setScene;
import static com.example.agrocalculator.database.DataBaseConnection.currentUserId;

public class CalculatorController {
    public Label answerLabel;
    public TextField productivityInput;
    public ChoiceBox<String> cultureSelector;
    public VBox answerWrapper;
    public TextField areaInput;
    public TextField plowingDepthInput;
    public TextField soilDensityInput;
    public TextField nitrogenInput;
    public TextField phosphorusInput;
    public TextField potassiumInput;
    private final String[] culturesList = {
            "Озимая пшеница", "Озимый ячмень", "Овес", "Рис",
            "Кукуруза (зерно)", "Горох (зерно)", "Фасоль",
            "Соя", "Зернобобовые (зерно)", "Подсолнечник (семена)",
            "Клещевина", "Рапс (семена)", "Арахис", "Табак",
            "Сахарная свекла (корнеплоды)", "Сахарная свекла (семена)",
            "Картофель", "Бахчевые культуры", "Овощи", "Морковь",
            "Свекла столовая", "Огурцы", "Томаты, лук на репку",
            "Кормовые корнеплоды", "Кукуруза (зеленая масса)",
            "Подсолнечник (зеленая масса)",
            "Злакобобовая смесь (зеленая масса)",
            "Многолетние травы (сено)", "Злаковые травы (сено)",
            "Сад (плоды)", "Ягодники (ягоды)", "Орехоплодные (орехи)",
            "Виноградник", "Питомник (вынос с 1 га)",
            "Древесные насаждения (вынос с 1 га)"
    };

    public void initialize() {
        answerWrapper.setVisible(false);
        for (String culture : culturesList) {
            cultureSelector.getItems().add(culture);
        }
    }

    public void calculate(ActionEvent actionEvent) {
        TextField[] inputs = {
                productivityInput, areaInput,
                plowingDepthInput, soilDensityInput,
                nitrogenInput, phosphorusInput,
                potassiumInput
        };
        ArrayList<Double> numbers = extractNumbers(inputs);
        if(numbers.size() == inputs.length) {
            double soil = calculateSoil(numbers.get(1) * 10000, numbers.get(2), numbers.get(3));
            double nitrogen = customRound(calculateNutrientLeaching(0, numbers.get(0)) -
                    (numbers.get(4) * soil * Math.pow(10, -6) * 0.60));
            double phosphorus = customRound(calculateNutrientLeaching(1, numbers.get(0)) -
                    (numbers.get(5) * soil * Math.pow(10, -6) * 0.40));
            double potassium = customRound(calculateNutrientLeaching(2, numbers.get(0)) -
                    (numbers.get(6) * soil * Math.pow(10, -6) * 0.70));

            answerLabel.setText(
                    "Для указанного поля, с планируемой урожайностью — " + productivityInput.getText() +
                            " тонн с гектара,\nрекомендованная норма удобрений составляет:\n" +
                            "Азота не менее " + (nitrogen < 0 ? 0 : nitrogen) + " кг/га,\n" +
                            "фосфора не менее " + (phosphorus < 0 ? 0 : phosphorus) + " кг/га,\n" +
                            "калия не менее " + (potassium < 0 ? 0 : potassium) + " кг/га."
            );
            answerWrapper.setVisible(true);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatDateTime = LocalDateTime.now().format(formatter);

            new DataBaseConnection().addCalculation(new Calculation(
                    currentUserId, formatDateTime, cultureSelector.getValue(),
                    numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3),
                    numbers.get(4), numbers.get(5), numbers.get(6), (nitrogen < 0 ? 0 : nitrogen),
                    (phosphorus < 0 ? 0 : phosphorus), (potassium < 0 ? 0 : potassium)
            ));
        } else {
            new DialogWindow("CalculationError").showDialog();
        }
    }

    private double calculateNutrientLeaching(int index, double productivity) {
        double norm = new RemovalOfNutrients(culturesList)
                .getRemoval(cultureSelector.getValue(), index);
        return productivity * norm;
    }

    private double customRound(double n) {
        n = n * 100;
        int result = (int) Math.round(n);
        return (double) result / 100;
    }

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

    private double calculateSoil(double area, double plowingDepth, double soilDensity) {
        return customRound(area * plowingDepth * soilDensity * 1000);
    }

    public void goToProfile(ActionEvent actionEvent) throws IOException {
        setScene("profile.fxml");
    }
}
