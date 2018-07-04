package com.lmu.pem.finanzapp.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.R;

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


        dbRef = FirebaseDatabase.getInstance().getReference().child("settings");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer newCurrency = dataSnapshot.child("currency").getValue(Integer.class);
                if(newCurrency!=null) currency = newCurrency;
                Integer newHomeTab = dataSnapshot.child("homeTab").getValue(Integer.class);
                if(newHomeTab!=null) homeTab = newHomeTab;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("123123123","Cancelled: "+databaseError.toString());
            }
        });
    }

    public int getCurrency() {
        return currency;
    }

    public String getCurrencyString(){
        return context.getResources().getStringArray(R.array.currency_array)[currency];
    }

    public void setCurrency(int currency) {
        this.currency = currency;
        dbRef.child("currency").setValue(currency);
    }

    public int getHomeTab() {
        return homeTab;
    }

    public void setHomeTab(int homeTab) {
        this.homeTab = homeTab;
        dbRef.child("homeTab").setValue(homeTab);
    }
}
