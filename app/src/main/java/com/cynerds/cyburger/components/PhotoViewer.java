package com.cynerds.cyburger.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.FileDialogHelper;
import com.cynerds.cyburger.helpers.FirebaseStorageHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by fabri on 12/10/2017.
 */

public class PhotoViewer extends ConstraintLayout {

    private Context context;
    private ImageView selectedPhotoImgView;
    private boolean isEditable;

    public String getFileName() {
        return fileName;
    }

    private String fileName;

    public byte[] getData() {
        return data;
    }

    private byte[] data;

    public PhotoViewer(Context context) {
        super(context);
        this.context = context;
        initializeViews(context);
    }

    public PhotoViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_photo_viewer, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        selectedPhotoImgView = findViewById(R.id.selectedPhotoImgView);

        selectedPhotoImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PhotoViewer.this.isEditable) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Escolher imagem")
                            .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //camera intent
                                           /* Intent cameraIntent = new Intent(ProfileActivity.this, CameraActivity.class);
                                            //cameraIntent.putExtra("EXTRA_CONTACT_JID", contact.getJid());
                                            startActivity(cameraIntent);*/
                                }
                            })
                            .setNegativeButton("Galeria", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String[] extensions = {".jpeg", ".bmp", ".jpg", ".png"};
                                    FileDialogHelper.showFileDialog((Activity) context, Environment.DIRECTORY_DCIM, extensions)
                                            .addFileListener(new FileDialogHelper.FileSelectedListener() {
                                                @Override
                                                public void fileSelected(final File file) {
                                                    FileInputStream streamIn = null;
                                                    try {
                                                        streamIn = new FileInputStream(file);
                                                        Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
                                                        streamIn.close();
                                                        selectedPhotoImgView.setImageBitmap(bitmap);
                                                        selectedPhotoImgView.setDrawingCacheEnabled(true);
                                                        selectedPhotoImgView.buildDrawingCache();
                                                        Bitmap cachedBitmap = selectedPhotoImgView.getDrawingCache();
                                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                                        data = baos.toByteArray();
                                                        fileName = file.getName();

                                                        FirebaseStorageHelper firebaseStorageHelper = new FirebaseStorageHelper();
                                                        firebaseStorageHelper.insert("images/"+ fileName, data)
                                                                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                                                if(task.isSuccessful()){
                                                                    LogHelper.log("Image: " + fileName +" uploaded");
                                                                }else{
                                                                    LogHelper.log("Failed to upload image: " + fileName);
                                                                }
                                                            }
                                                        });

                                                    } catch (FileNotFoundException e) {
                                                        e.printStackTrace();
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }


                                                }
                                            });
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

    }

    public void setEditable(boolean isEditable) {

        this.isEditable = isEditable;

    }


}
