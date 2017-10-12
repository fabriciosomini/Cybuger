package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.adapters.DashboardCardAdapter;
import com.cynerds.cyburger.components.TagInput;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.models.Tag;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.combos.ComboDay;
import com.cynerds.cyburger.models.combos.MonthlyCombo;
import com.cynerds.cyburger.models.foodmenu.Item;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ManageCombosActivity extends BaseActivity {

    private final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    private final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelperItems;

    public ManageCombosActivity() {
        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(MonthlyCombo.class);
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
        Spinner comboDayCbx = (Spinner) findViewById(R.id.comboDayCbx);
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

        TagInput tagInput = (TagInput)findViewById(R.id.itemsTagInput);
        tagInput.setFilterableList(tagList);
    }

    List<Item> getItems() {

        List<Item> items = firebaseRealtimeDatabaseHelperItems.get();
        return items;

    }

    private void createCombos() {
        List<DashboardCardViewItem> dashboardCardViewItems = new ArrayList<>();

        Item item = new Item();
        Item item2 = new Item();
        List<Item> comboItemsList = new ArrayList<>();
        Combo combo = new Combo();
        List<Combo> combosList = new ArrayList<>();
        MonthlyCombo monthlyCombo = new MonthlyCombo();

        //1
        item.setDescription("Coca-cola");
        item.setPrice(3.50f);
        item.setSize("600ml");
        item.setId(UUID.randomUUID().toString());

        item2.setDescription("Hamburger");
        item2.setPrice(10.50f);
        item2.setSize("1 unidade");
        item2.setId(UUID.randomUUID().toString());

        //2
        comboItemsList.add(item);
        comboItemsList.add(item2);


        //3
        combo.setComboItems(comboItemsList);
        combo.setComboName("King Combo: Coca + Hamburger");
        combo.setId(UUID.randomUUID().toString());

        //4
        combosList.add(combo);


        monthlyCombo.setCombos(combosList);
        monthlyCombo.setId(UUID.randomUUID().toString());


        firebaseRealtimeDatabaseHelper.insert(monthlyCombo);


    }


}
