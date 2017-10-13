package com.cynerds.cyburger.models;

/**
 * Created by fabri on 06/10/2017.
 */

public class BaseModel<T> implements Cloneable {
    private String id;
    private String objectKey;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public T getClone() {
        try {

            return (T) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println(" Cloning not allowed. ");
            return (T) this;
        }
    }

}
