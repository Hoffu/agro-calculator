package com.example.agrocalculator.model;

//Вспомогательный класс с расчетами
public class Calculation {
    private final int userId;
    private final String date;
    private final String culture;
    private final double[] calculations;

    public Calculation(int userId, String date, String culture, double[] calculations) {
        this.userId = userId;
        this.date = date;
        this.culture = culture;
        this.calculations = calculations;
    }

    public int getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getCulture() {
        return culture;
    }

    public double[] getCalculations() {
        return calculations;
    }
}
