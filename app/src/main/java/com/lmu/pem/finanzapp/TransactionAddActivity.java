package com.lmu.pem.finanzapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.lmu.pem.finanzapp.model.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class TransactionAddActivity extends AppCompatActivity {

    private double amount = 0;
    private String category, account, date, description;
    private int year, month, day;

    private RelativeLayout expenseRelativeLayout, incomeRelativeLayout, transactionAddRelativeLayout;
    private EditText moneyEditText;
    private EditText descriptionEditText;
    private Button expenseButton, incomeButton, doneButton;
    private Spinner accountSpinner, expenseCategorySpinner, incomeCategorySpinner;
    private TextView dateDisplay, currencySymbol;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private AccountManager accountManager;

    private Context context;

    private Calendar cal;

    private  String newItem;

    private List<String> expenseArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_add);
        context = this;
        //setTitle("Add Transaction");
        accountManager = AccountManager.getInstance();

        // Alle findViewByIDs
        dateDisplay = (TextView) findViewById(R.id.dateDisplay_textView);
        currencySymbol = (TextView) findViewById(R.id.expenseAdd_textView);

        moneyEditText = (EditText) findViewById(R.id.expenseAdd_editText);
        descriptionEditText = (EditText) findViewById(R.id.description_editView);

        incomeRelativeLayout = (RelativeLayout) findViewById(R.id.income_layout);
        expenseRelativeLayout = (RelativeLayout) findViewById(R.id.expense_layout);
        transactionAddRelativeLayout = (RelativeLayout) findViewById(R.id.transaction_add_layout);

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



        dateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Calendar cal = Calendar.getInstance();
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TransactionAddActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                setDateOnDisplay();
            }
        };


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


        // Category-Spinner(Dropdown)
        // Expense-Category
        // getCategorySpinner();
        //expenseArray = getResources().getStringArray(R.array.expense_category);

        expenseArray.addAll(Arrays.asList("Food", "Household", "Transportation", "Health", "ADD"));

        //expenseArray = Arrays.asList(getResources().getStringArray(R.array.expense_category));

        //List<String> expList = Arrays.asList(getResources().getStringArray(R.array.expense_category));
        ArrayAdapter<String> expenseCategoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, expenseArray);

       // ArrayAdapter<CharSequence> expenseCategoryAdapter = ArrayAdapter.createFromResource(this, R.array.expense_category, android.R.layout.simple_spinner_item);
        expenseCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(expenseCategoryAdapter);
        expenseCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_SHORT).show();

                if(expenseCategorySpinner.getSelectedItemPosition() == expenseArray.size()-1){
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
                    // set dialog message
                    AlertDialog.Builder builder = alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            /*
                                            String newItem = userInput.getText().toString();
                                            expList.add(newItem);
                                            expenseCategoryAdapter.notifyDataSetChanged();
                                            category = parent.getItemAtPosition(position).toString();
                                            */

                                             newItem = userInput.getText().toString();
                                             int pos = expenseArray.size()-1;
                                             expenseArray.set(pos, newItem);



                                            /*
                                            for(int i = 0; i < pos; i++) {
                                                String[] newArray = new String[pos];
                                                newArray[i] = expenseArray[i];
                                                newArray[pos] = newItem;
                                            }
                                            */

                                            /*
                                            for(int i = 0 ; i < pos; i++){
                                                expenseArray = new String[pos+1];
                                                expenseArray[i] = expenseArray[i];

                                            }
                                            */


                                            // expenseArray[pos] = "Add";

                                            expenseCategoryAdapter.notifyDataSetChanged();
                                            category = parent.getItemAtPosition(position).toString();

                                            /*
                                            for(int i = 0 ; i < pos; i++){
                                                expenseArray = new String[pos+1];
                                                expenseArray[i] = expenseArray[i];
                                                expenseArray[pos] = "Add";

                                            }
                                            */


                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

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
        ArrayAdapter<CharSequence> incomeCategoryAdapter = ArrayAdapter.createFromResource(this, R.array.income_category, android.R.layout.simple_spinner_item);
        incomeCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incomeCategorySpinner.setAdapter(incomeCategoryAdapter);
        incomeCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getBaseContext(), category + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });


        // Wenn the Add-activitiy is open for updating/editing the transaction
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = getIntent().getStringExtra("date");
            description = getIntent().getStringExtra("description");
            dateDisplay.setText(date);
            descriptionEditText.setText(description);
            // TODO: Expense and Income
            moneyEditText.setText(String.valueOf(getIntent().getDoubleExtra("amount", 0)));

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
            }}


        // Buttons (expenseButton, incomeButton, doneButton)
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    incomeCategoryShow();

                } else {
                   // expense = 0;
                   // income = Double.parseDouble(moneyEditText.getText().toString());
                    amount = Double.parseDouble(moneyEditText.getText().toString());
                    incomeCategoryShow();
                    transactionAddRelativeLayout.setBackgroundColor(Color.parseColor("#F1F8E0"));

                    // Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                    //startActivityForResult(intent, 222);
                }
            }
        });




        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
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
