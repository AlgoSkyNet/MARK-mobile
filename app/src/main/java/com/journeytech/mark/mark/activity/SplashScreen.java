package com.journeytech.mark.mark.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.journeytech.mark.mark.R;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Thread th1 = new Thread() {
            public void run() {
                try {

                    sleep(2700);
                    startActivity(new Intent(SplashScreen.this, LogIn.class));

                } catch (InterruptedException e) {

                    e.printStackTrace();
                } finally {
                    finish();

                }
            }
        };

        th1.start();

    }

    @Override
    protected void onPause() {

        super.onPause();

    }
}