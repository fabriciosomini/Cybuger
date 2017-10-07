package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;

public class ManageItemsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);

        EditText searchItemBoxTxt = (EditText) findViewById(R.id.searchItemBoxTxt);
        searchItemBoxTxt.setFocusableInTouchMode(true);

        Button addNewItemBtn = (Button) findViewById(R.id.addNewItemBtn);
        addNewItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogAction dialogAction = new DialogAction();
                dialogAction.setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(ManageItemsActivity.this, "SALVAR", Toast.LENGTH_SHORT).show();
                    }
                });

                dialogAction.setNeutralAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ManageItemsActivity.this, "CANCEL", Toast.LENGTH_SHORT).show();
                    }
                });

                //ActivityManager.startActivity(ManageItemsActivity.this, ItemDialogActivity.class);
                DialogManager dialogManager = new DialogManager(ManageItemsActivity.this,
                        DialogManager.DialogType.SAVE_CANCEL,
                        dialogAction);
                dialogManager.setContentView(R.layout.dialog_add_item);
                dialogManager.showDialog(getString(R.string.manage_items_add_item), "");

            }
        });

    }
}
