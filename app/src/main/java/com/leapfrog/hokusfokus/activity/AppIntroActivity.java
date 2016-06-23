package com.leapfrog.hokusfokus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.fragment.AppIntroSlideFragment;

/*
* Shows App Introduction at start of the app
* */
public class AppIntroActivity extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(AppIntroSlideFragment.newInstance(R.layout.intro_7));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.intro_2));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.intro_3));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.intro_6));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.intro_5));
        addSlide(AppIntroSlideFragment.newInstance(R.layout.intro_4));
    }

    private void loadMainActivity() {
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to {@link MainActivity} when user presses DONE
     */
    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }
}
