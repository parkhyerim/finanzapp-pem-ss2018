package com.lmu.pem.finanzapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.support.v7.widget.SearchView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.jaychang.srv.decoration.SectionHeaderProvider;
import com.lmu.pem.finanzapp.MainActivity;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.RecyclerSectionItemDecoration;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.controller.TransactionAdapter;
import com.lmu.pem.finanzapp.model.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment implements SearchView.OnQueryTextListener {

    DatabaseReference db;
    DatabaseReference transactionRef;

    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;


    private FloatingActionButton addButton;

    private static ArrayList<Transaction> transactionList;

    private int position;
    private int imageResource;
    private double expense = 0;
    private double income = 0;
    private String category = "";
    private String account = "";
    private String date = "";
    private String description = "";


    public TransactionFragment() {
        super();
        // Required empty public constructor
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

        createTransactionList();
        // transactionList = getTransactionSorted();

        // all findViewByID
        recyclerView = rootView.findViewById(R.id.transaction_recyclerView);
        addButton = rootView.findViewById(R.id.transaction_add_button);


        // RecyclerView
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new TransactionAdapter(transactionList, rootView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Header-Section
        RecyclerSectionItemDecoration transactionSectionItemDecoration =
                new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.transaction_recycler_section_header),
                        true,
                        getSectionCallback(transactionList));
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
            position = transactionList.size();

            date = data.getStringExtra("date");
            account = data.getStringExtra("account");
            category = data.getStringExtra("category");
            description = data.getStringExtra("description");
            expense = data.getDoubleExtra("expense", 0);
            income = data.getDoubleExtra("income", 0);

            //A new transaction can be added to the transaction list
            insertItem(position, date, account, category, description, expense, income);
        }
    }


    public void createTransactionList() {
        transactionList = new ArrayList<>();


        // dummy transaction list
        transactionList.add(new Transaction("04/28/2018", R.drawable.salary, "Cash", "Salary", "Werkstudenten-Gehalt", 0, 450));
        //transactionList.add(new Transaction("04/29/2018", R.drawable.food, "Main", "Food", "Pizza & Burger", 42, 0));
        //transactionList.add(new Transaction("05/01/2018", R.drawable.music, "Main", "Music", "BTS CD",28, 0));
        transactionList.add(new Transaction("05/02/2018", R.drawable.household, "Cash", "Household", "Edeka", 55.20, 0));
        transactionList.add(new Transaction("05/02/2018", R.drawable.bonus, "Cash", "Bonus", "Bonus!!!", 0, 180));
        transactionList.add(new Transaction("05/05/2018", R.drawable.movie, "Cash", "Movie", "Black Panther", 21, 0));
        transactionList.add(new Transaction("05/05/2018", R.drawable.gift, "Cash", "Gift", "Muttertag", 38.25, 0));

    }


    public void insertItem(int position, String date, String account, String category, String description, double expense, double income){

        final String key = FirebaseDatabase.getInstance().getReference().child("transaction").push().getKey();

        this.imageResource = getActivity().getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", getActivity().getPackageName());
        this.date = date;
        this.account = account;
        this.category = category;
        this.income = income;
        this.expense = expense;
        this.description = description;
        Transaction transaction = new Transaction(this.date, this.imageResource, this.account, this.category, this.description, this.expense, this.income);
        transactionList.add(transaction);
        adapter.notifyItemInserted(position);

        // Firebase
        Map<String, Object> transactionItemValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/transaction/" + key, transactionItemValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }


    public static ArrayList<Transaction> getTransactionSorted() {
        Collections.sort(transactionList);
        return transactionList;
    }


    // Header-Section by date
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<Transaction> transactionList){
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                /*
                String date = transactionList.get(position).getDate();
                String date2 = transactionList.get(position - 1).getDate();
                return position == 0 || !date.equals(date2);
                */

                return position == 0 || transactionList.get(position).getDate().charAt(0) != transactionList.get(position - 1).getDate().charAt(0);
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                date = transactionList.get(position).getDate().toString();
                return date;
                //return transactionList.get(position).getDate().subSequence(0, 8);
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
                adapter.setSearchResult(transactionList);
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
        final ArrayList<Transaction> filteredList = filter(transactionList, newText);
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
            final String expenseText = String.valueOf(transaction.getExpense()).toLowerCase();
            final String incomeText = String.valueOf(transaction.getIncome()).toLowerCase();
            if(descText.contains(query) || accText.contains(query)
                    ||cateText.contains(query) ||dateText.contains(query)
                    || expenseText.contains(query) || incomeText.contains(query)){
                fiteredList.add(transaction);
            }
        }
        return  fiteredList;
    }

}
