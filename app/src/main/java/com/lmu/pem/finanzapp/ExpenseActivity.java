package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lmu.pem.finanzapp.views.TransactionFragment;

public class ExpenseActivity extends AppCompatActivity {

    Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        doneButton = (Button) findViewById(R.id.doneBtn);



        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneButton.setBackgroundColor(R.drawable.ic_launcher_background);
               // Intent resultIntent = new Intent();
               // resultIntent.putExtra("catego", "ccccc" );
               // resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               // setResult(RESULT_OK, resultIntent);



             //   finish();







            }
        });
    }
}
