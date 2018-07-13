package com.lmu.pem.finanzapp.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.controller.BudgetAdapter;
import com.lmu.pem.finanzapp.model.budgets.Budget;
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

    FloatingActionButton fab;

    public static final int REQUEST_ADD_BUDGET = 0;
    public static final int REQUEST_EDIT_BUDGET = 1;

    public BudgetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View aboutView = inflater.inflate(R.layout.budget_fragment, container, false);

        recyclerView = aboutView.findViewById(R.id.recyclerView);

        fab = aboutView.findViewById(R.id.addBudget);

        fab.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), AddBudgetActivity.class);
            startActivityForResult(intent, 0);
        });

        // use a linear layout manager
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new BudgetAdapter(getContext(), this, BudgetManager.getInstance().getBudgets());
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return aboutView;
    }

    public void onBudgetClicked(String id) {
        Intent i = new Intent(getContext(), AddBudgetActivity.class);
        i.putExtra("budgetToEdit", BudgetManager.getInstance().getById(id));

        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_BUDGET) adapter.notifyDataSetChanged();
        if (requestCode == REQUEST_EDIT_BUDGET) adapter.notifyDataSetChanged();

    }
}
