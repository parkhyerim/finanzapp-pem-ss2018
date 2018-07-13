package com.lmu.pem.finanzapp.model;

import android.util.Log;

import com.lmu.pem.finanzapp.data.categories.CategoryManager;
import com.lmu.pem.finanzapp.model.budgets.Budget;
import com.lmu.pem.finanzapp.model.budgets.BudgetManager;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public  class Analyzer {

    public static HashMap<String, Float> calculateMostExpensiveCategory (TransactionManager manager) {
        ArrayList<String> cat = CategoryManager.getInstance().getExpCategories();

        float [] categoryCounters = new float[cat.size()];

        for (Transaction transaction : manager.getTransactions()) {
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

    public static ArrayList<Budget> getBudgetsOver(float threshold, boolean onlyIfOverTime) {
        ArrayList<Budget> buffer = new ArrayList<>();
        for (Budget budget : BudgetManager.getInstance().getBudgets()) {
            if (!onlyIfOverTime && budget.getAmountPart() > threshold) buffer.add(budget);
            else if (onlyIfOverTime && budget.getAmountPart() > threshold && budget.getAmountPart() > budget.getDatePart()) buffer.add(budget);
        }
        return buffer;
    }
}
