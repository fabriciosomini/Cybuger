package com.cynerds.cyburger.components;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import com.cynerds.cyburger.components.base.BaseComponent;

/**
 * Created by fabri on 13/08/2017.
 */

public class JBNumber extends BaseComponent<EditText> {
    public JBNumber(Context context, AttributeSet attrs) {
        super(context, attrs);

        component = new EditText(getContext());
        ((EditText) component).setInputType(InputType.TYPE_CLASS_NUMBER);
    }
}
