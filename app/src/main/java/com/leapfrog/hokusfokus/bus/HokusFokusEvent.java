package com.leapfrog.hokusfokus.bus;

import com.leapfrog.hokusfokus.model.Task;

/**
 * Class to look over all the Otto EVents
 */
public class HokusFokusEvent {


    /**
     * Track for change in the edittext value
     */

    public static class EditTextChangeListener {
        public int detailTextLength;

        public EditTextChangeListener(int length) {
            this.detailTextLength = length;
        }

    }


    public static class LeftToRightSwipeListener {
        public Task task;

        public LeftToRightSwipeListener(Task task) {
            this.task = task;
        }
    }

    /**
     * Watch if DONE button is pressed on the keyboard of the device
     */
    public static class ActionDoneListener {
        public boolean donePressed;

        public ActionDoneListener(boolean pressedDone) {
            this.donePressed = pressedDone;

        }
    }

    /**
     * pass the scroll co-ordinates to the respective activiy
     */
    public static class RecyclerViewScrollListener {
        public int firstChildVisibleItem;

        public RecyclerViewScrollListener(int firstChildVisibleItem) {
            this.firstChildVisibleItem = firstChildVisibleItem;
        }
    }

    /**
     * for the event of focus hour on/off event from the settings activity.
     */
    public static class AlarmStateEvent {
        public boolean focusHourState;

        public AlarmStateEvent(boolean state) {
            this.focusHourState = state;
        }
    }

    /*
     * event to notify the recyclerview{@link com.leapfrog.hokusfokus.widget.adapter.ActiveTaskListingAdapter} item longClick event.
     */
    public static class OnLongClickListener {
        public boolean longPressed;

        public OnLongClickListener(boolean longPressed) {

            this.longPressed = longPressed;
        }

    }


    /**
     * event fires when the slidingUp panel is collapsed.
     */
    public static class SlidingPanelCollapsed {
        public SlidingPanelCollapsed() {

        }
    }

    /**
     * event to pause the progress view;
     */
    public static class PauseProgressView {
        public boolean pause;

        public PauseProgressView(boolean pasue) {
            this.pause = pasue;
        }
    }
}