package com.lmu.pem.finanzapp.model.transactions;

import com.lmu.pem.finanzapp.data.categories.Category;

import java.util.ArrayList;

public class TransactionHistory extends TransactionHistoryEventSource{

    private static TransactionHistory instance;


    private final ArrayList<Transaction> transactions = new ArrayList<>();


    private TransactionHistory () {}

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
