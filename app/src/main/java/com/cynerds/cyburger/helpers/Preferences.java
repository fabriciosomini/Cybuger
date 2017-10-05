package com.cynerds.cyburger.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fabri on 15/07/2017.
 */

public class Preferences {
    private Activity context;


    public Preferences(Activity context) {
        this.context = context;


    }

    public boolean setPreferenceValue(String preferenceKey, String preferenceValue) {
        try {

            SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(preferenceKey, preferenceValue);

            return editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public String getPreferenceValue(String preferenceKey) {
        SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);


        try {
            String encPreferenceKey = preferenceKey;
            String encValue = sharedPreferences.getString(encPreferenceKey, "");
            return encValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean removePreference(String preferenceKey) {

        try {


            SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);
            return sharedPreferences.edit().remove(preferenceKey).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
