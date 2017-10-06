package com.cynerds.cyburger.models.combos;

import java.util.Date;
import java.util.List;

/**
 * Created by comp8 on 05/10/2017.
 */

public class DailyCombo {

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String date;
    List<Combo> combos;

    public List<Combo> getCombos() {
        return combos;
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos;
    }


}
