package com.hasgeek.funnel;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.hasgeek.funnel.data.DeviceController;
import com.hasgeek.funnel.data.SharedPreferenceController;
import com.karumi.dexter.Dexter;
import io.fabric.sdk.android.Fabric;

/**
 * Author: @karthikb351
 * Project: android
 */

public class HasGeek extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Answers(), new Crashlytics());
        Dexter.initialize(getApplicationContext());
        SharedPreferenceController.init(getApplicationContext());
        DeviceController.init(getApplicationContext());
    }
}
