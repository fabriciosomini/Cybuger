package com.cynerds.cyburger.interfaces;

import com.cynerds.cyburger.components.TagItem;

/**
 * Created by fabri on 13/11/2017.
 */

public interface OnItemAddedListener {

    void onAddItem(TagItem tagItem);

    void onRemoveItem(TagItem tagItem);
}