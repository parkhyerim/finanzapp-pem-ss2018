package com.lmu.pem.finanzapp.model.dashboard.cards;

public class WelcomeCard extends DbCard {

    private String btn2Text;

    public WelcomeCard (String title, String primaryText, String btn1Text, String btn2Text) {
        super(title, primaryText, btn1Text);
        this.btn2Text = btn2Text;

    }

    public String getBtn2Text() {
        return btn2Text;
    }

    public void setBtn2Text(String btn2Text) {
        this.btn2Text = btn2Text;
    }
}
