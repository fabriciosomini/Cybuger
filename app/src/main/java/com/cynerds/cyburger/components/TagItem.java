package com.cynerds.cyburger.components;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.cynerds.cyburger.R;

/**
 * Created by comp8 on 11/10/2017.
 */

public class TagItem extends ConstraintLayout {
    private EditText editText;

    public TagItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
    }

    public TagItem(Context context)
    {

        super(context);

        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        editText =  (EditText)inflater.inflate(R.layout.component_tag, this).findViewById(R.id.tagItem);
        editText.setFocusable(false);

        int rightDrawable = R.drawable.ic_chevron_right;
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,rightDrawable,0);
    }

    public void setText(String text){

        editText.setText(text);
    }
}
