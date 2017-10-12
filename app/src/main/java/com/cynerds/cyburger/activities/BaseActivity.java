package com.cynerds.cyburger.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.activities.admin.ManageItemsActivity;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.helpers.ActivityManager;
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
    private Badge badge;
    private View hamburgerMenu;

    public Badge getBadge() {
        return badge;
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
        actionBarTitle.setText(title);

    }


    protected void closeActivity() {

        if (isWorkspaceDirty() && !ignoreUnsavedChanges) {

            DialogAction dialogAction = new DialogAction();
            final DialogManager dialogManager = new DialogManager(this,
                    DialogManager.DialogType.YES_NO,
                    dialogAction);

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

        actionBarTitle = findViewById(R.id.action_bar_title);


        getView().setFocusableInTouchMode(true);

        badge = findViewById(R.id.badge);
        hamburgerMenu = findViewById(R.id.hamburgerMenu);

        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(BaseActivity.this, hamburgerMenu);
                popupMenu.inflate(R.menu.menu_overflow);

                popupMenu.getMenu().findItem(R.id.action_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(BaseActivity.this, ProfileActivity.class);
                        return false;
                    }
                });


                popupMenu.getMenu().findItem(R.id.action_manage_items).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(BaseActivity.this, ManageItemsActivity.class);
                        return false;
                    }
                });

                popupMenu.getMenu().findItem(R.id.action_manage_combos).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(BaseActivity.this, ManageCombosActivity.class);
                        return false;
                    }
                });

                popupMenu.show();

            }
        });
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

    protected void showBadge(boolean showBadge) {

        if (showBadge) {
            badge.setVisibility(View.VISIBLE);

        } else {
            badge.setVisibility(View.INVISIBLE);

        }
    }

    protected void showActionBarMenu(boolean showMenu) {

        if (showMenu) {


            if (showMenu) {
                hamburgerMenu.setVisibility(View.VISIBLE);

            } else {
                hamburgerMenu.setVisibility(View.INVISIBLE);

            }
        }
    }


}

