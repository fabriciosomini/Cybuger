package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.adapters.SpinnerArrayAdapter;
import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.components.TagInput;
import com.cynerds.cyburger.components.TagItem;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.interfaces.OnItemAddedListener;
import com.cynerds.cyburger.models.combo.Combo;
import com.cynerds.cyburger.models.combo.ComboDay;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.item.Item;
import com.cynerds.cyburger.models.view.TagModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageCombosActivity extends BaseActivity {

    private final FirebaseDatabaseHelper firebaseDatabaseHelper;
    private final FirebaseDatabaseHelper firebaseDatabaseHelperItems;

    public ManageCombosActivity() {
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this, Combo.class);
        firebaseDatabaseHelperItems = new FirebaseDatabaseHelper(this, Item.class);

    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.toString(e.getEnumConstants()).replaceAll("^.|.$", "").split(", ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_combos);
        setActionBarTitle(getString(R.string.menu_manage_combos));

        setUIEvents();

    }

    private void setUIEvents() {
        SpinnerArrayAdapter arrayAdapter = new SpinnerArrayAdapter(this,
                R.layout.component_dropdown_item,
                getNames(ComboDay.class));
        final Spinner comboDayCbx = findViewById(R.id.comboDayCbx);
        final EditText comboNameTxt = findViewById(R.id.comboNameTxt);
        final EditText comboInfoTxt = findViewById(R.id.comboInfoTxt);
        final TagInput itemsTagInput = findViewById(R.id.itemsTagInput);
        final EditText comboBonusPointsTxt = findViewById(R.id.comboBonusPointTxt);
        final EditText comboPriceTxt = findViewById(R.id.comboPriceTxt);
        final Button saveComboBtn = findViewById(R.id.saveComboBtn);
        final TextView deleteComboLink = findViewById(R.id.deleteComboLink);
        final Combo loadedCombo = (Combo) getExtra(Combo.class);

        comboNameTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        comboInfoTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        comboDayCbx.setAdapter(arrayAdapter);


        itemsTagInput.setOnItemAddedListener(new OnItemAddedListener() {
            @Override
            public void onAddItem(TagItem tagItem) {

                String suggestedPrice = recalcuteSuggestedPrice(itemsTagInput.getSelectedTagModels());
                comboPriceTxt.setHint(suggestedPrice);

                String suggestedBonusPoints = recalcuteSuggestedBonutsPoints(itemsTagInput.getSelectedTagModels());
                comboBonusPointsTxt.setHint(suggestedBonusPoints);

            }

            @Override
            public void onRemoveItem(TagItem tagItem) {

                String suggestedPrice = recalcuteSuggestedPrice(itemsTagInput.getSelectedTagModels());
                comboPriceTxt.setHint(suggestedPrice);

                String suggestedBonusPoints = recalcuteSuggestedBonutsPoints(itemsTagInput.getSelectedTagModels());
                comboBonusPointsTxt.setHint(suggestedBonusPoints);

            }
        });

        if (loadedCombo != null) {
            int selectedItemIndex = 0;
            for (int i = 0; i < ComboDay.values().length; i++) {
                ComboDay c = ComboDay.values()[i];
                if (c == loadedCombo.getComboDay()) {
                    selectedItemIndex = i;
                    break;
                }
            }
            comboDayCbx.setSelection(selectedItemIndex);
            comboNameTxt.setText(loadedCombo.getComboName());
            comboInfoTxt.setText(loadedCombo.getComboInfo());
            comboBonusPointsTxt.setText(String.valueOf(loadedCombo.getComboBonusPoints()));
            comboPriceTxt.setText(String.valueOf(loadedCombo.getComboAmount()));
            List<TagModel> tagModels = generateTagModels(loadedCombo.getComboItems());
            itemsTagInput.setFilterableList(tagModels);
            itemsTagInput.addAllTags();
        }


        saveComboBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //VALIDAÇÕES -
                //ANTES DE PEGAR O CONTEÚDO DOS CAMPOS
                //E DENTRO DO EVENTO DE CLIQUE DO BOTÃO DE SALVAR
                if (FieldValidationHelper.isEditTextValidated(comboNameTxt) &&
                        FieldValidationHelper.isEditTextValidated(comboInfoTxt) &&
                        FieldValidationHelper.isEditTextValidated(comboBonusPointsTxt) &&
                        FieldValidationHelper.isEditTextValidated(comboPriceTxt)) {


                    //Validação especial, SOMENTE para este caso
                    if (itemsTagInput.getSelectedTagModels().size() == 0) {
                        FieldValidationHelper.setFieldAsInvalid(itemsTagInput.getSearchTagItemBox(), R.string.manage_combos_required_field);
                        return;
                    }

                    saveComboBtn.setEnabled(false);
                    showBusyLoader(true);

                    String comboName = comboNameTxt.getText().toString().trim();
                    String comboInfo = comboInfoTxt.getText().toString().trim();
                    int comboBonusPoints = Integer.valueOf(comboBonusPointsTxt.getText().toString().trim());
                    Float comboAmount = Float.valueOf(comboPriceTxt.getText().toString().trim());
                    ComboDay comboDay = ComboDay.valueOf(comboDayCbx.getSelectedItem().toString().trim());
                    int comboItems = itemsTagInput.getSelectedTagModels().size();

                    Combo combo = loadedCombo == null ? new Combo() : loadedCombo;
                    combo.setComboName(comboName);
                    combo.setComboAmount(comboAmount);
                    combo.setComboDay(comboDay);
                    combo.setComboInfo(comboInfo);
                    combo.setComboBonusPoints(comboBonusPoints);

                    List<Item> items = new ArrayList<Item>();
                    for (TagModel tagModel : itemsTagInput.getSelectedTagModels()) {
                        Object o = tagModel.getObject();
                        if (o instanceof Item) {
                            items.add((Item) o);
                        }
                    }
                    combo.setComboItems(items);

                    if (loadedCombo == null) {

                        firebaseDatabaseHelper.insert(combo).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    MessageHelper.show(ManageCombosActivity.this,
                                            MessageType.SUCCESS,
                                            "Combo adicionado");
                                    saveComboBtn.setEnabled(true);
                                    finish();
                                } else {
                                    MessageHelper.show(ManageCombosActivity.this,
                                            MessageType.ERROR,
                                            "Erro ao adicionar combo");
                                    saveComboBtn.setEnabled(true);
                                    showBusyLoader(false);
                                }
                            }
                        });
                    } else {
                        firebaseDatabaseHelper.update(combo).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    MessageHelper.show(ManageCombosActivity.this,
                                            MessageType.SUCCESS,
                                            "Combo atualizado");
                                    saveComboBtn.setEnabled(true);
                                    finish();
                                } else {
                                    MessageHelper.show(ManageCombosActivity.this,
                                            MessageType.ERROR,
                                            "Erro ao atualizar combo");
                                    saveComboBtn.setEnabled(true);
                                    showBusyLoader(false);
                                }
                            }
                        });
                    }

                }
            }
        });

        if (CyburgerApplication.isAdmin()) {
            if (loadedCombo != null) {
                deleteComboLink.setVisibility(View.VISIBLE);
                deleteComboLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogAction deleteComboAction = new DialogAction();
                        deleteComboAction.setPositiveAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                showBusyLoader(true);
                                firebaseDatabaseHelper.delete(loadedCombo).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {

                                        if (task.isSuccessful()) {
                                            MessageHelper.show(ManageCombosActivity.this,
                                                    MessageType.SUCCESS,
                                                    "Combo removido");
                                            saveComboBtn.setEnabled(true);

                                            finish();
                                        } else {

                                            MessageHelper.show(ManageCombosActivity.this,
                                                    MessageType.ERROR,
                                                    "Erro ao remover combo");
                                            showBusyLoader(false);
                                        }
                                    }
                                });

                                LogHelper.log("Combo removido");
                            }
                        });

                        DialogManager confirmDeleteDialog = new DialogManager(ManageCombosActivity.this,
                                DialogManager.DialogType.YES_NO);
                        confirmDeleteDialog.setAction(deleteComboAction);
                        confirmDeleteDialog.showDialog("Remover combo", "Tem certeza que deseja remover esse item?");

                    }
                });
            } else {
                deleteComboLink.setVisibility(View.GONE);

            }


        } else {
            deleteComboLink.setVisibility(View.GONE);

        }


        OnDataChangeListener onDataChangeListener = new OnDataChangeListener() {
            @Override
            public void onDataChanged() {


                if (firebaseDatabaseHelperItems.get().size() > 0) {

                    updateTags();
                }

            }

            @Override
            public void onCancel() {

            }
        };

        firebaseDatabaseHelperItems.setOnDataChangeListener(onDataChangeListener);
    }

    private String recalcuteSuggestedPrice(List<TagModel> tagModels) {
        float suggestedPrice = 0;
        for (TagModel tagModel : tagModels) {
            Item item = (Item) tagModel.getObject();

            suggestedPrice += item.getPrice();

        }

        return "Preço sugerido: R$ " + String.format("%.2f", suggestedPrice);
    }


    private String recalcuteSuggestedBonutsPoints(List<TagModel> tagModels) {
        int suggestedBonusPoints = 0;
        for (TagModel tagModel : tagModels) {
            Item item = (Item) tagModel.getObject();

            suggestedBonusPoints += item.getBonusPoints();

        }

        return "Pontos sugeridos " + String.valueOf(suggestedBonusPoints);
    }

    private void updateTags() {

        List<Item> items = getItems();

        List<TagModel> tagModelList = generateTagModels(items);


        TagInput tagInput = findViewById(R.id.itemsTagInput);
        tagInput.setFilterableList(tagModelList);
    }

    private List<TagModel> generateTagModels(List<Item> items) {
        List<TagModel> tagModelList = new ArrayList<>();
        for (Item item :
                items) {
            TagModel tagModel = new TagModel();
            tagModel.setDescription(item.getDescription());
            tagModel.setObject(item);
            tagModelList.add(tagModel);
        }
        return tagModelList;
    }

    List<Item> getItems() {

        List<Item> items = firebaseDatabaseHelperItems.get();
        return items;

    }


}
