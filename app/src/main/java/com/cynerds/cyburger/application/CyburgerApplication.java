package com.cynerds.cyburger.application;

import android.app.Application;
import android.content.Context;


import com.cynerds.cyburger.R;
import com.cynerds.cyburger.activities.BaseActivity;
import com.cynerds.cyburger.helpers.AuthenticationHelper;
import com.cynerds.cyburger.helpers.DateHelper;
import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;
import com.cynerds.cyburger.handlers.ApplicationLifecycleHandler;
import com.cynerds.cyburger.helpers.CountDownTimerHelper;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.MessageHelper;
import com.cynerds.cyburger.interfaces.CountDownTimeoutListener;
import com.cynerds.cyburger.interfaces.OnFatalErrorListener;
import com.cynerds.cyburger.interfaces.OnDataChangeListener;
import com.cynerds.cyburger.interfaces.OnSyncResultListener;
import com.cynerds.cyburger.models.account.UserAccount;
import com.cynerds.cyburger.models.general.MessageType;
import com.cynerds.cyburger.models.parameters.Parameters;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.report.CrashReport;
import com.cynerds.cyburger.models.role.Role;
import com.cynerds.cyburger.models.sync.Sync;
import com.firebase.ui.auth.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fabri on 30/09/2017.
 */

public class CyburgerApplication extends Application {

    public static OnFatalErrorListener onFatalErrorListener;
    public static boolean autoLogin = true;
    private static Profile profile;
    private static boolean isSynced;
    private static List<OnDataChangeListener> onDataChangeListeners;
    private static Parameters parameters;
    private static UserAccount userAccount;
    private static OnSyncResultListener onSyncResultListener;
    private static Context context;
    private static boolean syncNotified;
    private static Sync sync;
    private static FirebaseDatabaseHelper<Profile> firebaseDatabaseHelperProfile;
    private static boolean offlineMode;

    public static Parameters getParameters() {
        return parameters;
    }

    public static void setParameters(Parameters parameters) {
        CyburgerApplication.parameters = parameters;
    }

    public static Sync getSync() {
        return sync;
    }

    public static UserAccount getUserAccount() {
        return userAccount;
    }

    public static void setUserAccount(UserAccount userAccount) {
        CyburgerApplication.userAccount = userAccount;
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
            topicName = context.getString(R.string.prefix_cyburger) + userId;
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

    public static void addListenerToNotify(OnDataChangeListener onDataChangeListener) {
        onDataChangeListeners.add(onDataChangeListener);
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

            final FirebaseDatabaseHelper<Sync> firebaseDatabaseHelper = new FirebaseDatabaseHelper(Sync.class);

            firebaseDatabaseHelper.setOnDataChangeListener(new OnDataChangeListener() {
                @Override
                public void onDatabaseChanges() {
                    if (firebaseDatabaseHelper.get().size() > 0) {
                        sync = firebaseDatabaseHelper.get().get(0);

                        if (sync != null) {
                            isSynced = sync.isSynced();
                            setApplicationParameters();
                        }

                    }

                }

                @Override
                public void onCancel() {


                }


            });


            CountDownTimerHelper.waitFor(new CountDownTimeoutListener() {
                @Override
                public void onTimeout() {
                    if (!syncNotified) {
                        firebaseDatabaseHelper.removeListenters();

                        if (CyburgerApplication.onSyncResultListener != null) {
                            onSyncResultListener.onSyncResult(false);
                            CyburgerApplication.onSyncResultListener = null;
                        }

                    }


                }
            }, 15000, 50);


        }
    }

    private static void createProfileWatcher() {

        if(onDataChangeListeners == null)
        {
            onDataChangeListeners = new ArrayList<>();
        }

        if(firebaseDatabaseHelperProfile == null){
            firebaseDatabaseHelperProfile = new FirebaseDatabaseHelper(Profile.class);
            firebaseDatabaseHelperProfile.setOnDataChangeListener(new OnDataChangeListener() {
                @Override
                public void onDatabaseChanges() {

                    for (Profile profile :
                            firebaseDatabaseHelperProfile.get()) {
                        if (profile != null) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user!=null){
                                if(user.getUid().equals(profile.getUserId()))
                                {
                                    LogHelper.log("Profile change detected. Updating: " + profile.getUserId());
                                    CyburgerApplication.profile = profile;
                                }
                            }


                        }
                    }

                    for (OnDataChangeListener onDataChangeListener :
                            onDataChangeListeners) {
                        if (onDataChangeListener != null) {
                            onDataChangeListener.onDatabaseChanges();
                        }
                    }

                }

                @Override
                public void onCancel() {

                }
            });
        }

    }

    private static void setApplicationParameters() {
        final FirebaseDatabaseHelper<Parameters> firebaseDatabaseHelperParameters
                = new FirebaseDatabaseHelper(context, Parameters.class);

        firebaseDatabaseHelperParameters.setOnDataChangeListener(new OnDataChangeListener() {
            @Override
            public void onDatabaseChanges() {
                List<Parameters> parametersList = firebaseDatabaseHelperParameters.get();

                Parameters parameters = null;

                if (parametersList.size() > 0) {
                    parameters = parametersList.get(0);
                }
                if (parameters != null) {

                    CyburgerApplication.setParameters(parameters);

                    syncNotified = true;
                    if (CyburgerApplication.onSyncResultListener != null) {

                        onSyncResultListener.onSyncResult(isSynced);
                        CyburgerApplication.onSyncResultListener = null;
                    }



                    for (OnDataChangeListener onDataChangeListener :
                            onDataChangeListeners) {
                        if (onDataChangeListener != null) {
                            onDataChangeListener.onDatabaseChanges();
                        }
                    }


                }
            }

            @Override
            public void onCancel() {

            }
        });

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

        createProfileWatcher();
    }

    private void reportError(Exception ex, String activityName) {
        FirebaseDatabaseHelper<CrashReport> crashReportFirebaseDatabaseHelper
                = new FirebaseDatabaseHelper(CrashReport.class);

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

        crashReportFirebaseDatabaseHelper.insert(crashReport);

    }
}
