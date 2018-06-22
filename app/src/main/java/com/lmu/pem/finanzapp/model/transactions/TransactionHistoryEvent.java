package com.lmu.pem.finanzapp.model.transactions;

public class TransactionHistoryEvent {

    public enum EventType {
        ADDED,
        REMOVED,
    }

    TransactionHistoryEventSource source;
    Transaction transaction;
    EventType type;

    public TransactionHistoryEvent(EventType type, TransactionHistoryEventSource source, Transaction transaction) {
        this.type = type;
        this.source = source;
        this.transaction = transaction;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public TransactionHistoryEventSource getSource() {
        return source;
    }

    public void setSource(TransactionHistoryEventSource source) {
        this.source = source;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
