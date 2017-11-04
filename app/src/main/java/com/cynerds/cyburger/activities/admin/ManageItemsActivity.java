package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.adapters.SpinnerArrayAdapter;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.models.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ManageItemsActivity extends BaseActivity {


    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;


    public ManageItemsActivity() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(this, Item.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);
        setActionBarTitle(getString(R.string.menu_manage_items));


        setUIEvents();

    }

    private void setUIEvents() {


        SpinnerArrayAdapter spinnerArrayAdapter =
                new SpinnerArrayAdapter(getApplicationContext(),
                        R.layout.component_dropdown_item,
                        getMeasureUnitItems());


        final Spinner spinner = findViewById(R.id.itemMeasureUnitCbx);
        spinner.setAdapter(spinnerArrayAdapter);

        final EditText itemDescriptionTxt = findViewById(R.id.itemDescriptionTxt);
        final EditText itemPriceTxt = findViewById(R.id.itemPriceTxt);
        final EditText itemIngredientsTxt = findViewById(R.id.itemIngredientsTxt);
        final EditText itemBonusPointTxt = findViewById(R.id.itemBonusPointTxt);
        final Button saveItemBtn = findViewById(R.id.saveItemBtn);
        final TextView deleteItemLink = findViewById(R.id.deleteItemLink);
        final Item loadedItem = (Item) getExtra(Item.class);

        itemDescriptionTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());

        if (loadedItem != null) {

            itemDescriptionTxt.setText(loadedItem.getDescription());
            itemPriceTxt.setText(String.valueOf(loadedItem.getPrice()));
            itemIngredientsTxt.setText(loadedItem.getIngredients());
            itemBonusPointTxt.setText(String.valueOf(loadedItem.getBonusPoints()));

        }

        saveItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FieldValidationHelper.isEditTextValidated(itemDescriptionTxt) &&
                        FieldValidationHelper.isEditTextValidated(itemIngredientsTxt) &&
                        FieldValidationHelper.isEditTextValidated(itemBonusPointTxt) &&
                        FieldValidationHelper.isEditTextValidated(itemPriceTxt)) {

                    String itemDescription = itemDescriptionTxt.getText().toString().trim();
                    Float itemPrice = Float.valueOf(itemPriceTxt.getText().toString().trim());
                    String itemIngredients = itemIngredientsTxt.getText().toString().trim();
                    String size = spinner.getSelectedItem().toString();
                    int bonusPoint = Integer.valueOf(itemBonusPointTxt.getText().toString().trim());

                    Item item = loadedItem == null ? new Item() : loadedItem;
                    item.setDescription(itemDescription);
                    item.setPrice(itemPrice);
                    item.setIngredients(itemIngredients);
                    item.setSize(size);
                    item.setBonusPoints(bonusPoint);


                    if (loadedItem == null) {

                        firebaseRealtimeDatabaseHelper.insert(item);
                    } else {

                        firebaseRealtimeDatabaseHelper.update(item);
                    }

                    finish();

                }


            }
        });

        if(CyburgerApplication.isAdmin()) {
            if (loadedItem != null) {
                deleteItemLink.setVisibility(View.VISIBLE);
                deleteItemLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogAction deleteComboAction = new DialogAction();
                        deleteComboAction.setPositiveAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                firebaseRealtimeDatabaseHelper.delete(loadedItem);

                                LogHelper.error("Item removido");
                            }
                        });
                        DialogManager confirmDeleteDialog = new DialogManager(ManageItemsActivity.this,
                                DialogManager.DialogType.YES_NO);
                        confirmDeleteDialog.setAction(deleteComboAction);
                        confirmDeleteDialog.showDialog("Remover combo", "Tem certeza que deseja remover esse item?");
                    }
                });


            }else{

                deleteItemLink.setVisibility(View.GONE);
            }
        }else{

            deleteItemLink.setVisibility(View.GONE);
        }


    }


    public List<String> getMeasureUnitItems() {

        List<String> measureUnitItems = new ArrayList<>();

        measureUnitItems.add("Unidade");
        measureUnitItems.add("Fatia");
        measureUnitItems.add("Porção grande");
        measureUnitItems.add("Porção média");
        measureUnitItems.add("Porção pequena");

        measureUnitItems.add("Dose");
        measureUnitItems.add("250ml");
        measureUnitItems.add("600ml");
        measureUnitItems.add("1L");
        measureUnitItems.add("2L");
        measureUnitItems.add("2.5L");
        measureUnitItems.add("3L");


        return measureUnitItems;
    }
}
