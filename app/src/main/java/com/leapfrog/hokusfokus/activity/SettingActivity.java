package com.leapfrog.hokusfokus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.leapfrog.hokusfokus.utils.ShowToast;
import com.leapfrog.hokusfokus.widget.WatchDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.SimpleDialog;

import butterknife.InjectView;
import butterknife.OnClick;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;


/*
* Handle settings set by the user
* User can
* <li>Set reminder for the notification</li>
* <li>Set time for notification</li>
* <li>set focus hour</li>
* <li>set reminder for task</li>
* */
public class SettingActivity extends BaseActivity implements WatchDialog.OnSetClickListener {
    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.switch_Reminder)
    SwitchCompat switchReminder;

    @InjectView(R.id.txt_taskReminder)
    TextView txtTaskReminder;

    @InjectView(R.id.txt_focusHourReminder)
    TextView txtFocusHourReminder;

    @InjectView(R.id.txt_focusHourTime)
    TextView txtFocusHourTime;

    final String[] time = new String[]{"5", "10", "15", "20", "30"};

    Dialog.Builder builder = null;

    public TourGuide mTutorialHandler;

    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(SettingActivity.this, R.drawable.ic_backarrow));

        showTutorial();

        setSettingsParameter();
    }

    /**
     * set values to the view of Setting as per set by user
     */
    private void setSettingsParameter() {
        switchReminder.setChecked(PrefUtil.getReminderState());
        txtTaskReminder.setText(time[PrefUtil.getTaskReminderDurationIndex()] + " Min");
        txtFocusHourReminder.setText(time[PrefUtil.getFocusHourReminderDurationIndex()] + " Min");
        txtFocusHourTime.setText(PrefUtil.getFocusHourStartTime() + getString(R.string.minus_sign) + PrefUtil.getFocusHourEndTime());
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    WatchDialog dialogFragment;

    /**
     * show dialog on the basis where user clicks
     *
     * @param view -  which view is to be shown like Clock, time  etc
     */
    public void showDialog(View view) {

        switch (view.getId()) {
            case R.id.ll_set_task_reminder:
                showTimeDialog(PrefUtil.getTaskReminderDurationIndex(), Constants.TASK_REMINDER_DIALOG);
                break;
            case R.id.ll_set_focus_hour_reminder:
                showTimeDialog(PrefUtil.getFocusHourReminderDurationIndex(), Constants.FOCUS_HOUR_REMINDER_DIALOG);
                break;
            case R.id.ll_focus_hour:
                if(PrefUtil.getFirstInstallation()) {
                    mTutorialHandler.cleanUp();
                }
                if (DateTimeUtils.isFocusTime()) {
                    ShowToast.showToast("Focus Hour Running", false);
                } else {

                    dialogFragment = new WatchDialog();
                    dialogFragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag));
                    dialogFragment.setOnSetClickListener(this);
                    setSettingsParameter();
                }
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }


    /**
     * watch dialog which shows two watch with start time and end time.
     *
     * @param defaultSelection time selected by user fof focus hour
     * @param type             task reminder or focus hour reminder
     */
    private void showTimeDialog(Integer defaultSelection, final String type) {
        builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
                if (Constants.TASK_REMINDER_DIALOG.equals(type)) {
                    PrefUtil.setTaskReminderDurationIndex(getSelectedIndex());
                    txtTaskReminder.setText(time[getSelectedIndex()] + " Min");

                } else {
                    PrefUtil.setFocusHourReminderDurationIndex(getSelectedIndex());
                    txtFocusHourReminder.setText(time[getSelectedIndex()] + " Min");
                }
                super.onPositiveActionClicked(fragment);

            }

            @Override
            public void onNegativeActionClicked(com.rey.material.app.DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        ((SimpleDialog.Builder) builder).items(time, defaultSelection)
                .title(getString(R.string.activity_settings_dialog_select_time_title))
                .positiveAction(getString(R.string.activity_settings_dialog_positive))
                .negativeAction(getString(R.string.activity_settings_dialog_negative));

        com.rey.material.app.DialogFragment fragment = com.rey.material.app.DialogFragment.newInstance(builder);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSettingsParameter();
    }

    @Override
    public void onSetClicked() {
        setSettingsParameter();

    }

    @OnClick({R.id.switch_Reminder})
    public void onClick() {
        if (switchReminder.isChecked()) {
            PrefUtil.enableReminder();
            EventBus.post(new HokusFokusEvent.AlarmStateEvent(true));
        } else {
            PrefUtil.disableReminder();
            EventBus.post(new HokusFokusEvent.AlarmStateEvent(false));
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMainActivity = new Intent(SettingActivity.this, MainActivity.class);
        startMainActivity.putExtra(Constants.IS_FROM_BACK_PRESS, true);
        startActivity(startMainActivity);
        finish();
        super.onBackPressed();
    }

    /**
     * Show tutorial how to set fokus hour for first installation
     */
    private void showTutorial() {
         /* setup enter and exit animation */
        Animation enterAnimation = new AlphaAnimation(0f, 1f);

        enterAnimation.setDuration(Constants.TUTORIAL_ANIMATION_DURATION);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(Constants.TUTORIAL_ANIMATION_DURATION);
        exitAnimation.setFillAfter(true);
        if (PrefUtil.getFirstInstallation()) {

          /* initialize TourGuide without playOn() */
            mTutorialHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip()
                            .setTitle("Focus Hour")
                            .setDescription("Set your focus time.")
                            .setGravity(Gravity.START))
                    .setOverlay(new Overlay().setEnterAnimation(enterAnimation).setExitAnimation(exitAnimation)).playOn(txtFocusHourTime);
        }
    }
}
