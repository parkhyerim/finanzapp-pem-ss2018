package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.data.Account;
import com.lmu.pem.finanzapp.model.AccountManager;
import com.lmu.pem.finanzapp.model.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    private ArrayList<Transaction> transactionList;
    Context context;

   // private final int rowLayout;



    public TransactionAdapter(ArrayList<Transaction> transactionList, Context context){

        this.transactionList = transactionList;
        //this.rowLayout = rowLayout;
        this.context = context;

    }


    public static class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView categoryImageView;
        private TextView descriptionTextView;
        private TextView accountTextView;
        private TextView moneyTextView;
        ArrayList<Transaction> transactions = new ArrayList<>();
        Context context;


        public TransactionViewHolder(View itemView, Context context, ArrayList<Transaction> transactions) {
            super(itemView);

            this.transactions = transactions;
            this.context = context;
            itemView.setOnClickListener(this);
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_imageView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_textView);
            accountTextView = (TextView) itemView.findViewById(R.id.account_textView);
            moneyTextView = (TextView) itemView.findViewById(R.id.money_textView);

        }

        public void onClick(View v) {

            // update(edit) a transaction

            int position = getAdapterPosition();
            Transaction transaction = this.transactions.get(position);
            Intent intent = new Intent(this.context, TransactionAddActivity.class);
            intent.putExtra("date", transaction.getDate());
            intent.putExtra("money", transaction.getMoney());
            intent.putExtra("account", transaction.getAccount());
            intent.putExtra("description", transaction.getDescription());

            int index = 0;
            String[] expenseCategories, incomeCategories;

            expenseCategories = context.getResources().getStringArray(R.array.expense_category);
            incomeCategories = context.getResources().getStringArray(R.array.income_category);

            if (Arrays.asList(expenseCategories).contains(transaction.getCategory())){

                for(int i = 0 ; i < expenseCategories.length; i++) {
                    if(expenseCategories[i].equals(transaction.getCategory())){
                        index = i;
                        intent.putExtra("category", index);
                    }
                }
            } else {

                for(int i = 0 ; i < incomeCategories.length; i++) {
                    if(incomeCategories[i].equals(transaction.getCategory())){
                        index = i;
                        intent.putExtra("category2", index);

                    }
                }
            }

            this.context.startActivity(intent);
        }
    }

    /*

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
*/


    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactions_item, parent, false);
        TransactionViewHolder transactionViewHolder = new TransactionViewHolder(view, context, transactionList);
        return transactionViewHolder;

    }



    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        Transaction currentItem = transactionList.get(position);
        Account acc = AccountManager.getInstance().getAccountById(currentItem.getAccount());
        if(acc==null){
            holder.accountTextView.setText(currentItem.getAccount());
        }else{
            holder.accountTextView.setText(acc.getName());
        }

        holder.categoryImageView.setImageResource(currentItem.getImageResource());


        //TODO Währungssymbol effizienter, kluger zeigen...
        if(currentItem.getExpense() > currentItem.getIncome()) {

            holder.moneyTextView.setText("-" + String.valueOf(currentItem.getExpense())+ "\u20ac");
            holder.moneyTextView.setTextColor(Color.parseColor("#ff0000"));

        } else  {

            holder.moneyTextView.setText(String.valueOf(currentItem.getIncome())+ "\u20ac");
            holder.moneyTextView.setTextColor(Color.parseColor("#2BAB68"));

        }

        if(currentItem.getDescription().equals("")){

            // If there is no description-input, then the category name will be shown.
            holder.descriptionTextView.setText(currentItem.getCategory());

        } else {

            holder.descriptionTextView.setText(currentItem.getDescription());
        }




    }

    @Override
    public int getItemCount() {

        // Wenn transactionList null ist, dann gibt 0 Wert zurück
        return null!= transactionList? transactionList.size():0;

    }


    public void setSearchResult(ArrayList<Transaction> result){
        transactionList = result;
        notifyDataSetChanged();
    }





}
