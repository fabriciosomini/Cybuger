package com.cynerds.cyburger.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.activities.admin.ManageItemsActivity;
import com.cynerds.cyburger.activities.admin.ManageParametersActivity;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.fragments.CombosFragment;
import com.cynerds.cyburger.fragments.ItemsMenuFragment;
import com.cynerds.cyburger.fragments.OrdersFragment;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.helpers.PostNotificationHelper;
import com.cynerds.cyburger.models.combo.Combo;
import com.cynerds.cyburger.models.customer.Customer;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.item.Item;
import com.cynerds.cyburger.models.order.Order;
import com.cynerds.cyburger.models.parameters.Parameters;
import com.cynerds.cyburger.models.profile.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;


public class MainActivity extends BaseActivity {

    public static final int COMBO_TAB = 0;
    public static final int ITEMS_TAB = 1;
    public static final int ORDERS_TAB = 2;
    FragmentManager fragmentManager = getSupportFragmentManager();
    CombosFragment combosFragment = new CombosFragment();
    ItemsMenuFragment itemsMenuFragment = new ItemsMenuFragment();
    OrdersFragment ordersFragment = new OrdersFragment();
    private FirebaseDatabaseHelper<Order> firebaseDatabaseHelperOrders;
    private FirebaseDatabaseHelper<Profile> firebaseDatabaseHelperProfile;

    private Badge badge;
    private View hamburgerMenu;
    private ImageButton hamburgerMenuIcon;
    private Order order;
    private Order previousOrder;
    private AHBottomNavigation bottomNavigation;
    private int[] notifications = new int[3];
    private boolean displayOrderDialogButtons = true;


    private AHBottomNavigation.OnTabSelectedListener mOnNavigationItemSelectedListener = new AHBottomNavigation.OnTabSelectedListener() {
        @Override
        public boolean onTabSelected(int position, boolean wasSelected) {
            switch (position) {

                case COMBO_TAB:

                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            combosFragment,
                            combosFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_combos));

                    return true;

                case ITEMS_TAB:
                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            itemsMenuFragment,
                            itemsMenuFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_dashboard));

                    return true;

                case ORDERS_TAB:
                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            ordersFragment,
                            ordersFragment.getTag()).commit();
                    setActionBarTitle(getString(R.string.title_notifications));


            }
            return true;
        }
    };


    public MainActivity() {
        firebaseDatabaseHelperOrders = new FirebaseDatabaseHelper(MainActivity.this, Order.class);
        firebaseDatabaseHelperProfile = new FirebaseDatabaseHelper(MainActivity.this, Profile.class);

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

        CyburgerApplication.subscribeToUserTopic();

    }


    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {

        return super.onCreateView(name, context, attrs);
    }

    private void setUIEvents() {


        order = new Order();
        badge = findViewById(R.id.badge);
        hamburgerMenu = findViewById(R.id.hamburgerMenu);
        hamburgerMenuIcon = findViewById(R.id.hamburgerMenuIcon);


        bottomNavigation = findViewById(R.id.bottom_navigation);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.navigation);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation);
        bottomNavigation.setOnTabSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigation.setCurrentItem(COMBO_TAB);
        bottomNavigation.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.transparent));
        bottomNavigation.setAccentColor(ContextCompat.getColor(this, R.color.redishOrange0));
        bottomNavigation.setInactiveColor(ContextCompat.getColor(this, R.color.mediumgrey));
        fragmentManager.beginTransaction().attach(ordersFragment).commit();

        setActionBarTitle(getString(R.string.title_combos));
        showActionBarMenu(true);


        final PopupMenu popupMenu = new PopupMenu(MainActivity.this, hamburgerMenu);
        popupMenu.inflate(R.menu.menu_overflow);


        View.OnTouchListener hamburgerMenuTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                popupMenu.getMenu().findItem(R.id.action_profile).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        ActivityManager.startActivity(MainActivity.this, ProfileActivity.class);
                        return false;
                    }
                });


                if (CyburgerApplication.isAdmin()) {

                    popupMenu.getMenu().findItem(R.id.action_manage_items).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.action_manage_combos).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.action_manage_parameters).setVisible(true);


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

                    popupMenu.getMenu().findItem(R.id.action_manage_parameters).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            ActivityManager.startActivity(MainActivity.this, ManageParametersActivity.class);
                            return false;
                        }
                    });
                } else {
                    popupMenu.getMenu().findItem(R.id.action_manage_items).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.action_manage_combos).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.action_manage_parameters).setVisible(false);
                }

                popupMenu.getMenu().findItem(R.id.action_sign_out).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        CyburgerApplication.autoLogin = false;
                        ActivityManager.startActivityKillingThis(MainActivity.this, LoginActivity.class);

                        return false;
                    }
                });

                popupMenu.show();

                return false;
            }
        };

        hamburgerMenuIcon.setOnTouchListener(hamburgerMenuTouchListener);
        hamburgerMenu.setOnTouchListener(hamburgerMenuTouchListener);


        badge.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                displayOrderDialog();


                return false;
            }
        });
    }


    public void displayOrderDialog(boolean displayOrderDialogButtons) {
        this.displayOrderDialogButtons = displayOrderDialogButtons;
        displayOrderDialog();
    }

    public void displayOrderDialog() {

        final boolean readOnly = order.getKey() != null;

        String title = getString(R.string.get_order);
        if (readOnly) {
            title = getString(R.string.confirmad_order);
        }

        final DialogManager orderDialog = new DialogManager(MainActivity.this);
        orderDialog.setContentView(R.layout.dialog_ordering_items);
        orderDialog.showDialog(title, "");


        TextView orderedItemsTxtView = orderDialog.getContentView().findViewById(R.id.orderedItemsTxtView);
        TextView orderedItemsAmountTxtView = orderDialog.getContentView().findViewById(R.id.orderedItemsAmountTxtView);

        String orderedItemsString = "";
        String orderedItemsAmountString = "";
        float orderedItemsAmount = 0;

        for (Combo combo :
                order.getOrderedCombos()) {

            orderedItemsAmount += combo.getComboAmount();
            orderedItemsString += combo.getComboName() + ": R$ " + combo.getComboAmount() + " (" + combo.getComboBonusPoints() + " pontos)" + "\n";
        }

        for (Item item :
                order.getOrderedItems()) {

            orderedItemsAmount += item.getPrice();
            orderedItemsString += item.getDescription() + ": R$ " + item.getPrice() + " (" + item.getBonusPoints() + " pontos)" + "\n";
        }


        orderedItemsAmountString = "R$ " + String.format("%.2f", orderedItemsAmount);
        orderedItemsAmountTxtView.setText(orderedItemsAmountString);

        if (!orderedItemsString.isEmpty()) {
            orderedItemsTxtView.setText(orderedItemsString);
        }

        if (order.getOrderedItems().size() > 0 || order.getOrderedCombos().size() > 0 || readOnly) {

            if (displayOrderDialogButtons) {
                Button confirmOrderBtn = orderDialog.getContentView().findViewById(R.id.confirmOrderBtn);
                Button removeOrderBtn = orderDialog.getContentView().findViewById(R.id.removeOrderBtn);


                if (readOnly) {


                    if (CyburgerApplication.isAdmin()) {
                        confirmOrderBtn.setText(getString(R.string.order_finishOrder));
                        confirmOrderBtn.setVisibility(View.VISIBLE);

                        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final DialogManager confirmFinishOrderDialog = new DialogManager(MainActivity.this,
                                        DialogManager.DialogType.YES_NO);

                                DialogAction confirmFinishOrderDialogAction = new DialogAction();
                                confirmFinishOrderDialogAction.setPositiveAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {


                                        for (final Profile p :
                                                firebaseDatabaseHelperProfile.get()) {
                                            if (order.getCustomer().getLinkedProfileId().equals(p.getUserId())) {
                                                int comboBonusPoints = 0;
                                                int itemsBonusPoints = 0;


                                                for (Combo combo :
                                                        order.getOrderedCombos()) {
                                                    comboBonusPoints += combo.getComboBonusPoints();
                                                }

                                                for (Item item :
                                                        order.getOrderedItems()) {
                                                    itemsBonusPoints += item.getBonusPoints();
                                                }

                                                int totalBonusPoints = comboBonusPoints
                                                        + itemsBonusPoints
                                                        + p.getBonusPoints();


                                                List<Order> orderList = firebaseDatabaseHelperOrders.get();
                                                if (orderList.size() > 1) {
                                                    Order nextOrder = orderList.get(1);
                                                    Customer nextCustomer = nextOrder.getCustomer();
                                                    String nextCustomerName = nextCustomer.getCustomerName();
                                                    String topic = getString(R.string.prefix_cyburger) + nextCustomer.getLinkedProfileId();
                                                    PostNotificationHelper.post(MainActivity.this,
                                                            "", nextCustomerName
                                                                    + getString(R.string.you_next), topic);
                                                }


                                                p.setBonusPoints(totalBonusPoints);
                                                firebaseDatabaseHelperProfile.update(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            firebaseDatabaseHelperOrders.delete(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {
                                                                        String topic = getString(R.string.prefix_cyburger) + p.getUserId();
                                                                        String customerName = order.getCustomer().getCustomerName();

                                                                        confirmFinishOrderDialog.closeDialog();
                                                                        orderDialog.closeDialog();
                                                                        MessageHelper.show(MainActivity.this,
                                                                                MessageType.SUCCESS, getString(R.string.order_complete));

                                                                        PostNotificationHelper.post(MainActivity.this,
                                                                                "", customerName
                                                                                        + getString(R.string.order_ok), topic);
                                                                    } else {
                                                                        MessageHelper.show(MainActivity.this,
                                                                                MessageType.ERROR,
                                                                                getString(R.string.err_complete_order));
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            MessageHelper.show(MainActivity.this,
                                                                    MessageType.ERROR,
                                                                    getString(R.string.err_points_profile));
                                                        }
                                                    }
                                                });

                                                return;
                                            }
                                        }

                                        MessageHelper.show(MainActivity.this,
                                                MessageType.ERROR,
                                                getString(R.string.client_404));
                                    }

                                });


                                confirmFinishOrderDialog.setAction(confirmFinishOrderDialogAction);
                                confirmFinishOrderDialog.showDialog(getString(R.string.complete_order));
                            }
                        });

                    } else {

                        ConstraintLayout.LayoutParams removeOrderBtnViewParams =
                                (ConstraintLayout.LayoutParams) removeOrderBtn.getLayoutParams();
                        removeOrderBtnViewParams
                                .setMargins(removeOrderBtnViewParams.leftMargin,
                                        removeOrderBtnViewParams.topMargin,
                                        removeOrderBtnViewParams.rightMargin,
                                        removeOrderBtnViewParams.bottomMargin);
                        removeOrderBtnViewParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        removeOrderBtn.invalidate();
                        removeOrderBtn.refreshDrawableState();

                        // removeOrderBtn.getLayoutParams().width = (int) getResources().getDimension(R.dimen.default_width);
                    }


                    orderDialog.setOnCanceListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {

                            if (previousOrder != null) {
                                order = previousOrder;
                                LogHelper.log(getString(R.string.restore_previous_order));
                            } else {
                                order = new Order();
                                LogHelper.log(getString(R.string.reset_order));
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
                            firebaseDatabaseHelperOrders.insert(order);

                            LogHelper.log(getString(R.string.order_comfirmed));

                            //Reset - pedido confirmado
                            badge.setBadgeCount(0);
                            order = new Order();
                            previousOrder = new Order();
                            orderDialog.closeDialog();


                            MessageHelper.show(MainActivity.this,
                                    MessageType.SUCCESS,
                                    getString(R.string.wait_order));


                        }
                    });
                }


                removeOrderBtn.setVisibility(View.VISIBLE);
                removeOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LogHelper.log(getString(R.string.canceled_order));

                        DialogAction removeOrderDialogAction = new DialogAction();
                        removeOrderDialogAction.setPositiveAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (readOnly) {
                                    firebaseDatabaseHelperOrders.delete(order);
                                    removeNotification(ORDERS_TAB, 1);

                                    if (CyburgerApplication.isAdmin()) {

                                        Customer customer = order.getCustomer();
                                        String topic = getString(R.string.prefix_cyburger) + customer.getLinkedProfileId();
                                        String customerName = order.getCustomer().getCustomerName();
                                        PostNotificationHelper.post(MainActivity.this,
                                                "", customerName
                                                        + getString(R.string.you_order_canceled), topic);
                                    }

                                } else {
                                    previousOrder = new Order();
                                    badge.setBadgeCount(0);
                                }

                                if (previousOrder != null) {
                                    order = previousOrder;
                                    LogHelper.log(getString(R.string.restore_previous_order));
                                } else {
                                    order = new Order();
                                    LogHelper.log(getString(R.string.reset_order));
                                }


                                orderDialog.closeDialog();

                                MessageHelper.show(MainActivity.this,
                                        MessageType.SUCCESS, getString(R.string.canceled_order));
                            }
                        });

                        DialogManager removeOrderDialog = new DialogManager(MainActivity.this,
                                DialogManager.DialogType.YES_NO);
                        removeOrderDialog.setAction(removeOrderDialogAction);
                        removeOrderDialog.showDialog(getString(R.string.msg_cancel_order), getString(R.string.qst_cancel_order));


                    }
                });
            } else {
                displayOrderDialogButtons = true;
                TextView orderedItemsTotalTxtView = orderDialog.getContentView().findViewById(R.id.orderedItemsTotalTxtView);

                if (orderedItemsTotalTxtView != null && orderedItemsAmountTxtView != null) {

                    ConstraintLayout.LayoutParams orderedItemsTotalTxtViewParams =
                            (ConstraintLayout.LayoutParams) orderedItemsTotalTxtView.getLayoutParams();
                    orderedItemsTotalTxtViewParams
                            .setMargins(orderedItemsTotalTxtViewParams.leftMargin,
                                    orderedItemsTotalTxtViewParams.topMargin,
                                    orderedItemsTotalTxtViewParams.rightMargin,
                                    0);

                    ConstraintLayout.LayoutParams orderedItemsAmountTxtViewParams =
                            (ConstraintLayout.LayoutParams) orderedItemsAmountTxtView.getLayoutParams();

                    orderedItemsAmountTxtViewParams
                            .setMargins(orderedItemsAmountTxtViewParams.leftMargin,
                                    orderedItemsAmountTxtViewParams.topMargin,
                                    orderedItemsAmountTxtViewParams.rightMargin,
                                    0);

                    orderDialog.getContentView().invalidate();
                    orderDialog.getContentView().refreshDrawableState();

                }

            }


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

        final DialogManager dialogManager = new DialogManager(this);
        dialogManager.setContentView(R.layout.dialog_confirm_exit);
        dialogManager.showDialog(getString(R.string.exit_app), "");

        dialogManager.getContentView().findViewById(R.id.confirmExitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishApplication();
            }
        });

        dialogManager.getContentView().findViewById(R.id.cancelExitBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogManager.closeDialog();
            }
        });

    }

    public void addNotification(int tabIndex, int notificationCount) {
        notifications[tabIndex] += notificationCount;

        setNotification(tabIndex, notifications[tabIndex]);
    }

    public void removeNotification(int tabIndex, int notificationCount) {

        notifications[tabIndex] -= notificationCount;
        setNotification(tabIndex, notifications[tabIndex]);
    }

    private void setNotification(int tabIndex, int count) {


        AHNotification notification = new AHNotification.Builder()
                .setText(String.valueOf(count == 0 ? "" : count))
                .setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setTextColor(ContextCompat.getColor(this, R.color.verylightgrey))
                .build();
        bottomNavigation.setNotification(notification, tabIndex);
    }


    public void clearNotifications(int tabIndex) {

        notifications[tabIndex] = 0;
        setNotification(tabIndex, notifications[tabIndex]);
    }
}
