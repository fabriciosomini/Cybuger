package com.cynerds.cyburger.models.combos;

import com.cynerds.cyburger.models.BaseModel;

import java.util.List;

/**
 * Created by comp8 on 05/10/2017.
 */

public class DailyCombo extends BaseModel {

    private String date;
    private List<Combo> combos;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Combo> getCombos() {
        return combos;
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos;
    }


}
