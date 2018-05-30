package com.lmu.pem.finanzapp.controller;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.lmu.pem.finanzapp.views.TransactionFragment;
import com.lmu.pem.finanzapp.views.BudgetFragment;
import com.lmu.pem.finanzapp.views.DashboardFragment;
import com.lmu.pem.finanzapp.views.AccountFragment;

public class PagerAdapter extends FragmentPagerAdapter {


    public PagerAdapter(FragmentManager fm){
        super(fm);

    }


    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0: return new DashboardFragment();
            case 1: return new AccountFragment();
            case 2: return new TransactionFragment();
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
            case 0: return "Dashboard";
            case 1: return "Konten";
            case 2: return "Trans.";
            case 3: return "Budget";
        }
        return null;
    }
}
