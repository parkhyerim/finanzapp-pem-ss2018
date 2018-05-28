package com.lmu.pem.finanzapp.controller;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lmu.pem.finanzapp.views.BudgTab;
import com.lmu.pem.finanzapp.views.DbTab;
import com.lmu.pem.finanzapp.views.KtoTab;
import com.lmu.pem.finanzapp.views.TransTab;

public class PagerAdapter extends FragmentPagerAdapter {


    public PagerAdapter(FragmentManager fm){
        super(fm);

    }


    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0: return new DbTab();
            case 1: return new KtoTab();
            case 2: return new TransTab();
            case 3: return new BudgTab();
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
