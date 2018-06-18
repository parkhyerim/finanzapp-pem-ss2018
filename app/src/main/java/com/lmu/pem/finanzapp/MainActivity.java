package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import com.lmu.pem.finanzapp.controller.PagerAdapter;
import com.lmu.pem.finanzapp.model.AccountManager;

public class MainActivity extends AppCompatActivity  {

    //implements DashboardFragment.OnFragmentInteractionListener, AccountFragment.OnFragmentInteractionListener, TransactionFragment.OnFragmentInteractionListener, BudgetFragment.OnFragmentInteractionListener

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), getApplicationContext()));
        TabLayout tabs = findViewById(R.id.tabLayoutId);
        tabs.setupWithViewPager(viewPager);
        tabs.getTabAt(0).setIcon(R.drawable.tab_dashboard_white);
        tabs.getTabAt(1).setIcon(R.drawable.tab_transactions_black);
        tabs.getTabAt(2).setIcon(R.drawable.tab_accounts_black);
        tabs.getTabAt(3).setIcon(R.drawable.tab_budgets_black);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        tab.setIcon(R.drawable.tab_dashboard_white);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.tab_transactions_white);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.tab_accounts_white);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.tab_budgets_white);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        tab.setIcon(R.drawable.tab_dashboard_black);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.tab_transactions_black);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.tab_accounts_black);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.tab_budgets_black);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = findViewById(R.id.menu_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        return true;
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}