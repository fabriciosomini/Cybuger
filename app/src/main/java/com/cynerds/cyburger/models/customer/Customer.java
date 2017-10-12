package com.cynerds.cyburger.models.customer;

import com.cynerds.cyburger.models.BaseModel;

/**
 * Created by fabri on 07/10/2017.
 */

public class Customer extends BaseModel {

    private String customerName;
    private String linkedProfileId;

    public String getLinkedProfileId() {
        return linkedProfileId;
    }

    public void setLinkedProfileId(String linkedProfileId) {
        this.linkedProfileId = linkedProfileId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
