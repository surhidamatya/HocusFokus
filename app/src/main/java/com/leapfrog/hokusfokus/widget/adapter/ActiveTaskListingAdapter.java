
package com.leapfrog.hokusfokus.widget.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.leapfrog.hokusfokus.HokusFocusApplication;
import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.model.Task;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.utils.ResourceUtils;
import com.leapfrog.hokusfokus.utils.TaskStatusBackGround;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Adapter for {@link com.leapfrog.hokusfokus.fragment.ActiveTaskListing}
 * lists all the tasks and sets background to {@link CardView} according to the Task status from {@link TaskStatusBackGround}
 */
public class ActiveTaskListingAdapter extends RecyclerView.Adapter<ActiveTaskListingAdapter.ActiveTaskListViewHolder> {

    public List<Task> activeTaskList;
    DatabaseManager databaseManager;

    int taskId;

    private float x1, x2;
    static final int MIN_DISTANCE = 50;


    public ActiveTaskListingAdapter(List<Task> activeTaskList) {
        this.activeTaskList = activeTaskList;
        databaseManager = DatabaseManager.getInstance(HokusFocusApplication.getContext());
        EventBus.register(this);
    }

    public static class ActiveTaskListViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.txt_active_task)
        TextView txtActiveTask;

        @InjectView(R.id.viewSwitcher)
        ViewSwitcher viewSwitcher;

        @InjectView(R.id.img_active_task_cancel)
        ImageView imgActiveTaskCancel;

        @InjectView(R.id.edt_active_task)
        EditText edtActiveTask;

        @InjectView(R.id.txt_char_counter)
        TextView txtCharCounter;

        @InjectView(R.id.card_view_active_task)
        CardView cardViewActiveTask;

        @InjectView(R.id.parent_layout)
        FrameLayout parentLayout;


        public ActiveTaskListViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }

    @Override
    public ActiveTaskListingAdapter.ActiveTaskListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_active_task, parent, false);
        ActiveTaskListViewHolder vh = new ActiveTaskListViewHolder(v);
        return vh;
    }

    private int selectedPosition = -1;

    @Override
    public void onBindViewHolder(final ActiveTaskListingAdapter.ActiveTaskListViewHolder holder, final int position) {

        int childIndex = holder.viewSwitcher.getDisplayedChild();
        holder.txtActiveTask.setText(activeTaskList.get(position).task);

        holder.txtActiveTask.setTag(activeTaskList.get(position));

        holder.cardViewActiveTask.setCardBackgroundColor(TaskStatusBackGround.setBackgroundColor(activeTaskList.get(position).taskStatus));

        if (selectedPosition == position && childIndex == 0) {
            holder.viewSwitcher.showNext();
        } else if (childIndex == 1) {
            holder.viewSwitcher.showPrevious();
        }


        holder.imgActiveTaskCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnimation(holder.cardViewActiveTask, position);
                activeTaskList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, activeTaskList.size());
                selectedPosition = -1;
                databaseManager.deleteTaskById(holder.edtActiveTask.getText().toString());

            }
        });


        final Task task = (Task) holder.txtActiveTask.getTag();
        holder.txtActiveTask.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (DateTimeUtils.isFocusTime()) {


                    EventBus.post(new HokusFokusEvent.OnLongClickListener(true));
                    taskId = task.id;
                    if (!task.taskStatus.equalsIgnoreCase(Constants.DONE)) {

                        if (selectedPosition != -1) {
                            return false;
                        }

                        selectedPosition = position;


                        holder.viewSwitcher.showNext();
                        holder.edtActiveTask.setText(holder.txtActiveTask.getText());

                        holder.edtActiveTask.requestFocus(View.FOCUS_RIGHT);
                        holder.edtActiveTask.setText(holder.txtActiveTask.getText());

                        holder.cardViewActiveTask.setCardBackgroundColor(Color.TRANSPARENT);
                        holder.cardViewActiveTask.setCardElevation(0);
                        holder.cardViewActiveTask.setRadius(0);
                        holder.cardViewActiveTask.setPreventCornerOverlap(false);


                        ResourceUtils.forceShowKeyboardLayout();

                        holder.txtCharCounter.setText(getCharacterCount(holder.txtActiveTask.getText().toString()));

                        holder.edtActiveTask.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            }

                            @Override
                            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                holder.txtCharCounter.setText(getCharacterCount(charSequence.toString()));
                            }

                            @Override
                            public void afterTextChanged(Editable editable) {
                            }
                        });
                    }
                }

                return false;

            }
        });


        if (!Constants.DONE.equals(task.taskStatus)) {
            holder.txtActiveTask.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x1 = motionEvent.getX();
                            break;
                        case MotionEvent.ACTION_UP:
                            x2 = motionEvent.getX();
                            float deltaX = x2 - x1;

                            if (Math.abs(deltaX) > MIN_DISTANCE) {
                                if (DateTimeUtils.isFocusTime()) {
                                    Task task = activeTaskList.get(position);
                                    task.taskStatus = Constants.DONE;
                                    EventBus.post(new HokusFokusEvent.LeftToRightSwipeListener(task));

                                    databaseManager.updateTaskStatus(task.id, Constants.DONE);
                                    databaseManager.updateTaskPriority(task.id, 1);

                                    Task task1 = activeTaskList.get(position);
                                    activeTaskList.remove(position);

                                    if (activeTaskList.get(0).taskStatus.equalsIgnoreCase(Constants.NOT_STARTED)) {
                                        activeTaskList.get(0).taskStatus = Constants.NOT_COMPLETED;
                                        databaseManager.updateTaskStatus(activeTaskList.get(0).id, Constants.NOT_COMPLETED);
                                    }
                                    activeTaskList.add(activeTaskList.size(), task1);
                                    notifyDataSetChanged();

                                }


                            }
                            break;
                    }
                    return false;
                }
            });
        }

        holder.edtActiveTask.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    EventBus.post(new HokusFokusEvent.OnLongClickListener(false));
                    String taskDetailText = holder.edtActiveTask.getText().toString();
                    if (!TextUtils.isEmpty(taskDetailText)) {

                        String changedTask = holder.edtActiveTask.getText().toString();
                        databaseManager.updateTask(taskId, changedTask);

                        Task task = activeTaskList.get(position);
                        task.task = changedTask;

                        holder.viewSwitcher.showNext();
                        selectedPosition = -1;
                        notifyItemChanged(position);
                    } else {
                        holder.edtActiveTask.setError("Task detail can't be empty");
                    }

                    ResourceUtils.hideKeyboard(holder.edtActiveTask);
                }
                return false;

            }
        });


    }


    @Override
    public int getItemCount() {
        return activeTaskList.size();
    }

    private void setAnimation(View viewToAnimate, int position) {

        Animation animation = AnimationUtils.loadAnimation(HokusFocusApplication.getContext(), android.R.anim.slide_out_right);
        viewToAnimate.startAnimation(animation);

    }

    private String getCharacterCount(String s) {
        return String.valueOf(s.length()) + "/200";
    }

    @Subscribe
    public void handleMessage(HokusFokusEvent.LeftToRightSwipeListener event) {
        Task task = event.task;
        notifyDataSetChanged();
    }

}