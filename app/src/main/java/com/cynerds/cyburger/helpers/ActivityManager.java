package com.cynerds.cyburger.helpers;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by fabri on 15/07/2017.
 */

public abstract class ActivityManager<T> {



    public static void startActivity(Activity activity, Class tClass) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
    }

    public static void startActivity(Activity activity, Class tClass, Object extra) {
        Intent intent = new Intent(activity, tClass);
        intent.putExtra(extra.getClass().getSimpleName(), GsonHelper.ToGson(extra));
        activity.startActivity(intent);
    }

    public static void startActivityKillingThis(Activity activity, Class tClass) {
        Intent intent = new Intent(activity, tClass);
        activity.startActivity(intent);
        activity.finish();

    }
}
