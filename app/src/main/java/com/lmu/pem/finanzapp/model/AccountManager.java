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
    private DatabaseReference dbRef;

    private AccountManager() {
        this.accounts = new ArrayList<Account>();
        //this.accounts.add(new Account("Cash", 0xff00695c, true, 64.45)); //TODO - temporary
        dbRef = FirebaseDatabase.getInstance().getReference().child("accounts");

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> map = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                for (String key : map.keySet()) {

                    Account newAcc=new Account(
                            dataSnapshot.child(key).child("name").getValue(String.class),
                            dataSnapshot.child(key).child("color").getValue(Integer.class),
                            dataSnapshot.child(key).child("isDefault").getValue(Boolean.class),
                            dataSnapshot.child(key).child("balance").getValue(Double.class),
                            dataSnapshot.child(key).child("id").getValue(String.class)
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

    /**
     * Get the AccountManager (Singleton implementation). If there already is one, return this instance. If there isn't, create a new one and return it.
     * @return the AccountManager instance
     */
    public static AccountManager getInstance () {
        if (instance == null) instance = new AccountManager();
        return instance;
    }

    /**
     * Get the Accounts this AccountManager manages.
     * @return an unordered ArrayList containing all the Accounts
     */
    //also to be used for changing attributes of a given account
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    /**
     * Get an array with all accounts' names of the AccountManager
     * @return a String array with the accounts' names, the default account's name being the first.
     */
    public String[] getNameArray(){
        String[] result = new String[accounts.size()];
        ArrayList<String> list=new ArrayList<String>(accounts.size());
        list.add(defaultAcc.getName());
        for(Account a : accounts){
            if(!a.equals(defaultAcc)){
                list.add(a.getName());
            }
        }
        return list.toArray(result);
    }

    /**
     * Add an Account to the AccountManager
     * @param acc The Account object to be added
     */
    public void addAccount(Account acc){
        this.accounts.add(acc);
        if(acc.isDefault()) setDefaultAcc(acc);
    }

    /**
     * Remove an account from this AccountManager
     * @param index The index of the Account to be removed
     */
    public void deleteAccount(int index){ //TODO: pass ID and figure out index automatically
        this.accounts.remove(index);
    }

    /**
     * Get the Account object with the specified Account ID
     * @param id The ID String of the desired Account
     * @return The Account object with the passed ID. If there is none matching the ID, null is returned
     */
    public Account getAccountById(String id){
        for(Account acc : accounts){
            if(acc.getId().equals(id)){
                return acc;
            }
        }
        return null;
    }

    /**
     * Get the ID of the Account that has the specified name. Since same-name Accounts are prohibited, an Account's name is unique.
     * @param name the name of the Account we're looking for
     * @return either the found ID or null if the name matched none of the account
     */
    public String getAccountIdByName(String name){
        for(Account acc : accounts){
            if(acc.getName().equals(name)){
                return acc.getId();
            }
        }
        return null;
    }

    /**
     * Checks if the AccountManager already manages an Account with the specified name
     * @param name the name to check by
     * @return true if there is already an Account with this name, false if there is not
     */
    public boolean isNameTaken(String name){
        for(Account acc : accounts){
            if(acc.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * Get the default Account of this AccountManager
     * @return the Account object set as default
     */
    public Account getDefaultAcc(){
        return defaultAcc;
    }

    /**
     * Sets the default Account of this AccountManager to a new one
     * @param acc the Account that should become the new default
     */
    public void setDefaultAcc(Account acc) {
        this.defaultAcc.setDefault(false);
        dbRef.child(defaultAcc.getId()).child("isDefault").setValue(false);
        this.defaultAcc = acc;
    }
}