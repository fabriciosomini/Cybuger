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
    private float comboAmount;
    private ComboDay comboDay;

    public float getComboAmount() {
        return comboAmount;
    }

    public void setComboAmount(float comboAmount) {
        this.comboAmount = comboAmount;
    }

    public ComboDay getComboDay() {
        return comboDay;
    }

    public void setComboDay(ComboDay comboDay) {
        this.comboDay = comboDay;
    }

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
