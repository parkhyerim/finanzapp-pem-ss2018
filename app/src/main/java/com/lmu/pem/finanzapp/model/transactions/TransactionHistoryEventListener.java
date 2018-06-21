package com.lmu.pem.finanzapp.model.transactions;

public interface TransactionHistoryEventListener {

    public abstract void handle(TransactionHistoryEvent event);
}
