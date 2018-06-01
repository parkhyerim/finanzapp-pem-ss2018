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

    public ArrayList<Account> getAccounts() {
        return accounts;
    }
}
