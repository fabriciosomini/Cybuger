package com.cynerds.cyburger.models.customer;

import com.cynerds.cyburger.models.BaseModel;

/**
 * Created by fabri on 07/10/2017.
 */

public class Customer extends BaseModel {

    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
