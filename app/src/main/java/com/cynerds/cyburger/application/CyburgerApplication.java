package com.cynerds.cyburger.application;

import android.app.Application;

import com.cynerds.cyburger.models.profile.Profile;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by fabri on 30/09/2017.
 */

public class CyburgerApplication extends Application {

    private static Profile profile;

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile profile) {
        CyburgerApplication.profile = profile;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }


}
