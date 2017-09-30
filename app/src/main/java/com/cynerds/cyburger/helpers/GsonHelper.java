package com.cynerds.cyburger.helpers;

import com.google.gson.Gson;

/**
 * Created by fabri on 26/07/2017.
 */

public class GsonHelper {
    public static String ToGson(Object object) {
        Gson gson = new Gson();
        String jsonRepresentation = gson.toJson(object);

        return jsonRepresentation;

    }

    public static Object ToObject(Class type, String serial) {
        Gson gson = new Gson();
        return gson.fromJson(serial, type);
    }
}
