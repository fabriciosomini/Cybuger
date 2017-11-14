package com.cynerds.cyburger.application;

import android.app.Application;
import android.content.Context;


import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.data.FirebaseDatabaseManager;
import com.cynerds.cyburger.handlers.ApplicationLifecycleHandler;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.interfaces.OnFatalErrorListener;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.interfaces.OnSyncResultListener;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.report.CrashReport;
import com.cynerds.cyburger.models.role.Role;
import com.cynerds.cyburger.models.sync.Sync;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fabri on 30/09/2017.
 */

public class CyburgerApplication extends Application {

    public static OnFatalErrorListener onFatalErrorListener;
    public static boolean autoLogin = true;
    private static Profile profile;

    private static Credential credential;
    private static OnSyncResultListener onSyncResultListener;
    private static Context context;

    public static Credential getCredential() {
        return credential;
    }

    public static void setCredential(Credential credential) {
        CyburgerApplication.credential = credential;
    }

    public static Profile getProfile() {
        return profile;
    }

    public static void setProfile(Profile profile) {
        LogHelper.log("Set the new CyburgerApplication profile!");

        CyburgerApplication.profile = profile;
    }

    public static boolean isAdmin() {

        return profile != null && profile.getRole() == Role.ADMIN;
    }


    public static String getUserTopicName() {

        String topicName = "";
        if (profile != null) {
            String userId = profile.getUserId();
            topicName = "cyburger-" + userId;
        }

        return topicName;
    }

    public static void subscribeToUserTopic() {

        if (profile != null) {
            FirebaseMessaging.getInstance().subscribeToTopic(getUserTopicName());
        }


    }

    public static void unsubscribeToUserTopic() {

        if (profile != null) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(getUserTopicName());
        }


    }

    public static void setOnSyncResultListener(OnSyncResultListener onSyncResultListener) {
        if (onSyncResultListener != null) {
            if (CyburgerApplication.onSyncResultListener == null) {
                CyburgerApplication.onSyncResultListener = onSyncResultListener;

                if (CyburgerApplication.onSyncResultListener != null) {
                    getSyncResponse(onSyncResultListener);
                }
            } else {

                LogHelper.log("There's already a onSyncResultListener");
            }
        }

    }

    private static void getSyncResponse(final OnSyncResultListener onSyncResultListener) {
        if (onSyncResultListener != null) {

            final FirebaseDatabaseManager<Sync> firebaseDatabaseManager = new FirebaseDatabaseManager(Sync.class);

            firebaseDatabaseManager.setOnDataChangeListener(new OnDataChangeListener() {
                @Override
                public void onDataChanged() {
                    if(firebaseDatabaseManager.get().size()>0){
                        Sync sync = firebaseDatabaseManager.get().get(0);
                        boolean isSynced = sync.isSynced();
                        onSyncResultListener.onSyncResult(isSynced);

                    }

                }

                @Override
                public void onCancel() {
                    onSyncResultListener.onSyncResult(false);
                }


            });


        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();

        FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
        firebaseInstance.setPersistenceEnabled(true);

        registerActivityLifecycleCallbacks(new ApplicationLifecycleHandler());
        onFatalErrorListener = new OnFatalErrorListener() {
            @Override
            public void onFatalError(final Context context, final Exception ex) {

                if (context != null) {
                    final BaseActivity activity = ((BaseActivity) (context));


                    reportError(ex, activity.getClass().getSimpleName());


                    MessageHelper.OnMessageDismissListener onDismissListener = new MessageHelper.OnMessageDismissListener() {
                        @Override
                        public void onDismiss() {
                            activity.finishApplication();
                        }
                    };

                    MessageHelper.show(activity, MessageType.ERROR, "Erro interno", onDismissListener);


                } else {

                }

            }
        };
    }

    private void reportError(Exception ex, String activityName) {
        FirebaseDatabaseManager<CrashReport> crashReportFirebaseDatabaseManager
                = new FirebaseDatabaseManager(CrashReport.class);

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

        crashReportFirebaseDatabaseManager.insert(crashReport);

    }
}
