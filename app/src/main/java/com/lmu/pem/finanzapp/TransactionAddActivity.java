package com.lmu.pem.finanzapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class TransactionAddActivity extends AppCompatActivity {

    private EditText currencyEditText;
    private Button expenseButton;
    private Button incomeButton;
    private EditText descEditText;

    private EditText descriptionET;

    private Button doneButton;
    private TextView dateDisplay;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private Spinner accountSpinner;
    private Spinner expenseCategorySpinner;
    private Spinner incomeCategorySpinner;

    private ViewPager viewPager;

    private double expense = 0.0;
    private double income = 0.0;
    private String category;
    private String account;
    private String date;
    private String description;



    private RelativeLayout expenseRelativeLayout;
    private RelativeLayout incomeRelativeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_add);

        setTitle("Add Transaction");

        descriptionET = findViewById(R.id.description_editView);

        currencyEditText = findViewById(R.id.expenseAdd_editText);
        incomeRelativeLayout = findViewById(R.id.income_layout);
        incomeRelativeLayout.setEnabled(false);
        expenseRelativeLayout = (RelativeLayout) findViewById(R.id.expense_layout);
        incomeRelativeLayout.setVisibility(View.GONE);



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

        // Account-Spinner(Dropdown)
        accountSpinner = (Spinner) findViewById(R.id.account_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.account, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        accountSpinner.setAdapter(adapter);
        accountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                account = parent.getItemAtPosition(position).toString();
                Toast.makeText(getBaseContext(), account + " selected", Toast.LENGTH_SHORT).show();





            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // Category-Spinner(Dropdown)

        //getCategorySpinner();
        expenseCategorySpinner = (Spinner) findViewById(R.id.category_Spinner);
        ArrayAdapter<CharSequence> categoryAdaper = ArrayAdapter.createFromResource(this, R.array.expense_category, android.R.layout.simple_spinner_item);
        categoryAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        expenseCategorySpinner.setAdapter(categoryAdaper);


        /*
        ArrayAdapter<CharSequence> categoryAdaper = ArrayAdapter.createFromResource(this, R.array.expense_category, android.R.layout.simple_spinner_item);
        categoryAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(categoryAdaper);
        */
        expenseCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        //getCategorySpinner();
        incomeCategorySpinner = (Spinner) findViewById(R.id.category_Spinner2);
        ArrayAdapter<CharSequence> categoryAdaper2 = ArrayAdapter.createFromResource(this, R.array.income_category, android.R.layout.simple_spinner_item);
        categoryAdaper2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        incomeCategorySpinner.setAdapter(categoryAdaper2);


        /*
        ArrayAdapter<CharSequence> categoryAdaper = ArrayAdapter.createFromResource(this, R.array.expense_category, android.R.layout.simple_spinner_item);
        categoryAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(categoryAdaper);
        */
        incomeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        descriptionET = (EditText) findViewById(R.id.description_editView);



        expenseButton = (Button) findViewById(R.id.expense_button);
        incomeButton = (Button) findViewById(R.id.income_button);
        incomeButton.setAlpha(0.4f);
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| currencyEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert inputs", Toast.LENGTH_LONG).show();
                } else {
                    income = 0;
                    expense = Double.parseDouble(currencyEditText.getText().toString());

                    expenseButton.setAlpha(1.0f);
                    incomeButton.setAlpha(0.4f);
                    expenseRelativeLayout.setEnabled(true);


                   // Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                    //startActivityForResult(intent, 222);
                }

            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| currencyEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert inputs", Toast.LENGTH_LONG).show();
                } else {
                    expense = 0;

                    income = Double.parseDouble(currencyEditText.getText().toString());
                    incomeButton.setAlpha(1.0f);
                    expenseButton.setAlpha(0.4f);
                    expenseRelativeLayout.setEnabled(false);
                    expenseRelativeLayout.setVisibility(View.GONE);
                    incomeRelativeLayout.setEnabled(true);
                    incomeRelativeLayout.setVisibility(View.VISIBLE);
                    //  incomeRelativeLayout.setVisibility(View.VISIBLE);

                    // Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                    //startActivityForResult(intent, 222);
                }


            }
        });



        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| currencyEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert inputs", Toast.LENGTH_LONG).show();

                } else if(accountSpinner.getSelectedItem().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please select the account", Toast.LENGTH_LONG).show();

                } else if(expenseCategorySpinner.getSelectedItem().toString().equals("") && incomeCategorySpinner.getSelectedItem().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please select the category", Toast.LENGTH_LONG).show();

                } else {
                    expense = Double.parseDouble(currencyEditText.getText().toString());
                    date = dateDisplay.getText().toString();
                    description = descriptionET.getText().toString();

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



        /*
        viewPager = findViewById(R.id.transaction_add_viewPager);
        viewPager.setAdapter(new TransactionPagerAdapter(getSupportFragmentManager(), getApplicationContext()));
        TabLayout tabs = findViewById(R.id.transaction_add_TablayoutID);
        tabs.setupWithViewPager(viewPager);
        */



        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        dateDisplay = (TextView) findViewById(R.id.dateDisplay_textView);
        dateDisplay.setText(month + "/" + day + "/" + year);
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
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                Log.d("date", "onDateSet: date: " + year + "/" + month +"/" + dayOfMonth);

                String date = month + "/" + dayOfMonth + "/" + year;
                dateDisplay.setText(date);

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 222 && resultCode == Activity.RESULT_OK) {
            category = data.getStringExtra("catego");

        }

    }

}
