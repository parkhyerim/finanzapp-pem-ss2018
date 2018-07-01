package com.lmu.pem.finanzapp.model.transactions;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.data.Account;

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionManager {

    private static TransactionManager instance;

    private ArrayList<Transaction> transactions;
    private Transaction defaultTransaction;
    private TransactionHistory transactionHistory;

    DatabaseReference db;
    DatabaseReference transactionRef;


    private TransactionManager() {
        this.transactions = new ArrayList<>();
        createTransactionList();
        db = FirebaseDatabase.getInstance().getReference().child("transaction");
        //transactionRef = db.child("transaction");
        //transactionHistory = TransactionHistory.getInstance();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> map = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                for (String key : map.keySet()) {

                    Transaction newTransaction = new Transaction(
                            dataSnapshot.child(key).child("date").getValue(String.class),
                            dataSnapshot.child(key).child("imageResource").getValue(Integer.class),
                            dataSnapshot.child(key).child("account").getValue(String.class),
                            dataSnapshot.child(key).child("category").getValue(String.class),
                            dataSnapshot.child(key).child("description").getValue(String.class),
                            dataSnapshot.child(key).child("amount").getValue(Double.class)

                    );
                   // if ((boolean) map.get(key).get("isDefault"))
                    if(!containsTransaction(newTransaction)){
                        transactions.add(newTransaction);
                    }
                    //defaultTransaction = newTransaction;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("123123123","Cancelled: "+databaseError.toString());
            }
        });


    }


    public static TransactionManager getInstance() {
        if(instance == null) instance = new TransactionManager();
        return instance;
    }


    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
    }


    public void removeTransaction(Transaction transaction){
        this.transactions.remove(transaction);
    }


    public boolean containsTransaction(Transaction transaction){
        for(Transaction t : transactions){
            // TODO: temporäre Code... "equals" und "==" funktioniert nicht...
            if(t.getAmount() == transaction.getAmount() && t.getDescription()== transaction.getDescription()){
                return true;
            }

        }
        return false;
    }

    public ArrayList<Transaction> getTransactions(){
        return transactions;

    }

    public void createTransactionList(){
        // dummy transaction list
        addTransaction(new Transaction("04/28/2018", R.drawable.salary, "Cash", "Salary", "Werkstudenten-Gehalt", 450));
        //transactionList.add(new Transaction("04/29/2018", R.drawable.food, "Main", "Food", "Pizza & Burger", 42, 0));
        //transactionList.add(new Transaction("05/01/2018", R.drawable.music, "Main", "Music", "BTS CD",28, 0));
        addTransaction(new Transaction("05/02/2018", R.drawable.household, "Cash", "Household", "Edeka", -55.20));
        addTransaction(new Transaction("05/02/2018", R.drawable.bonus, "Cash", "Bonus", "Bonus!!!", 180));
        addTransaction(new Transaction("05/05/2018", R.drawable.movie, "Cash", "Movie", "Black Panther", -21));
        addTransaction(new Transaction("05/05/2018", R.drawable.gift, "Cash", "Gift", "Muttertag", -38.25));
    }


}
