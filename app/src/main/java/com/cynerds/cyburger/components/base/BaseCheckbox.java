package com.cynerds.cyburger.components.base;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.StringImporter;

/**
 * Created by fabri on 21/07/2017.
 */

public class BaseCheckbox extends ConstraintLayout {

    private final String attributeText;

    private CheckBox checkbox;
    private String text;

    public BaseCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);

        String namespace = "http://schemas.android.com/apk/res-auto";
        attributeText = StringImporter.getStringFromResource(context, attrs.getAttributeValue(namespace, "checkboxText"));
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.checkbox_base, this);
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public void setButtonText(String text) {
        this.text = text;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkbox = (CheckBox) findViewById(R.id.baseCheckbox);

        if (attributeText != null) {
            checkbox.setText(attributeText);
        }


    }
}
