package com.cynerds.cyburger.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.cynerds.cyburger.components.base.BaseComponent;

/**
 * Created by fabri on 11/07/2017.
 */

public class JBSpinner extends BaseComponent<Spinner> {


    public JBSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        component = new Spinner(getContext());

        ((Spinner) component).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstTime = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (isFirstTime) {
                    isFirstTime = false;


                } else {
                    finalWorkspaceActivity.addDirty(baseCompoenent);
                    componentValue = ((Spinner) component).getSelectedItem();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                finalWorkspaceActivity.removeDirty(baseCompoenent);
                componentValue = null;
            }
        });
    }
}
