package com.cynerds.cyburger.helpers;

import android.content.Context;

/**
 * Created by comp8 on 09/08/2017.
 */

public class StringImporter {

    public static String getStringFromResource(Context context, String resourceId) {
        if (context != null) {
            if (resourceId != null) {

                String idStr = resourceId.replace("@", "");

                try{
                    int id = Integer.valueOf(idStr);
                    resourceId = context.getString(id);

                }catch(NumberFormatException ex){}

            }
        }
        return resourceId;
    }

}
