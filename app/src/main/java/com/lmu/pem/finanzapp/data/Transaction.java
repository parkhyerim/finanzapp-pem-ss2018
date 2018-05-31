package com.lmu.pem.finanzapp.data;

public class Transaction {
    double mExpense;
    double mIncome;
    double mAmount;
    String mCategory;
    String mAccount;
    String mItem;

    public Transaction(String category, String account, double expense, double income, double amount) {
        mCategory = category;
        mAccount = account;
        mExpense = expense;
        mIncome = income;
        mAmount = amount;
    }

    public String getAccount() {
        return mAccount;
    }

    public double getAmount() {
        return mAmount;
    }

    public double addExpense(double expense){
        mAmount -= expense;
        return mAmount;
    }

    public double addIncome(double income) {
        mAmount += income;
        return mAmount;
    }
}
