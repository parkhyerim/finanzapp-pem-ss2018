package com.lmu.pem.finanzapp.views;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lmu.pem.finanzapp.model.Account;

public class AccountAdapter extends BaseAdapter {
    private Context context;
    private Account[] accounts;
    private final int CIRCLE_SIZE = 400;

    public AccountAdapter(Context context, Account[] accounts) {
        this.context = context;
        this.accounts = accounts;
    }

    @Override
    public int getCount() {
        return accounts.length;
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
        if(convertView == null){ //not a recycled view
            circleView = new CircleView(this.context);
            circleView.setLayoutParams(new ViewGroup.LayoutParams(CIRCLE_SIZE,CIRCLE_SIZE));
        }else{
            circleView = (CircleView) convertView;
        }
        circleView.setText(accounts[position].getName(), accounts[position].isDefault());
        circleView.setCircleColor(accounts[position].getColor());

        return circleView;
    }
}
