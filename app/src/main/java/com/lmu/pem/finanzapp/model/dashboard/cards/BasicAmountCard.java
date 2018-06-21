package com.lmu.pem.finanzapp.model.dashboard.cards;

import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;

public class BasicAmountCard extends DbCard{



    public enum AmountType {POSITIVE, NEGATIVE, NEUTRAL}

    private float amount;
    private AmountType amountType;
    private String amountDescription;
    private String secondaryMessage;

    private String btn2Text;

    public BasicAmountCard(DashboardManager.CardType type, String title, String primaryText, float amount, AmountType amountType, String amountDescription, String secondaryMessage, String btn1Text, String btn2Text) {
        super(type, title, primaryText, btn1Text);
        this.amount = amount;
        this.amountType = amountType;
        this.amountDescription = amountDescription;
        this.secondaryMessage = secondaryMessage;
        this.btn2Text = btn2Text;
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


    public String getBtn2Text() {
        return btn2Text;
    }

    public void setBtn2Text(String btn2Text) {
        this.btn2Text = btn2Text;
    }
}
