package com.hasgeek.funnel.data;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Author: @karthikb351
 * Project: android
 */

public class DeviceController {

    static int deviceHeight;
    static int deviceWidth;
    static float deviceScaledDensity;

    public static void init(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        deviceHeight = metrics.heightPixels;
        deviceWidth = metrics.widthPixels;
        deviceScaledDensity = metrics.scaledDensity;
    }

    public static int getDeviceHeight() {
        return deviceHeight;
    }

    public static int getDeviceWidth() {
        return deviceWidth;
    }

    public static float getDeviceScaledDensity() {
        return deviceScaledDensity;
    }
}
