package com.hasgeek.funnel.helpers.schedule;

import android.content.Context;
import android.util.Log;

import com.hasgeek.funnel.data.DeviceController;
import com.hasgeek.funnel.helpers.utils.TimeUtils;
import com.hasgeek.funnel.model.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Author: @karthikb351
 * Project: android
 */

public class ScheduleHelper {


    public static HashMap<Integer, List<Session>> getDayOfYearMapFromSessions(List<Session> sessions) {
        HashMap<Integer, List<Session>> hashMap = new HashMap<>();

        for (Session s: sessions) {
            List<Session> sessionListDay = hashMap.get(TimeUtils.getCalendarFromISODateString(s.getStart()).get(Calendar.DAY_OF_YEAR));
            if (sessionListDay ==  null) {
                sessionListDay = new ArrayList<>();
                hashMap.put(TimeUtils.getCalendarFromISODateString(s.getStart()).get(Calendar.DAY_OF_YEAR), sessionListDay);
            }
            sessionListDay.add(s);
        }

        return hashMap;
    }

    public static void addDimensToSessions(List<Session> sessions) throws ParseException {


        Log.i("ScheduleHelper", "Device Density -> "+ DeviceController.getDeviceScaledDensity());
        int MIN_SESSION_HEIGHT = (int) (200 * DeviceController.getDeviceScaledDensity());
        int SEGMENT_HEIGHT = (int) (5 * DeviceController.getDeviceScaledDensity());
        int maxHeight = 0;

        Collections.sort(sessions, new Comparator<Session>() {
            @Override
            public int compare(Session s1, Session s2) {
                Calendar t1 = TimeUtils.getCalendarFromISODateString(s1.getStart());
                Calendar t2 = TimeUtils.getCalendarFromISODateString(s2.getStart());
                return t1.compareTo(t2);
            }
        });

        HashMap<String, Integer> masterTimeMap = new HashMap<>();

        String startTime = "8:00 AM";
        String endTime = "11:30 PM";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

        Calendar c = Calendar.getInstance();
        c.setTime(simpleDateFormat.parse(startTime));

        for (int i = 0; i < 186; i++) {
            masterTimeMap.put(TimeUtils.getSimpleTimeForCalendar(c), SEGMENT_HEIGHT * i);
            if (maxHeight< SEGMENT_HEIGHT * i)
                maxHeight = SEGMENT_HEIGHT * i;
            c.add(Calendar.MINUTE, 5);
        }


        for (Session s : sessions) {
            int timeDiffInMinutes = TimeUtils.getTimeDifferenceInMinutes(TimeUtils.getCalendarFromISODateString(s.getStart()), TimeUtils.getCalendarFromISODateString(s.getEnd()));
            if (timeDiffInMinutes / 5 * SEGMENT_HEIGHT < MIN_SESSION_HEIGHT) {
                Calendar x = Calendar.getInstance();
                String start = s.getStart();
                x.setTime(simpleDateFormat.parse(TimeUtils.getSimpleTimeForString(s.getStart())));
                x.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
                int offset = MIN_SESSION_HEIGHT / (timeDiffInMinutes / 5);
                for (int y = 0; y < timeDiffInMinutes / 5; y++) {
                    Integer h = masterTimeMap.get(TimeUtils.getSimpleTimeForCalendar(x));
                    if (h != null) {
                        masterTimeMap.put(TimeUtils.getSimpleTimeForCalendar(x), h + offset * y);
                        if (maxHeight < h + offset * y)
                            maxHeight = h + offset * y;
                        x.add(Calendar.MINUTE, 5);
                    }

                }
                do {
                    Calendar z = Calendar.getInstance();
                    z.setTime(x.getTime());
                    z.add(Calendar.MINUTE, -5);
                    int h = masterTimeMap.get(TimeUtils.getSimpleTimeForCalendar(z));
                    masterTimeMap.put(TimeUtils.getSimpleTimeForCalendar(x), h + SEGMENT_HEIGHT);
                    if (maxHeight < h + SEGMENT_HEIGHT)
                        maxHeight = h + SEGMENT_HEIGHT;
                    x.add(Calendar.MINUTE, 5);
                } while ((!simpleDateFormat.format(x.getTime()).equals(endTime)));


            }
        }

        for (Session s : sessions) {
            int top = masterTimeMap.get(TimeUtils.getSimpleTimeForString(s.getStart()));
            int height = masterTimeMap.get(TimeUtils.getSimpleTimeForString(s.getEnd())) - top;

            s.setMarginTop(top);
            s.setHeight(height);
        }
    }
}