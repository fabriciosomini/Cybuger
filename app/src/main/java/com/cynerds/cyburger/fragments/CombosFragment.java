package com.cynerds.cyburger.fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.cynerds.cyburger.activities.admin.ManageCombosActivity;
import com.cynerds.cyburger.adapters.CardAdapter;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.Badge;
import com.cynerds.cyburger.components.PhotoViewer;
import com.cynerds.cyburger.helpers.BonusPointExchangeHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.FileHelper;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.helpers.ActivityManager;
import com.cynerds.cyburger.helpers.CardModelFilterHelper;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.GsonHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.interfaces.InnerMethod;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.models.combo.Combo;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.view.CardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CombosFragment extends Fragment {


    private final FirebaseDatabaseHelper firebaseDatabaseHelper;
    private MainActivity currentActivty;
    private List<CardModel> cardModels;
    private CardAdapter adapter;
    private boolean isListCreated;
    private String filter = "";
    private ListView listView;
    private View view;
    private boolean eventSet;

    public CombosFragment() {

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(getContext(), Combo.class);
        cardModels = new ArrayList<>();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        currentActivty = (MainActivity) getActivity();

        if (view == null) {

            view = inflater.inflate(R.layout.fragment_combos, container, false);
        }

        if (listView == null) {

            listView = view.findViewById(android.R.id.list);
        }

        if (!isListCreated) {
            isListCreated = true;
            setListDataListener(view);
        }

        if (!eventSet) {
            eventSet = true;
            updateList();
            setUIEvents(view);
        }

        return view;
    }


    private void setUIEvents(View view) {


        final EditText searchBoxCombosTxt = view.findViewById(R.id.searchBoxCombosTxt);


        searchBoxCombosTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().isEmpty()) {
                    searchBoxCombosTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                } else {
                    searchBoxCombosTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_close, 0);
                    searchBoxCombosTxt.setClickable(false);
                    searchBoxCombosTxt.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                                Drawable drawable = searchBoxCombosTxt.getCompoundDrawables()[2];

                                if (drawable != null) {
                                    boolean clicked = event.getRawX() >=
                                            searchBoxCombosTxt.getRight()
                                                    - drawable.getBounds().width();
                                    if (clicked) {
                                        searchBoxCombosTxt.setText("");
                                        searchBoxCombosTxt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                                        return true;
                                    }
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
                updateList();
            }

            @Override
            public void onCancel() {

            }
        };

        firebaseDatabaseHelper.setOnDataChangeListener(onDataChangeListener);
        CyburgerApplication.addListenerToNotify(onDataChangeListener);
    }

    private void updateList() {


        generateDashboardCardViewItems();

        if (!filter.isEmpty()) {
            filterList(filter);
        }

        if (adapter == null) {
            adapter =
                    new CardAdapter(currentActivty,
                            R.layout.dashboard_card_view, cardModels);


            listView.setAdapter(adapter);
        } else {

            if (listView.getAdapter() == null) {

                listView.setAdapter(adapter);
            }

            currentActivty.runOnUiThread(new Runnable() {
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void generateDashboardCardViewItems() {


        cardModels.clear();

        List<Combo> combos = getCombos();


        for (final Combo combo :
                combos) {


            final CardModel cardModel = new CardModel();
            cardModel.setTitle(combo.getComboName());
            cardModel.setExtra(combo);
            cardModel.setContent(combo.getComboInfo()
                    + "\n\nVocê ganha " + combo.getComboBonusPoints() + " pontos");
            cardModel.setSubContent("R$ " + combo.getComboAmount());
            cardModel.setPictureUri(combo.getPictureUri());

            float amount = combo.getComboAmount();
            if (BonusPointExchangeHelper.convertUserPointsToCash() >= amount) {
                DecimalFormat format = new DecimalFormat();
                format.setDecimalSeparatorAlwaysShown(false);
                String requiredPoints = "(ou " + BonusPointExchangeHelper.convertAmountToPoints(amount) + " pontos)";
                cardModel.setRightContent(requiredPoints);

            }


            cardModel.setOnCardViewClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final DialogManager previewItemDialogManager = new DialogManager(currentActivty);
                    previewItemDialogManager.setContentView(R.layout.dialog_preview_item);
                    previewItemDialogManager.showDialog(combo.getComboName(), "");

                    Button editRecordBtn = previewItemDialogManager.getContentView().findViewById(R.id.editRecordBtn);
                    Button addToOrderBtn = previewItemDialogManager.getContentView().findViewById(R.id.addToOrderBtn);
                    PhotoViewer photoViewer = previewItemDialogManager.getContentView().findViewById(R.id.previewItemComboPhotoViewer);
                    photoViewer.setEditable(false);
                    photoViewer.setPicture(FileHelper.getStoragePath(currentActivty, combo.getPictureUri()));

                    if (CyburgerApplication.isAdmin()) {

                        editRecordBtn.setVisibility(View.VISIBLE);

                        editRecordBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                ActivityManager.startActivity(currentActivty, ManageCombosActivity.class, cardModel.getExtra());
                                previewItemDialogManager.closeDialog();
                            }
                        });


                    } else {

                        editRecordBtn.setVisibility(View.INVISIBLE);
                    }


                    addToOrderBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final InnerMethod addToOrder = new InnerMethod() {
                                @Override
                                public void onExecute(Object... params) {

                                    Combo paidCombo = null;
                                    if (params.length > 0) {
                                        paidCombo = (Combo) params[0];
                                    }
                                    LogHelper.log("Item adicionado ao pedido");


                                    Badge badge = currentActivty.getBadge();

                                    if (paidCombo != null) {

                                        currentActivty.getOrder().getOrderedCombos().add(paidCombo);
                                    } else {
                                        currentActivty.getOrder().getOrderedCombos().add(combo);
                                    }
                                    badge.setBadgeCount(badge.getBadgeCount() + 1);

                                    previewItemDialogManager.closeDialog();
                                }
                            };

                            float amount = combo.getComboAmount();
                            if (BonusPointExchangeHelper.convertUserPointsToCash() >= amount) {

                                DialogManager askForPaymentMethodDialog =
                                        new DialogManager(currentActivty, DialogManager.DialogType.YES_NO);

                                DialogAction dialogAction = new DialogAction();
                                dialogAction.setPositiveAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Profile profile = CyburgerApplication.getProfile();
                                        if (profile != null) {

                                            int bonusPoint = profile.getBonusPoints();
                                            final int pointsToRemove = BonusPointExchangeHelper.convertAmountToPoints(combo.getComboAmount());
                                            int totalBonusBalance = bonusPoint - pointsToRemove;
                                            Combo paidCombo = (Combo) combo.copyValues(Combo.class);
                                            paidCombo.setComboAmount(0);
                                            paidCombo.setComboBonusPoints(0);
                                            paidCombo.setComboSpentPoints(pointsToRemove);
                                            addToOrder.onExecute(paidCombo);
                                        }


                                    }
                                });

                                dialogAction.setNegativeAction(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addToOrder.onExecute();
                                    }
                                });
                                askForPaymentMethodDialog.setAction(dialogAction);
                                askForPaymentMethodDialog.showDialog("Você gostaria de usar " +
                                        "seus pontos para comprar este combo?");


                            } else {
                                addToOrder.onExecute();
                            }


                        }
                    });


                }
            });


            cardModels.add(cardModel);
        }


    }

    List<Combo> getCombos() {

        List<Combo> combos = firebaseDatabaseHelper.get();
        return combos;

    }


}
