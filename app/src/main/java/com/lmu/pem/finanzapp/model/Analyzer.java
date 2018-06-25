package com.lmu.pem.finanzapp.model;

import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistory;

import java.util.AbstractMap;
import java.util.Calendar;

public class Analyzer {

    public static AbstractMap.SimpleEntry<Integer, Float> calculateMostExpensiveMonthFor (TransactionHistory history) {
        float [] monthsCounters = new float[12];

        for (Transaction transaction : history.getTransactions()) {
            if (transaction.getYear() != Calendar.getInstance().get(Calendar.YEAR)) continue;
            monthsCounters[transaction.getMonth()] += transaction.getAmount();
        }

        int maxMonth = -1;
        float maxMonthAmount = 0;

        for (int i = 0; i < monthsCounters.length; i++) {

            if (maxMonthAmount < monthsCounters[i]) {
                maxMonth = i;
                maxMonthAmount = monthsCounters[i];
            }

        }

        if (maxMonth == -1) return null;

        return new AbstractMap.SimpleEntry<Integer, Float>(maxMonth, maxMonthAmount);

    }

}
