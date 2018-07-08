package com.lmu.pem.finanzapp.views;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.data.categories.Category;
import com.lmu.pem.finanzapp.data.categories.CategoryManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.budgets.Budget;
import com.lmu.pem.finanzapp.model.budgets.BudgetManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddBudgetActivity extends AppCompatActivity {


    //region View Handles
    TextView currencySymbol;
    EditText budgetEditText;
    Spinner categorySpinner;
    Spinner renewalTypeSpinner;

    LinearLayout customDateLayout;
    EditText customDateEditText;

    Button submitButton;
    //endregion



    String category;

    Budget.RenewalTypes renewalType;

    Date customDate;

    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViewHandles();
        initCategorySpinner();
        initRenewalTypeSpinner();
        initCustomDateEditText();
        initSubmitButton();

    }

    private void initViewHandles() {
        currencySymbol = findViewById(R.id.currencySymbol);

        budgetEditText = findViewById(R.id.budgetEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        renewalTypeSpinner = findViewById(R.id.renewalTypeSpinner);

        customDateLayout = findViewById(R.id.customDateLayout);
        customDateEditText = findViewById(R.id.customDateEditText);
        currencySymbol.setText(GlobalSettings.getInstance(this).getCurrencyString());

        submitButton = findViewById(R.id.submitButton);
    }



    private void initCategorySpinner() {
        ArrayList<String> expenses = new ArrayList<>(CategoryManager.getInstance().getExpCategories());
        ArrayAdapter<String> expenseCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenses);
        categorySpinner.setAdapter(expenseCategoryAdapter);
        expenseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = CategoryManager.getInstance().getExpCategories().get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initRenewalTypeSpinner () {
        String[] types = Budget.RenewalTypes.getNames();
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        renewalTypeSpinner.setAdapter(typesAdapter);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        renewalTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                renewalType = Budget.RenewalTypes.values()[position];
                setCustomDateActive(renewalType == Budget.RenewalTypes.NONE);
                Calendar c = Calendar.getInstance();
                c.setTime(c.getTime());
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
                }

                setCustomDateValue(c.getTime());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void initCustomDateEditText() {
        customDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(
                        AddBudgetActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        (view, y, m, d) -> setCustomDateValue(new Date(y, m, d)),
                        customDate.getYear() + 1900,
                        customDate.getMonth() + 1,
                        customDate.getDate());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();
            }
        });
    }

    private void setCustomDateActive(boolean active) {
        customDateEditText.setEnabled(active);
    }

    private void setCustomDateValue(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YY", Locale.getDefault());
        customDate = date;
        customDateEditText.setText(format.format(customDate));
    }

    private void initSubmitButton() {
        submitButton.setOnClickListener((v) -> submit());
    }

    private boolean verify() {
        boolean valid = true;
        if (budgetEditText.getText().toString().isEmpty()) {
            budgetEditText.setError("You need to set a budget!");
            valid = false;
        }
        //TODO CHECK SPINNERS

        return valid;
    }

    private void submit() {
        if (!verify()) return;

        if (renewalType == Budget.RenewalTypes.NONE)
            BudgetManager.getInstance().addBudget(category, getBudgetAmount(),customDate);
        else {
            BudgetManager.getInstance().addBudget(category, getBudgetAmount(), renewalType);
        }
    }

    private float getBudgetAmount() {
        return Float.parseFloat(budgetEditText.getText().toString());
    }
}
