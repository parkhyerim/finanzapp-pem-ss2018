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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class TransactionAddActivity extends AppCompatActivity {

    private EditText expenseEditText;
    private Button expenseButton;
    private Button incomeButton;

    private Button doneButton;
    private TextView dateDisplay;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private ViewPager viewPager;

    private double expense;
    private double income;
    private String category;
    private String account;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_add);

        setTitle("Add Transaction");

        expenseButton = (Button) findViewById(R.id.expense_button);
        incomeButton = (Button) findViewById(R.id.income_button);
        expenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| expenseEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert inputs", Toast.LENGTH_LONG).show();
                } else {

                    incomeButton.setAlpha(0.2f);

                   // Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
                    //startActivityForResult(intent, 222);
                }

            }
        });

        incomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateDisplay.getText().toString().equals("")|| expenseEditText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert inputs", Toast.LENGTH_LONG).show();
                } else {
                    double expense = Double.parseDouble(expenseEditText.getText().toString());
                    String dateInput = dateDisplay.getText().toString();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("category", dateInput);
                    resultIntent.putExtra("expense", expense);
                    resultIntent.putExtra("catego", category);
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

        expenseEditText = (EditText) findViewById(R.id.expenseAdd_editText);

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
