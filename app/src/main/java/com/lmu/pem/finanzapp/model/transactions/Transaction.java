package com.lmu.pem.finanzapp.model.transactions;

import android.support.annotation.NonNull;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Transaction implements Comparable<Transaction> {

    public static final String CATEGORY_SHIFT = "Shift";
    private String category;
    private String account, account2;
    private String description;
    private int imageResource = 0;
    private double amount;

    private int year, month, day;

    private String key;

    private Map<String, Transaction> trns = new HashMap<>();


    public Transaction(){
        // Default constructor required for calls to DataSnapshot.getValue(Transaction.class)
    }

    public Transaction(int year, int month, int day, int imageResource, String account, String category, String description, double amount) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.imageResource = imageResource;
        this.account = account;
        this.account2 = null;
        this.category = category;
        this.description = description;
        this.amount = amount;
        //key will be set later when it's created by Firebase
    }

    public Transaction(int year, int month, int day, int imageResource, String account, String account2, String category, String description, double amount) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.imageResource = imageResource;
        this.account = account; //from
        this.account2 = account2; //to
        this.category = category;
        this.description = description;
        this.amount = amount;
        //key will be set later when it's created by Firebase
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

    public double getAmount() { return amount; }

    public String getAccount2() {
        return account2;
    }

    public void setAccount2(String account2) {
        this.account2 = account2;
    }

    public int getDay() { return day; }

    public int getMonth() { return month; }

    public int getYear() { return year;    }

    public String getDescription() {
        return description;
    }

    public String getKey() { return key; }

    public void setKey(String key){ this.key = key; }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    // for date header
    @Override
    public int compareTo(@NonNull Transaction transaction) {
        Date date1 = new Date(this.year, this.month, this.day);
        Date date2 = new Date(transaction.getYear(), transaction.getMonth(), transaction.getDay());
        return date1.compareTo(date2);
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("day", day);
        result.put("description", description);
        result.put("category", category);
        result.put("account", account);
        result.put("account2", account2);
        result.put("amount", amount);
        result.put("imageResource", imageResource);

        return result;
    }
}
