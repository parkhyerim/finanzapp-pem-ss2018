package com.lmu.pem.finanzapp.controller;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.model.accounts.Account;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.accounts.AccountManager;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.views.AccountFragment;
import com.lmu.pem.finanzapp.views.CircleView;
import com.lmu.pem.finanzapp.views.TransactionFragment;

import java.util.ArrayList;
import java.util.Locale;

public class AccountAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Account> accounts;
    private AccountFragment fragment;

    private final int CIRCLE_SIZE = 380;

    public AccountAdapter(Context context, ArrayList<Account> accounts, AccountFragment fragment) {
        this.context = context;
        this.accounts = accounts;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CircleView circleView;
        RelativeLayout relativeLayout;
        ImageView imageView;
        if(convertView == null){ //not a recycled view
            circleView = new CircleView(this.context);
            circleView.setLayoutParams(new ViewGroup.LayoutParams(CIRCLE_SIZE,CIRCLE_SIZE));
            circleView.setId(View.generateViewId());
            relativeLayout = new RelativeLayout(context);
            relativeLayout.setGravity(Gravity.CENTER);
            relativeLayout.addView(circleView);
            imageView = new ImageView(context);
            imageView.setImageResource(R.drawable.bonus);
            relativeLayout.addView(imageView);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(110, 110);
            layoutParams.addRule(RelativeLayout.ALIGN_END, circleView.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, circleView.getId());
            imageView.setLayoutParams(layoutParams);
        }else{
            relativeLayout = (RelativeLayout) convertView;
            circleView = (CircleView) ((RelativeLayout) convertView).getChildAt(0);
            imageView = (ImageView) ((RelativeLayout) convertView).getChildAt(1);
        }
        String text = accounts.get(position).getName();
        String trimmedText=text;
        if(text.length()>9) trimmedText=text.substring(0, 6)+"...";
        circleView.setText(trimmedText, accounts.get(position).isDefault());
        String subtext = String.format(Locale.getDefault(), "%,.2f %s",accounts.get(position).getBalance(), GlobalSettings.getInstance().getCurrencyString());
        circleView.setSubText(subtext);
        circleView.setCircleColor(accounts.get(position).getColor());

        circleView.setOnClickListener(view -> {
            fragment.editAccount(accounts.get(position).getId());
        });

        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TransactionAddActivity.class);
            intent.putExtra("account", accounts.get(position).getId());
            fragment.startActivityForResult(intent, TransactionFragment.REQUEST_CODE_ADD_TRANSACTION);
        });


        imageView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("$$$", accounts.get(position).getId());
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);
            return true;
        });

        circleView.setOnDragListener((v, event) -> {
            if(event.getAction()==DragEvent.ACTION_DROP){
                Log.println(Log.INFO, "123123123", "Dropped! "+ event.getClipData().getItemAt(0).getText().toString()+" -> "+accounts.get(position).getId());
                if(!(event.getClipData().getItemAt(0).getText().toString().equals(accounts.get(position).getId()))){
                    Intent intent = new Intent(context, TransactionAddActivity.class);
                    intent.putExtra("account", event.getClipData().getItemAt(0).getText().toString());
                    intent.putExtra("account2", accounts.get(position).getId());
                    fragment.startActivityForResult(intent, TransactionFragment.REQUEST_CODE_ADD_TRANSACTION);
                }
            }
            return true;
        });

        circleView.setOnLongClickListener((v)->{
            String accId = accounts.get(position).getId();

            AlertDialog.Builder builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            if(TransactionManager.getInstance().transactionsForAccount(accId)){
                builder.setTitle("Can't delete account")
                        .setMessage("You can't delete an account that has transactions linked to it!\nYou have to delete the transactions for this Account before you can delete this account.")
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            // do nothing
                        });
            }else{
                builder.setTitle("Delete account")
                        .setMessage("Are you sure you want to delete this account?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            AccountManager.getInstance().deleteAccount(accId);
                            notifyDataSetChanged();
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // do nothing
                        });
            }
            builder.show();
            return true;
        });

        return relativeLayout;
    }
}
