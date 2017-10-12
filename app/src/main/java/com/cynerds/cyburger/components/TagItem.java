package com.cynerds.cyburger.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cynerds.cyburger.R;

/**
 * Created by comp8 on 11/10/2017.
 */

public class TagItem extends android.support.v7.widget.AppCompatTextView {
    private TextView textView;

    public TagItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
    }

    public TagItem(Context context)
    {

        super(context);

        initializeViews(context);
    }

    private void initializeViews(final Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        textView = (TextView) inflater.inflate(R.layout.component_tag, null);

        int rightDrawable = R.drawable.ic_action_add;
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,rightDrawable,0);
        textView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (textView.getRight() - textView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        Toast.makeText(context, "Adicionar item", Toast.LENGTH_SHORT).show();

                        return true;
                    }
                }
                return false;
            }


        });
    }

    public void setText(String text){

        text = text + "       ";
        textView.setText(text);
    }

    public TextView getTextView() {
        return textView;
    }
}
