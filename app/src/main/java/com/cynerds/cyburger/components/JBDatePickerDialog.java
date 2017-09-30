package com.cynerds.cyburger.components;

import android.content.Context;

import com.cynerds.cyburger.components.base.BaseDatePickerDialog;

/**
 * Created by fabri on 13/08/2017.
 */

public class JBDatePickerDialog extends BaseDatePickerDialog {


    public JBDatePickerDialog(Context context, DatePickerType type) {
        super(context);
        setDatePickerType(type);

    }
}
