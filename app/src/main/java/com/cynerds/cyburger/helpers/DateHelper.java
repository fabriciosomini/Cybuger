package com.cynerds.cyburger.helpers;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by fabri on 21/07/2017.
 */

public class DateHelper {

    private final Context context;
    private Calendar calendar;
    private DateFormat dateFormat;

    public DateHelper(Context context) {
        this.context = context;

        Locale br = new Locale("pt", "BR");
        calendar = Calendar.getInstance(br);
        dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, br);

    }

    public Calendar getCalendar() {

        return calendar;

    }

    public String getMonthAndYear(Date date) {


        String regex = "(\\d+/)(\\d+/\\d+)";
        String value = getStringFromDate(date).replaceAll(regex, "$2");
        return value;
    }

    public String getCurrentDateString() {


        String todayStr = dateFormat.format(getCurrentDate());
        todayStr = Pattern.compile("\\d+\\:\\d+").split(todayStr)[0].trim();
        return todayStr;
    }

    public Date getCurrentDate() {

        String dateStr = calendar.get(Calendar.DAY_OF_MONTH) + "/"
                + (calendar.get(Calendar.MONTH) + 1) + "/"
                + calendar.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            // handle exception here !
        }

        return date;

    }

    public Date getDateFromString(String dateString) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse(dateString);

        return date;

    }

    public String getStringFromDate(Date date) {


        String dateStr = dateFormat.format(date);
        dateStr = Pattern.compile("\\d+\\:\\d+").split(dateStr)[0].trim();


        return dateStr;
    }
}
