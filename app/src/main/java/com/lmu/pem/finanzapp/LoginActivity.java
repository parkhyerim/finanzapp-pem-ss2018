package com.lmu.pem.finanzapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.data.Account;
import com.lmu.pem.finanzapp.model.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText emailEditText, pwEditText;
    private Button loginButton, registerButton;
    private LinearLayout loginScreen;
    private String inputMail, inputPW;
    private DatabaseReference userRef;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if(user != null) {
                Log.i("123123123", "onCreate: Logged in");
                uid = user.getUid();
                loadFirebaseData();
            }
        };
        setupUI();
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void setupUI(){
        loginScreen = findViewById(R.id.loginScreen);
        emailEditText = findViewById(R.id.emailEditText);
        pwEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        loginButton.setOnClickListener((v)->{
            if(checkInputs()) signIn(inputMail, inputPW);
        });
        registerButton.setOnClickListener((v)->{
            if(checkInputs()) createAccount(inputMail, inputPW);
        });
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Snackbar.make(loginScreen, "Logged in as "+user.getEmail()+"!", Snackbar.LENGTH_LONG);
                        Log.i("123123123", "signIn: Logged in");
                        uid = user.getUid();
                        loadFirebaseData();
                    } else {
                        Snackbar.make(loginScreen, "Authentication failed.", Snackbar.LENGTH_LONG);
                    }
                });
    }

    private void createAccount(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Snackbar.make(loginScreen, "Account created for "+user.getEmail()+"!", Snackbar.LENGTH_LONG);
                        //Create Cash Account
                        AccountManager accountManager = AccountManager.getInstance();
                        accountManager.initializeWithCashAccount();
                        goToMainActivity();
                    } else {
                        Snackbar.make(loginScreen, "Authentication failed.", Snackbar.LENGTH_LONG);
                    }

                });
    }

    private boolean checkInputs(){
        boolean valid=true;
        inputMail = emailEditText.getText().toString();
        inputPW = pwEditText.getText().toString();
        if(inputMail.length()<1){
            valid=false;
            emailEditText.setError("You have to enter a valid e-mail address!");
            emailEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    emailEditText.setError(null);
                }
            });
        }
        if(inputPW.length()<1){
            valid=false;
            pwEditText.setError("You have to enter your password!");
            pwEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    pwEditText.setError(null);
                }
            });
        }
        return valid;
    }

    private void loadFirebaseData(){
        DatabaseReference curUserRef = userRef.child(uid);
        AccountManager accountManager = AccountManager.getInstance();
        TransactionManager transactionManager = TransactionManager.getInstance();
        GlobalSettings globalSettings = GlobalSettings.getInstance();
        curUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, HashMap<String, Object>>> map = (HashMap<String, HashMap<String, HashMap<String, Object>>>) dataSnapshot.getValue();
                try{
                    //accounts
                    HashMap<String, HashMap<String, Object>> accMap = map.get("accounts");
                    for (String key : accMap.keySet()) {

                        Account newAcc=new Account(
                                dataSnapshot.child("accounts").child(key).child("name").getValue(String.class),
                                dataSnapshot.child("accounts").child(key).child("color").getValue(Integer.class),
                                dataSnapshot.child("accounts").child(key).child("isDefault").getValue(Boolean.class),
                                dataSnapshot.child("accounts").child(key).child("balance").getValue(Double.class),
                                key
                        );
                        accountManager.addAccount(newAcc);
                    }

                    //transactions
                    HashMap<String, HashMap<String, Object>> transMap = map.get("transactions");
                    for (String key : transMap.keySet()) {
                        Transaction newTransaction = new Transaction(
                                dataSnapshot.child("transactions").child(key).child("year").getValue(Integer.class),
                                dataSnapshot.child("transactions").child(key).child("month").getValue(Integer.class),
                                dataSnapshot.child("transactions").child(key).child("day").getValue(Integer.class),
                                dataSnapshot.child("transactions").child(key).child("imageResource").getValue(Integer.class),
                                dataSnapshot.child("transactions").child(key).child("account").getValue(String.class),
                                dataSnapshot.child("transactions").child(key).child("account2").getValue(String.class),
                                dataSnapshot.child("transactions").child(key).child("category").getValue(String.class),
                                dataSnapshot.child("transactions").child(key).child("description").getValue(String.class),
                                dataSnapshot.child("transactions").child(key).child("amount").getValue(Double.class)
                        );
                        newTransaction.setKey(key);
                        transactionManager.addTransactionLocally(newTransaction, false);
                    }

                    //settings
                    HashMap<String, HashMap<String, Object>> settingsMap = map.get("settings");
                    Integer newCurrency = dataSnapshot.child("settings").child("currency").child("value").getValue(Integer.class);
                    if(newCurrency!=null) globalSettings.setCurrency(newCurrency);
                    Integer newHomeTab = dataSnapshot.child("settings").child("homeTab").child("value").getValue(Integer.class);
                    if(newHomeTab!=null) globalSettings.setHomeTab(newHomeTab);
                }catch(NullPointerException e){
                    Log.e("123123123", "onDataChange: NullPointerException!", e);
                }

                goToMainActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //transactionManager.createTransactionList();
    }

    private void goToMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
