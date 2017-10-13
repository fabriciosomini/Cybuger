package com.cynerds.cyburger.models.orders;

import com.cynerds.cyburger.models.BaseModel;
import com.cynerds.cyburger.models.combos.Combo;
import com.cynerds.cyburger.models.customer.Customer;
import com.cynerds.cyburger.models.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 07/10/2017.
 */

public class Order extends BaseModel {

    private Customer customer;
    private List<Combo> orderedCombos;
    private List<Item> orderedItems;

    public Order() {
        orderedCombos = new ArrayList<>();
        orderedItems = new ArrayList<>();
    }

    public List<Combo> getOrderedCombos() {
        return orderedCombos;
    }

    public void setOrderedCombos(List<Combo> orderedCombos) {
        this.orderedCombos = orderedCombos;
    }

    public List<Item> getOrderedItems() {
        return orderedItems;
    }

    public void setOrderedItems(List<Item> orderedItems) {
        this.orderedItems = orderedItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


}
