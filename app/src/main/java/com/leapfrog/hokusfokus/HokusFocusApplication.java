package com.leapfrog.hokusfokus;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;


/**
 * Main Application responsible for static context to be used by anywhere in the class
 * and responsible for initializing Crashalytics
 */
public class HokusFocusApplication extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        //canary leak intilization
        LeakCanary.install(this);

        context = this;

    }
    public static Context getContext() {
        return context;
    }
}
