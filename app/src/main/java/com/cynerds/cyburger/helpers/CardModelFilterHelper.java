package com.cynerds.cyburger.helpers;

import com.cynerds.cyburger.models.views.CardModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabri on 02/11/2017.
 */

public class CardModelFilterHelper {

    public static List<CardModel> filterCardModel(List<CardModel> cards, String filter){
        List<CardModel> filteredCards = new ArrayList<>();

        for (CardModel cardModel :
                cards) {

            String cardTitle  = cardModel.getTitle();
            String cardContent  = cardModel.getContent();
            String cardSubContent  = cardModel.getSubContent();
            if(matches(filter,cardTitle)
                    ||matches(filter,cardContent)
                    || matches(filter,cardSubContent)){

                filteredCards.add(cardModel);

            }
        }
        return filteredCards;
    }

    private static boolean matches(String filter, String input){

        if(filter ==null || input == null){
            return false;
        }

        String[] filterArray = filter.split(" ");

        for (String str :
                filterArray) {
            if (!input.toLowerCase().contains(str.toLowerCase()))
            {
                return false;
            }
        }


        return true;
    }

}
