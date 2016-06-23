package com.leapfrog.hokusfokus.listener;

import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.utils.AppLog;

/**
 * Class to look over if user has pressed DONE button in the keyboard or not
 */
public class EditorActionListener implements TextView.OnEditorActionListener {

    private static final String TAG = EditorActionListener.class.getSimpleName();

    public EditorActionListener() {
    }

/**
 * this function tracks if user has pressed DONE button or not
* */
    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {

            EventBus.post(new HokusFokusEvent.ActionDoneListener(true));
            AppLog.showLog(TAG, "DONE Button pressed");
            return true;
        }
        return false;
    }
}
