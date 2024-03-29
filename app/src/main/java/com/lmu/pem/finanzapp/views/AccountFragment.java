package com.lmu.pem.finanzapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lmu.pem.finanzapp.AccountAddActivity;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.AccountAdapter;
import com.lmu.pem.finanzapp.model.accounts.Account;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements TransactionHistoryEventListener {

    public static final int REQUEST_CODE_ACCOUNT_CHANGE = 123;
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private AccountAdapter adapter;
    private DatabaseReference dbRef;
    private GridView gridView;

    public AccountFragment() {
        this.accountManager = AccountManager.getInstance();
        this.transactionManager = TransactionManager.getInstance();
        transactionManager.addListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("accounts");

        View v = inflater.inflate(R.layout.account_fragment, container, false);
        gridView = (GridView) v.findViewById(R.id.gridview);
        adapter = new AccountAdapter(getContext(), accountManager.getAccounts(), this);
        gridView.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.acc_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), AccountAddActivity.class);
            intent.putExtra("newAccount", true);
            startActivityForResult(intent, REQUEST_CODE_ACCOUNT_CHANGE);
        });

        return v;
    }

    public void editAccount(String id){
        Intent intent = new Intent(getActivity().getApplicationContext(), AccountAddActivity.class);
        intent.putExtra("newAccount", false);
        intent.putExtra("accountID", id);
        startActivityForResult(intent, REQUEST_CODE_ACCOUNT_CHANGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_ACCOUNT_CHANGE && resultCode == Activity.RESULT_OK) {

            String name = data.getStringExtra("name");
            double balance = data.getDoubleExtra("balance", 0);
            boolean defaultAcc = data.getBooleanExtra("default", false);
            boolean newAccount = data.getBooleanExtra("newAccount", true);
            int color = data.getIntExtra("color", Account.DEFAULT_COLOR);

            Account acc;
            if(newAccount){
                acc = new Account(name, color, defaultAcc, balance);
                accountManager.addAccount(acc);
            }else{
                String accountID = data.getStringExtra("accountID");
                acc = accountManager.getAccountById(accountID);
                acc.setName(name);
                acc.setBalance(balance);
                acc.setColor(color);
                acc.setDefault(defaultAcc);
                if(defaultAcc && (accountManager.getDefaultAcc()==null || !(accountManager.getDefaultAcc().getId().equals(accountID)))) accountManager.setDefaultAcc(acc);
            }
            accountManager.writeAccountToFirebase(acc);

            //normally we'd call adapter.notifyDataSetChanged() here, but that doesn't do the trick if you just change the default account, so we're re-setting the adapter.
            gridView.setAdapter(adapter);
        }else if(requestCode==TransactionFragment.REQUEST_CODE_ADD_TRANSACTION && resultCode == Activity.RESULT_OK){
            int year = data.getIntExtra("year",0);
            int month = data.getIntExtra("month", 0);
            int day = data.getIntExtra("day",0);
            String account = data.getStringExtra("account");
            String account2 = data.getStringExtra("account2");
            String category = data.getStringExtra("category");
            String description = data.getStringExtra("description");
            double amount = data.getDoubleExtra("amount",0);

            Transaction transaction;
            transaction = new Transaction(year, month, day, account, account2, category, description, amount);

            transactionManager.addTransaction(transaction);
        }
    }

    @Override
    public void handle(TransactionHistoryEvent event) {
        adapter.notifyDataSetChanged();
    }
}
