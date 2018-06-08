package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.lmu.pem.finanzapp.model.AccountManager;

public class AccountAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_add);

        Button addAccButton = findViewById(R.id.addAccButton);
        addAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameView = findViewById(R.id.nameEdit);
                String name = nameView.getText().toString();

                if(AccountManager.getInstance().isNameTaken(name)){
                    nameView.setBackgroundColor(0x70af0000);
                    final Snackbar sb = Snackbar.make(v, "There is already an account with this name! Please insert a diffferent one.", Snackbar.LENGTH_LONG);
                    sb.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sb.dismiss();
                        }
                    });
                    sb.show();
                }else if(name.length()<=0) {
                    nameView.setBackgroundColor(0x70af0000);
                    final Snackbar sb = Snackbar.make(v, "You have to insert a name for the account.", Snackbar.LENGTH_LONG);
                    sb.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sb.dismiss();
                        }
                    });
                    sb.show();
                }else{
                    nameView.setBackgroundColor(0x00000000);
                    EditText balanceView = findViewById(R.id.balanceEdit);
                    double balance = 0;
                    try {
                        balance = Double.parseDouble(balanceView.getText().toString());
                    }catch (Exception e){} //Shouldn't really be necessary because of set inputType, but hey, better safe than sorry.

                    CheckBox defaultCheckView = findViewById(R.id.defaultCheck);
                    boolean defaultSelected = defaultCheckView.isChecked();


                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("name", name);
                    resultIntent.putExtra("balance", balance);
                    resultIntent.putExtra("default", defaultSelected);
                    //resultIntent.putExtra("color", color);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}
