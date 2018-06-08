package com.lmu.pem.finanzapp.model;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.controller.AccountAdapter;
import com.lmu.pem.finanzapp.data.Account;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountManager {

    private static AccountManager instance;

    private ArrayList<Account> accounts;
    private Account defaultAcc;

    private AccountManager() {
        this.accounts = new ArrayList<Account>();
        //this.accounts.add(new Account("Cash", 0xff00695c, true, 64.45)); //TODO - temporary
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("accounts");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> map = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                for (String key : map.keySet()) {

                    Account newAcc=new Account(
                            dataSnapshot.child(key).child("name").getValue(String.class),
                            dataSnapshot.child(key).child("color").getValue(Integer.class),
                            dataSnapshot.child(key).child("isDefault").getValue(Boolean.class),
                            dataSnapshot.child(key).child("balance").getValue(Double.class)
                    );
                    if((boolean) map.get(key).get("isDefault")) defaultAcc=newAcc;
                    accounts.add(newAcc);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("123123123","Cancelled: "+databaseError.toString());
            }
        });
    }

    public static AccountManager getInstance () {
        if (instance == null) instance = new AccountManager();
        return instance;
    }

    //also to be used for changing attributes of a given account
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account acc){
        this.accounts.add(acc);
        if(acc.isDefault()){
            this.defaultAcc.setDefault(false);
            this.defaultAcc = acc;
        }
    }

    public void deleteAccount(int index){
        this.accounts.remove(index);
    }

    public Account getAccountById(String id){
        for(Account acc : accounts){
            if(acc.getId().equals(id)){
                return acc;
            }
        }
        return null;
    }

    public boolean isNameTaken(String name){
        for(Account acc : accounts){
            if(acc.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public Account getDefault(){
        return defaultAcc;
    }
}