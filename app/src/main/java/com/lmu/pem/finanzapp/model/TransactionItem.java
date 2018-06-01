package com.lmu.pem.finanzapp.model;

public class TransactionItem {
    private int mImageResource;
    private String accountName;
    private String categoryName;
    private String mAmount;

    public TransactionItem(int imageResource, String account, String category, String amount){
        mImageResource = imageResource;
        accountName = account;
        categoryName = category;
        mAmount = amount;
    }



    public int getImageResource() {
        return mImageResource;
    }

    public String getAmount() {
        return mAmount;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}

