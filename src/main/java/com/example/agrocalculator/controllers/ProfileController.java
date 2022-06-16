package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.Calculation;
import com.example.agrocalculator.model.User;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import javax.print.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.agrocalculator.MainApplication.setScene;
import static com.example.agrocalculator.database.DataBaseConnection.currentUserId;

public class ProfileController {
    //Объявление всех полей ввода и вывода
    public Label name;
    public Label phone;
    public Label email;
    public ComboBox<String> previousCalculations;
    public TextField filterInput;
    public Label culture;
    public Label productivity;
    public Label area;
    public Label plowingDepth;
    public Label soilDensity;
    public Label nitrogen;
    public Label phosphorus;
    public Label potassium;
    public Label nitrogenFertilizer;
    public Label phosphorusFertilizer;
    public Label potassiumFertilizer;
    public Button saveButton;
    public Button printButton;

    //Метод вызовется при рендеринге страницы
    public void initialize() {
        //По айди пользователя, который входил, из БД берутся его данные и ставятся в соответствующие поля
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        User user = dataBaseConnection.getUser(currentUserId);
        name.setText(user.getName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        culture.setText("");

        //Поля вывода для расчетов собираются в массив, для удобства
        Label[] labels = {
                productivity, area, plowingDepth, soilDensity,
                nitrogen, phosphorus, potassium,
                nitrogenFertilizer, phosphorusFertilizer,
                potassiumFertilizer
        };

        //Поля делаются невидимыми
        changeVisibility(labels, false);
        //Кнопки для печати и сохранения в пдф делаются невидимыми
        changeVisibility(new Control[]{saveButton, printButton}, false);

        //Достаем из БД предыдущие вычисления
        ArrayList<Calculation> calculations = setChoiceBoxItems("");
        //Если лист с вычиселниями не пуст, то даем пользователю выбрать дату, иначе скрываем поля ввода и выводим
        //текст о том, что вычислений нет
        if(calculations.size() > 0) {
            //Навешиваем слушатели изменений
            previousCalculations.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> selectChangeHandler(calculations, newValue, labels));
            filterInput.textProperty().addListener((observable, oldValue, newValue) -> {
                setChoiceBoxItems(newValue);
            });
        } else {
            culture.setText("Расчеты ни разу не производились.");
            changeVisibility(new Control[]{previousCalculations, filterInput}, false);
        }
    }

    //Переход к калькулятору
    public void backToCalc(ActionEvent actionEvent) throws IOException {
        setScene("calculator-view.fxml");
    }

    //Смена видимости элементов
    private void changeVisibility(Control[] controls, boolean value) {
        for (Control control : controls) {
            control.setVisible(value);
        }
    }

    //Достаем из БД предыдущие вычисления и заполняем поле с выбором датами вычислений
    private ArrayList<Calculation> setChoiceBoxItems(String cultureFiler) {
        ArrayList<Calculation> calculations = new DataBaseConnection().getCalculations(currentUserId, cultureFiler);
        previousCalculations.getItems().clear();
        previousCalculations.setPromptText("Выберите дату расчета");
        calculations.forEach(calculation -> previousCalculations.getItems().add(calculation.getDate()));
        return calculations;
    }

    //Вызовется при изменении (пользователь выбрал дату)
    //Пройдет по все вычислениям, найдет с выбранной датой, и заполнит поля вывода, а также сделает их видимыми
    //Для последних 3 значений если они не использовались, то вернется -1, тогда эти поля вывода остаются невидимыми
    private void selectChangeHandler(ArrayList<Calculation> calculations, String newValue, Label[] labels) {
        calculations.forEach(calculation -> {
            if(calculation.getDate().equals(newValue)) {
                setValues(calculation, labels);
                changeVisibility(labels, true);
                changeVisibility(new Control[]{saveButton, printButton}, true);
                if (calculation.getCalculations()[calculation.getCalculations().length - 1] == -1.0) {
                    changeVisibility(new Control[]{nitrogenFertilizer, phosphorusFertilizer,
                            potassiumFertilizer}, false);
                }
            }
        });
    }

    //Заполенение полей вывода значениями
    private void setValues(Calculation calculation, Label[] labels) {
        String[] labelTemplates = {
                "Планируемая урожаемость: ",
                "Площадь участка: ", "Глубина вспашки: ",
                "Плотность почвы: ", "Содержание азота: ",
                "Содержание фосфора: ", "Содержания калия: ",
                "Норма азота составляет не менее: ",
                "Норма фосфора составляет не менее: ",
                "Норма калия составляет не менее: "
        };
        String[] units = {
                " тонн с гектара", " га",
                " м", " кг/дм3", " мг/кг",
                " мг/кг", " мг/кг", " кг/га",
                " кг/га", " кг/га"
        };
        double[] calculations = calculation.getCalculations();
        culture.setText("Культура: " + calculation.getCulture());
        for(int i = 0; i < labels.length; i++) {
            labels[i].setText(labelTemplates[i] + calculations[i] + units[i]);
        }
    }

    //Вызовется для распечатки на принтере
    public boolean print(ActionEvent actionEvent) throws FileNotFoundException {
        try {
            //Создаем пдф
            String name = createPDF();
            //Печатаем пдф
            FileInputStream in = new FileInputStream(name);
            Doc doc = new SimpleDoc(in, DocFlavor.INPUT_STREAM.AUTOSENSE, null);
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            service.createPrintJob().print(doc, null);
            File file = new File(name);
            return file.delete();
        } catch (IOException | PrintException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Сохранение в пдф
    public void saveInPDF(ActionEvent actionEvent) {
        try {
            createPDF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Создание пдф файла
    private String createPDF() throws IOException {
        Label[] labels = {
                culture, productivity, area, plowingDepth,
                soilDensity, nitrogen, phosphorus, potassium,
                nitrogenFertilizer, phosphorusFertilizer,
                potassiumFertilizer
        };

        //Задаем имя
        String name = "result.pdf";
        //Создаем документ
        PDDocument document = new PDDocument();
        //Создаем страницу
        PDPage page = new PDPage();
        //Добавляем страницу в документ
        document.addPage(page);

        //Создаем поток
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        //Устанавливваем шрифт, его размер и т.д.
        PDFont font = PDType0Font.load(document, new File("Times New Roman.ttf"));
        contentStream.setFont(font, 14);
        contentStream.beginText();
        contentStream.setLeading(14.5f);

        //Устанавливаем начало первой строки
        contentStream.newLineAtOffset(50,750);
        //Записываем на каждую строку текст с полей вывода
        for (Label label : labels) {
            String text = label.getText();
            if(!text.endsWith("-1.0 кг/га")) {
                contentStream.showText(text);
                contentStream.newLine();
            }
        }

        //Закрываем поток
        contentStream.endText();
        contentStream.close();

        //Сохраняем документ с заданным именем
        document.save(name);
        //Закрвыаем документ
        document.close();
        //Возвращаем имя документа
        return name;
    }
}
