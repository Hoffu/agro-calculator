package com.example.agrocalculator.database;

import com.example.agrocalculator.model.Calculation;
import com.example.agrocalculator.model.User;

import java.sql.*;
import java.util.ArrayList;

public class DataBaseConnection {
    //Запросы в БД
    private final String sqlSelectById = "SELECT * FROM agro_calculator.users WHERE id = ";
    private final String sqlInsertNewUser = "INSERT INTO agro_calculator.users(email, phone, name, password)" +
            " VALUES (?, ?, ?, ?)";
    private final String sqlSelectByEmail = "SELECT * FROM agro_calculator.users WHERE email = '";
    private final String sqlInsertNewCalculation = "INSERT INTO agro_calculator.calculations(userId, date," +
            " culture, productivity, area, plowingDepth, soilDensity, nitrogen, phosphorus, potassium, " +
            "nitrogenFertilizer, phosphorusFertilizer, potassiumFertilizer) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final String sqlSelectAllCalculationsOfUser = "SELECT * FROM agro_calculator.calculations WHERE userId = '";
    //Конфигурация БД
    private final String connectionUrl = "jdbc:mysql://localhost:3306/agro_calculator?serverTimezone=UTC";
    private final String username = "root";
    private final String password = "kw2zbtiQ";
    //Айди текущего пользователя
    public static int currentUserId = -1;

    //Добавление пользователя
    public void addUser(User user) {
        try {
            Connection conn = DriverManager.getConnection(connectionUrl, username, password);
            PreparedStatement pstmt = conn.prepareStatement(sqlInsertNewUser, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getPassword());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Получение пользвателя по айди
    public User getUser(int id) {
        User user = new User();
        try {
            Connection conn = DriverManager.getConnection(connectionUrl, username, password);
            PreparedStatement ps = conn.prepareStatement(sqlSelectById + id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    //Получение хэша пароля по мылу
    public String userLogin(String email) {
        String pass = "";
        try {
            Connection conn = DriverManager.getConnection(connectionUrl, username, password);
            PreparedStatement pstmt = conn.prepareStatement(sqlSelectByEmail + email + "'");
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt(1);
                pass = rs.getString("password");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pass;
    }

    //Добавление расчетов
    public void addCalculation(Calculation calculation) {
        try {
            Connection conn = DriverManager.getConnection(connectionUrl, username, password);
            PreparedStatement pstmt = conn.prepareStatement(sqlInsertNewCalculation, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, calculation.getUserId());
            pstmt.setString(2, calculation.getDate());
            pstmt.setString(3, calculation.getCulture());
            for(int i = 0; i < calculation.getCalculations().length; i++) {
                pstmt.setDouble(i + 4, calculation.getCalculations()[i]);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //Получение расчетов по айди пользователя
    public ArrayList<Calculation> getCalculations(int userId, String cultureFiler) {
        String sql = sqlSelectAllCalculationsOfUser + userId + "'";
        if(!cultureFiler.equals("")) {
            sql = sql + " AND culture LIKE '%" + cultureFiler + "%'";
        }
        ArrayList<Calculation> calculations = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(connectionUrl, username, password);
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                calculations.add(new Calculation(
                        rs.getInt("userId"), rs.getString("date"),
                        rs.getString("culture"), new double[]{
                        rs.getDouble("productivity"),
                        rs.getDouble("area"), rs.getDouble("plowingDepth"),
                        rs.getDouble("soilDensity"), rs.getDouble("nitrogen"),
                        rs.getDouble("phosphorus"), rs.getDouble("potassium"),
                        rs.getDouble("nitrogenFertilizer"),
                        rs.getDouble("phosphorusFertilizer"),
                        rs.getDouble("potassiumFertilizer")
                }
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return calculations;
    }
}
