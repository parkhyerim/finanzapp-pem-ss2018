package com.lmu.pem.finanzapp.model.budgets;

import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;

import java.util.ArrayList;

public abstract class BudgetEventSource {

    private final ArrayList<BudgetEventListener> listeners = new ArrayList<>();


    public void addListener(BudgetEventListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeListener(BudgetEventListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);
    }

    void fireBudgetEvent(BudgetEvent event) {
        for (BudgetEventListener listener : listeners) {
            listener.handle(event);
        }
    }
}
