package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.data.categories.Category;
import com.lmu.pem.finanzapp.model.Budget;
import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;
import com.lmu.pem.finanzapp.model.dashboard.cards.BasicAmountCard;
import com.lmu.pem.finanzapp.model.dashboard.cards.DbCard;
import com.lmu.pem.finanzapp.model.dashboard.cards.WelcomeCard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>{

    private Context context;

    private ArrayList<Budget> dataSet;



    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView titleText;

        TextView startDate;
        TextView endDate;

        int barWidth;

        CardView dateBar;
        TextView currentDate;

        CardView amountBar;
        TextView currentAmount;

        TextView startAmount;
        TextView endAmount;

        public BudgetViewHolder(View view) {
            super(view);
            this.titleText = view.findViewById(R.id.title);

            this.startDate = view.findViewById(R.id.startDate);
            this.endDate = view.findViewById(R.id.endDate);

            this.barWidth = view.findViewById(R.id.dateBarBg).getWidth();

            this.dateBar = view.findViewById(R.id.dateBar);
            this.currentDate = view.findViewById(R.id.currentDate);

            this.amountBar = view.findViewById(R.id.amountBar);
            this.currentAmount = view.findViewById(R.id.currentAmount);

            this.startAmount = view.findViewById(R.id.startAmount);
            this.endAmount = view.findViewById(R.id.endAmount);
        }
    }

    public BudgetAdapter(ArrayList<Budget> dataSet) {
        this.dataSet = dataSet;
    }

    public BudgetAdapter.BudgetViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget, parent, false);
        return new BudgetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.", Locale.getDefault());

        Budget b = dataSet.get(position);

        holder.titleText.setText(b.getCategory().getName());
        holder.startDate.setText(format.format(b.getFrom()));
        holder.endDate.setText(format.format(b.getUntil()));
        holder.startAmount.setText(String.format(Locale.getDefault(), "%.2f %s",0.0f, "€"));
        holder.endAmount.setText(String.format(Locale.getDefault(), "%.2f %s",b.getBudget(), "€"));



        holder.currentDate.setText(format.format(Calendar.getInstance().getTime()));
        holder.currentAmount.setText(String.format(Locale.getDefault(), "%.2f %s",b.getCurrentAmount(), "€"));


        float datePart = (float)(Calendar.getInstance().getTimeInMillis() / (b.getUntil().getTime() - b.getFrom().getTime()));
        float amountPart = b.getCurrentAmount() / b.getBudget();

        System.out.println(datePart);
        System.out.println(amountPart);
        holder.dateBar.setLayoutParams(new RelativeLayout.LayoutParams((int)(holder.barWidth * datePart), holder.dateBar.getHeight()));
        holder.amountBar.setLayoutParams(new RelativeLayout.LayoutParams((int)(holder.barWidth * amountPart), holder.amountBar.getHeight()));



    }


    private void removeView(View view) {
        ((ViewManager) view.getParent()).removeView(view);
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
