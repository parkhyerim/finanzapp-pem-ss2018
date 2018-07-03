package com.lmu.pem.finanzapp.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.BudgetAdapter;
import com.lmu.pem.finanzapp.model.budgets.BudgetManager;

import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class BudgetFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;

    public BudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View aboutView = inflater.inflate(R.layout.budget_fragment, container, false);

        recyclerView = aboutView.findViewById(R.id.recyclerView);

        // use a linear layout manager
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new BudgetAdapter(BudgetManager.getInstance().getBudgets());
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return aboutView;
    }
}
