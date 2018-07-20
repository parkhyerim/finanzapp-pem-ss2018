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

    /**
     * The singleton instance
     */
    private static DashboardManager instance;

    /**
     * A handle to the adapter that's responsible for displaying the cards. Used to notify it when
     * the data set is updated
     */
    private CardAdapter adapterHandle;

    /**
     * Holds the value of different card types to the user. Is not used to its fullest extent, as it
     * doesn't get saved persistently, but the framework is there if it should be implemented in the
     * future
     */
    private HashMap<CardType, Integer> cardTypeValues = new HashMap<>();


    /**
     * The types of cards that exist.
     */
    public enum CardType {
        WELCOME,
        SWIPETUTORIAL,
        HIGHESTINCOME,
        HIGHESTEXPENSE,
        BUDGETWARNING,
        BUDGETFAILED,
    }

    /**
     * The context, in which the DashboardManager is used.
     */
    private Context context;

    /**
     * The cards on display currently.
     */
    private ArrayList<DbCard> activeCards;

    /**
     * The cards that have been dismissed by the user.
     */
    private ArrayList<DbCard> archivedCards = new ArrayList<>();

    /**
     * Private constructor for the singleton instance. Adds this as listener to the transactions and
     * budgets, so it can react to updates.
     * @param context The current context.
     */
    private DashboardManager(Context context) {
        this.context = context;
        TransactionManager.getInstance().addListener(this);
        BudgetManager.getInstance().addListener(this);
        reset();
    }

    /**
     * Resets the users preferences and reloads all the cards.
     */
    public void reset() {
        cardTypeValues.clear();
        archivedCards.clear();

        refreshActiveCards();
    }

    /**
     * Reacts to a TransactionHistoryEvent to update the cards when a change to the transactions
     * occured.
     * @param event The event, that was fired by the TransactionManager.
     */
    @Override
    public void handle(TransactionHistoryEvent event) {
        refreshActiveCards();
    }

    /**
     * Reacts to a BudgetEvent to update the cards when a change to the budgets
     * occured.
     * @param event The event, that was fired by the BudgetManager.
     */
    @Override
    public void handle(BudgetEvent event) {
        refreshActiveCards();
    }

    /**
     * Public getter for the singleton instance. Needs to be given the current context for certain
     * functionalities.
     * @param context The context, in which the DashboardManager is used.
     * @return Returns the singleton instance.
     */
    public static DashboardManager getInstance(Context context) {
        if (instance == null) {
            instance = new DashboardManager(context);
        }
        instance.context = context;

        return instance;
    }


    /**
     * Public getter for the DataSet (all cards meant to be displayed).
     * @return An ArrayList of all active Cards.
     */
    public ArrayList<DbCard> getDataSet() {
        if (activeCards != null)
            return activeCards;

        refreshActiveCards();
        return activeCards;

    }

    /**
     * Sets a CardAdapter as the responsible listener for this DashboardManager, so it gets notified
     * on Updates to the DataSet.
     * @param cardAdapter The CardAdapter to be notified.
     */
    public void setAdapterListener(CardAdapter cardAdapter) {
        adapterHandle = cardAdapter;
    }

    /**
     * Main method. Reloads all active cards while using the specific factories. Takes preference
     * and sensibility into account.
     */
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

    /**
     * Dismisses a given card and notifies the adapter that it has to reload the view.
     * Also updates the users preference to take into account, that this card is not liked.
     * @param card The card that is dismissed.
     */
    public void dismissCard(DbCard card) {
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

    /**
     * Checks if a given CardType should be displayed to the user, according to his preference.
     * @param t The CardType that is checked against user preference.
     * @return True, if the CardType should be displayed. False, if not.
     */
    private boolean isCardTypeWanted(CardType t) {
        return (getCardTypeValue(t) > 0 || getCardTypeValue(t) == -1);
    }

    /**
     * Sets a value to a given type, to accomodate user preference.
     * @param type The type to set a value to.
     * @param value The value of this type to the user.
     */
    private void setCardTypeValue(CardType type, int value) {
        cardTypeValues.put(type, value);
    }

    /**
     * Gets a value for a given type, to check the user preference.
     * @param type The type to get the value from
     * @return  The value of this type to the user. -1 if there is no specified value yet.
     */
    private int getCardTypeValue(CardType type) {
        if (cardTypeValues.containsKey(type))
            return cardTypeValues.get(type);

        return -1;
    }

    //region Card Factories



    /**
     * The Main Factory for all cards. Takes a card type as an argument and calls the corresponding
     * factory.
     * @param type The CardType to be displayed.
     */
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

    /**
     * The factory for the highest income card. If it is useful, it calculates the values and adds
     * the card to the active cards.
     */
    private void highestIncomeCardFactory() {
        HashMap<String, Float> i = Analyzer.calculateBestIncomeCategory(TransactionManager.getInstance());
        String iCat = "";
        if (!i.isEmpty())
            iCat = i.keySet().iterator().next();

        if (!iCat.isEmpty())
            activeCards.add(createHighestIncomeCardLayout(iCat, i.get(iCat)));
    }

    /**
     * Creates and returns a new AmountCard of type HighestIncome with a given category and amount.
     * @param category The category, that the user got the most money from.
     * @param amount The amount he got from on that category.
     * @return The BasicAmountCard representing the HighestIncomeCard.
     */
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

    /**
     * The factory for the highest expense card. If it is useful, it calculates the values and adds
     * the card to the active cards.
     */
    private void highestExpenseCardFactory() {

        HashMap<String, Float> h = Analyzer.calculateMostExpensiveCategory(TransactionManager.getInstance());
        String cat = "";
        if (!h.isEmpty())
            cat = h.keySet().iterator().next();

        if (!cat.isEmpty())
            activeCards.add(createHighestExpensesCardLayout(cat, h.get(cat)));
    }

    /**
     * Creates and returns a new AmountCard of type HighestExpense with a given category and amount.
     * @param category The category, that the user spent the most money on.
     * @param amount The amount he spent on that category.
     * @return The BasicAmountCard representing the HighestExpenseCard.
     */
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

    /**
     * The factory for the welcome card. If it is useful, it adds
     * the card to the active cards.
     */
    private void welcomeCardFactory() {
        if (activeCards.size() == 0) activeCards.add(createWelcomeCardLayout());
    }

    /**
     * Creates and returns a new WelcomeCard.
     * @return The WelcomeCard.
     */
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

    /**
     * The factory for the swipe tutorial card. If it is useful, it adds
     * the card to the active cards.
     */
    private void swipeTutorialFactory() {
        if (cardTypeValues.containsKey(CardType.SWIPETUTORIAL) && cardTypeValues.get(CardType.SWIPETUTORIAL) == 0)
            return;
        if (activeCards.size() > 1 || (activeCards.size() == 1 && activeCards.get(0).getType() != CardType.WELCOME))
            return;
        if (TransactionManager.getInstance().getTransactions().size() > 0)
            return;
        activeCards.add(createSwipeTutorialCardLayout());
    }

    /**
     * Creates and returns a new Swipe Tutorial Card.
     * @return The WelcomeCard.
     */
    private WelcomeCard createSwipeTutorialCardLayout() {
        return new WelcomeCard(
                CardType.SWIPETUTORIAL,
                context.getString(R.string.swipetut_title),
                context.getString(R.string.swipetut_text),
                ""
        );
    }

    /**
     * The factory for the budget warning card. If it is useful, it calculates the values and adds
     * the card to the active cards.
     */
    private void budgetWarningCardFactory() {
        for (Budget budget : Analyzer.getBudgetsOver(0f, true, false)) {
            activeCards.add(createBudgetWarningCardLayout(budget));
        }
    }


    /**
     * Creates and returns a new AmountCard of type BudgetWarninf with a given category and amount.
     * @param budget The budget, that the user should be warned about.
     * @return The BasicAmountCard representing the BudgetWarningCard.
     */
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

    /**
     * The factory for the budget failed card. If it is useful, it calculates the values and adds
     * the card to the active cards.
     */
    private void budgetFailedCardFactory() {

        for (Budget budget : Analyzer.getBudgetsOver(1f, true, true)) {
            activeCards.add(createBudgetFailedCardLayout(budget));
        }
    }

    /**
     * Creates and returns a new AmountCard of type BudgetFailed with a given category and amount.
     * @param budget The budget, that the user has exceeded.
     * @return The BasicAmountCard representing the BudgetFailedCard.
     */
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
