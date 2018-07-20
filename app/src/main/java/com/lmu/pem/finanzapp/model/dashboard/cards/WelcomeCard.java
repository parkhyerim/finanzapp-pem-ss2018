package com.lmu.pem.finanzapp.model.dashboard.cards;

import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;

public class WelcomeCard extends DbCard {

    private String btnText;

    public WelcomeCard (DashboardManager.CardType type, String title, String primaryText, String btnText) {
        super(type, title, primaryText);
        this.btnText = btnText;
    }

    public String getBtnText() {
        return btnText;
    }
}
