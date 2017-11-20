package com.cynerds.cyburger.fragments;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.adapters.CardAdapter;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.helpers.CardModelFilterHelper;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.models.combo.Combo;
import com.cynerds.cyburger.models.customer.Customer;
import com.cynerds.cyburger.models.item.Item;
import com.cynerds.cyburger.models.order.Order;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.view.CardModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    final FirebaseDatabaseHelper firebaseDatabaseHelper;
    private final Profile profile;
    OnDataChangeListener onDataChangeListener;
    List<CardModel> cardModels;
    CardAdapter adapter;
    private boolean isListCreated;
    private MainActivity currentActivty;
    private View view;
    private String filter = "";
    private ListView listView;
    private boolean eventSet;

    public OrdersFragment() {

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(getContext(), Order.class);
        cardModels = new ArrayList<>();

        profile = CyburgerApplication.getProfile();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        currentActivty = (MainActivity) context;
        LayoutInflater inflater = (LayoutInflater) currentActivty
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_orders, null);
        }

        if (listView == null) {

            listView = view.findViewById(android.R.id.list);
            listView.setEmptyView(view.findViewById(R.id.emptyOrdersListTxt));
        }


        if (!isListCreated) {
            isListCreated = true;
            setListDataListener(view);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!eventSet) {
            eventSet = true;
            setUIEvents(view);
        }

        return view;
    }

    private void setUIEvents(View view) {


        final EditText searchBoxOrdersTxt = view.findViewById(R.id.searchBoxOrdersTxt);

        searchBoxOrdersTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    searchBoxOrdersTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    searchBoxOrdersTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_close, 0);
                    searchBoxOrdersTxt.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Drawable drawable = searchBoxOrdersTxt.getCompoundDrawables()[2];

                            if (drawable != null) {
                                boolean clicked = event.getRawX() >=
                                        searchBoxOrdersTxt.getRight()
                                                - drawable.getBounds().width();
                                if (clicked) {
                                    searchBoxOrdersTxt.setText("");
                                    searchBoxOrdersTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }

                generateDashboardCardViewItems();
                filterList(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void filterList(String filter) {
        this.filter = filter;
        List<CardModel> filteredCardModels = CardModelFilterHelper.filterCardModel(cardModels, filter);
        cardModels.clear();
        cardModels.addAll(filteredCardModels);
        currentActivty.runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setListDataListener(final View view) {

        OnDataChangeListener onDataChangeListener
                = new OnDataChangeListener() {
            @Override
            public void onDatabaseChanges() {
                updateList();
            }

            @Override
            public void onCancel() {

            }
        };

        firebaseDatabaseHelper.setOnDataChangeListener(onDataChangeListener);
        CyburgerApplication.addListenerToNotify(onDataChangeListener);
    }

    private void updateList() {


        generateDashboardCardViewItems();

        if (!filter.isEmpty()) {
            filterList(filter);
        }

        if (adapter == null) {
            adapter =
                    new CardAdapter(currentActivty,
                            R.layout.dashboard_card_view, cardModels);


            listView.setAdapter(adapter);

        } else {

            if (listView.getAdapter() == null) {

                listView.setAdapter(adapter);
            }

            currentActivty.runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void generateDashboardCardViewItems() {

        cardModels.clear();
        currentActivty.clearNotifications(MainActivity.ORDERS_TAB);
        List<Order> orders = getOrders();

        for (final Order order :
                orders) {

            final Customer customer = order.getCustomer();
            String customerName = order.getCustomer().getCustomerName();

            if (profile != null) {
                final CardModel cardModel = new CardModel();
                cardModel.setExtra(order);
                cardModel.setNoPicture(true);

                if (customer.getLinkedProfileId().equals(profile.getUserId())) {
                    cardModel.setTitle("Meu pedido");
                    cardModel.setTitleColor(R.color.colorAccent);
                } else {
                    cardModel.setTitle(customerName);
                    cardModel.setTitleColor(R.color.lightGrey);
                }


                if (CyburgerApplication.isAdmin()) {
                    cardModel.setOnCardViewClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            currentActivty.setOrder(order);
                            currentActivty.displayOrderDialog();

                        }
                    });


                    currentActivty.addNotification(MainActivity.ORDERS_TAB, 1);
                } else {

                    String profileId = customer.getLinkedProfileId();
                    if (profileId != null && profile != null) {
                        if (profileId.equals(profile.getUserId())) {

                            cardModel.setOnCardViewClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    currentActivty.setOrder(order);
                                    currentActivty.displayOrderDialog();
                                }
                            });

                            currentActivty.addNotification(MainActivity.ORDERS_TAB, 1);
                        } else {
                            cardModel.setOnCardViewClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            });
                        }
                    }
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
                cardModel.setContent(orderedItemsString);

                cardModels.add(cardModel);


            }
        }

    }

    List<Order> getOrders() {

        List<Order> orders = firebaseDatabaseHelper.get();
        return orders;

    }


}
