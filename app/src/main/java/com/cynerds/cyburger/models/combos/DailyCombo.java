package com.cynerds.cyburger.models.combos;

import com.cynerds.cyburger.models.BaseModel;

import java.util.List;

/**
 * Created by comp8 on 05/10/2017.
 */

public class DailyCombo extends BaseModel {

    private ComboDay comboDay;
    private List<Combo> combos;

    public ComboDay getComboDay() {
        return comboDay;
    }

    public void setComboDay(ComboDay comboDay) {
        this.comboDay = comboDay;
    }

    public List<Combo> getCombos() {
        return combos;
    }

    public void setCombos(List<Combo> combos) {
        this.combos = combos;
    }

    public enum ComboDay {
        SEGUNDA,
        TERCA,
        QUARTA,
        QUINTA,
        SEXTA,
        SABADO,
        DOMINGO

    }


}
