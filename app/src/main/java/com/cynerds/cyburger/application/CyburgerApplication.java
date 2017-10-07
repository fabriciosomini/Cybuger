package com.cynerds.cyburger.application;

import android.app.Application;

import com.cynerds.cyburger.models.profile.Profile;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by fabri on 30/09/2017.
 */

public class CyburgerApplication extends Application {

    private Profile profile;

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }


}
