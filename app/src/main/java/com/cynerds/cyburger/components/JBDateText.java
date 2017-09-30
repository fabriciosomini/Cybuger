package com.cynerds.cyburger.components;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.components.base.BaseComponent;
import com.cynerds.cyburger.components.base.BaseDatePickerDialog;
import com.cynerds.cyburger.helpers.DateHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by fabri on 13/08/2017.
 */

public class JBDateText extends BaseComponent<EditText> {

    private String attributeDatePickerStyle;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private BaseDatePickerDialog.DatePickerType calendarType;

    public JBDateText(Context context, AttributeSet attrs) {
        super(context, attrs);


        component = inflateExternalComponent(R.layout.component_inner_date, R.id.innerbaseDate);

        component.setFocusable(false);
        component.setClickable(false);

        attributeDatePickerStyle = attrs.getAttributeValue(namespace, "datePickerStyle");

        attributeDatePickerStyle = attributeDatePickerStyle == null ? "full" : attributeDatePickerStyle;

        component.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });
        ((EditText) component).setInputType(InputType.TYPE_NULL);

        DateHelper dateHelper = new DateHelper(getContext());

        Date currentDate = dateHelper.getCurrentDate();


        if (attributeDatePickerStyle.equals("0") || attributeDatePickerStyle.equals("full")) {
            calendarType = BaseDatePickerDialog.DatePickerType.Full;
            componentValue = dateHelper.getCurrentDateString();

        } else if (attributeDatePickerStyle.equals("1") || attributeDatePickerStyle.equals("month")) {

            calendarType = BaseDatePickerDialog.DatePickerType.Month;
            componentValue = dateHelper.getMonthAndYear(dateHelper.getCurrentDate());

        }


        if (attributeHint != null) {
            ((EditText) component).setHint(attributeHint);

        } else {
            ((EditText) component).setHint(componentValue.toString());
        }

    }


    private void showCalendar() {

        if (onDateSetListener == null) {

            calendar = Calendar.getInstance();

            onDateSetListener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateContent();
                }

            };
        }

        JBDatePickerDialog jbDatePickerDialog = new JBDatePickerDialog(getContext(), calendarType);
        jbDatePickerDialog.show();


    }

    private void updateDateContent() {


        DateHelper dateHelper = new DateHelper(getContext());
        ((EditText) component).setText(dateHelper.getStringFromDate(calendar.getTime()));
    }
}
