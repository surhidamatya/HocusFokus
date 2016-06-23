package com.leapfrog.hokusfokus.utils;

import android.text.TextUtils;

import com.leapfrog.hokusfokus.HokusFocusApplication;
import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.data.Constants;

/**
 * Created by leapfrog on 6/16/15.
 * function which takes {@link com.leapfrog.hokusfokus.model.Task} status as input and
 * return background color to respective class.
 * */
public class TaskStatusBackGround {

    private static String TAG= TaskStatusBackGround.class.getSimpleName();

    public static int setBackgroundColor(String taskStatus) {

//        AppLog.showLog("task","status"+taskStatus);
        if (!TextUtils.isEmpty(taskStatus)) {
            if (taskStatus.equalsIgnoreCase(Constants.DONE)) {
                AppLog.showLog(TAG, "task status" + taskStatus);
                return HokusFocusApplication.getContext().getResources().getColor(R.color.task_done_color);
            } else if (taskStatus.equalsIgnoreCase(Constants.NOT_COMPLETED)) {
//                AppLog.showLog("task", "status" + taskStatus);
                return  HokusFocusApplication.getContext().getResources().getColor(R.color.task_not_completed);
            } else {
//                AppLog.showLog("task", "status" + taskStatus);
                return  HokusFocusApplication.getContext().getResources().getColor(R.color.task_not_started);
            }
        }
        throw new IllegalArgumentException("Not a valid Task Status");
    }
}
