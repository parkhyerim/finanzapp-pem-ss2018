package com.lmu.pem.finanzapp.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.RecyclerSectionItemDecoration;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEvent;
import com.lmu.pem.finanzapp.model.transactions.TransactionHistoryEventListener;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.controller.TransactionAdapter;
import com.lmu.pem.finanzapp.model.transactions.Transaction;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment implements SearchView.OnQueryTextListener, TransactionHistoryEventListener {

    private TransactionManager transactionManager;

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton addButton;

    private View rootView;

    public final static int REQUEST_CODE_ADD_TRANSACTION = 111;
    public final static int REQUEST_CODE_EDIT_TRANSACTION = 112;


    public TransactionFragment() {
        transactionManager = TransactionManager.getInstance();
        transactionManager.addListener(this);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.transaction_fragment, container, false);

        handleListEmptyText();
        setupUI();
        return rootView;
    }

    private void setupUI() {
        recyclerView = rootView.findViewById(R.id.transaction_recyclerView);
        addButton = rootView.findViewById(R.id.transaction_add_button);

        // RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new TransactionAdapter(transactionManager.getTransactions(), rootView.getContext(), rootView, this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(adapter.getItemCount()-1);

        // Header-Section
        RecyclerSectionItemDecoration transactionSectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.transaction_recycler_section_header), true, getSectionCallback(transactionManager.getTransactions()));
        recyclerView.addItemDecoration(transactionSectionItemDecoration);

        // Add Button -> Add Page
        addButton.setOnClickListener(v -> {
            if(AccountManager.getInstance().getAccounts().size()>0){
                Intent intent = new Intent(getActivity().getApplicationContext(), TransactionAddActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_TRANSACTION);
            }else{
                Snackbar.make(rootView, "You have to create an account before you can add a transaction!", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void handleListEmptyText() {
        TextView emptyListText = rootView.findViewById(R.id.emptyListText);
        if(transactionManager.getTransactions().size()<1){
            if(emptyListText!=null) emptyListText.setVisibility(View.VISIBLE);
        }else{
            if(emptyListText!=null) emptyListText.setVisibility(View.GONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            int year = data.getIntExtra("year",0);
            int month = data.getIntExtra("month", 0);
            int day = data.getIntExtra("day",0);
            String account = data.getStringExtra("account");
            String account2 = data.getStringExtra("account2");
            String category = data.getStringExtra("category");
            String description = data.getStringExtra("description");
            double amount = data.getDoubleExtra("amount",0);

            if(requestCode == REQUEST_CODE_ADD_TRANSACTION) {
                insertItem(year, month, day, account, account2, category, description, amount);
            }else if(requestCode == REQUEST_CODE_EDIT_TRANSACTION){
                String key = data.getStringExtra("key");
                transactionManager.updateTransaction(key, year, month, day, account, account2, category, description, amount);
            }
        }
    }


    public void insertItem(int year, int month, int day, String account, String account2, String category, String description, double amount){
        Transaction transaction = new Transaction(year, month, day, account, account2, category, description, amount);
        transactionManager.addTransaction(transaction);
    }

    // Header-Section by date
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<Transaction> transactionList){
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                if(position==-1) return false;
                return position == 0
                        || (transactionList.get(position).getYear() != transactionList.get(position-1).getYear())
                        || (transactionList.get(position).getMonth() != transactionList.get(position-1).getMonth())
                        || (transactionList.get(position).getDay()!=transactionList.get(position-1).getDay());
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                String date;
                String[] months = new DateFormatSymbols().getMonths();
                if(position >= 0) {
                    date = months[transactionList.get(position).getMonth()-1] + " " + transactionList.get(position).getDay() + ", " + transactionList.get(position).getYear();
                } else {
                    date = "";
                }
                return date;
            }
        };
    }


    // Suchfunktion
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        //
        inflater.inflate(R.menu.menu_items, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        //
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.setSearchResult(transactionManager.getTransactions());
                return true;
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final ArrayList<Transaction> filteredList = filter(transactionManager.getTransactions(), newText);
        adapter.setSearchResult(filteredList);
        return false;
    }


    private ArrayList<Transaction> filter(ArrayList<Transaction> transactionList, String query){
        query = query.toLowerCase();
        final ArrayList<Transaction> fiteredList = new ArrayList<>();
        for(Transaction transaction : transactionList){
            final String descText = transaction.getDescription().toLowerCase();
            final String accText = AccountManager.getInstance().getAccountById(transaction.getAccount()).getName().toLowerCase();
            final String cateText = transaction.getCategory().toLowerCase();
            final String year = String.valueOf(transaction.getYear());
            final String month = String.valueOf(transaction.getMonth());
            final String day = String.valueOf(transaction.getDay());
            final String amountText = String.valueOf(transaction.getAmount()).toLowerCase();
            final String monthStr = getMonthStr(transaction.getMonth()).toLowerCase();

            if(descText.contains(query)
                    || accText.contains(query)
                    || cateText.contains(query)
                    || monthStr.contains(query)
                    || year.equals(query) //if one digit should suffice, replace equals() with contains()
                    || month.equals(query)
                    || day.equals(query)
                    || amountText.contains(query)){
                fiteredList.add(transaction);
            }
        }
        return fiteredList;
    }

    @Override
    public void handle(TransactionHistoryEvent event) {
        adapter.notifyDataSetChanged();
        handleListEmptyText();
    }


    public String getMonthStr(int month) {
        return new DateFormatSymbols().getMonths()[month-1];
    }
}
