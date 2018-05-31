package com.lmu.pem.finanzapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

public class SearchableActivity extends Activity {

    //TODO

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO - setContentView(R.layout.search);

        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);

            Log.println(Log.INFO,"Search","Query entered: "+query);
            //TODO - process query
        }
    }
}
