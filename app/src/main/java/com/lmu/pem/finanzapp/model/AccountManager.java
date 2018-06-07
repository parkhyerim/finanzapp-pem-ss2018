package com.lmu.pem.finanzapp.model;

import com.lmu.pem.finanzapp.data.Account;

import java.util.ArrayList;

public class AccountManager {

    private static AccountManager instance;

    private ArrayList<Account> accounts;

    public AccountManager() {
        // TODO - read accounts from database
        this.accounts = new ArrayList<Account>();
        this.accounts.add(new Account("Cash", 64.45)); //TODO durch String-Ressource ersetzen - Crash bei: getContext().getString(R.string.account_cash)
        this.accounts.add(new Account("Main", 0xff00695c, true, 2049.05));
        this.accounts.add(new Account("Vacation", 0xffc62828, 256.09));
    }

    public static AccountManager getInstance () {
        if (instance == null) return new AccountManager();
        else return instance;
    }

    //also to be used for changing attributes of a given account
    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account acc){
        this.accounts.add(acc);
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
}