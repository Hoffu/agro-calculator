package com.example.agrocalculator.model;

//Вспомогательный класс с пинффой о пользователе
public class User {
    private String email;
    private String phone;
    private String name;
    private String password;

    public User(String email, String phone, String name, String password) {
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.password = password;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
