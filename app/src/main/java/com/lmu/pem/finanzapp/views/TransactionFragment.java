package com.lmu.pem.finanzapp.views;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {

    DatabaseReference db;
    DatabaseReference transactionRef;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Button addButton;

    private static ArrayList<Transaction> transactionList;
    private Transaction transaction;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.transaction_fragment, container, false);


        db = FirebaseDatabase.getInstance().getReference();
        transactionRef = db.child("transaction");


        createTransactionList();

        recyclerView = rootView.findViewById(R.id.transaction_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new TransactionAdapter(transactionList, R.layout.transactions_item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



        transactionList = getTransactionSorted();



        RecyclerSectionItemDecoration transactionSectionItemDecoration = new RecyclerSectionItemDecoration(
                        getResources().getDimensionPixelSize(R.dimen.transaction_recycler_section_header), true, getSectionCallback(transactionList));
        recyclerView.addItemDecoration(transactionSectionItemDecoration);


        // to add a new transaction
        addButton = rootView.findViewById(R.id.transaction_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TransactionAddActivity.class);
                startActivityForResult(intent, 111);
            }
        });

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

               //new transaction can be added to the transaction list
               insertItem(position, date, account, category, description, expense, income);

           }
    }



    public void createTransactionList() {

        transactionList = new ArrayList<>();

        // dummy transaction list
        /*
        transactionList.add(new Transaction("28/04/2018", R.drawable.salary, "Bank Account", "Salary", "Werkstudenten-Gehalt", 0, 450));
        transactionList.add(new Transaction("29/04/2018", R.drawable.food, "Main", "Food", "Pizza & Burger", 42, 0));
        transactionList.add(new Transaction("01/05/2018", R.drawable.music, "Main", "Music", "BTS CD",28, 0));
        transactionList.add(new Transaction("04/05/2018", R.drawable.household, "Cash", "Household", "Edeka", 55.20,0));
        transactionList.add(new Transaction("05/05/2018", R.drawable.bonus, "Cash", "Bonus", "Bonus!!!",0,180));
        transactionList.add(new Transaction("13/05/2018", R.drawable.gift, "Cash", "Gift","Muttertag", 38.25,0));
        transactionList.add(new Transaction("14/05/2018", R.drawable.culture, "Cash", "Culture","Black Panther", 21,0));
*/
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

        Map<String, Object> transactionItemValues = transaction.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/transaction/" + key, transactionItemValues);
        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

    }


    public static ArrayList<Transaction> getTransactionSorted() {
        Collections.sort(transactionList);
        return transactionList;
    }


    // Header
    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<Transaction> transactionList){
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
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
}
