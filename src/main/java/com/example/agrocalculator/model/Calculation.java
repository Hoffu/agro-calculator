package com.example.agrocalculator.model;

public class Calculation {
    private final int userId;
    private final String date;
    private final String culture;
    private final double productivity;
    private final double area;
    private final double plowingDepth;
    private final double soilDensity;
    private final double nitrogen;
    private final double phosphorus;
    private final double potassium;
    private final double nitrogenFertilizer;
    private final double phosphorusFertilizer;
    private final double potassiumFertilizer;

    public Calculation(int userId, String date, String culture, double productivity, double area,
                       double plowingDepth, double soilDensity, double nitrogen, double phosphorus,
                       double potassium, double nitrogenFertilizer, double phosphorusFertilizer,
                       double potassiumFertilizer) {
        this.userId = userId;
        this.date = date;
        this.culture = culture;
        this.productivity = productivity;
        this.area = area;
        this.plowingDepth = plowingDepth;
        this.soilDensity = soilDensity;
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.nitrogenFertilizer = nitrogenFertilizer;
        this.phosphorusFertilizer = phosphorusFertilizer;
        this.potassiumFertilizer = potassiumFertilizer;
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

    public double getProductivity() {
        return productivity;
    }

    public double getArea() {
        return area;
    }

    public double getPlowingDepth() {
        return plowingDepth;
    }

    public double getSoilDensity() {
        return soilDensity;
    }

    public double getNitrogen() {
        return nitrogen;
    }

    public double getPhosphorus() {
        return phosphorus;
    }

    public double getPotassium() {
        return potassium;
    }

    public double getNitrogenFertilizer() {
        return nitrogenFertilizer;
    }

    public double getPhosphorusFertilizer() {
        return phosphorusFertilizer;
    }

    public double getPotassiumFertilizer() {
        return potassiumFertilizer;
    }
}
