package com.cynerds.cyburger.components.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cynerds.cyburger.R;
import com.cynerds.cyburger.components.JBButton;
import com.cynerds.cyburger.helpers.DateHelper;

import java.util.Date;

/**
 * Created by fabri on 13/08/2017.
 */

public abstract class BaseDatePickerDialog extends ConstraintLayout {
    private final Dialog dialog;
    private final DateHelper dateHelper;
    DatePickerType datePickerType;
    private Date date;
    private ImageButton previousMonthBtn;
    private ImageButton nextMonthBtn;
    private TextView datepickerYearLbl;
    private TextView datePickerMonthDayLbl;
    private TextView datePickerMonthYearLabel;
    private boolean listenersCreated;

    public BaseDatePickerDialog(Context context) {
        super(context);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.datapicker_base);
        dateHelper = new DateHelper(context);
        date = dateHelper.getCurrentDate();


    }


    public void setDatePickerType(DatePickerType datePickerType) {
        this.datePickerType = datePickerType;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }

    private void updateCalendar() {


        datepickerYearLbl.setText(String.valueOf(date.getYear()));

    }

    public void show() {


        if (!dialog.isShowing()) {
            switch (datePickerType) {

                case Month:
                    View daysTable = dialog.findViewById(R.id.datePickerDaysTable);
                    daysTable.setVisibility(GONE);
                    break;
                case Full:
                    break;
            }
            dialog.show();

            JBButton cancelButton = (JBButton) dialog.findViewById(R.id.datePickerCancelBtn);
            cancelButton.getButton().setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (!listenersCreated) {
                previousMonthBtn = (ImageButton) dialog.findViewById(R.id.datePickerPreviousMonth);
                nextMonthBtn = (ImageButton) dialog.findViewById(R.id.datePickerNextMonth);
                datepickerYearLbl = (TextView) dialog.findViewById(R.id.datepickerYearLbl);
                datePickerMonthDayLbl = (TextView) dialog.findViewById(R.id.datePickerMonthDayLbl);
                datePickerMonthYearLabel = (TextView) dialog.findViewById(R.id.datePickerMonthYearLabel);

                previousMonthBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int month = date.getMonth();
                        int year = date.getYear();
                        if (month > 1) {
                            date.setMonth(month - 1);

                        } else {
                            if (year > 1) {
                                date.setYear(year - 1);
                            }
                        }

                        updateCalendar();
                    }
                });

                nextMonthBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int month = date.getMonth();
                        int year = date.getYear();
                        date.setMonth(month + 1);
                        if (month < 12) {
                            date.setMonth(month - 1);

                        } else {
                            date.setYear(year + 1);
                        }
                        updateCalendar();
                    }
                });
                listenersCreated = true;
            }

        }

    }

    public enum DatePickerType {

        Month, Full

    }
}
