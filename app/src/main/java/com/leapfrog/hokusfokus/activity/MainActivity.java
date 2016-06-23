package com.leapfrog.hokusfokus.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.fragment.ActiveTaskTimer;
import com.leapfrog.hokusfokus.service.HokusFocusService;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.PrefUtil;
import com.leapfrog.hokusfokus.utils.ResourceUtils;
import com.leapfrog.hokusfokus.utils.ShowToast;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import de.psdev.licensesdialog.LicensesDialog;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * MainActivity hosts {@link ActiveTaskTimer} and {@link com.leapfrog.hokusfokus.fragment.ActiveTaskListing}
 * This activity is responsible for showing Timer to show running timer on active task and hokus focus time
 * also host fragment to show active task lists.
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String FRAGMENT_TAG = "MyFragment";

    @InjectView(R.id.sliding_layout)
    SlidingUpPanelLayout mLayout;

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.arrow)
    ImageView slidingArrow;


    //boolean value to check if panel is slide up
    public static boolean panelSlidedUp = false;

    FragmentManager manager;

    public TourGuide tutorialHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.register(this);

        startHokusFocusService();

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float v) {
                if (v < Constants.SLIDING_THRESHOLD) {
                    hideSoftKeyboard();
                }

            }

            @Override
            public void onPanelCollapsed(View view) {

                EventBus.post(new HokusFokusEvent.SlidingPanelCollapsed());
                animateArrow(Constants.ROTATE_DEGREE_HALF, Constants.ROTATE_DEGREE_FULL);
                panelSlidedUp = false;

                /**
                 * Update task title on {@link ActiveTaskTimer} when user slides the panel
                 * */
                if (manager.findFragmentByTag(FRAGMENT_TAG) != null) {
                    ActiveTaskTimer activeTaskTimer = (ActiveTaskTimer) manager.findFragmentByTag(FRAGMENT_TAG);
                    activeTaskTimer.setActiveTaskTitle();
                }

            }

            @Override
            public void onPanelExpanded(View view) {
                if(tutorialHandler != null) {
                    tutorialHandler.cleanUp();
                }
                animateArrow(Constants.ROTATE_DEGREE_START, Constants.ROTATE_DEGREE_HALF);
                panelSlidedUp = true;
                setFirstInstallation();

            }

            @Override
            public void onPanelAnchored(View view) {

            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        loadActiveTaskTimer();

    }

    /**
     * load {@link ActiveTaskTimer} so that updated value ca be loaded to this fragment by checking tag
     */
    private void loadActiveTaskTimer() {

        manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.activeTaskListingParent, new ActiveTaskTimer(), FRAGMENT_TAG);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        AppLog.showLog(TAG, "First installation" + PrefUtil.getFirstMenu());

        // Inflate the menu; this adds items to the action bar if it is present.
        if ( PrefUtil.getFirstMenu()) {
            AppLog.showLog(TAG, "check if");
//            ShowToast.showToast("Check if", true);
            getMenuInflater().inflate(R.menu.first_time_menu, menu);

            // We need to get the menu item as a View in order to work with TourGuide
            MenuItem menuItem = menu.getItem(0);
            ImageView button = (ImageView) menuItem.getActionView();

            // just adding some padding to look better
            float density = this.getResources().getDisplayMetrics().density;
            int padding = (int) (5 * density);
            button.setPadding(padding, padding, padding, padding);

            // set an image
            button.setImageDrawable(this.getResources().getDrawable(R.mipmap.ic_launcher));

            ToolTip toolTip = new ToolTip()
                    .setTitle("Welcome!")
                    .setDescription("Set Focus Hour from Settings.")
                    .setGravity(Gravity.START | Gravity.BOTTOM);

            tutorialHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .motionType(TourGuide.MotionType.ClickOnly)
                    .setPointer(new Pointer())
                    .setToolTip(toolTip)
                    .setOverlay(new Overlay())
                    .playOn(button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    invalidateOptionsMenu();
                    tutorialHandler.cleanUp();
                    showOverFlowMenu();
                }
            });
        } else {
//            ShowToast.showToast("Check Else", true);
            AppLog.showLog(TAG, "check Else");
            invalidateOptionsMenu();
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }


        return true;
    }

    /**
     * show overflow menu if it is first installation
     */
    private void showOverFlowMenu() {
        invalidateOptionsMenu();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toolbar.showOverflowMenu();
                PrefUtil.setFirstMenu();
            }
        }, 100);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                startSettings();
//                mTutorialHandler2.cleanUp();
                break;
            case R.id.action_history:
                if (DatabaseManager.getInstance(this).isSingleRowExist()) {
                    showHistory();
                } else {
                    ShowToast.showToast(getString(R.string.no_summary_message), true);
                }
                break;
            case R.id.action_about:
                startAbout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * start history page
     */
    private void showHistory() {
        startActivity(new Intent(MainActivity.this, HistoryActivity.class));
    }


    /**
     * start settings page
     */
    private void startSettings() {
        Intent startSetting = new Intent(MainActivity.this, SettingActivity.class);
        startActivity(startSetting);
    }

    /**
     * start about third party library licenses used in app
     */
    private void startAbout() {
//        show license dialog
        showLicensesDialog();
    }

    private void startHokusFocusService() {
        startService(new Intent(MainActivity.this, HokusFocusService.class));
    }

    @Subscribe
    public void catchRecyclerViewFirstVisibleItem(HokusFokusEvent.RecyclerViewScrollListener listener) {
        if (listener.firstChildVisibleItem == 0) {
            mLayout.setEnabled(true);
        } else {
            mLayout.setEnabled(false);
        }
    }

    /**
     * hides SoftKeyboard
     */
    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * animate sliding arrow up and down according to slidingUp panel expanded or collapsed.
     *
     * @param startAngle angle to describe from where rotation will start
     * @param rotationAngle angle to describe from where rotation will end
     */
    private void animateArrow(int startAngle, int rotationAngle) {
        RotateAnimation r;
        r = new RotateAnimation(startAngle, rotationAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration(400);
        r.setRepeatCount(0);
        r.setFillEnabled(true);
        r.setFillAfter(true);
        slidingArrow.startAnimation(r);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.unregister(this);
    }

    @Override
    public void onBackPressed() {
        /**
         * Check if active fragment is {@link com.leapfrog.hokusfokus.fragment.ActiveTaskListing} navigate to {@link ActiveTaskTimer}
         * else exit
         * @param panelSlidedUp true if fragment is {@link com.leapfrog.hokusfokus.fragment.ActiveTaskListing}
         */
        if (panelSlidedUp) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            AppLog.showLog(TAG, "slided up list fragment");
            ActiveTaskTimer.newInstance();
        } else {
            AppLog.showLog(TAG, "timer fragment");
            super.onBackPressed();

        }
    }

    /**
     * Generates a licenses dialog about libraries used.
     */
    public void showLicensesDialog() {

        new LicensesDialog.Builder(this)
                .setNotices(ResourceUtils.getLicenses())
                .setThemeResourceId(R.style.Base_Theme_AppCompat_Light)
                .setTitle(getString(R.string.license_dialog))
                .build()
                .show();

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            openOptionsMenu();
        } catch (Exception ex) {
            Log.e("ERR", "Error: " + ex.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        boolean fromBack= intent.getBooleanExtra(Constants.IS_FROM_BACK_PRESS, false);
        if(fromBack && PrefUtil.getFirstInstallation()){
              /* setup enter and exit animation */
            Animation enterAnimation = new AlphaAnimation(0f, 1f);
            enterAnimation.setDuration(600);
            enterAnimation.setFillAfter(true);

            Animation exitAnimation = new AlphaAnimation(1f, 0f);
            exitAnimation.setDuration(600);
            exitAnimation.setFillAfter(true);

            tutorialHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip()
                            .setTitle("Focus")
                            .setDescription("Click here to view Task List!!")
                            .setGravity(Gravity.TOP))
                    .setOverlay(new Overlay().setEnterAnimation(enterAnimation).setExitAnimation(exitAnimation)).playOn(slidingArrow);
            setFirstInstallation();
        }
        }

    /**
     * set first installation value to true
     */
    private void setFirstInstallation(){
        PrefUtil.setFirstInstallation();
    }


}
