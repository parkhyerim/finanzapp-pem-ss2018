package com.lmu.pem.finanzapp.model.cards;

public class BasicAmountCard extends DbCard{

    public enum CardType {WELCOME, HIGHESTEXPENSES, MOSTECONOMICALMONTH}

    public enum AmountType {POSITIVE, NEGATIVE, NEUTRAL}

    public CardType type;

    private float amount;
    private AmountType amountType;
    private String amountDescription;
    private String secondaryMessage;

    private String btn2Text;

    public BasicAmountCard(CardType type, String title, String primaryText, float amount, AmountType amountType, String amountDescription, String secondaryMessage, String btn1Text, String btn2Text) {
        super(title, primaryText, btn1Text);
        this.type = type;
        this.amount = amount;
        this.amountType = amountType;
        this.amountDescription = amountDescription;
        this.secondaryMessage = secondaryMessage;
        this.btn2Text = btn2Text;
    }

    public CardType getType() {
        return type;
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
