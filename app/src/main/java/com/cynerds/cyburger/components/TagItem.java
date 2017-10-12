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
import android.widget.Toast;

import com.cynerds.cyburger.R;

/**
 * Created by comp8 on 11/10/2017.
 */

public class TagItem extends android.support.v7.widget.AppCompatTextView {
    private TextView textView;
    private Drawable newBackground;
    private boolean isAdded = false;
    private Drawable defaultBackground;
    private LayoutInflater inflater;
    private TagItemStateChangeListener tagItemStateChangeListener;

    public TagItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
    }

    public TagItem(Context context) {

        super(context);

        initializeViews(context);
    }

    public TagItemStateChangeListener getTagItemStateChangeListener() {
        return tagItemStateChangeListener;
    }

    public void setTagItemStateChangeListener(TagItemStateChangeListener tagItemStateChangeListener) {
        this.tagItemStateChangeListener = tagItemStateChangeListener;
    }

    public boolean isAdded() {
        return isAdded;
    }

    private void initializeViews(final Context context) {
        inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        textView = (TextView) inflater.inflate(R.layout.component_tag, null);
        defaultBackground = textView.getBackground();

        int accentColor = ContextCompat.getColor(getContext(), R.color.colorAccent);
        newBackground = textView.getBackground().getConstantState().newDrawable();
        newBackground.setColorFilter(accentColor, PorterDuff.Mode.ADD);

        toggleAdded();

        textView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (textView.getRight() - textView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {


                        toggleAdded();
                        return true;
                    }
                }
                return false;
            }


        });
    }

    private void toggleAdded() {

        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_action_add, 0);
        textView.setBackground(defaultBackground);

        if (!isAdded) {

            Toast.makeText(getContext(), "Adicionar item", Toast.LENGTH_SHORT).show();
        } else {


            Toast.makeText(getContext(), "Remover item", Toast.LENGTH_SHORT).show();
        }

        isAdded = !isAdded;

        if (tagItemStateChangeListener != null) {
            tagItemStateChangeListener.onTagItemStateChanged(this);
        }
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public TextView getTextView() {
        return textView;
    }

    public interface TagItemStateChangeListener {

        void onTagItemStateChanged(TagItem item);
    }
}
