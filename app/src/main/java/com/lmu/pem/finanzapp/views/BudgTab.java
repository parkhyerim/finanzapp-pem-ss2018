package com.lmu.pem.finanzapp.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lmu.pem.finanzapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class BudgTab extends Fragment {
    public BudgTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View aboutView = inflater.inflate(R.layout.fragment_tab4, container, false);

        // Inflate the layout for this fragment
        return aboutView;
    }
}
