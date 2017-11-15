package com.cynerds.cyburger.interfaces;

import com.cynerds.cyburger.models.general.FirebaseRealtimeDatabaseResult;

/**
 * Created by fabri on 15/11/2017.
 */

public interface OnCompleteFirebaseOperationListener {

    public void onSucess(FirebaseRealtimeDatabaseResult firebaseRealtimeDatabaseResult);
    public void onError(FirebaseRealtimeDatabaseResult firebaseRealtimeDatabaseResult);

}
