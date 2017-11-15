package com.cynerds.cyburger.models.parameters;

import com.cynerds.cyburger.models.general.BaseModel;

/**
 * Created by fabri on 15/11/2017.
 */

public class Parameters  extends BaseModel{
    private float baseAmount;
    private int basePoints;

    public float getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(float baseAmount) {
        this.baseAmount = baseAmount;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(int basePoints) {
        this.basePoints = basePoints;
    }

    public int getBaseExchangePoints() {
        return baseExchangePoints;
    }

    public void setBaseExchangePoints(int baseExchangePoints) {
        this.baseExchangePoints = baseExchangePoints;
    }

    public float getBaseExchangeAmount() {
        return baseExchangeAmount;
    }

    public void setBaseExchangeAmount(float baseExchangeAmount) {
        this.baseExchangeAmount = baseExchangeAmount;
    }

    private int baseExchangePoints;
    private float baseExchangeAmount;

}
