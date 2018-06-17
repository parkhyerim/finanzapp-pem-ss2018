package com.lmu.pem.finanzapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.lmu.pem.finanzapp.controller.TransactionAdapter;
import com.lmu.pem.finanzapp.model.AccountManager;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class TransactionAddActivity extends AppCompatActivity {

    private double expense = 0;
    private double income = 0;
    private String category;
    private String account;
    private String date;
    private String description;
    private int year;
    private int month;
    private int day;

    private RelativeLayout expenseRelativeLayout;
    private RelativeLayout incomeRelativeLayout;
    private RelativeLayout transactionAddRelativeLayout;

    private EditText moneyEditText;
    private EditText descriptionEditText;

    private Button expenseButton;
    private Button incomeButton;
    private Button doneButton;

    private TextView dateDisplay;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private Spinner accountSpinner;
    private Spinner expenseCategorySpinner;
    private Spinner incomeCategorySpinner;

    private AccountManager accountManager;

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_add);
        //setTitle("Add Transaction");

        moneyEditText = findViewById(R.id.expenseAdd_editText);
        descriptionEditText = findViewById(R.id.description_editView);
        incomeRelativeLayout = findViewById(R.id.income_layout);
        expenseRelativeLayout = findViewById(R.id.expense_layout);
        transactionAddRelativeLayout = findViewById(R.id.transaction_add_layout);


        incomeRelativeLayout.setEnabled(false);
        incomeRelativeLayout.setVisibility(View.GONE);

        accountManager = AccountManager.getInstance();





        /*
        currencyEditText.addTextChangedListener(new TextWatcher() {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                if(!s.toString().equals(current)){
                    currencyEditText.removeTextChangedListener(this);

                    String replaceable = String.format(" %.2f \\u20AC\", 123.10");
                   // String replaceable = String.format("[%s,.\\s]",   NumberFormat.getCurrencyInstance().getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }
                    NumberFormat formatter = NumberFormat.getCurrencyInstance();
                    formatter.setMaximumFractionDigits(0);
                    String formatted = formatter.format((parsed));

                    current = formatted;
                    currencyEditText.setText(formatted);
                    currencyEditText.setSelection(formatted.length());
                    currencyEditText.addTextChangedListener(this);

                }

            }
        });
        */


        // Date
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        dateDisplay = (TextView) findViewById(R.id.dateDisplay_textView);
        if (day < 10 && month < 10) {
            dateDisplay.setText("0"+ month + "/" + "0" + day + "/" + year);
        } else if (month < 10) {
            dateDisplay.setText("0"+ month + "/" + day + "/" + year);
        } else if (day < 10) {
            dateDisplay.setText(month + "/" +  "0" + day + "/" + year);
        } else {
            dateDisplay.setText(month + "/" + day + "/" + year);
        }

        dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TransactionAddActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                //Log.d("date", "onDateSet: date: " + year + "/" + month +"/" + dayOfMonth);
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
        };

        // Spinner
        // Account-Spinner(Dropdown)
        accountSpinner = (Spinner) findViewById(R.id.account_spinner);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.account, android.R.layout.simple_spinner_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountManager.getNameArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(adapter);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                account = accountManager.getAccountIdByName(parent.getItemAtPosition(position).toString());
                //Toast.makeText(getBaseContext(), account + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Category-Spinner(Dropdown)
        // getCategorySpinner();
        expenseCategorySpinner = (Spinner) findViewById(R.id.category_Spinner);
        ArrayAdapter<CharSequence> categoryAdaper = ArrayAdapter.createFromResource(this, R.array.expense_category, android.R.layout.simple_spinner_item);
        categoryAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(categoryAdaper);
        expenseCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        incomeCategorySpinner = (Spinner) findViewById(R.id.category_Spinner2);
        ArrayAdapter<CharSequence> categoryAdaper2 = ArrayAdapter.createFromResource(this, R.array.income_category, android.R.layout.simple_spinner_item);
        categoryAdaper2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeCategorySpinner.setAdapter(categoryAdaper2);
        incomeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });



        // wenn the Addactivitiy-view is open for updating/editing the transaction

        dateDisplay.setText(getIntent().getStringExtra("date"));
        descriptionEditText.setText(getIntent().getStringExtra("description"));
        moneyEditText.setText(String.valueOf(getIntent().getDoubleExtra("money", 0.0)));

        //
        if(getIntent().hasExtra("category")){
            int expense = getIntent().getIntExtra("category", 0);
            expenseCategorySpinner.setSelection(expense);
        } else {
            expenseRelativeLayout.setEnabled(false);
            expenseRelativeLayout.setVisibility(View.GONE);
            incomeRelativeLayout.setEnabled(true);
            incomeRelativeLayout.setVisibility(View.VISIBLE);

            int income = getIntent().getIntExtra("category2",0);
            incomeCategorySpinner.setSelection(income);
        }



        // Buttons (expenseButton, incomeButton, doneButton)

        expenseButton = (Button) findViewById(R.id.expense_button);
        incomeButton = (Button) findViewById(R.id.income_button);
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| moneyEditText.getText().toString().equals("")){
                    Toast toast = Toast.makeText(getApplicationContext(), "Please insert amount", Toast.LENGTH_LONG);
                    //Toast.makeText(TransactionAddActivity.this, "Please insert amount", Toast.LENGTH_LONG).show();
                    TextView toastmsg = (TextView) toast.getView().findViewById(android.R.id.message);
                    toastmsg.setTextColor(Color.parseColor("#0B4C5F"));
                    toast.show();

                    expenseButton.setAlpha(1.0f);
                    incomeButton.setAlpha(0.5f);
                    incomeRelativeLayout.setEnabled(false);
                    incomeRelativeLayout.setVisibility(View.GONE);
                    expenseRelativeLayout.setEnabled(true);
                    expenseRelativeLayout.setVisibility(View.VISIBLE);
                } else {

                    income = 0;
                    expense = Double.parseDouble(moneyEditText.getText().toString());

                    expenseButton.setAlpha(1.0f);
                    incomeButton.setAlpha(0.5f);
                    incomeRelativeLayout.setEnabled(false);
                    incomeRelativeLayout.setVisibility(View.GONE);
                    expenseRelativeLayout.setEnabled(true);
                    expenseRelativeLayout.setVisibility(View.VISIBLE);
                    transactionAddRelativeLayout.setBackgroundColor(Color.parseColor("#F8E0E0"));

                    //Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                    //startActivityForResult(intent, 222);
                }
            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| moneyEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert amount", Toast.LENGTH_LONG).show();

                    incomeButton.setAlpha(1.0f);
                    expenseButton.setAlpha(0.5f);
                    expenseRelativeLayout.setEnabled(false);
                    expenseRelativeLayout.setVisibility(View.GONE);
                    incomeRelativeLayout.setEnabled(true);
                    incomeRelativeLayout.setVisibility(View.VISIBLE);
                } else {
                    expense = 0;
                    income = Double.parseDouble(moneyEditText.getText().toString());

                    incomeButton.setAlpha(1.0f);
                    expenseButton.setAlpha(0.5f);
                    expenseRelativeLayout.setEnabled(false);
                    expenseRelativeLayout.setVisibility(View.GONE);
                    incomeRelativeLayout.setEnabled(true);
                    incomeRelativeLayout.setVisibility(View.VISIBLE);
                    transactionAddRelativeLayout.setBackgroundColor(Color.parseColor("#F1F8E0"));

                    // Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                    //startActivityForResult(intent, 222);
                }
            }
        });



        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| moneyEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert account", Toast.LENGTH_LONG).show();

                } else if(accountSpinner.getSelectedItem().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please select an account", Toast.LENGTH_LONG).show();

                } else if(expenseCategorySpinner.getSelectedItem().toString().equals("") && incomeCategorySpinner.getSelectedItem().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please select a category", Toast.LENGTH_LONG).show();

                } else {
                    date = dateDisplay.getText().toString();
                    description = descriptionEditText.getText().toString();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("date", date);
                    resultIntent.putExtra("category", category);
                    resultIntent.putExtra("account", account);
                    resultIntent.putExtra("expense", expense);
                    resultIntent.putExtra("income", income);
                    resultIntent.putExtra("description", description);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }



    /*
    public void editTransaction() {
        editTransaction();
        description = getIntent().getStringExtra("description");
        descriptionEditText.setText(description);

        currentEditText.setText(String.valueOf(getIntent().getDoubleExtra("money", 0.0)));
        dateDisplay.setText(getIntent().getStringExtra("date"));

        Intent resultIntent = new Intent();
        resultIntent.putExtra("description", description);
        setResult(RESULT_OK, resultIntent);
        finish();

    }

    */
}
