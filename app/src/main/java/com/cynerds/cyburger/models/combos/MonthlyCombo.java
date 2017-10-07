package com.cynerds.cyburger.models.combos;

import com.cynerds.cyburger.models.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 05/10/2017.
 */

public class MonthlyCombo extends BaseModel {


    private List<DailyCombo> monthlyCombos = new ArrayList<>();

    public List<DailyCombo> getMonthlyCombos() {
        return monthlyCombos;
    }

    public void setMonthlyCombos(List<DailyCombo> monthlyCombos) {
        this.monthlyCombos = monthlyCombos;
    }


}
