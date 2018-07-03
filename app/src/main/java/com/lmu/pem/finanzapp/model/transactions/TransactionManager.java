package com.lmu.pem.finanzapp.model.transactions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionManager extends TransactionHistoryEventSource{

    private static TransactionManager instance;

    private ArrayList<Transaction> transactions;

    private DatabaseReference db;
    private DatabaseReference transactionRef;

    private String userId;
    private Context context;


    private TransactionManager() {
        db = FirebaseDatabase.getInstance().getReference();
        transactionRef = db.child("transactions");

        this.transactions = new ArrayList<>();
        //createTransactionList();


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
                    if(!containsTransaction(newTransaction)){
                        transactions.add(newTransaction);
                        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.ADDED, TransactionManager.getInstance(), newTransaction));
                    }
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

                String key = dataSnapshot.getKey();
                Transaction newTransaction = dataSnapshot.getValue(Transaction.class);
                for(Transaction t: transactions){
                    if(t.getKey().equals(key)){
                        t.setTransaction(newTransaction);
                        break;
                    }

                }

             }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Log.d(TAG+"Removed",dataSnapshot.getValue(Transaction.class).toString());
                String key = dataSnapshot.getKey();
                for(Transaction t: transactions) {
                    if(key.equals(t.getKey())) {
                        transactions.remove(t);
                        break;
                    }
                }
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
        writeNewTransactionToFB(transaction);
        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.ADDED, this, transaction));
    }


    public void removeTransaction(Transaction transaction){

        deleteTransactionFromFB(transaction);
        this.transactions.remove(transaction);
        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.REMOVED, this, transaction));
    }


    public boolean containsTransaction(Transaction transaction){
        for(Transaction t : transactions){
            // TODO: temporäre Code... "equals" und "==" funktioniert nicht...
            if(t.getAmount() == transaction.getAmount() && t.getDescription().equals(transaction.getDescription())){
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


    public void writeNewTransactionToFB(Transaction transaction){

        String key = FirebaseDatabase.getInstance().getReference().child("transaction").push().getKey();

        /*
        String key = transactionRef.push().getKey();
        transaction.setTransactionKey(key);
        */

        // Toast.makeText(getActivity(), "ke:"+ key, Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), "ID:"+ transaction.getTransactionId(), Toast.LENGTH_LONG).show();


       // transactionRef.push().setValue(transaction);

        Map<String, Object> transactionValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/transactions/" + key, transactionValues);
        transactionRef.updateChildren(childUpdates);
        //FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);



    }

    public void updateTransactionInFB(Transaction oldT, Transaction newT){
        transactionRef.child(oldT.getKey()).setValue(newT);
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
