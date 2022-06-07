package com.example.agrocalculator.database;

import com.example.agrocalculator.model.User;

import java.sql.*;

public class DataBaseConnection {
    private final String sqlSelectById = "SELECT * FROM agro_calculator.users WHERE id = ";
    private final String sqlInsertNewUser = "INSERT INTO agro_calculator.users(email, phone, name, password)" +
            " VALUES (?, ?, ?, ?)";
    private final String sqlSelectByEmail = "SELECT * FROM agro_calculator.users WHERE email = '";
    private final String connectionUrl = "jdbc:mysql://localhost:3306/agro_calculator?serverTimezone=UTC";
    private final String username = "root";
    private final String password = "kw2zbtiQ";
    public static int currentUserId = -1;

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

    public User getUser(int id) {
        User user = new User();
        try {
            Connection conn = DriverManager.getConnection(connectionUrl, username, password);
            PreparedStatement ps = conn.prepareStatement(sqlSelectById + id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

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
}
