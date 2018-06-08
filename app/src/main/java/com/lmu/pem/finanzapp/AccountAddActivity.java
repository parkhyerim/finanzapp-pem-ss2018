package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
                final EditText nameView = findViewById(R.id.nameEdit);
                String name = nameView.getText().toString();

                if(name.length()<=0) {
                    nameView.setError("You have to insert a name for the account.");
                    nameView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            nameView.setError(null);
                            nameView.removeTextChangedListener(this);
                        }
                    });
                }else if(AccountManager.getInstance().isNameTaken(name)){
                    nameView.setError("There is already an account with this name");
                    nameView.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            nameView.setError(null);
                            nameView.removeTextChangedListener(this);
                        }
                    });
                }else{
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
