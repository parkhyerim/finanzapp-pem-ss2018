package com.lmu.pem.finanzapp.model.budgets;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BudgetManager extends BudgetEventSource implements TransactionHistoryEventListener{

    private static BudgetManager instance;


    private DatabaseReference budgetsRef;

    private ArrayList<Budget> budgets;

    private BudgetManager() {
        reset();
        TransactionManager.getInstance().addListener(this);
    }

    public void reset() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        budgetsRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("budgets");
        budgets = new ArrayList<>();
    }

    private void renewBudgets() {
        for (Budget budget : this.budgets) {
            if (!isBudgetCurrent(budget) && budget.getRenewalType() == Budget.RenewalTypes.CUSTOM) {
                continue;
            }

            while (!isBudgetCurrent(budget))
                forceRenewSingleBudget(budget);
        }

        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
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
            case UPDATED:
                for (Budget budget : budgets) {
                    if (budget.category.equalsIgnoreCase(event.getTransaction().getCategory())) {
                        budget.setCurrentAmount(budget.getCurrentAmount() + (float)event.getTransactionOld().getAmount());
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

    private Budget addBudgetLocally(String category, float budget, Date endingDate) {
        Calendar c = Calendar.getInstance();
        Date startingDate = new Date(c.getTime().getYear(), c.getTime().getMonth(), c.getTime().getDate());

        endingDate.setHours(23);
        endingDate.setMinutes(59);
        endingDate.setSeconds(59);

        float amount = getTransactionSum(category, startingDate, endingDate);

        Budget newBudget = new Budget(category, startingDate, endingDate, budget, amount);

        budgets.add(newBudget);
        return newBudget;
    }

    public void addBudget (String category, float budget, Date endingDate) {
        Budget b = addBudgetLocally(category, budget, endingDate);
        b.setId(writeNewBudgetToFirebase(b));
        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
    }

    private Budget addBudgetLocally(String category, float budget, Budget.RenewalTypes renewalType) {

        Calendar c = Calendar.getInstance();
        Date startingDate = new Date(c.getTime().getYear(), c.getTime().getMonth(), c.getTime().getDate());


        Date endingDate = new Date();
        endingDate.setHours(23);
        endingDate.setMinutes(59);
        endingDate.setSeconds(59);
        endingDate = addTimeByRenewalType(startingDate, renewalType);

        float amount = getTransactionSum(category, startingDate, endingDate);
        Budget newBudget = new Budget(category, startingDate, endingDate, budget, amount, renewalType);
        budgets.add(newBudget);
        return newBudget;
    }

    public void addBudget (String category, float budget, Budget.RenewalTypes renewalType) {
        Budget b = addBudgetLocally(category, budget, renewalType);
        b.setId(writeNewBudgetToFirebase(b));
        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
    }

    public void addBudgetFromFirebase(String id, Budget budget) {
        budgets.add(budget);
        budget.setId(id);
        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
    }


    public Budget getById(String id) {
        for (Budget budget : budgets) {
            if (budget.getId().equals(id))
                return budget;
        }
        return null;
    }

    public boolean editById(String id, String category, float budget, Budget.RenewalTypes renewalType) {
        Budget b = getById(id);
        if (b == null) return false;

        b.setBudget(budget);
        b.setCategory(category);
        b.setRenewalType(renewalType);
        b.setUntil(addTimeByRenewalType(b.getFrom(), renewalType));


        b.setCurrentAmount(getTransactionSum(category, b.getFrom(), b.getUntil()));
        editInFirebase(b.getId(), b);
        renewBudgets();

        return true;
    }

    public boolean editById(String id, String category, float budget, Date customDate) {
        Budget b = getById(id);
        if (b == null) return false;

        b.setBudget(budget);
        b.setCategory(category);
        b.setRenewalType(Budget.RenewalTypes.CUSTOM);
        b.setUntil(customDate);


        b.setCurrentAmount(getTransactionSum(category, b.getFrom(), b.getUntil()));
        editInFirebase(b.getId(), b);
        renewBudgets();

        editInFirebase(b.getId(), b);
        renewBudgets();

        return true;
    }

    private float getTransactionSum(String category, Date startingDate, Date endingDate) {
        float amount = 0f;

        for (Transaction transaction : TransactionManager.getInstance().getTransactions()) {
            Date transactionDate = new Date(transaction.getYear() - 1900, transaction.getMonth()-1, transaction.getDay());

            if (transaction.getCategory().equalsIgnoreCase(category)
                    && (startingDate.before(transactionDate) || startingDate.equals(transactionDate))
                    && (endingDate.after(transactionDate) || endingDate.equals(transactionDate))) {
                amount += transaction.getAmount();
            }
        }

        return (amount == 0f) ? amount : -amount;
    }

    private void editInFirebase(String id, Budget newBudget) {
        budgetsRef.child(id).setValue(newBudget);
    }

    public boolean removeById(String id) {
        Budget b = getById(id);
        if (b == null) return false;

        budgets.remove(b);
        budgetsRef.child(id).removeValue();
        renewBudgets();
        return true;
    }


    private String writeNewBudgetToFirebase(Budget budget) {
        String key = budgetsRef.push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, budget);
        budgetsRef.updateChildren(childUpdates);
        return key;
    }
}
