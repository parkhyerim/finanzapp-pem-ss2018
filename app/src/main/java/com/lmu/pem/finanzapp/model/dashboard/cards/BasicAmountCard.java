package com.lmu.pem.finanzapp.model.dashboard.cards;

import android.util.Log;

import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;

public class BasicAmountCard extends DbCard {


    public enum AmountType {POSITIVE, NEGATIVE, WARNING, NEUTRAL}

    private float amount;
    private AmountType amountType;
    private String amountDescription;
    private String secondaryMessage;


    public BasicAmountCard(DashboardManager.CardType type, String title, String primaryText, float amount, AmountType amountType, String amountDescription, String secondaryMessage) {
        super(type, title, primaryText);
        this.amount = amount;
        this.amountType = amountType;
        this.amountDescription = amountDescription;
        this.secondaryMessage = secondaryMessage;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public AmountType getAmountType() {
        return amountType;
    }

    public void setAmountType(AmountType amountType) {
        this.amountType = amountType;
    }

    public String getAmountDescription() {
        return amountDescription;
    }

    public void setAmountDescription(String amountDescription) {
        this.amountDescription = amountDescription;
    }

    public String getSecondaryMessage() {
        return secondaryMessage;
    }

    public void setSecondaryMessage(String secondaryMessage) {
        this.secondaryMessage = secondaryMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicAmountCard))
            return false;
        BasicAmountCard card = (BasicAmountCard) obj;
        Log.i("ACTIVECARD", "comparing " + card.getTitle());

        return (card.getTitle().equals(this.getTitle()) &&
                card.getPrimaryText().equals(this.getPrimaryText()) &&
                card.getAmount() == this.getAmount() &&
                card.getAmountDescription().equals(this.getAmountDescription()) &&
                card.getAmountType().equals(this.getAmountType()));
    }
}
