package com.lmu.pem.finanzapp.model.accounts;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;

import java.util.ArrayList;

public class AccountManager implements TransactionHistoryEventListener {

    private static AccountManager instance;

    private ArrayList<Account> accounts;
    private Account defaultAcc;
    private DatabaseReference dbRef;

    private AccountManager() {
        reset();

    }

    /**
     * Get the AccountManager (Singleton implementation). If there already is one, return this instance. If there isn't, create a new one and return it.
     * @return the AccountManager instance
     */
    public static AccountManager getInstance () {
        if (instance == null) instance = new AccountManager();
        return instance;
    }

    public void reset(){
        if (this.accounts != null)
            this.accounts.clear();
        else
            this.accounts = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("accounts");
        defaultAcc=null;
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
     * Add an Account to the AccountManager. This method doesn't write the Account to Firebase, you have to call {@link #writeAccountToFirebase(Account)} for that separately.
     * @param acc The Account object to be added
     */
    public void addAccount(Account acc){
        this.accounts.add(acc);
        Log.i("123123123", "addAccount: "+acc.getId()+" ->default? "+acc.isDefault());
        if(acc.isDefault()) setDefaultAcc(acc);
    }

    public void writeAccountToFirebase(Account acc) {
        dbRef.child(acc.getId()).setValue(acc.toMap());
    }

    /**
     * Removes an account from this AccountManager and deletes in in Firebase
     * @param id The ID of the Account to be removed
     */
    public void deleteAccount(String id){
        int index = accounts.indexOf(getAccountById(id));
        if(index!=-1){
            dbRef.child(id).setValue(null);
            accounts.remove(index);
        }
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
        if(defaultAcc!=null){
            this.defaultAcc.setDefault(false);
            writeAccountToFirebase(defaultAcc);
        }
        this.defaultAcc = acc;
    }

    @Override
    public void handle(TransactionHistoryEvent event) {
        Transaction transaction = event.getTransaction();
        Account account = getAccountById(transaction.getAccount());
        Account account2 = getAccountById(transaction.getAccount2());

        if(account==null) return;

        switch(event.getType()){
            case ADDED:
                if(account2==null){
                    account.setBalance(account.getBalance() + transaction.getAmount());
                }else{
                    account.setBalance(account.getBalance() - transaction.getAmount());
                    account2.setBalance(account2.getBalance() + transaction.getAmount());
                }
                break;
            case REMOVED:
                if(account2==null) {
                    account.setBalance(account.getBalance() - transaction.getAmount());
                }else{
                    account.setBalance(account.getBalance() + transaction.getAmount());
                    account2.setBalance(account2.getBalance() - transaction.getAmount());
                }
                break;
            case UPDATED: //"remove" old transaction, "add" the new one
                Transaction transactionOld = event.getTransactionOld();
                Account accountOld = getAccountById(transactionOld.getAccount());
                Account accountOld2 = getAccountById(transactionOld.getAccount2());

                //remove the old transaction
                if(accountOld2==null){
                    accountOld.setBalance(accountOld.getBalance() - transactionOld.getAmount());
                }else{
                    accountOld.setBalance(accountOld.getBalance() + transactionOld.getAmount());
                    accountOld2.setBalance(accountOld2.getBalance() - transactionOld.getAmount());
                    dbRef.child(accountOld2.getId()).child("balance").setValue(accountOld2.getBalance());
                }

                //add the new one
                if(account2==null) {
                    account.setBalance(account.getBalance() + transaction.getAmount());
                }else{
                    account.setBalance(account.getBalance() - transaction.getAmount());
                    account2.setBalance(account2.getBalance() + transaction.getAmount());
                }
                dbRef.child(accountOld.getId()).child("balance").setValue(accountOld.getBalance());
        }
        if(account2!=null) dbRef.child(account2.getId()).child("balance").setValue(account2.getBalance());
        dbRef.child(account.getId()).child("balance").setValue(account.getBalance());
    }

    public void initializeWithCashAccount() {
        Account cashAcc = new Account("Cash", true, 0);
        addAccount(cashAcc);
        writeAccountToFirebase(cashAcc);
    }
}