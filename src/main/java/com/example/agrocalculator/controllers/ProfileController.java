package com.example.agrocalculator.controllers;

import com.example.agrocalculator.database.DataBaseConnection;
import com.example.agrocalculator.model.Calculation;
import com.example.agrocalculator.model.User;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;

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

    public void initialize() {
        DataBaseConnection dataBaseConnection = new DataBaseConnection();
        User user = dataBaseConnection.getUser(currentUserId);
        name.setText(user.getName());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());

        changeVisibility(new Control[]{
                culture, area, plowingDepth, soilDensity,
                nitrogen, phosphorus, potassium,
                nitrogenFertilizer, phosphorusFertilizer,
                potassiumFertilizer
        }, false);

        ArrayList<Calculation> calculations = dataBaseConnection.getCalculations(currentUserId);
        if(calculations.size() > 0) {
            calculations.forEach(calculation -> previousCalculations.getItems().add(calculation.getDate()));
            previousCalculations.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        calculations.forEach(calculation -> {
                            if(calculation.getDate().equals(newValue)) {
                                setValues(calculation);
                                changeVisibility(new Control[]{
                                        previousCalculations, culture, area, plowingDepth,
                                        soilDensity, nitrogen, phosphorus, potassium,
                                        nitrogenFertilizer, phosphorusFertilizer,
                                        potassiumFertilizer
                                }, true);
                            }
                        });
                    });
        } else {
            productivity.setText("Расчеты ни разу не производились.");
            changeVisibility(new Control[]{previousCalculations}, false);
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

    private void setValues(Calculation calculation) {
        culture.setText("Культура: " + calculation.getCulture());
        productivity.setText("Планируемая урожаемость: "
                + calculation.getCalculations()[0] + " тонн с гектара");
        area.setText("Площадт участка: " + calculation.getCalculations()[1] + " га");
        plowingDepth.setText("Глубина вспашки: " + calculation.getCalculations()[2] + " м");
        soilDensity.setText("Плотность почвы: " + calculation.getCalculations()[3] + " кг/дм3");
        nitrogen.setText("Содержание азота: " + calculation.getCalculations()[4] + " мг/кг");
        phosphorus.setText("Содержание фосфора: " + calculation.getCalculations()[5] + " мг/кг");
        potassium.setText("Содержания калия: " + calculation.getCalculations()[6] + " мг/кг");
        nitrogenFertilizer.setText("Норма азота составляет не менее: "
                + calculation.getCalculations()[7] + " кг/га");
        phosphorusFertilizer.setText("Норма фосфора составляет не менее: "
                + calculation.getCalculations()[8] + " кг/га");
        potassiumFertilizer.setText("Норма калия составляет не менее: "
                + calculation.getCalculations()[9] + " кг/га");
    }
}
