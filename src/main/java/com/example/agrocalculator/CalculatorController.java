package com.example.agrocalculator;

import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

import static com.example.agrocalculator.MainApplication.setScene;

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
        double fertilizerDoseOfNitrogen = customRound(calculateNutrientLeaching(0) -
                calculateSupply(nitrogenInput.getText().replaceAll(",", ".")) * 0.60
        );
        double fertilizerDoseOfPhosphorus = customRound(calculateNutrientLeaching(1) -
                calculateSupply(phosphorusInput.getText().replaceAll(",", ".")) * 0.40
        );
        double fertilizerDoseOfPotassium = customRound(calculateNutrientLeaching(2) -
                calculateSupply(potassiumInput.getText().replaceAll(",", ".")) * 0.70
        );
        answerLabel.setText(
                "Для указанного поля, с планируемой урожайностью — " +
                productivityInput.getText().replaceAll(",", ".") +
                " тонн с гектара,\nрекомендованная норма удобрений составляет:\n" +
                "Азота не менее " + fertilizerDoseOfNitrogen + " кг/га,\n" +
                "фосфора не менее " + fertilizerDoseOfPhosphorus + " кг/га,\n" +
                "калия не менее " + fertilizerDoseOfPotassium + " кг/га."
        );
        answerWrapper.setVisible(true);
    }

    private double calculateNutrientLeaching(int index) {
        double nutrientLeaching = 0;
        try {
            double productivity = Double.parseDouble(productivityInput
                    .getText().replaceAll(",", "."));
            double norm = new RemovalOfNutrients(culturesList)
                    .getRemoval(cultureSelector.getValue(), index);
            nutrientLeaching = productivity * norm;
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
        return nutrientLeaching;
    }

    private double calculateSupply(String inputText) {
        double supply = 0;
        try {
            double contain = Double.parseDouble(inputText);
            supply = contain * calculateSoil() * Math.pow(10, -6);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
        return supply;
    }

    private double customRound(double n) {
        n = n * 100;
        int result = (int) Math.round(n);
        return (double) result / 100;
    }

    private double calculateSoil() {
        double soil = 0;
        try {
            double area = Double.parseDouble(areaInput.getText().replaceAll(",", ".")) * 10000;
            double plowingDepth = Double.parseDouble(plowingDepthInput.getText().replaceAll(",", "."));
            double soilDensity = Double.parseDouble(soilDensityInput.getText().replaceAll(",", "."));
            soil = customRound(area * plowingDepth * soilDensity * 1000);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }
        return soil;
    }

    public void goToProfile(ActionEvent actionEvent) throws IOException {
        setScene("profile.fxml");
    }
}
