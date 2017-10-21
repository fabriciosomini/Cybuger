package com.cynerds.cyburger.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.adapters.SpinnerArrayAdapter;
import com.cynerds.cyburger.components.TagInput;
import com.cynerds.cyburger.components.TagItem;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.combos.ComboDay;
import com.cynerds.cyburger.models.items.Item;
import com.cynerds.cyburger.models.views.TagModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageCombosActivity extends BaseActivity {

    private final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelper;
    private final FirebaseRealtimeDatabaseHelper firebaseRealtimeDatabaseHelperItems;

    public ManageCombosActivity() {
        firebaseRealtimeDatabaseHelper = new FirebaseRealtimeDatabaseHelper(Combo.class);
        firebaseRealtimeDatabaseHelperItems = new FirebaseRealtimeDatabaseHelper(Item.class);

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
        final Combo loadedCombo = (Combo) getExtra(Combo.class);

        comboNameTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());
        comboDayCbx.setAdapter(arrayAdapter);


        itemsTagInput.setOnItemAddedListener(new TagInput.OnItemAddedListener() {
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
                    if (loadedCombo != null) {
                        firebaseRealtimeDatabaseHelper.update(combo);
                    } else {
                        firebaseRealtimeDatabaseHelper.insert(combo);
                    }
                    finish();
                }
            }
        });


        FirebaseRealtimeDatabaseHelper.DataChangeListener dataChangeListener = new FirebaseRealtimeDatabaseHelper.DataChangeListener() {
            @Override
            public void onDataChanged(Object item) {


                if (firebaseRealtimeDatabaseHelperItems.get().size() > 0) {


                    updateTags();
                }

            }
        };

        firebaseRealtimeDatabaseHelperItems.setDataChangeListener(dataChangeListener);
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

        List<Item> items = firebaseRealtimeDatabaseHelperItems.get();
        return items;

    }


}
