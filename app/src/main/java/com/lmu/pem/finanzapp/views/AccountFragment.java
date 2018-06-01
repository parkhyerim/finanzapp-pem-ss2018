package com.lmu.pem.finanzapp.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.AccountAdapter;
import com.lmu.pem.finanzapp.model.AccountManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private AccountManager accountManager;

    public AccountFragment() {
        this.accountManager = AccountManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.account_fragment, container, false);
        GridView gridView = (GridView) v.findViewById(R.id.gridview);
        AccountAdapter adapter = new AccountAdapter(getContext(), accountManager.getAccounts());
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

        return v;
    }

}
