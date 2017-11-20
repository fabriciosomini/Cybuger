package com.cynerds.cyburger.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.FileHelper;
import com.cynerds.cyburger.helpers.FirebaseStorageHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.models.view.CardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.List;

/**
 * Created by fabri on 05/07/2017.
 */

public class CardAdapter extends ArrayAdapter<CardModel> {


    private final Context context;

    public CardAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<CardModel> objects) {
        super(context, resource, objects);
        this.context = context;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        final CardModel cardModel = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dashboard_card_view, parent, false);
        }
        // Lookup view for data population
        final ImageView cardPicture = convertView.findViewById(R.id.cardIcon);
        TextView cardTitle = convertView.findViewById(R.id.cardTitle);
        TextView cardContent = convertView.findViewById(R.id.cardContent);
        TextView cardSubContent = convertView.findViewById(R.id.cardSubContent);
        TextView cardRightContent = convertView.findViewById(R.id.cardRightContent);
        ImageView cardManageIcon = convertView.findViewById(R.id.cardManageIcon);
        ConstraintLayout cardClickArea = convertView.findViewById(R.id.cardClickArea);
        final View.OnClickListener onCardViewClickListener = cardModel.getOnCardViewClickListener();
        View.OnClickListener onManageClickListener = cardModel.getOnManageClickListener();
        View.OnClickListener onPictureViewClickListener = cardModel.getOnPictureClickListener();

        cardManageIcon.setImageResource(cardModel.getActionIconId());


        if (cardModel.getTitleColor() > 0) {
            cardTitle.setTextColor(ContextCompat.getColor(getContext(), cardModel.getTitleColor()));
        }

        if (onPictureViewClickListener != null) {
            cardPicture.setClickable(true);
            cardPicture.setOnClickListener(onPictureViewClickListener);

        }

        if (onManageClickListener != null) {
            cardManageIcon.setOnClickListener(onManageClickListener);

        }

        if (onCardViewClickListener != null) {
            cardClickArea.setOnClickListener(onCardViewClickListener);
            if (onPictureViewClickListener == null) {
                cardPicture.setOnClickListener(onCardViewClickListener);

            }

        }


        final String pictureUri = cardModel.getPictureUri();


        if (pictureUri != null) {
            final String localPictureUri = FileHelper.getStoragePath(pictureUri);
            final File file = new File(localPictureUri);

            if (!file.exists()) {
                downloadImage(localPictureUri, file, cardPicture);
            } else {

                if (file.length() > 0) {
                    setPicture(cardPicture, localPictureUri);
                } else {
                    downloadImage(cardModel.getPictureUri(), file, cardPicture);
                }

            }
        }


        cardPicture.setImageResource(cardModel.getHeaderIconId());
        cardTitle.setText(cardModel.getTitle());
        cardContent.setText(cardModel.getContent());
        cardSubContent.setText(cardModel.getSubContent());
        cardRightContent.setText(cardModel.getRightContent());

        return convertView;
    }

    private void downloadImage(final String pictureUri, final File file, final ImageView cardPicture) {
        FirebaseStorageHelper firebaseStorageHelper = new FirebaseStorageHelper();
        firebaseStorageHelper.get(pictureUri, file).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {
                    LogHelper.log("CardAdapter Loaded picture " + pictureUri);
                    setPicture(cardPicture, pictureUri);

                } else {
                    LogHelper.log("CardAdapter Failed to load picture " + pictureUri);
                }
            }
        });
    }

    public boolean setPicture(ImageView selectedPhotoImgView, String uri) {

        if (uri != null) {
            File file = new File(uri);
            if (file != null) {
                if (file.exists()) {

                    ViewGroup.LayoutParams layoutParams = selectedPhotoImgView.getLayoutParams();
                    int width = layoutParams.width;
                    int height = layoutParams.height;

                    RequestCreator requestCreator = Picasso.with(context).load(file);
                    if (width > 0 && height > 0) {
                        requestCreator.resize(width, height).centerCrop().into(selectedPhotoImgView);
                    } else {
                        requestCreator.into(selectedPhotoImgView);
                    }


                    return true;

                }


            }
        }


        return false;
    }


}
