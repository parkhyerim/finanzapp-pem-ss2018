package com.lmu.pem.finanzapp.model;

import com.lmu.pem.finanzapp.data.categories.Category;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Budget {
    Category category;

    Date from;
    Date until;

    float budget;

    float currentAmount;

    public Budget(Category category, Date from, Date until, float budget, float currentAmount) {
        this.category = category;
        this.from = from;
        this.until = until;
        this.budget = budget;
        this.currentAmount = currentAmount;
    }

    public Category getCategory() {
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
