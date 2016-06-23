package com.leapfrog.hokusfokus.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.leapfrog.hokusfokus.HokusFocusApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

/**
 * Handle all the common methods
 */
public class ResourceUtils {

    /**
     * method checks database existence.
     *
     * @param dbName
     * @return true if database exist
     */

    public static boolean doesDatabaseExist(String dbName) {
        File dbFile = HokusFocusApplication.getContext().getDatabasePath(dbName);
        return dbFile.exists();
    }

    /**
     * sort the months got from database
     *
     * @param unsortedMonths list of months from the database
     * @return sorted list of months
     */
    public static ArrayList<String> sortMonth(Set<String> unsortedMonths) {
        //code taken from url="http://stackoverflow.com/questions/8409290/sort-array-of-strings-based-off-of-parent-array"
        String[] monthOrder = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        ArrayList<String> sortedMonths;
        sortedMonths = new ArrayList<String>();
        for (String month : monthOrder) {
            if (unsortedMonths.contains(month))
                sortedMonths.add(month);
        }
        return sortedMonths;
    }


    /**
     * function to set suffix to dates according to
     *
     * @return date with suffix i.e 1st, 2nd etc
     */
    public static String getDayOfMonthSuffix(final int day) {
//        checkArgument(n >= 1 && n <= 31, "illegal day of month: " + n);
        if (day >= 11 && day <= 13) {
            return day + "th";
        }
        switch (day % 10) {
            case 1:
                return day + "st";
            case 2:
                return day + "nd";
            case 3:
                return day + "rd";
            default:
                return day + "th";
        }
    }

    /**
     * shows softKeyboard when focus is on editText
     */
    public static void forceShowKeyboardLayout() {
        InputMethodManager inputMethodManager = (InputMethodManager) HokusFocusApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * split the passed string
     *
     * @param stringSplit string in the format of "aa:bb"
     * @return string array with focus hour start hour and minute
     */
    public static String[] splitString(String stringSplit) {

        String[] timeValue = stringSplit.split(":");

        return timeValue;

    }

    /**
     * hides keyboard when user navigates to other activity or fragment
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) HokusFocusApplication.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * convert dp to pixel
     */
    public static float convertDpToPx(int dp) {

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    /**
     * Provides collection of notices. Used to create licenses about thhird party library used in the app.
     *
     * @return {@link Notices} - collection of notice
     */
    public static Notices getLicenses() {
        Notices notices = new Notices();

        notices.addNotice(new Notice("Picasso v2.5.2", "https://github.com/square/picasso", "Copyright 2013 Square, Inc.", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Material v1.2.1", "https://github.com/rey5137/material", "Copyright 2015 Rey Pham.", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Butter Knife v7.0.1", "https://github.com/JakeWharton/butterknife", "Copyright 2013 Jake Wharton", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("LicensesDialog v1.8.0", "https://github.com/PSDev/LicensesDialog", "Copyright 2013 Philip Schiffer", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Otto - An event bus by Square v1.2.8", "https://github.com/square/otto", "Copyright 2012 Square, Inc.\n" +
                "Copyright 2010 Google, Inc.", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("AppIntro v3.2.0", "https://github.com/PaoloRotolo/AppIntro", "Copyright PaoloRotolo", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("Android Sliding Up Panel v3.1.1", "https://github.com/PaoloRotolo/AppIntro", "Copyright umano", new ApacheSoftwareLicense20()));

        return notices;
    }
}
