package com.lmu.pem.finanzapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.AccountAddActivity;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.AccountAdapter;
import com.lmu.pem.finanzapp.data.Account;
import com.lmu.pem.finanzapp.model.AccountManager;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements TransactionHistoryEventListener {

    public static final int REQUEST_CODE_ACCOUNT_CHANGE = 123;
    private AccountManager accountManager;
    private TransactionManager transactionManager;
    private AccountAdapter adapter;
    private DatabaseReference dbRef;
    private boolean notify;

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
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, Object>> map = (HashMap<String, HashMap<String, Object>>) dataSnapshot.getValue();
                if(notify) {
                    for (String key : map.keySet()) {
                        Snackbar.make(getView(), "Added account " + map.get(key).get("name") + ".", Snackbar.LENGTH_SHORT).show();
                    }
                    notify = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("123123123","Cancelled: "+databaseError.toString());
            }
        });

        View v = inflater.inflate(R.layout.account_fragment, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
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
            String accountID;
            if(newAccount){
                acc = new Account(name, color, defaultAcc, balance);
                accountID=acc.getId();
                accountManager.addAccount(acc);
                notify=true;
            }else{
                accountID = data.getStringExtra("accountID");
                acc = accountManager.getAccountById(accountID);
                acc.setName(name);
                acc.setBalance(balance);
                acc.setColor(color);
                acc.setDefault(defaultAcc);
                if(defaultAcc && !(accountManager.getDefaultAcc().getId().equals(accountID))) accountManager.setDefaultAcc(acc);
            }
            dbRef.child(accountID).setValue(acc.toMap());

            adapter.notifyDataSetChanged();
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
            if(account2==null){
                transaction = new Transaction(year, month, day, getImageByCategory(category), account, category, description, amount);
            }else{
                transaction = new Transaction(year, month, day, getImageByCategory(category), account, account2, category, description, amount);
            }

            transactionManager.addTransaction(transaction);
        }
    }

    @Override
    public void handle(TransactionHistoryEvent event) {
        adapter.notifyDataSetChanged();
    }

    /**
     * Gets an image ResourceID for a given category. Duplicate of this method exists in TransactionFragment, so don't forget to apply changes to both. It's a maintenance nightmare, but the Activity framework doesn't really leave us a choice.
     * @param category the Transaction category to get the image for. If there is no image for this category, a default image will be chosen.
     * @return the ResourceID for the image
     */
    private int getImageByCategory(String category) {
        int imageResource;
        int img = getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", getActivity().getPackageName());
        if(img == 0){
            imageResource = getResources().getIdentifier("money", "drawable", getActivity().getPackageName());
        } else {
            imageResource = img;
        }
        //this.imageResource = getActivity().getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", getActivity().getPackageName());
        return imageResource;
    }
}
