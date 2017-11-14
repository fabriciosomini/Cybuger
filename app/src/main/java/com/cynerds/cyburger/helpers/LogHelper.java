package com.cynerds.cyburger.helpers;

import android.util.Log;

import com.cynerds.cyburger.BuildConfig;

public class LogHelper {

    public static void log(String message) {

        if(message == null){
            message = "NO_MESSAGE";
        }

        if (BuildConfig.DEBUG) {
            Log.e("UI Log",message);
        }


    }

}
