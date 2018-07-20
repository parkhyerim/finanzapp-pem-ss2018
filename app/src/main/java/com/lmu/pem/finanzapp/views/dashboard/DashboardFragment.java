package com.lmu.pem.finanzapp.views.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.TransactionAddActivity;
import com.lmu.pem.finanzapp.controller.CardAdapter;
import com.lmu.pem.finanzapp.model.dashboard.DashboardEvent;
import com.lmu.pem.finanzapp.model.dashboard.DashboardEventListener;
import com.lmu.pem.finanzapp.model.transactions.Transaction;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;
import com.lmu.pem.finanzapp.views.TransactionFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements DashboardEventListener{

    RecyclerView recyclerView;
    CardAdapter adapter;
    RecyclerView.LayoutManager manager;

    DashboardManager dashboardManager;
    private TransactionManager transactionManager;
    private View aboutView;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        dashboardManager = DashboardManager.getInstance(this.getContext());
        dashboardManager.addListener(this);

        aboutView = inflater.inflate(R.layout.dashboard_fragment, container, false);

        transactionManager = TransactionManager.getInstance();
        handleListEmptyText();


        recyclerView = aboutView.findViewById(R.id.recyclerView);

        // use a linear layout manager
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        // specify an adapter (see also next example)

        adapter = new CardAdapter(dashboardManager.getDataSet(), this);
        recyclerView.setAdapter(adapter);
        dashboardManager.setAdapterListener(adapter);

        ItemTouchHelper.Callback callback =
                new DashboardTouchHelperCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);

        // Inflate the layout for this fragment
        return aboutView;
    }

    private void handleListEmptyText() {
        TextView emptyListText = aboutView.findViewById(R.id.emptyListText);
        if(dashboardManager.getDataSet().size()<1){
            if(emptyListText!=null) emptyListText.setVisibility(View.VISIBLE);
        }else{
            if(emptyListText!=null) emptyListText.setVisibility(View.GONE);
        }
    }

    @Override
    public void handle(DashboardEvent event) {
        handleListEmptyText();
    }

    public void startAddTransactionActivity() {
        Intent intent = new Intent(getContext(), TransactionAddActivity.class);
        startActivityForResult(intent, TransactionFragment.REQUEST_CODE_ADD_TRANSACTION);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==TransactionFragment.REQUEST_CODE_ADD_TRANSACTION && resultCode == Activity.RESULT_OK){
            int year = data.getIntExtra("year",0);
            int month = data.getIntExtra("month", 0);
            int day = data.getIntExtra("day",0);
            String account = data.getStringExtra("account");
            String account2 = data.getStringExtra("account2");
            String category = data.getStringExtra("category");
            String description = data.getStringExtra("description");
            double amount = data.getDoubleExtra("amount",0);

            Transaction transaction;
            transaction = new Transaction(year, month, day, account, account2, category, description, amount);

            transactionManager.addTransaction(transaction);
        }
    }
}
