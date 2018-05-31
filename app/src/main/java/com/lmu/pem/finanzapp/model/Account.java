package com.lmu.pem.finanzapp.model;

public class Account {
    //TODO - Evtl wäre eine ID sinnvoll, da müsste man sich überlegen wie man die am besten vergibt und speichert
    private String name, color;
    private boolean isDefault;
    private double balance;
    //TODO - sinnvolle Konstante für Cash-Account

    public Account(String name) {
        this(name, "#283593", false);
    }
    public Account(String name, boolean isDefault) {
        this(name, "#283593", isDefault);
    }
    public Account(String name, String color, boolean isDefault) {
        this.name = name;
        this.color = "#283593";
        this.isDefault = isDefault;
        this.balance = 0.0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
