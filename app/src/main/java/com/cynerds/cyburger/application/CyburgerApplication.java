package com.cynerds.cyburger.application;

import android.app.Application;
import android.content.Context;

import com.cynerds.cyburger.handlers.ApplicationLifecycleHandler;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.roles.Role;
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

    public static boolean isAdmin() {

        return profile != null && profile.getRole() == Role.ADMIN;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        registerActivityLifecycleCallbacks(new ApplicationLifecycleHandler());
    }



}
