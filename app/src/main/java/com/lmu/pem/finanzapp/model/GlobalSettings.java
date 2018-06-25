package com.lmu.pem.finanzapp.model;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.lmu.pem.finanzapp.R;

public class GlobalSettings {

    private static GlobalSettings instance;
    private Context context;
    public final static int TAB_DASHBOARD = 0;
    public final static int TAB_TRANSACTIONS = 1;
    public final static int TAB_ACCOUNTS = 2;
    public final static int TAB_BUDGETS = 3;

    public final static int CURRENCY_EURO = 0;
    public final static int CURRENCY_DOLLAR = 1;
    public final static int CURRENCY_POUND = 2;

    private int currency;
    private int homeTab;

    public static GlobalSettings getInstance(Context context) {
        if (instance == null) instance = new GlobalSettings(context);
        return instance;
    }

    public GlobalSettings(Context context) {
        this.context = context;
        this.currency = CURRENCY_EURO;
        this.homeTab = TAB_DASHBOARD;
    }

    public int getCurrency() {
        return currency;
    }

    public String getCurrencyString(){
        return context.getResources().getStringArray(R.array.currency_array)[currency];
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public int getHomeTab() {
        return homeTab;
    }

    public void setHomeTab(int homeTab) {
        this.homeTab = homeTab;
    }
}
