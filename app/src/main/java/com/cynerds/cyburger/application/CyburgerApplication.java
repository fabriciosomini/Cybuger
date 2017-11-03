package com.cynerds.cyburger.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.View;

import com.cynerds.cyburger.data.FirebaseRealtimeDatabaseHelper;
import com.cynerds.cyburger.handlers.ApplicationLifecycleHandler;
import com.cynerds.cyburger.helpers.DialogAction;
import com.cynerds.cyburger.helpers.DialogManager;
import com.cynerds.cyburger.helpers.LogHelper;
import com.cynerds.cyburger.helpers.OnFatalErrorListener;
import com.cynerds.cyburger.models.profile.Profile;
import com.cynerds.cyburger.models.report.CrashReport;
import com.cynerds.cyburger.models.roles.Role;
import com.google.firebase.database.FirebaseDatabase;

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

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        registerActivityLifecycleCallbacks(new ApplicationLifecycleHandler());
        onFatalErrorListener = new OnFatalErrorListener() {
            @Override
            public void onFatalError(final Context context, final Exception ex) {
                final Activity activity =  ((Activity)(context));

                DialogAction sendToDevsDialogAction = new DialogAction();
                sendToDevsDialogAction.setPositiveAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseRealtimeDatabaseHelper<CrashReport> crashReportFirebaseRealtimeDatabaseHelper
                                = new FirebaseRealtimeDatabaseHelper(CrashReport.class);

                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        String sStackTrace = sw.toString(); // stack trace as a string

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                        String date = simpleDateFormat.format(new Date());

                        String userId ="not_logged_in";

                        if(profile!=null){
                            userId = profile.getUserId();
                        }

                        CrashReport crashReport = new CrashReport();
                        crashReport.setActivityName(activity.getClass().getSimpleName());
                        crashReport.setUserId(userId);
                        crashReport.setErrorMessage(ex.getMessage());
                        crashReport.setStackTrace(sStackTrace);
                        crashReport.setDate(date);

                        crashReportFirebaseRealtimeDatabaseHelper.insert(crashReport);
                        activity.finishAffinity();
                    }
                });

                sendToDevsDialogAction.setNegativeAction(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       activity.finishAffinity();
                    }
                });
                if(context!=null){
                    DialogManager fatalErrorDialogManager = new DialogManager(context,
                            DialogManager.DialogType.YES_NO);
                    fatalErrorDialogManager.setAction(sendToDevsDialogAction);
                    fatalErrorDialogManager.showDialog("Erro","Ocorreu um erro fatal e aplicação será encerrada."
                            +" Você deseja enviar este erro para os desenvolvedores avaliarem o que houve?");
                }else{

                }

            }
        };



    }



}
