package com.lmu.pem.finanzapp.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.support.v7.widget.SearchView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.RecyclerItemTouchHelperListener;
import com.lmu.pem.finanzapp.RecyclerSectionItemDecoration;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.controller.TransactionAdapter;
import com.lmu.pem.finanzapp.model.transactions.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment implements SearchView.OnQueryTextListener, RecyclerItemTouchHelperListener {

    private TransactionManager transactionManager;

    DatabaseReference db;
    DatabaseReference transactionRef;

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private CoordinatorLayout trsView;

    private FloatingActionButton addButton;

    private int position;
    private int imageResource;
    private double amount = 0;
    private String category = "";
    private String account = "";
    private String date = "";
    private String description = "";


    public TransactionFragment() {
       // super();
        // Required empty public constructor
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


        // Firebase : get Reference
        db = FirebaseDatabase.getInstance().getReference();
        transactionRef = db.child("transaction");

        // transactionList = getTransactionSorted();

        // all findViewByID
        recyclerView = rootView.findViewById(R.id.transaction_recyclerView);
        addButton = rootView.findViewById(R.id.transaction_add_button);
        trsView = rootView.findViewById(R.id.trans_fragment);


        // RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new TransactionAdapter(transactionManager.getTransactions(), rootView.getContext(), rootView);
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
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TransactionAddActivity.class);
                startActivityForResult(intent, 111);
            }
        });


        // Firebase
        transactionRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               // Log.d(TAG+"Added", dataSnapshot.getValue(Transaction.class).toString());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG+"Changed",dataSnapshot.getValue(Transaction.class).toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG+"Removed",dataSnapshot.getValue(Transaction.class).toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG+"Moved",dataSnapshot.getValue(Transaction.class).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG+"Cancelled",databaseError.toString());
            }
        });

        return rootView;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Fragment fragment = getChildFragmentManager().findFragmentById(R.id.trans_fragment);
        if(requestCode == 111 && resultCode == Activity.RESULT_OK) {
            position = transactionManager.getTransactions().size();

            date = data.getStringExtra("date");
            account = data.getStringExtra("account");
            category = data.getStringExtra("category");
            description = data.getStringExtra("description");
            amount = data.getDoubleExtra("amount",0);

            //A new transaction can be added to the transaction list
            insertItem(position, date, account, category, description, amount);

        }
    }




    public void insertItem(int position, String date, String account, String category, String description, double amount){

        final String key = FirebaseDatabase.getInstance().getReference().child("transaction").push().getKey();

        this.imageResource = getActivity().getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", getActivity().getPackageName());
        this.date = date;
        this.account = account;
        this.category = category;
        this.amount = amount;
        this.description = description;
        Transaction transaction = new Transaction(this.date, this.imageResource, this.account, this.category, this.description, this.amount);
        transactionManager.addTransaction(transaction);

        adapter.notifyItemInserted(position);

        // Firebase
        Map<String, Object> transactionItemValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/transaction/" + key, transactionItemValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
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
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deletedTransaction, deletedIndex);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();

        }

    }
}
