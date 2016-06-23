package com.leapfrog.hokusfokus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.leapfrog.hokusfokus.HokusFocusApplication;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.receivers.HokusFocusAlarm;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.squareup.otto.Subscribe;

/**
 * Check if HokusFocus Timer has come or not
 */
public class HokusFocusService extends Service {

    private boolean alarmState;
    private HokusFocusAlarm alarm = new HokusFocusAlarm();

    private static String TAG = HokusFocusService.class.getSimpleName();

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        alarmState = PrefUtil.getReminderState();

        startAlarm(alarmState);
        if (DateTimeUtils.isFocusTime()) {
            startTimer();
        }

//        alarm.setEndAlarm(this);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * start {@link TimerService}
     */
    private void startTimer() {
        Intent startTimer = new Intent(HokusFocusApplication.getContext(), TimerService.class);
        HokusFocusApplication.getContext().startService(startTimer);
    }

    /**
     * listens if user has changed the state of Reminder of Focus HOur
     * @param event - passes event <i></>true</i></> if user has set to true else false
     */
    @Subscribe
    private void handleReminderOnOffEvent(HokusFokusEvent.AlarmStateEvent event) {
        alarmState = event.focusHourState;
        startAlarm(alarmState);
    }


    /**
     * set Start and End of the Alarm
     * @param alarmState - true if user has set true else false in {@link com.leapfrog.hokusfokus.activity.SettingActivity}
     *
     */
    private void startAlarm(boolean alarmState) {
        if (alarmState) {

            alarm.setReminderAlarm(this);
            alarm.setStartAlarm(this);
            alarm.setEndAlarm(this);
            AppLog.showLog(TAG, "Hokus Focus Service set alarm");
        }
        stopSelf();
    }

}
