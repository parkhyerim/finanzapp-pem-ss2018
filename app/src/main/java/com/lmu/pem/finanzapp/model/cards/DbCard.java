package com.lmu.pem.finanzapp.model.cards;

public class DbCard {

     String title;
     String primaryText;
     String btn1Text;

    public DbCard(String title, String primaryText, String btn1Text) {
        this.title = title;
        this.primaryText = primaryText;
        this.btn1Text = btn1Text;
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
