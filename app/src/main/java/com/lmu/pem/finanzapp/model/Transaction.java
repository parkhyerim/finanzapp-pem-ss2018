package com.lmu.pem.finanzapp.model;

import android.support.annotation.NonNull;

public class Transaction implements Comparable<Transaction> {
    private double expense;
    private double income;
    private double amount;

    private String category;
    private String account;
    private String descrition;

    private String date;
    private int imageResource;
    private int id;


    public Transaction(String date, int imageResource, String account, String category, String description, double expense, double income) {

        this.date = date;
        this.imageResource = imageResource;
        this.account = account;
        this.category = category;
        this.descrition = description;
        this.expense = expense;
        this.income = income;

    }


    public Transaction(int imageResource, String category, String account, double expense, double income, double amount) {

        this.imageResource = imageResource;
        this.category = category;
        this.account = account;
        this.expense = expense;
        this.income = income;
        this.amount = amount;

    }

    public Transaction(String category, String account, double expense, double income, double amount) {
        this.category = category;
        this.account = account;
        this.expense = expense;
        this.income = income;
        this.amount = amount;
    }

    public Transaction(int imageResource, String account, String category, double expense, String date){
        this.imageResource = imageResource;
        this.account = account;
        this.category = category;
        this.expense = expense;
        this.date = date;
    }



    public String getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

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

    public String getDescrition() {
        return descrition;
    }

    public int getId() { return id; }

    public double addExpense(double expense){
        amount -= expense;
        return amount;
    }

    public double addIncome(double income) {
        amount += income;
        return amount;
    }

    @Override
    public int compareTo(@NonNull Transaction transaction) {

        return getDate().toString()
                .compareTo(transaction.getDate().toString());

        /*
        return getCategory().toString()
                            .compareTo(o.getCategory().toString());

                            */
    }
}
