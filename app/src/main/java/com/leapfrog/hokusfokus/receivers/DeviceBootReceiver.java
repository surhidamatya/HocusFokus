package com.leapfrog.hokusfokus.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leapfrog.hokusfokus.HokusFocusApplication;

/**
 * Broadcast receiver, starts when the device gets started after reboot.
 * Start repeating alarm for focus hour.
 */
public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            Intent alarmIntent = new Intent(context, HokusFocusAlarm.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            HokusFocusAlarm alarm = new HokusFocusAlarm();
            alarm.setStartAlarm(HokusFocusApplication.getContext());
            alarm.setEndAlarm(HokusFocusApplication.getContext());

        }
    }
}
