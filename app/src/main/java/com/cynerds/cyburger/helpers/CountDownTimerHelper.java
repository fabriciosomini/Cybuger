package com.cynerds.cyburger.helpers;

import android.os.CountDownTimer;

import com.cynerds.cyburger.interfaces.OnTimeoutListener;

/**
 * Created by fabri on 13/11/2017.
 */

public class CountDownTimerHelper {

    public static void waitFor(final OnTimeoutListener onTimeoutListener, long time, long tick){

        if(onTimeoutListener !=null){
            new CountDownTimer(time, tick) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    onTimeoutListener.onTimeout();
                }
            }.start();
        }
    }
}
