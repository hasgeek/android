package com.hasgeek.funnel.helpers.utils;

/**
 * Author: @karthikb351
 * Project: android
 */

public class AuthUtils {

    public static String getAuthHeaderFromToken(String token) {
        return "Bearer "+token;
    }
}
