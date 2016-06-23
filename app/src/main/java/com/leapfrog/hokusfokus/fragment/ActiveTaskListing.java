package com.leapfrog.hokusfokus.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.TextView;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.activity.NewTaskActivity;
import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.fragment.base.BaseFragment;
import com.leapfrog.hokusfokus.model.Task;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.widget.adapter.ActiveTaskListingAdapter;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;
import butterknife.OnClick;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ActiveTaskListing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActiveTaskListing extends BaseFragment {

    @InjectView(R.id.recycler_view_active_task_listing)
    RecyclerView recyclerViewActiveTaskListing;

    @InjectView(R.id.txt_empty_task)
    TextView txtEmptyText;

    @InjectView(R.id.snackBarPosition)
    View snackBar;

    DatabaseManager databaseManager;

    LinearLayoutManager layoutManager;

    @InjectView(R.id.fab_add_new_task)
    FloatingActionButton floatingActionButton;

    int taskId;

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    public TourGuide mTutorialHandler;


    // TODO: Rename and change types and number of parameters
    public static ActiveTaskListing newInstance() {
        ActiveTaskListing fragment = new ActiveTaskListing();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ActiveTaskListing() {
        // Required empty public constructor
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* setup enter and exit animation */
        Animation enterAnimation = new AlphaAnimation(0f, 1f);
        enterAnimation.setDuration(600);
        enterAnimation.setFillAfter(true);

        Animation exitAnimation = new AlphaAnimation(1f, 0f);
        exitAnimation.setDuration(600);
        exitAnimation.setFillAfter(true);
        mTutorialHandler = TourGuide.init(getActivity()).with(TourGuide.Technique.Click)
                .setPointer(new Pointer())
                .setToolTip(new ToolTip()
                        .setTitle("Let's Add")
                        .setDescription("Add new task.")
                        .setGravity(Gravity.TOP))
                .setOverlay(new Overlay().setEnterAnimation(enterAnimation).setExitAnimation(exitAnimation));//.playOn(floatingActionButton);

        databaseManager = DatabaseManager.getInstance(getActivity());
        EventBus.register(this);
        setRecyclerView();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_active_task_listing;
    }


    private void setRecyclerView() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewActiveTaskListing.setLayoutManager(layoutManager);

        if (databaseManager.getAllTask(DateTimeUtils.currentDateDb()).size() == 0) {
            txtEmptyText.setVisibility(View.VISIBLE);
        } else {

            /*if active task size is only one change the task status to NOT_STARTED*/
            if (databaseManager.getAllTask(DateTimeUtils.currentDateDb()).size() == 1) {

                Task task = databaseManager.getAllTask(DateTimeUtils.currentDateDb()).get(0);
                databaseManager.updateTaskStatus(task.id, Constants.NOT_COMPLETED);
            }

            ActiveTaskListingAdapter activeTaskAdapter = new ActiveTaskListingAdapter(databaseManager.getAllTask(DateTimeUtils.currentDateDb()));
            recyclerViewActiveTaskListing.setAdapter(activeTaskAdapter);
        }


        recyclerViewActiveTaskListing.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > Constants.MINIMUM_SCROLLED_HEIGHT) {
                    animateOut(floatingActionButton);
                } else {
                    animateIn(floatingActionButton);
                }
                EventBus.post(new HokusFokusEvent.RecyclerViewScrollListener(layoutManager.findFirstCompletelyVisibleItemPosition()));
            }
        });


        recyclerViewActiveTaskListing.removeOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
                EventBus.post(new HokusFokusEvent.RecyclerViewScrollListener(layoutManager.findFirstCompletelyVisibleItemPosition()));
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {

            }
        });

    }

    @OnClick({R.id.fab_add_new_task})
    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add_new_task:
                startAddNewTask();
                break;
        }
    }

    /**
     * Start {@link NewTaskActivity} when user presses {@link FloatingActionButton}
     */
    private void startAddNewTask() {
        getActivity().startActivity(new Intent(getActivity(), NewTaskActivity.class));
    }

    /**
     * listen to swipe event on {@link RecyclerView} to change the status of the app as DONE
     *
     * @param event - Event is swipe is done or not on the view of {@link RecyclerView}
     */
    @Subscribe
    public void handleSwipeEvent(HokusFokusEvent.LeftToRightSwipeListener event) {
        final Task task = event.task;

        if (taskId != task.id) {
            showSnackBar(task);
            taskId = task.id;
        }
    }

    /**
     * show status bar on swiping the left to right in task listing recycler view.
     *
     * @param task {@link Task} to undo the the task
     */
    private void showSnackBar(final Task task) {
        Snackbar.make(snackBar, R.string.snackbar_text, Snackbar.LENGTH_LONG)
                .setAction(R.string.snackbar_action_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseManager.updateTaskStatus(task.id, Constants.NOT_COMPLETED);
                        task.taskStatus = Constants.NOT_COMPLETED;
                        EventBus.post(new HokusFokusEvent.LeftToRightSwipeListener(task));
                        taskId = 0;
                    }
                })
                .show();
    }

    /**
     * animate in the {@link FloatingActionButton} when it is hide or shown
     *
     * @param button - {@link FloatingActionButton} which is to be shown or hidden
     */
    private void animateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_in);
            anim.setDuration(2000L);
            anim.setInterpolator(INTERPOLATOR);
            button.startAnimation(anim);
        }
    }

    /**
     * animate out the {@link FloatingActionButton} when it is hide or shown
     *
     * @param button - {@link FloatingActionButton} which is to be shown or hidden
     */
    private void animateOut(final FloatingActionButton button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                        }

                        public void onAnimationCancel(View view) {
                        }

                        public void onAnimationEnd(View view) {
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.fab_out);
            anim.setInterpolator(INTERPOLATOR);
            anim.setDuration(2000L);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    button.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }
            });
            button.startAnimation(anim);
        }
    }

    /**
     * set {@link RecyclerView} when {@link com.sothree.slidinguppanel.SlidingUpPanelLayout} is collapsed
     *
     * @param panelCollapsed - true if panel is collapsed else false
     */
    @Subscribe
    public void handleSlidingUpPanelCollapsedEvent(HokusFokusEvent.SlidingPanelCollapsed panelCollapsed) {
        setRecyclerView();
    }

    @Subscribe
    public void handleOnLongClickListener(HokusFokusEvent.OnLongClickListener listener) {
        if (listener.longPressed) {
            floatingActionButton.setVisibility(View.INVISIBLE);
            if (getActivity() != null) {
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            }
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    }


}