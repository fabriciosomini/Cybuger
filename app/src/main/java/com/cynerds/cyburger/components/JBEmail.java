package com.cynerds.cyburger.components;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

import com.cynerds.cyburger.components.base.BaseComponent;

/**
 * Created by fabri on 13/08/2017.
 */

public class JBEmail extends BaseComponent<EditText> {


    public JBEmail(Context context, AttributeSet attrs) {
        super(context, attrs);

        component = new EditText(getContext());
        ((EditText) component).setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
    }


}
