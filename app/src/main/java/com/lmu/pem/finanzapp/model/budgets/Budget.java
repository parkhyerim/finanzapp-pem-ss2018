package com.lmu.pem.finanzapp.model.budgets;

import com.lmu.pem.finanzapp.data.categories.Category;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Budget {
    String category;

    private Date from;
    private Date until;

    private float budget;

    private float currentAmount;

    public Budget(String category, Date from, Date until, float budget, float currentAmount) {
        this.category = category;
        this.from = from;
        this.until = until;
        this.budget = budget;
        this.currentAmount = currentAmount;
    }

    public String getCategory() {
        return category;
    }

    public Date getFrom() {
        return from;
    }

    public Date getUntil() {
        return until;
    }

    public float getBudget() {
        return budget;
    }

    public void setCurrentAmount(float currentAmount) {
        this.currentAmount = currentAmount;
    }

    public float getCurrentAmount() {
        return currentAmount;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> buffer = new HashMap<>();
        buffer.put("budgetCategory", this.category);
        buffer.put("budgetStartingDate", this.from);
        buffer.put("budgetEndingDate", this.until);
        buffer.put("budgetAmount", this.budget);
        return buffer;
    }
}
