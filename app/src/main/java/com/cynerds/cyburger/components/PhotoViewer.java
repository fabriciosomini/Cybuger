package com.cynerds.cyburger.components;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.cynerds.cyburger.R;

/**
 * Created by fabri on 12/10/2017.
 */

public class PhotoViewer extends ConstraintLayout {

    private ImageView selectedPhotoImgView;

    public PhotoViewer(Context context) {
        super(context);

        initializeViews(context);
    }

    public PhotoViewer(Context context, AttributeSet attrs) {
        super(context, attrs);

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


    }

    public void setEditable(boolean isEditable) {

        if (isEditable) {

            selectedPhotoImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                                    Intent intent = new Intent();
                                    // Show only images, no videos or anything else
                                    intent.setType("image/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    // Always show the chooser (if there are multiple options available)
                                    // getContext().startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), PICK_IMAGE_REQUEST);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
        }

    }
}
