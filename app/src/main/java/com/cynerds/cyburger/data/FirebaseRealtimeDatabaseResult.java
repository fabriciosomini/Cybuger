package com.cynerds.cyburger.data;

/**
 * Created by fabri on 08/07/2017.
 */


public class FirebaseRealtimeDatabaseResult {

    private FirebaseRealtimeDatabaseHelper.DatabaseOperationResultType resultType;
    private String message;

    public FirebaseRealtimeDatabaseHelper.DatabaseOperationResultType getResultType() {
        return resultType;
    }

    public void setResultType(FirebaseRealtimeDatabaseHelper.DatabaseOperationResultType resultType) {
        this.resultType = resultType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean sucess() {
        return resultType == FirebaseRealtimeDatabaseHelper.DatabaseOperationResultType.SUCCESS;
    }
}
