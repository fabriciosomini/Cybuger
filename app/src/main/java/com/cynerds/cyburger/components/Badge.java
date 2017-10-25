package com.cynerds.cyburger.components;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cynerds.cyburger.R;

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

       if(count>-1)
       {
           badgeCountText.setText(String.valueOf(count));
       }

    }
}
