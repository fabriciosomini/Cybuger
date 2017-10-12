package com.cynerds.cyburger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.adapters.DashboardCardAdapter;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.models.items.Item;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsMenuFragment extends Fragment {

    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener;
    List<DashboardCardViewItem> dashboardCardViewItems;
    DashboardCardAdapter adapter;
    private boolean isListCreated;
    private BaseActivity currentActivty;


    public ItemsMenuFragment() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Item.class);
        dashboardCardViewItems = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentActivty = (BaseActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_combos, container, false);

        if (!isListCreated) {
            isListCreated = true;
            createList(view);
        }

        updateList(view);
        setUIEvents(view);

        return view;
    }

    private void setUIEvents(View view) {


        EditText searchBoxCombosTxt = view.findViewById(R.id.searchBoxCombosTxt);


        searchBoxCombosTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void createList(final View view) {


        FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener = new FirebaseRealtimeDatabaseHelper.DataChangeListener() {
            @Override
            public void onDataChanged(Object item) {



                    updateList(view);


            }
        };

        firebaseRealtimeDatabaseHelper.setDataChangeListener(dataChangeListener);
    }

    private void updateList(View view) {

        Toast.makeText(currentActivty, "UpdateList " + this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();
        final ListView listview = view.findViewById(android.R.id.list);
        getDashboardCardViewItems();

        if (adapter == null) {
            adapter =
                    new DashboardCardAdapter(getContext(),
                            R.layout.dashboard_card_view, dashboardCardViewItems);


            listview.setAdapter(adapter);
        } else {

            if (listview.getAdapter() == null) {

                listview.setAdapter(adapter);
            }

            currentActivty.runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }


    List<Item> getItems() {

        List<Item> items = firebaseRealtimeDatabaseHelper.get();
        return items;

    }

    public void getDashboardCardViewItems() {

        dashboardCardViewItems.clear();

        List<Item> items = getItems();

        for (final Item item :
                items) {

            DashboardCardViewItem dashboardCardViewItem = new DashboardCardViewItem();
            dashboardCardViewItem.setId(item.getId());
            dashboardCardViewItem.setTitle(item.getDescription());
            dashboardCardViewItem.setActionIconId(R.drawable.ic_action_add);
            dashboardCardViewItem.setContent(
                    "Ingredientes: " + item.getIngredients()
                    + "\nUnidade de medida: "
                    + item.getSize()
                            + "\nValor: " + item.getPrice());


            dashboardCardViewItem.setOnManageClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogAction dialogAction = new DialogAction();
                    dialogAction.setPositiveAction(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Item adicionado ao carrinho", Toast.LENGTH_SHORT).show();

                            BaseActivity baseActivity = ((BaseActivity) getActivity());
                            Badge badge = baseActivity.getBadge();
                            baseActivity.getOrder().getOrderedItems().add(item);
                            badge.setBadgeCount(badge.getBadgeCount() + 1);


                        }
                    });
                    DialogManager dialogManager = new DialogManager(getContext(), DialogManager.DialogType.YES_NO, dialogAction);
                    dialogManager.showDialog("", "Deseja adicionar esse item?");


                }
            });

            dashboardCardViewItem.setOnCardViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogManager dialogManager = new DialogManager(getContext());
                    dialogManager.setContentView(R.layout.dialog_preview_item);
                    dialogManager.showDialog(item.getDescription(), "");
                }
            });


            dashboardCardViewItems.add(dashboardCardViewItem);


        }

    }


}
