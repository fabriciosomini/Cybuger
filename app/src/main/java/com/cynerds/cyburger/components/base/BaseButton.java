package com.cynerds.cyburger.components.base;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.StringImporter;

/**
 * Created by fabri on 14/07/2017.
 */

public abstract class BaseButton<T> extends ConstraintLayout {

    private  String attributeText;
    private  String attributeType;
    private Button button;
    private String text;

    public BaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        String namespace = "http://schemas.android.com/apk/res-auto";

        attributeText = attrs.getAttributeValue(namespace, "buttonText");
        attributeType = attrs.getAttributeValue(namespace, "type");

        attributeText = StringImporter.getStringFromResource(getContext(), attributeText);


        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.button_base, this);
    }

    public Button getButton() {
        return button;
    }

    public void setButtonText(String text) {
        this.text = text;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        button = (Button) findViewById(R.id.baseButton);

        if (attributeText != null) {
            button.setText(attributeText);
        }

        if (attributeType != null) {

            int color = 0;


            if (color != 0) {
                button.setBackgroundColor(color);


            }
        }

    }
}
