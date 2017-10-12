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
import com.cynerds.cyburger.adapters.DashboardCardAdapter;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.views.DashboardCardViewItem;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CombosFragment extends Fragment {



    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;

    List<DashboardCardViewItem> dashboardCardViewItems;
    DashboardCardAdapter adapter;
    private boolean isListCreated;

    public CombosFragment() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Combo.class);
        dashboardCardViewItems = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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


                if (firebaseRealtimeDatabaseHelper.get().size() > 0) {

                    updateList(view);

                }
            }
        };

        firebaseRealtimeDatabaseHelper.setDataChangeListener(dataChangeListener);
    }

    private void updateList(View view) {

        Toast.makeText(getActivity(), "updateList", Toast.LENGTH_SHORT).show();
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

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void getDashboardCardViewItems() {


        List<Combo> combos = getCombos();

        boolean repeat = false;


                for (Combo combo :
                        combos) {


                    DashboardCardViewItem dashboardCardViewItem = new DashboardCardViewItem();
                    dashboardCardViewItem.setTitle(combo.getComboName());
                    dashboardCardViewItem.setId(combo.getId());
                    dashboardCardViewItem.setActionIconId(R.drawable.ic_action_add);
                    dashboardCardViewItem.setContent(combo.getComboInfo() + "\n"
                            + "Esse combo está por R$" + combo.getComboAmount());
                    dashboardCardViewItem.setOnManageClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogAction dialogAction = new DialogAction();
                            dialogAction.setPositiveAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getContext(), "Item adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                                }
                            });
                            DialogManager dialogManager = new DialogManager(getContext(), DialogManager.DialogType.YES_NO, dialogAction);
                            dialogManager.showDialog("Deseja pedir esse item?");

                        }
                    });

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

    List<Combo> getCombos() {

        List<Combo> combos = firebaseRealtimeDatabaseHelper.get();
        return combos;

    }






}
