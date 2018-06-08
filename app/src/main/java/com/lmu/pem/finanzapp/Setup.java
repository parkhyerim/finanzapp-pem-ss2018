package com.lmu.pem.finanzapp;

import android.app.Application;
import com.google.firebase.database.FirebaseDatabase;

public class Setup extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
