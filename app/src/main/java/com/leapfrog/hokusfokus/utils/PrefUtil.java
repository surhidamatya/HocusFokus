package com.leapfrog.hokusfokus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.leapfrog.hokusfokus.HokusFocusApplication;
import com.leapfrog.hokusfokus.data.Constants;

/**
 * Class to set and get shared preferences
 */
public class PrefUtil {
    private static final String PREF_NAME = "Focus";
    private static final String REMINDER_STATE = "rState";
    private static final String TASK_REMINDER_DURATION_INDEX = "tRD";
    private static final String FOCUS_HOUR_REMINDER_DURATION_INDEX = "fhRD";
    private static final String FOCUS_HOUR_START_TIME = "startTime";
    private static final String FOCUS_HOUR_END_TIME = "endTime";
    private static final String FULL_FOCUS_HOUR_START_TIME = "fullStartTime";
    private static final String FULL_FOCUS_HOUR_END_TIME = "fullEndTime";

    private static final String TIMER_STOP = "timer";

    private static final String PROGRESS_VALUE = "progress";

    private static final String IS_FIRST_TIME = "isFirstTIme";
    private static final String IS_FIRST_TIME_MENU = "isFirstTImeMenu";

    private static SharedPreferences sharedPreferences;
    private static PrefUtil prefUtil = null;

    private PrefUtil() {

    }

    private static SharedPreferences getPrefInstance() {
        if (sharedPreferences == null) {
            sharedPreferences = HokusFocusApplication.getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }

        return sharedPreferences;
    }

    /**
     * Store user preference for enabling reminder
     */
    public static void enableReminder() {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putBoolean(REMINDER_STATE, true);
        editor.apply();
    }

    /**
     * Store user preference for disabling reminder
     */
    public static void disableReminder() {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putBoolean(REMINDER_STATE, false);
        editor.apply();
    }

    /**
     * Get user preference for enabling/disabling reminder state
     */

    public static Boolean getReminderState() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getBoolean(REMINDER_STATE, true);
    }

    /*Check first installation*/

    public static void setFirstInstallation(){

        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putBoolean(IS_FIRST_TIME, false);
        editor.apply();

    }

    public static boolean getFirstInstallation(){

        sharedPreferences = getPrefInstance();
        return sharedPreferences.getBoolean(IS_FIRST_TIME, true);

    }


    /*
    * check value to show menu for the first time
    * */
    public static void setFirstMenu(){

        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putBoolean(IS_FIRST_TIME_MENU, false);
        editor.apply();

    }


    public static boolean getFirstMenu(){

        sharedPreferences = getPrefInstance();
        return sharedPreferences.getBoolean(IS_FIRST_TIME_MENU, true);

    }

    /**
     * Store user preference task reminder duration
     */
    public static void setTaskReminderDurationIndex(Integer duration) {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putInt(TASK_REMINDER_DURATION_INDEX, duration);
        editor.apply();
    }

    /**
     * Get user preference task reminder duration
     */
    public static Integer getTaskReminderDurationIndex() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getInt(TASK_REMINDER_DURATION_INDEX, 0);
    }

    /**
     * Store user preference for focus hour reminder duration
     */
    public static void setFocusHourReminderDurationIndex(Integer duration) {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putInt(FOCUS_HOUR_REMINDER_DURATION_INDEX, duration);
        editor.apply();
    }

    /**
     * Get user preference for focus hour reminder
     */
    public static Integer getFocusHourReminderDurationIndex() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getInt(FOCUS_HOUR_REMINDER_DURATION_INDEX, 0);
    }

    /**
     * Store user preference for focus hour start time
     */
    public static void setFocusHourStartTime(String time) {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putString(FOCUS_HOUR_START_TIME, time);
        editor.apply();
    }

    /**
     * Store user preference for focus hour end time
     */
    public static void setFocusHourEndTime(String time) {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putString(FOCUS_HOUR_END_TIME, time);
        editor.apply();
    }

    /**
     * Get user preference for focus hour start time
     */
    public static String getFocusHourStartTime() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getString(FOCUS_HOUR_START_TIME, Constants.FOCUS_HOUR_START_TIME);
    }

    /**
     * Get user preference for focus hour end time
     */
    public static String getFocusHourEndTime() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getString(FOCUS_HOUR_END_TIME, Constants.FOCUS_HOUR_END_TIME);
    }

    /**
     * Set Focus hour start time in full format
     */
    public static void setFullFocusHourStartTime(String time) {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putString(FULL_FOCUS_HOUR_START_TIME, time);
        editor.apply();
    }

    /**
     * Set Focus hour End time in full format
     */
    public static void setFullFocusHourEndTime(String time) {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putString(FULL_FOCUS_HOUR_END_TIME, time);
        editor.apply();
    }

    /**
     * Get Focus hour start time in full format
     */
    public static String getFullFocusHourStartTime() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getString(FULL_FOCUS_HOUR_START_TIME, DateTimeUtils.currentDateDb() + " 10:00:00");
    }

    /**
     * Get Focus hour End time in full format
     */
    public static String getFullFocusHourEndTime() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getString(FULL_FOCUS_HOUR_END_TIME, DateTimeUtils.currentDateDb() + " 11:00:00");
    }

    public static void setTimerState(boolean state) {
        SharedPreferences.Editor editor = getPrefInstance().edit();
        editor.putBoolean(TIMER_STOP, state);
        editor.apply();
    }

    public static boolean getTimerState() {
        sharedPreferences = getPrefInstance();
        return sharedPreferences.getBoolean(TIMER_STOP, false);
    }
}
