package com.hasgeek.funnel;

import android.app.Application;

import com.hasgeek.funnel.data.DeviceController;
import com.karumi.dexter.Dexter;

/**
 * Author: @karthikb351
 * Project: android
 */

public class HasGeek extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Dexter.initialize(getApplicationContext());
        DeviceController.init(getApplicationContext());
    }
}
