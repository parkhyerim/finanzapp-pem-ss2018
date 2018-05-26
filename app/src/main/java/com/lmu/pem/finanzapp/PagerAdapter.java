package com.lmu.pem.finanzapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int numOfTabs;

    public PagerAdapter(FragmentManager fm, int NumerOfTabs){
        super(fm);
        this.numOfTabs = NumerOfTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                DbTab tab1 = new DbTab();
                return tab1;
            case 1:
                KtoTab tab2 = new KtoTab();
                return tab2;

            case 2:
                TransTab tab3 = new TransTab();
                return tab3;

            case 3:
                BudgTab tab4 = new BudgTab();
                return tab4;

            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
