package com.cynerds.cyburger.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.components.base.BaseComponent;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.GsonHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 08/07/2017.
 */

public class WorkspaceActivity extends AppCompatActivity {

    private ArrayList dirty = new ArrayList<>();
    private Runnable onPermissionGrantedAction;
    private Runnable onPermissionDeniedAction;
    private boolean ignoreUnsavedChanges;
    private String unsavedChangesMessage;
    private List<BaseComponent> baseComponentList;
    private Bundle savedInstanceState;
    private View.OnClickListener onSaveListener;
    private View.OnClickListener onCancelListener;

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


    protected boolean isComponentDirty(BaseComponent baseComponent) {

        boolean isDirty = dirty.contains(baseComponent.getHashId());
        return isDirty;
    }


    private void findBaseComponents(View view) {

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = ((ViewGroup) view);

            for (int i = 0; i < viewGroup.getChildCount(); i++) {

                View child = viewGroup.getChildAt(i);
                if (child.getClass().getSuperclass() == BaseComponent.class) {
                    BaseComponent component = (BaseComponent) child;
                    if (!baseComponentList.contains(component)) {
                        baseComponentList.add(component);
                    }

                } else {
                    //Let's dig more
                    if (child instanceof ViewGroup) {
                        findBaseComponents(child);
                    }


                }
            }
        }


    }

    protected boolean validateFields() {

        boolean isWorkspaceValid = true;


        findBaseComponents(findViewById(android.R.id.content).getRootView());//((ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0));

        for (int i = 0; i < baseComponentList.size(); i++) {

            BaseComponent component = baseComponentList.get(i);
            if (!isComponentDirty(component) && component.isRequired()) {
                component.setValidationMessage(getString(R.string.general_label_requiredfield));
                component.showValidationMessage();
                isWorkspaceValid = false;
            } else {
                component.hideValidationMessage();
            }
        }

        return isWorkspaceValid;
    }

    protected void closeActivity() {

        if (isWorkspaceDirty() && !ignoreUnsavedChanges) {

            DialogAction dialogAction = new DialogAction();
            final DialogManager dialogManager = new DialogManager();

            dialogAction.setPositiveAction(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });

            dialogAction.setNegativeAction(new Runnable() {
                @Override
                public void run() {
                    dialogManager.closeDialog();
                }
            });

            dialogManager.showDialog(this, "Você possui alterações. Deseja sair?"
                    , DialogManager.DialogType.YES_NO, dialogAction);

        } else {
            finish();
        }


    }

    public void removeDirty(BaseComponent baseComponent) {
        String hashcode = String.valueOf(baseComponent.getHashId());
        if (dirty.contains(hashcode)) {
            dirty.remove(hashcode);
        }
    }

    public void addDirty(BaseComponent baseComponent) {

        if (!dirty.contains(baseComponent.getHashId())) {
            dirty.add(baseComponent.getHashId());

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        baseComponentList = new ArrayList<>();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "GRANTED MF", Toast.LENGTH_SHORT).show();
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
            newObject = (String) savedInstanceState.getSerializable(type.getSimpleName());
        }

        newObject = GsonHelper.ToObject(type, (String) newObject);
        return newObject;
    }
}

