package com.cynerds.cyburger.models.general;


import com.cynerds.cyburger.helpers.FirebaseDatabaseHelper;

/**
 * Created by fabri on 08/07/2017.
 */


public class FirebaseRealtimeDatabaseResult {

    private FirebaseDatabaseHelper.DatabaseOperationResultType resultType;
    private String message;

    public FirebaseDatabaseHelper.DatabaseOperationResultType getResultType() {
        return resultType;
    }

    public void setResultType(FirebaseDatabaseHelper.DatabaseOperationResultType resultType) {
        this.resultType = resultType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean sucess() {
        return resultType == FirebaseDatabaseHelper.DatabaseOperationResultType.SUCCESS;
    }
}
