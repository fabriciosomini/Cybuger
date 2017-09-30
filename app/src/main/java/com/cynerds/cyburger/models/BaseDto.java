package com.cynerds.cyburger.models;

/**
 * Created by fabri on 08/07/2017.
 */

public abstract class BaseDto {

    private String id;
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
