package com.cynerds.cyburger.models.combos;

import com.cynerds.cyburger.models.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 05/10/2017.
 */

public class MonthlyCombo extends BaseModel {


    private List<Combo> combos = new ArrayList<>();

    public List<Combo> getCombos() {
        return combos;
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos;
    }


}
