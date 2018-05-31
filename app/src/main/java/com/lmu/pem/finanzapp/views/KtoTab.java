package com.lmu.pem.finanzapp.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.model.Account;


/**
 * A simple {@link Fragment} subclass.
 */
public class KtoTab extends Fragment {

    private Account[] accounts;

    public KtoTab() {
        // TODO - read accounts from database
        this.accounts = new Account[]{
                new Account("Bargeld", true), //TODO durch String-Ressource ersetzen - Crash bei: getContext().getString(R.string.account_cash)
                new Account("Urlaub", 0xffff5722),
                new Account("Sonst", 0xff005700)
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tab3, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        AccountAdapter adapter = new AccountAdapter(getContext(), accounts);
        gridView.setAdapter(adapter);
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
        });

        //View aboutView = inflater.inflate(R.layout.fragment_tab2, container, false);

        // Inflate the layout for this fragment
        return v;
    }

}
