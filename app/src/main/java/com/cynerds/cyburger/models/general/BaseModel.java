package com.cynerds.cyburger.models.general;

import com.cynerds.cyburger.helpers.GsonHelper;
import com.cynerds.cyburger.models.combo.Combo;

/**
 * Created by fabri on 06/10/2017.
 */

public class BaseModel {
    private String id;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public Object copyValues(Class type) {

        String json = GsonHelper.ToGson(this);
        return GsonHelper.ToObject(type, json);
    }
}
