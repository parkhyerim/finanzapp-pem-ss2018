package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.app.SearchManager;
import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lmu.pem.finanzapp.controller.PagerAdapter;
import com.lmu.pem.finanzapp.model.GlobalSettings;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity  {

    private ViewPager viewPager;
    private GlobalSettings globalSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        globalSettings = GlobalSettings.getInstance();
        globalSettings.setContext(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupToolbar();
        setupTabs();
    }

    private void setupToolbar() {
        ImageView settingsButton = findViewById(R.id.action_settings);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = findViewById(R.id.menu_search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
    }

    private void setupTabs() {
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
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        tabs.getTabAt(globalSettings.getHomeTab()).select();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}