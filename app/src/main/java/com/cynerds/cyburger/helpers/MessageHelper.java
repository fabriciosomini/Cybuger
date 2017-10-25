package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.components.MessageToast;
import com.cynerds.cyburger.models.general.MessageType;

/**
 * Created by fabri on 25/10/2017.
 */

public class MessageHelper {

    private static boolean isInitialized;
    private static MessageToast messageToast;
    private static Toast toast;
    private static Context context;

    public static void initialize(Context context) {

        if (!isInitialized) {

            MessageHelper.context = context;

            toast = new Toast(context);
            isInitialized = true;
            messageToast = new MessageToast(context);
        }


    }


    public static void show(MessageType messageType, String message) {

        messageToast.setType(messageType);
        messageToast.setText(message);
        toast.setView(messageToast);
        toast.show();
    }




}
