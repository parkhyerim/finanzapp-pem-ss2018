package com.lmu.pem.finanzapp.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.views.dashboard.DashboardFragment;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    /**
     * The DataSet of Cards to display
     */
    private ArrayList<DbCard> dataSet;

    /**
     * A handle to the fragment with the RecyclerView.
     */
    private DashboardFragment fragmentHandle;

    /**
     * The basic ViewHolder for any card.
     */
    static abstract class CardViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;

        CardViewHolder(View view) {
            super(view);
        }
    }

    /**
     * The ViewHolder for the basic amount cards.
     */
    public static class BasicAmountViewHolder extends CardViewHolder {
        // each data item is just a string in this case
        TextView primaryText;
        TextView amountText;
        TextView amountDescText;
        TextView secondaryText;


        BasicAmountViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.title);
            primaryText = itemView.findViewById(R.id.primaryMessage);
            amountText = itemView.findViewById(R.id.amount);
            amountDescText = itemView.findViewById(R.id.amountDescription);
            secondaryText = itemView.findViewById(R.id.secondaryMessage);


        }
    }

    /**
     * The ViewHolder for the welcome cards.
     */
    public static class WelcomeCardViewHolder extends CardViewHolder {
        TextView primaryText;
        public Button button;

        WelcomeCardViewHolder(View view) {
            super(view);
            this.titleText = view.findViewById(R.id.title);
            this.primaryText = view.findViewById(R.id.primaryMessage);
            this.button = view.findViewById(R.id.btn1);

        }
    }

    /**
     * Constructs a new CardAdapter. Takes the dataSet to display as an argument and the fragment
     * that created it.
     * @param dataSet The DataSet of Cards to display
     * @param fragment The fragment containing the RecyclerView that displays the cards.
     */
    public CardAdapter(ArrayList<DbCard> dataSet, @NonNull DashboardFragment fragment) {
        this.dataSet = dataSet;
        this.fragmentHandle = fragment;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
        View v;

        if (viewType == DashboardManager.CardType.WELCOME.hashCode() || viewType == DashboardManager.CardType.SWIPETUTORIAL.hashCode()) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.db_welcome, parent, false);
            return new WelcomeCardViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.db_basic_amount, parent, false);
            return new BasicAmountViewHolder(v);

        }


    }

    /**
     * Binds a ViewHolder to the model object representing the card. Also deals with setting colors
     * and dynamic text.
     * @param holder The ViewHolder to bind.
     * @param position the position in the list to bind.
     */
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {


        holder.titleText.setText(dataSet.get(position).getTitle());


        if (getItemViewType(position) == DashboardManager.CardType.WELCOME.hashCode()
                || getItemViewType(position) == DashboardManager.CardType.SWIPETUTORIAL.hashCode()) {
            WelcomeCardViewHolder h = (WelcomeCardViewHolder) holder;
            WelcomeCard c = (WelcomeCard) dataSet.get(position);
            if (c.getBtnText().equals(""))
                h.button.setVisibility(View.GONE);
            else if (TransactionManager.getInstance().getTransactions().size() == 0)
                h.button.setOnClickListener((v) -> fragmentHandle.startAddTransactionActivity()
                );
            else
                h.button.setOnClickListener((v) -> DashboardManager.getInstance(fragmentHandle.getContext()).reset());

            h.primaryText.setText(c.getPrimaryText());
        } else {
            BasicAmountViewHolder h = (BasicAmountViewHolder) holder;
            BasicAmountCard c = (BasicAmountCard) dataSet.get(position);

            h.primaryText.setText(c.getPrimaryText());
            h.amountText.setText(String.format(Locale.getDefault(), "%,.2f %s", c.getAmount(), GlobalSettings.getInstance().getCurrencyString()));

            Context context = fragmentHandle.getContext();
            assert context != null;

            switch (c.getAmountType()) {
                case POSITIVE:
                    h.amountText.setTextColor(context.getColor(R.color.positiveAmount));
                    h.amountDescText.setTextColor(context.getColor(R.color.positiveAmount));
                    break;
                case WARNING:
                    h.amountText.setTextColor(context.getColor(R.color.warningAmount));
                    h.amountDescText.setTextColor(context.getColor(R.color.warningAmount));
                    break;
                case NEGATIVE:
                    h.amountText.setTextColor(context.getColor(R.color.negativeAmount));
                    h.amountDescText.setTextColor(context.getColor(R.color.negativeAmount));
                    break;
            }

            if (c.getAmountDescription().equals(""))
                removeView(h.amountDescText);
            else
                h.amountDescText.setText(c.getAmountDescription());

            if (c.getSecondaryMessage().equals(""))
                removeView(h.secondaryText);
            else
                h.secondaryText.setText(c.getSecondaryMessage());

        }

    }

    /**
     * Removes a given view from its parent.
     * @param view The view to remove from its parent.
     */
    private void removeView(View view) {
        if (view.getParent() != null) {
            ((ViewManager) view.getParent()).removeView(view);
        }
    }


    /**
     * Returns the amount of cards to display.
     * @return The amount of displayed cards.
     */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * Gets the hasCode of the type of a Card at the given position in the dataset.
     * @param position The position of the card to check.
     * @return The Hashcode of the cards Type.
     */
    @Override
    public int getItemViewType(int position) {
        DbCard buffer = dataSet.get(position);

        return buffer.getType().hashCode();

    }

    /**
     * Is called when an item at a given position is dismissed. Updates the DashboardManager and
     * shows a SnackBar.
     * @param position The position of the dismissed item.
     */
    public void onItemDismiss(int position) {
        DashboardManager.getInstance(fragmentHandle.getContext()).dismissCard(dataSet.get(position));
        notifyItemRemoved(position);
        Snackbar.make(Objects.requireNonNull(fragmentHandle.getView()), R.string.budget_delete_message, Snackbar.LENGTH_LONG).show();

    }
}