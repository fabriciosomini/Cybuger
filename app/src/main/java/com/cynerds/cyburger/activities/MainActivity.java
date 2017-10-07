package com.cynerds.cyburger.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.fragments.CombosFragment;
import com.cynerds.cyburger.fragments.FoodMenuFragment;
import com.cynerds.cyburger.fragments.OrdersFragment;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;

public class MainActivity extends BaseActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    CombosFragment combosFragment = new CombosFragment();
    FoodMenuFragment foodMenuFragment = new FoodMenuFragment();
    OrdersFragment ordersFragment = new OrdersFragment();
    private int backPressed = 0;
    private TextView mTextMessage;
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

                    return true;

                case R.id.navigation_foodMenu:
                    fragmentManager.beginTransaction().replace(R.id.contentLayout,
                            foodMenuFragment,
                            foodMenuFragment.getTag()).commit();
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_combos);

        setActionBarTitle(getString(R.string.title_combos));
        showActionBarMenu(true);

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

            DialogManager dialogManager = new DialogManager(this,
                    DialogManager.DialogType.YES_NO,
                    dialogAction);
            dialogManager.showDialog("Tem certeza que deseja sair?");
        }
    }


}
