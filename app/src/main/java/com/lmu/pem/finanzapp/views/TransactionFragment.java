package com.lmu.pem.finanzapp.views;

import android.app.Activity;
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

import com.lmu.pem.finanzapp.MainActivity;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.controller.TransactionAdapter;
import com.lmu.pem.finanzapp.model.Transaction;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionFragment extends Fragment {

    private ArrayList<Transaction> mTransactionList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button addButton;
    private Transaction mTransaction;

    private int position;
    private double mExpense;
    private double mAmount;
    private String mCategory;
    private String mAccount;
    private Object setContentView;


    public TransactionFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.transaction_fragment, container, false);
        MainActivity main = (MainActivity) getActivity();

        createTransactionList();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.transaction_recyclerView);


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new TransactionAdapter(mTransactionList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);



        addButton = rootView.findViewById(R.id.transaction_addButton);


        mTransaction = new Transaction("Food", "Cash", 0, 0, 0);
        mAmount = mTransaction.getAmount();



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
        //super.onActivityResult(requestCode, resultCode, data);

//        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.trans_fragment);

           if(requestCode == 111 && resultCode == Activity.RESULT_OK) {
               position = mTransactionList.size();
               mExpense = data.getDoubleExtra("expense", 0);
               mCategory = data.getStringExtra("category");
               insertItem(position, mAccount, mExpense, mCategory);

           }



    }

    /*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 111 ) {
            postion = mTransactionList.size();
            mExpense = data.getDoubleExtra("expense", 0);
            mCategory = data.getStringExtra("category");
            insertItem(postion, mAccount, mAmount, mCategory);

        }

    }
    */


    public void createTransactionList() {
        mTransactionList = new ArrayList<>();
        mTransactionList.add(new Transaction(R.drawable.food, "Cash", "Food",30));
        mTransactionList.add(new Transaction(R.drawable.car, "Bank Accounts", "Car",30));
        mTransactionList.add(new Transaction(R.drawable.hausehold, "Cash", "Household",30));
        mTransactionList.add(new Transaction(R.drawable.car, "Bank Accounts", "Car",120));
        mTransactionList.add(new Transaction(R.drawable.hausehold, "Cash", "Household",10));
    }

    public void insertItem(int position, String account, double amount, String category){
        mAccount = account;
        String amountStr = String.valueOf(amount) + " Euro";

        mTransactionList.add(new Transaction(R.drawable.money, "Cash", category, amount));
        mAdapter.notifyItemInserted(position);

    }





}
