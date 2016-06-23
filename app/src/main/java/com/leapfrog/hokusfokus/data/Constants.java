package com.leapfrog.hokusfokus.data;

/**
 * Class to hold global Constant values.
 */
public class Constants {
    public static final int SPLASH_TIME_OUT = 1000;
    public static final String HOCUS_FOCUS_TIMER = "hokusFokusTimer";
    public static final String HOCUS_FOCUS_TIMER_MILLI_SECOND = "hokusFokusTimerMilliSecond";
    public static final String IS_TIME_STOPPED = "isTimeStopped";

    public static final String TASK_REMINDER_DIALOG = "taskReminder";
    public static final String FOCUS_HOUR_REMINDER_DIALOG = "focusHourReminder";

    //Task Status
    public static final String NOT_COMPLETED = "NOT_COMPLETED";
    public static final String NOT_STARTED = "NOT_STARTED";
    public static final String DONE = "COMPLETED";

    public static final String ALARM_TYPE = "AlarmType";

    //Rotation value
    public static int ROTATE_DEGREE_HALF = 180;
    public static int ROTATE_DEGREE_FULL = 360;
    public static int ROTATE_DEGREE_START = 0;

    //Sliding threshold value to start animation when user slides vie
    public static float SLIDING_THRESHOLD = 0.3f;

    public static int CONSTANT_DIMENSION = 238;
    public static int TOOLBAR_HEIGHT = 48;

    public static int TIMER_DELAY = 100;
    public static int UPDATE_TIME = 1000;

    public static String STOP_MESSAGE = "STOP";
    public static String COMPLETE_MESSAGE = "DONE";

    //Image height and width in splash screen
    public static int SPLASH_IMAGE_WIDTH = 512;
    public static int SPLASH_IMAGE_HEIGHT = 1024;

    public static int MINIMUM_SCROLLED_HEIGHT = 0;

    public static long MILLI_SECOND_VALUE = 1000;

    //Time format
    public static String FOCUS_HOUR_START_TIME = "10:00";
    public static String FOCUS_HOUR_END_TIME = "11:00";

    public static String HOUR_MINUTE_TIME_FORMAT = "HH:mm";
    public static String HOUR_MINUTE_SECONDS_TIME_FORMAT = "HH:mm:ss";
    public static String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";

    public static String SPACE = " ";
    public static String DASH = "-";
    public static String COMMA = ",";

    //Time unit value
    public static int TIME_VALUE = 60;
    public static int TIME_VALUE_HOUR = 24;

    //Handle BackPress on first install
    public static final String IS_FROM_BACK_PRESS = "isFromBackPress";

    public static final int TUTORIAL_ANIMATION_DURATION = 600;

}
