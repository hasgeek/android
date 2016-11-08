package com.hasgeek.funnel.helpers.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Author: @karthikb351
 * Project: android
 */

public class PackageUtils {

    public static final String SLACK_ANDROID_PACKAGE_NAME = "com.Slack";

    public static boolean isPackageInstalled(String targetPackage, PackageManager packageManager){
        try {
            PackageInfo info=packageManager.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
