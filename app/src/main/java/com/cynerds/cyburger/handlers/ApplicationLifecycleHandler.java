package com.cynerds.cyburger.handlers;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.cynerds.cyburger.helpers.LogHelper;

/**
 * Created by fabri on 25/10/2017.
 */

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

        LogHelper.log("Activity created!");
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}