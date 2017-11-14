package com.cynerds.cyburger.models.sync;

import com.cynerds.cyburger.models.general.BaseModel;

import java.util.Date;

/**
 * Created by fabri on 12/11/2017.
 */

public class Sync extends BaseModel {

    private Date lastSyncedDate;
    private boolean isSynced;

    public Date getLastSyncedDate() {
        return lastSyncedDate;
    }

    public void setLastSyncedDate(Date lastSyncedDate) {
        this.lastSyncedDate = lastSyncedDate;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
