package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.model.budgets.Budget;

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

        ProgressBar dateBar;
        TextView currentDate;
        ProgressBar amountBar;
        TextView currentAmount;


        TextView startAmount;
        TextView endAmount;

        public BudgetViewHolder(View view) {
            super(view);
            this.titleText = view.findViewById(R.id.title);

            this.startDate = view.findViewById(R.id.startDate);
            this.endDate = view.findViewById(R.id.endDate);


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

    @NonNull
    public BudgetAdapter.BudgetViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.budget, parent, false);
        return new BudgetViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.YY", Locale.getDefault());

        Budget b = dataSet.get(position);

        holder.titleText.setText(b.getCategory());
        holder.startDate.setText(format.format(b.getFrom()));
        holder.currentDate.setText(format.format(Calendar.getInstance().getTime()));

        holder.endDate.setText(format.format(b.getUntil()));
        holder.startAmount.setText(String.format(Locale.getDefault(), "%.2f %s",0.0f, "€"));
        holder.currentAmount.setText(String.format(Locale.getDefault(), "%.2f %s",b.getCurrentAmount(), "€"));
        holder.endAmount.setText(String.format(Locale.getDefault(), "%.2f %s",b.getBudget(), "€"));


        float datePart = 1f;
        if (b.getUntil().getTime() != b.getFrom().getTime())
            datePart = ((float)(Calendar.getInstance().getTime().getTime() - b.getFrom().getTime()) / (b.getUntil().getTime() - b.getFrom().getTime()));
        System.out.println(b.getFrom().getTime() + " - " + b.getUntil().getTime());
        System.out.println(Calendar.getInstance().getTime().getTime());
        float amountPart = b.getCurrentAmount() / b.getBudget();

        System.out.println(datePart);
        System.out.println(amountPart);


        setProgressBarColor(holder.dateBar, Color.parseColor("#00BBD3"));

        if (amountPart > 1f) setProgressBarColor(holder.amountBar, Color.parseColor("#EB5757"));
        else if (amountPart > datePart) setProgressBarColor(holder.amountBar, Color.parseColor("#F2994A"));
        else setProgressBarColor(holder.amountBar, Color.parseColor("#6FCF97"));



        holder.dateBar.setProgress((int)(datePart*100));
        holder.amountBar.setProgress((int)(amountPart*100));
    }

    private void setProgressBarColor(ProgressBar bar, int color) {
        if (!(bar.getProgressDrawable() instanceof LayerDrawable)) return;
        LayerDrawable layered = (LayerDrawable) bar.getProgressDrawable();
        Drawable d = layered.getDrawable(2);
        d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }


    private void removeView(View view) {
        ((ViewManager) view.getParent()).removeView(view);
    }


    @Override
    public int getItemCount() {
        return dataSet.size();
    }


}
