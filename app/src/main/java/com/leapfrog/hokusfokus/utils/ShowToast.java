package com.leapfrog.hokusfokus.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.leapfrog.hokusfokus.HokusFocusApplication;

/**
 * class to handle toast message
 *shows  {@link Toast}
 * input Message and duration to show the toast
 */
public class ShowToast {

    public static void showToast(String message, boolean forShortTime){
        int duration = forShortTime ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(HokusFocusApplication.context, message, duration);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();


    }
}
