package com.cynerds.cyburger.components;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.helpers.LogHelper;


/**
 * Created by fabri on 12/10/2017.
 */

public class Badge extends ConstraintLayout {


    TextView badgeCountText;
    private int badgeCount;
    private View badge;
    private LayoutInflater inflater;


    public Badge(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
    }

    public Badge(Context context) {
        super(context);
        initializeViews(context);
    }


    private void initializeViews(Context context) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        badge = inflater.inflate(R.layout.component_badge, this);

    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        badgeCountText = badge.findViewById(R.id.badgeCountText);

    }


    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int count) {

        this.badgeCount = count;

        badge.clearAnimation();

        if (count < 1) {
            LogHelper.error("Hide the badge");
            this.setVisibility(INVISIBLE);
            this.refreshDrawableState();
        } else {
            LogHelper.error("Show the badge");
            this.setVisibility(VISIBLE);
            badgeCountText.setText(String.valueOf(count));
            animateSize();
        }

    }

    private void animateSize() {

        Animation anim = new ScaleAnimation(
                1f, 1.25f, // Start and end values for the X axis scaling
                1f, 1.25f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.25f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(50);
        badge.startAnimation(anim);

        anim = new ScaleAnimation(
                1.25f, 1f, // Start and end values for the X axis scaling
                1.25f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.25f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(50);
        badge.startAnimation(anim);

    }
}
