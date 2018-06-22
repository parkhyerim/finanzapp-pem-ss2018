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

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private AccountManager accountManager;
    private AccountAdapter adapter;
    private DatabaseReference dbRef;
    private boolean notify;

    public AccountFragment() {
        this.accountManager = AccountManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dbRef = FirebaseDatabase.getInstance().getReference().child("accounts");
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
        adapter = new AccountAdapter(getContext(), accountManager.getAccounts());
        gridView.setAdapter(adapter);

        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Short Click detected!", Toast.LENGTH_SHORT).show();
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Long Click detected!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/

        FloatingActionButton fab = v.findViewById(R.id.acc_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AccountAddActivity.class);
                startActivityForResult(intent, 123);
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123 && resultCode == Activity.RESULT_OK) {

            String name = data.getStringExtra("name");
            double balance = data.getDoubleExtra("balance", 0);
            boolean defaultAcc = data.getBooleanExtra("default", false);

            Account newAcc = new Account(name, defaultAcc, balance);
            accountManager.addAccount(newAcc); //TODO - color
            notify=true;
            DatabaseReference accRef = FirebaseDatabase.getInstance().getReference().child("accounts").child(newAcc.getId());
            accRef.setValue(newAcc.toMap());
            adapter.setAccounts(accountManager.getAccounts());
            adapter.notifyDataSetChanged();
        }
    }

}
