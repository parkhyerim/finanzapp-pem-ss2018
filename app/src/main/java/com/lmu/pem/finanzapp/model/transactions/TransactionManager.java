package com.lmu.pem.finanzapp.model.transactions;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.data.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class TransactionManager extends Activity {

    private static TransactionManager instance;

    private ArrayList<Transaction> transactions;
    private Transaction defaultTransaction;
    private TransactionHistory transactionHistory;

    private DatabaseReference db;
    private DatabaseReference transactionRef;

    private String userId;

    public TransactionManager() {
        db = FirebaseDatabase.getInstance().getReference();
        transactionRef = db.child("transactions");

        this.transactions = new ArrayList<>();

        transactionRef.addValueEventListener(new ValueEventListener() {
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



/*
 // In order to listen for child events on DatabaseReference, attach a ChildEventListener
        transactionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Log.d(TAG+"Added", dataSnapshot.getValue(Transaction.class).toString());

                Transaction t = dataSnapshot.getValue(Transaction.class);
                Transaction newTrans = new Transaction(t.getDate(), t.getImageResource(), t.getAccount(), t.getCategory(), t.getDescription(), t.getAmount());
                transactions.add(newTrans);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // Log.d(TAG+"Changed",dataSnapshot.getValue(Transaction.class).toString());

                Transaction t = dataSnapshot.getValue(Transaction.class);
                String k = dataSnapshot.getKey();

                Transaction newTrans = new Transaction(t.getDate(), t.getImageResource(), t.getAccount(), t.getCategory(), t.getDescription(), t.getAmount());
                transactions.add(newTrans);
             }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Log.d(TAG+"Removed",dataSnapshot.getValue(Transaction.class).toString());
                String transactionKey = dataSnapshot.getKey();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //Log.d(TAG+"Moved",dataSnapshot.getValue(Transaction.class).toString());
                Transaction transaction = dataSnapshot.getValue(Transaction.class);
                String transactionKey = dataSnapshot.getKey();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.d(TAG+"Cancelled",databaseError.toString());
                Toast.makeText(getApplicationContext(), "Failed to load transactions.", Toast.LENGTH_SHORT).show();
            }
        });

*/

    }

    /*

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance().getReference();
        userId = database.child("transactions").push().getKey();

        if(userId != null){
            database.child("transactions").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Transaction transaction = dataSnapshot.getValue(Transaction.class);
                    Log.d(TAG, "Category: " + transaction.getCategory());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }

    }
*/


    public static TransactionManager getInstance() {
        if(instance == null) instance = new TransactionManager();
        return instance;
    }


    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
        writeNewTransaction(transaction);
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


    private void writeNewTransaction(Transaction transaction){

        // String key = FirebaseDatabase.getInstance().getReference().child("transaction").push().getKey();

        String key = transactionRef.push().getKey();
        transaction.setTransactionId(key);

        // Toast.makeText(getActivity(), "key:"+ key, Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), "ID:"+ transaction.getTransactionId(), Toast.LENGTH_LONG).show();


        Map<String, Object> transactionValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/transactions/" + key, transactionValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
        //transactionRef.updateChildren(childUpdates);

    }

}
