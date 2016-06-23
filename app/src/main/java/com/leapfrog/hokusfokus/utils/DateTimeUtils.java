package com.leapfrog.hokusfokus.utils;

import com.leapfrog.hokusfokus.data.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to handle Date and Time operation
 */

public class DateTimeUtils {

    private static String TAG = DateTimeUtils.class.getSimpleName();

    /**
     * function to return current date
     *
     * @return current {@link Date} with "th" appended to date
     */

    public static String currentDate() {

        String currentDates = "";

        Calendar c = Calendar.getInstance();
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String day = ResourceUtils.getDayOfMonthSuffix(c.get(Calendar.DAY_OF_MONTH));
        int year = c.get(Calendar.YEAR);

        currentDates = day + Constants.SPACE + month + Constants.SPACE + year;

        return currentDates;

    }

    /**
     * function to return current date and time
     *
     * @return current {@link Date} with "th" appended to date and time of device
     */
    public static String currentDateTime() {

        String currentDatTimes = "";

        Calendar c = Calendar.getInstance();
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        String day = ResourceUtils.getDayOfMonthSuffix(c.get(Calendar.DAY_OF_MONTH));
        int year = c.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.HOUR_MINUTE_TIME_FORMAT);
        String test = sdf.format(c.getTime());

        String daytime = c.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.ENGLISH);
        currentDatTimes = day + Constants.SPACE + month + Constants.SPACE + year + Constants.COMMA + Constants.SPACE + test + Constants.SPACE + daytime;

        return currentDatTimes;

    }

    /**
     * function to return current date
     *
     * @return current {@link Date} to search inside database
     */
    public static String currentDateDb() {

        String currentDates = "";

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);

        currentDates = year + Constants.DASH + month + Constants.DASH + day;
        return currentDates;

    }

    /**
     * get current date and month
     *
     * @return string value of current month with date
     */
    public static String getCurrentMonthAndDate() {
        String currentDates = "";

        Calendar c = Calendar.getInstance();
        String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int year = c.get(Calendar.YEAR);

        currentDates = month + Constants.SPACE + day;

        return currentDates;
    }

    /**
     * function to calculate time difference
     *
     * @return difference between hokus focus start and end time
     */

    public static long timeDifference(String startTime, String endTime) {

        long hokusFocusTime = 0;

        long hours = 0;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.HOUR_MINUTE_TIME_FORMAT);

            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);
            hokusFocusTime = endDate.getTime() - startDate.getTime();

            int days = (int) (hokusFocusTime
                    / (Constants.MILLI_SECOND_VALUE * Constants.TIME_VALUE * Constants.TIME_VALUE * Constants.TIME_VALUE_HOUR));
            hours = (int) ((hokusFocusTime
                    - (Constants.MILLI_SECOND_VALUE * Constants.TIME_VALUE * Constants.TIME_VALUE * Constants.TIME_VALUE_HOUR * days))
                    / (Constants.MILLI_SECOND_VALUE * Constants.TIME_VALUE * Constants.TIME_VALUE));
        } catch (ParseException e) {
        }

        return hours;

    }


    /**
     * function to calculate time difference
     *
     * @return difference between hokus focus start and end time
     */

    public static long focusHourDifference(String startTime, String endTime) {

        long hokusFocusTime = 0;

        if (isFocusTime()) {

            Calendar c = Calendar.getInstance();

            Date startDate = null;
            Date endDate = null;
            Date currentDate = null;

            SimpleDateFormat sdf = new SimpleDateFormat(Constants.HOUR_MINUTE_SECONDS_TIME_FORMAT);
            String currentTime = sdf.format(c.getTime());

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.HOUR_MINUTE_TIME_FORMAT);
                startDate = simpleDateFormat.parse(startTime);
                endDate = simpleDateFormat.parse(endTime);
                currentDate = simpleDateFormat.parse(currentTime);

                hokusFocusTime = (endDate.getTime() - startDate.getTime()) - (currentDate.getTime() - startDate.getTime());
            } catch (ParseException e) {
                AppLog.showLog(TAG, "Time Difference" + e.getMessage());
            }
        }

        return hokusFocusTime;

    }

    /**
     * get current month
     *
     * @return string value of current month with display name
     */
    public static String getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        return c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
    }

    /**
     * check if current time lies in  between focus hour
     *
     * @return true if current time lies in between focus hour else false
     */
    public static boolean isFocusTime() {

        boolean isFocusTimes = false;
        try {

            AppLog.showLog(DateTimeUtils.class.getSimpleName(), "Focus time value " + PrefUtil.getFullFocusHourStartTime() + "end time" + PrefUtil.getFullFocusHourEndTime());

            String focusTimeStart = PrefUtil.getFullFocusHourStartTime();
            Date time1 = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT + Constants.SPACE + Constants.HOUR_MINUTE_TIME_FORMAT).parse(focusTimeStart);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);
            calendar1.add(Calendar.DATE, 0);

            String focusHourEndTime = PrefUtil.getFullFocusHourEndTime();
            Date time2 = new SimpleDateFormat(Constants.SIMPLE_DATE_FORMAT + " " + Constants.HOUR_MINUTE_TIME_FORMAT).parse(focusHourEndTime);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 0);

            Calendar calendar3 = Calendar.getInstance();
            Date currentTime = calendar3.getTime();
            calendar3.add(Calendar.DATE, 0);

            AppLog.showLog(TAG, "focustime start " + time1 + "focus end " + time2 + "current Time " + currentTime);

            if (currentTime.after(calendar1.getTime()) && currentTime.before(calendar2.getTime())) {
                isFocusTimes = true;
            } else {
                isFocusTimes = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            AppLog.showLog(DateTimeUtils.class.getSimpleName(), e.getMessage());
        }

        return isFocusTimes;

    }

    /**
     * get the elapsed time respective to focus hour start time
     *
     * @param startTime focus hour start time
     * @return elapsed time calculated from current time
     */
    public static long getElapsedTime(String startTime) {

        long hokusFocusElapsedTime = 0;

        Calendar c = Calendar.getInstance();


        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.HOUR_MINUTE_TIME_FORMAT);
            String currentTime = simpleDateFormat.format(c.getTime());

            Date startDate = simpleDateFormat.parse(startTime);
            Date currentDate = simpleDateFormat.parse(currentTime);

            hokusFocusElapsedTime = ((currentDate.getTime() - startDate.getTime())) / Constants.MILLI_SECOND_VALUE;
        } catch (ParseException e) {
        }

        return hokusFocusElapsedTime;

    }

    /**
     * function to calculate time difference
     *
     * @return difference between hokus focus start and end time
     */

    public static long focusTimeDifference(String startTime, String endTime) {

        long hokusFocusTime = 0;

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.HOUR_MINUTE_TIME_FORMAT);
            Date startDate = simpleDateFormat.parse(startTime);
            Date endDate = simpleDateFormat.parse(endTime);

            hokusFocusTime = endDate.getTime() - startDate.getTime();
        } catch (ParseException e) {
        }

        return hokusFocusTime;

    }


}
