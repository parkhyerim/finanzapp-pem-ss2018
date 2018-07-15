package com.lmu.pem.finanzapp.model.transactions;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TransactionManager extends TransactionHistoryEventSource{

    private static TransactionManager instance;

    private ArrayList<Transaction> transactions;

    private DatabaseReference db;
    private DatabaseReference transactionRef;


    private TransactionManager() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        transactionRef = db.child("transactions");

        this.transactions = new ArrayList<>();
    }


    public static TransactionManager getInstance() {
        if(instance == null) instance = new TransactionManager();
        return instance;
    }


    /**
     * Adds a Transaction to the Transaction Manager and stores it in Firebase by calling the {@link #writeNewTransactionToFB(Transaction) writeNewTransactionToFB} method.
     * This method also automatically stores the Firebase key in the given Transaction object.
     * @param transaction the Transaction object to be added
     */
    public void addTransaction(Transaction transaction){
        addTransactionLocally(transaction, true);
        String key = writeNewTransactionToFB(transaction);
        transaction.setKey(key);
    }

    public void addTransactionLocally(Transaction transaction, boolean fireEvent){
        this.transactions.add(transaction);
        if(fireEvent) fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.ADDED, this, transaction));
    }

    public void updateTransaction(String key, int year, int month, int day, String account, String account2, String category, int imageResource, String description, double amount){
        Transaction transaction = getTransactionByKey(key);
        Transaction transactionOld;
        if(transaction.getAccount2()==null){
            transactionOld = new Transaction(
                    transaction.getYear(),
                    transaction.getMonth(),
                    transaction.getDay(),
                    transaction.getImageResource(),
                    transaction.getAccount(),
                    transaction.getCategory(),
                    transaction.getDescription(),
                    transaction.getAmount()
            );
        }else{
            transactionOld = new Transaction(
                    transaction.getYear(),
                    transaction.getMonth(),
                    transaction.getDay(),
                    transaction.getImageResource(),
                    transaction.getAccount(),
                    transaction.getAccount2(),
                    transaction.getCategory(),
                    transaction.getDescription(),
                    transaction.getAmount()
            );
        }
        if(transaction.getYear() != year) transaction.setYear(year);
        if(transaction.getMonth() != month) transaction.setMonth(month);
        if(transaction.getDay() != day) transaction.setDay(day);
        if(!(transaction.getAccount().equals(account))) transaction.setAccount(account);
        if(!(transaction.getAccount2()!=null && transaction.getAccount2().equals(account2)) && !(transaction.getAccount2()==null && account2==null)) transaction.setAccount2(account2);
        if(!(transaction.getCategory().equals(category))){
            transaction.setCategory(category);
            transaction.setImageResource(imageResource);
        }
        if(!(transaction.getDescription().equals(description))) transaction.setDescription(description);
        if(!(transaction.getAmount() == amount)) transaction.setAmount(amount);
        updateTransactionInFB(key, transaction);
        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.UPDATED, this, transaction, transactionOld));
    }


    public void removeTransaction(Transaction transaction){
        deleteTransactionFromFB(transaction);
        this.transactions.remove(transaction);
        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.REMOVED, this, transaction));
    }


    public boolean containsTransaction(Transaction transaction){
        return getTransactionByKey(transaction.getKey()) != null;
    }

    public Transaction getTransactionByKey(String key){
        for(Transaction t : transactions){
            if(t.getKey().equals(key)){
                return t;
            }
        }
        return null;
    }

    public ArrayList<Transaction> getTransactions(){
        Collections.sort(transactions);
        return transactions;

    }

    /**
     * for debugging / testing purposes
     */
    public void createTransactionList(){
        // dummy transaction list
        addTransaction(new Transaction(2018, 4, 28, R.drawable.salary, "Cash", "Salary", "Werkstudenten-Gehalt", 450));
        addTransaction(new Transaction(2018, 5, 2, R.drawable.household, "Cash", "Household", "Edeka", -55.20));
        addTransaction(new Transaction(2018, 5,2, R.drawable.bonus, "Cash", "Bonus", "Bonus!!!", 180));
        addTransaction(new Transaction(2018, 5,5,R.drawable.movie, "Cash", "Movie", "Black Panther", -21));
        addTransaction(new Transaction(2018, 5,5,R.drawable.gift, "Cash", "Gift", "Muttertag", -38.25));
    }


    /**
     * Writes a given Transaction into the Firebase Database
     * @param transaction the Transaction object to be stored in the database
     * @return the key of the dataset in Firebase (will be added to the Transaction in {@link #addTransaction(Transaction) addTransaction})
     */
    public String writeNewTransactionToFB(Transaction transaction) {

        String key = transactionRef.push().getKey();

        // Toast.makeText(getActivity(), "ke:"+ key, Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), "ID:"+ transaction.getTransactionId(), Toast.LENGTH_LONG).show();

        // transactionRef.push().setValue(transaction);

        Map<String, Object> transactionValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, transactionValues);
        transactionRef.updateChildren(childUpdates);
        return key;
    }

    public void updateTransactionInFB(String key, Transaction newT){
        transactionRef.child(key).setValue(newT.toMap());
    }

    public void deleteTransactionFromFB(Transaction transaction) {
        String key = transaction.getKey();

      //  Toast.makeText(this,"key;"+ key, Toast.LENGTH_LONG).show();
       // transactionRef.child(key).setValue(null);


        if (key != null) {
            transactionRef.child(key).removeValue();
        }

        /*
        String key = transactionRef.push().getKey();
        this.userId = transaction.getTransactionId();
        transactionRef.child(transaction.get).removeValue();
        */
    }
}
