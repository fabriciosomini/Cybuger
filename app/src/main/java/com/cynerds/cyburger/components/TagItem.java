package com.cynerds.cyburger.components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cynerds.cyburger.R;

import java.util.Random;

/**
 * Created by comp8 on 11/10/2017.
 */

public class TagItem extends android.support.v7.widget.AppCompatTextView {
    private TextView textView;
    private Drawable newBackground;
    private boolean isAdded = false;
    private LayoutInflater inflater;
    private TagItemStateChangeListener tagItemStateChangeListener;
    private int randomColor;

    public TagItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
    }

    public TagItem(Context context) {

        super(context);

        initializeViews(context);
    }


    public void setTagItemStateChangeListener(TagItemStateChangeListener tagItemStateChangeListener) {
        this.tagItemStateChangeListener = tagItemStateChangeListener;
    }


    private void initializeViews(final Context context) {
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        textView = (TextView) inflater.inflate(R.layout.component_tag, null);

        int accentColor = getRandomColor();
        newBackground = textView.getBackground().getConstantState().newDrawable();
        newBackground.setColorFilter(accentColor, PorterDuff.Mode.ADD);
        textView.setBackground(newBackground);

        int white = ContextCompat.getColor(getContext(), R.color.white);
        Drawable mDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_action_close);
        mDrawable.setColorFilter(white, PorterDuff.Mode.SRC_IN);
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, mDrawable, null);
        textView.setTextColor(white);

        textView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (textView.getRight() - textView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                        isAdded = !isAdded;

                        if (tagItemStateChangeListener != null) {
                            tagItemStateChangeListener.onTagItemStateChanged();
                        }

                        return true;
                    }
                }
                return false;
            }


        });
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public TextView getTextView() {
        return textView;
    }

    public int getRandomColor() {

        Random r = new Random();
        int i1 = r.nextInt(4 - 1) + 1;

        int randomColor = i1;

        switch (randomColor) {
            case 1:
                randomColor = ContextCompat.getColor(getContext(), R.color.randomColor0);
                break;
            case 2:
                randomColor = ContextCompat.getColor(getContext(), R.color.randomColor1);
                break;
            case 3:
                randomColor = ContextCompat.getColor(getContext(), R.color.randomColor2);
                break;

        }

        return randomColor;
    }

    public interface TagItemStateChangeListener {

        void onTagItemStateChanged();
    }
}
