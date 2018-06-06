package com.lmu.pem.finanzapp.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {


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

        createTransactionList();

        recyclerView = rootView.findViewById(R.id.transaction_recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new TransactionAdapter(transactionList, R.layout.transactions_item);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);



/*
//
//
        transactionList = getTransactionSorted();


        RecyclerSectionItemDecoration transactionSectionItemDecoration = new RecyclerSectionItemDecoration(
                        getResources().getDimensionPixelSize(R.dimen.transaction_recycler_section_header), true, getSectionCallback(transactionList));
        recyclerView.addItemDecoration(transactionSectionItemDecoration);

*/
        addButton = rootView.findViewById(R.id.transaction_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), TransactionAddActivity.class);
                startActivityForResult(intent, 111);
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
               category = data.getStringExtra("category");
               account = data.getStringExtra("account");
               expense = data.getDoubleExtra("expense", 0);
               income = data.getDoubleExtra("income", 0);
               description = data.getStringExtra("description");
               //date = "11.Mai";
               //description = "...";

               //mAmount += mExpense;
               insertItem(position, date, account, category, description, expense, income);

           }
    }



    public void createTransactionList() {

        transactionList = new ArrayList<>();
        transactionList.add(new Transaction("05/05/2018", R.drawable.food, "Cash", "Food", "Hamburger", 30, 0));
        transactionList.add(new Transaction("05/29/2018", R.drawable.car, "Main", "Car", "Auto waschen",30, 0));
        transactionList.add(new Transaction("05/29/2018", R.drawable.hausehold, "Cash", "Household", "Edeka", 75,0));
        transactionList.add(new Transaction("05/30/2018", R.drawable.food, "Cash", "Food", "Käse",20,0));
        transactionList.add(new Transaction("05/30/2018", R.drawable.money, "Bank Account", "Salary","Werkstudenten-Gehalt", 0,1000));

    }


    public void insertItem(int position, String date, String account, String category, String description, double expense, double income){
        this.imageResource = getActivity().getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", getActivity().getPackageName());

        this.date = date;
        this.account = account;
        this.category = category;
        this.income = income;
        this.expense = expense;
        this.description = description;



        transactionList.add(new Transaction(this.date, this.imageResource, this.account, this.category, this.description, this.expense, this.income));
        adapter.notifyItemInserted(position);

    }



    public static ArrayList<Transaction> getTransactionSorted() {
        Collections.sort(transactionList);
        return transactionList;
    }


    /*
    //
    //
    //
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

*/





}
