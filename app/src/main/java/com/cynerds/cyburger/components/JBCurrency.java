package com.cynerds.cyburger.components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.components.base.BaseComponent;

/**
 * Created by fabri on 13/08/2017.
 */

public class JBCurrency extends BaseComponent<EditText> {
    public JBCurrency(Context context, AttributeSet attrs) {
        super(context, attrs);

        component = inflateExternalComponent(R.layout.component_inner_currency, R.id.innerbaseCurrency);
        componentValue = "0.00";
        component.setTextAlignment(TEXT_ALIGNMENT_TEXT_END);
        ((EditText) component).setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                | android.text.InputType.TYPE_CLASS_NUMBER);

        if (attributeHint != null) {
            ((EditText) component).setHint(attributeHint);

        } else {
            ((EditText) component).setHint(componentValue.toString());
        }
    }
}
