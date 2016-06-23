package com.leapfrog.hokusfokus.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.utils.HokusFocusCountDownTimer;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.squareup.otto.Subscribe;

public class TimerService extends Service {

    private static final String TAG = TimerService.class.getSimpleName();

    private long timeDifference;
    private boolean pauseProgressView = false;

    HokusFocusCountDownTimer countDown;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        AppLog.showLog(TAG, "Timer Started");
        EventBus.register(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        AppLog.showLog(TAG, "Timer Stopped");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (DateTimeUtils.isFocusTime()) {
            getHokusFocusDifference();

            //Initialize and start the counter
            countDown = HokusFocusCountDownTimer.getInstance(timeDifference, Constants.MILLI_SECOND_VALUE);
            if (!HokusFocusCountDownTimer.isRunning) {
                countDown.start();
            }

            AppLog.showLog(TAG, "Timer instance created" + countDown);

        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * get difference between start and end time of Hokus focus time period
     */
    private void getHokusFocusDifference() {
        if (!DateTimeUtils.isFocusTime() || timeDifference == 0) {
            stopSelf();
        }

        timeDifference = DateTimeUtils.focusHourDifference(PrefUtil.getFocusHourStartTime(), PrefUtil.getFocusHourEndTime());
    }


    /**
     * listens if user has pressed "STOP" or not
     *
     * @param progressView - {@link com.leapfrog.hokusfokus.widget.ProgressView}
     */
    @Subscribe
    public void handleProgressViewPauseEvent(HokusFokusEvent.PauseProgressView progressView) {

        pauseProgressView = progressView.pause;
        if (HokusFocusCountDownTimer.isRunning && pauseProgressView == true) {
            countDown.cancel();
            // AppLog.showLog(TAG, "Timer instance created stopped" + countDown);
            stopSelf();
        }

    }
}