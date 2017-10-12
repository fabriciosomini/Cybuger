package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
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


        ArrayAdapter<String> spinnerArrayAdapter =
                new ArrayAdapter<String>(getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        getMeasureUnitItems());

        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = findViewById(R.id.itemMeasureUnitCbx);
        spinner.setAdapter(spinnerArrayAdapter);

        final EditText itemDescription = findViewById(R.id.itemDescription);
        final EditText itemPrice = findViewById(R.id.itemPrice);
        final EditText itemIngredients = findViewById(R.id.itemIngredients);
        final Button saveItemBtn = findViewById(R.id.saveItemBtn);

        saveItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Item item = new Item();
                item.setDescription(itemDescription.getText().toString());
                item.setPrice(Float.parseFloat(itemPrice.getText().toString()));
                item.setIngredients(itemIngredients.getText().toString());
                item.setSize(spinner.getSelectedItem().toString());

                firebaseRealtimeDatabaseHelper.insert(item);
                finish();
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
