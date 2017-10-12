package com.cynerds.cyburger.components;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.cynerds.cyburger.R;

/**
 * Created by fabri on 12/10/2017.
 */

public class Badge extends ConstraintLayout {


    TextView badgeCountText;
    private int badgeCount;

    public Badge(Context context, AttributeSet attrs) {
        super(context, attrs);

        initializeViews(context);
    }

    public Badge(Context context) {
        super(context);
        initializeViews(context);
    }


    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_badge, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        badgeCountText = findViewById(R.id.badgeCountText);

    }

    public int getBadgeCount() {
        return badgeCount;
    }

    public void setBadgeCount(int count) {

        this.badgeCount = count;
        badgeCountText.setText(String.valueOf(count));
    }
}
