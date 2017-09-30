package com.cynerds.cyburger.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;

public class MainActivity extends WorkspaceActivity {

    private int backPressed = 0;
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_combos:
                    mTextMessage.setText(R.string.title_combos);
                    setActionBarTitle(getString(R.string.title_combos));
                    return true;
                case R.id.navigation_foodMenu:
                    mTextMessage.setText(R.string.title_dashboard);
                    setActionBarTitle(getString(R.string.title_dashboard));
                    return true;
                case R.id.navigation_orders:
                    mTextMessage.setText(R.string.title_notifications);
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setActionBarTitle(getString(R.string.title_combos));
    }

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed > 0) {
            Runnable positiveAction = new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            };

            Runnable negativeAction = new Runnable() {
                @Override
                public void run() {
                    backPressed = 0;
                }
            };
            DialogAction dialogAction = new DialogAction();
            dialogAction.setPositiveAction(positiveAction);
            dialogAction.setNegativeAction(negativeAction);

            DialogManager dialogManager = new DialogManager();
            dialogManager.showDialog(this, "Tem certeza que deseja sair?", DialogManager.DialogType.YES_NO, dialogAction);
        }
    }

}
