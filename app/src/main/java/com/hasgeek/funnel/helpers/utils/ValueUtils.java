package com.hasgeek.funnel.helpers.utils;

/**
 * Author: @karthikb351
 * Project: android
 */

public class ValueUtils {

    public static boolean isBlank(String s){
        if (s==null)
            return true;
        if (s.equals(""))
            return true;

        return false;
    }

    public static boolean isNotBlank(String s){
        return !isBlank(s);
    }
}
