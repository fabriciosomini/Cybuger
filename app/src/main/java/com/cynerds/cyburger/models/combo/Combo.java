package com.cynerds.cyburger.models.combo;

import com.cynerds.cyburger.models.general.BaseModel;
import com.cynerds.cyburger.models.item.Item;

import java.util.List;

/**
 * Created by comp8 on 05/10/2017.
 */

public class Combo extends BaseModel {
    private List<Item> comboItems;
    private String comboName;
    private String comboInfo;
    private float comboAmount;
    private ComboDay comboDay;
    private int comboBonusPoints;
    private String pictureUri;
    private int comboSpentPoints;

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }

    public String getComboInfo() {
        return comboInfo;
    }

    public void setComboInfo(String comboInfo) {
        this.comboInfo = comboInfo;
    }

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

    public int getComboBonusPoints() {
        return comboBonusPoints;
    }

    public void setComboBonusPoints(int comboBonusPoints) {
        this.comboBonusPoints = comboBonusPoints;
    }

    public int getComboSpentPoints() {
        return comboSpentPoints;
    }

    public void setComboSpentPoints(int comboSpentPoints) {
        this.comboSpentPoints = comboSpentPoints;
    }
}
