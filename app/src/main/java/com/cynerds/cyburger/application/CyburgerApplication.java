package com.cynerds.cyburger.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.handlers.ApplicationLifecycleHandler;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.helpers.OnFatalErrorListener;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.report.CrashReport;
import com.cynerds.cyburger.models.roles.Role;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

/**
 * Created by fabri on 30/09/2017.
 */

public class CyburgerApplication extends Application {

    public static OnFatalErrorListener onFatalErrorListener;


    private static Profile profile;

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile profile) {
        LogHelper.error("Set the new CyburgerApplication profile!");

        CyburgerApplication.profile = profile;
    }

    public static boolean isAdmin() {

        return profile != null && profile.getRole() == Role.ADMIN;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
        firebaseInstance.setPersistenceEnabled(false);

        registerActivityLifecycleCallbacks(new ApplicationLifecycleHandler());
        onFatalErrorListener = new OnFatalErrorListener() {
            @Override
            public void onFatalError(final Context context, final Exception ex) {

                if (context != null) {
                    final BaseActivity activity = ((BaseActivity) (context));


                    reportError(ex, activity.getClass().getSimpleName());


                    MessageHelper.OnMessageDismissListener onDismissListener  = new MessageHelper.OnMessageDismissListener() {
                        @Override
                        public void onDismiss() {
                            activity.finishApplication();
                        }
                    };

                    MessageHelper.show(activity, MessageType.ERROR,"Erro interno", onDismissListener);




                } else {

                }

            }
        };
    }

    private void reportError(Exception ex, String activityName) {
        FirebaseRealtimeDatabaseHelper<CrashReport> crashReportFirebaseRealtimeDatabaseHelper
                = new FirebaseRealtimeDatabaseHelper(CrashReport.class);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String sStackTrace = sw.toString(); // stack trace as a string

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        String date = simpleDateFormat.format(new Date());

        String userId = "not_logged_in";

        if (profile != null) {
            userId = profile.getUserId();
        }

        CrashReport crashReport = new CrashReport();
        crashReport.setActivityName(activityName);
        crashReport.setUserId(userId);
        crashReport.setErrorMessage(ex.getMessage());
        crashReport.setStackTrace(sStackTrace);
        crashReport.setDate(date);

        crashReportFirebaseRealtimeDatabaseHelper.insert(crashReport);

    }

    public  static String getUserTopicName (){

        String topicName = "";
        if(profile!=null) {
            String userId = profile.getUserId();
            topicName = "cyburger-" + userId;
        }

        return topicName;
    }

    public static void subscribeToUserTopic() {

            if(profile!=null){
                FirebaseMessaging.getInstance().subscribeToTopic(getUserTopicName());
            }


    }
}
