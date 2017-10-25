package com.cynerds.cyburger.helpers;

import android.util.Log;

import com.cynerds.cyburger.BuildConfig;

public class LogHelper {

    public static void show(String message) {

        if (BuildConfig.DEBUG) {
            Log.e("UI Log",message);
        }


    }

}
