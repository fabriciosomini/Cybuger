package com.cynerds.cyburger.models.combos;

import com.cynerds.cyburger.models.foodmenu.Item;

import java.util.List;

/**
 * Created by comp8 on 05/10/2017.
 */

public class Combo {

    public List<Item> getComboItems() {
        return comboItems;
    }

    public void setComboItems(List<Item> comboItems) {
        this.comboItems = comboItems;
    }

    private List<Item> comboItems;
}
