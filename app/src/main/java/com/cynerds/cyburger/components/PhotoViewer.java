package com.cynerds.cyburger.components;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.FileDialogHelper;
import com.cynerds.cyburger.helpers.FirebaseStorageConstants;
import com.cynerds.cyburger.interfaces.OnPictureChangedListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

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
    private TextView selectedPhotoImgViewTxt;
    private boolean isEditable;
    private OnPictureChangedListener onPictureChangedListener;
    private String fileName;
    private byte[] data;
    private String picture;

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

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
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
        selectedPhotoImgViewTxt = findViewById(R.id.selectedPhotoImgViewTxt);

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

                                                    setPicture(file.getPath());


                                                }
                                            });
                                }
                            });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    Button negativeBtn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positiveBtn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                    Button neutralBtn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);

                    if (negativeBtn != null) {
                        negativeBtn.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    }

                    if (positiveBtn != null) {
                        positiveBtn.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    }

                    if (neutralBtn != null) {

                        neutralBtn.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    }
                }
            }
        });

    }

    public void setEditable(boolean isEditable) {

        this.isEditable = isEditable;

        if(!isEditable){
            selectedPhotoImgViewTxt.setVisibility(GONE);
        }
        else{
            selectedPhotoImgViewTxt.setVisibility(VISIBLE);
        }

    }


    public void addOnPictureChangedListener(OnPictureChangedListener onPictureChangedListener) {
        this.onPictureChangedListener = onPictureChangedListener;
    }

    public boolean setPicture(String uri) {

        if (uri != null) {
            File file = new File(uri);
            if (file != null) {
                if (file.exists()) {

                   if(file.length()>0){
                       try {


                           ViewGroup.LayoutParams layoutParams = selectedPhotoImgView.getLayoutParams();
                           int width = layoutParams.width;
                           int height = layoutParams.height;

                           RequestCreator requestCreator = Picasso.with(context).load(file);
                           if (width > 0 && height > 0) {
                               requestCreator.resize(width, height).centerCrop().into(selectedPhotoImgView);
                           } else {
                               requestCreator.into(selectedPhotoImgView);
                           }


                           selectedPhotoImgViewTxt.setVisibility(GONE);


                           FileInputStream streamIn = new FileInputStream(file);
                           Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
                           streamIn.close();
                           selectedPhotoImgView.setDrawingCacheEnabled(true);
                           selectedPhotoImgView.buildDrawingCache();
                           ByteArrayOutputStream baos = new ByteArrayOutputStream();
                           bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                           data = baos.toByteArray();
                           baos.close();
                           fileName = file.getName();

                           if (onPictureChangedListener != null) {
                               onPictureChangedListener.onPictureChanged();
                           }
                           return true;
                       } catch (FileNotFoundException e) {
                           e.printStackTrace();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                }


            }
        }

       if(isEditable){
           selectedPhotoImgViewTxt.setVisibility(VISIBLE);
       }

        return false;
    }
}
