package com.lmu.pem.finanzapp.model.dashboard;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class DashboardManager extends DashboardEventSource implements TransactionHistoryEventListener, BudgetEventListener {

    /***
     * The singleton instance
     */
    private static DashboardManager instance;

    private CardAdapter adapterHandle;

    private HashMap<CardType, Integer> cardTypeValues = new HashMap<>();


    public enum CardType {
        WELCOME,
        SWIPETUTORIAL,
        HIGHESTINCOME,
        HIGHESTEXPENSE,
        BUDGETWARNING,
        BUDGETFAILED,
    }

    private Context context;

    private ArrayList<DbCard> activeCards;
    private ArrayList<DbCard> archivedCards = new ArrayList<>();

    private DashboardManager(Context context) {
        this.context = context;
        TransactionManager.getInstance().addListener(this);
        BudgetManager.getInstance().addListener(this);
        reset();
    }

    public void reset() {
        cardTypeValues.clear();
        archivedCards.clear();

        refreshActiveCards();
    }

    @Override
    public void handle(TransactionHistoryEvent event) {
        refreshActiveCards();
    }

    @Override
    public void handle(BudgetEvent event) {
        refreshActiveCards();
    }

    public static DashboardManager getInstance(Context context) {
        if (instance == null) {
            instance = new DashboardManager(context);
        }
        instance.context = context;

        return instance;
    }


    public ArrayList<DbCard> getDataSet() {
        if (activeCards != null)
            return activeCards;

        refreshActiveCards();
        return activeCards;

    }

    public void setAdapterListener(CardAdapter cardAdapter) {
        adapterHandle = cardAdapter;
    }

    private void refreshActiveCards() {
        if (activeCards == null) activeCards = new ArrayList<>();
        activeCards.clear();


        for (CardType cardType : CardType.values()) {
            if (isCardTypeWanted(cardType))
                cardFactory(cardType);
        }

        //===
        welcomeCardFactory();
        swipeTutorialFactory();

        ArrayList<DbCard> removalBuffer = new ArrayList<>();
        for (DbCard activeCard : activeCards) {
            for (DbCard archivedCard : archivedCards) {
                if (activeCard.equals(archivedCard)) removalBuffer.add(activeCard);
                Log.i("ACTIVECARD", activeCard.getTitle() + " and " + archivedCard.getTitle() + " : " + activeCard.equals(archivedCard));
            }
        }
        activeCards.removeAll(removalBuffer);

        if (adapterHandle != null) adapterHandle.notifyDataSetChanged();
        fireDashboardEvent(new DashboardEvent(this, DashboardEvent.EventType.UPDATED));
    }

    public void deleteCard(DbCard card) {
        archivedCards.add(card);
        boolean stillOthers = false;
        for (DbCard activeCard : activeCards) {
            if (activeCard.getType() == card.getType() && !activeCard.equals(card)) {
                stillOthers = true;
                break;
            }
        }
        if (!stillOthers)
            setCardTypeValue(card.getType(), 0);
        refreshActiveCards();
    }

    //region Card Factories

    private boolean isCardTypeWanted(CardType t) {
        return (getCardTypeValue(t) > 0 || getCardTypeValue(t) == -1);
    }

    private void cardFactory(CardType type) {
        if (type == null) return;

        switch (type) {
            case HIGHESTINCOME:
                highestIncomeCardFactory();
                break;
            case HIGHESTEXPENSE:
                highestExpenseCardFactory();
                break;
            case BUDGETWARNING:
                budgetWarningCardFactory();
                break;
            case BUDGETFAILED:
                budgetFailedCardFactory();
                break;
        }
    }

    private void highestIncomeCardFactory() {
        HashMap<String, Float> i = Analyzer.calculateBestIncomeCategory(TransactionManager.getInstance());
        String iCat = "";
        if (!i.isEmpty())
            iCat = i.keySet().iterator().next();

        if (!iCat.isEmpty())
            activeCards.add(createHighestIncomeCardLayout(iCat, i.get(iCat)));
    }

    private BasicAmountCard createHighestIncomeCardLayout(String category, float amount) {
        String title = context.getString(R.string.highestincome_title);
        String primaryMessage = context.getString(R.string.highestincome_mainText) + " " + category + ":";

        return new BasicAmountCard(
                CardType.HIGHESTINCOME,
                title,
                primaryMessage,
                Math.abs(amount),
                BasicAmountCard.AmountType.POSITIVE,
                "this year.",
                "");
    }

    private void highestExpenseCardFactory() {

        HashMap<String, Float> h = Analyzer.calculateMostExpensiveCategory(TransactionManager.getInstance());
        String cat = "";
        if (!h.isEmpty())
            cat = h.keySet().iterator().next();

        if (!cat.isEmpty())
            activeCards.add(createHighestExpensesCardLayout(cat, h.get(cat)));
    }

    private BasicAmountCard createHighestExpensesCardLayout(String category, float amount) {
        String title = context.getString(R.string.highestexpenses_title);
        String primaryMessage = context.getString(R.string.highestexpenses_mainText) + " " + category + ":";

        return new BasicAmountCard(
                CardType.HIGHESTEXPENSE,
                title,
                primaryMessage,
                Math.abs(amount),
                BasicAmountCard.AmountType.NEGATIVE,
                "",
                "");
    }

    private void welcomeCardFactory() {
        if (activeCards.size() == 0) activeCards.add(createWelcomeCardLayout());
    }

    private WelcomeCard createWelcomeCardLayout() {
        if (TransactionManager.getInstance().getTransactions().size() == 0)
            return new WelcomeCard(
                CardType.WELCOME,
                context.getString(R.string.welcomecard_title),
                context.getString(R.string.welcomecard_text),
                context.getString(R.string.welcomecard_btn1));
        else
            return new WelcomeCard(CardType.WELCOME,
                    context.getString(R.string.welcomecard_title_swiped),
                    context.getString(R.string.welcomecard_text_swiped),
                    context.getString(R.string.welcomecard_btn1));
    }

    private void swipeTutorialFactory() {
        if (cardTypeValues.containsKey(CardType.SWIPETUTORIAL) && cardTypeValues.get(CardType.SWIPETUTORIAL) == 0)
            return;
        if (activeCards.size() > 1 || (activeCards.size() == 1 && activeCards.get(0).getType() != CardType.WELCOME))
            return;
        if (TransactionManager.getInstance().getTransactions().size() > 0)
            return;
        activeCards.add(createSwipeTutorialCardLayout());
    }

    private WelcomeCard createSwipeTutorialCardLayout() {
        return new WelcomeCard(
                CardType.SWIPETUTORIAL,
                context.getString(R.string.swipetut_title),
                context.getString(R.string.swipetut_text),
                ""
        );
    }

    private void budgetWarningCardFactory() {
        for (Budget budget : Analyzer.getBudgetsOver(0f, true, false)) {
            activeCards.add(createBudgetWarningCardLayout(budget));
        }
    }

    public void setCardTypeValue(CardType type, int value) {
        cardTypeValues.put(type, value);
    }

    private int getCardTypeValue(CardType type) {
        if (cardTypeValues.containsKey(type))
            return cardTypeValues.get(type);

        return -1;
    }


    private BasicAmountCard createBudgetWarningCardLayout(Budget budget) {
        String title = context.getString(R.string.bw_title) + " " + budget.getCategory() + " " + context.getString(R.string.bw_title2);

        long days = Analyzer.getBudgetDays(budget);
        float overshootAmount = Analyzer.getOvershootAmount(budget);

        String main = context.getString(R.string.bw_mainText) + " " + days + " days, you spent";
        String amountDesc = context.getString(R.string.bw_amountDesc)
                + String.format(Locale.getDefault(), " %.2f %s", budget.getBudget(), GlobalSettings.getInstance().getCurrencyString());

        String extraDaysString = String.format(Locale.getDefault(), " %.2f %s", overshootAmount, GlobalSettings.getInstance().getCurrencyString());
        String secondaryText = context.getString(R.string.bw_secondaryText1) + extraDaysString;

        return new BasicAmountCard(CardType.BUDGETWARNING, title, main, budget.getCurrentAmount(),
                BasicAmountCard.AmountType.WARNING, amountDesc, secondaryText);

    }

    private void budgetFailedCardFactory() {

        for (Budget budget : Analyzer.getBudgetsOver(1f, true, true)) {
            activeCards.add(createBudgetFailedCardLayout(budget));
        }
    }

    private BasicAmountCard createBudgetFailedCardLayout(Budget budget) {
        String title = context.getString(R.string.bf_title) + " " + budget.getCategory() + " " + context.getString(R.string.bf_title2);

        long days = Analyzer.getBudgetDays(budget);
        float extraDays = Analyzer.getBudgetExtrapolationInDays(budget);

        String main = context.getString(R.string.bf_mainText1) + " " + budget.getCategory() + " " + context.getString(R.string.bf_mainText2);
        String amountDesc = context.getString(R.string.bf_amountDesc)
                + String.format(Locale.getDefault(), " %.2f %s", budget.getBudget(), GlobalSettings.getInstance().getCurrencyString());

        String extraDaysString = String.format(Locale.getDefault(), " %.1f ", extraDays - days);
        String secondaryText = context.getString(R.string.bf_secondaryText1) + extraDaysString + context.getString(R.string.bf_secondaryText2);

        return new BasicAmountCard(CardType.BUDGETFAILED, title, main, budget.getCurrentAmount(),
                BasicAmountCard.AmountType.NEGATIVE, amountDesc, secondaryText);

    }

    //endregion


}
