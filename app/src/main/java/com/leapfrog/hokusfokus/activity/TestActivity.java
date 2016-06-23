package com.leapfrog.hokusfokus.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.leapfrog.hokusfokus.R;

/**
 * Created by rajesh on 8/3/15.
 * <p/>
 * activity to Test layouts and values to be passed
 * can be deleted after Testing
 */
public class TestActivity extends Activity {

    @Nullable
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }
}
