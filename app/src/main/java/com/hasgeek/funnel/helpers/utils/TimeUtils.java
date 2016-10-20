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
}
