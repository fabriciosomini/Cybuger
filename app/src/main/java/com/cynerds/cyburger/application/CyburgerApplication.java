package com.cynerds.cyburger.application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by fabri on 30/09/2017.
 */

public class CyburgerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
