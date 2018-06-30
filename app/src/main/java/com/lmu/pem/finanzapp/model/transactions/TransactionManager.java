package com.lmu.pem.finanzapp.model.transactions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lmu.pem.finanzapp.R;

import java.util.ArrayList;

public class TransactionManager {

    private static TransactionManager instance;

    private ArrayList<Transaction> transactions;
    private Transaction defaultTransaction;
    private TransactionHistory transactionHistory;


    private TransactionManager() {
        this.transactions = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("transactions");
        transactionHistory = TransactionHistory.getInstance();

        createTransactionList();
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
