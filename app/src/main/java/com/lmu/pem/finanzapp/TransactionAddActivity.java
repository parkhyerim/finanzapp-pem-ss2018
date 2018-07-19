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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormatSymbols;


import com.lmu.pem.finanzapp.model.transactions.CategoryManager;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.transactions.Transaction;

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

    private LinearLayout expenseLayout, incomeLayout, accountLine2;
    private EditText amountEditText, descriptionEditText;
    private Button expenseButton, incomeButton, shiftButton, doneButton;
    private Spinner accountSpinner, account2Spinner, expenseCategorySpinner, incomeCategorySpinner;
    private TextView dateDisplay, currencySymbol, accountTextView;
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

        currencySymbol.setText(GlobalSettings.getInstance().getCurrencyString());

        expense_selected(); // expense is selected by default

        setupDatePicker();
        setupAccountSpinners();
        setupCategorySpinners();
        setButtonClickListeners();
        checkForExtras();
    }

    private void checkForExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if(getIntent().hasExtra("year") && getIntent().hasExtra("month") && getIntent().hasExtra("day")){
                year = getIntent().getIntExtra("year", 2018);
                month = getIntent().getIntExtra("month", 1);
                day = getIntent().getIntExtra("day", 1);
                setDateOnDisplay(year, month, day);
            }
            if(getIntent().hasExtra("description")){
                description = getIntent().getStringExtra("description");
                descriptionEditText.setText(description);
            }
            if(getIntent().hasExtra("amount")){
                amount = getIntent().getDoubleExtra("amount", 0);
                amountEditText.setText(String.valueOf(Math.abs(amount)));
                if(amount<0){
                    expenseButton.callOnClick();
                } else {
                    incomeButton.callOnClick();
                }

                //If we have an amount, the Transaction already exists - so customize the Toolbar title
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle(R.string.transaction_add_title_edit);
            }

            // Accounts
            String[] accounts =  accountManager.getNameArray(); //need to go by names here since this is the same order the spinner will load the accounts in (with default account in the first spot)

            if(getIntent().hasExtra("account")) {
                account = getIntent().getStringExtra("account");
                for (int i = 0; i < accounts.length; i++) {
                    if (accountManager.getAccountIdByName(accounts[i]).equals(account)) {
                        accountSpinner.setSelection(i);
                    }
                }
            }

            if(getIntent().hasExtra("account2")) {
                account2 = getIntent().getStringExtra("account2");
                shiftButton.callOnClick();
                for(int i =0; i < accounts.length ; i++){
                    if (accountManager.getAccountIdByName(accounts[i]).equals(account2)){
                        account2Spinner.setSelection(i);
                    }
                }
            }

            // Category
            if(getIntent().hasExtra("category")){
                expense_selected();
                int expense = getIntent().getIntExtra("category", 0);
                expenseCategorySpinner.setSelection(expense);
                category = expenseCategorySpinner.getItemAtPosition(expense).toString();
            } else if(getIntent().hasExtra("category2")) {
                income_selected();
                int income = getIntent().getIntExtra("category2",0);
                incomeCategorySpinner.setSelection(income);
                category = incomeCategorySpinner.getItemAtPosition(income).toString();
            }

            //Key - ultimately this is the extra that indicates that the transaction already exists and we just want to edit it
            if(getIntent().hasExtra("key")) key = getIntent().getStringExtra("key");
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
            boolean valid=true;
            if (amountEditText.getText().toString().equals("") || Double.valueOf(amountEditText.getText().toString()) == 0.0){
                valid=false;
                amountEditText.setError("Please enter a valid amount.");
                amountEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        amountEditText.setError(null);
                        amountEditText.removeTextChangedListener(this);
                    }
                });
            }
            if(expenseCategorySpinner.getSelectedItem().toString().equals("") && incomeCategorySpinner.getSelectedItem().toString().equals("") && selection!=SELECTED_SHIFT){
                valid=false;
                TextView errorText;
                if(selection==SELECTED_EXPENSE){
                    errorText = (TextView) expenseCategorySpinner.getSelectedView();
                }else{
                    errorText = (TextView) incomeCategorySpinner.getSelectedView();
                }
                errorText.setError("Please select a valid category.");
                errorText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        errorText.setError(null);
                        errorText.removeTextChangedListener(this);
                    }
                });
            }
            if(selection==SELECTED_SHIFT && (accountSpinner.getSelectedItemPosition()==account2Spinner.getSelectedItemPosition())){
                valid=false;
                TextView errorText1 = (TextView) accountSpinner.getSelectedView();
                TextView errorText2 = (TextView) account2Spinner.getSelectedView();
                String errTxt = "Same account selected twice!";
                errorText1.setError(errTxt);
                errorText2.setError(errTxt);
            }
            if(valid){
                amount = Double.parseDouble(amountEditText.getText().toString());
                if(selection==SELECTED_EXPENSE){
                    amount = -1 * amount;
                }

                description = descriptionEditText.getText().toString();
                if(description.equals("")) description = category;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("year", year);
                resultIntent.putExtra("month", month);
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
        expenses.addAll(categoryManager.getUIExpCategories());
        ArrayAdapter<String> expenseCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenses);
        expenseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(expenseCategoryAdapter);
        expenseCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                category = (String) expenseCategorySpinner.getSelectedItem();
                if(category.equals("Add")){
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.transaction_prompts, null);
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
                                        if(newItem.length()<1 || categoryManager.getUIExpCategories().contains(newItem)) return;
                                        categoryManager.addExpenseCategory(newItem, true);
                                        expenses.clear();
                                        expenses.addAll(categoryManager.getUIExpCategories());
                                        int pos = expenses.indexOf(newItem);
                                        expenseCategoryAdapter.notifyDataSetChanged();
                                        parent.setSelection(pos);
                                        category = parent.getItemAtPosition(pos).toString();
                                    })
                            .setNegativeButton("Cancel",
                                    (dialog, id12) -> dialog.cancel());

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Income-Category
        if(incomes.isEmpty()) {
            incomes.addAll(categoryManager.getUIIncCategories());
        }
        ArrayAdapter<String> incomeCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incomes);

        incomeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeCategorySpinner.setAdapter(incomeCategoryAdapter);
        incomeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();

                if(incomeCategorySpinner.getSelectedItemPosition() == incomes.size()-1){
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.transaction_prompts, null);
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
                                        if(newItem.length()<1 || categoryManager.getUIIncCategories().contains(newItem)) return;
                                        categoryManager.addIncomeCategory(newItem, true);
                                        incomes.clear();
                                        incomes.addAll(categoryManager.getUIIncCategories());
                                        int pos = incomes.indexOf(newItem);
                                        incomeCategoryAdapter.notifyDataSetChanged();
                                        parent.setSelection(pos);
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
        month = cal.get(Calendar.MONTH)+1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        setDateOnDisplay(year, month, day);

        dateSetListener = (view, year, month, day) -> {
            this.year = year;
            this.month = month+1;
            this.day = day;
            setDateOnDisplay(year, month+1, day);
        };
        dateDisplay.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(TransactionAddActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month-1, day);
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

                if(selection==SELECTED_SHIFT){
                    //reset the error message (see doneButton onClickListener)
                    TextView errorText1 = (TextView) accountSpinner.getSelectedView();
                    errorText1.setError(null);
                    TextView errorText2 = (TextView) account2Spinner.getSelectedView();
                    errorText2.setError(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        account2Spinner.setAdapter(adapter);
        account2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account2 = accountManager.getAccountIdByName(parent.getItemAtPosition(position).toString());

                if(selection==SELECTED_SHIFT){
                    //reset the error message (see doneButton onClickListener)
                    TextView errorText1 = (TextView) accountSpinner.getSelectedView();
                    errorText1.setError(null);
                    TextView errorText2 = (TextView) account2Spinner.getSelectedView();
                    errorText2.setError(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void findViews() {
        dateDisplay = findViewById(R.id.dateDisplay_textView);
        currencySymbol = findViewById(R.id.expenseAdd_textView);
        accountTextView = findViewById(R.id.account_textView);

        amountEditText = findViewById(R.id.expenseAdd_editText);
        descriptionEditText = findViewById(R.id.description_editView);

        incomeLayout = findViewById(R.id.catLine_income);
        expenseLayout = findViewById(R.id.catLine_expense);
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

    public void expense_selected() {
        selection = SELECTED_EXPENSE;
        expenseButton.setAlpha(1.0f);
        incomeButton.setAlpha(0.3f);
        shiftButton.setAlpha(0.3f);
        expenseLayout.setEnabled(true);
        incomeLayout.setEnabled(false);
        accountLine2.setEnabled(false);
        incomeLayout.setVisibility(View.GONE);
        accountLine2.setVisibility(View.GONE);
        expenseLayout.setVisibility(View.VISIBLE);
        accountTextView.setText(R.string.transaction_add_account);
        account2=null;
        //transactionAddLayout.setBackgroundColor(Color.parseColor("#F8E0E0"));
    }

    public void income_selected() {
        selection = SELECTED_INCOME;
        incomeButton.setAlpha(1.0f);
        expenseButton.setAlpha(0.3f);
        shiftButton.setAlpha(0.3f);
        incomeLayout.setEnabled(true);
        expenseLayout.setEnabled(false);
        accountLine2.setEnabled(false);
        expenseLayout.setVisibility(View.GONE);
        accountLine2.setVisibility(View.GONE);
        incomeLayout.setVisibility(View.VISIBLE);
        accountTextView.setText(R.string.transaction_add_account);
        account2=null;
        //transactionAddLayout.setBackgroundColor(Color.parseColor("#F1F8E0"));
    }

    public void shift_selected(){
        selection = SELECTED_SHIFT;
        shiftButton.setAlpha(1.0f);
        expenseButton.setAlpha(0.3f);
        incomeButton.setAlpha(0.3f);
        expenseLayout.setEnabled(false);
        incomeLayout.setEnabled(false);
        accountLine2.setEnabled(true);
        expenseLayout.setVisibility(View.GONE);
        incomeLayout.setVisibility(View.GONE);
        accountLine2.setVisibility(View.VISIBLE);
        accountTextView.setText(R.string.transaction_add_accountFrom);
        category = Transaction.CATEGORY_SHIFT;
    }

    public void setDateOnDisplay(int year, int month, int day) {
        String monthStr = getMonth(month);
        dateDisplay.setText(monthStr + " "  + day + ", " + year);
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }

}
