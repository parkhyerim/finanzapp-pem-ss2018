package com.lmu.pem.finanzapp.model.dashboard;

import android.content.Context;

import com.lmu.pem.finanzapp.model.dashboard.cards.DbCard;

import java.util.ArrayList;

public class DashboardManager {
    private static DashboardManager instance;

    private ArrayList<DbCard> activeCards;
    private ArrayList<DbCard> archivedCards;





    public static DashboardManager getInstance () {
        if (instance == null) return new DashboardManager();
        else return instance;
    }

}
