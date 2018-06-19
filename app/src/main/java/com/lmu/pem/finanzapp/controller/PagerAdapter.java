package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lmu.pem.finanzapp.views.TransactionFragment;
import com.lmu.pem.finanzapp.views.BudgetFragment;
import com.lmu.pem.finanzapp.views.DashboardFragment;
import com.lmu.pem.finanzapp.views.AccountFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public PagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0: return new DashboardFragment();
            case 1: return new TransactionFragment();
            case 2: return new AccountFragment();
            case 3: return new BudgetFragment();
        }
        return null;

    }

    @Override
    public int getCount() {
        return 4;
    }
}
