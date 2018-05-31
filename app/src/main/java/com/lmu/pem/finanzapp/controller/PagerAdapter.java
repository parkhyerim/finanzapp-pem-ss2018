package com.lmu.pem.finanzapp.controller;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lmu.pem.finanzapp.R;
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

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0: return context.getString(R.string.tab_1_label);
            case 1: return context.getString(R.string.tab_2_label);
            case 2: return context.getString(R.string.tab_3_label);
            case 3: return context.getString(R.string.tab_4_label);
        }
        return null;
    }
}
