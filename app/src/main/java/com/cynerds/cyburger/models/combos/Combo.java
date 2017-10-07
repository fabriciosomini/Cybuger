package com.cynerds.cyburger.models.combos;

import com.cynerds.cyburger.models.BaseModel;
import com.cynerds.cyburger.models.foodmenu.Item;

import java.util.List;

/**
 * Created by comp8 on 05/10/2017.
 */

public class Combo extends BaseModel {
    private List<Item> comboItems;
    private String comboName;

    public String getComboName() {
        return comboName;
    }

    public void setComboName(String comboName) {
        this.comboName = comboName;
    }

    public List<Item> getComboItems() {
        return comboItems;
    }

    public void setComboItems(List<Item> comboItems) {
        this.comboItems = comboItems;
    }


}
