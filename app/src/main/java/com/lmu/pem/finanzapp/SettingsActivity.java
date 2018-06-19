package com.lmu.pem.finanzapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.lmu.pem.finanzapp.model.GlobalSettings;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final GlobalSettings globalSettings = GlobalSettings.getInstance();

        setupSpinners(globalSettings);
    }

    private void setupSpinners(final GlobalSettings globalSettings) {
        Spinner currencySpinner = (Spinner) findViewById(R.id.currencySpinner);
        ArrayAdapter<CharSequence> currencyAdapter = ArrayAdapter.createFromResource(this, R.array.currency_array, android.R.layout.simple_spinner_item);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currency = (String) parent.getItemAtPosition(position);
                globalSettings.setCurrency(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Spinner homeTabSpinner = (Spinner) findViewById(R.id.homeTabSpinner);
        ArrayAdapter<CharSequence> homeTabAdapter = ArrayAdapter.createFromResource(this, R.array.tabs_array, android.R.layout.simple_spinner_item);
        homeTabAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        homeTabSpinner.setAdapter(homeTabAdapter);
        homeTabSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                globalSettings.setHomeTab(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
