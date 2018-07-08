package com.lmu.pem.finanzapp.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.RecyclerItemTouchHelperListener;
import com.lmu.pem.finanzapp.RecyclerSectionItemDecoration;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.controller.TransactionAdapter;
import com.lmu.pem.finanzapp.model.transactions.Transaction;

import java.util.ArrayList;

import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment implements SearchView.OnQueryTextListener, RecyclerItemTouchHelperListener {

    private TransactionManager transactionManager;

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private CoordinatorLayout trsView;

    private FloatingActionButton addButton;

    private int position;
    private double amount = 0;
    private String category = "";
    private String account = "";
    private String date = "";
    private String description = "";
    private String key;
    private String id;
    private int year,month,day;

    public final static int REQUEST_CODE_ADD_TRANSACTION = 111;
    public final static int REQUEST_CODE_EDIT_TRANSACTION = 112;


    public TransactionFragment() {
        this.transactionManager = TransactionManager.getInstance();
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
        View rootView = inflater.inflate(R.layout.transaction_fragment, container, false);
        ButterKnife.bind(this,rootView);

        // all findViewByID
        recyclerView = rootView.findViewById(R.id.transaction_recyclerView);
        addButton = rootView.findViewById(R.id.transaction_add_button);
        trsView = rootView.findViewById(R.id.trans_fragment);


        // RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new TransactionAdapter(transactionManager.getTransactions(), rootView.getContext(), rootView, this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        /*ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);*/


        // Header-Section
        RecyclerSectionItemDecoration transactionSectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.transaction_recycler_section_header),
                        true,
                        getSectionCallback(transactionManager.getTransactions()));
        recyclerView.addItemDecoration(transactionSectionItemDecoration);


        // Add Button -> Add Page
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), TransactionAddActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_TRANSACTION);
        });
        return rootView;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Fragment fragment = getChildFragmentManager().findFragmentById(R.id.trans_fragment);
        if(requestCode == REQUEST_CODE_ADD_TRANSACTION && resultCode == Activity.RESULT_OK) {

            position = transactionManager.getTransactions().size();
            date = data.getStringExtra("date");
            year = data.getIntExtra("year",0);
            month = data.getIntExtra("month", 0);
            day = data.getIntExtra("day",0);
            account = data.getStringExtra("account");
            category = data.getStringExtra("category");
            description = data.getStringExtra("description");
            amount = data.getDoubleExtra("amount",0);

            //A new transaction can be added to the transaction list
            insertItem(position, date, year, month, day, account, category, description, amount);
        }else if(requestCode == REQUEST_CODE_EDIT_TRANSACTION && resultCode == Activity.RESULT_OK){
            date = data.getStringExtra("date");
            year = data.getIntExtra("year",0);
            month = data.getIntExtra("month", 0);
            day = data.getIntExtra("day",0);
            account = data.getStringExtra("account");
            category = data.getStringExtra("category");
            description = data.getStringExtra("description");
            amount = data.getDoubleExtra("amount",0);
            key = data.getStringExtra("key");

            transactionManager.updateTransaction(key, date, account, category, getImageByCategory(category), description, amount);
            adapter.notifyItemChanged(transactionManager.getTransactions().indexOf(transactionManager.getTransactionByKey(key)));
        }
    }


    public void insertItem(int position, String date, int year, int month, int day, String account, String category, String description, double amount){
        this.date = date;
        this.year = year;
        this.month = month;
        this.day = day;
        this.account = account;
        this.category = category;
        this.amount = amount;
        this.description = description;
        Transaction transaction = new Transaction(this.date, this.year, this.month, this.day, getImageByCategory(category), this.account, this.category, this.description, this.amount);
        transactionManager.addTransaction(transaction);

        adapter.notifyItemInserted(position);
    }

    private int getImageByCategory(String category) {
        int imageResource;
        int img = getActivity().getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", getActivity().getPackageName());
        if(img == 0){
            imageResource = getActivity().getResources().getIdentifier("money", "drawable", getActivity().getPackageName());
        } else {
            imageResource = img;
        }
        //this.imageResource = getActivity().getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", getActivity().getPackageName());
        return imageResource;
    }

    // Header-Section by date
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<Transaction> transactionList){
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {

                //TODO: Try... Catch... richtig schreiben....
                if(position > 0 ) {
                    return transactionList.get(position).getDate().charAt(0) != transactionList.get(position - 1).getDate().charAt(0);
                } else {
                    return position == 0;
                }
               // return position == 0 || transactionList.get(position).getDate().charAt(0) != transactionList.get(position - 1).getDate().charAt(0);
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                // TODO: Richtige Lösung für Index out of bounds schreiben...
                if(position >= 0) {
                    date = transactionList.get(position).getDate().toString();

                } else {
                    date = "01/01/2018";
                }
               // date = transactionList.get(position).getDate().toString();
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
        searchView.setBackgroundColor(Color.parseColor("#ffffff"));
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
        for(Transaction transaction: transactionList){
            // TODO: bessere saubere Codes..
            final String descText = transaction.getDescription().toLowerCase();
            final String accText = transaction.getAccount().toLowerCase();
            final String cateText = transaction.getCategory().toLowerCase();
            final String dateText = transaction.getDate().toLowerCase();
            final String amountText = String.valueOf(transaction.getAmount()).toLowerCase();
            if(descText.contains(query) || accText.contains(query)
                    ||cateText.contains(query) ||dateText.contains(query)
                    || amountText.contains(query)){
                fiteredList.add(transaction);
            }
        }
        return  fiteredList;
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof TransactionAdapter.TransactionViewHolder){
            String name = transactionManager.getTransactions().get(viewHolder.getAdapterPosition()).getDescription();
            final Transaction deletedTransaction = transactionManager.getTransactions().get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();
            adapter.removeItem(deletedIndex);

            Snackbar snackbar = Snackbar.make(trsView, name + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", v -> adapter.restoreItem(deletedTransaction, deletedIndex));

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }

    }
}
