package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.model.GlobalSettings;
import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;
import com.lmu.pem.finanzapp.model.dashboard.cards.*;

import java.util.ArrayList;
import java.util.Locale;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private Context context;

    private ArrayList<DbCard> dataSet;


    public static abstract class CardViewHolder extends  RecyclerView.ViewHolder {
        public TextView titleText;
        public Button btn1;
        public Button btn2;

        public CardViewHolder (View view) {
            super(view);
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class BasicAmountViewHolder extends CardViewHolder {
        // each data item is just a string in this case
        public TextView primaryText;
        public TextView amountText;
        public TextView amountDescText;
        public TextView secondaryText;


        public BasicAmountViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.title);
            primaryText = itemView.findViewById(R.id.primaryMessage);
            amountText = itemView.findViewById(R.id.amount);
            amountDescText = itemView.findViewById(R.id.amountDescription);
            secondaryText = itemView.findViewById(R.id.secondaryMessage);
            btn1 = itemView.findViewById(R.id.btn1);
            btn2 = itemView.findViewById(R.id.btn2);



        }
    }

    public static class WelcomeCardViewHolder extends CardViewHolder {
        public TextView primaryText;

        public  WelcomeCardViewHolder(View view) {
            super(view);
            this.titleText = view.findViewById(R.id.title);
            this.primaryText = view.findViewById(R.id.primaryMessage);
            this.btn1 = view.findViewById(R.id.btn1);
            this.btn2 = view.findViewById(R.id.btn2);


        }
    }

    public CardAdapter(ArrayList<DbCard> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v;

        if (viewType == DashboardManager.CardType.WELCOME.hashCode()) {
            v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.db_welcome, parent, false);
            WelcomeCardViewHolder vh = new WelcomeCardViewHolder(v);
            return vh;
        }
        else {
            v =  LayoutInflater.from(parent.getContext()).inflate(R.layout.db_basic_amount, parent, false);
            BasicAmountViewHolder vh = new BasicAmountViewHolder(v);
            return vh;

        }


    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {


        holder.titleText.setText(dataSet.get(position).getTitle());
        holder.btn1.setText(dataSet.get(position).getBtn1Text());


        if (getItemViewType(position) == DashboardManager.CardType.WELCOME.hashCode()) {
            WelcomeCardViewHolder h = (WelcomeCardViewHolder) holder;
            WelcomeCard c = (WelcomeCard) dataSet.get(position);

            h.primaryText.setText(c.getPrimaryText());
            h.btn2.setText(c.getBtn2Text());
        }
        else {
            BasicAmountViewHolder h = (BasicAmountViewHolder) holder;
            BasicAmountCard c = (BasicAmountCard) dataSet.get(position);

            h.primaryText.setText(c.getPrimaryText());
            h.amountText.setText(String.format(Locale.getDefault(), "%.2f %s",c.getAmount(), GlobalSettings.getInstance().getCurrency()));


            switch (c.getAmountType()) {


                case POSITIVE:
                    h.amountText.setTextColor(Resources.getSystem().getColor(android.R.color.holo_green_light));
                    h.amountDescText.setTextColor(Resources.getSystem().getColor(android.R.color.holo_green_light));
                    break;
                case NEGATIVE:
                    h.amountText.setTextColor(Resources.getSystem().getColor(android.R.color.holo_red_light));
                    h.amountDescText.setTextColor(Resources.getSystem().getColor(android.R.color.holo_red_light));
                    break;
            }

            if (c.getAmountDescription() == "")
                removeView(h.amountDescText);
            else
                h.amountDescText.setText(c.getAmountDescription());

            if (c.getSecondaryMessage() == "")
                removeView(h.secondaryText);
            else
                h.secondaryText.setText(c.getSecondaryMessage());

            if (c.getBtn2Text() == "")
                removeView(h.btn2);
            else
                h.btn2.setText(c.getBtn2Text());
        }

    }

    private void removeView(View view) {
        ((ViewManager) view.getParent()).removeView(view);
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    @Override
    public int getItemViewType(int position) {
        DbCard buffer = dataSet.get(position);

        return buffer.getType().hashCode();

    }
}