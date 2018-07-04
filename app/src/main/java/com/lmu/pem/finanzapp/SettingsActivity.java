package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.lmu.pem.finanzapp.model.GlobalSettings;

public class SettingsActivity extends AppCompatActivity {
    private GlobalSettings globalSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        globalSettings = GlobalSettings.getInstance(this);
        setupToolbar();
        setupSpinners();
    }

    private void setupToolbar() {
        ImageView homeButton = findViewById(R.id.action_done);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    private void setupSpinners() {
        Spinner currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this, R.array.currency_array, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);
        currencySpinner.setSelection(globalSettings.getCurrency());
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                globalSettings.setCurrency(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner homeTabSpinner = (Spinner) findViewById(R.id.homeTabSpinner);
        ArrayAdapter<CharSequence> homeTabAdapter = ArrayAdapter.createFromResource(this, R.array.tabs_array, android.R.layout.simple_spinner_item);
        homeTabAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeTabSpinner.setAdapter(homeTabAdapter);
        homeTabSpinner.setSelection(globalSettings.getHomeTab());
        homeTabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                globalSettings.setHomeTab(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
