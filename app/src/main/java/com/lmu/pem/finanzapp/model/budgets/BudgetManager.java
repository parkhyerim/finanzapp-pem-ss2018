package com.lmu.pem.finanzapp.model.budgets;

import android.util.Log;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BudgetManager extends BudgetEventSource implements TransactionHistoryEventListener{

    /**
     * The singleton instance.
     */
    private static BudgetManager instance;

    /**
     * The Firebase Reference to the budgets.
     */
    private DatabaseReference budgetsRef;

    /**
     * All Budgets.
     */
    private ArrayList<Budget> budgets;

    /**
     * Private Constructor for singleton instance.
     */
    private BudgetManager() {
        reset();
        TransactionManager.getInstance().addListener(this);
    }

    /**
     * Public getter for the singleton instance.
     * @return The Singleton instance
     */
    public static BudgetManager getInstance() {
        if (instance == null) instance = new BudgetManager();
        return instance;
    }

    /**
     * Resets all the references and lists, so that they can be populated by firebase.
     */
    public void reset() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        budgetsRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("budgets");
        if (budgets == null)
            budgets = new ArrayList<>();
        else
            budgets.clear();
    }

    /**
     * Checks all budgets if they are expired and should renew. If so, it renews them.
     */
    private void renewBudgets() {
        for (Budget budget : this.budgets) {
            if (isBudgetExpired(budget) && budget.getRenewalType() == Budget.RenewalTypes.CUSTOM) {
                continue;
            }

            while (isBudgetExpired(budget))
                forceRenewSingleBudget(budget);
        }

        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
    }

    /**
     * Checks if a given budget has expired.
     * @param budget The budget to check.
     * @return True, if the budget is expired, false if it is still running.
     */
    private boolean isBudgetExpired(Budget budget) {
        Date today = Calendar.getInstance().getTime();
        return (!today.after(budget.getFrom()) || !today.before(budget.getUntil()));

    }

    /**
     * Renews a given budget without consideration for its expiry
     * @param budget The budget to renew.
     */
    private void forceRenewSingleBudget(Budget budget) {
        budget.setFrom(addTimeByRenewalType(budget.getFrom(), budget.getRenewalType()));
        budget.setUntil(addTimeByRenewalType(budget.getUntil(), budget.getRenewalType()));
    }

    /**
     * Takes a date and RenewalType and returns a new Date with the added time from the renewal.
     * @param date Date to add to.
     * @param renewalType RenewalType to add.
     * @return Date with added time from RenewalType.
     */
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

    /**
     * Handles an event from the TransactionManager to react to changes to the transactions by updating
     * the budgets.
     * @param event The Event fired by the TransactionManager.
     */
    @Override
    public void handle(TransactionHistoryEvent event) {
        switch (event.getType()) {
            case ADDED:
                for (Budget budget : budgets) {
                    if (doesTransactionFitBudget(event.getTransaction(), budget)) {
                        budget.setCurrentAmount(budget.getCurrentAmount() - (float)event.getTransaction().getAmount());
                    }
                }
                break;
            case UPDATED:
                for (Budget budget : budgets) {
                    if (doesTransactionFitBudget(event.getTransaction(), budget)) {
                        if (doesTransactionFitBudget(event.getTransactionOld(), budget))
                            budget.setCurrentAmount(budget.getCurrentAmount() + (float)event.getTransactionOld().getAmount());
                        budget.setCurrentAmount(budget.getCurrentAmount() - (float)event.getTransaction().getAmount());
                    }
                }
                break;
            case REMOVED:
                for (Budget budget : budgets) {
                    if (doesTransactionFitBudget(event.getTransaction(), budget)) {
                        budget.setCurrentAmount(budget.getCurrentAmount() + (float)event.getTransaction().getAmount());
                    }
                }
                break;
        }
    }

    /**
     * Checks if a given transaction should be considered by a given budget. (Time and Category)
     * @param t The transaction to check.
     * @param b The budget to check it against.
     * @return True, if the transaction fits, false if not.
     */
    private boolean doesTransactionFitBudget(Transaction t, Budget b) {
        Date transactionDate = new Date(t.getYear() - 1900, t.getMonth() - 1, t.getDay());
        transactionDate.setHours(12);
        boolean dateFits = (b.getFrom().before(transactionDate) && b.getUntil().after(transactionDate)) ;
        boolean categoryFits = (b.category.equalsIgnoreCase(t.getCategory()));

        return (dateFits && categoryFits);
    }

    public ArrayList<Budget> getBudgets() {
        return budgets;
    }

    /**
     * Adds a budget with the given parameters to the local list.
     * @param category The budgets category.
     * @param budget The budgets maximum amount.
     * @param startingDate The budgets starting date.
     * @param endingDate The budgets ending date.
     * @return The budget that was added to the list.
     */
    private Budget addBudgetLocally(String category, float budget, Date startingDate, Date endingDate) {
        startingDate.setHours(0);
        startingDate.setMinutes(0);
        startingDate.setSeconds(0);

        endingDate.setHours(23);
        endingDate.setMinutes(59);
        endingDate.setSeconds(59);

        float amount = getTransactionSum(category, startingDate, endingDate);

        Budget newBudget = new Budget(category, startingDate, endingDate, budget, amount);

        budgets.add(newBudget);
        return newBudget;
    }

    /**
     * Is called by the BudgetAddActivity, after the user added a budget. Adds it locally and to firebase.
     * @param category The budgets category.
     * @param budget The budgets maximum amount.
     * @param startingDate The budgets starting Date.
     * @param endingDate The budgets ending date.
     */
    public void addBudgetByUser(String category, float budget, Date startingDate, Date endingDate) {
        Budget b = addBudgetLocally(category, budget, startingDate, endingDate);
        b.setId(writeNewBudgetToFirebase(b));
        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
    }

    /**
     * Adds a budget with the given parameters to the local list.
     * @param category The budgets category.
     * @param budget The budgets maximum amount.
     * @param startingDate The budgets starting date.
     * @param renewalType The budgets renewal type.
     * @return The budget that was added to the list.
     */
    private Budget addBudgetLocally(String category, float budget, Date startingDate, Budget.RenewalTypes renewalType) {
        startingDate.setHours(0);
        startingDate.setMinutes(0);
        startingDate.setSeconds(0);

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

    /**
     * Is called by the BudgetAddActivity, after the user added a budget. Adds it locally and to firebase.
     * @param category The budgets category.
     * @param budget The budgets maximum amount.
     * @param startingDate The budgets starting Date.
     * @param renewalType The budgets renewal type.
     */
    public void addBudgetByUser(String category, float budget, Date startingDate, Budget.RenewalTypes renewalType) {
        Budget b = addBudgetLocally(category, budget, startingDate, renewalType);
        b.setId(writeNewBudgetToFirebase(b));
        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
    }

    /**
     * Adds a budget from firebase with a given id.
     * @param id The id of the budget to add.
     * @param budget The budget to add
     */
    public void addBudgetFromFirebase(String id, Budget budget) {
        budgets.add(budget);
        budget.setId(id);
        budget.setCurrentAmount(getTransactionSum(budget.category, budget.getFrom(), budget.getUntil()));
        renewBudgets();
        fireBudgetEvent(new BudgetEvent(BudgetEvent.EventType.UPDATED, this));
    }

    /**
     * Gets a budget by a given id.
     * @param id The id of the budget to get.
     * @return The budget associated with the id, null if there is none.
     */
    public Budget getById(String id) {
        for (Budget budget : budgets) {
            if (budget.getId().equals(id))
                return budget;
        }
        return null;
    }

    /**
     * Edits a budget by a given id.
     * @param id The id of the budget to edit.
     * @param category The new category of the budget.
     * @param budget The new limit.
     * @param startingDate the new starting date.
     * @param renewalType the new renewal type.
     */
    public void editById(String id, String category, float budget, Date startingDate, Budget.RenewalTypes renewalType) {
        Budget b = getById(id);
        if (b == null) return;

        b.setBudget(budget);
        b.setCategory(category);
        b.setFrom(startingDate);
        b.setRenewalType(renewalType);
        b.setUntil(addTimeByRenewalType(b.getFrom(), renewalType));


        b.setCurrentAmount(getTransactionSum(category, b.getFrom(), b.getUntil()));
        editInFirebase(b.getId(), b);
        renewBudgets();

    }

    /**
     * Edits a budget by a given id.
     * @param id The id of the budget to edit.
     * @param category The new category of the budget.
     * @param budget The new limit.
     * @param startingDate the new starting date.
     * @param customDate the new ending date.
     */
    public void editById(String id, String category, float budget, Date startingDate, Date customDate) {
        Budget b = getById(id);
        if (b == null) return;

        b.setBudget(budget);
        b.setCategory(category);
        b.setRenewalType(Budget.RenewalTypes.CUSTOM);
        b.setFrom(startingDate);
        b.setUntil(customDate);


        b.setCurrentAmount(getTransactionSum(category, b.getFrom(), b.getUntil()));
        editInFirebase(b.getId(), b);
        renewBudgets();

        editInFirebase(b.getId(), b);
        renewBudgets();

    }

    /**
     * Iterates through all transactions between startingDate and endingDate that belong to category,
     * sums up their amounts and returns the total.
     * @param category The category that the transactions must have
     * @param startingDate All transactions must be after this date to be taken into account.
     * @param endingDate All transactions must be before this date to be taken into account.
     * @return The total amount of all the transactions.
     */
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

    /**
     * Edits a budget of given id in firebase, by overwriting it.
     * @param id Id of the budget to edit
     * @param newBudget Budget to overwrite the old one with.
     */
    private void editInFirebase(String id, Budget newBudget) {
        budgetsRef.child(id).setValue(newBudget);
    }

    /**
     * Removes a budget of given id.
     * @param id Id of the budget to remove.
     */
    public void removeById(String id) {
        Budget b = getById(id);
        if (b == null) return;

        budgets.remove(b);
        budgetsRef.child(id).removeValue();
        renewBudgets();
    }

    /**
     * Removes a budget at a given place in the list
     * @param position The position of the budget to remove in the list.
     */
    public void removeAt(int position) {
        if (position < 0 || position >= budgets.size()) return;
        budgetsRef.child(budgets.get(position).getId()).removeValue();
        budgets.remove(position);
    }

    /**
     * Swaps two budgets in the list. Used to reorder budgets by the user.
     * @param fromPosition position of the first budget.
     * @param toPosition position of the second budget.
     */
    public void swap(int fromPosition, int toPosition) {
        Log.i("BUDGETSWAP:", "from: " + fromPosition + " to: " + toPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(budgets, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(budgets, i, i - 1);
            }
        }
    }


    /**
     * Writes a given budget to the firebase database.
     * @param budget The budget to write
     * @return The id of the written budget.
     */
    private String writeNewBudgetToFirebase(Budget budget) {
        String key = budgetsRef.push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, budget);
        budgetsRef.updateChildren(childUpdates);
        return key;
    }
}
