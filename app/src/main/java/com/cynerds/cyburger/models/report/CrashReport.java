package com.cynerds.cyburger.models.report;

import com.cynerds.cyburger.models.general.BaseModel;

/**
 * Created by fabri on 02/11/2017.
 */

public class CrashReport extends BaseModel{

    public String getDate() {
        return date;
    }

    private String date;

    public String getActivityName() {
        return activityName;
    }

    private String activityName;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    private String errorMessage;
    private String stackTrace;

    public String getUserId() {
        return userId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }


    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
