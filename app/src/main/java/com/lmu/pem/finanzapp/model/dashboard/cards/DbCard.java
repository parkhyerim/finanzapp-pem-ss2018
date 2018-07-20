package com.lmu.pem.finanzapp.model.dashboard.cards;

import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;

public abstract class DbCard {

    DashboardManager.CardType type;

     String title;
     String primaryText;

    public DbCard(DashboardManager.CardType type, String title, String primaryText) {
        this.type = type;
        this.title = title;
        this.primaryText = primaryText;
    }

    public DashboardManager.CardType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public void setPrimaryText(String primaryText) {
        this.primaryText = primaryText;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DbCard))
            return false;
        DbCard card = (DbCard)obj;
        return (card.title.equals(this.title) && card.primaryText.equals(this.primaryText) && card.type.equals(this.type));
    }
}
