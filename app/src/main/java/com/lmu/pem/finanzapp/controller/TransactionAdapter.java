package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.data.Account;
import com.lmu.pem.finanzapp.model.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.transactions.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    private Context context;
    private ArrayList<Transaction> transactionList;
    private View rootView;
    private TransactionViewHolder selectedItem;


    public TransactionAdapter(ArrayList<Transaction> transactionList, Context context, View rootView){
        this.transactionList = transactionList;
        this.context = context;
        this.rootView = rootView;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImageView;
        private TextView descriptionTextView;
        private TextView accountTextView;
        private TextView amountTextView;
        public RelativeLayout viewForeground;
        private LinearLayout viewBackground;
        private ImageButton editButton, deleteButton, backButton;
        ArrayList<Transaction> transactions;
        Context context;
        private TransactionAdapter transactionAdapter;


        public TransactionViewHolder(View itemView, Context context, ArrayList<Transaction> transactions, TransactionAdapter adapter) {
            super(itemView);
            this.transactions = new ArrayList<>();
            this.transactions = transactions;
            this.context = context;
            this.transactionAdapter = adapter;

            // Alle findViewByIDs
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_imageView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_textView);
            accountTextView = itemView.findViewById(R.id.account_textView);
            amountTextView = itemView.findViewById(R.id.money_textView);
            viewForeground = (RelativeLayout) itemView.findViewById(R.id.transaction_item_layout);
            viewBackground = (LinearLayout) itemView.findViewById(R.id.transaction_interaction);
            editButton = (ImageButton) itemView.findViewById(R.id.edit_icon);
            deleteButton = itemView.findViewById(R.id.delete_icon);
            backButton =  itemView.findViewById(R.id.back_icon);

            itemView.setOnLongClickListener((view)->{
                showInteractionView();

                editButton.setOnClickListener((v)->{
                    clickEdit();
                    hideInteractionView();
                });
                deleteButton.setOnClickListener((v)->{
                    clickDelete(transactionAdapter);
                    hideInteractionView();
                });
                backButton.setOnClickListener((v)->{
                    hideInteractionView();
                });
                return true;
            });
        }

        private void showInteractionView(){
            viewForeground.setVisibility(View.INVISIBLE);
            viewBackground.setVisibility(View.VISIBLE);

            if(transactionAdapter.selectedItem!=null){
                transactionAdapter.selectedItem.hideInteractionView();
            }
            transactionAdapter.selectedItem = this;
        }
        private void hideInteractionView(){
            viewForeground.setVisibility(View.VISIBLE);
            viewBackground.setVisibility(View.INVISIBLE);

            transactionAdapter.selectedItem = null;
        }

        private void clickDelete(TransactionAdapter adapter) {
           // TransactionHistory transactionHistory = TransactionHistory.getInstance();
            TransactionManager transactionManager = TransactionManager.getInstance();
            String name = transactionManager.getTransactions().get(getAdapterPosition()).getDescription();
            final Transaction deletedTransaction = transactionManager.getTransactions().get(getAdapterPosition());
            final int deletedIndex = getAdapterPosition();
            adapter.removeItem(deletedIndex);

            Snackbar snackbar = Snackbar.make(adapter.rootView, name + " removed from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deletedTransaction, deletedIndex);
                }
            });

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }


        private void clickEdit() {
            // Update(edit) a transaction
            int position = getAdapterPosition();
            Transaction transaction = this.transactions.get(position);
            Intent intent = new Intent(this.context, TransactionAddActivity.class);
            intent.putExtra("date", transaction.getDate());
            intent.putExtra("account", transaction.getAccount());
            intent.putExtra("description", transaction.getDescription());
            intent.putExtra("amount", transaction.getAmount());

            int index;
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
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView){
            super(itemView);
            }
        }
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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_item, parent, false);
        TransactionViewHolder transactionViewHolder = new TransactionViewHolder(itemView, context, transactionList, this);
        return transactionViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        Transaction currentTransactionItem = transactionList.get(position);

        // Account
        Account account = AccountManager.getInstance().getAccountById(currentTransactionItem.getAccount());
        if(account==null){
            holder.accountTextView.setText(currentTransactionItem.getAccount());
        }else{
            holder.accountTextView.setText(account.getName());
        }

        // Category-Image


        holder.categoryImageView.setImageResource(currentTransactionItem.getImageResource());

        // Expense or Income
        String prefix = "";
        double amount = currentTransactionItem.getAmount();

        if(amount < 0) {
            //prefix="-";
            //amount = currentTransactionItem.getAmount();
            holder.amountTextView.setTextColor(Color.parseColor("#ff0000"));
        } else {
           // amount = currentTransactionItem.getAmount();
            holder.amountTextView.setTextColor(Color.parseColor("#2BAB68"));
        }
        holder.amountTextView.setText(String.format(Locale.getDefault(), "%,.2f %s", amount, GlobalSettings.getInstance(context).getCurrencyString()));


        // Description
        if(currentTransactionItem.getDescription().equals("")){
            // If there is no description-input, then the category name will be shown.
            holder.descriptionTextView.setText(currentTransactionItem.getCategory());
        } else {
            holder.descriptionTextView.setText(currentTransactionItem.getDescription());
        }
    }


    @Override
    public int getItemCount() {
        // Wenn transactionList null ist, dann gibt 0 Wert zurück
        return null!= transactionList? transactionList.size():0;
    }


    public void removeItem(int position) {
        transactionList.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(Transaction transactionItem, int position){
        transactionList.add(position, transactionItem);
        notifyItemInserted(position);
    }


    public void setSearchResult(ArrayList<Transaction> result){
        transactionList = result;
        notifyDataSetChanged();
    }

}