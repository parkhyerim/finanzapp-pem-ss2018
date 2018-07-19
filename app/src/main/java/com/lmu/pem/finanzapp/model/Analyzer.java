package com.lmu.pem.finanzapp.model;

import com.lmu.pem.finanzapp.model.transactions.CategoryManager;
import com.lmu.pem.finanzapp.model.budgets.Budget;
import com.lmu.pem.finanzapp.model.budgets.BudgetManager;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;

import java.util.ArrayList;
import java.util.HashMap;

public  class Analyzer {

    public static HashMap<String, Float> calculateMostExpensiveCategory (TransactionManager manager) {
        ArrayList<String> cat = CategoryManager.getInstance().getPureExpCategories();

        float [] categoryCounters = new float[cat.size()];

        for (Transaction transaction : manager.getTransactions()) {
            if (cat.contains(transaction.getCategory()))
                categoryCounters[cat.indexOf(transaction.getCategory())] += transaction.getAmount();
        }

        int mostExpensive = 0;
        for (int i = 0; i < categoryCounters.length; i++) {
            float counter = categoryCounters[i];
            if (counter < categoryCounters[mostExpensive]) {
                mostExpensive = i;
            }
        }


        HashMap<String, Float> h = new HashMap<>();
        h.put(cat.get(mostExpensive), categoryCounters[mostExpensive]);
        return h;
    }

    public static HashMap<String, Float> calculateBestIncomeCategory (TransactionManager manager) {
        ArrayList<String> cat = CategoryManager.getInstance().getPureIncCategories();

        float [] categoryCounters = new float[cat.size()];

        for (Transaction transaction : manager.getTransactions()) {
            if (cat.contains(transaction.getCategory()))
                categoryCounters[cat.indexOf(transaction.getCategory())] += transaction.getAmount();
        }

        int mostExpensive = 0;
        for (int i = 0; i < categoryCounters.length; i++) {
            float counter = categoryCounters[i];
            if (counter > categoryCounters[mostExpensive]) {
                mostExpensive = i;
            }
        }


        HashMap<String, Float> h = new HashMap<>();
        if (cat.size() != 0)
            h.put(cat.get(mostExpensive), categoryCounters[mostExpensive]);
        return h;
    }

    public static ArrayList<Budget> getBudgetsOver(float threshold, boolean onlyIfOverTime, boolean includeOvershot) {
        ArrayList<Budget> buffer = new ArrayList<>();
        for (Budget budget : BudgetManager.getInstance().getBudgets()) {
            if (budget.getAmountPart() > threshold) {
                if (budget.getAmountPart() >= 1f && includeOvershot) {
                    if (onlyIfOverTime) {
                        if (budget.getAmountPart() > budget.getDatePart()) buffer.add(budget);
                    }
                    else {
                        buffer.add(budget);
                    }
                }
                else if (budget.getAmountPart() < 1f) {
                    if (onlyIfOverTime) {
                        if (budget.getAmountPart() > budget.getDatePart()) buffer.add(budget);
                    }
                    else {
                        buffer.add(budget);
                    }
                }
            }
        }
        return buffer;
    }

    public static long getBudgetDays(Budget budget) {
        long milis = budget.getUntil().getTime() - budget.getFrom().getTime();
        return milis / 1000 / 60 / 60 / 24;
    }

    public static float getBudgetExtrapolationInDays(Budget budget) {
        long days = getBudgetDays(budget);

        return ((budget.getAmountPart() / budget.getDatePart())* days);
    }

    public static float getOvershootAmount(Budget budget) {
        return (budget.getAmountPart()/budget.getDatePart()) * budget.getCurrentAmount();
    }
}
