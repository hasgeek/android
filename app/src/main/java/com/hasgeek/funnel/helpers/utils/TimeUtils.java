package com.hasgeek.funnel.helpers.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Author: @karthikb351
 * Project: android
 */

public class TimeUtils {

    public static Calendar getCalendarFromISODateString(String s) {
        Calendar calendar = Calendar.getInstance();
        try {
            DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            m_ISO8601Local.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            calendar.setTime(m_ISO8601Local.parse(s));
        } catch (Exception e) {
            e.printStackTrace();
            calendar = null;
        }
        return calendar;
    }

    public static int getTimeDifferenceInMinutes(Calendar c1, Calendar c2) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
            Date d1 = simpleDateFormat.parse(simpleDateFormat.format(c1.getTime()));
            Date d2 = simpleDateFormat.parse(simpleDateFormat.format(c2.getTime()));
            return (int)((Math.abs(d1.getTime() - d2.getTime())/ 1000) / 60);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static String getSimpleTimeForCalendar(Calendar c) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");
        return simpleDateFormat.format(c.getTime());
    }

    public static String getSimpleTimeForString(String date) {
        Calendar cal = TimeUtils.getCalendarFromISODateString(date);
        return getSimpleTimeForCalendar(cal);
    }

    public static String getDayOfWeekFromCalendar(Calendar c) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
        return simpleDateFormat.format(c.getTime());
    }
}
