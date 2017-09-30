package com.cynerds.cyburger.components;

import android.content.Context;
import android.util.AttributeSet;

import com.cynerds.cyburger.components.base.BaseButtonGroup;

/**
 * Created by fabri on 25/07/2017.
 */

public class JBThreeButtonsButtonGroup extends BaseButtonGroup {
    public JBThreeButtonsButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setButtonGroupType(ButtonGroupType.THREE);
    }
}
