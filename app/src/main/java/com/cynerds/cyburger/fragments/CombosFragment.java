package com.cynerds.cyburger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.adapters.DashboardCardAdapter;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.combos.DailyCombo;
import com.cynerds.cyburger.models.combos.MonthlyCombo;
import com.cynerds.cyburger.models.foodmenu.Item;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class CombosFragment extends Fragment {


    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    List<DashboardCardViewItem> dashboardCardViewItems;
    DashboardCardAdapter adapter;

    public CombosFragment() {
        // Required empty public constructor

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(MonthlyCombo.class);
        dashboardCardViewItems = new ArrayList<>();
        //createCombos();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_combos, container, false);
        createList(view);

        return view;
    }

    private void createList(View view) {
        final ListView listview = (ListView) view.findViewById(android.R.id.list);


        FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener = new FirebaseRealtimeDatabaseHelper.DataChangeListener() {
            @Override
            public void onDataChanged(Object item) {


                if (firebaseRealtimeDatabaseHelper.selectAll().size() > 0) {

                    getDashboardCardViewItems();

                    if (adapter == null) {
                        adapter =
                                new DashboardCardAdapter(getContext(),
                                        R.layout.dashboard_card_view, dashboardCardViewItems);


                        listview.setAdapter(adapter);
                    } else {


                        getActivity().runOnUiThread(new Runnable() {
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


    List<MonthlyCombo> getDailyCombo() {

        List<MonthlyCombo> server = firebaseRealtimeDatabaseHelper.selectAll();
        return server;

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

    public void getDashboardCardViewItems() {



        List<MonthlyCombo> monthlyCombos = getDailyCombo();

        boolean repeat = false;
        for (MonthlyCombo monthlyCombo :
                monthlyCombos) {

            for (DailyCombo dailyCombo :
                    monthlyCombo.getMonthlyCombos()) {

                for (Combo combo :
                        dailyCombo.getCombos()) {


                    DashboardCardViewItem dashboardCardViewItem = new DashboardCardViewItem();
                    dashboardCardViewItem.setTitle(combo.getComboName());
                    dashboardCardViewItem.setId(combo.getId());


                    for (int i = 0; i < dashboardCardViewItems.size(); i++) {
                        DashboardCardViewItem d = dashboardCardViewItems.get(i);
                        if (combo.getId().equals(d.getId())) {
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


        }

    }


}
