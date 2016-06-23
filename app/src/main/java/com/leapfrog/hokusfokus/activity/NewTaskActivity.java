package com.leapfrog.hokusfokus.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.listener.EditTextChangeListener;
import com.leapfrog.hokusfokus.listener.EditorActionListener;
import com.leapfrog.hokusfokus.model.Task;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.DateTimeUtils;
import com.leapfrog.hokusfokus.utils.ShowToast;
import com.leapfrog.hokusfokus.widget.CustomEditText;
import com.squareup.otto.Subscribe;

import butterknife.InjectView;


/**
 * Adding the new task for the hokusFokus Hour by the User
 * Task is entered from the edit text and stored in the database once user press <b>DONE</b> in the keyboard
 * task detail can only be 200 in the words
 */
public class NewTaskActivity extends BaseActivity {

    private static final String TAG = NewTaskActivity.class.getSimpleName();

    @InjectView(R.id.tool_bar)
    Toolbar toolbar;

    @InjectView(R.id.txt_current_date)
    TextView txtCurrentDate;

    @InjectView(R.id.edt_active_task)
    CustomEditText edtTaskDetail;

    @InjectView(R.id.txt_char_counter)
    TextView txtDetailLengthCount;

    @InjectView(R.id.img_active_task_cancel)
    ImageView btnClose;

    @InjectView(R.id.viewSwitcher)
    ViewSwitcher viewSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        edtTaskDetail.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edtTaskDetail.addTextChangedListener(new EditTextChangeListener(edtTaskDetail));
        edtTaskDetail.setOnEditorActionListener(new EditorActionListener());


        toolbar.setNavigationIcon(ContextCompat.getDrawable(NewTaskActivity.this, R.drawable.ic_backarrow));
        toolbar.setLogo(null);

        setView();

        btnClose.setVisibility(View.GONE);

        setNewTaskCurrentDate();

        registerForEvents(true);

        viewSwitcher.showNext();


    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_task;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Sets current Date from the system
     */
    private void setNewTaskCurrentDate() {

        txtCurrentDate.setText(DateTimeUtils.currentDate());

    }

    /**
     * Function to look after change of texts and calculate it's length
     */
    @Subscribe
    public void EditTextChange(HokusFokusEvent.EditTextChangeListener event) {
        int length = event.detailTextLength;
        setDetailTaskCount(length);

    }

    /**
     * sets the length of the task detail entered in the EditText
     *
     * @param length of the text entered in the EditText
     */
    private void setDetailTaskCount(int length) {
        AppLog.showLog(TAG, "Length of text" + length);
        txtDetailLengthCount.setText(length + "/200");
    }

    /**
     * Function to listen if done  button is pressed or not in key board
     */
    @Subscribe
    public void EditorAction(HokusFokusEvent.ActionDoneListener event) {
        boolean donePressed = event.donePressed;
        addTask(donePressed);
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
     * function to add Tasks to the database
     *
     * @param donePressed value to know if done button is pressed or not
     */
    private void addTask(boolean donePressed) {

        String taskDetailText = edtTaskDetail.getText().toString().trim();

        if (donePressed) {
            if (!TextUtils.isEmpty(taskDetailText)) {
                Task taskDetail = new Task();
                taskDetail.taskStatus = Constants.NOT_STARTED;
                taskDetail.task = taskDetailText;
                taskDetail.timeStamp = DateTimeUtils.currentDateDb();
                taskDetail.taskMonth = DateTimeUtils.getCurrentMonth();
                taskDetail.taskMonthDate = DateTimeUtils.getCurrentMonthAndDate();


                DatabaseManager db = DatabaseManager.getInstance(NewTaskActivity.this);
                if (db.insertTask(taskDetail)) {
                    startMainActivity();
                    ShowToast.showToast("Task Added Successfully.", false);
                } else {
                    startMainActivity();
                    ShowToast.showToast("Task cannot be added. please try again later.", false);
                }
                AppLog.showLog(TAG, "Add task" + taskDetail.task);
            } else {
                edtTaskDetail.setError("Task detail can't be empty");
            }
        }
    }

    /**
     * Start {@link MainActivity} after addition of new task
     */
    private void startMainActivity() {
        Intent startMain = new Intent(NewTaskActivity.this, MainActivity.class);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startMain);
    }

    //setting visibility of the textview to show the current date as it needs to be shown on Adding Task page
    private void setView() {
        txtCurrentDate.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideKeyboardLayout();

    }

    //Hide keyboard from the screen if focus gets changed from EditText
    private void hideKeyboardLayout() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }


}
