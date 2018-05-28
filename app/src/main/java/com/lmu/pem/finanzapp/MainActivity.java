package com.lmu.pem.finanzapp;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lmu.pem.finanzapp.controller.PagerAdapter;

public class MainActivity extends AppCompatActivity  {

    //implements DbTab.OnFragmentInteractionListener, KtoTab.OnFragmentInteractionListener, TransTab.OnFragmentInteractionListener, BudgTab.OnFragmentInteractionListener

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        TabLayout tabs = findViewById(R.id.tabLayoutId);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabTextColors(Color.GRAY, Color.parseColor("white"));


    }
}