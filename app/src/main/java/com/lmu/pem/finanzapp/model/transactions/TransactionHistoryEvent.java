package com.lmu.pem.finanzapp.model.transactions;

public class TransactionHistoryEvent {

    public enum EventType {
        ADDED,
        REMOVED,
        UPDATED
    }

    TransactionHistoryEventSource source;
    Transaction transaction, transactionOld;
    EventType type;

    /**
     * A TransactionHistoryEvent indicating a change in the transaction history.
     * @param type           the EventType (ADDED, REMOVED, UPDATED) of this Event. This constructor should only be used with ADDED and REMOVED events.
     * @param source         the event source object extending TransactionHistoryEventSource
     * @param transaction    the Transaction in question
     */
    public TransactionHistoryEvent(EventType type, TransactionHistoryEventSource source, Transaction transaction) {
        this(type, source, transaction, null);
    }

    /**
     * Additional Constructor needed for UPDATED event in order to differentiate between two Transactions
     * @param type           the EventType (ADDED, REMOVED, UPDATED) of this Event. This constructor should only be used with UPDATED events.
     * @param source         the event source object extending TransactionHistoryEventSource
     * @param transactionNew the new Transaction (after the update)
     * @param transactionOld the old Transaction (before the update)
     */
    public TransactionHistoryEvent(EventType type, TransactionHistoryEventSource source, Transaction transactionNew, Transaction transactionOld) {
        this.type = type;
        this.source = source;
        this.transaction = transactionNew;
        this.transactionOld = transactionOld;
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

    public Transaction getTransactionOld() {
        return transactionOld;
    }

    public void setTransactionOld(Transaction transactionOld) {
        this.transactionOld = transactionOld;
    }
}
