package com.lmu.pem.finanzapp.controller;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.data.Account;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.views.AccountFragment;
import com.lmu.pem.finanzapp.views.CircleView;
import com.lmu.pem.finanzapp.views.TransactionFragment;

import java.util.ArrayList;
import java.util.Locale;

public class AccountAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Account> accounts;
    private AccountFragment fragment;

    private final int CIRCLE_SIZE = 400;

    public AccountAdapter(Context context, ArrayList<Account> accounts, AccountFragment fragment) {
        this.context = context;
        this.accounts = accounts;
        this.fragment = fragment;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
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
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);
            layoutParams.addRule(RelativeLayout.ALIGN_END, circleView.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM, circleView.getId());
            imageView.setLayoutParams(layoutParams);
        }else{
            relativeLayout = (RelativeLayout) convertView;
            circleView = (CircleView) ((RelativeLayout) convertView).getChildAt(0);
            imageView = (ImageView) ((RelativeLayout) convertView).getChildAt(1);
        }
        String text = accounts.get(position).getName();
        circleView.setText(text, accounts.get(position).isDefault());
        String subtext = String.format(Locale.getDefault(), "%,.2f %s",accounts.get(position).getBalance(), GlobalSettings.getInstance().getCurrencyString());
        circleView.setSubText(subtext);
        circleView.setCircleColor(accounts.get(position).getColor());

        circleView.setOnClickListener(view -> {
            fragment.editAccount(accounts.get(position).getId());
        });


        imageView.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("$$$", accounts.get(position).getId());
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            /*
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(){
                @Override
                public void onDrawShadow(Canvas canvas) {
                    Drawable img = context.getResources().getDrawable(R.drawable.bonus);
                    //img.setBounds(100,100,100,100);
                    img.draw(canvas);
                }
            };*/
            v.startDrag(data, shadowBuilder, v, 0);
            return true;
        });

        circleView.setOnDragListener((v, event) -> {
            switch(event.getAction()){
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.println(Log.INFO, "123123123", "Started dragging...");
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.println(Log.INFO, "123123123", "Drag entered!");
                    //TODO set color (call invalidate!) / ...
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.println(Log.INFO, "123123123", "Drag exited!");
                    //TODO set color (call invalidate!) / ...
                    break;
                case DragEvent.ACTION_DROP:
                    Log.println(Log.INFO, "123123123", "Dropped! "+ event.getClipData().getItemAt(0).getText().toString()+" -> "+accounts.get(position).getId());
                    if(!(event.getClipData().getItemAt(0).getText().toString().equals(accounts.get(position).getId()))){
                        Intent intent = new Intent(context, TransactionAddActivity.class);
                        intent.putExtra("account", event.getClipData().getItemAt(0).getText().toString());
                        intent.putExtra("account2", accounts.get(position).getId());
                        fragment.startActivityForResult(intent, TransactionFragment.REQUEST_CODE_ADD_TRANSACTION);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.println(Log.INFO, "123123123", "...Stopped dragging!");
                    //TODO set color (call invalidate!) / ...
                    break;
            }
            return true;
        });

        return relativeLayout;
    }
}
