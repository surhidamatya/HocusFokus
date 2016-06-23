package com.leapfrog.hokusfokus.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.activity.MainActivity;
import com.leapfrog.hokusfokus.activity.NewTaskActivity;
import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.service.HokusFocusService;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.utils.ShowToast;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * shows watch to user to set hokus focus time period
 */
public class WatchDialog extends DialogFragment implements TimePicker.OnTimeChangedListener, View.OnClickListener {

    @InjectView(R.id.time_picker_from)
    TimePicker timePickerFrom;

    @InjectView(R.id.time_picker_to)
    TimePicker timePickerTo;

    @InjectView(R.id.btn_set)
    Button btnSet;

    Dialog d = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        EventBus.register(this);

        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setCancelable(false);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_watch_layout, null);
        dialog.setView(view);

        ButterKnife.inject(this, view);

        enableTwentyFourHourFormat();
        setWatchToUtilsTime();

        btnSet.setEnabled(true);
        btnSet.setOnClickListener(this);

        timePickerFrom.setOnTimeChangedListener(this);
        timePickerTo.setOnTimeChangedListener(this);

        d = dialog.create();
        return d;
    }

    /**
     * enable the watch in 24 hour format.
     */
    private void enableTwentyFourHourFormat() {
        timePickerFrom.setIs24HourView(true);
        timePickerTo.setIs24HourView(true);
    }

    /**
     * set's the time saved in the shared preferences to pop up watch dialog {@link WatchDialog}.
     */
    private void setWatchToUtilsTime() {
        timePickerFrom.setCurrentHour(Integer.parseInt(parseHour(PrefUtil.getFocusHourStartTime())));
        timePickerFrom.setCurrentMinute(Integer.parseInt(parseMinute(PrefUtil.getFocusHourStartTime())));
        timePickerTo.setCurrentHour(Integer.parseInt(parseHour(PrefUtil.getFocusHourEndTime())));
        timePickerTo.setCurrentMinute(Integer.parseInt(parseMinute(PrefUtil.getFocusHourEndTime())));
    }

    private OnSetClickListener listener;

    public void setOnSetClickListener(OnSetClickListener listener) {
        this.listener = listener;
    }


    @Override
    public void onTimeChanged(TimePicker timePicker, int i, int i1) {
        btnSet.setEnabled(false);

        switch (timePicker.getId()) {
            case R.id.time_picker_to:
                if (isTimeValid()) {
                    btnSet.setEnabled(true);
                } else {
                    btnSet.setEnabled(false);
                }
                break;
            case R.id.time_picker_from:
                break;
        }

    }

    /**
     * check wether the focus hour start and end time has one hour difference.
     * if hour difference is one return true.
     * else return false.
     *
     * @return boolean
     */
    private Boolean isTimeValid() {
        long l = DateTimeUtils.timeDifference(timePickerFrom.getCurrentHour() + ":" + timePickerFrom.getCurrentMinute(),
                timePickerTo.getCurrentHour() + ":" + timePickerTo.getCurrentMinute());
        return l > 0 ? true : false;
    }

    @Override
    public void onClick(View view) {
        if (btnSet.isEnabled()) {

            PrefUtil.setFocusHourStartTime(timePickerFrom.getCurrentHour() + ":" + timePickerFrom.getCurrentMinute());
            PrefUtil.setFocusHourEndTime(timePickerTo.getCurrentHour() + ":" + timePickerTo.getCurrentMinute());

            String focusTimeStart = PrefUtil.getFocusHourStartTime();
            PrefUtil.setFullFocusHourStartTime(DateTimeUtils.currentDateDb() + " " + focusTimeStart);


            String focusHourEndTime = PrefUtil.getFocusHourEndTime();
            PrefUtil.setFullFocusHourEndTime(DateTimeUtils.currentDateDb() + " " + focusHourEndTime);

            AppLog.showLog(DateTimeUtils.class.getSimpleName(), "focus time start watch dialog" + timePickerTo.getCurrentHour() + ":" + timePickerTo.getCurrentMinute());

            DatabaseManager db = DatabaseManager.getInstance(getActivity());


            if (db.getAllTask(DateTimeUtils.currentDateDb()).size() == 0) {
                ShowToast.showToast("Add your task for focus hour", false);
                startAddNewTask();

            } else {
                startMainActivity();
            }

            getActivity().startService(new Intent(getActivity(), HokusFocusService.class));


            if (listener != null) {
                listener.onSetClicked();
            }
            dismiss();
        }
    }

    public interface OnSetClickListener {
        void onSetClicked();
    }

    /**
     * parses the hour from the given time of format {10:25}
     *
     * @param time
     * @return hour.
     */
    private String parseHour(String time) {
        String[] a = time.split(":");
        return a[0];
    }

    /**
     * parses the minute from the given time of format {10:25}
     *
     * @param time
     * @return minute.
     */
    private String parseMinute(String time) {
        String[] a = time.split(":");
        return a[1];
    }

    private void startMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    /**
     * Start {@link NewTaskActivity}
     */
    private void startAddNewTask() {

        Intent startNewTask = new Intent(getActivity(), NewTaskActivity.class);
        getActivity().startActivity(startNewTask);
        getActivity().finish();

    }
}
