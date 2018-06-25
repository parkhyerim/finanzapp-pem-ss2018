package com.lmu.pem.finanzapp.model.transactions;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Transaction implements Comparable<Transaction> {

    private String category;
    private String account;
    private String description;
    private int imageResource;

    private double amount;
    private double balance;

    private String date;
    private int year, month, day;


    public Transaction(String date, int imageResource, String account, String category, String description, double amount) {
        this.date = date;
        this.imageResource = imageResource;
        this.account = account;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public Transaction(int month, int day, int year, int imageResource, String account, String category, String description, double amount) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.imageResource = imageResource;
        this.account = account;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }


    public int getImageResource() {
        return imageResource;
    }

    public String getCategory() {
        return category;
    }

    public String getAccount() {
        return account;
    }

    public String getDate() {
        return date;
    }

    public double getAmount() { return amount; }

    public double getBalance() { return balance; }


    public int getDay() { return day; }

    public int getMonth() { return month; }

    public int getYear() { return year;    }

    public String getDescription() {
        return description;
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
        result.put("date", date);
        result.put("description", description);
        result.put("category", category);
        result.put("account", account);
        result.put("amount", amount);
        result.put("imageResource", imageResource);

        return result;
    }

}
