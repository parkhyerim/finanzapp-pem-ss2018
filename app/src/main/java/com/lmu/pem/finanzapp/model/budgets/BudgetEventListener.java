package com.lmu.pem.finanzapp.model.budgets;

import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;

public interface BudgetEventListener {

    public abstract void handle(BudgetEvent event);
}
