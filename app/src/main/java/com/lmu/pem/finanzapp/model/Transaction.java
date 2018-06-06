package com.lmu.pem.finanzapp.model;

import android.support.annotation.NonNull;

public class Transaction implements Comparable<Transaction> {

    private double expense;
    private double income;
    private double amount;

    private String category;
    private String account;
    private String description;
    private String date;

    private int imageResource;


    public Transaction(String date, int imageResource, String account, String category, String description, double expense, double income) {

        this.date = date;
        this.imageResource = imageResource;
        this.account = account;
        this.category = category;
        this.description = description;
        this.expense = expense;
        this.income = income;

    }



    public String getAccount() {
        return account;
    }

    public double getAmount() { return amount; }

    public double getExpense() {
        return expense;
    }

    public double getIncome() {
        return income;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }


    public double addExpense(double expense){
        amount -= expense;
        return amount;
    }

    public double addIncome(double income) {
        amount += income;
        return amount;
    }



    // for date header
    @Override
    public int compareTo(@NonNull Transaction transaction) {

        return getDate().toString()
                .compareTo(transaction.getDate().toString());

    }
}
