package com.leapfrog.hokusfokus.utils;

import android.text.TextUtils;
import android.util.Log;

import com.leapfrog.hokusfokus.BuildConfig;

/**
 * This class will contain the methods to print out the logs when running the application.
 * <p/>
 * will print default message if message is passed empty
 */
public class AppLog {
    /**
     * print log
     * @param tag name of the class from which  log is being printed
     * @param message message to print
     */

    public static void showLog(String tag, String message) {
        if (BuildConfig.DEBUG_MODE) {
            Log.i(tag, TextUtils.isEmpty(message) ? "Could not print log." : message);
        }
    }
}
