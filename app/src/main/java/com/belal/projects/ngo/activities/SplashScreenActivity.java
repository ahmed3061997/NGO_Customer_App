package com.belal.projects.ngo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.belal.projects.ngo.R;
import com.google.firebase.FirebaseApp;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
    // duration of watit
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_splash_screen );

        FirebaseApp.initializeApp(this);

        /* new Handler to start the Menu-Activity
           and close this splash-screen after some seconds */
        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent( SplashScreenActivity.this, MainActivity.class);
                startActivity( mainIntent );
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH );
    }
}
