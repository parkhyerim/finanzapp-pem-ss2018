package com.lmu.pem.finanzapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.lmu.pem.finanzapp.model.accounts.Account;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;

import java.util.Locale;

public class AccountAddActivity extends AppCompatActivity {

    private boolean newAccount, wasDefault;
    private AccountManager accountManager;
    private String accountID;
    private int color;

    private EditText nameView, balanceView;
    private TextView balanceText;
    private CheckBox defaultCheckView;
    private Button addAccButton;
    private RadioGroup colorRadioGroup;
    private RadioButton colorRadio2, colorRadio3, colorRadio4, colorRadio5, colorRadio6;
    private TextView newDefaultAccText;
    private Spinner newDefaultAccSpinner;

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
            wasDefault = acc.isDefault();
            if(wasDefault){
                defaultCheckView.setChecked(true);
                if(accountManager.getAccounts().size()<2) defaultCheckView.setEnabled(false); //if there is only one account, it HAS to be the default one

                newDefaultAccText = findViewById(R.id.newDefaultText);
                newDefaultAccSpinner = findViewById(R.id.newDefaultAccSpinner);
                ArrayAdapter<String> newDefaultAccAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountManager.getNameArray());
                newDefaultAccAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                newDefaultAccSpinner.setAdapter(newDefaultAccAdapter);

                defaultCheckView.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(!isChecked){ //if we are de-selecting the default account, we have to choose a new one (there has to be a default account)
                        newDefaultAccText.setVisibility(View.VISIBLE);
                        newDefaultAccSpinner.setVisibility(View.VISIBLE);
                    }else{
                        newDefaultAccText.setVisibility(View.GONE);
                        newDefaultAccSpinner.setVisibility(View.GONE);
                    }
                });
            }
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

                    boolean defaultSelected = defaultCheckView.isChecked();

                    if(!newAccount && wasDefault && !defaultSelected){
                        String newDefault = accountManager.getAccountIdByName(accountManager.getNameArray()[newDefaultAccSpinner.getSelectedItemPosition()]);
                        if(newDefault==null || accountID.equals(newDefault)){
                            defaultSelected=true;
                        }else{
                            Account newDefaultAcc = accountManager.getAccountById(newDefault);
                            newDefaultAcc.setDefault(true);
                            accountManager.setDefaultAcc(newDefaultAcc);
                        }
                    }

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
