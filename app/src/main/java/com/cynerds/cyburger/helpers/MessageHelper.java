package com.cynerds.cyburger.helpers;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.cynerds.cyburger.components.MessageToast;
import com.cynerds.cyburger.models.general.MessageType;

/**
 * Created by fabri on 25/10/2017.
 */

public class MessageHelper {


    public  interface OnMessageDismissListener
    {
        public void onDismiss();
    }


    public static void show(Context context, MessageType messageType, String message) {


        show(context, messageType, message,null);

    }

    public static void show(Context context, MessageType messageType, String message, final OnMessageDismissListener onMessageDismissListener) {
        MessageToast messageToast = new MessageToast(context);
        messageToast.setType(messageType);
        messageToast.setText(message);

        final Toast toast  = new Toast(context);
        toast.setView(messageToast);


        LogHelper.log("Show MessageToast:" + String.valueOf(toast.getDuration()));

         new CountDownTimer(4000, 1000) {
            @Override
            public void onFinish() {

                toast.show();

                if(onMessageDismissListener!=null){
                    onMessageDismissListener.onDismiss();
                }

            }

            @Override
            public void onTick(long millisUntilFinished) {


                toast.show();



            }
        }.start();
    }


}
