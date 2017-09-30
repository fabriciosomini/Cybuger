package com.cynerds.cyburger.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fabri on 15/07/2017.
 */

public class Preferences {
    private Activity context;
    private CryptographyManager cryptographyManager;

    public Preferences(Activity context) {
        this.context = context;
        cryptographyManager = new CryptographyManager(context);

    }

    public boolean setPreferenceValue(String preferenceKey, String preferenceValue) {
        try {
            preferenceKey = cryptographyManager.encrypt(preferenceKey);
            preferenceValue = cryptographyManager.encrypt(preferenceValue);
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
            String encPreferenceKey = cryptographyManager.encrypt(preferenceKey);
            String encValue = sharedPreferences.getString(encPreferenceKey, "");
            return cryptographyManager.decrypt(encValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean removePreference(String preferenceKey) {

        try {
            preferenceKey = cryptographyManager.encrypt(preferenceKey);

            SharedPreferences sharedPreferences = context.getPreferences(Context.MODE_PRIVATE);
            return sharedPreferences.edit().remove(preferenceKey).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
