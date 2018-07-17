package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lmu.pem.finanzapp.model.accounts.Account;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;

import java.util.Locale;

public class AccountAddActivity extends AppCompatActivity {

    private boolean newAccount;
    private AccountManager accountManager;
    private String accountID;
    private int color;

    private EditText nameView, balanceView;
    private TextView balanceText;
    private CheckBox defaultCheckView;
    private Button addAccButton;
    private RadioGroup colorRadioGroup;
    private RadioButton colorRadio1, colorRadio2, colorRadio3, colorRadio4, colorRadio5, colorRadio6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_add);

        setupViews();
        balanceText.setText("Balance ("+ GlobalSettings.getInstance().getCurrencyString()+"): ");
        accountID = "";
        color = Account.DEFAULT_COLOR;
        accountManager = AccountManager.getInstance();

        newAccount = getIntent().getExtras().getBoolean("newAccount");
        if(!newAccount){
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Edit Account");
            addAccButton.setText("Apply Changes");

            accountID = getIntent().getExtras().getString("accountID");
            Account acc = accountManager.getAccountById(accountID);
            color = acc.getColor();
            if(color==colorRadio2.getButtonTintList().getDefaultColor()) colorRadio2.setChecked(true);
            else if(color==colorRadio3.getButtonTintList().getDefaultColor()) colorRadio3.setChecked(true);
            else if(color==colorRadio4.getButtonTintList().getDefaultColor()) colorRadio4.setChecked(true);
            else if(color==colorRadio5.getButtonTintList().getDefaultColor()) colorRadio5.setChecked(true);
            else if(color==colorRadio6.getButtonTintList().getDefaultColor()) colorRadio6.setChecked(true);

            nameView.setText(acc.getName());
            balanceView.setText(String.format(Locale.getDefault(), "%,.2f",acc.getBalance()));
            balanceText.setVisibility(View.GONE);
            balanceView.setVisibility(View.GONE);
            defaultCheckView.setChecked(acc.isDefault());
        }

        addAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameView.getText().toString();

                if(name.length()<=0) {
                    showError("You have to insert a name for the account.");
                }else if(accountManager.isNameTaken(name) && !(accountManager.getAccountIdByName(name).equals(accountID))){
                    showError("There is already an account with this name.");
                }else{
                    double balance = 0;
                    try {
                        balance = Double.parseDouble(balanceView.getText().toString());
                    }catch (Exception e){} //Shouldn't really be necessary because of set inputType, but hey, better safe than sorry.

                    boolean defaultSelected = defaultCheckView.isChecked(); //TODO what if a default account was un-defaulted?

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("newAccount", newAccount);
                    resultIntent.putExtra("accountID", accountID);
                    resultIntent.putExtra("name", name);
                    resultIntent.putExtra("balance", balance);
                    resultIntent.putExtra("default", defaultSelected);
                    resultIntent.putExtra("color", color);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }

            private void showError(String msg) {
                nameView.setError(msg);
                nameView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        nameView.setError(null);
                        nameView.removeTextChangedListener(this);
                    }
                });
            }
        });
    }

    private void setupViews() {
        nameView = findViewById(R.id.nameEdit);
        balanceView = findViewById(R.id.balanceEdit);
        defaultCheckView = findViewById(R.id.defaultCheck);
        addAccButton = findViewById(R.id.addAccButton);
        balanceText = findViewById(R.id.balanceText);
        colorRadioGroup = findViewById(R.id.colorRadioGroup);
        colorRadio1 = findViewById(R.id.colorRadio1);
        colorRadio2 = findViewById(R.id.colorRadio2);
        colorRadio3 = findViewById(R.id.colorRadio3);
        colorRadio4 = findViewById(R.id.colorRadio4);
        colorRadio5 = findViewById(R.id.colorRadio5);
        colorRadio6 = findViewById(R.id.colorRadio6);

        colorRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton btn = (RadioButton) findViewById(checkedId);
            if(btn!=null) color = btn.getButtonTintList().getDefaultColor();
        });
    }
}
