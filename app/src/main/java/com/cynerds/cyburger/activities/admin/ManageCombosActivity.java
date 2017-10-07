package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.combos.DailyCombo;
import com.cynerds.cyburger.models.combos.MonthlyCombo;
import com.cynerds.cyburger.models.foodmenu.Item;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManageCombosActivity extends BaseActivity {

    private final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;

    public ManageCombosActivity() {
        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(MonthlyCombo.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_combos);


    }

    private void createCombos() {
        List<DashboardCardViewItem> dashboardCardViewItems = new ArrayList<>();

        Item item = new Item();
        Item item2 = new Item();
        List<Item> comboItemsList = new ArrayList<>();
        Combo combo = new Combo();
        List<Combo> combosList = new ArrayList<>();
        DailyCombo dailyCombo = new DailyCombo();
        List<DailyCombo> dailyComboList = new ArrayList<>();
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

        //5
        dailyCombo.setCombos(combosList);
        dailyCombo.setComboDay(DailyCombo.ComboDay.SEGUNDA);
        dailyCombo.setId(UUID.randomUUID().toString());

        //6
        dailyComboList.add(dailyCombo);

        monthlyCombo.setMonthlyCombos(dailyComboList);
        monthlyCombo.setId(UUID.randomUUID().toString());


        firebaseRealtimeDatabaseHelper.insert(monthlyCombo);


    }


}
