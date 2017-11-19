package com.cynerds.cyburger.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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
import com.cynerds.cyburger.helpers.FirebaseStorageConstants;
import com.cynerds.cyburger.helpers.FirebaseStorageHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.models.view.CardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

        try {

            final String pictureUri = cardModel.getPictureUri();
            if (pictureUri != null) {
                File cacheDir = context.getCacheDir();
                File regexFileName = new File(pictureUri);
                String fileName = regexFileName.getName();

                File file = new File(cacheDir + "/"+ fileName);

                if (!file.exists()) {
                    final File localFile = File.createTempFile(fileName, "jpg", cacheDir);
                    FirebaseStorageHelper firebaseStorageHelper = new FirebaseStorageHelper();
                    firebaseStorageHelper.get(cardModel.getPictureUri(), localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){
                                LogHelper.log("CardAdapter Loaded picture " + pictureUri);
                                setImage(cardPicture, localFile);
                            }else
                            {
                                LogHelper.log("CardAdapter Failed to load picture " + pictureUri);
                            }
                        }
                    });
                } else {
                    setImage(cardPicture, file);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        cardPicture.setImageResource(cardModel.getHeaderIconId());
        cardTitle.setText(cardModel.getTitle());
        cardContent.setText(cardModel.getContent());
        cardSubContent.setText(cardModel.getSubContent());
        cardRightContent.setText(cardModel.getRightContent());

        return convertView;
    }

    private void setImage(ImageView cardPicture, File localFile) {
        FileInputStream streamIn = null;
        try {
            streamIn = new FileInputStream(localFile);
            Bitmap bitmap = BitmapFactory.decodeStream(streamIn); //This gets the image
            streamIn.close();
            cardPicture.setImageBitmap(bitmap);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
