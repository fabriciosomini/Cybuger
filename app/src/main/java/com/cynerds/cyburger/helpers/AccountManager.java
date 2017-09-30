package com.cynerds.cyburger.helpers;

import android.app.Activity;

/**
 * Created by fabri on 15/07/2017.
 */

public class AccountManager {

    private Preferences preferences;
    private Activity activity;
    private String aName;
    private String pName;

    public AccountManager(Activity activity, String accountType) throws Exception {

        this.activity = activity;
        CryptographyManager cryptographyManager = new CryptographyManager(activity);
        preferences = new Preferences(activity);

        aName = cryptographyManager.encrypt(accountType);
        pName = cryptographyManager.encrypt(activity.getClass().getSimpleName());

    }


    public Account getAccount() {


        String v = preferences.getPreferenceValue(aName);//firebaseAccount

        if (v != null) {

            if (!v.isEmpty()) {

                Account account = new Account();
                account.setCredentials(v);

                return account;
            }
        }
        return null;
    }

    public String getEmail(Account account) throws Exception {

        if (account == null) {

            return "";
        }

        String dec = new CryptographyManager(activity).decrypt(account.getCredentials());

        return dec.substring(0, dec.indexOf(aName));

    }

    public String getPassword(Account account) throws Exception {
        if (account == null) {

            return "";
        }

        String dec = new CryptographyManager(activity).decrypt(account.getCredentials());

        return dec.substring(dec.indexOf(aName) + aName.length(), dec.length());

    }

    public boolean setAccount(String email, String password) throws Exception {
        String v = email + aName + password;
        CryptographyManager cryptographyManager = new CryptographyManager(activity);

        return preferences.setPreferenceValue(aName, cryptographyManager.encrypt(v));

    }

    public boolean removeAccount(String email, String password) {

        return preferences.removePreference(aName);
    }
}
