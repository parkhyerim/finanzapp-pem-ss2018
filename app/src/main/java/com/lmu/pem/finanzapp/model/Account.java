package com.lmu.pem.finanzapp.model;

public class Account {
    //TODO - Evtl wäre eine ID sinnvoll, da müsste man sich überlegen wie man die am besten vergibt und speichert
    private String name;
    private int color;
    private boolean isDefault;
    private double balance;
    private static final int DEFAULT_COLOR = 0xff283593;
    //TODO - sinnvolle Konstante für Cash-Account

    public Account(String name) {
        this(name, DEFAULT_COLOR, false);
    }
    public Account(String name, int color) {
        this(name, color, false);
    }
    public Account(String name, boolean isDefault) {
        this(name, DEFAULT_COLOR, isDefault);
    }
    public Account(String name, int color, boolean isDefault) {
        this.name = name;
        this.color = color;
        this.isDefault = isDefault;
        this.balance = 0.0;
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
