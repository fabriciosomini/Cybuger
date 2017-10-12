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
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.customer.Customer;
import com.cynerds.cyburger.models.items.Item;
import com.cynerds.cyburger.models.orders.Order;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener;
    List<DashboardCardViewItem> dashboardCardViewItems;
    DashboardCardAdapter adapter;
    private boolean isListCreated;
    private BaseActivity currentActivty;
    public OrdersFragment() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Order.class);
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

    public void getDashboardCardViewItems() {

        dashboardCardViewItems.clear();

        List<Order> orders = getOrders();

        for (Order order :
                orders) {


            Profile profile = CyburgerApplication.getProfile();
            Customer customer = order.getCustomer();
            String customerName = order.getCustomer().getCustomerName();
            DashboardCardViewItem dashboardCardViewItem = new DashboardCardViewItem();


            dashboardCardViewItem.setId(order.getId());

            if (customer.getLinkedProfileId().equals(profile.getUserId())) {
                dashboardCardViewItem.setTitle("Seu pedido");
                dashboardCardViewItem.setTitleColor(R.color.colorAccent);
            } else {
                dashboardCardViewItem.setTitle(customerName);
            }

            //Pega Items do pedido-----------
            String orderedItemsString = "";


            for (Combo combo :
                    order.getOrderedCombos()) {


                orderedItemsString += combo.getComboName() + "\n";
            }

            for (Item item :
                    order.getOrderedItems()) {


                orderedItemsString += item.getDescription() + "\n";
            }


            //------------------------------
            dashboardCardViewItem.setContent(orderedItemsString);


            dashboardCardViewItems.add(dashboardCardViewItem);


        }

    }

    List<Order> getOrders() {

        List<Order> orders = firebaseRealtimeDatabaseHelper.get();
        return orders;

    }


}
