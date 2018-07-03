package com.lmu.pem.finanzapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lmu.pem.finanzapp.data.categories.CategoryManager;
import com.lmu.pem.finanzapp.model.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;

import java.util.ArrayList;
import java.util.Calendar;

public class TransactionAddActivity extends AppCompatActivity {

    private double amount = 0;
    private String category, account, date, description, key;
    private int year, month, day;

    private RelativeLayout expenseRelativeLayout, incomeRelativeLayout;
    private LinearLayout transactionAddLayout;
    private EditText moneyEditText;
    private EditText descriptionEditText;
    private Button expenseButton, incomeButton, doneButton;
    private Spinner accountSpinner, expenseCategorySpinner, incomeCategorySpinner;
    private TextView dateDisplay, currencySymbol;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private AccountManager accountManager;
    private CategoryManager categoryManager;

    private Context context;

    private Calendar cal;

    private ArrayList<String> expenses = new ArrayList<>();
    private ArrayList<String> incomes = new ArrayList<>();

    //private List<String> expenseArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_add);
        context = this;

        accountManager = AccountManager.getInstance();
        categoryManager = CategoryManager.getInstance();

        // Alle findViewByIDs
        dateDisplay = (TextView) findViewById(R.id.dateDisplay_textView);
        currencySymbol = (TextView) findViewById(R.id.expenseAdd_textView);

        moneyEditText = (EditText) findViewById(R.id.expenseAdd_editText);
        descriptionEditText = (EditText) findViewById(R.id.description_editView);

        incomeRelativeLayout = (RelativeLayout) findViewById(R.id.income_layout);
        expenseRelativeLayout = (RelativeLayout) findViewById(R.id.expense_layout);
        transactionAddLayout = (LinearLayout) findViewById(R.id.transaction_add_layout);

        expenseCategorySpinner = (Spinner) findViewById(R.id.category_Spinner);
        incomeCategorySpinner = (Spinner) findViewById(R.id.category_Spinner2);
        accountSpinner = (Spinner) findViewById(R.id.account_spinner);

        expenseButton = (Button) findViewById(R.id.expense_button);
        incomeButton = (Button) findViewById(R.id.income_button);
        doneButton = (Button) findViewById(R.id.done_button);


        // Currency
        currencySymbol.setText(GlobalSettings.getInstance(this).getCurrencyString());

        // Date
        cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        setDateOnDisplay();

        // Default-Page for an expense selection
        incomeRelativeLayout.setEnabled(false);
        incomeRelativeLayout.setVisibility(View.GONE);



        dateDisplay.setOnClickListener(v -> {
            //Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(TransactionAddActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
            dialog.show();
        });

        dateSetListener = (view, year, month, day) -> setDateOnDisplay();


        // SPINNER
        // Account-Spinner(Dropdown)
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.account, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountManager.getNameArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(adapter);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                account = accountManager.getAccountIdByName(parent.getItemAtPosition(position).toString());
                //Toast.makeText(getBaseContext(), account + " selected", Toast.LENGTH_SHORT).show();
                /*

                if(accountSpinner.getSelectedItemPosition() == accountManager.getNameArray().length-1) {
                    Toast.makeText(getBaseContext(), accountSpinner.getSelectedItemPosition() + " selected", Toast.LENGTH_SHORT).show();

                }
                */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Expense-Category
        // getCategorySpinner();

/*
        if(expenses.isEmpty()) {

            //expenses.addAll(Arrays.asList(getResources().getStringArray(R.array.expense_category)));
            expenses.addAll(categoryManager.getCategories());
        }
        */

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


        // extras indicate that we want to edit an existing transaction instead of creating a new one
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = getIntent().getStringExtra("date");
            description = getIntent().getStringExtra("description");
            dateDisplay.setText(date);
            descriptionEditText.setText(description);
            // TODO: Expense and Income
            amount = getIntent().getDoubleExtra("amount", 0);
            moneyEditText.setText(String.valueOf(Math.abs(amount)));
            if(amount<0){
                expenseButton.callOnClick();
            } else {
                incomeButton.callOnClick();
            }

            // Account
            String account = accountSpinner.getSelectedItem().toString();
            String[] accounts =  AccountManager.getInstance().getNameArray();
            for(int i =0; i < accounts.length ; i++){
                if (accounts[i].equals(account)){
                    accountSpinner.setSelection(i);
                } else {
                    accountSpinner.setSelection(0);
                }
            }

            // Category
            if(getIntent().hasExtra("category")){
                expenseCategoryShow();
                int expense = getIntent().getIntExtra("category", 0);
                expenseCategorySpinner.setSelection(expense);
            } else {
                incomeCategoryShow();
                int income = getIntent().getIntExtra("category2",0);
                incomeCategorySpinner.setSelection(income);
            }

            //Key
            key = getIntent().getStringExtra("key");

            //customize Toolbar title
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Edit Transaction");
        }

        // Buttons (expenseButton, incomeButton, doneButton)
        expenseButton.setOnClickListener(v -> {
            if(dateDisplay.getText().toString().equals("")|| moneyEditText.getText().toString().equals("")){
                Toast.makeText(TransactionAddActivity.this, "Please insert amount", Toast.LENGTH_LONG).show();
                /*
                Toast toast = Toast.makeText(getApplicationContext(), "Please insert amount", Toast.LENGTH_LONG);
                TextView toastmsg = (TextView) toast.getView().findViewById(android.R.id.message);
                toastmsg.setTextColor(Color.parseColor("#0B4C5F"));
                toast.show();
                */

                expenseCategoryShow();

            } else {
               // income = 0;
                //expense = Double.parseDouble(moneyEditText.getText().toString());
                amount = -1 * Double.parseDouble(moneyEditText.getText().toString());
                expenseCategoryShow();
                transactionAddLayout.setBackgroundColor(Color.parseColor("#F8E0E0"));

                //Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                //startActivityForResult(intent, 222);
            }
        });

        incomeButton.setOnClickListener(v -> {
            if(dateDisplay.getText().toString().equals("")|| moneyEditText.getText().toString().equals("")){
                Toast.makeText(TransactionAddActivity.this, "Please insert amount", Toast.LENGTH_LONG).show();
                incomeCategoryShow();

            } else {
               // expense = 0;
               // income = Double.parseDouble(moneyEditText.getText().toString());
                amount = Double.parseDouble(moneyEditText.getText().toString());
                incomeCategoryShow();
                transactionAddLayout.setBackgroundColor(Color.parseColor("#F1F8E0"));

                // Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                //startActivityForResult(intent, 222);
            }
        });




        doneButton.setOnClickListener(v -> {
                if(dateDisplay.getText().toString().equals("")|| moneyEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert account", Toast.LENGTH_LONG).show();

                } else if(accountSpinner.getSelectedItem().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please select an account", Toast.LENGTH_LONG).show();

                } else if(expenseCategorySpinner.getSelectedItem().toString().equals("") && incomeCategorySpinner.getSelectedItem().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please select a category", Toast.LENGTH_LONG).show();

                }  else {
                    date = dateDisplay.getText().toString();
                    description = descriptionEditText.getText().toString();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("date", date);
                    resultIntent.putExtra("category", category);
                    resultIntent.putExtra("account", account);
                    resultIntent.putExtra("amount", amount);
                    resultIntent.putExtra("description", description);
                    resultIntent.putExtra("key", key);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
        });
    }



    public void expenseCategoryShow() {
        expenseButton.setAlpha(1.0f);
        incomeButton.setAlpha(0.5f);
        incomeRelativeLayout.setEnabled(false);
        incomeRelativeLayout.setVisibility(View.GONE);
        expenseRelativeLayout.setEnabled(true);
        expenseRelativeLayout.setVisibility(View.VISIBLE);
    }


    public void incomeCategoryShow() {
        incomeButton.setAlpha(1.0f);
        expenseButton.setAlpha(0.5f);
        expenseRelativeLayout.setEnabled(false);
        expenseRelativeLayout.setVisibility(View.GONE);
        incomeRelativeLayout.setEnabled(true);
        incomeRelativeLayout.setVisibility(View.VISIBLE);
    }

    public void setDateOnDisplay() {
        month ++;

        if (day < 10 && month < 10) {
            dateDisplay.setText("0"+ month + "/" + "0" + day + "/" + year);
        } else if (month < 10) {
            dateDisplay.setText("0"+ month + "/" + day + "/" + year);
        } else if (day < 10) {
            dateDisplay.setText(month + "/" +  "0" + day + "/" + year);
        } else {
            dateDisplay.setText(month + "/" + day + "/" + year);
        }
    }

}
