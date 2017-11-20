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
import com.cynerds.cyburger.components.PhotoViewer;
import com.cynerds.cyburger.helpers.FileHelper;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.FieldValidationHelper;
import com.cynerds.cyburger.helpers.FirebaseStorageConstants;
import com.cynerds.cyburger.helpers.FirebaseStorageHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.interfaces.OnPictureChangedListener;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.item.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManageItemsActivity extends BaseActivity {


    final FirebaseDatabaseHelper firebaseDatabaseHelper;

    private byte[] data;
    private String pictureUri;
    private File file;
    private String localPictureUri;

    public ManageItemsActivity() {

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this, Item.class);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_items);
        setActionBarTitle(getString(R.string.menu_manage_items));


        setUIEvents();

    }

    private void setUIEvents() {


        SpinnerArrayAdapter spinnerArrayAdapter =
                new SpinnerArrayAdapter(getApplicationContext(),
                        R.layout.component_dropdown_item,
                        getMeasureUnitItems());


        final Spinner spinner = findViewById(R.id.itemMeasureUnitCbx);
        spinner.setAdapter(spinnerArrayAdapter);

        final EditText itemDescriptionTxt = findViewById(R.id.itemDescriptionTxt);
        final EditText itemPriceTxt = findViewById(R.id.itemPriceTxt);
        final EditText itemIngredientsTxt = findViewById(R.id.itemIngredientsTxt);
        final EditText itemBonusPointTxt = findViewById(R.id.itemBonusPointTxt);
        final Button saveItemBtn = findViewById(R.id.saveItemBtn);
        final Button addItemPictureBtn = findViewById(R.id.addItemPictureBtn);
        final TextView deleteItemLink = findViewById(R.id.deleteItemLink);
        final Item loadedItem = (Item) getExtra(Item.class);

        itemDescriptionTxt.setTransformationMethod(android.text.method.SingleLineTransformationMethod.getInstance());

        final FirebaseStorageHelper firebaseStorageHelper = new FirebaseStorageHelper(this);

        if (loadedItem != null) {
            pictureUri = loadedItem.getPictureUri();
        }

        if (pictureUri != null) {

            localPictureUri = FileHelper.getStoragePath(this, pictureUri);
            file = new File(localPictureUri);

            if (!file.exists()) {
                firebaseStorageHelper.get(localPictureUri, file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            LogHelper.log("Loaded picture " + pictureUri);
                        } else {
                            LogHelper.log("Failed to load picture " + pictureUri);
                        }
                    }
                });
            }

        }


        addItemPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogManager previewItemDialogManager = new DialogManager(ManageItemsActivity.this);
                previewItemDialogManager.setContentView(R.layout.dialog_preview_picture);
                previewItemDialogManager.showDialog("Imagem do item", "");

                final PhotoViewer photoViewer = previewItemDialogManager.getContentView().findViewById(R.id.previewPhotoViewer);
                photoViewer.setEditable(true);

                photoViewer.addOnPictureChangedListener(new OnPictureChangedListener() {
                    @Override
                    public void onPictureChanged() {
                        localPictureUri = FileHelper.getStoragePath(
                                ManageItemsActivity.this,
                                FirebaseStorageConstants.PICTURE_FOLDER
                                        + "/" + photoViewer.getSelectedFileName());
                        pictureUri = FileHelper.getFirebasePictureStoragePath(photoViewer.getSelectedFileName());
                        data = photoViewer.getData();

                        File checkSelectedFile = new File(localPictureUri);
                        if (!checkSelectedFile.exists()) {

                            File sourceFile = new File(photoViewer.getSelectedFilePath());
                            try {
                                FileHelper.copy(sourceFile, checkSelectedFile);
                            } catch (IOException e) {
                                e.printStackTrace();

                            }

                        }

                        Button savePictureBtn = previewItemDialogManager.getContentView().findViewById(R.id.savePictureBtn);
                        Button removePictureBtn = previewItemDialogManager.getContentView().findViewById(R.id.removePictureBtn);

                        savePictureBtn.setVisibility(View.VISIBLE);
                        removePictureBtn.setVisibility(View.VISIBLE);

                        savePictureBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                previewItemDialogManager.closeDialog();
                                MessageHelper.show(ManageItemsActivity.this,
                                        MessageType.INFO,
                                        "Não se esqueça de salvar");

                            }
                        });

                        removePictureBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                data = null;
                                pictureUri = null;
                                previewItemDialogManager.closeDialog();
                            }
                        });


                    }
                });

                if (pictureUri != null) {
                    boolean pictureChanged = photoViewer.setPicture(localPictureUri);
                }

            }
        });

        if (loadedItem != null) {

            itemDescriptionTxt.setText(loadedItem.getDescription());
            itemPriceTxt.setText(String.valueOf(loadedItem.getPrice()));
            itemIngredientsTxt.setText(loadedItem.getIngredients());
            itemBonusPointTxt.setText(String.valueOf(loadedItem.getBonusPoints()));

        }

        saveItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FieldValidationHelper.isEditTextValidated(itemDescriptionTxt) &&
                        FieldValidationHelper.isEditTextValidated(itemIngredientsTxt) &&
                        FieldValidationHelper.isEditTextValidated(itemBonusPointTxt) &&
                        FieldValidationHelper.isEditTextValidated(itemPriceTxt)) {


                    pictureUri = pictureUri == null ? "" : pictureUri;

                    if(pictureUri.isEmpty()){
                        MessageHelper.show(ManageItemsActivity.this, MessageType.ERROR,
                                "Selecione uma imagem para o item");
                        return;
                    }


                    showBusyLoader(true);
                    saveItemBtn.setEnabled(false);

                    String itemDescription = itemDescriptionTxt.getText().toString().trim();
                    Float itemPrice = Float.valueOf(itemPriceTxt.getText().toString().trim());
                    String itemIngredients = itemIngredientsTxt.getText().toString().trim();
                    String size = spinner.getSelectedItem().toString();
                    int bonusPoint = Integer.valueOf(itemBonusPointTxt.getText().toString().trim());


                    Item item = loadedItem == null ? new Item() : loadedItem;
                    item.setDescription(itemDescription);
                    item.setPrice(itemPrice);
                    item.setIngredients(itemIngredients);
                    item.setSize(size);
                    item.setBonusPoints(bonusPoint);
                    item.setPictureUri(pictureUri);

                    if (loadedItem == null) {

                        firebaseDatabaseHelper.insert(item).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    MessageHelper.show(ManageItemsActivity.this,
                                            MessageType.SUCCESS,
                                            getString(R.string.ok_add_item));
                                    saveItemBtn.setEnabled(true);
                                    finish();
                                } else {
                                    MessageHelper.show(ManageItemsActivity.this,
                                            MessageType.ERROR,
                                            getString(R.string.err_add_item));
                                    saveItemBtn.setEnabled(true);
                                    showBusyLoader(false);
                                }
                            }
                        });
                    } else {

                        firebaseDatabaseHelper.update(item).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    MessageHelper.show(ManageItemsActivity.this,
                                            MessageType.SUCCESS,
                                            getString(R.string.ok_update_item));
                                    saveItemBtn.setEnabled(true);
                                    finish();
                                } else {
                                    MessageHelper.show(ManageItemsActivity.this,
                                            MessageType.ERROR,
                                            getString(R.string.err_update_item));
                                    saveItemBtn.setEnabled(true);
                                    showBusyLoader(false);
                                }
                            }
                        });
                    }

                    if (pictureUri != null && data != null) {

                        firebaseStorageHelper.insert(pictureUri, data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    LogHelper.log("Saved picture " + pictureUri);
                                } else {
                                    LogHelper.log("Failed to save picture " + pictureUri);
                                }
                            }
                        });
                    }


                }


            }
        });

        if (CyburgerApplication.isAdmin()) {
            if (loadedItem != null) {
                deleteItemLink.setVisibility(View.VISIBLE);
                deleteItemLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DialogAction deleteComboAction = new DialogAction();
                        deleteComboAction.setPositiveAction(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                showBusyLoader(true);

                                firebaseDatabaseHelper.delete(loadedItem).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            MessageHelper.show(ManageItemsActivity.this,
                                                    MessageType.SUCCESS,
                                                    getString(R.string.ok_remove_item));
                                            saveItemBtn.setEnabled(true);

                                            finish();
                                        } else {

                                            MessageHelper.show(ManageItemsActivity.this,
                                                    MessageType.ERROR,
                                                    getString(R.string.err_remove_item));
                                            showBusyLoader(false);
                                        }
                                    }
                                });

                                LogHelper.log(getString(R.string.ok_remove_item));
                            }
                        });
                        DialogManager confirmDeleteDialog = new DialogManager(ManageItemsActivity.this,
                                DialogManager.DialogType.YES_NO);
                        confirmDeleteDialog.setAction(deleteComboAction);
                        confirmDeleteDialog.showDialog(getString(R.string.remove_combo), getString(R.string.qst_remove_combo));
                    }
                });


            } else {

                deleteItemLink.setVisibility(View.GONE);
            }
        } else {

            deleteItemLink.setVisibility(View.GONE);
        }


    }


    public List<String> getMeasureUnitItems() {

        List<String> measureUnitItems = new ArrayList<>();

        measureUnitItems.add(getString(R.string.type_unit));
        measureUnitItems.add(getString(R.string.type_fatia));
        measureUnitItems.add(getString(R.string.type_big_portion));
        measureUnitItems.add(getString(R.string.type_avg_portion));
        measureUnitItems.add(getString(R.string.type_little_portion));

        measureUnitItems.add(getString(R.string.type_dose));
        measureUnitItems.add(getString(R.string.type_250));
        measureUnitItems.add(getString(R.string.type_600));
        measureUnitItems.add(getString(R.string.type_1l));
        measureUnitItems.add(getString(R.string.type_2l));
        measureUnitItems.add(getString(R.string.type_25l));
        measureUnitItems.add(getString(R.string.type_3l));


        return measureUnitItems;
    }
}
