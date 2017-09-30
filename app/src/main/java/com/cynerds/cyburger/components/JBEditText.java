package com.cynerds.cyburger.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.cynerds.cyburger.components.base.BaseComponent;

/**
 * Created by fabri on 11/07/2017.
 */

public class JBEditText extends BaseComponent<EditText> {


    public JBEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        component = new EditText(getContext());

    }


}
