package com.leapfrog.hokusfokus.receivers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;

import com.leapfrog.hokusfokus.HokusFocusApplication;
import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.data.AlarmType;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.service.TimerService;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.leapfrog.hokusfokus.utils.ResourceUtils;

import java.util.Calendar;

/**
 * Receive alarm broadcast
 * notify user by Notification on Reminder , Start of the Focus Hour and End.
 */
public class HokusFocusAlarm extends BroadcastReceiver {

    private String notifyMessage = "";

    private static String TAG = HokusFocusAlarm.class.getSimpleName();

    String[] timeArray = new String[]{"5", "10", "15", "20", "30"};

    @Override
    public void onReceive(Context context, Intent intent) {
        if (DateTimeUtils.isFocusTime()) {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
            wl.acquire();
            // Put code here.
            wl.release();

            if (intent != null && intent.getExtras() != null) {
                Bundle params = intent.getExtras();
                if (params.containsKey(Constants.ALARM_TYPE)) {
                    AlarmType alarmType = params.getParcelable(Constants.ALARM_TYPE);
                    AppLog.showLog(TAG, "Alarm Type " + alarmType.alarmType);
                    switch (alarmType.alarmType) {
                        case AlarmType.ALARM_START:
                            //do nothing
                            notifyMessage = "Focus hour Started!";
                            showNotification();
                            startTimer();
                            break;
                        case AlarmType.ALARM_REMINDER:
                            //check settings and repeat
                            setReminderAlarm(HokusFocusApplication.getContext());
                            break;
                        case AlarmType.ALARM_END:
                            //check settings and repeat
                            setEndAlarm(HokusFocusApplication.getContext());
                            notifyMessage = "Focus hour ended!";
                            stopTimer();
                            showNotification();
                            break;
                    }
                }
            }
        }
    }

    /**
     * set alarm to notify user of starting focus hour
     */
    public void setStartAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String[] time = ResourceUtils.splitString(PrefUtil.getFocusHourStartTime());

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

        Intent setFocusAlarm = new Intent(context, HokusFocusAlarm.class);

        AlarmType alarmType = new AlarmType(AlarmType.ALARM_START);
        Bundle bunlde = new Bundle();
        bunlde.putParcelable(Constants.ALARM_TYPE, alarmType);
        setFocusAlarm.putExtras(bunlde);

        //Bundle params
        PendingIntent focusAlarmPendingIntent = PendingIntent.getBroadcast(context, AlarmType.ALARM_START, setFocusAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), focusAlarmPendingIntent);
        AppLog.showLog(TAG, "focus Start Alarm " + time[0] + " " + time[1]);


    }

    /**
     * set alarm to notify user of ending focus hour
     */
    public void setEndAlarm(Context context) {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String[] time = ResourceUtils.splitString(PrefUtil.getFocusHourEndTime());

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

        Intent setFocusAlarm = new Intent(context, HokusFocusAlarm.class);

        AlarmType alarmType = new AlarmType(AlarmType.ALARM_END);
        Bundle bunlde = new Bundle();
        bunlde.putParcelable(Constants.ALARM_TYPE, alarmType);
        setFocusAlarm.putExtras(bunlde);

        PendingIntent focusAlarmPendingIntent = PendingIntent.getBroadcast(context, AlarmType.ALARM_END, setFocusAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 0, focusAlarmPendingIntent);

        AppLog.showLog(TAG, "focus End Alarm " + time[0] + " " + time[1]);


    }

    /**
     * set alarm to notify user before focus hour starting reminder
     */
    public void setReminderAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        String[] time = ResourceUtils.splitString(PrefUtil.getFocusHourStartTime());

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]) - Integer.parseInt(timeArray[PrefUtil.getFocusHourReminderDurationIndex()]));

        Intent setFocusAlarm = new Intent(context, HokusFocusAlarm.class);

        AlarmType alarmType = new AlarmType(AlarmType.ALARM_REMINDER);
        Bundle bunlde = new Bundle();
        bunlde.putParcelable(Constants.ALARM_TYPE, alarmType);
        setFocusAlarm.putExtras(bunlde);

        //Bundle params
        PendingIntent focusAlarmPendingIntent = PendingIntent.getBroadcast(context, AlarmType.ALARM_REMINDER, setFocusAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), focusAlarmPendingIntent);
        AppLog.showLog(TAG, "focus Reminder Alarm " + calendar);


    }

    /**
     * cancel alarm
     */
    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, HokusFocusAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    /**
     * Function to show notification
     */
    private void showNotification() {


        //define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification mNotification = new Notification.Builder(HokusFocusApplication.getContext())

                .setContentTitle("Hokus Focus")
                .setContentText(notifyMessage)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setSound(soundUri)
                .build();

        NotificationManager notificationManager = (NotificationManager) HokusFocusApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotification);
    }

    /**
     * Start timer service as soon as the Focus hour starts
     */
    private void startTimer() {
        Intent startTimer = new Intent(HokusFocusApplication.getContext(), TimerService.class);
        HokusFocusApplication.getContext().startService(startTimer);
    }

    /**
     * Stop timer service as soon as the Focus hour ends
     */
    private void stopTimer() {
        Intent startTimer = new Intent(HokusFocusApplication.getContext(), TimerService.class);
        HokusFocusApplication.getContext().stopService(startTimer);
    }
}
