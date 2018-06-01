package com.lmu.pem.finanzapp.model;

public class Transaction {
    private double expense;
    private double income;
    private double amount;

    private String category;
    private String account;
    private String item;

    private int imageResource;

    public Transaction( int imageResource, String category, String account, double expense, double income, double amount) {

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

    public Transaction(int imageResource, String account, String category, double expense){
        this.imageResource = imageResource;
        this.account = account;
        this.category = category;
        this.expense = expense;
    }



    public String getAccount() {
        return account;
    }

    public double getAmount() {
        return amount;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getCategory() {
        return category;
    }


    public double addExpense(double expense){
        amount -= expense;
        return amount;
    }

    public double addIncome(double income) {
        amount += income;
        return amount;
    }
}
