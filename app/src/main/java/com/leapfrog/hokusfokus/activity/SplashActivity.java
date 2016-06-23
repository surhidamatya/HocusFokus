package com.leapfrog.hokusfokus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.leapfrog.hokusfokus.R;
import com.leapfrog.hokusfokus.data.Constants;
import com.leapfrog.hokusfokus.data.DatabaseHelper;
import com.leapfrog.hokusfokus.utils.ResourceUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class SplashActivity extends AppCompatActivity {

    private RelativeLayout splashLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if(true){
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
            return;
        }

        setBackground();

        startMainActivity();
    }

    /**
     * Initialize all the views and assign listeners to it
     */
    private void setBackground() {
        splashLayout = (RelativeLayout) findViewById(R.id.splash_layout);

        Picasso.with(SplashActivity.this).load(R.drawable.splash_bg).resize(Constants.SPLASH_IMAGE_WIDTH, Constants.SPLASH_IMAGE_HEIGHT).centerInside().into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    splashLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                } else {
                    splashLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    /**
     * Start the main Activity after splash screen
     */
    private void startMainActivity() {

        if (!ResourceUtils.doesDatabaseExist(DatabaseHelper.DB_NAME)) {
            finish();
            Intent intent = new Intent(this, AppIntroActivity.class);
            startActivity(intent);
        } else {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent startMainActivity = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(startMainActivity);
                    finish();
                }
            }, Constants.SPLASH_TIME_OUT);
        }
    }
}
