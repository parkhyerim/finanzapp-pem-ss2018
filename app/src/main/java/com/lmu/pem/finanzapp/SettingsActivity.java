package com.lmu.pem.finanzapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lmu.pem.finanzapp.model.GlobalSettings;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final GlobalSettings globalSettings = GlobalSettings.getInstance();
        RadioGroup currencyGroup = findViewById(R.id.currencyToggleGroup);
        currencyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.euroToggle:
                        Toast.makeText(SettingsActivity.this, "EURO", Toast.LENGTH_SHORT).show();
                        globalSettings.setCurrency("€");
                        break;
                    case R.id.dollarToggle:
                        Toast.makeText(SettingsActivity.this, "DOLLAR", Toast.LENGTH_SHORT).show();
                        globalSettings.setCurrency("$");
                        break;
                    case R.id.poundToggle:
                        Toast.makeText(SettingsActivity.this, "POUND", Toast.LENGTH_SHORT).show();
                        globalSettings.setCurrency("£");
                        break;
                }
            }
        });

        RadioGroup homeTabGroup = findViewById(R.id.homeTabToggleGroup);
        homeTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Toast.makeText(SettingsActivity.this, checkedId+"", Toast.LENGTH_SHORT).show();
                switch (checkedId){
                    case R.id.dbTabToggle:
                        Toast.makeText(SettingsActivity.this, "DASH", Toast.LENGTH_SHORT).show();
                        globalSettings.setHomeTab(GlobalSettings.TAB_DASHBOARD);
                        break;
                    case R.id.transTabToggle:
                        Toast.makeText(SettingsActivity.this, "TRANS", Toast.LENGTH_SHORT).show();
                        globalSettings.setHomeTab(GlobalSettings.TAB_TRANSACTIONS);
                        break;
                    case R.id.accTabToggle:
                        Toast.makeText(SettingsActivity.this, "ACC", Toast.LENGTH_SHORT).show();
                        globalSettings.setHomeTab(GlobalSettings.TAB_ACCOUNTS);
                        break;
                    case R.id.budgTabToggle:
                        Toast.makeText(SettingsActivity.this, "BUDGET", Toast.LENGTH_SHORT).show();
                        globalSettings.setHomeTab(GlobalSettings.TAB_BUDGETS);
                        break;
                }
            }
        });
    }
}
