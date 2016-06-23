package com.leapfrog.hokusfokus.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.leapfrog.hokusfokus.bus.EventBus;
import com.leapfrog.hokusfokus.bus.HokusFokusEvent;

/**
 * class to track change of letters in any {@link android.widget.EditText} to count number of words typed
 * and count the length
 */
public class EditTextChangeListener implements TextWatcher{

    private final View view;
    public EditTextChangeListener(View view) {
        this.view = view;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

        EventBus.post(new HokusFokusEvent.EditTextChangeListener(editable.toString().length()));
    }
}
