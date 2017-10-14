package com.cynerds.cyburger.activities;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.activities.admin.ManageItemsActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.fragments.CombosFragment;
import com.cynerds.cyburger.fragments.ItemsMenuFragment;
import com.cynerds.cyburger.fragments.OrdersFragment;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.customer.Customer;
import com.cynerds.cyburger.models.items.Item;
import com.cynerds.cyburger.models.orders.Order;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends BaseActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    CombosFragment combosFragment = new CombosFragment();
    ItemsMenuFragment itemsMenuFragment = new ItemsMenuFragment();
    OrdersFragment ordersFragment = new OrdersFragment();
    private int backPressed = 0;

    private FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelperOrders;
    private Badge badge;
    private View hamburgerMenu;
    private Order order;
    private Order previousOrder;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {

                case R.id.navigation_combos:

                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            combosFragment,
                            combosFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_combos));
                    Toast.makeText(MainActivity.this, "CombosFragment", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.navigation_foodMenu:
                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            itemsMenuFragment,
                            itemsMenuFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_dashboard));

                    return true;

                case R.id.navigation_orders:
                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            ordersFragment,
                            ordersFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_notifications));

                    return true;
            }
            return false;

        }

    };

    public MainActivity() {
        firebaseRealtimeDatabaseHelperOrders = new FirebaseRealtimeDatabaseHelper(Order.class);
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Badge getBadge() {
        return badge;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setUIEvents();

    }

    private void setUIEvents() {

        order = new Order();
        badge = findViewById(R.id.badge);
        hamburgerMenu = findViewById(R.id.hamburgerMenu);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_combos);

        setActionBarTitle(getString(R.string.title_combos));
        showActionBarMenu(true);
        showBadge(true);

        hamburgerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, hamburgerMenu);
                popupMenu.inflate(R.menu.menu_overflow);

                popupMenu.getMenu().findItem(R.id.action_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(MainActivity.this, ProfileActivity.class);
                        return false;
                    }
                });


                popupMenu.getMenu().findItem(R.id.action_manage_items).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(MainActivity.this, ManageItemsActivity.class);
                        return false;
                    }
                });

                popupMenu.getMenu().findItem(R.id.action_manage_combos).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(MainActivity.this, ManageCombosActivity.class);
                        return false;
                    }
                });

                popupMenu.show();

            }
        });


        badge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                displayOrderDialog();


                return false;
            }
        });
    }

    public void displayOrderDialog() {

        final boolean readOnly = order.getKey() != null;

        String title = "Fazer pedido";
        if (readOnly) {
            title = "PEDIDO CONFIRMADO";
        }

        final DialogManager dialogManager = new DialogManager(MainActivity.this);
        dialogManager.setContentView(R.layout.dialog_ordering_items);
        dialogManager.showDialog(title, "");


        TextView orderedItemsTxtView = dialogManager.getContentView().findViewById(R.id.orderedItemsTxtView);
        TextView orderedItemsAmountTxtView = dialogManager.getContentView().findViewById(R.id.orderedItemsAmountTxtView);

        String orderedItemsString = "";
        String orderedItemsAmountString = "";
        float orderedItemsAmount = 0;

        for (Combo combo :
                order.getOrderedCombos()) {

            orderedItemsAmount += combo.getComboAmount();
            orderedItemsString += combo.getComboName() + " - R$ " + combo.getComboAmount() + "\n";
        }

        for (Item item :
                order.getOrderedItems()) {

            orderedItemsAmount += item.getPrice();
            orderedItemsString += item.getDescription() + " - R$ " + item.getPrice() + "\n";
        }


        orderedItemsAmountString = "R$ " + String.valueOf(orderedItemsAmount);
        orderedItemsAmountTxtView.setText(orderedItemsAmountString);

        if (!orderedItemsString.isEmpty()) {
            orderedItemsTxtView.setText(orderedItemsString);
        }

        if (order.getOrderedItems().size() > 0 || order.getOrderedCombos().size() > 0 || readOnly) {

            Button confirmOrderBtn = dialogManager.getContentView().findViewById(R.id.confirmOrderBtn);
            Button removeOrderBtn = dialogManager.getContentView().findViewById(R.id.removeOrderBtn);


            if (readOnly) {

                removeOrderBtn.setText("Cancelar pedido");
                removeOrderBtn.getLayoutParams().width *= 2;

                dialogManager.setOnCanceListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (previousOrder != null) {
                            order = previousOrder;
                            Toast.makeText(MainActivity.this, "Restore previous order", Toast.LENGTH_SHORT).show();
                        } else {
                            order = new Order();
                            Toast.makeText(MainActivity.this, "reset order", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            } else {

                previousOrder = order;
                confirmOrderBtn.setVisibility(View.VISIBLE);
                confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String customerName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        Customer customer = new Customer();
                        customer.setCustomerName(customerName);
                        customer.setLinkedProfileId(CyburgerApplication.getProfile().getUserId());

                        order.setCustomer(customer);
                        firebaseRealtimeDatabaseHelperOrders.insert(order);

                        Toast.makeText(MainActivity.this, "Pedido confirmado", Toast.LENGTH_SHORT).show();

                        //Reset - pedido confirmado
                        badge.setBadgeCount(0);
                        order = new Order();
                        previousOrder = new Order();
                        dialogManager.closeDialog();
                    }
                });
            }


            removeOrderBtn.setVisibility(View.VISIBLE);
            removeOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(MainActivity.this, "Pedido cancelado", Toast.LENGTH_SHORT).show();

                    if (readOnly) {
                        firebaseRealtimeDatabaseHelperOrders.delete(order);
                    } else {
                        previousOrder = new Order();
                    }

                    //Reset - pedido cancelado
                    badge.setBadgeCount(0);
                    order = new Order();
                    dialogManager.closeDialog();

                }
            });


        }

    }

    private void showBadge(boolean showBadge) {

        if (showBadge) {
            badge.setVisibility(View.VISIBLE);

        } else {
            badge.setVisibility(View.INVISIBLE);

        }
    }

    private void showActionBarMenu(boolean showMenu) {

        if (showMenu) {


            if (showMenu) {
                hamburgerMenu.setVisibility(View.VISIBLE);

            } else {
                hamburgerMenu.setVisibility(View.INVISIBLE);

            }
        }
    }

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed > 0) {


            DialogAction dialogAction = new DialogAction();
            dialogAction.setPositiveAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            dialogAction.setNegativeAction(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backPressed = 0;
                }
            });

            DialogManager dialogManager = new DialogManager(this, DialogManager.DialogType.YES_NO);
            dialogManager.setAction(dialogAction);
            dialogManager.showDialog("Tem certeza que deseja sair?");
        }
    }


}
