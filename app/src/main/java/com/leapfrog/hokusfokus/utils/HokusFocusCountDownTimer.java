package com.leapfrog.hokusfokus.utils;

import android.content.Intent;
import android.os.CountDownTimer;

import com.leapfrog.hokusfokus.HokusFocusApplication;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.service.TimerService;

import java.util.concurrent.TimeUnit;

/**
 * This class handles the count down timer
 */
public class HokusFocusCountDownTimer extends CountDownTimer {

    private static final String TAG = HokusFocusCountDownTimer.class.getSimpleName();

    public static final String COUNTDOWN_BROADCAST_RECEIVER = "hokusFocusBroadcast";

    private final Intent countDownTimer = new Intent(COUNTDOWN_BROADCAST_RECEIVER);

    public static boolean isRunning = false;

    public static long focusHourMilli = 0;

    public static HokusFocusCountDownTimer hokusFocusCountDownTimer;

    public static HokusFocusCountDownTimer getInstance(long millisInFuture, long countDownInterval) {
        if (hokusFocusCountDownTimer == null) {
            hokusFocusCountDownTimer = new HokusFocusCountDownTimer(millisInFuture, countDownInterval);
        }
        return hokusFocusCountDownTimer;
    }


    private HokusFocusCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }


    @Override
    public void onTick(long millisUntilFinished) {

        isRunning = true;
        this.focusHourMilli = millisUntilFinished;

//        long millis = millisUntilFinished;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

        sendTimerBroadcastTimer(hms, millisUntilFinished, false);


        AppLog.showLog(TAG, "*****running timer  " + hms + " Milli Second " + millisUntilFinished);


    }

    @Override
    public void onFinish() {
        /**
         *The CountDownTimer is not precise, it will return as close to 1 second (in this case)
         * as it can but often that won't be enough to give you what you want so using this way to set "00:00:00"
         * in {@link com.leapfrog.hokusfokus.fragment.ActiveTaskTimer} after countdown finishes
         *
         * http://stackoverflow.com/questions/6810416/android-countdowntimer-shows-1-for-two-seconds
         *
         * http://stackoverflow.com/questions/4824068/how-come-millisuntilfinished-cannot-detect-exact-countdowntimer-intervals
         */
        sendTimerBroadcastTimer("00:00:00", 0, true);

        isRunning = false;

        stopTimer();

        AppLog.showLog(TAG, "countdown finish");
    }

    //    Stop timer service as soon as the Focus hour ends
    private void stopTimer() {
        Intent startTimer = new Intent(HokusFocusApplication.getContext(), TimerService.class);
        HokusFocusApplication.getContext().stopService(startTimer);
    }

    /**
     * sends broadcast to {@link com.leapfrog.hokusfokus.fragment.ActiveTaskTimer} on each second to show timer
     *
     * @param hms                 shows time in 00:00:00 i.e HH:MM:SS  format
     * @param millisUntilFinished millisecond of the countdown timer
     */
    private void sendTimerBroadcastTimer(String hms, long millisUntilFinished, boolean isTimeFinished) {
        countDownTimer.putExtra(Constants.HOCUS_FOCUS_TIMER, hms);
        countDownTimer.putExtra(Constants.HOCUS_FOCUS_TIMER_MILLI_SECOND, TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished));
        countDownTimer.putExtra(Constants.IS_TIME_STOPPED, isTimeFinished);
        HokusFocusApplication.context.sendBroadcast(countDownTimer);
    }
}
