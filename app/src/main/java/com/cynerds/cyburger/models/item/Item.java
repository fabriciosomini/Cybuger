package com.cynerds.cyburger.models.item;

import com.cynerds.cyburger.models.general.BaseModel;

/**
 * Created by comp8 on 05/10/2017.
 */

public class Item extends BaseModel {

    private String description;
    private String ingredients;
    private float price;
    private String size;
    private int bonusPoints;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

}
