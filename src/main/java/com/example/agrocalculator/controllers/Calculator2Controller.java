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

public class Calculator2Controller {
    //Объявление всех полей ввода и вывода
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
    //Объявление списка культур
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

    //При загрузке страницы список культур добавится в поле с выбором культуры,
    //а также поле для ответа становится невидимым
    public void initialize() {
        answerWrapper.setVisible(false);
        for (String culture : culturesList) {
            cultureSelector.getItems().add(culture);
        }
    }

    //Расчет, вызывается по соответсующей кнопке
    public void calculate(ActionEvent actionEvent) {
        //Собираем все поля ввода в массив, для удобства
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
            //Расчет удобрений
            double soil = calculateSoil(numbers.get(1) * 10000, numbers.get(2), numbers.get(3));
            double nitrogen = customRound(calculateNutrientLeaching(0, numbers.get(0)) -
                    (numbers.get(4) * soil * Math.pow(10, -6) * 0.60));
            double phosphorus = customRound(calculateNutrientLeaching(1, numbers.get(0)) -
                    (numbers.get(5) * soil * Math.pow(10, -6) * 0.40));
            double potassium = customRound(calculateNutrientLeaching(2, numbers.get(0)) -
                    (numbers.get(6) * soil * Math.pow(10, -6) * 0.70));
            //Если удобрения уходят в минус, заменяем на 0
            nitrogen = nitrogen < 0 ? 0 : nitrogen;
            phosphorus = phosphorus < 0 ? 0 : phosphorus;
            potassium = potassium < 0 ? 0 : potassium;
            //Выводим текст с результами  вычислений
            answerLabel.setText(
                    "Для указанного поля, с планируемой урожайностью — " + productivityInput.getText() +
                            " тонн с гектара,\nрекомендованная норма удобрений составляет:\n" +
                            "Азота не менее " + nitrogen + " кг/га,\n" +
                            "фосфора не менее " + phosphorus + " кг/га,\n" +
                            "калия не менее " + potassium + " кг/га."
            );
            //Делаем поле вывода видимым
            answerWrapper.setVisible(true);

            //Создаем "форматтер" для форматирования даты и времени, т. к. в сыром виде, есть лишнее и выглядит не очень
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //Получаем время в данный момент (когда производятся вычисления)
            String formatDateTime = LocalDateTime.now().format(formatter);

            //В лист с числами, который вводил пользователь добавляем результаты вычислений
            numbers.add(nitrogen);
            numbers.add(phosphorus);
            numbers.add(potassium);
            //Добавляем запись в БД
            new DataBaseConnection().addCalculation(new Calculation(
                    currentUserId, formatDateTime, cultureSelector.getValue(),
                    numbers.stream().mapToDouble(d -> d).toArray()
            ));
        } else {
            //Вывод окна с ошибкой
            new DialogWindow("CalculationError").showDialog();
        }
    }

    //Метод для вычесления части формулы
    private double calculateNutrientLeaching(int index, double productivity) {
        double norm = new RemovalOfNutrients(culturesList)
                .getRemoval(cultureSelector.getValue(), index);
        return productivity * norm;
    }

    //Округление до сотой части дроби
    private double customRound(double n) {
        n = n * 100;
        int result = (int) Math.round(n);
        return (double) result / 100;
    }

    //Получение чисел из полей ввода
    private ArrayList<Double> extractNumbers(TextField[] inputs) {
        //Создаем лист
        ArrayList<Double> numbers = new ArrayList<>();
        try {
            //Пробуем запарсить текст с полей ввода в числа, при этом у строки заменям , на .
            //чтобы пользователь мог вводить дробные числа и через . и через ,
            for (TextField input : inputs) {
                numbers.add(Double.parseDouble(input.getText().replaceAll(",", ".")));
            }
        } catch (NumberFormatException ignored) {

        }
        return numbers;
    }

    //Опять же, часть формулы
    private double calculateSoil(double area, double plowingDepth, double soilDensity) {
        return customRound(area * plowingDepth * soilDensity * 1000);
    }

    //Переход на страницу профиля
    public void goToProfile(ActionEvent actionEvent) throws IOException {
        setScene("profile.fxml");
    }

    //Смена на калькулятор культуры
    public void goToOtherCalculator(ActionEvent actionEvent) throws IOException {
        setScene("calculator-view.fxml");
    }
}
