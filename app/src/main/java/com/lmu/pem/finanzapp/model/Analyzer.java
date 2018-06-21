package com.lmu.pem.finanzapp.model;

import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistory;

import java.util.AbstractMap;

public class Analyzer {

    public static AbstractMap.SimpleEntry<Integer, Float> calculateMostExpensiveMonthFor (TransactionHistory history) {
        float [] monthsCounters = new float[12];

        for (Transaction transaction : history.getTransactions()) {
            monthsCounters[transaction.getMonth()] += transaction.getExpense();
        }

        int maxMonth = -1;
        float maxMonthAmount = -1;

        for (int i = 0; i < monthsCounters.length; i++) {



            if (maxMonthAmount < monthsCounters[i]) {
                maxMonth = i;
                maxMonthAmount = monthsCounters[i];
            }

        }

        return new AbstractMap.SimpleEntry<Integer, Float>(maxMonth, maxMonthAmount);

    }

}
