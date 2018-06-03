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
import com.lmu.pem.finanzapp.RecyclerSectionItemDecoration;
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
    private String date;


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
        mAdapter = new TransactionAdapter(mTransactionList, R.layout.transactions_item);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerSectionItemDecoration transactionSectionItemDecoration = new RecyclerSectionItemDecoration(getResources().getDimensionPixelSize(R.dimen.transaction_recycler_section_header),
                true, getSectionCallback(mTransactionList) );
        mRecyclerView.addItemDecoration(transactionSectionItemDecoration);





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
               this.date = "11.Mai";
               //mAmount += mExpense;
               insertItem(position, mAccount, mExpense, mCategory, this.date);

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
        mTransactionList.add(new Transaction(R.drawable.food, "Cash", "Food",30, "1.Mai"));
        mTransactionList.add(new Transaction(R.drawable.car, "Bank Accounts", "Car",30, "1.Mai"));
        mTransactionList.add(new Transaction(R.drawable.hausehold, "Cash", "Household",30,"2.Mai"));
        mTransactionList.add(new Transaction(R.drawable.car, "Bank Accounts", "Car",120,"3.Mai"));
        mTransactionList.add(new Transaction(R.drawable.hausehold, "Cash", "Household",10,"1.Mai"));
    }

    public void insertItem(int position, String account, double amount, String category, String date){
        mAccount = account;
        String amountStr = String.valueOf(amount) + " Euro";
        this.date = date;

        mTransactionList.add(new Transaction(R.drawable.money, "Cash", category, amount, this.date));
        mAdapter.notifyItemInserted(position);

    }



    private RecyclerSectionItemDecoration.SectionCallback getSectionCallback(final ArrayList<Transaction> transactionList){
        return new RecyclerSectionItemDecoration.SectionCallback() {
            @Override
            public boolean isSection(int position) {
                return position == 0
                        || transactionList.get(position)
                        .getCategory()
                        .charAt(0) != transactionList.get(position - 1)
                        .getCategory()
                        .charAt(0);
            }

            @Override
            public CharSequence getSectionHeader(int position) {
                return transactionList.get(position)
                        .getCategory()
                        .subSequence(0,
                                1);
            }
        };
    }



}
