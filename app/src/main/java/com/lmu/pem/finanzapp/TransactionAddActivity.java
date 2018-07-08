package com.lmu.pem.finanzapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormatSymbols;


import com.lmu.pem.finanzapp.data.categories.CategoryManager;
import com.lmu.pem.finanzapp.model.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionAddActivity extends AppCompatActivity {

    private static final int SELECTED_EXPENSE = 1;
    private static final int SELECTED_INCOME = 2;
    private static final int SELECTED_SHIFT = 3;

    private double amount = 0;
    private String category, account, account2, description, key;
    private int year, month, day;
    private Calendar cal;

    private LinearLayout expenseLayout, incomeLayout, transactionAddLayout, accountLine2;
    private EditText amountEditText, descriptionEditText;
    private Button expenseButton, incomeButton, shiftButton, doneButton;
    private Spinner accountSpinner, account2Spinner, expenseCategorySpinner, incomeCategorySpinner;
    private TextView dateDisplay, currencySymbol, accountTextView, account2TextView;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private AccountManager accountManager;
    private CategoryManager categoryManager;
    private Context context;


    private ArrayList<String> expenses = new ArrayList<>();
    private ArrayList<String> incomes = new ArrayList<>();

    private int selection = SELECTED_EXPENSE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_add);
        context = this;

        accountManager = AccountManager.getInstance();
        categoryManager = CategoryManager.getInstance();

        findViews();

        currencySymbol.setText(GlobalSettings.getInstance(this).getCurrencyString());

        expense_selected(); // expense is selected by default

        setupDatePicker();
        setupAccountSpinners();
        setupCategorySpinners();

        checkIfUpdating();

        setButtonClickListeners();
    }

    private void checkIfUpdating() {
        // extras indicate that we want to edit an existing transaction instead of creating a new one
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            year = getIntent().getIntExtra("year", 2018);
            month = getIntent().getIntExtra("month", 1);
            day = getIntent().getIntExtra("day", 1);
            description = getIntent().getStringExtra("description");
           // dateDisplay.setText(month + "/" + day + "/" + year);
            setDateOnDisplay(year, month, day);

            descriptionEditText.setText(description);
            // TODO: Expense and Income
            amount = getIntent().getDoubleExtra("amount", 0);
            amountEditText.setText(String.valueOf(Math.abs(amount)));
            if(amount<0){
                expenseButton.callOnClick();
            } else {
                incomeButton.callOnClick();
            }

            // Account
            String account = accountManager.getAccountById(getIntent().getStringExtra("account")).getName();
            String[] accounts =  accountManager.getNameArray();
            for(int i =0; i < accounts.length ; i++){
                if (accounts[i].equals(account)){
                    accountSpinner.setSelection(i);
                } else {
                    accountSpinner.setSelection(0);
                }
            }

            if(getIntent().hasExtra("account2")) {
                String account2 = getIntent().getStringExtra("account2");
                shiftButton.callOnClick();
                for(int i =0; i < accounts.length ; i++){
                    if (accounts[i].equals(account)){
                        account2Spinner.setSelection(i);
                    } else {
                        account2Spinner.setSelection(0);
                    }
                }
            }

            // Category
            if(getIntent().hasExtra("category")){
                expense_selected();
                int expense = getIntent().getIntExtra("category", 0);
                expenseCategorySpinner.setSelection(expense);
            } else {
                income_selected();
                int income = getIntent().getIntExtra("category2",0);
                incomeCategorySpinner.setSelection(income);
            }

            //Key
            key = getIntent().getStringExtra("key");

            //customize Toolbar title
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Edit Transaction");
        }
    }

    private void setButtonClickListeners() {
        expenseButton.setOnClickListener(v -> {
            expense_selected();
        });

        incomeButton.setOnClickListener(v -> {
            income_selected();
        });

        shiftButton.setOnClickListener(v -> {
            shift_selected();
        });


        doneButton.setOnClickListener(v -> {
                if(dateDisplay.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please choose a date.", Toast.LENGTH_LONG).show();

                } else if (amountEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please enter the amount.", Toast.LENGTH_LONG).show();

                } else if(accountSpinner.getSelectedItem().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please choose an account.", Toast.LENGTH_LONG).show();
                } else if(account2Spinner.getSelectedItem().toString().equals("") && selection==SELECTED_SHIFT){
                    Toast.makeText(TransactionAddActivity.this, "Please choose a second account.", Toast.LENGTH_LONG).show();
                } else if(expenseCategorySpinner.getSelectedItem().toString().equals("") && incomeCategorySpinner.getSelectedItem().toString().equals("") && selection!=SELECTED_SHIFT){
                    Toast.makeText(TransactionAddActivity.this, "Please choose an category.", Toast.LENGTH_LONG).show();

                }  else {
                    amount = Double.parseDouble(amountEditText.getText().toString());
                    if(selection==SELECTED_EXPENSE){
                        amount = -1 * amount;
                    }

                    description = descriptionEditText.getText().toString();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("year", year);
                    resultIntent.putExtra("month", month+1);
                    resultIntent.putExtra("day", day);
                    resultIntent.putExtra("category", category);
                    resultIntent.putExtra("account", account);
                    resultIntent.putExtra("account2", account2);
                    resultIntent.putExtra("amount", amount);
                    resultIntent.putExtra("description", description);
                    resultIntent.putExtra("key", key);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
        });
    }

    private void setupCategorySpinners() {
        // Expense-Category
        expenses.addAll(categoryManager.getExpCategories());
        ArrayAdapter<String> expenseCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenses);
        // ArrayAdapter<CharSequence> expenseCategoryAdapter = ArrayAdapter.createFromResource(this, R.array.expense_category, android.R.layout.simple_spinner_item);
        expenseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(expenseCategoryAdapter);
        expenseCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_SHORT).show();
                category = (String) expenseCategorySpinner.getSelectedItem();
                if(category.equals("Add")){
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                    // set dialog message
                    AlertDialog.Builder builder = alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    (dialog, id1) -> {
                                        // get user input and set it to result
                                        // edit text

                                        String newItem = userInput.getText().toString();
                                        int pos = expenses.size()-1;
                                        expenses.add(pos, newItem);

                                        expenseCategoryAdapter.notifyDataSetChanged();
                                        category = parent.getItemAtPosition(pos).toString();
                                    })
                            .setNegativeButton("Cancel",
                                    (dialog, id12) -> dialog.cancel());

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                    //Toast.makeText(getBaseContext(), expenseCategorySpinner.getSelectedItemPosition() + " selected", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Income-Category
        //String[] incomeArray = getResources().getStringArray(R.array.income_category);
        if(incomes.isEmpty()) {
           // incomes.addAll(Arrays.asList(getResources().getStringArray(R.array.income_category)));
            incomes.addAll(categoryManager.getIncCategories());
        }
        // ArrayAdapter<CharSequence> incomeCategoryAdapter = ArrayAdapter.createFromResource(this, R.array.income_category, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> incomeCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomes);

        incomeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeCategorySpinner.setAdapter(incomeCategoryAdapter);
        incomeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_SHORT).show();

                if(incomeCategorySpinner.getSelectedItemPosition() == incomes.size()-1){
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                    // set dialog message
                    AlertDialog.Builder builder = alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    (dialog, id14) -> {
                                        // get user input and set it to result
                                        // edit text
                                        String newItem = userInput.getText().toString();
                                        int pos = incomes.size()-1;
                                        incomes.add(pos, newItem);

                                        incomeCategoryAdapter.notifyDataSetChanged();
                                        category = parent.getItemAtPosition(pos).toString();

                                    })
                            .setNegativeButton("Cancel",
                                    (dialog, id13) -> dialog.cancel());

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupDatePicker() {
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        setDateOnDisplay(year, month, day);

        dateSetListener = (view, year, month, day) -> {
            this.year = year;
            this.month = month;
            this.day = day;
            setDateOnDisplay(year, month+1, day);
        };
        dateDisplay.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(TransactionAddActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
            dialog.show();
        });
    }

    private void setupAccountSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountManager.getNameArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(adapter);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account = accountManager.getAccountIdByName(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        account2Spinner.setAdapter(adapter);
        account2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account2 = accountManager.getAccountIdByName(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void findViews() {
        dateDisplay = findViewById(R.id.dateDisplay_textView);
        currencySymbol = findViewById(R.id.expenseAdd_textView);
        accountTextView = findViewById(R.id.account_textView);
        account2TextView = findViewById(R.id.account2_textView);

        amountEditText = findViewById(R.id.expenseAdd_editText);
        descriptionEditText = findViewById(R.id.description_editView);

        incomeLayout = findViewById(R.id.catLine_income);
        expenseLayout = findViewById(R.id.catLine_expense);
        transactionAddLayout = findViewById(R.id.transaction_add_layout);
        accountLine2 = findViewById(R.id.accountLine2);

        expenseCategorySpinner = findViewById(R.id.category_Spinner);
        incomeCategorySpinner = findViewById(R.id.category_Spinner2);
        accountSpinner = findViewById(R.id.account_spinner);
        account2Spinner = findViewById(R.id.account2_spinner);

        expenseButton = findViewById(R.id.expense_button);
        incomeButton = findViewById(R.id.income_button);
        shiftButton = findViewById(R.id.shift_button);
        doneButton = findViewById(R.id.done_button);
    }


    public void categoryShow(){
        if(selection==SELECTED_INCOME){
            income_selected();
        } else {
            expense_selected();
        }
    }

    public void expense_selected() {
        selection = SELECTED_EXPENSE;
        expenseButton.setAlpha(1.0f);
        incomeButton.setAlpha(0.5f);
        shiftButton.setAlpha(0.5f);
        expenseLayout.setEnabled(true);
        incomeLayout.setEnabled(false);
        accountLine2.setEnabled(false);
        incomeLayout.setVisibility(View.GONE);
        accountLine2.setVisibility(View.GONE);
        expenseLayout.setVisibility(View.VISIBLE);
        accountTextView.setText("Account");
        //transactionAddLayout.setBackgroundColor(Color.parseColor("#F8E0E0"));
    }

    public void income_selected() {
        selection = SELECTED_INCOME;
        incomeButton.setAlpha(1.0f);
        expenseButton.setAlpha(0.5f);
        shiftButton.setAlpha(0.5f);
        incomeLayout.setEnabled(true);
        expenseLayout.setEnabled(false);
        accountLine2.setEnabled(false);
        expenseLayout.setVisibility(View.GONE);
        accountLine2.setVisibility(View.GONE);
        incomeLayout.setVisibility(View.VISIBLE);
        accountTextView.setText("Account");
        //transactionAddLayout.setBackgroundColor(Color.parseColor("#F1F8E0"));
    }

    public void shift_selected(){
        selection = SELECTED_SHIFT;
        shiftButton.setAlpha(1.0f);
        expenseButton.setAlpha(0.5f);
        incomeButton.setAlpha(0.5f);
        expenseLayout.setEnabled(false);
        incomeLayout.setEnabled(false);
        accountLine2.setEnabled(true);
        expenseLayout.setVisibility(View.GONE);
        incomeLayout.setVisibility(View.GONE);
        accountLine2.setVisibility(View.VISIBLE);
        accountTextView.setText("Account (From)");
        category = Transaction.CATEGORY_SHIFT;
    }

    public void setDateOnDisplay(int year, int month, int day) {
        String monthStr = getMonth(month);
        dateDisplay.setText(monthStr + " "  + day + ", " + year);

        /*
        if (day < 10 && month < 10) { //TODO replace with number formatter
            dateDisplay.setText("0" + month + "/" + "0" + day + "/" + year);

        } else if (month < 10) {
            dateDisplay.setText("0" + month + "/" + day + "/" + year);

        } else if (day < 10) {
            dateDisplay.setText(month + "/" + "0" + day + "/" + year);

        } else {
            dateDisplay.setText(monthStr + "/" + day + "/" + year);
        }
        */

    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

}
