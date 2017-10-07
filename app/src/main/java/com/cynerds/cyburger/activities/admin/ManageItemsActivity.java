package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.adapters.DashboardCardAdapter;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.models.foodmenu.Item;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.List;

public class ManageItemsActivity extends BaseActivity {


    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    List<DashboardCardViewItem> dashboardCardViewItems;
    DashboardCardAdapter adapter;

    public ManageItemsActivity() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Item.class);
        dashboardCardViewItems = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);


        EditText searchItemBoxTxt = (EditText) findViewById(R.id.searchItemBoxTxt);
        searchItemBoxTxt.setFocusableInTouchMode(true);

        Button addNewItemBtn = (Button) findViewById(R.id.addNewItemBtn);

        final DialogAction dialogAction = new DialogAction();
        final DialogManager dialogManager = new DialogManager(ManageItemsActivity.this,
                DialogManager.DialogType.SAVE_CANCEL,
                dialogAction);
        dialogManager.setContentView(R.layout.dialog_add_item);

        addNewItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialogAction.setPositiveAction(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {

                        View dialogContent = dialogManager.getContentView();

                        EditText itemDescription = (EditText) dialogContent.findViewById(R.id.itemDescription);
                        EditText itemPrice = (EditText) dialogContent.findViewById(R.id.itemPrice);
                        EditText itemIngredients = (EditText) dialogContent.findViewById(R.id.itemIngredients);
                        Spinner spinner = (Spinner) dialogContent.findViewById(R.id.itemMeasureUnitCbx);

                        Item item = new Item();
                        item.setDescription(itemDescription.getText().toString());
                        item.setPrice(Float.parseFloat(itemPrice.getText().toString()));
                        item.setIngredients(itemIngredients.getText().toString());
                        item.setSize(spinner.getSelectedItem().toString());

                        firebaseRealtimeDatabaseHelper.insert(item);

                    }
                });

                dialogAction.setNeutralAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ManageItemsActivity.this, "CANCEL", Toast.LENGTH_SHORT).show();
                    }
                });


                dialogManager.showDialog(getString(R.string.manage_items_add_item), "");

                //Calling down here since the spinner has not content yet
                View dialogContent = dialogManager.getContentView();
                ArrayAdapter<String> spinnerArrayAdapter =
                        new ArrayAdapter<String>(getApplicationContext(),
                                android.R.layout.simple_spinner_item,
                                getMeasureUnitItems());

                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner spinner = (Spinner) dialogContent.findViewById(R.id.itemMeasureUnitCbx);
                spinner.setAdapter(spinnerArrayAdapter);


            }
        });


        createList();

    }

    private void createList() {


        final ListView listview = (ListView) findViewById(android.R.id.list);


        FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener = new FirebaseRealtimeDatabaseHelper.DataChangeListener() {
            @Override
            public void onDataChanged(Object item) {


                if (firebaseRealtimeDatabaseHelper.selectAll().size() > 0) {

                    getDashboardCardViewItems();

                    if (adapter == null) {
                        adapter =
                                new DashboardCardAdapter(getApplicationContext(),
                                        R.layout.dashboard_card_view, dashboardCardViewItems);


                        listview.setAdapter(adapter);
                    } else {


                        runOnUiThread(new Runnable() {
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }

                }


            }
        };

        firebaseRealtimeDatabaseHelper.setDataChangeListener(dataChangeListener);

    }

    List<Item> getItems() {

        List<Item> server = firebaseRealtimeDatabaseHelper.selectAll();
        return server;

    }

    public void getDashboardCardViewItems() {


        List<Item> monthlyCombos = getItems();

        boolean repeat = false;

        for (Item item :
                monthlyCombos) {

            DashboardCardViewItem dashboardCardViewItem = new DashboardCardViewItem();
            dashboardCardViewItem.setId(item.getId());
            dashboardCardViewItem.setTitle(item.getDescription());
            dashboardCardViewItem.setContent("Ingredientes: " + item.getIngredients()
                    + "\nUnidade de medida: "
                    + item.getSize()
                    + "\nPreço: " + item.getPrice());


            for (int i = 0; i < dashboardCardViewItems.size(); i++) {
                DashboardCardViewItem d = dashboardCardViewItems.get(i);
                if (item.getId().equals(d.getId())) {
                    repeat = true;
                    dashboardCardViewItems.set(i, dashboardCardViewItem);
                    break;
                } else {
                    repeat = false;
                }
            }

            if (repeat) {
                continue;
            }


            dashboardCardViewItems.add(dashboardCardViewItem);


        }

    }

    public List<String> getMeasureUnitItems() {

        List<String> measureUnitItems = new ArrayList<>();
        measureUnitItems.add("250ml");
        measureUnitItems.add("600ml");
        measureUnitItems.add("2L");
        measureUnitItems.add("2.5L");
        measureUnitItems.add("3L");
        measureUnitItems.add("Unidade");
        measureUnitItems.add("Fatia");

        return measureUnitItems;
    }
}
