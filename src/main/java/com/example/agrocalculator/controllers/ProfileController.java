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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static com.example.agrocalculator.MainApplication.setScene;
import static com.example.agrocalculator.database.DataBaseConnection.currentUserId;

public class ProfileController {
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

    public void initialize() {
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        User user = dataBaseConnection.getUser(currentUserId);
        name.setText(user.getName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        culture.setText("Выберите дату расчета");

        Label[] labels = {
                productivity, area, plowingDepth, soilDensity,
                nitrogen, phosphorus, potassium,
                nitrogenFertilizer, phosphorusFertilizer,
                potassiumFertilizer
        };

        changeVisibility(labels, false);
        changeVisibility(new Control[]{saveButton, printButton}, false);

        ArrayList<Calculation> calculations = dataBaseConnection.getCalculations(currentUserId);
        if(calculations.size() > 0) {
            calculations.forEach(calculation -> previousCalculations.getItems().add(calculation.getDate()));
            previousCalculations.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
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

    public void backToCalc(ActionEvent actionEvent) throws IOException {
        setScene("calculator-view.fxml");
    }

    private void changeVisibility(Control[] controls, boolean value) {
        for (Control control : controls) {
            control.setVisible(value);
        }
    }

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

    public boolean print(ActionEvent actionEvent) throws FileNotFoundException {
        try {
            String name = createPDF();
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

    public void saveInPDF(ActionEvent actionEvent) {
        try {
            createPDF();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createPDF() throws IOException {
        String name = "result.pdf";
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
        contentStream.beginText();
        contentStream.setLeading(14.5f);

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

        contentStream.endText();
        contentStream.close();

        document.save(name);
        document.close();
        return name;
    }
}
