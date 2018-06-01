package com.lmu.pem.finanzapp.model;

public class Account {
    private String name;
    private int color, id; //TODO - ID muss sinnvoll gesetzt und ggf. gespeichert werden
    private boolean isDefault;
    private double balance;
    private static final int DEFAULT_COLOR = 0xff283593;
    //TODO - sinnvolle Konstante für Cash-Account

    public Account(String name) {
        this(name, DEFAULT_COLOR);
    }
    public Account(String name, double balance) {
        this(name, DEFAULT_COLOR, false, balance);
    }
    public Account(String name, int color) {
        this(name, color, false, 0.0);
    }
    public Account(String name, int color, double balance) {
        this(name, color, false, balance);
    }
    public Account(String name, int color, boolean isDefault, double balance) {
        this.name = name;
        this.color = color; //TODO - integrate array of (10?) default colors
        this.isDefault = isDefault;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
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
