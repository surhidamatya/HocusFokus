package com.leapfrog.hokusfokus.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.leapfrog.hokusfokus.bus.EventBus;

import butterknife.ButterKnife;


/**
 *  Abstract class created to ease the calling of setContentView value
 * and View Injection part. ButterKnife is used for view injection.
 */
public abstract class BaseActivity extends AppCompatActivity {


    /**
     * Boolean value to determine whether the subclasses listen to Events (Otto)
     */
    private boolean registerForEvents = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.inject(this);
        setSupportActionBar(getToolbar());

    }

    /**
     * Method need to be implemented by subclasses and valid layout id needs to be supplied
     */
    public abstract int getLayoutId();

    public abstract Toolbar getToolbar();


    /**
     * enable event's(Otto) registration.
     *
     * @param registerForEvents: if true events are able to register and unregister.
     */
    protected void registerForEvents(boolean registerForEvents) {
        this.registerForEvents = registerForEvents;
    }

    /**
     * register event(Otto) on onResume() if any subclasses have registered for  listening to events
     */
    @Override
    protected void onResume() {
        if (registerForEvents) {
            EventBus.register(this);
        }

        super.onResume();
    }

    /**
     * un-register event(Otto) on onPause() if any subclasses have registered for listening to events
     */
    @Override
    protected void onPause() {
        if (registerForEvents) {
            EventBus.unregister(this);
        }

        super.onPause();
    }
}
