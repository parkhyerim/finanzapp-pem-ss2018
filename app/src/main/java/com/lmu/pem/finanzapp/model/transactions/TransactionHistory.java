package com.lmu.pem.finanzapp.model.transactions;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.data.categories.Category;

import java.util.ArrayList;

public class TransactionHistory extends TransactionHistoryEventSource{

    private static TransactionHistory instance;


    private final ArrayList<Transaction> transactions = new ArrayList<>();


    private TransactionHistory () {
        // dummy transaction list
        addTransaction(new Transaction("04/28/2018", R.drawable.salary, "Cash", "Salary", "Werkstudenten-Gehalt", 0, 450));
        //transactionList.add(new Transaction("04/29/2018", R.drawable.food, "Main", "Food", "Pizza & Burger", 42, 0));
        //transactionList.add(new Transaction("05/01/2018", R.drawable.music, "Main", "Music", "BTS CD",28, 0));
        addTransaction(new Transaction("05/02/2018", R.drawable.household, "Cash", "Household", "Edeka", 55.20, 0));
        addTransaction(new Transaction("05/02/2018", R.drawable.bonus, "Cash", "Bonus", "Bonus!!!", 0, 180));
        addTransaction(new Transaction("05/05/2018", R.drawable.movie, "Cash", "Movie", "Black Panther", 21, 0));
        addTransaction(new Transaction("05/05/2018", R.drawable.gift, "Cash", "Gift", "Muttertag", 38.25, 0));
    }

    public static TransactionHistory getInstance() {
        if (instance == null) instance = new TransactionHistory();
        return instance;
    }


    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    /***
     * @deprecated DON'T USE THIS UNTIL CATEGORY CLASS IS IMPLEMENTED
     * @param categories
     * @return
     */
    public ArrayList<Transaction> getTransactionByCategories(ArrayList<String> categories) {
        ArrayList<Transaction> buffer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (categories.contains(transaction.getCategory())) buffer.add(transaction);
        }
        return buffer;
    }

    public void addTransaction(Transaction t) {
        if (transactions.contains(t)) return;
        transactions.add(t);
        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.ADDED, this, t));
    }

    public void removeTransaction(Transaction t) {
        if (transactions.contains(t)) transactions.remove(t);
        fireTransactionHistoryEvent(new TransactionHistoryEvent(TransactionHistoryEvent.EventType.REMOVED, this, t));
    }
}
