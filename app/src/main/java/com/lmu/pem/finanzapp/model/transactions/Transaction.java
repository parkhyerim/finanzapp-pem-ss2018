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

    private String date;
    private int year, month, day;

    private String key;

    private Map<String, Transaction> trns = new HashMap<>();


    public Transaction(){
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

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

    public Transaction(String key, String date, int imageResource, String account, String category, String description, double amount) {
        this.key = key;
        this.date = date;
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


    public int getDay() { return day; }

    public int getMonth() { return month; }

    public int getYear() { return year;    }

    public String getDescription() {
        return description;
    }

    public String getKey() { return key; }

    public void setTransactionKey(String key){ this.key = key; }

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

    public void setTransaction(Transaction newT) {
        Transaction transaction = new Transaction(newT.getDate(), newT.getImageResource(), newT.getAccount(), newT.getCategory(), newT.getDescription(), newT.getAmount());
    }
}
