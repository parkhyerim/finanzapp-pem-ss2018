package com.lmu.pem.finanzapp.views.budgets;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmu.pem.finanzapp.BudgetAddActivity;
import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.BudgetAdapter;
import com.lmu.pem.finanzapp.model.budgets.BudgetEvent;
import com.lmu.pem.finanzapp.model.budgets.BudgetEventListener;
import com.lmu.pem.finanzapp.model.budgets.BudgetManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class BudgetFragment extends Fragment implements BudgetEventListener {

    RecyclerView recyclerView;
    BudgetAdapter adapter;
    RecyclerView.LayoutManager manager;

    FloatingActionButton fab;

    private View aboutView;

    public static final int REQUEST_ADD_BUDGET = 0;
    public static final int REQUEST_EDIT_BUDGET = 1;

    public BudgetFragment() {
        BudgetManager.getInstance().addListener(this);
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        aboutView = inflater.inflate(R.layout.budget_fragment, container, false);

        recyclerView = aboutView.findViewById(R.id.recyclerView);

        handleListEmptyText();

        fab = aboutView.findViewById(R.id.addBudget);
        fab.setOnClickListener((v) -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), BudgetAddActivity.class);
            startActivityForResult(intent, 0);
        });

        // use a linear layout manager
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        adapter = new BudgetAdapter(getContext(), this, BudgetManager.getInstance().getBudgets());
        recyclerView.setAdapter(adapter);


        ItemTouchHelper.Callback callback =
                new BudgetTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        // Inflate the layout for this fragment
        return aboutView;
    }

    private void handleListEmptyText() {
        TextView emptyListText = aboutView.findViewById(R.id.emptyListText);
        if(BudgetManager.getInstance().getBudgets().size()<1){
            if(emptyListText!=null) emptyListText.setVisibility(View.VISIBLE);
        }else{

            if(emptyListText!=null) emptyListText.setVisibility(View.GONE);
        }
    }

    public void onBudgetClicked(String id) {
        Intent i = new Intent(getContext(), BudgetAddActivity.class);
        i.putExtra("budgetToEdit", BudgetManager.getInstance().getById(id));

        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_BUDGET) adapter.notifyDataSetChanged();
        if (requestCode == REQUEST_EDIT_BUDGET) adapter.notifyDataSetChanged();

    }

    @Override
    public void handle(BudgetEvent event) {
        handleListEmptyText();
    }
}
