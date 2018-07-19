package com.lmu.pem.finanzapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.transactions.CategoryManager;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    private GlobalSettings globalSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        globalSettings = GlobalSettings.getInstance();
        setupLoggedInText();
        setupLogoutButton();
        setupToolbar();
        setupSpinners();
    }

    private void setupLogoutButton() {
        Button btn = findViewById(R.id.logoutButton);
        btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void setupLoggedInText() {
        TextView tv = findViewById(R.id.loggedInAsText);
        tv.setText(getString(R.string.settings_loggedInAs)+ FirebaseAuth.getInstance().getCurrentUser().getEmail());
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

        CategoryManager categoryManager = CategoryManager.getInstance();
        final ArrayList<String> uiExpCategories = categoryManager.getUIExpCategories();
        final ArrayList<String> uiIncCategories = categoryManager.getUIIncCategories();

        Spinner deleteCatsExpSpinner = (Spinner) findViewById(R.id.deleteCatsExpSpinner);
        ArrayAdapter<String> deleteExpAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, uiExpCategories);
        deleteExpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteCatsExpSpinner.setAdapter(deleteExpAdapter);
        deleteCatsExpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = uiExpCategories.get(position);
                if(cat.equals("")) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                if(TransactionManager.getInstance().transactionsForExpenseCategory(cat)){
                    builder.setTitle("Can't delete category")
                            .setMessage("You can't delete a category with existing transactions! You have to delete the linked transactions before you can delete this category.")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                deleteCatsExpSpinner.setSelection(0);
                            })
                            .show();
                }else {
                    builder.setTitle("Delete category")
                            .setMessage("Are you sure you want to delete the expense category " + cat + "?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                categoryManager.deleteExpenseCategory(cat);
                                uiExpCategories.clear();
                                uiExpCategories.addAll(categoryManager.getUIExpCategories());
                                deleteExpAdapter.notifyDataSetChanged();
                                deleteCatsExpSpinner.setSelection(0);
                            })
                            .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                deleteCatsExpSpinner.setSelection(0);
                            })
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Spinner deleteCatsIncSpinner = (Spinner) findViewById(R.id.deleteCatsIncSpinner);
        ArrayAdapter<String> deleteIncAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, uiIncCategories);
        deleteIncAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deleteCatsIncSpinner.setAdapter(deleteIncAdapter);
        deleteCatsIncSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat = uiIncCategories.get(position);
                if(cat.equals("")) return;
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                if(TransactionManager.getInstance().transactionsForIncomeCategory(cat)){
                    builder.setTitle("Can't delete category")
                            .setMessage("You can't delete a category with existing transactions! You have to delete the linked transactions before you can delete this category.")
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                deleteCatsIncSpinner.setSelection(0);
                            })
                            .show();
                }else {
                    builder.setTitle("Delete category")
                            .setMessage("Are you sure you want to delete the income category " + cat + "?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                categoryManager.deleteIncomeCategory(cat);
                                uiIncCategories.clear();
                                uiIncCategories.addAll(categoryManager.getUIIncCategories());
                                deleteIncAdapter.notifyDataSetChanged();
                                deleteCatsIncSpinner.setSelection(0);
                            })
                            .setNegativeButton(android.R.string.no, (dialog, which) -> {
                                deleteCatsIncSpinner.setSelection(0);
                            })
                            .show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
