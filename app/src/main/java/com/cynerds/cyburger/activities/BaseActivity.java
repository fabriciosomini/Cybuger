package com.cynerds.cyburger.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.GsonHelper;

import java.util.ArrayList;

/**
 * Created by fabri on 08/07/2017.
 */

public class BaseActivity extends AppCompatActivity {


    private ArrayList dirty = new ArrayList<>();
    private Runnable onPermissionGrantedAction;
    private Runnable onPermissionDeniedAction;
    private boolean ignoreUnsavedChanges;
    private String unsavedChangesMessage;

    private Bundle savedInstanceState;
    private View.OnClickListener onSaveListener;
    private View.OnClickListener onCancelListener;
    private TextView actionBarTitle;
    private boolean finishApp;


    public BaseActivity() {


    }




    public View getView() {


        return findViewById(android.R.id.content);
    }

    public View.OnClickListener getOnSaveListener() {
        return onSaveListener;
    }

    public void setOnSaveListener(View.OnClickListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public View.OnClickListener getOnCancelListener() {
        return onCancelListener;
    }

    public void setOnCancelListener(View.OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    protected void setOnPermissionGrantedAction(Runnable onPermissionGrantedAction) {
        this.onPermissionGrantedAction = onPermissionGrantedAction;
    }

    protected void setOnPermissionDeniedAction(Runnable onPermissionDeniedAction) {
        this.onPermissionDeniedAction = onPermissionDeniedAction;
    }

    protected boolean isIgnoreUnsavedChanges() {
        return ignoreUnsavedChanges;
    }

    protected void setIgnoreUnsavedChanges(boolean ignoreUnsavedChanges) {
        this.ignoreUnsavedChanges = ignoreUnsavedChanges;
    }

    protected String getUnsavedChangesMessage() {
        return unsavedChangesMessage;
    }

    protected void setUnsavedChangesMessage(String unsavedChangesMessage) {
        this.unsavedChangesMessage = unsavedChangesMessage;
    }

    @Override
    public void onBackPressed() {

        closeActivity();

    }

    protected boolean isWorkspaceDirty() {

        return dirty.size() > 0;
    }

    protected void setActionBarTitle(String title) {
        actionBarTitle.setVisibility(View.VISIBLE);
        actionBarTitle.setText(title);

    }

    protected void closeActivity() {

        if (isWorkspaceDirty() && !ignoreUnsavedChanges) {

            DialogAction dialogAction = new DialogAction();
            final DialogManager dialogManager = new DialogManager(this,
                    DialogManager.DialogType.YES_NO);
            dialogManager.setAction(dialogAction);

            dialogAction.setPositiveAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            dialogAction.setNegativeAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogManager.closeDialog();
                }
            });

            dialogManager.showDialog("Você possui alterações. Deseja sair?");

        } else {
            finish();
        }


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
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                    if (onPermissionGrantedAction != null) {

                        onPermissionGrantedAction.run();
                    }
                } else {


                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
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

    public void finishApplication(){
        this.finishApp = true;
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(finishApp){
            CyburgerApplication.unsubscribeToUserTopic();
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

}

