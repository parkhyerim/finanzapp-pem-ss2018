package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TransactionAddActivity extends AppCompatActivity {

    private EditText mCategoryText;
    private EditText mExpenseText;
    private Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_add);

        setTitle("Add Transaction");

        mCategoryText = (EditText) findViewById(R.id.categoryAdd_editText);
        mExpenseText = (EditText) findViewById(R.id.expenseAdd_editText);
        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCategoryText.getText().toString().equals("")|| mExpenseText.getText().toString().equals("")){
                    Toast.makeText(TransactionAddActivity.this, "Please insert inputs", Toast.LENGTH_LONG).show();
                } else {
                    double expense = Double.parseDouble(mExpenseText.getText().toString());
                    String categoryName = mCategoryText.getText().toString();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("category", categoryName);
                    resultIntent.putExtra("expense", expense);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });



    }
}
