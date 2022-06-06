package com.example.agrocalculator.model;

import java.util.HashMap;
import java.util.Map;

public class RemovalOfNutrients {
    private final Map<String, double[]> listOfNutrients = new HashMap<>();
    private final double[][] removals = {
            new double[]{33.0, 10.4, 21.7},
            new double[]{27.3, 10.4, 23.6},
            new double[]{31.3, 12.5, 26.9},
            new double[]{33.6, 14.8, 32.8},
            new double[]{28.1,  9.1, 23.6},
            new double[]{60.3, 14.9, 27.2},
            new double[]{36.8, 13.8, 17.2},
            new double[]{74.5, 18.9, 26.9},
            new double[]{54.3, 14.4, 18.4},
            new double[]{52.7, 19.6, 100.6},
            new double[]{51.9, 13.6, 26.1},
            new double[]{41.1, 19.3, 27.0},
            new double[]{54.7, 10.3, 31.0},
            new double[]{44.0, 16.7, 54.1},
            new double[]{ 4.9,  1.6,  6.3},
            new double[]{39.1, 17.0, 52.1},
            new double[]{ 5.7,  1.7,  7.6},
            new double[]{ 5.0,  0.6,  5.8},
            new double[]{ 4.4,  1.4,  4.6},
            new double[]{ 3.2,  1.0,  5.0},
            new double[]{ 2.7,  1.5,  4.3},
            new double[]{ 1.7,  1.4,  2.6},
            new double[]{ 3.7,  1.2,  4.0},
            new double[]{ 3.2,  1.5,  5.7},
            new double[]{ 3.7,  1.1,  3.5},
            new double[]{ 3.0,  0.8,  6.0},
            new double[]{ 4.5,  1.2,  4.0},
            new double[]{26.3,  6.2, 20.2},
            new double[]{20.2,  6.2, 17.3},
            new double[]{ 5.8,  2.4,  5.8},
            new double[]{ 5.0,  1.6,  5.2},
            new double[]{25.0,  4.0,  5.2},
            new double[]{ 7.1,  3.3,  9.0},
            new double[]{42.2, 16.1, 23.9},
            new double[]{32.1,  6.4, 24.2}
    };

    public RemovalOfNutrients(String[] cultures) {
        for(int i = 0; i < cultures.length; i++) {
            listOfNutrients.put(cultures[i], removals[i]);
        }
    }

    public double getRemoval(String culture, int nutrient) {
        if(listOfNutrients.containsKey(culture)) {
            return listOfNutrients.get(culture)[nutrient];
        }
        return 0;
    }
}
