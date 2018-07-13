package com.lmu.pem.finanzapp.model.budgets;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Budget implements Serializable {
    String id;
    String category;

    private Date from;
    private Date until;

    private float budget;

    private float currentAmount;

    private RenewalTypes renewalType;


    public enum RenewalTypes {
        CUSTOM,
        DAY,
        WEEK,
        MONTH,
        YEAR;

        public static String[] getNames() {
            String[] returnBuffer = new String[RenewalTypes.values().length];

            for (int i = 0; i < RenewalTypes.values().length; i++) {
                returnBuffer[i] = RenewalTypes.values()[i].toString().toLowerCase().substring(0, 1).toUpperCase()
                        + RenewalTypes.values()[i].toString().toLowerCase().substring(1);
            }

            return returnBuffer;
        }
    }

    public Budget() {}

    Budget(String category, Date from, Date until, float budget, float currentAmount, RenewalTypes renewalType) {
        this.category = category;
        this.from = from;
        this.until = until;
        this.budget = budget;
        this.currentAmount = currentAmount;
        this.renewalType = renewalType;
    }

    Budget(String category, Date from, Date until, float budget, float currentAmount) {
        this.category = category;
        this.from = from;
        this.until = until;
        this.budget = budget;
        this.currentAmount = currentAmount;
        this.renewalType = RenewalTypes.CUSTOM;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getUntil() {
        return until;
    }

    public void setUntil(Date until) {
        this.until = until;
    }

    public float getBudget() {
        return budget;
    }

    public void setBudget(float budget) {
        this.budget = budget;
    }

    public void setCurrentAmount(float currentAmount) {
        this.currentAmount = currentAmount;
    }

    public float getCurrentAmount() {
        return currentAmount;
    }

    public RenewalTypes getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(RenewalTypes renewalType) {
        this.renewalType = renewalType;
    }

    public float getDatePart() {
        float datePart = 1f;
        if (getUntil().getTime() != getFrom().getTime())
            datePart = ((float)(Calendar.getInstance().getTime().getTime() - getFrom().getTime()) / (getUntil().getTime() - getFrom().getTime()));
        return datePart;
    }

    public float getAmountPart() {
       return getCurrentAmount() / getBudget();
    }

}
