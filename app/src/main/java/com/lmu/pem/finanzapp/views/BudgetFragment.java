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
import com.lmu.pem.finanzapp.data.categories.Category;
import com.lmu.pem.finanzapp.data.categories.CategoryManager;
import com.lmu.pem.finanzapp.data.categories.DefaultCategory;
import com.lmu.pem.finanzapp.model.Budget;

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

        ArrayList<Budget> budgets = new ArrayList<>();

        budgets.add(new Budget(DefaultCategory.FOOD, new Date(2018 - 1900, 0, 1), new Date(2018 - 1900, 11, 31), 110f, 14.5f));
        budgets.add(new Budget(DefaultCategory.FOOD, new Date(2018  - 1900, 4, 1), new Date(2018  - 1900, 6, 1), 24, 14.5f));
        budgets.add(new Budget(DefaultCategory.FOOD, new Date(2018  - 1900, 1, 1), new Date(2018  - 1900, 3, 1), 13, 14.5f));

        adapter = new BudgetAdapter(budgets);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return aboutView;
    }
}
