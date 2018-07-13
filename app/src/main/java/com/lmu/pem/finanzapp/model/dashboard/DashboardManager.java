package com.lmu.pem.finanzapp.model.dashboard;

import android.content.Context;
import android.util.Log;
import android.widget.Adapter;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.CardAdapter;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.budgets.Budget;
import com.lmu.pem.finanzapp.model.budgets.BudgetEvent;
import com.lmu.pem.finanzapp.model.budgets.BudgetEventListener;
import com.lmu.pem.finanzapp.model.budgets.BudgetManager;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DashboardManager extends DashboardEventSource implements TransactionHistoryEventListener, BudgetEventListener {

    private static DashboardManager instance;

    private CardAdapter adapterHandle;



    public enum CardType {
        WELCOME,
        MEM,
        HIGHESTEXPENSE,
        BUDGETWARNING,
        BUDGETFAILED,
    }

    private Context context;

    private ArrayList<DbCard> activeCards;
    private ArrayList<DbCard> archivedCards;

    private DashboardManager() {
        TransactionManager.getInstance().addListener(this);
        BudgetManager.getInstance().addListener(this);
    }

    @Override
    public void handle(TransactionHistoryEvent event) {
        refreshActiveCards(TransactionManager.getInstance());
    }

    @Override
    public void handle(BudgetEvent event) {

        refreshActiveCards(TransactionManager.getInstance());

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

    public void setAdapterListener(CardAdapter cardAdapter) {
        adapterHandle = cardAdapter;
    }

    private void refreshActiveCards(TransactionManager history) {
        if (activeCards == null) activeCards = new ArrayList<>();
        activeCards.clear();
        Log.i("DashboardManager", "REFRESHED ACTIVE CARDS");
        Log.i("DashboardManager", "There are " + history.getTransactions().size() + " Transactions!");

        //TODO Be smart about any of this

        TransactionManager manager = TransactionManager.getInstance();

        HashMap<String, Float> h = Analyzer.calculateMostExpensiveCategory(manager);
        String cat = h.keySet().iterator().next();

        if (!cat.isEmpty())
            activeCards.add(createHighestExpensesCard(cat, h.get(cat)));

        for (Budget budget : Analyzer.getBudgetsOver(0f, true, false)) {
            activeCards.add(createBudgetWarningCard(budget));
        }

        for (Budget budget : Analyzer.getBudgetsOver(1f, true, true)) {
            activeCards.add(createBudgetFailedCard(budget));
        }

        if (adapterHandle != null) adapterHandle.notifyDataSetChanged();
        fireDashboardEvent(new DashboardEvent(this, DashboardEvent.EventType.UPDATED));
    }


    //region Card Factories

    private BasicAmountCard createHighestExpensesCard(String category, float amount) {
        String title = context.getString(R.string.highestexpenses_title);
        String primaryMessage = context.getString(R.string.highestexpenses_mainText) + " " + category + ":";
        String btn1Text = context.getString(R.string.highestexpenses_btn1);

        return new BasicAmountCard(
                CardType.HIGHESTEXPENSE,
                title,
                primaryMessage,
                Math.abs(amount),
                BasicAmountCard.AmountType.NEGATIVE,
                "",
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
                context.getString(R.string.mem_secondaryText)
        );
    }

    private BasicAmountCard createBudgetWarningCard(Budget budget) {
        String title = context.getString(R.string.bw_title) + " " + budget.getCategory() + " " + context.getString(R.string.bw_title2);

        long days = Analyzer.getBudgetDays(budget);
        float extraDays = Analyzer.getBudgetExtrapolationInDays(budget);

        String main = context.getString(R.string.bw_mainText) + " " + days + " days, you spent";
        String amountDesc = context.getString(R.string.bw_amountDesc)
                + String.format(Locale.getDefault(), " %.2f %s",budget.getBudget(), GlobalSettings.getInstance().getCurrencyString());

        String extraDaysString = String.format(Locale.getDefault(), " %.1f ", extraDays);
        String secondaryText =  context.getString(R.string.bw_secondaryText1) + extraDaysString + context.getString(R.string.bw_secondaryText2);

        return new BasicAmountCard(CardType.BUDGETWARNING, title, main, budget.getCurrentAmount(),
                BasicAmountCard.AmountType.WARNING, amountDesc, secondaryText);

    }

    private BasicAmountCard createBudgetFailedCard(Budget budget) {
        String title = context.getString(R.string.bf_title) + " " + budget.getCategory() + " " + context.getString(R.string.bf_title2);

        long days = Analyzer.getBudgetDays(budget);
        float extraDays = Analyzer.getBudgetExtrapolationInDays(budget);

        String main = context.getString(R.string.bf_mainText1) + " " + budget.getCategory() + " "+ context.getString(R.string.bf_mainText2);
        String amountDesc = context.getString(R.string.bf_amountDesc)
                + String.format(Locale.getDefault(), " %.2f %s",budget.getBudget(), GlobalSettings.getInstance().getCurrencyString());

        String extraDaysString = String.format(Locale.getDefault(), " %.1f ", extraDays -  days);
        String secondaryText =  context.getString(R.string.bf_secondaryText1) + extraDaysString + context.getString(R.string.bf_secondaryText2);

        return new BasicAmountCard(CardType.BUDGETFAILED, title, main, budget.getCurrentAmount(),
                BasicAmountCard.AmountType.NEGATIVE, amountDesc, secondaryText);

    }

    //endregion


}
