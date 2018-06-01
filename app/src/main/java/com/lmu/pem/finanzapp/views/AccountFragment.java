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
import com.lmu.pem.finanzapp.model.Account;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private Account[] accounts;

    public AccountFragment() {
        // TODO - read accounts from database
        this.accounts = new Account[]{
                new Account("Cash", 64.45), //TODO durch String-Ressource ersetzen - Crash bei: getContext().getString(R.string.account_cash)
                new Account("Main", 0xff00695c, true, 2049.05),
                new Account("Vacation", 0xffc62828, 256.09)
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.account_fragment, container, false);
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

        return v;
    }

}
