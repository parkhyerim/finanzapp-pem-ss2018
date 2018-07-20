package com.lmu.pem.finanzapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lmu.pem.finanzapp.model.transactions.CategoryManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.budgets.Budget;
import com.lmu.pem.finanzapp.model.budgets.BudgetManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BudgetAddActivity extends AppCompatActivity {


    //region View Handles
    TextView currencySymbol;
    EditText budgetEditText;
    Spinner categorySpinner;

    EditText startingDateEditText;

    Spinner renewalTypeSpinner;

    EditText customDateEditText;

    Button submitButton;
    Button deleteButton;
    //endregion

    Budget budgetToEdit = null;

    String category;

    Budget.RenewalTypes renewalType;

    Date startingDate;
    Date customDate;

    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_add);

        Toolbar toolbar = findViewById(R.id.toolbar);

        if (getIntent().hasExtra("budgetToEdit")) {
            budgetToEdit = (Budget) getIntent().getSerializableExtra("budgetToEdit");
            toolbar.setTitle(R.string.budget_edit_title_edit);
        }

        setSupportActionBar(toolbar);
        initViewHandles();


        initStartingDateEditText();
        initCustomDateEditText();

        initAmountEditText();
        initCategorySpinner();
        initRenewalTypeSpinner();

        initSubmitButton();
        initDeleteButton();

    }

    private void initViewHandles() {
        currencySymbol = findViewById(R.id.currencySymbol);

        budgetEditText = findViewById(R.id.budgetEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        renewalTypeSpinner = findViewById(R.id.renewalTypeSpinner);
        startingDateEditText = findViewById(R.id.startingDateEditText);
        customDateEditText = findViewById(R.id.customDateEditText);
        currencySymbol.setText(GlobalSettings.getInstance().getCurrencyString());

        submitButton = findViewById(R.id.submitButton);
        deleteButton = findViewById(R.id.deleteButton);
    }

    private void initAmountEditText() {
        if (budgetToEdit != null)
            budgetEditText.setText(String.format(Locale.getDefault(), "%.2f", budgetToEdit.getBudget()));
    }

    private void initCategorySpinner() {
        ArrayList<String> categories = new ArrayList<>(CategoryManager.getInstance().getUIExpCategories());
        ArrayAdapter<String> expenseCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categorySpinner.setAdapter(expenseCategoryAdapter);

        expenseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (budgetToEdit != null) {
            categorySpinner.setSelection(categories.indexOf(budgetToEdit.getCategory()));
        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = CategoryManager.getInstance().getUIExpCategories().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initRenewalTypeSpinner() {
        String[] types = Budget.RenewalTypes.getNames();
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        renewalTypeSpinner.setAdapter(typesAdapter);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (budgetToEdit != null) {
            renewalTypeSpinner.setSelection(budgetToEdit.getRenewalType().ordinal());
            Log.i("RENEWAL:", budgetToEdit.getRenewalType().name());
        }
        renewalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renewalType = Budget.RenewalTypes.values()[position];
                setCustomDateActive(renewalType == Budget.RenewalTypes.CUSTOM);
                setCustomDateByRenewalType();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCustomDateByRenewalType() {
        Calendar c = Calendar.getInstance();

        c.setTime(startingDate);

        switch (renewalType) {
            case DAY:
                c.add(Calendar.DATE, 1);
                break;
            case WEEK:
                c.add(Calendar.DATE, 7);
                break;
            case MONTH:
                c.add(Calendar.MONTH, 1);
                break;
            case YEAR:
                c.add(Calendar.YEAR, 1);
                break;
            case CUSTOM:
                if (budgetToEdit != null) c.setTime(budgetToEdit.getUntil());
                break;
        }

        setCustomDateValue(c.getTime());
    }

    public void initStartingDateEditText() {
        if (budgetToEdit != null) {
            setStartingDateValue(budgetToEdit.getFrom());
        } else {
            setStartingDateValue(Calendar.getInstance().getTime());
        }
        startingDateEditText.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    BudgetAddActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (view, y, m, d) -> setStartingDateValue(new Date(y - 1900, m, d)),
                    startingDate.getYear() + 1900,
                    startingDate.getMonth(),
                    startingDate.getDate());
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
            dialog.show();
        });
    }

    public void initCustomDateEditText() {
        if (budgetToEdit != null) {
            setCustomDateValue(budgetToEdit.getUntil());
        }
        customDateEditText.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                    BudgetAddActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (view, y, m, d) -> setCustomDateValue(new Date(y - 1900, m, d)),
                    customDate.getYear() + 1900,
                    customDate.getMonth(),
                    customDate.getDate());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
            dialog.getDatePicker().setMinDate(startingDate.getTime());
            dialog.show();
        });
    }

    private void setCustomDateActive(boolean active) {
        customDateEditText.setEnabled(active);
    }

    private void setStartingDateValue(Date date) {
        startingDate = date;
        startingDateEditText.setText(GlobalSettings.GLOBAL_DATE_FORMAT.format(date));
        if (renewalType != null) setCustomDateByRenewalType();
    }

    private void setCustomDateValue(Date date) {
        customDate = date;
        customDateEditText.setText(GlobalSettings.GLOBAL_DATE_FORMAT.format(date));
    }

    private void initSubmitButton() {
        submitButton.setOnClickListener((v) -> submit());
    }

    private void initDeleteButton() {
        if (budgetToEdit == null)
            deleteButton.setVisibility(View.GONE);
        else {
            deleteButton.setOnClickListener((v) -> deleteBudget());
        }

    }

    private boolean verify() {
        boolean valid = true;



        if (startingDate.after(Calendar.getInstance().getTime())) {
            startingDateEditText.setError("Starting date can't lie in the future!");
            valid = false;
        }

        if (startingDate.after(customDate)) {
            customDateEditText.setError("Ending date has to be after starting date!");
            valid = false;
        }

        if (budgetEditText.getText().toString().isEmpty()) {
            budgetEditText.setError("You need to set a budget!");
            valid = false;
        }
        if (categorySpinner.getSelectedItem().equals("")) {
            TextView errorText = (TextView) categorySpinner.getSelectedView();
            errorText.setError("anything here, just to add the icon");
            valid = false;
        }

        for (Budget budget : BudgetManager.getInstance().getBudgets()) {
            if (budget.getCategory().equals(category)) {
                String error = String.format(Locale.getDefault(),
                        "You already have a %s budget of %.2f %s",
                        category,
                        budget.getBudget(), GlobalSettings.getInstance().getCurrencyString());
                budgetEditText.setError(error);
                valid = false;
                break;
            }
        }

        return valid;
    }

    private void deleteBudget() {
        if (budgetToEdit != null) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            builder.setTitle("Delete budget")
                    .setMessage("Are you sure you want to delete this budget?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        BudgetManager.getInstance().removeById(budgetToEdit.getId());
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

    }

    private void submit() {
        if (!verify()) return;

        if (budgetToEdit != null) {
            if (renewalType != Budget.RenewalTypes.CUSTOM)
                BudgetManager.getInstance().editById(budgetToEdit.getId(), category, getBudgetAmount(), startingDate, renewalType);
            else
                BudgetManager.getInstance().editById(budgetToEdit.getId(), category, getBudgetAmount(), startingDate, customDate);
            finish();
            return;
        }

        if (renewalType == Budget.RenewalTypes.CUSTOM)
            BudgetManager.getInstance().addBudgetByUser(category, getBudgetAmount(), startingDate, customDate);
        else
            BudgetManager.getInstance().addBudgetByUser(category, getBudgetAmount(), startingDate, renewalType);

        setResult(0);
        finish();
    }

    private float getBudgetAmount() {
        return Float.parseFloat(budgetEditText.getText().toString());
    }
}
