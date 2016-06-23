package com.leapfrog.hokusfokus.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.data.DatabaseManager;
import com.leapfrog.hokusfokus.widget.adapter.ViewPagerAdapter;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Lists History of the Tasks and it's status on the basis of Date
 */
public class HistoryActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.toolbar_flexible_space)
    Toolbar toolbar;

    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    @InjectView(R.id.txt_year)
    TextView txtYear;
    int viewPagerSize = 0;

    ArrayList<String> taskAddedMonths;

    @OnClick({R.id.img_leftBackArrow, R.id.img_rightBackArrow})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_rightBackArrow:
                moveForward();
                break;
            case R.id.img_leftBackArrow:
                moveBackWard();
                break;

        }

    }

    /**
     * move view pager page as according to clicking on backward arrow.
     */
    private void moveBackWard() {
        int currentPosition = viewPager.getCurrentItem();
        if (currentPosition > 0) {
            int nextPosition = currentPosition - 1;
            viewPager.setCurrentItem(nextPosition);
            mCollapsingToolbar.setTitle(getCollapsingToolbarTitle(taskAddedMonths.get(nextPosition)));
        }
    }

    /**
     * move view pager page as according to clicking on forward arrow.
     */
    private void moveForward() {
        int currentPosition = viewPager.getCurrentItem();

        if (currentPosition < viewPagerSize-1) {
            int nextPosition = currentPosition + 1;
            if (viewPagerSize == 1) {
                //do nothing
            } else {
                viewPager.setCurrentItem(currentPosition + 1);
                mCollapsingToolbar.setTitle(getCollapsingToolbarTitle(taskAddedMonths.get(nextPosition)));
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskAddedMonths = DatabaseManager.getInstance(HistoryActivity.this).getAllMonths();
        viewPagerSize = taskAddedMonths.size();

        mCollapsingToolbar.setTitle(getCollapsingToolbarTitle(taskAddedMonths.get(0)));
        mCollapsingToolbar.setExpandedTitleColor(Color.WHITE);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_backarrow));

        viewPager.setAdapter(new ViewPagerAdapter(HistoryActivity.this, DatabaseManager.getInstance(HistoryActivity.this).getAllMonths()));
        viewPager.addOnPageChangeListener(this);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * get the name of month with concatenating History as like this{May History};
     *
     * @param month string defining the month
     * @return string value of month as entered
     */
    private String getCollapsingToolbarTitle(String month) {
        return month + " " + "History";
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //change the title of collapsing toolbar according to change in page of viewpager
        mCollapsingToolbar.setTitle(getCollapsingToolbarTitle(taskAddedMonths.get(position)));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
