package com.cynerds.cyburger.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.helpers.DateHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.GsonHelper;
import com.cynerds.cyburger.models.sync.Sync;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by fabri on 08/07/2017.
 */

public class BaseActivity extends AppCompatActivity {


    private ArrayList dirty = new ArrayList<>();
    private ActivityCompat.OnRequestPermissionsResultCallback onRequestPermissionsResultCallback;
    private Bundle savedInstanceState;
    private TextView actionBarTitle;
    private boolean finishApp;

    public BaseActivity() {


    }

    public void setOnRequestPermissionsResultCallback(ActivityCompat.OnRequestPermissionsResultCallback
                                                              onRequestPermissionsResultCallback) {
        this.onRequestPermissionsResultCallback = onRequestPermissionsResultCallback;
    }

    public void updateLastSyncDate(Date lastDate) {
        if (CyburgerApplication.isAdmin()) {
            FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper(Sync.class);
            Date currentDate = new DateHelper(this).getCurrentDate();
            Sync sync = CyburgerApplication.getSync();
            sync.setSynced(true);
            sync.setLastSyncedDate(currentDate);

            firebaseDatabaseHelper.update(sync);
        }
    }

    public View getView() {


        return findViewById(android.R.id.content);
    }


    protected boolean isWorkspaceDirty() {

        return dirty.size() > 0;
    }

    protected void setActionBarTitle(String title) {
        actionBarTitle.setVisibility(View.VISIBLE);
        actionBarTitle.setText(title);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.savedInstanceState = savedInstanceState;

        setUIEvents();


    }

    private void setUIEvents() {


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); //bellow setSupportActionBar(toolbar);
        actionBar.setCustomView(R.layout.base_titlebar);
        actionBarTitle = findViewById(R.id.actionBarTextView);
        getView().setFocusableInTouchMode(true);
        getView().setFitsSystemWindows(true);
        setStatusBarTranslucent(true);

    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (onRequestPermissionsResultCallback != null) {

            onRequestPermissionsResultCallback.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


    public Object getExtra(Class type) {
        Object newObject;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                newObject = null;
            } else {
                newObject = extras.getString(type.getSimpleName());
            }
        } else {
            newObject = savedInstanceState.getSerializable(type.getSimpleName());
        }

        newObject = GsonHelper.ToObject(type, (String) newObject);
        return newObject;
    }

    public void finishApplication() {
        this.finishApp = true;
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (finishApp) {
            CyburgerApplication.unsubscribeToUserTopic();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

}

