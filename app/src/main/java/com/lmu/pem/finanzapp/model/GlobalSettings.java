package com.lmu.pem.finanzapp.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class GlobalSettings {

    private static GlobalSettings instance;
    private Context context;
    private DatabaseReference dbRef;

    public final static int TAB_DASHBOARD = 0;
    public final static int TAB_TRANSACTIONS = 1;
    public final static int TAB_ACCOUNTS = 2;
    public final static int TAB_BUDGETS = 3;

    public final static int CURRENCY_EURO = 0;
    public final static int CURRENCY_DOLLAR = 1;
    public final static int CURRENCY_POUND = 2;

    public final static SimpleDateFormat GLOBAL_DATE_FORMAT = new SimpleDateFormat("MM/dd/yy", Locale.getDefault());


    private int currency;
    private int homeTab;

    public static GlobalSettings getInstance() {
        if (instance == null) instance = new GlobalSettings();
        return instance;
    }

    public GlobalSettings() {
        reset();
    }

    public void reset() {
        this.currency = CURRENCY_EURO;
        this.homeTab = TAB_DASHBOARD;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("settings");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getCurrency() {
        return currency;
    }

    public String getCurrencyString(){
        return context.getResources().getStringArray(R.array.currency_array)[currency];
    }

    public void setCurrency(int currency) {
        this.currency = currency;
        dbRef.child("currency").child("value").setValue(currency);
    }

    public int getHomeTab() {
        return homeTab;
    }

    public void setHomeTab(int homeTab) {
        this.homeTab = homeTab;
        dbRef.child("homeTab").child("value").setValue(homeTab);
    }
}
