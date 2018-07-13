package com.lmu.pem.finanzapp.views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmu.pem.finanzapp.R;
import com.lmu.pem.finanzapp.controller.CardAdapter;
import com.lmu.pem.finanzapp.model.dashboard.DashboardEvent;
import com.lmu.pem.finanzapp.model.dashboard.DashboardEventListener;
import com.lmu.pem.finanzapp.model.transactions.TransactionManager;
import com.lmu.pem.finanzapp.model.dashboard.DashboardManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment implements DashboardEventListener{

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager manager;

    DashboardManager dashboardManager;
    private TransactionManager transactionManager;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View aboutView = inflater.inflate(R.layout.dashboard_fragment, container, false);

        dashboardManager = DashboardManager.getInstance(this.getContext());
        dashboardManager.addListener(this);
        transactionManager = TransactionManager.getInstance();
        if(dashboardManager.getDataSet(transactionManager).size()<1){
            TextView emptyListText = aboutView.findViewById(R.id.emptyListText);
            emptyListText.setVisibility(View.VISIBLE);
        }


        recyclerView = aboutView.findViewById(R.id.recyclerView);

        // use a linear layout manager
        manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        // specify an adapter (see also next example)

        adapter = new CardAdapter(dashboardManager.getDataSet(transactionManager), getContext());
        recyclerView.setAdapter(adapter);


        // Inflate the layout for this fragment
        return aboutView;
    }

    @Override
    public void handle(DashboardEvent event) {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
