package com.lmu.pem.finanzapp.model.budgets;

import android.util.Log;

import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class BudgetManager implements TransactionHistoryEventListener{

    private static BudgetManager instance;

    private final ArrayList<Budget> budgets = new ArrayList<>();

    private BudgetManager() {
        TransactionManager.getInstance().addListener(this);

        addBudget("Household", 100.0f,  Budget.RenewalTypes.YEAR);
        addBudget("Movie", 30.0f, new Date(2018-1900, 6, 30));
    }

    private void renewBudgets() {
        ArrayList<Budget> budgetsToRemove = new ArrayList<>();
        for (Budget budget : this.budgets) {
            if (!isBudgetCurrent(budget) && budget.getRenewalType() == Budget.RenewalTypes.NONE) {
                //TODO DON'T REMOVE
                budgetsToRemove.add(budget);
                continue;
            }

            while (!isBudgetCurrent(budget))
                forceRenewSingleBudget(budget);
        }
        this.budgets.removeAll(budgetsToRemove);
    }

    private boolean isBudgetCurrent(Budget budget) {
        Date today = Calendar.getInstance().getTime();
        return (today.after(budget.getFrom()) && today.before(budget.getUntil()));

    }

    private void forceRenewSingleBudget(Budget budget) {

        budget.setFrom(addTimeByRenewalType(addOneDay(budget.getFrom()), budget.getRenewalType()));
        budget.setUntil(addTimeByRenewalType(addOneDay(budget.getUntil()), budget.getRenewalType()));


    }

    private Date addTimeByRenewalType(Date date, Budget.RenewalTypes renewalType) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);


        switch (renewalType) {
            case DAY:
                c.add(Calendar.DATE, 1);
                break;
            case WEEK:
                c.add(Calendar.DATE, 7);
                break;
            case MONTH:
                c.add(Calendar.MONTH, 1);
                break;
            case YEAR:
                c.add(Calendar.YEAR, 1);
                break;
        }
        return c.getTime();
    }


    private Date addOneDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
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
        renewBudgets();
        return budgets;
    }

    public void addBudget (String category, float budget, Date endingDate) {
        float amount = 0f;

        for (Transaction transaction : TransactionManager.getInstance().getTransactions()) {
            if (transaction.getCategory().equalsIgnoreCase(category)) {
                amount += transaction.getAmount();
            }
        }

        Date startingDate = Calendar.getInstance().getTime();
        startingDate.setHours(0);
        startingDate.setMinutes(0);
        startingDate.setSeconds(0);

        endingDate.setHours(23);
        endingDate.setMinutes(59);
        endingDate.setSeconds(59);

        budgets.add(new Budget(Calendar.getInstance().getTime().hashCode() + category.hashCode(), category, startingDate, endingDate, budget, -amount));
    }
    public void addBudget (String category, float budget, Budget.RenewalTypes renewalType) {
        float amount = 0f;

        for (Transaction transaction : TransactionManager.getInstance().getTransactions()) {
            if (transaction.getCategory().equalsIgnoreCase(category)) {
                amount += transaction.getAmount();
            }
        }
        Date startingDate = Calendar.getInstance().getTime();
        startingDate.setHours(0);
        startingDate.setMinutes(0);
        startingDate.setSeconds(0);

        Date endingDate = new Date();
        endingDate.setHours(23);
        endingDate.setMinutes(59);
        endingDate.setSeconds(59);
        endingDate = addTimeByRenewalType(startingDate, renewalType);

        budgets.add(new Budget(Calendar.getInstance().getTime().hashCode() + category.hashCode(), category, startingDate, endingDate, budget, -amount, renewalType));
    }

    public Budget getById(int id) {
        for (Budget budget : budgets) {
            if (budget.getId() == id)
                return budget;
        }
        return null;
    }

    public boolean editById(int id, String category, float budget, Budget.RenewalTypes renewalType) {
        Budget b = getById(id);
        if (b == null) return false;

        b.setBudget(budget);
        b.setCategory(category);
        b.setRenewalType(renewalType);
        b.setUntil(addTimeByRenewalType(b.getFrom(), renewalType));
        renewBudgets();

        return true;
    }

    public boolean editById(int id, String category, float budget, Date customDate) {
        Budget b = getById(id);
        if (b == null) return false;

        b.setBudget(budget);
        b.setCategory(category);
        b.setRenewalType(Budget.RenewalTypes.NONE);
        b.setUntil(customDate);
        renewBudgets();

        return true;
    }

}
