package com.leapfrog.hokusfokus.fragment.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leapfrog.hokusfokus.bus.EventBus;

import butterknife.ButterKnife;

/**
 * Abstract class created to ease of view creation value
 * and View Injection part and is also responsible for taking context of Activity
 */
public abstract class BaseFragment extends Fragment {

    protected abstract int getLayoutId();

    protected Context context;

    private boolean registerForEvents = false;

    /**
     * View injections and layout inflation
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    /**
     * Taking reference of Activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    /**
     * enable event's registration.
     *
     * @param registerForEvents if true events are able to register and unregister.
     */
    protected void registerForEvents(boolean registerForEvents) {
        this.registerForEvents = registerForEvents;
    }

    /**
     * register event
     */
    @Override
    public void onStart() {
        if (registerForEvents)
            EventBus.register(this);
        super.onStart();
    }

    /**
     * un-register event
     */
    @Override
    public void onStop() {
        if (registerForEvents)
            EventBus.unregister(this);
        super.onStop();
    }


}
