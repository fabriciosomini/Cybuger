package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.adapters.SpinnerArrayAdapter;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.models.items.Item;

import java.util.ArrayList;
import java.util.List;

public class ManageItemsActivity extends BaseActivity {


    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;


    public ManageItemsActivity() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Item.class);

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

                    Item item = new Item();
                    item.setDescription(itemDescription);
                    item.setPrice(itemPrice);
                    item.setIngredients(itemIngredients);
                    item.setSize(size);
                    item.setBonusPoints(bonusPoint);

                    firebaseRealtimeDatabaseHelper.insert(item);
                    finish();

                }



            }
        });


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
