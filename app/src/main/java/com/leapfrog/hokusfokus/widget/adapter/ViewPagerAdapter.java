package com.leapfrog.hokusfokus.widget.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.model.Task;
import com.leapfrog.hokusfokus.utils.AppLog;
import com.leapfrog.hokusfokus.utils.TaskStatusBackGround;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by root on 6/16/15.
 * class to handle the swipe of history layout and display month wise
 */
public class ViewPagerAdapter extends PagerAdapter {
    private final Context context;
    private final ArrayList<String> month;
    private final LayoutInflater inflater;
    private final DatabaseManager dbManager;

    @InjectView(R.id.ll_container_parent)
    LinearLayout containerParent;

    private String TAG = ViewPagerAdapter.class.getSimpleName();


    public ViewPagerAdapter(Context context, ArrayList<String> month) {
        this.context = context;
        this.month = month;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dbManager = DatabaseManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return month.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ArrayList<String> date = dbManager.getTaskDateAddedInMonth(month.get(position));
        HashMap<Integer, ArrayList<Task>> hashMap = dbManager.getTaskByDate(date);

        View view = inflater.inflate(R.layout.activity_history_viewpager_layout, null);

        ButterKnife.inject(this, view);

        for (int j = 0; j < hashMap.size(); j++) {
            View perDayTaskCell = inflater.inflate(R.layout.view_pager_single_day_task_layout, null);
            LinearLayout linearLayout = (LinearLayout) perDayTaskCell.findViewById(R.id.ll_container);

            TextView txtTaskAddedDate = (TextView) perDayTaskCell.findViewById(R.id.txt_taskAddedDate);
            txtTaskAddedDate.setText(hashMap.get(j).get(0).taskMonthDate);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 10;


            for (int i = 0; i < hashMap.get(j).size(); i++) {
                TextView textView = new TextView(context);
                textView.setBackgroundResource(R.drawable.rounded_corners);

                GradientDrawable drawable = (GradientDrawable) textView.getBackground();

                drawable.setColor(TaskStatusBackGround.setBackgroundColor(hashMap.get(j).get(i).taskStatus));
                AppLog.showLog(TAG, "task color " + TaskStatusBackGround.setBackgroundColor(hashMap.get(j).get(i).taskStatus) + " status " + hashMap.get(j).get(i).taskStatus);
                textView.setText(hashMap.get(j).get(i).task);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(30, 10, 5, 10);
                linearLayout.addView(textView, params);
            }

            containerParent.addView(perDayTaskCell);
        }
        container.addView(view);
        return view;
    }

}