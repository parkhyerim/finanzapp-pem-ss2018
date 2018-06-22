package com.lmu.pem.finanzapp.model.dashboard.cards;

import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;

public abstract class DbCard {

    DashboardManager.CardType type;

     String title;
     String primaryText;
     String btn1Text;

    public DbCard(DashboardManager.CardType type, String title, String primaryText, String btn1Text) {
        this.type = type;
        this.title = title;
        this.primaryText = primaryText;
        this.btn1Text = btn1Text;
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

    public String getBtn1Text() {
        return btn1Text;
    }

    public void setBtn1Text(String btn1Text) {
        this.btn1Text = btn1Text;
    }
}
