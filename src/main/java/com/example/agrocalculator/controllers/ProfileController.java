package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.Calculation;
import com.example.agrocalculator.model.User;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.print.*;
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
    public ChoiceBox<String> previousCalculations;
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
    public TextField filterInput;

    //Метод вызовется при рендеринге страницы
    public void initialize() {
        //По айди пользователя, который входил, из БД берутся его данные и ставятся в соответствующие поля
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        User user = dataBaseConnection.getUser(currentUserId);
        name.setText(user.getName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        culture.setText("Выберите дату расчета");

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
        ArrayList<Calculation> calculations = dataBaseConnection.getCalculations(currentUserId);
        //Если лист с вычиселниями не пуст, то даем пользователю выбрать дату, иначе скрываем поля ввода и выводим
        //текст о том, что вычислений нет
        if(calculations.size() > 0) {
            //Заполняем поле с выбором датами вычислений
            calculations.forEach(calculation -> previousCalculations.getItems().add(calculation.getDate()));
            //Навешиваем слушателя изменений
            previousCalculations.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        //Вызовется при изменении (пользователь выбрал дату)
                        //Пройдет по все вычислениям, найдет с выбранной датой, и заполнит поля вывода,
                        //а также сделает их видимыми
                        calculations.forEach(calculation -> {
                            if(calculation.getDate().equals(newValue)) {
                                setValues(calculation, labels);
                                changeVisibility(labels, true);
                                changeVisibility(new Control[]{saveButton, printButton}, true);
                            }
                        });
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
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
        contentStream.beginText();
        contentStream.setLeading(14.5f);

        //Устанавливаем начало первой строки
        contentStream.newLineAtOffset(50,750);
        String line1 = "China said its giant Sky Eye telescope may have";
        String line2 = "picked up signs of alien civilizations, according to a report ";
        String line3 = "by the state-backed Science and Technology Daily, which then appeared";
        String line4 = "to have deleted the report and posts about the discovery.";
        contentStream.showText(line1);
        contentStream.newLine();
        contentStream.showText(line2);
        contentStream.newLine();
        contentStream.showText(line3);
        contentStream.newLine();
        contentStream.showText(line4);

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
