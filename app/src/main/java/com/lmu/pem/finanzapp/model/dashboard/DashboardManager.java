package com.lmu.pem.finanzapp.model.dashboard;

import android.content.Context;
import android.util.Log;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.model.Analyzer;
import com.lmu.pem.finanzapp.model.dashboard.cards.BasicAmountCard;
import com.lmu.pem.finanzapp.model.dashboard.cards.DbCard;
import com.lmu.pem.finanzapp.model.dashboard.cards.WelcomeCard;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;

import java.text.DateFormatSymbols;
import java.util.AbstractMap;
import java.util.ArrayList;

public class DashboardManager implements TransactionHistoryEventListener {

    private static DashboardManager instance;

    @Override
    public void handle(TransactionHistoryEvent event) {
        refreshActiveCards(TransactionManager.getInstance());
    }

    public enum CardType {
        WELCOME,
        MEM,
        HIGHESTEXPENSE,
        DEBUG,
    }

    private Context context;

    private ArrayList<DbCard> activeCards;
    private ArrayList<DbCard> archivedCards;

    private DashboardManager() {
        TransactionManager.getInstance().addListener(this);
    }

    public static DashboardManager getInstance(Context context) {
        if (instance == null) {
            instance = new DashboardManager();
        }
        instance.context = context;

        return instance;
    }


    public ArrayList<DbCard> getDataSet(TransactionManager history) {
        if (activeCards != null)
            return activeCards;

        refreshActiveCards(history);
        return activeCards;

    }

    private void refreshActiveCards(TransactionManager history) {
        if (activeCards == null) activeCards = new ArrayList<>();
        activeCards.clear();
        Log.i("DashboardManager", "REFRESHED ACTIVE CARDS");
        Log.i("DashboardManager", "There are " + history.getTransactions().size() + " Transactions!");

        //TODO Be smart about any of this
        activeCards.add(createWelcomeCard());

        AbstractMap.SimpleEntry<Integer, Float> memData = Analyzer.calculateMostExpensiveMonthFor(history);
        if (memData != null) activeCards.add(createMemCard(memData.getKey(), memData.getValue()));
        activeCards.add(createHighestExpensesCard(123.45f, "Car"));
        activeCards.add(createDebugCard(history));
    }


    //region Card Factories

    private BasicAmountCard createHighestExpensesCard(float amount, String category) {
        String title = context.getString(R.string.highestexpenses_title);
        String primaryMessage = context.getString(R.string.highestexpenses_mainText) + " " + category + ":";
        String btn1Text = context.getString(R.string.highestexpenses_btn1);

        return new BasicAmountCard(
                CardType.HIGHESTEXPENSE,
                title,
                primaryMessage,
                amount,
                BasicAmountCard.AmountType.NEGATIVE,
                "",
                "",
                btn1Text,
                "");
    }

    private BasicAmountCard createMemCard(int month, float amount) {
        return new BasicAmountCard(
                CardType.MEM,
                context.getString(R.string.mem_title),
                context.getString(R.string.mem_mainText) + " " + new DateFormatSymbols().getMonths()[month] + ".",
                amount,
                BasicAmountCard.AmountType.NEGATIVE,
                context.getString(R.string.mem_amountDesc),
                context.getString(R.string.mem_secondaryText),
                context.getString(R.string.mem_btn1),
                ""
        );
    }

    private WelcomeCard createWelcomeCard() {
        return new WelcomeCard(
                CardType.WELCOME,
                context.getString(R.string.welcomecard_title),
                context.getString(R.string.welcomecard_text),
                context.getString(R.string.welcomecard_btn1),
                context.getString(R.string.welcomecard_btn2));
    }

    private DbCard createDebugCard(TransactionManager history) {
        float totalAmount = 0f;

        for (Transaction transaction : history.getTransactions()) {
            totalAmount += transaction.getAmount();
           // totalAmount -= transaction.getExpense();
        }
        Log.i("DashboardManager", "TotalAmount: " + totalAmount);

        BasicAmountCard.AmountType type = (totalAmount > 0f) ? BasicAmountCard.AmountType.POSITIVE : BasicAmountCard.AmountType.NEGATIVE;
        return new BasicAmountCard(
                CardType.DEBUG,
                "DEBUG",
                "Total Amount:",
                totalAmount,
                type,
                "",
                "is this right?",
                "YES",
                "");
    }

    //endregion


}
