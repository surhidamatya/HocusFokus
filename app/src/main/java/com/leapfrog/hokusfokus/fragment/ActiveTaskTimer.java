package com.leapfrog.hokusfokus.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.leapfrog.hokusfokus.HokusFocusApplication;
import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.data.DatabaseHelper;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.fragment.base.BaseFragment;
import com.leapfrog.hokusfokus.model.Task;
import com.leapfrog.hokusfokus.service.TimerService;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.utils.HokusFocusCountDownTimer;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.leapfrog.hokusfokus.utils.ResourceUtils;
import com.leapfrog.hokusfokus.widget.ProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ActiveTaskTimer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveTaskTimer extends BaseFragment {

    @InjectView(R.id.txt_active_task_date)
    TextView txtActiveTaskDate;

    @InjectView(R.id.txt_focus_hour_timer)
    TextView txtFocusHourTimer;

   /* @InjectView(R.id.txt_empty_task)
    TextView txtEmptyTask;*/

    @InjectView(R.id.layout_current_date)
    LinearLayout layoutCurrentDate;

    @InjectView(R.id.layout_task_title)
    LinearLayout layoutTaskTitle;

    @InjectView(R.id.txt_active_task_title)
    TextView txtActiveTaskTitle;

    @InjectView(R.id.active_task_timer_parent)
    FrameLayout parentLayout;

    @InjectView(R.id.progressView)

    ProgressView progressView;

    private List<Task> activeTask;

    private static final String TAG = ActiveTaskTimer.class.getSimpleName();

    long focusMilliSecond = 0L;

    Activity activity;

    DatabaseManager db;

    int statusBarHeight;
    int deviceWidth;

    TextView emptyTextView;

    // TODO: Rename and change types and number of parameters
    public static ActiveTaskTimer newInstance() {
        ActiveTaskTimer fragment = new ActiveTaskTimer();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public ActiveTaskTimer() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarHeight = getStatusBarHeightForOnCreate();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressView.getLayoutParams();

        params.height = (int) getDisplayHeight()
                - (int) ResourceUtils.convertDpToPx(Constants.CONSTANT_DIMENSION)
                - statusBarHeight
                - (int) ResourceUtils.convertDpToPx(Constants.TOOLBAR_HEIGHT);

        progressView.setLayoutParams(params);
        setTextViewDynamically();
        return view;

    }

    /**
     * get the height of device screen
     */
    private float getDisplayHeight() {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float deviceHeight = outMetrics.heightPixels;
        deviceWidth = (int) (outMetrics.widthPixels / density);
        return deviceHeight;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppLog.showLog(TAG, "focus time " + DateTimeUtils.isFocusTime());

        if (DateTimeUtils.isFocusTime()) {
            progressView.setElapsedTimeValue((float) DateTimeUtils.getElapsedTime(PrefUtil.getFocusHourStartTime()));
            DateTimeUtils.getElapsedTime(PrefUtil.getFocusHourStartTime());

            setActiveTaskDateAndTime();
            db = DatabaseManager.getInstance(getActivity());

            if (db.getAllTask(DateTimeUtils.currentDateDb()).size() == 0) {
                txtActiveTaskTitle.setText(getResources().getString(R.string.no_task));
                emptyTextView.setVisibility(View.INVISIBLE);

            } else {
                activeTask = db.getAllTask(DateTimeUtils.currentDateDb());
                layoutTaskTitle.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.INVISIBLE);
                setActiveTaskTitle();
            }
        } else

        {
            progressView.setElapsedTimeValue(0f);
            progressView.setVisibility(View.INVISIBLE);
            txtFocusHourTimer.setVisibility(View.INVISIBLE);
            layoutTaskTitle.setVisibility(View.INVISIBLE);
            layoutCurrentDate.setVisibility(View.INVISIBLE);
            getActivity().stopService(new Intent(getActivity(), TimerService.class));
            PrefUtil.setTimerState(false);
        }


    }
//test comment
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_active_task_timer_main;
    }

    /**
     * set current date and time
     */
    private void setActiveTaskDateAndTime() {

        txtActiveTaskDate.setText(DateTimeUtils.currentDateTime());

    }

    /*
     * This is used to receive the updated time from broadcast receiver
     * */
    private final BroadcastReceiver timeBroadCaster = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //method used to update your GUI fields
            updateGUI(intent);
        }
    };

    /**
     * update UI with changing timer of Focus Hour
     *
     * @param intent Focus hour timer value
     */
    private void updateGUI(Intent intent) {

        if (intent.getExtras() != null) {

            String focusHMS = intent.getStringExtra(Constants.HOCUS_FOCUS_TIMER);
            focusMilliSecond = intent.getLongExtra(Constants.HOCUS_FOCUS_TIMER_MILLI_SECOND, 0);
            boolean isTimeFinished = intent.getBooleanExtra(Constants.IS_TIME_STOPPED, false);

            //invalidate progress view if time is already finished
            if (isTimeFinished) {
                progressView.setForceStop(isTimeFinished);
                progressView.invalidate();
            }

            txtFocusHourTimer.setText("" + focusHMS);
            if (DateTimeUtils.isFocusTime()) {

                setProgressValue();
            }

            AppLog.showLog(TAG, "active running timer fragment" + " Milli Second " + focusMilliSecond + "Angle" + progressView.getProgressAngle());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(timeBroadCaster, new IntentFilter(
                HokusFocusCountDownTimer.COUNTDOWN_BROADCAST_RECEIVER));

        AppLog.showLog(TAG, "on resume");

        if (DateTimeUtils.isFocusTime()) {
            progressView.setElapsedTimeValue((float) DateTimeUtils.getElapsedTime(PrefUtil.getFocusHourStartTime()));
            setProgressValue();
            setActiveTaskTitle();

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            getActivity().unregisterReceiver(timeBroadCaster);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        try {
            getActivity().unregisterReceiver(timeBroadCaster);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    /**
     * Set title of the active task that is running
     */
    public void setActiveTaskTitle() {
        DatabaseManager databaseManager = DatabaseManager.getInstance(HokusFocusApplication.getContext());

        activeTask = databaseManager.getAllTask(DateTimeUtils.currentDateDb());

        List<Task> incompleteTask = new ArrayList<Task>();


        for (int i = 0; i < activeTask.size(); i++) {
            if (!activeTask.get(i).taskStatus.equalsIgnoreCase(Constants.DONE)) {
                incompleteTask.add(activeTask.get(i));
            }
        }

        /*populate incomplete task in txtActiveTaskTitle from incompleteTask List*/
        for (int i = 0; i < incompleteTask.size(); i++) {
            txtActiveTaskTitle.setText(incompleteTask.get(0).task);
            Task task = incompleteTask.get(0);
            databaseManager.updateTaskStatus(task.id, Constants.NOT_COMPLETED);
        }

    }

    /**
     * set value to the progress view according to the changing timer
     */
    private void setProgressValue() {
        progressView.setElapsedTimeValue((float) DateTimeUtils.getElapsedTime(PrefUtil.getFocusHourStartTime()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    /**
     * get status bar height of device
     */
    public int getStatusBarHeightForOnCreate() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setTextViewDynamically() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        emptyTextView = new TextView(getActivity());
        emptyTextView.setText("No task Found");
        emptyTextView.setGravity(Gravity.CENTER);
        emptyTextView.setTextAppearance(getActivity(), R.style.empty_text_style);
        parentLayout.addView(emptyTextView, params);
    }
}
