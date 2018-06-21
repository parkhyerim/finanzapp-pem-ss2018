package com.lmu.pem.finanzapp.model.transactions;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Transaction implements Comparable<Transaction> {

    private double expense;
    private double income;
    private double amount;
    private double money;

    private String category;
    private String account;
    private String description;
    private String date;

    private int imageResource;
    private int year, month, day;


    public Transaction(String date, int imageResource, String account, String category, String description, double expense, double income) {
        this.date = date;
        this.imageResource = imageResource;
        this.account = account;
        this.category = category;
        this.description = description;
        this.expense = expense;
        this.income = income;
    }

    public Transaction(int month, int day, int year, int imageResource, String account, String category, String description, double expense, double income) {
        this.year = year;
        this.month = month;
        this.day = day;
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

    public int getDay() { return day; }

    public int getMonth() { return month; }

    public int getYear() { return year;    }

    public String getDescription() {
        return description;
    }

    public double getMoney() {
        if(getExpense() > getIncome()){
            money = getExpense();
        } else {
            money = getIncome();
        }
        return money;
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("transactionDate", date);
        result.put("expense", expense);
        result.put("income", income);
        result.put("description", description);
        result.put("category", category);
        result.put("account", account);
        result.put("imageResource", imageResource);
        return result;
    }
}
