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
                if(map != null){
                    for (String key : map.keySet()) {

                        Transaction newTransaction = new Transaction(
                                dataSnapshot.child(key).child("date").getValue(String.class),
                                dataSnapshot.child(key).child("year").getValue(Integer.class),
                                dataSnapshot.child(key).child("month").getValue(Integer.class),
                                dataSnapshot.child(key).child("day").getValue(Integer.class),
                                dataSnapshot.child(key).child("imageResource").getValue(Integer.class),
                                dataSnapshot.child(key).child("account").getValue(String.class),
                                dataSnapshot.child(key).child("account2").getValue(String.class),
                                dataSnapshot.child(key).child("category").getValue(String.class),
                                dataSnapshot.child(key).child("description").getValue(String.class),
                                dataSnapshot.child(key).child("amount").getValue(Double.class)
                        );
                        newTransaction.setKey(key);
                        if(!containsTransaction(newTransaction)){
                            transactions.add(newTransaction);
                            fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.ADDED, TransactionManager.getInstance(), newTransaction));
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("123123123","Cancelled: "+databaseError.toString());
            }
        });

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


    /**
     * Adds a Transaction to the Transaction Manager and stores it in Firebase by calling the {@link #writeNewTransactionToFB(Transaction) writeNewTransactionToFB} method.
     * This method also automatically stores the Firebase key in the given Transaction object.
     * @param transaction the Transaction object to be added
     */
    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
        String key = writeNewTransactionToFB(transaction);
        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.ADDED, this, transaction));
        transaction.setKey(key);
    }

    public void updateTransaction(String key, String date, String account, String category, int imageResource, String description, double amount){
        Transaction transaction = getTransactionByKey(key);
        Transaction transactionOld = new Transaction(
                transaction.getDate(),
                transaction.getYear(),
                transaction.getMonth(),
                transaction.getDay(),
                transaction.getImageResource(),
                transaction.getAccount(),
                transaction.getCategory(),
                transaction.getDescription(),
                transaction.getAmount()
                );
        if(!(transaction.getDate().equals(date))) transaction.setDate(date);
        if(!(transaction.getAccount().equals(account))) transaction.setAccount(account);
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
        return transactions;

    }

    public void createTransactionList(){
        // dummy transaction list
        addTransaction(new Transaction("04/28/2018", 2018, 4, 28, R.drawable.salary, "Cash", "Salary", "Werkstudenten-Gehalt", 450));
        //transactionList.add(new Transaction("04/29/2018", R.drawable.food, "Main", "Food", "Pizza & Burger", 42, 0));
        //transactionList.add(new Transaction("05/01/2018", R.drawable.music, "Main", "Music", "BTS CD",28, 0));
        addTransaction(new Transaction("05/02/2018",2018, 5, 2, R.drawable.household, "Cash", "Household", "Edeka", -55.20));
        addTransaction(new Transaction("05/02/2018", 2018, 5,2, R.drawable.bonus, "Cash", "Bonus", "Bonus!!!", 180));
        addTransaction(new Transaction("05/05/2018", 2018, 5,5,R.drawable.movie, "Cash", "Movie", "Black Panther", -21));
        addTransaction(new Transaction("05/05/2018", 2018, 5,5,R.drawable.gift, "Cash", "Gift", "Muttertag", -38.25));
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
