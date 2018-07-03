package com.lmu.pem.finanzapp.model.budgets;

import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;

import java.util.ArrayList;
import java.util.Date;

public class BudgetManager implements TransactionHistoryEventListener{

    private static BudgetManager instance;

    private final ArrayList<Budget> budgets = new ArrayList<>();

    private BudgetManager() {
        TransactionManager.getInstance().addListener(this);

        addBudget("Household", 100.0f, new Date(2018-1900, 0, 1), new Date(2018-1900, 11, 31));
        addBudget("Movie", 30.0f, new Date(2018-1900, 5, 1), new Date(2018-1900, 5, 30));
    }

    @Override
    public void handle(TransactionHistoryEvent event) {
        switch (event.getType()) {
            case ADDED:
                for (Budget budget : budgets) {
                    if (budget.category.equalsIgnoreCase(event.getTransaction().getCategory())) {
                        budget.setCurrentAmount(budget.getCurrentAmount() - (float)event.getTransaction().getAmount());
                    }
                }
                break;
            case REMOVED:
                for (Budget budget : budgets) {
                    if (budget.category.equalsIgnoreCase(event.getTransaction().getCategory())) {
                        budget.setCurrentAmount(budget.getCurrentAmount() + (float)event.getTransaction().getAmount());
                    }
                }
                break;
        }
    }

    public static BudgetManager getInstance() {
        if (instance == null) instance = new BudgetManager();
        return instance;
    }

    public ArrayList<Budget> getBudgets() {
        return budgets;
    }

    public void addBudget (String category, float budget, Date startingDate, Date endingDate) {
        float amount = 0f;

        for (Transaction transaction : TransactionManager.getInstance().getTransactions()) {
            if (transaction.getCategory().equalsIgnoreCase(category)) {
                amount += transaction.getAmount();
            }
        }

        budgets.add(new Budget(category, startingDate, endingDate, budget, -amount));
    }

}
