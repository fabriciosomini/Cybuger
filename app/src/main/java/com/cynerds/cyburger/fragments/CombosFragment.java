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
import com.cynerds.cyburger.models.foodmenu.Item;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CombosFragment extends Fragment {




    public CombosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_combos, container, false);

        ListView listview = (ListView) view.findViewById(android.R.id.list);

        DashboardCardAdapter adapter =
                new DashboardCardAdapter(getActivity(), R.layout.dashboard_card_view, getDashboardCardViewItems());

        listview.setAdapter(adapter);

        getDailyCombo();

        return view;
    }

    DailyCombo getDailyCombo() {


        List<DashboardCardViewItem> dashboardCardViewItems = new ArrayList<>();

        Item item = new Item();
        Item item2 = new Item();
        List<Item> comboItems = new ArrayList<>();
        Combo combo = new Combo();
        List<Combo> combos = new ArrayList<>();
        DailyCombo dailyCombo = new DailyCombo();


        //1
        item.setDescription("Coca-cola");
        item.setPrice(3.50f);
        item.setSize("600ml");

        item2.setDescription("Hamburger");
        item2.setPrice(10.50f);
        item2.setSize("1 unidade");

        //2
        comboItems.add(item);
        comboItems.add(item2);


        //3
        combo.setComboItems(comboItems);


        //4
        combos.add(combo);

        //5
        dailyCombo.setCombos(combos);
        dailyCombo.setDate("Segunda-feira");


        FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(DailyCombo.class);
        firebaseRealtimeDatabaseHelper.insert(dailyCombo);


        DailyCombo server = firebaseRealtimeDatabaseHelper.selectAll();
        return dailyCombo;

    }


    public List<DashboardCardViewItem> getDashboardCardViewItems() {

        List<DashboardCardViewItem> items = new ArrayList<>();
        return items;
    }
}
