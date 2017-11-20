package com.cynerds.cyburger.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.MainActivity;
import com.cynerds.cyburger.activities.admin.ManageItemsActivity;
import com.cynerds.cyburger.adapters.CardAdapter;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.components.PhotoViewer;
import com.cynerds.cyburger.helpers.BonusPointExchangeHelper;
import com.cynerds.cyburger.helpers.FileHelper;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.CardModelFilterHelper;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.models.item.Item;
import com.cynerds.cyburger.models.view.CardModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemsMenuFragment extends Fragment {

    final FirebaseDatabaseHelper firebaseDatabaseHelper;
    OnDataChangeListener onDataChangeListener;
    List<CardModel> cardModels;
    CardAdapter adapter;
    private boolean isListCreated;
    private MainActivity currentActivty;
    private String filter = "";

    public ItemsMenuFragment() {

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(getContext(), Item.class);
        cardModels = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentActivty = (MainActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_items_menu, container, false);

        if (!isListCreated) {
            isListCreated = true;
            setListDataListener(view);
        }

        updateList(view);
        setUIEvents(view);

        return view;
    }

    private void setUIEvents(View view) {

        final EditText searchBoxItemsTxt = view.findViewById(R.id.searchBoxItemsTxt);

        searchBoxItemsTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().isEmpty()){
                    searchBoxItemsTxt.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                }
                else{
                    searchBoxItemsTxt.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_action_close,0);
                    searchBoxItemsTxt.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Drawable drawable = searchBoxItemsTxt.getCompoundDrawables()[2];

                            if(drawable!=null){
                                boolean clicked = event.getRawX() >=
                                        searchBoxItemsTxt.getRight()
                                                - drawable.getBounds().width();
                                if (clicked) {
                                    searchBoxItemsTxt.setText("");
                                    searchBoxItemsTxt.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
                generateDashboardCardViewItems();
                filterList(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filterList(String filter) {
        this.filter = filter;
        List<CardModel> filteredCardModels = CardModelFilterHelper.filterCardModel(cardModels, filter);
        cardModels.clear();
        cardModels.addAll(filteredCardModels);
        currentActivty.runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void setListDataListener(final View view) {

        OnDataChangeListener onDataChangeListener
                = new OnDataChangeListener() {
            @Override
            public void onDatabaseChanges() {
                updateList(view);
            }

            @Override
            public void onCancel() {

            }
        };

        firebaseDatabaseHelper.setOnDataChangeListener(onDataChangeListener);
        CyburgerApplication.addListenerToNotify(onDataChangeListener);
    }

    private void updateList(View view) {

        final ListView listview = view.findViewById(android.R.id.list);
        generateDashboardCardViewItems();

        if(!filter.isEmpty()){
            filterList(filter);
        }

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

        List<Item> items = firebaseDatabaseHelper.get();
        return items;

    }

    public void generateDashboardCardViewItems() {

        cardModels.clear();

        List<Item> items = getItems();

        for (final Item item :
                items) {

            final CardModel cardModel = new CardModel();
            cardModel.setExtra(item);
            cardModel.setTitle(item.getDescription());
            cardModel.setContent("Unidade de medida: "
                    + item.getSize()
                    + "\n\nVocê ganha " + item.getBonusPoints() + " pontos");
            cardModel.setSubContent("R$" + item.getPrice());
            cardModel.setPictureUri(item.getPictureUri());

            float amount = item.getPrice();
            if(BonusPointExchangeHelper.convertUserPointsToCash()>=amount)
            {
                DecimalFormat format = new DecimalFormat();
                format.setDecimalSeparatorAlwaysShown(false);
                String requiredPoints = "(ou "
                        + format.format(BonusPointExchangeHelper.convertAmountToPoints(amount)) + " pontos)";
                cardModel.setRightContent(requiredPoints);

            }
            cardModel.setOnCardViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogManager previewItemDialogManager = new DialogManager(currentActivty);
                    previewItemDialogManager.setContentView(R.layout.dialog_preview_item);
                    previewItemDialogManager.showDialog(item.getDescription(), "");


                    Button editRecordBtn = previewItemDialogManager.getContentView().findViewById(R.id.editRecordBtn);
                    Button addToOrderBtn = previewItemDialogManager.getContentView().findViewById(R.id.addToOrderBtn);
                    PhotoViewer photoViewer = previewItemDialogManager.getContentView().findViewById(R.id.previewItemComboPhotoViewer);
                    photoViewer.setEditable(false);
                    photoViewer.setPicture(FileHelper.getStoragePath(currentActivty, item.getPictureUri()));

                    if (CyburgerApplication.isAdmin()) {

                        editRecordBtn.setVisibility(View.VISIBLE);



                        editRecordBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ActivityManager.startActivity(currentActivty, ManageItemsActivity.class, cardModel.getExtra());
                                previewItemDialogManager.closeDialog();
                            }
                        });
                    } else {

                        editRecordBtn.setVisibility(View.INVISIBLE);
                    }

                    addToOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            LogHelper.log("Item adicionado ao pedido");

                            Badge badge = currentActivty.getBadge();

                            currentActivty.getOrder().getOrderedItems().add(item);
                            badge.setBadgeCount(badge.getBadgeCount() + 1);

                            previewItemDialogManager.closeDialog();


                        }
                    });

                }
            });


            cardModels.add(cardModel);


        }

    }


}
