package com.lmu.pem.finanzapp.model.transactions;

import java.util.ArrayList;

public abstract class TransactionHistoryEventSource {

    private final ArrayList<TransactionHistoryEventListener> listeners = new ArrayList<>();


    public void addListener(TransactionHistoryEventListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void removeListener(TransactionHistoryEventListener listener) {
        if (listeners.contains(listener)) listeners.remove(listener);
    }

    void fireTransactionHistoryEvent(TransactionHistoryEvent event) {
        for (TransactionHistoryEventListener listener : listeners) {
            listener.handle(event);
        }
    }
}
