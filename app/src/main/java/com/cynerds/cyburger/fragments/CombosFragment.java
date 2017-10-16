package com.cynerds.cyburger.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.adapters.CardAdapter;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.views.CardModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CombosFragment extends Fragment {


    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;

    List<CardModel> cardModels;
    CardAdapter adapter;
    private boolean isListCreated;
    private MainActivity currentActivty;

    public CombosFragment() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Combo.class);
        cardModels = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentActivty = (MainActivity) getActivity();

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

        // Toast.makeText(currentActivty, "UpdateList " + this.getClass().getSimpleName(), Toast.LENGTH_SHORT).show();

        final ListView listview = view.findViewById(android.R.id.list);
        getDashboardCardViewItems();

        if (adapter == null) {
            adapter =
                    new CardAdapter(currentActivty,
                            R.layout.dashboard_card_view, cardModels);


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

        cardModels.clear();

        List<Combo> combos = getCombos();


        for (final Combo combo :
                combos) {


            final CardModel cardModel = new CardModel();
            cardModel.setTitle(combo.getComboName());
            cardModel.setExtra(combo);
            cardModel.setActionIconId(R.drawable.ic_action_add);
            cardModel.setContent(combo.getComboInfo() + "\n"
                    + "Esse combo está por R$" + combo.getComboAmount());

            final DialogManager addComboToOrderingDialogManager = new DialogManager(currentActivty);
            cardModel.setOnManageClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    addComboToOrderingDialogManager.setContentView(R.layout.dialog_ordering_confirm);
                    addComboToOrderingDialogManager.showDialog("Adicionar item", "");


                    Button addToOrderBtn = addComboToOrderingDialogManager.getContentView().findViewById(R.id.addToOrderBtn);
                    Button cancelAddToOrderBtn = addComboToOrderingDialogManager.getContentView().findViewById(R.id.cancelAddToOrderBtn);

                    addToOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(currentActivty, "Item adicionado ao pedido", Toast.LENGTH_SHORT).show();


                            Badge badge = currentActivty.getBadge();

                            EditText confirmItemQuantityTxt = addComboToOrderingDialogManager.getContentView()
                                    .findViewById(R.id.confirmItemQuantityTxt);
                            String confirmItemQuatityStr = confirmItemQuantityTxt.getText().toString();

                            if (confirmItemQuatityStr.isEmpty()) {

                                confirmItemQuatityStr = confirmItemQuantityTxt.getHint().toString();
                            }

                            int itemQuantity = Integer.valueOf(confirmItemQuatityStr);
                            for (int i = 0; i < itemQuantity; i++) {

                                currentActivty.getOrder().getOrderedCombos().add(combo);
                                badge.setBadgeCount(badge.getBadgeCount() + 1);
                            }
                            addComboToOrderingDialogManager.closeDialog();


                        }
                    });

                    cancelAddToOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addComboToOrderingDialogManager.closeDialog();
                        }
                    });

                }
            });

            cardModel.setOnPictureClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogManager previewItemDialogManager = new DialogManager(currentActivty);
                    previewItemDialogManager.setContentView(R.layout.dialog_preview_item);
                    previewItemDialogManager.showDialog(combo.getComboName(), "");

                    Button deleteComboBtn = previewItemDialogManager.getContentView().findViewById(R.id.deleteComboBtn);
                    Button editComboBtn = previewItemDialogManager.getContentView().findViewById(R.id.editComboBtn);


                    deleteComboBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogAction deleteComboAction = new DialogAction();
                            deleteComboAction.setPositiveAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Combo comboToDelete = (Combo) cardModel.getExtra();
                                    firebaseRealtimeDatabaseHelper.delete(comboToDelete);
                                    previewItemDialogManager.closeDialog();
                                    Toast.makeText(currentActivty, "Combo removido", Toast.LENGTH_SHORT).show();
                                }
                            });
                            DialogManager confirmDeleteDialog = new DialogManager(currentActivty, DialogManager.DialogType.YES_NO);
                            confirmDeleteDialog.setAction(deleteComboAction);
                            confirmDeleteDialog.showDialog("Remover combo", "Tem certeza que deseja remover esse combo?");
                        }
                    });

                    editComboBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ActivityManager.startActivity(currentActivty, ManageCombosActivity.class, cardModel.getExtra());
                        }
                    });

                }
            });


            cardModels.add(cardModel);
        }


    }

    List<Combo> getCombos() {

        List<Combo> combos = firebaseRealtimeDatabaseHelper.get();
        return combos;

    }


}
