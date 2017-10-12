package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.components.TagInput;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.models.Tag;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.combos.ComboDay;
import com.cynerds.cyburger.models.foodmenu.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageCombosActivity extends BaseActivity {

    private final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    private final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelperItems;

    public ManageCombosActivity() {
        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Combo.class);
        firebaseRealtimeDatabaseHelperItems = new FirebaseRealtimeDatabaseHelper(Item.class);

    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_combos);
        setActionBarTitle(getString(R.string.menu_manage_combos));

        setUIEvents();

    }

    private void setUIEvents() {
        Spinner comboDayCbx = findViewById(R.id.comboDayCbx);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                getNames(ComboDay.class));

        comboDayCbx.setAdapter(arrayAdapter);



        FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener = new FirebaseRealtimeDatabaseHelper.DataChangeListener() {
            @Override
            public void onDataChanged(Object item) {


                if (firebaseRealtimeDatabaseHelperItems.get().size() > 0) {


                   updateTags();
                }


            }
        };

        firebaseRealtimeDatabaseHelperItems.setDataChangeListener(dataChangeListener);
    }

    private void updateTags() {
        List<Tag> tagList = new ArrayList<>();
        List<Item> items = getItems();

        for (Item item :
                items) {
            Tag tag = new Tag();
            tag.setDescription(item.getDescription());
            tag.setObject(item);
            tagList.add(tag);
        }

        TagInput tagInput = findViewById(R.id.itemsTagInput);
        tagInput.setFilterableList(tagList);
    }

    List<Item> getItems() {

        List<Item> items = firebaseRealtimeDatabaseHelperItems.get();
        return items;

    }




}
