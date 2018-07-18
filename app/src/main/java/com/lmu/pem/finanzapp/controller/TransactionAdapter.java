package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
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
import com.lmu.pem.finanzapp.model.accounts.Account;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.views.TransactionFragment;

import java.util.ArrayList;
import java.util.Locale;


public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    private Context context;
    private ArrayList<Transaction> transactions;
    private View rootView;
    private TransactionViewHolder selectedItem;
    private TransactionManager transactionManager;
    private TransactionFragment transactionFragment;


    public TransactionAdapter(ArrayList<Transaction> transactions, Context context, View rootView, TransactionFragment fragment){
        this.transactions = transactions;
        this.context = context;
        this.rootView = rootView;
        this.transactionFragment = fragment;
        transactionManager = TransactionManager.getInstance();
    }



    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private ImageView categoryImageView;
        private TextView descriptionTextView, accountTextView, amountTextView;
        public RelativeLayout viewForeground;
        private LinearLayout viewBackground;
        private ImageButton deleteButton, backButton;
        ArrayList<Transaction> transactions;
        Context context;
        private TransactionAdapter transactionAdapter;


        public TransactionViewHolder(View itemView, Context context, ArrayList<Transaction> transactions, TransactionAdapter adapter) {
            super(itemView);
            //this.transactions = new ArrayList<>();
            this.transactions = transactions;
            this.context = context;
            this.transactionAdapter = adapter;


            // Alle findViewByIDs
            categoryImageView = (ImageView) itemView.findViewById(R.id.category_imageView);
            descriptionTextView = (TextView) itemView.findViewById(R.id.description_textView);
            accountTextView = itemView.findViewById(R.id.account_textView);
            amountTextView = itemView.findViewById(R.id.amount_textView);
            viewForeground = (RelativeLayout) itemView.findViewById(R.id.transaction_item_layout);
            viewBackground = (LinearLayout) itemView.findViewById(R.id.transaction_interaction);
            deleteButton = itemView.findViewById(R.id.delete_icon);
            backButton =  itemView.findViewById(R.id.back_icon);

            itemView.setOnLongClickListener((view)->{
                showInteractionView();

                deleteButton.setOnClickListener((v)->{
                    clickDelete(transactionAdapter);
                    hideInteractionView();
                });
                backButton.setOnClickListener((v)->{
                    hideInteractionView();
                });
                return true;
            });
            itemView.setOnClickListener((view)->{
                clickEdit();
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
            TransactionManager transactionManager = TransactionManager.getInstance();
            String description = transactionManager.getTransactions().get(getAdapterPosition()).getDescription();
            if(description.length()<1) description = transactionManager.getTransactions().get(getAdapterPosition()).getCategory();
            final int deletedIndex = getAdapterPosition();
            final Transaction deletedTransaction = transactionManager.getTransactions().get(deletedIndex);
            adapter.removeItem(deletedIndex);

            Snackbar snackbar = Snackbar.make(adapter.rootView, "Removed transaction \"" + description + "\" from list!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", v -> adapter.restoreItem(deletedTransaction, deletedIndex));

            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }


        private void clickEdit() {
            // Update(edit) a transaction
            int position = getAdapterPosition();

            Transaction transaction = this.transactions.get(position);
            Intent intent = new Intent(this.context, TransactionAddActivity.class);
            intent.putExtra("year", transaction.getYear());
            intent.putExtra("month", transaction.getMonth());
            intent.putExtra("day", transaction.getDay());
            intent.putExtra("account", transaction.getAccount());
            String acc2 = transaction.getAccount2();
            if(acc2!=null) intent.putExtra("account2", acc2);
            intent.putExtra("description", transaction.getDescription());
            intent.putExtra("amount", transaction.getAmount());
            intent.putExtra("key", transaction.getKey());

            int index;
            String[] expenseCategories, incomeCategories;
            expenseCategories = context.getResources().getStringArray(R.array.expense_category);
            incomeCategories = context.getResources().getStringArray(R.array.income_category);

            if (transaction.getAmount()<0){
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
            transactionAdapter.transactionFragment.startActivityForResult(intent, TransactionFragment.REQUEST_CODE_EDIT_TRANSACTION);
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transactions_item, parent, false);
        TransactionViewHolder transactionViewHolder = new TransactionViewHolder(itemView, context, transactions, this);
        return transactionViewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        Transaction currentTransactionItem = transactions.get(position);

        // Account
        Account account = AccountManager.getInstance().getAccountById(currentTransactionItem.getAccount());
        Account account2 = AccountManager.getInstance().getAccountById(currentTransactionItem.getAccount2());
        String acc, acc2;
        if(account==null){
            acc = currentTransactionItem.getAccount();
        }else{
            acc = account.getName();
        }
        if(currentTransactionItem.getAccount2()==null){
            holder.accountTextView.setText(acc);
        }else{
            if(account2==null){
                acc2 = currentTransactionItem.getAccount2();
            }else{
                acc2 = account2.getName();
            }
            holder.accountTextView.setText(Html.fromHtml(acc + " to " + acc2));
        }

        // Category-Image
        try{
            holder.categoryImageView.setImageResource(getImageByCategory(currentTransactionItem.getCategory()));
        }catch(Resources.NotFoundException e){
            Log.e("123123", "onBindViewHolder: Didn't find Image Resource", e);
        }


        double amount = currentTransactionItem.getAmount();
        if(currentTransactionItem.getAccount2()!=null){ //shift
            holder.amountTextView.setTextColor(Color.parseColor("#5d7379"));
        }else if(amount < 0) { //expense
            holder.amountTextView.setTextColor(Color.parseColor("#ff0000"));
        } else { //income
            holder.amountTextView.setTextColor(Color.parseColor("#30bf74"));
        }
        holder.amountTextView.setText(String.format(Locale.getDefault(), "%,.2f %s", amount, GlobalSettings.getInstance().getCurrencyString()));


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
        // Wenn transactions null ist, dann gibt 0 Wert zurück
        return null!= transactions ? transactions.size():0;
    }


    public void removeItem(int position) {
        transactionManager.removeTransaction(transactions.get(position));
        //transactions.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(Transaction transactionItem, int position){
        transactions.add(position, transactionItem);
        notifyItemInserted(position);
    }


    public void setSearchResult(ArrayList<Transaction> result){
        transactions = result;
        notifyDataSetChanged();
    }

    /**
     * Gets an image ResourceID for a given category.
     * @param category the Transaction category to get the image for. If there is no image for this category, a default image will be chosen.
     * @return the ResourceID for the image
     */
    private int getImageByCategory(String category) {
        int imageResource;
        int img = context.getResources().getIdentifier(category.toLowerCase().replace(" ", ""), "drawable", context.getPackageName());
        if(img == 0){
            imageResource = context.getResources().getIdentifier("money", "drawable", context.getPackageName());
        } else {
            imageResource = img;
        }
        return imageResource;
    }

}