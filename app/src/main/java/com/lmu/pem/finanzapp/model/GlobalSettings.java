package com.lmu.pem.finanzapp.model;

public class GlobalSettings {

    private static GlobalSettings instance;
    public final static int TAB_DASHBOARD = 0;
    public final static int TAB_TRANSACTIONS = 1;
    public final static int TAB_ACCOUNTS = 2;
    public final static int TAB_BUDGETS = 3;

    private String currency = "€";
    private int homeTab = TAB_DASHBOARD;

    public static GlobalSettings getInstance() {
        if (instance == null) instance = new GlobalSettings();
        return instance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getHomeTab() {
        return homeTab;
    }

    public void setHomeTab(int homeTab) {
        this.homeTab = homeTab;
    }
}
