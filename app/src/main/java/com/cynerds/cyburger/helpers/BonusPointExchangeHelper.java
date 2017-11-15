package com.cynerds.cyburger.helpers;

import com.cynerds.cyburger.application.CyburgerApplication;
import com.cynerds.cyburger.models.parameters.Parameters;
import com.cynerds.cyburger.models.profile.Profile;

/**
 * Created by fabri on 15/11/2017.
 */

public class BonusPointExchangeHelper {
    public static float convertUserPointsToCash(){
        float userCacheFromPoints = 0;
        Profile profile = CyburgerApplication.getProfile();
        if(profile!=null){
            Parameters parameters = CyburgerApplication.getParameters();
            int userPoints = profile.getBonusPoints();

            if(parameters!=null){

                userCacheFromPoints = userPoints * parameters.getBaseExchangeAmount() / parameters.getBaseExchangePoints();

            }
        }

        return userCacheFromPoints;

    }

    public static float convertAmountToPoints(float amount){
        float pointsFromCache = 0;

            Parameters parameters = CyburgerApplication.getParameters();

            if(parameters!=null){

                pointsFromCache = amount * parameters.getBaseExchangePoints() / parameters.getBaseExchangeAmount();
                pointsFromCache = Math.round(pointsFromCache);

            }


        return pointsFromCache;

    }
}