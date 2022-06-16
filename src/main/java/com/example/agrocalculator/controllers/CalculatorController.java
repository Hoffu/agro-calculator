package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.Calculation;
import com.example.agrocalculator.model.DialogWindow;
import com.example.agrocalculator.model.RemovalOfNutrients;
import javafx.event.ActionEvent;
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
        //Извлекаем числа из полей ввода с помощью кастомного метода
        ArrayList<Double> numbers = extractNumbers(inputs);
        //Проверяем, что количество полученных чисел совпадает с количеством полей ввода, иначе выдаем ошибку
        if(numbers.size() == inputs.length) {
            //Расчитываем какую культуру можно посадить
            double soil = calculateSoil(numbers.get(1) * 10000, numbers.get(2), numbers.get(3));
            double nitrogen = customRound((numbers.get(4) * soil * Math.pow(10, -6) * 0.60) / numbers.get(0));
            double phosphorus = customRound((numbers.get(5) * soil * Math.pow(10, -6) * 0.40) / numbers.get(0));
            double potassium = customRound((numbers.get(6) * soil * Math.pow(10, -6) * 0.70) / numbers.get(0));
            String culture = new RemovalOfNutrients(culturesList).getCulture(new double[]{nitrogen, phosphorus, potassium});

            //Выводим текст с результами  вычислений и делаем поле вывода видимым
            answerLabel.setText("При указанных параметрах почвы следует посадить\n" + culture);
            answerWrapper.setVisible(true);

            //Создаем "форматтер" для форматирования даты и времени, т. к. в сыром виде, есть лишнее и выглядит не очень
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //Получаем время в данный момент (когда производятся вычисления)
            String formatDateTime = LocalDateTime.now().format(formatter);

            //Забиваем в массив три значения по -1, чтобы в БД в неиспользуемых полях хранились -1
            for(int i = 0; i < 3; i++)
                numbers.add(-1.0);

            new DataBaseConnection().addCalculation(new Calculation(
                    currentUserId, formatDateTime, culture,
                    numbers.stream().mapToDouble(d -> d).toArray()
            ));
        } else {
            new DialogWindow("CalculationError").showDialog();
        }
    }

    //Метод для вычесления части формулы
    private double calculateSoil(double area, double plowingDepth, double soilDensity) {
        return customRound(area * plowingDepth * soilDensity * 1000);
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
