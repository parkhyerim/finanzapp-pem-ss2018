package com.lmu.pem.finanzapp.model.budgets;

import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventSource;

public class BudgetEvent {

    public enum EventType {
        UPDATED
    }

    private BudgetEventSource source;
    private EventType type;



    public BudgetEvent(EventType type, BudgetEventSource source) {
        this.type = type;
        this.source = source;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public BudgetEventSource getSource() {
        return source;
    }

}
