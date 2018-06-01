package com.lmu.pem.finanzapp.views;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
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
        circleView.setSubText(""+accounts[position].getBalance());
        circleView.setCircleColor(accounts[position].getColor());

        circleView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch(event.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        Log.println(Log.INFO, "123123123", "Drag started!");
                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.println(Log.INFO, "123123123", "Drag entered!");
                        //TODO set color (call invalidate!) / ...
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    case DragEvent.ACTION_DROP:
                        Log.println(Log.INFO, "123123123", "Dropped!");
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.println(Log.INFO, "123123123", "Stopped dragging!");
                        return true;
                    default:
                        Log.println(Log.INFO, "123123123", "No idea what that was!");
                        break;
                }
                return true;
            }
        });

        return circleView;
    }
}
