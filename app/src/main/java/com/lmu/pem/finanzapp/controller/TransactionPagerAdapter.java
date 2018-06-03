package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.annotation.Nullable;

import com.lmu.pem.finanzapp.views.ExpenseFragment;
import com.lmu.pem.finanzapp.views.IncomeFragment;

public class TransactionPagerAdapter extends FragmentPagerAdapter {

    public Context context;

    public TransactionPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0: return new IncomeFragment();
            case 1: return new ExpenseFragment();

        }
        return null;

    }

    @Override
    public int getCount() {
        return 2;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0: return "Income";
            case 1: return "Expense";
        }
        return null;
    }
}
