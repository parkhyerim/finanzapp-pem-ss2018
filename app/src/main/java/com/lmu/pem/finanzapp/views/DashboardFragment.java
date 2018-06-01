package com.lmu.pem.finanzapp.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.CardAdapter;
import com.lmu.pem.finanzapp.model.dashboard.cards.BasicAmountCard;
import com.lmu.pem.finanzapp.model.dashboard.cards.DbCard;
import com.lmu.pem.finanzapp.model.dashboard.cards.WelcomeCard;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;

    ArrayList<DbCard> dataModels;
    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View aboutView = inflater.inflate(R.layout.dashboard_fragment, container, false);



        dataModels = new ArrayList<>();
        dataModels.add(createWelcomeCard());
        dataModels.add(createHighestExpensesCard(234f,"Test"));
        dataModels.add(createMemCard(932.56f, "November"));


        recyclerView = aboutView.findViewById(R.id.recyclerView);

        // use a linear layout manager
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        // specify an adapter (see also next example)

        adapter = new CardAdapter(dataModels);
        recyclerView.setAdapter(adapter);


        // Inflate the layout for this fragment
        return aboutView;
    }

    private BasicAmountCard createHighestExpensesCard(float amount, String category) {
        String title = getString(R.string.highestexpenses_title);
        String primaryMessage = getString(R.string.highestexpenses_mainText)+ " " + category +":";
        String btn1Text = getString(R.string.highestexpenses_btn1);

        return new BasicAmountCard(
                BasicAmountCard.CardType.HIGHESTEXPENSES,
                title,
                primaryMessage,
                amount,
                BasicAmountCard.AmountType.NEGATIVE,
                "",
                "",
                btn1Text,
                "");
    }

    private BasicAmountCard createMemCard(float amount, String month) {
        return new BasicAmountCard(
                BasicAmountCard.CardType.MOSTECONOMICALMONTH,
                getString(R.string.mem_title),
                getString(R.string.mem_mainText) + " " + month + ".",
                amount,
                BasicAmountCard.AmountType.NEGATIVE,
                getString(R.string.mem_amountDesc),
                getString(R.string.mem_secondaryText),
                getString(R.string.mem_btn1),
                ""
                );
    }

    private WelcomeCard createWelcomeCard() {
        return new WelcomeCard(
                getString(R.string.welcomecard_title),
                getString(R.string.welcomecard_text),
                getString(R.string.welcomecard_btn1),
                getString(R.string.welcomecard_btn2));
    }


}
