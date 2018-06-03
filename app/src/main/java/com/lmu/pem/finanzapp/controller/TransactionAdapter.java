package com.lmu.pem.finanzapp.controller;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.model.Transaction;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    private ArrayList<Transaction> mTransactionList;
    private final int rowLayout;

    public static class TransactionViewHolder extends RecyclerView.ViewHolder{

        public ImageView categoryImageView;
        public TextView categoryTextView;
        public TextView accountTextView;
        public TextView amountTextView;

        public TransactionViewHolder(View itemView) {
            super(itemView);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_imageView);
            categoryTextView = (TextView) itemView.findViewById(R.id.categoryName_textView);
            accountTextView = (TextView) itemView.findViewById(R.id.account_textView);
            amountTextView = (TextView) itemView.findViewById(R.id.amount_textView);
        }
    }

    public TransactionAdapter(ArrayList<Transaction> transactionList, int rowLayout){
        mTransactionList = transactionList;
        this.rowLayout = rowLayout;
    }


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_item, parent, false);
        TransactionViewHolder transactionViewHolder = new TransactionViewHolder(v);
        return transactionViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        Transaction currentItem = mTransactionList.get(position);

        holder.categoryImageView.setImageResource(currentItem.getImageResource());
        holder.categoryTextView.setText(currentItem.getCategory());
        holder.accountTextView.setText(currentItem.getAccount());
        holder.amountTextView.setText(String.valueOf(currentItem.getAmount()));

    }

    @Override
    public int getItemCount() {
        return null!= mTransactionList? mTransactionList.size():0;
    }


}
