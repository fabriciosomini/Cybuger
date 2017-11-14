package com.cynerds.cyburger.models.general;


import com.cynerds.cyburger.data.FirebaseDatabaseManager;

/**
 * Created by fabri on 08/07/2017.
 */


public class FirebaseRealtimeDatabaseResult {

    private FirebaseDatabaseManager.DatabaseOperationResultType resultType;
    private String message;

    public FirebaseDatabaseManager.DatabaseOperationResultType getResultType() {
        return resultType;
    }

    public void setResultType(FirebaseDatabaseManager.DatabaseOperationResultType resultType) {
        this.resultType = resultType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean sucess() {
        return resultType == FirebaseDatabaseManager.DatabaseOperationResultType.SUCCESS;
    }
}
