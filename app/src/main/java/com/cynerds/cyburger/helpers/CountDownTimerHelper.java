package com.cynerds.cyburger.helpers;

import android.os.CountDownTimer;

import com.cynerds.cyburger.interfaces.CountDownTimeoutListener;

/**
 * Created by fabri on 13/11/2017.
 */

public class CountDownTimerHelper {

    public static void waitFor(final CountDownTimeoutListener countDownTimeoutListener, long time, long tick){

        if(countDownTimeoutListener!=null){
            new CountDownTimer(time, tick) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    countDownTimeoutListener.onTimeout();
                }
            }.start();
        }
    }
}
