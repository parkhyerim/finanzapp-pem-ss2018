package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.graphics.Color;
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

    private ArrayList<Transaction> transactionList;
    private final int rowLayout;


    //
    //
    //


    public TransactionAdapter(ArrayList<Transaction> transactionList, int rowLayout){

        this.transactionList = transactionList;
        this.rowLayout = rowLayout;

    }


    public static class TransactionViewHolder extends RecyclerView.ViewHolder{

        private ImageView categoryImageView;
        private TextView descriptionTextView;
        private TextView accountTextView;
        private TextView moneyTextView;

        //
        //


        public TransactionViewHolder(View itemView) {
            super(itemView);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_imageView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_textView);
            accountTextView = (TextView) itemView.findViewById(R.id.account_textView);
            moneyTextView = (TextView) itemView.findViewById(R.id.money_textView);

            /*
            Context c = itemView.getApplicationContext();
            int id = c.getResources().getIdentifier("drawable/"+"food", null, c.getPackageName());

            */

        }
    }

    //
    //
    //
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView){
            super(itemView);

        }

    }

    //
    //
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView){
            super(itemView);

        }

    }





    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_item, parent, false);
        TransactionViewHolder transactionViewHolder = new TransactionViewHolder(view);
        return transactionViewHolder;


    }



    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        Transaction currentItem = transactionList.get(position);
        holder.categoryImageView.setImageResource(currentItem.getImageResource());

        holder.accountTextView.setText(currentItem.getAccount());


        //TODO Währungssymbol besser zeigen
        if(currentItem.getExpense() > currentItem.getIncome()) {
            holder.moneyTextView.setText("-" + String.valueOf(currentItem.getExpense())+ "\u20ac");
            holder.moneyTextView.setTextColor(Color.parseColor("#ff0000"));
        } else  {
            holder.moneyTextView.setText(String.valueOf(currentItem.getIncome())+ "\u20ac");
            holder.moneyTextView.setTextColor(Color.parseColor("#2BAB68"));
        }

        if(currentItem.getDescrition().equals("")){
            holder.descriptionTextView.setText(currentItem.getCategory());
        } else {
            holder.descriptionTextView.setText(currentItem.getDescrition());
        }




    }

    @Override
    public int getItemCount() {

        // Wenn transactionList null ist, dann gibt 0 Wert zurück
        return null!= transactionList? transactionList.size():0;
    }







}
