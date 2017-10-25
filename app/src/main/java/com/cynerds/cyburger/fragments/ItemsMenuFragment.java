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

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.activities.admin.ManageItemsActivity;
import com.cynerds.cyburger.adapters.CardAdapter;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.models.items.Item;
import com.cynerds.cyburger.models.views.CardModel;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsMenuFragment extends Fragment {

    final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener;
    List<CardModel> cardModels;
    CardAdapter adapter;
    private boolean isListCreated;
    private MainActivity currentActivty;


    public ItemsMenuFragment() {

        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Item.class);
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

        //LogHelper.show( "UpdateList " + this.getClass().getSimpleName());
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


    List<Item> getItems() {

        List<Item> items = firebaseRealtimeDatabaseHelper.get();
        return items;

    }

    public void getDashboardCardViewItems() {

        cardModels.clear();

        List<Item> items = getItems();

        for (final Item item :
                items) {

            final CardModel cardModel = new CardModel();
            cardModel.setExtra(item);
            cardModel.setTitle(item.getDescription());
            cardModel.setActionIconId(R.drawable.ic_action_add);
            cardModel.setContent("Unidade de medida: "
                    + item.getSize()
                    + "\n\n+" + item.getBonusPoints() + " pontos");
            cardModel.setSubContent("Valor: R$" + item.getPrice());


            final DialogManager addItemToOrderingDialogManager = new DialogManager(currentActivty);
            cardModel.setOnManageClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    addItemToOrderingDialogManager.setContentView(R.layout.dialog_ordering_confirm);
                    addItemToOrderingDialogManager.showDialog("Adicionar item", "");

                    Button addToOrderBtn = addItemToOrderingDialogManager.getContentView().findViewById(R.id.addToOrderBtn);
                    Button cancelAddToOrderBtn = addItemToOrderingDialogManager.getContentView().findViewById(R.id.cancelAddToOrderBtn);

                    addToOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LogHelper.show("Item adicionado ao pedido");


                            Badge badge = currentActivty.getBadge();

                            EditText confirmItemQuantityTxt = addItemToOrderingDialogManager.getContentView()
                                    .findViewById(R.id.confirmItemQuantityTxt);

                            String confirmItemQuatityStr = confirmItemQuantityTxt.getText().toString();

                            if (confirmItemQuatityStr.isEmpty()) {

                                confirmItemQuatityStr = confirmItemQuantityTxt.getHint().toString();
                            }

                            int itemQuantity = Integer.valueOf(confirmItemQuatityStr);
                            for (int i = 0; i < itemQuantity; i++) {

                                currentActivty.getOrder().getOrderedItems().add(item);
                                badge.setBadgeCount(badge.getBadgeCount() + 1);
                            }

                            addItemToOrderingDialogManager.closeDialog();

                        }
                    });

                    cancelAddToOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addItemToOrderingDialogManager.closeDialog();
                        }
                    });

                }
            });

            cardModel.setOnCardViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogManager previewItemDialogManager = new DialogManager(currentActivty);
                    previewItemDialogManager.setContentView(R.layout.dialog_preview_item);
                    previewItemDialogManager.showDialog(item.getDescription(), "");

                    Button deleteComboBtn = previewItemDialogManager.getContentView().findViewById(R.id.deleteComboBtn);
                    Button editComboBtn = previewItemDialogManager.getContentView().findViewById(R.id.editComboBtn);


                    deleteComboBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogAction deleteComboAction = new DialogAction();
                            deleteComboAction.setPositiveAction(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Item itemToDelete = (Item) cardModel.getExtra();
                                    firebaseRealtimeDatabaseHelper.delete(itemToDelete);
                                    previewItemDialogManager.closeDialog();
                                    LogHelper.show("Item removido");
                                }
                            });
                            DialogManager confirmDeleteDialog = new DialogManager(currentActivty, DialogManager.DialogType.YES_NO);
                            confirmDeleteDialog.setAction(deleteComboAction);
                            confirmDeleteDialog.showDialog("Remover combo", "Tem certeza que deseja remover esse item?");
                        }
                    });

                    editComboBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ActivityManager.startActivity(currentActivty, ManageItemsActivity.class, cardModel.getExtra());
                            previewItemDialogManager.closeDialog();
                        }
                    });

                }
            });


            cardModels.add(cardModel);


        }

    }


}
